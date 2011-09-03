/*
 * Copyright (C) 2011 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.novelang.outfit.xml;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang.StringUtils;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.TextTools;
import org.novelang.outfit.loader.ResourceName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.of;

/**
 * Base class for processing elements from SAX events, where "buildup" objects represent
 * parts of a greater entity and get stored in a stack that follows XML element hierarchy.
 *
 * @author Laurent Caillette
 */
public abstract class StackBasedElementReader< ELEMENT, ATTRIBUTE, BUILDUP >
    extends NamespaceAwareContentHandlerAdapter
{

  private static final Logger LOGGER = LoggerFactory.getLogger( StackBasedElementReader.class ) ;

  private final Function< String, ELEMENT > findFromLocalName ;
  private final Function< ATTRIBUTE, String > attributeToName ;
  private final ELEMENT rootElement ;

  private final BuildupStack< ELEMENT, BUILDUP > stack ;

  protected StackBasedElementReader(
      final String namespaceUri,
      final ImmutableSet< ImmutableList< ELEMENT > > elementPaths,
      final Function< ELEMENT, String > pathElementToString,
      final Function< ATTRIBUTE, String > attributeToName,
      final Function< String, ELEMENT > findFromLocalName
  ) {
    super( namespaceUri ) ;
    this.findFromLocalName = checkNotNull( findFromLocalName ) ;
    this.attributeToName = checkNotNull( attributeToName ) ;

    // Will throw some ugly exception if preconditions (at least one element) aren't met.
    this.rootElement = elementPaths.asList().get( 0 ).get( 0 ) ;

    this.stack = new BuildupStack< ELEMENT, BUILDUP >( elementPaths, pathElementToString ) ;
  }

  @SuppressWarnings( { "StringBufferField" } )
  private final StringBuilder charactersCollector = new StringBuilder() ;

  protected void throwException( final String message ) throws IncorrectXmlException {
    throw new IncorrectXmlException( getNamespaceAwareness().buildMessageWithLocation( message )
        + ( stack.isEmpty() ? "" : " (in " + stack.getPathAsString() + ")" )
    ) ;
  }

  @Override
  public void startElement(
      final String uri,
      final String localName,
      final String qName,
      final Attributes attributes
  ) throws SAXException {

    final ELEMENT element = findFromLocalName.apply( localName ) ;

    try {
      if( rootElement.equals( element ) ) {
        // We care of meta prefix only for local root FOP element.
        if( ! getNamespaceAwareness().isMetaPrefix( uri ) ) {
          throwException( "Expecting '" + element + "' element in " +
              "'" + getNamespaceAwareness().getNamespaceUri() + "' namespace" ) ;
        }
      } else if( ! stack.isEmpty() ) {
        if( element == null ) {
          throwException( "Unknown element: '" + localName + "'" ) ;
        }
      }
      if( element != null ) {
        stack.push( element, preparePush( element, attributes ) ) ;
//        StackBasedElementReader.LOGGER.debug( ">>> ", stack.getPathAsString() ) ;
      }
    } catch( BuildupStack.IllegalPathException e ) {
      throwException( e.getMessage() ) ;
    }
  }

  @Override
  public void endElement(
      final String uri,
      final String localName,
      final String qName
  ) throws SAXException {
    if( ! stack.isEmpty() ) {
      final BUILDUP newBuildup = preparePop() ;
      stack.pop() ;
//      StackBasedElementReader.LOGGER.debug( "<<< ", stack.getPathAsString() ) ;
      if( stack.isEmpty() ) {
        if( newBuildup != null ) {
          throw new IllegalStateException(
              "Stack is getting empty but got a non-null value frop preparePop(): " + newBuildup ) ;
        }
      } else {
        stack.setTopBuildup( newBuildup ) ;
      }
    }
  }

  protected abstract BUILDUP preparePush( ELEMENT element, Attributes attributes )
      throws IncorrectXmlException;

  protected abstract BUILDUP preparePop() throws IncorrectXmlException;



// ============
// Stack access
// ============

  protected final ELEMENT getTopSegment() {
    return stack.topSegment() ;
  }

  protected final BUILDUP getBuildupOnTop() {
    return stack.getBuildupOnTop() ;
  }

  protected final BUILDUP getBuildupUnderTop() {
    return stack.getBuildupUnderTop() ;
  }


// ==========
// Attributes
// ==========

  protected final String getStringAttributeValue(
      final Attributes attributes,
      final ATTRIBUTE attribute
  ) throws IncorrectXmlException {
    final String actualValue = attributes.getValue( attributeToName.apply( attribute ) ) ;
    if( actualValue == null ) {
      throwException( "Missing '" + attributeToName.apply( attribute ) + "' attribute" ) ;
      return null ; // Never executes but compiler gets happy.
    } else {
      return actualValue ;
    }
  }

  protected final boolean getBooleanAttributeValue(
      final Attributes attributes,
      final ATTRIBUTE attribute,
      final boolean defaultValue
  ) throws IncorrectXmlException {
    final String actualValue = attributes.getValue( attributeToName.apply( attribute ) ) ;
    if( actualValue == null ) {
      return defaultValue ;
    } else {
      if( "true".equalsIgnoreCase( actualValue ) ) {
        return true ;
      } else if( "false".equalsIgnoreCase( actualValue ) ) {
        return false ;
      } else {
        throwException(
            "Unsupported boolean value '" + actualValue + "', must be 'true' or 'false'" ) ;
        return false ; // Never executes but compiler gets pleased.
      }
    }
  }


// ====
// Text
// ====

  @Override
  public void characters(
      final char[] ch,
      final int start,
      final int length
  ) throws SAXException {
    charactersCollector.append( ch, start, length ) ;
  }

  protected String getAndClearCollectedText() {
    final String collectedText = charactersCollector.toString() ;
    TextTools.clear( charactersCollector ) ;
    return collectedText ;
  }


  /**
   * Side effect: clears collected text.
   */
  protected final int getIntegerFromCollectedText() throws IncorrectXmlException {
    final String text = StringUtils.trim( getAndClearCollectedText() ) ;
    try {
      return Integer.parseInt( text ) ;
    } catch( NumberFormatException e ) {
      throwException( "Couldn't parse '" + text + "' as an integer value " ) ;
      return 0 ; // Never executes but makes compiler happy.
    }
  }

  /**
   * Side effect: clears collected text.
   */
  protected final ResourceName getResourceNameFromCollectedText() throws IncorrectXmlException {
    final String text = StringUtils.trim( getAndClearCollectedText() ) ;
    try {
      if( StringUtils.isBlank( text ) ) {
        return null ;
      } else {
        return new ResourceName( text ) ;
      }
    } catch( IllegalArgumentException e ) {
      throwException( e.getMessage() ) ;
      return null ; // Never executes but makes compiler happy.
    }
  }
}

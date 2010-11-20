/*
 * Copyright (C) 2010 Laurent Caillette
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
package org.novelang.configuration.fop;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang.StringUtils;
import org.novelang.loader.ResourceName;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.CollectionTools;
import org.novelang.outfit.Husk;
import org.novelang.outfit.TextTools;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import static com.google.common.collect.ImmutableList.of;
import static org.novelang.configuration.fop.XmlElement.*;

/**
 * Creates a {@link FopFactoryConfiguration}.
 * We're implementing the rather unconvenient {@link ContentHandler} because
 * {@link org.novelang.rendering.XslWriter} loads XSL stylesheets from a SAX source.
 *
 * @author Laurent Caillette
 */
public class FopFactoryConfigurationReader implements ContentHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger( FopFactoryConfigurationReader.class ) ;

  private final String namespaceUri ;

  private static final Function< XmlElement,String > PATH_ELEMENT_TO_STRING =
      new Function< XmlElement, String >() {
        @Override
        public String apply( final XmlElement input ) {
          return input.getLocalName() ;
        }
      }
  ;

  public FopFactoryConfigurationReader() {
    this( XSL_META_NAMESPACE_URI ) ;
  }

  public FopFactoryConfigurationReader( final String namespaceUri ) {
    Preconditions.checkArgument( ! StringUtils.isBlank( namespaceUri ) ) ;
    this.namespaceUri = namespaceUri ;
  }

  private final ImmutableList.Builder< FopFactoryConfiguration > configurations =
      ImmutableList.builder() ;

  /**
   * Returns all the parsed configurations.
   * @return a non-null, possibly empty list.
   */
  public ImmutableList< FopFactoryConfiguration > getConfigurations() {
    return configurations.build() ;
  }

// =======
// Locator
// =======

  private Locator locator = null ;

  @Override
  public void setDocumentLocator( final Locator locator ) {
    this.locator = locator ;
  }

  private void throwException( final String message ) throws ConfigurationStructureException {
    throw new ConfigurationStructureException(
        locator,
        message + ( stack.isEmpty() ? "" : ( "(in " + stack.getPathAsString() + ")" ) )
    ) ;
  }



// ================
// Namespace prefix
// ================

  public static final String XSL_META_NAMESPACE_URI = "http://novelang.org/meta-xsl/1.0" ;


  private String namespacePrefix = null ;

  @Override
  public void startPrefixMapping( final String prefix, final String uri ) throws SAXException {
    if( namespaceUri.equals( uri ) ) {
      if( namespacePrefix == null ) {
        namespacePrefix = Preconditions.checkNotNull( prefix ) ;
      } else {
        throw new IllegalStateException(
            "Namespace URI '" + namespaceUri + "' already mapped to '" + namespacePrefix +"'" ) ; 
      }
    }
  }

  @Override
  public void endPrefixMapping( final String prefix ) throws SAXException {
    if( prefix.equals( namespacePrefix ) ) {
      namespacePrefix = null ;
    }
  }

  private boolean isMetaPrefix( final String uri ) {
    return namespaceUri.equals( uri ) ;
  }


// =======
// Element
// =======

  private static final ImmutableSet< ImmutableList< XmlElement > > ELEMENT_PATHS = ImmutableSet.of(
      of( FOP, TARGET_RESOLUTION ),
      of( FOP, RENDERERS ),
      of( FOP, RENDERERS, RENDERER ),
      of( FOP, RENDERERS, RENDERER, FONTS_DIRECTORY ),
      of( FOP, RENDERERS, RENDERER, OUTPUT_PROFILE ),
      of( FOP, RENDERERS, RENDERER, FILTER_LIST ),
      of( FOP, RENDERERS, RENDERER, FILTER_LIST, VALUE ),
      of( FOP )
  ) ;

  private final Stack< XmlElement, Object > stack =
      new Stack< XmlElement, Object >( ELEMENT_PATHS, PATH_ELEMENT_TO_STRING ) ;

  
  @Override
  public void startElement(
      final String uri,
      final String localName,
      final String qName,
      final Attributes attributes
  ) throws SAXException {

    final XmlElement element = XmlElement.fromLocalName( localName ) ;

    try {
      if( FOP == element ) {
        // We care of meta prefix only for local root FOP element.
        if( !isMetaPrefix( uri ) ) {
          throwException( "Expecting '" + element + "' element in " +
              "'" + namespaceUri + "' namespace" ) ;
        }
      } else if( ! stack.isEmpty() ) {
        if( element == null ) {
          throwException( "Unknown element: '" + localName + "'" ) ;
        }
      }
      if( element != null ) {
        stack.push( element, preparePush( element, attributes ) ) ;
        LOGGER.debug( ">>> ", stack.getPathAsString() ) ;
      }
    } catch( Stack.IllegalPathException e ) {
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
      final Object newBuildup = preparePop() ;
      stack.pop() ;
      LOGGER.debug( "<<< ", stack.getPathAsString() ) ;
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

// ====
// Text
// ====

  @SuppressWarnings( { "StringBufferField" } )
  private final StringBuilder charactersCollector = new StringBuilder() ;

  @Override
  public void characters(
      final char[] ch,
      final int start,
      final int length
  ) throws SAXException {
    charactersCollector.append( ch, start, length ) ;
  }

  private String getAndClearCollectedText() {
    final String collectedText = charactersCollector.toString() ;
    TextTools.clear( charactersCollector ) ;
    return collectedText ;
  }


// ========
// Stacking
// ========

  private Object preparePush( final XmlElement element, final Attributes attributes )
      throws ConfigurationStructureException
  {

    switch( element ) {
      case FOP :
        return Husk.create( FopFactoryConfiguration.class ) ;
      case TARGET_RESOLUTION :
        return null ;
      case RENDERERS :
        return ImmutableSet.< FopFactoryConfiguration.Renderer >of() ;
      case RENDERER :
        final FopFactoryConfiguration.Renderer renderer =
            Husk.create( FopFactoryConfiguration.Renderer.class ) ;
        return renderer
            .withMime( getStringAttributeValue( attributes, XmlAttribute.MIME ) )
            .withFontsDirectories(
                ImmutableList.< FopFactoryConfiguration.Renderer.FontsDirectory >of() )
            .withFilters( ImmutableList.< FopFactoryConfiguration.Renderer.Filters >of() )
        ;
      case FONTS_DIRECTORY :
        return Husk.create( FopFactoryConfiguration.Renderer.FontsDirectory.class )
            .withRecursive( getBooleanAttributeValue( attributes, XmlAttribute.RECURSIVE, false ) )
        ;
      case OUTPUT_PROFILE :
        return null ;
      case FILTER_LIST :
        return ImmutableList.< FopFactoryConfiguration.Renderer.Filters >of() ;
      case VALUE :
        return null ;
      default :
        throw new IllegalArgumentException( "Unsupported: " + element ) ;
    }
  }

  /**
   * Returns the new element at the top of the stack after popping.
   * This method typically read the current top of the stack, or the collected text.
   */
  private Object preparePop() throws ConfigurationStructureException {

    switch( stack.topSegment() ) {

      case FOP :
        configurations.add( ( FopFactoryConfiguration ) stack.getBuildupOnTop() ) ;
        return null ;

      case TARGET_RESOLUTION :
        return ( ( FopFactoryConfiguration ) stack.getBuildupUnderTop() )
            .withTargetResolution( getIntegerFromCollectedText() ) ;

      case RENDERERS :
        final FopFactoryConfiguration configuration = ( FopFactoryConfiguration )
            stack.getBuildupUnderTop() ;
        return configuration.withRenderers(
            ( ImmutableSet< FopFactoryConfiguration.Renderer > ) stack.getBuildupOnTop() ) ;

      case RENDERER :
        final ImmutableSet< FopFactoryConfiguration.Renderer > renderers =
            ( ImmutableSet< FopFactoryConfiguration.Renderer > ) stack.getBuildupUnderTop() ;
        final FopFactoryConfiguration.Renderer renderer =
            ( FopFactoryConfiguration.Renderer ) stack.getBuildupOnTop() ;
        return CollectionTools.append( renderers, renderer ) ;

      case FONTS_DIRECTORY :
        final FopFactoryConfiguration.Renderer renderer0 =
            ( FopFactoryConfiguration.Renderer ) stack.getBuildupUnderTop() ;
        final String directoryName = StringUtils.trim( getAndClearCollectedText() ) ;
        if( StringUtils.isBlank( directoryName ) ) {
          throwException( "Directory name cannot be empty" ) ;
        }
        final FopFactoryConfiguration.Renderer.FontsDirectory fontsDirectory =
            ( ( FopFactoryConfiguration.Renderer.FontsDirectory ) stack.getBuildupOnTop() )
            .withPath( directoryName )
        ;
        return renderer0.withFontsDirectories(
            CollectionTools.append( renderer0.getFontsDirectories(), fontsDirectory ) ) ;

      case OUTPUT_PROFILE:
        final FopFactoryConfiguration.Renderer renderer1 =
            ( FopFactoryConfiguration.Renderer ) stack.getBuildupUnderTop() ;
        final ResourceName profile = getResourceNameFromCollectedText() ;
        return renderer1.withOutputProfile( profile ) ;

      case FILTER_LIST:
        break;
      case VALUE:
        break;
    }

    // Default: avoid to discard stacks' top.
    return stack.getBuildupUnderTop() ;
  }


// =========
// Utilities
// =========

  private String getStringAttributeValue(
      final Attributes attributes,
      final XmlAttribute attribute
  ) throws ConfigurationStructureException {
    final String actualValue = attributes.getValue( attribute.getAttributeName() ) ;
    if( actualValue == null ) {
      throwException( "Missing '" + attribute.getAttributeName() + "' attribute" ) ;
      return null ; // Never executes but compiler gets happy.
    } else {
      return actualValue ;
    }
  }

  private boolean getBooleanAttributeValue(
      final Attributes attributes,
      final XmlAttribute attribute,
      final boolean defaultValue
  ) throws ConfigurationStructureException {
    final String actualValue = attributes.getValue( attribute.getAttributeName() ) ;
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

  /**
   * Side effect: clears collected text.
   */
  private int getIntegerFromCollectedText() throws ConfigurationStructureException {
    final String text = StringUtils.trim( getAndClearCollectedText() ) ;
    try {
      return Integer.parseInt( text ) ;
    } catch( NumberFormatException e ) {
      throwException( "Couldn't parse '" + text + "' as an integer value in " ) ;
      return 0 ; // Never executes but makes compiler happy.
    }
  }

  /**
   * Side effect: clears collected text.
   */
  private ResourceName getResourceNameFromCollectedText() throws ConfigurationStructureException {
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




// =======
// Ignored
// =======

  @Override
  public void startDocument() throws SAXException { }

  @Override
  public void endDocument() throws SAXException { }

  @Override
  public void ignorableWhitespace(
      final char[] ch,
      final int start,
      final int length
  ) throws SAXException { }

  @Override
  public void processingInstruction(
      final String target,
      final String data
  ) throws SAXException { }

  @Override
  public void skippedEntity( final String name ) throws SAXException { }
}

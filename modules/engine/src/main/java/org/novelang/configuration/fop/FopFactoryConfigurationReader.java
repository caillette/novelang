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

  private final String namespaceUri ;

  private static final Function<XmlElement,String> PATH_ELEMENT_TO_STRING =
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



// =======
// Locator
// =======

  private Locator locator = null ;

  @Override
  public void setDocumentLocator( final Locator locator ) {
    this.locator = locator ;
  }

  private void throwException( final String message ) throws ConfigurationStructureException {
    throw new ConfigurationStructureException( locator, message ) ;
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
      of( FOP, RENDERERS, RENDERER, FONTS ),
      of( FOP, RENDERERS, RENDERER, FONTS, DIRECTORY ),
      of( FOP, RENDERERS, RENDERER, OUTPUT_PROFILE ),
      of( FOP, RENDERERS, RENDERER, FILTER_LIST ),
      of( FOP, RENDERERS, RENDERER, FILTER_LIST, VALUE ),
      of( FOP )
  ) ;

  private final Stack< XmlElement > elementStack =
      new Stack< XmlElement >( ELEMENT_PATHS, PATH_ELEMENT_TO_STRING ) ;

  
  @Override
  public void startElement(
      final String uri,
      final String localName,
      final String qName,
      final Attributes attributes
  ) throws SAXException {

    final XmlElement element = XmlElement.fromLocalName( localName ) ;

    // We care of meta prefix only for local root FOP element.
    if( FOP == element ) {
      if( ! isMetaPrefix( uri ) ) {
        throwException( "Expecting '" + element + "' element in " +
            "'" + namespaceUri + "' namespace" ) ;
      }
    }

    if( element == null ) {
      throwException( "Unknown element: '" + localName + "'" ) ;
    } else {
      try {
        elementStack.push( element ) ;
      } catch( Stack.IllegalPathException e ) {
        throwException( e.getMessage() ) ;
      }
    }
  }

  @Override
  public void endElement(
      final String uri,
      final String localName,
      final String qName
  ) throws SAXException {
    if( ! elementStack.isEmpty() ) {
      elementStack.pop() ;
    }
  }

  @Override
  public void characters(
      final char[] ch,
      final int start,
      final int length
  ) throws SAXException {

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

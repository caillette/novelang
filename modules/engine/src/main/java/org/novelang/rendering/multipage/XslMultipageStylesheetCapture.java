/*
 * Copyright (C) 2008 Laurent Caillette
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
package org.novelang.rendering.multipage;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.dom4j.Document;
import org.dom4j.io.SAXContentHandler;
import org.novelang.common.SyntacticTree;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.xml.ContentHandlerAdapter;
import org.novelang.outfit.xml.XmlNamespaces;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Captures the XML document inside a &lt;{@value #MULTIPAGE_STYLESHEET_LOCALNAME}> element
 * (inside the "{@value org.novelang.outfit.xml.XmlNamespaces#XSL_META_NAMESPACE_URI}" namespace)
 * as a standalone XML document.
 * The purpose is to use it as a stylesheet afterwards.
 *
 * @author Laurent Caillette
 */
public class XslMultipageStylesheetCapture
    extends ContentHandlerAdapter
    implements PageIdentifierExtractor
{

  private static final Logger LOGGER = LoggerFactory.getLogger( XslMultipageStylesheetCapture.class ) ;

  private SAXContentHandler documentBuilder = null ;

  private final EntityResolver entityResolver ;

  private Document stylesheetDocument = null ;

  public XslMultipageStylesheetCapture( final EntityResolver entityResolver ) {
    this.entityResolver = Preconditions.checkNotNull( entityResolver ) ;
  }

  /**
   * This method allows to hide the {@link Document} object from the public API;
   * the {@link XslPageIdentifierExtractor} calls it to get what it wants.
   *
   * @return a possibly null object, depending on the state.
   */
  /*package*/ Document getStylesheetDocument() {
    return stylesheetDocument ;
  }

  private static final String MULTIPAGE_STYLESHEET_LOCALNAME = "multipage" ;

  private boolean isNestedStylesheetRootElement( final String uri, final String localName ) {
    return isMetaPrefix( uri ) && MULTIPAGE_STYLESHEET_LOCALNAME.equals( localName ) ;
  }

  private String getXsltPrefixMapping() {
    return getPrefixMappings().inverse().get( XmlNamespaces.XSL_NAMESPACE_URI ) ;
  }

  @Override
  public ImmutableMap< PageIdentifier, String > extractPageIdentifiers(
      final SyntacticTree documentTree
  ) throws Exception {
    throw new UnsupportedOperationException( "TODO" ) ;

  }

// ==============
// ContentHandler
// ==============

  @Override
  public void startElement(
      final String uri,
      final String localName,
      final String qName,
      final Attributes attributes
  ) throws SAXException {
    if( isNestedStylesheetRootElement( uri, localName ) ) {
      if( documentBuilder == null ) {
        stylesheetDocument = null ;
        documentBuilder = new SAXContentHandler() ;
        documentBuilder.setDocumentLocator( locator ) ;
        documentBuilder.setEntityResolver( entityResolver ) ;
        documentBuilder.startDocument() ;
        for( final Map.Entry< String, String > prefixMapping : getPrefixMappings().entrySet() ) {
          documentBuilder.startPrefixMapping( prefixMapping.getKey(), prefixMapping.getValue() ) ;
        }
      } else {
        throwException( "Not allowed: nested " +
            getNamespacePrefix() + ":" + MULTIPAGE_STYLESHEET_LOCALNAME ) ;
      }
    }
    if( documentBuilder != null ) {
      if( isNestedStylesheetRootElement( uri, localName ) ) {
        documentBuilder.startElement(
            XmlNamespaces.XSL_NAMESPACE_URI,
            "stylesheet",
            getXsltPrefixMapping() + ":" + "stylesheet",
            attributes
        ) ;
      } else {
        documentBuilder.startElement( uri, localName, qName, attributes ) ;
      }
    }

  }

  @Override
  public void endElement(
      final String uri,
      final String localName,
      final String qName
  ) throws SAXException {
    if( documentBuilder != null ) {
      if( isNestedStylesheetRootElement( uri, localName ) ) {
        documentBuilder.endElement(
            XmlNamespaces.XSL_NAMESPACE_URI,
            "stylesheet",
            getXsltPrefixMapping() + ":" + "stylesheet"
        ) ;
      } else {
        documentBuilder.endElement( uri, localName, qName ) ;
      }
      if( isNestedStylesheetRootElement( uri, localName ) ) {
        documentBuilder.endDocument() ;
        if( stylesheetDocument != null ) {
          LOGGER.warn( "Overwriting previous document" ) ;
        }
        stylesheetDocument = documentBuilder.getDocument() ;
        documentBuilder = null ;
      }
    }
  }

  @Override
  public void setDocumentLocator( final Locator locator ) {
    super.setDocumentLocator( locator ) ;
    if( documentBuilder != null ) {
      documentBuilder.setDocumentLocator( locator ) ;
    }
  }

  @Override
  public void startPrefixMapping( final String prefix, final String uri ) throws SAXException {
    super.startPrefixMapping( prefix, uri ) ;
    if( documentBuilder != null ) {
      documentBuilder.startPrefixMapping( prefix, uri ) ;
    }
  }

  @Override
  public void endPrefixMapping( final String prefix ) throws SAXException {
    super.endPrefixMapping( prefix ) ;
    if( documentBuilder != null ) {
      documentBuilder.endPrefixMapping( prefix ) ;
    }
  }

  @Override
  public void characters(
      final char[] characters,
      final int start,
      final int length
  ) throws SAXException {
    if( documentBuilder != null ) {
      documentBuilder.characters( characters, start, length ) ;
    }
  }

  @Override
  public void ignorableWhitespace(
      final char[] characters,
      final int start,
      final int length
  ) throws SAXException {
    if( documentBuilder != null ) {
      documentBuilder.ignorableWhitespace( characters, start, length ) ;
    }
  }

  @Override
  public void processingInstruction(
      final String target,
      final String data
  ) throws SAXException {
    if( documentBuilder != null ) {
      documentBuilder.processingInstruction( target, data ) ;
    }
  }

  @Override
  public void skippedEntity( final String name ) throws SAXException {
    if( documentBuilder != null ) {
      documentBuilder.skippedEntity( name ) ;
    }
  }
}

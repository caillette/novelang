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
package org.novelang.outfit.xml;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Makes a single SAX event source feed one SAX event consumer or more through the
 * {@link ContentHandler} interface.
 * <p>
 * All exceptions are fatal; if one {@code ContentHandler} throws an exception, there is no
 * recovery and other handlers won't be called.
 * <p>
 * TODO: refactor into a {@code SaxInterceptorStack}
 * where elements may implement a {@code SaxCancellator} interface with a single
 * {@code boolean cancelNext()} method. When this method returns true, the {@link SaxMulticaster}
 * doesn't call methods for elements above in the stack (saying for one method to intercept,
 * the {@link SaxMulticaster} calls the same method for every element of the stack from bottom
 * to top. This way, the {@link XslMultipageStylesheetCapture} can swallow XSL operations
 * in the metadata section, so they don't appear at a bad place.
 *
 *
 * @author Laurent Caillette
 */
public final class SaxMulticaster implements ContentHandler {

  private final List< ContentHandler > handlers  ;

  public SaxMulticaster(
      final ContentHandler first,
      final ContentHandler... others
  ) {
    this( ImmutableList.< ContentHandler >builder().add( first ).add( others ).build() ) ;
  }

  public SaxMulticaster( final ImmutableList< ContentHandler > handlers ) {
    this.handlers = Lists.newArrayList( handlers ) ;
  }

  public void removeAll() {
    handlers.clear() ;
  }

// ======================
// ContentHandler methods
// ======================

  @Override
  public void setDocumentLocator( final Locator locator ) {
    for( final ContentHandler handler : handlers ) {
      handler.setDocumentLocator( locator ) ;
    }
  }

  @Override
  public void startDocument() throws SAXException {
    for( final ContentHandler handler : handlers ) {
      handler.startDocument() ;
    }
  }

  @Override
  public void endDocument() throws SAXException {
    for( final ContentHandler handler : handlers ) {
      handler.endDocument() ;
    }
  }

  @Override
  public void startPrefixMapping( final String prefix, final String uri ) throws SAXException {
    for( final ContentHandler handler : handlers ) {
      handler.startPrefixMapping( prefix, uri ) ;
    }
  }

  @Override
  public void endPrefixMapping( final String prefix ) throws SAXException {
    for( final ContentHandler handler : handlers ) {
      handler.endPrefixMapping( prefix ) ;
    }
  }

  @Override
  public void startElement(
      final String uri,
      final String localName,
      final String qName,
      final Attributes atts
  ) throws SAXException {
    for( final ContentHandler handler : handlers ) {
      handler.startElement( uri, localName, qName, atts ) ;
    }
  }

  @Override
  public void endElement(
      final String uri, 
      final String localName, 
      final String qName 
  ) throws SAXException {
    for( final ContentHandler handler : handlers ) {
      handler.endElement( uri, localName, qName ) ;
    }
  }

  @Override
  public void characters(
      final char[] ch, 
      final int start, 
      final int length 
  ) throws SAXException {
    for( final ContentHandler handler : handlers ) {
      handler.characters( ch, start, length ) ;
    }
  }

  @Override
  public void ignorableWhitespace(
      final char[] ch, 
      final int start, 
      final int length 
  ) throws SAXException {
    for( final ContentHandler handler : handlers ) {
      handler.ignorableWhitespace( ch, start, length ) ;
    }
  }

  @Override
  public void processingInstruction( final String target, final String data ) throws SAXException {
    for( final ContentHandler handler : handlers ) {
      handler.processingInstruction( target, data ) ;
    }
  }

  @Override
  public void skippedEntity( final String name ) throws SAXException {
    for( final ContentHandler handler : handlers ) {
      handler.skippedEntity( name ) ;
    }
  }
}

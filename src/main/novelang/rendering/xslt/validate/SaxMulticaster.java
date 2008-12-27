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
package novelang.rendering.xslt.validate;

import java.util.List;

import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import com.google.common.collect.Lists;
import com.google.common.base.Preconditions;

/**
 * Makes a single SAX event source feed two SAX event consumers through the {@link ContentHandler}
 * interface.
 * <p>
 * All exceptions are fatal; if one {@code ContentHandler} throws an exception, there is no
 * recovery and other handlers won't be called. 
 *
 * @author Laurent Caillette
 */
public class SaxMulticaster implements ContentHandler {

  private final List< ContentHandler > handlers = Lists.newLinkedList() ;

  public void add( ContentHandler handler ) {
    Preconditions.checkNotNull( handler ) ;
    handlers.add( handler ) ;
  }

// ======================
// ContentHandler methods
// ======================

  public void setDocumentLocator( Locator locator ) {
    for( ContentHandler handler : handlers ) {
      handler.setDocumentLocator( locator ) ;
    }
  }

  public void startDocument() throws SAXException {
    for( ContentHandler handler : handlers ) {
      handler.startDocument() ;
    }
  }

  public void endDocument() throws SAXException {
    for( ContentHandler handler : handlers ) {
      handler.endDocument() ;
    }
  }

  public void startPrefixMapping( String prefix, String uri ) throws SAXException {
    for( ContentHandler handler : handlers ) {
      handler.startPrefixMapping( prefix, uri ) ;
    }
  }

  public void endPrefixMapping( String prefix ) throws SAXException {
    for( ContentHandler handler : handlers ) {
      handler.endPrefixMapping( prefix ) ;
    }
  }

  public void startElement(
      String uri,
      String localName,
      String qName,
      Attributes atts
  ) throws SAXException {
    for( ContentHandler handler : handlers ) {
      handler.startElement( uri, localName, qName, atts ) ;
    }
  }

  public void endElement( String uri, String localName, String qName ) throws SAXException {
    for( ContentHandler handler : handlers ) {
      handler.endElement( uri, localName, qName ) ;
    }
  }

  public void characters( char[] ch, int start, int length ) throws SAXException {
    for( ContentHandler handler : handlers ) {
      handler.characters( ch, start, length ) ;
    }
  }

  public void ignorableWhitespace( char[] ch, int start, int length ) throws SAXException {
    for( ContentHandler handler : handlers ) {
      handler.ignorableWhitespace( ch, start, length ) ;
    }
  }

  public void processingInstruction( String target, String data ) throws SAXException {
    for( ContentHandler handler : handlers ) {
      handler.processingInstruction( target, data ) ;
    }
  }

  public void skippedEntity( String name ) throws SAXException {
    for( ContentHandler handler : handlers ) {
      handler.skippedEntity( name ) ;
    }
  }
}

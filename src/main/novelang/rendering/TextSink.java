/*
 * Copyright (C) 2006 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.rendering;

import java.io.PrintWriter;
import java.io.OutputStream;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;

/**
 * 
 * @author Laurent Caillette
*/
/*package*/ class TextSink implements ContentHandler {

  private final PrintWriter writer ;

  public TextSink( OutputStream outputStream ) {
    writer = new PrintWriter( outputStream );
  }

  public void characters( char chars[], int start, int length ) throws SAXException {
    writer.write( chars, start, length ) ;
  }

  public void endDocument() throws SAXException {
    writer.flush() ;
  }

  public void ignorableWhitespace( char ch[], int start, int length ) throws SAXException {
    writer.write( ch, start, length ) ;
  }

  public void setDocumentLocator( Locator locator ) { }

  public void startDocument() throws SAXException { }

  public void startPrefixMapping( String prefix, String uri ) throws SAXException { }

  public void endPrefixMapping( String prefix ) throws SAXException { }

  public void startElement( String uri, String localName, String qName, Attributes atts )
      throws SAXException { }

  public void endElement( String uri, String localName, String qName ) throws SAXException { }

  public void processingInstruction( String target, String data ) throws SAXException { }

  public void skippedEntity( String name ) throws SAXException { }
}

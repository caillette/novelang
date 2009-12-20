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

package novelang.rendering;

import java.io.OutputStream;
import java.io.PrintWriter;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * 
 * @author Laurent Caillette
*/
/*package*/ class TextSink implements ContentHandler {

  private final PrintWriter writer ;

  public TextSink( final OutputStream outputStream ) {
    writer = new PrintWriter( outputStream );
  }

  public void characters( 
      final char[] chars, 
      final int start, 
      final int length 
  ) throws SAXException {
    writer.write( chars, start, length ) ;
  }

  public void endDocument() throws SAXException {
    writer.flush() ;
  }

  public void ignorableWhitespace( 
      final char[] ch, 
      final int start, 
      final int length 
  ) throws SAXException {
    writer.write( ch, start, length ) ;
  }

  public void setDocumentLocator( final Locator locator ) { }

  public void startDocument() throws SAXException { }

  public void startPrefixMapping( final String prefix, final String uri ) throws SAXException { }

  public void endPrefixMapping( final String prefix ) throws SAXException { }

  public void startElement( 
      final String uri, 
      final String localName, 
      final String qName, 
      final Attributes atts 
  ) throws SAXException { }

  public void endElement( 
      final String uri, 
      final String localName, 
      final String qName 
  ) throws SAXException { }

  public void processingInstruction( 
      final String target, 
      final String data 
  ) throws SAXException { }

  public void skippedEntity( final String name ) throws SAXException { }
}

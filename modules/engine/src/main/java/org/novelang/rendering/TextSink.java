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

package org.novelang.rendering;

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

  @Override
  public void characters(
      final char[] chars, 
      final int start, 
      final int length 
  ) throws SAXException {
    writer.write( chars, start, length ) ;
  }

  @Override
  public void endDocument() throws SAXException {
    writer.flush() ;
  }

  @Override
  public void ignorableWhitespace(
      final char[] ch, 
      final int start, 
      final int length 
  ) throws SAXException {
    writer.write( ch, start, length ) ;
  }

  @Override
  public void setDocumentLocator( final Locator locator ) { }

  @Override
  public void startDocument() throws SAXException { }

  @Override
  public void startPrefixMapping( final String prefix, final String uri ) throws SAXException { }

  @Override
  public void endPrefixMapping( final String prefix ) throws SAXException { }

  @Override
  public void startElement(
      final String uri, 
      final String localName, 
      final String qName, 
      final Attributes atts 
  ) throws SAXException { }

  @Override
  public void endElement(
      final String uri, 
      final String localName, 
      final String qName 
  ) throws SAXException { }

  @Override
  public void processingInstruction(
      final String target, 
      final String data 
  ) throws SAXException { }

  @Override
  public void skippedEntity( final String name ) throws SAXException { }
}

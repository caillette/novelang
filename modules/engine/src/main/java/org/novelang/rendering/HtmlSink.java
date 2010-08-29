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

package org.novelang.rendering;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Just write HTML with entities verbatim.
 * Attempts to make {@link org.dom4j.io.HTMLWriter} eat entites as produced
 * by XSL transformation were unsuccessful, while debugger showed that
 * correct characters were passed (though unescaped).
 *
 * @author Laurent Caillette
 */
public class HtmlSink implements ContentHandler {

  private final PrintWriter writer ;

  public HtmlSink( final OutputStream outputStream, final Charset charset ) {
    final OutputStreamWriter outputStreamWriter = new OutputStreamWriter( outputStream, charset ) ;    
    this.writer = new PrintWriter( outputStreamWriter, true ) ;
  }

  private static boolean isElementIWorthALineBreak( final String elementName ) {
    final String upperCaseName = elementName.toUpperCase() ;
    return
        upperCaseName.startsWith( "P" )  ||
        upperCaseName.startsWith( "BLOCKQUOTE" ) ||
        upperCaseName.startsWith( "H" ) ||
        upperCaseName.startsWith( "META" )
    ;
  }


// ==================================
// Interesting ContentHandler methods
// ==================================

  public void startDocument() throws SAXException {
    writer.println( "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"" ) ;
    writer.println( "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" ) ;
  }

  public void endDocument() throws SAXException {
    writer.flush() ;
  }

  public void startElement(
      final String uri,
      final String localName,
      final String qName,
      final Attributes atts
  ) throws SAXException {
    writer.append( "<" ).append( localName ) ;
    for( int i = 0 ; i < atts.getLength() ; i++ ) {
      writer.append( " " ).append( atts.getLocalName( i ) ).
          append( "=\"" ).append( atts.getValue( i )).append( "\"" ) ;
    }
    writer.append( " >" ) ;
    if( isElementIWorthALineBreak( localName ) ) {
      writer.println() ;
    }
  }

  public void endElement(
      final String uri,
      final String localName,
      final String qName
  ) throws SAXException {
    if( isElementIWorthALineBreak( localName ) ) {
      writer.println() ;
      writer.append( "</" ).append( localName ).append( ">" ) ;
      writer.println() ;
    } else {
      writer.append( "</" ).append( localName ).append( ">" ) ;
    }    
  }

  public void characters(
      final char[] chars,
      final int start,
      final int length
  ) throws SAXException {
    writer.write( chars, start, length );
  }

  public void ignorableWhitespace(
      final char[] chars,
      final int start,
      final int length
  ) throws SAXException {
    writer.write( chars, start, length );
  }


// ====================================
// Uninteresting ContentHandler methods
// ====================================

  public void setDocumentLocator( final Locator locator ) { }

  public void startPrefixMapping( final String prefix, final String uri )
      throws SAXException
  { }

  public void endPrefixMapping( final String prefix ) throws SAXException { }

  public void processingInstruction(
      final String target,
      final String data
  ) throws SAXException { }

  public void skippedEntity( final String name ) throws SAXException { }
}

/*
 * Copyright (C) 2008 Laurent Caillette
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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import novelang.model.common.NodePath;
import novelang.model.common.TreeMetadata;
import novelang.parser.Symbols;

/**
 * @author Laurent Caillette
 */
public class NlpWriter extends XslWriter {

  protected static final String DEFAULT_NLP_STYLESHEET =  "nlp.xsl" ;


  public NlpWriter() {
    super( DEFAULT_NLP_STYLESHEET, RenditionMimeType.NLP ) ;
  }

  public void write( NodePath kinship, String word ) throws Exception {
    final StringBuffer reconstructed = new StringBuffer() ;
    for( char c : word.toCharArray() ) {
      final String s = "" + c ; // Let the compiler optimize this!
      final String escaped = Symbols.escape( s ) ;
      if( null == escaped ) {
        reconstructed.append( c ) ;
      } else {
        reconstructed.append( "&" ).append( escaped ).append( ";" ) ;
      }
    }
    super.write( kinship, reconstructed.toString() ) ;
  }

  public void writeLitteral( NodePath kinship, String word ) throws Exception {
    super.write( kinship, word ) ;
  }

  // ==========
// Generation
// ==========

  protected final ContentHandler createSinkContentHandler(
      final OutputStream outputStream,
      TreeMetadata treeMetadata,
      final Charset encoding
  )
      throws Exception
  {

    // dom4j's XML writer does some clever entity-escaping not good for us.
    final ContentHandler sink = new ContentHandler() {

      final PrintWriter writer ;
      {
        writer = new PrintWriter( outputStream ) ;
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
    } ;

    return sink ;
  }


}
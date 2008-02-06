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
package novelang.renderer;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.dom4j.io.XMLWriter;
import org.dom4j.io.OutputFormat;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.AttributesImpl;
import novelang.model.common.Tree;
import novelang.model.common.NodeKind;
import novelang.model.implementation.Book;

/**
 * @author Laurent Caillette
 */
public class XmlRenderer implements Renderer {

  public void renderBook( Book book, OutputStream outputStream ) {
    try {
      final ContentHandler contentHandler =
          createContentHandler( outputStream, book.getEncoding() ) ;
      contentHandler.startDocument() ;
      renderTree( contentHandler, book.createBookTree() ) ;
      contentHandler.endDocument() ; // Does that flush?
    } catch( Exception e ) {
      throw new RuntimeException( e );
    }

  }

  public String getMimeType() {
    return "text/xml" ;
  }

  protected ContentHandler createContentHandler( OutputStream outputStream, Charset encoding )
      throws Exception
  {
    final XMLWriter xmlWriter ;
    try {
      xmlWriter = new XMLWriter(
          outputStream,
          new OutputFormat( "  ", true, encoding.name() )
      ) ;
    } catch( UnsupportedEncodingException e ) {
      throw new RuntimeException( e );
    }
    return xmlWriter;
  }

  private void renderTree( ContentHandler contentHandler, Tree tree ) throws SAXException {

    final String text = tree.getText() ;
    final NodeKind nodeKind = Enum.valueOf( NodeKind.class, text ) ;
    switch( nodeKind ) {

      case _BOOK :
      case PART :
      case CHAPTER :
      case SECTION :
      case _SPEECH_SEQUENCE :
      case PARAGRAPH_PLAIN :
      case PARAGRAPH_SPEECH :
      case PARAGRAPH_SPEECH_CONTINUED :
      case PARAGRAPH_SPEECH_ESCAPED :
      case QUOTE :
        startElement( contentHandler, text ) ;
        for( Tree subtree : tree.getChildren() ) {
          renderTree( contentHandler, subtree ) ;
        }
        endElement( contentHandler, text ) ;
        break ;

      case WORD :
      case WORDTRAIL :
        for( Tree wordToken : tree.getChildren() ) {
          word( contentHandler, wordToken.getText() ) ;
        }
        break ;

      default :
        break ;
//        throw new RuntimeException( "Unsupported token: " + text ) ;

    }
  }

  private String tokenNameAsXmlElementName( String tokenName ) {
    String result = tokenName.toLowerCase().replace( "_", "-" ) ;
    if( result.startsWith( "-" ) ) {
      result = result.substring( 1 ) ;
    }
    return result ;
  }

  private static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl() ;
  private static final char[] WORD_SEPARATOR = new char[] { ' ' } ;

  private void startElement( ContentHandler contentHandler, String tokenName ) throws SAXException {
    final String name = tokenNameAsXmlElementName( tokenName ) ;
    contentHandler.startElement( "", "", name, EMPTY_ATTRIBUTES ) ;
  }

  private void endElement( ContentHandler contentHandler, String tokenName ) throws SAXException {
    final String name = tokenNameAsXmlElementName( tokenName ) ;
    contentHandler.endElement( "", "", name ) ;
  }

  private void word( ContentHandler contentHandler, String word ) throws SAXException {
    contentHandler.characters( WORD_SEPARATOR, 0, 1 ) ;
    contentHandler.characters( word.toCharArray(), 0, word.length() ) ;
  }


}

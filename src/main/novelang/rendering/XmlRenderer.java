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
import novelang.model.renderable.Renderable;

/**
 * @author Laurent Caillette
 */
public class XmlRenderer extends AbstractRenderer {

  public void render( Renderable rendered, OutputStream outputStream ) {
    if( rendered.hasProblem() ) {
      renderProblems( rendered.getProblems(), outputStream ) ;
    } else {
      try {
        final ContentHandler contentHandler =
            createContentHandler( outputStream, rendered.getEncoding() ) ;
        contentHandler.startDocument() ;
        renderTree( contentHandler, rendered.getTree() ) ;
        contentHandler.endDocument() ; // Does that flush?
      } catch( Exception e ) {
        throw new RuntimeException( e );
      }
    }
  }

  public RenditionMimeType getMimeType() {
    return RenditionMimeType.XML ;
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

    if( ! NodeKind.treeTextHasNodeKindName( tree ) ) {
      throw new IllegalArgumentException( "This tree has no NodeKind: " + tree.toStringTree() ) ;
    }

    final String text = tree.getText() ;
    final NodeKind nodeKind = Enum.valueOf( NodeKind.class, text ) ;
    boolean declareNamespace = false ;
    switch( nodeKind ) {

      case _BOOK :
      case PART :
        declareNamespace = true ;
      case CHAPTER :
      case SECTION :
      case TITLE :
      case IDENTIFIER :
      case _SPEECH_SEQUENCE :
      case LOCUTOR :
      case PARAGRAPH_PLAIN :
      case PARAGRAPH_SPEECH :
      case PARAGRAPH_SPEECH_CONTINUED :
      case PARAGRAPH_SPEECH_ESCAPED :
      case PARENTHESIS :
      case EMPHASIS :
      case QUOTE :
      case BLOCKQUOTE :
        startElement( contentHandler, text, declareNamespace ) ;
        for( Tree subtree : tree.getChildren() ) {
          renderTree( contentHandler, subtree ) ;
        }
        endElement( contentHandler, text ) ;
        break ;

      case WORD :
        boolean first = true ;
        for( final Tree wordToken : tree.getChildren() ) {
          word( contentHandler, wordToken.getText(), first ) ;
          first = false ;
        }
        break ;


      case PUNCTUATION_SIGN :
        final String punctuationSignName = tree.getChildAt( 0 ).getText() ;
        startElement( contentHandler, punctuationSignName, false ) ;
        endElement( contentHandler, punctuationSignName ) ;
        break ;


      default :
        throw new RuntimeException( "Unsupported token: " + text ) ;

    }
  }

  private String tokenNameAsXmlElementName( String tokenName ) {
    String result = tokenName.toLowerCase().replace( "_", "-" ) ;
    if( result.startsWith( "-" ) ) {
      result = result.substring( 1 ) ;
    }
    return result ;
  }

  private static final String NAMESPACE_URI = "http://novelang.org/book-xml/1.0" ;
  private static final String NAME_QUALIFIER = "n" ;
  private static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl() ;
  private static final char[] WORD_SEPARATOR = new char[] { ' ' } ;

  private void startElement(
      ContentHandler contentHandler,
      String tokenName,
      boolean declareNamespace
  ) throws SAXException {
    tokenName = tokenNameAsXmlElementName( tokenName ) ;
    final Attributes attributes ;
    if( declareNamespace ) {
      final AttributesImpl mutableAttributes = new AttributesImpl() ;
      mutableAttributes.addAttribute(
          NAMESPACE_URI,
          NAME_QUALIFIER,
          "xmlns:" + NAME_QUALIFIER,
          "CDATA",
          NAMESPACE_URI
      ) ;
      attributes = mutableAttributes ;
    } else {
      attributes = EMPTY_ATTRIBUTES ;
    }
    contentHandler.startElement( 
        NAMESPACE_URI,
        tokenName,
        NAME_QUALIFIER + ":" + tokenName,
        attributes
    ) ;
  }

  private void endElement( ContentHandler contentHandler, String tokenName ) throws SAXException {
    tokenName = tokenNameAsXmlElementName( tokenName ) ;
    contentHandler.endElement( NAMESPACE_URI, tokenName, NAME_QUALIFIER + ":" + tokenName ) ;
  }

  private void word(
      ContentHandler contentHandler,
      String word,
      boolean separatorBefore
  ) throws SAXException {
    contentHandler.characters( WORD_SEPARATOR, 0, 1 ) ;
    contentHandler.characters( word.toCharArray(), 0, word.length() ) ;
  }


}

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
package novelang.renderer;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

import org.antlr.runtime.RecognitionException;
import novelang.model.common.NodeKind;
import novelang.model.common.Tree;
import novelang.model.renderable.Renderable;
import novelang.parser.ProblemDescription;

/**
 * A scratch version of a Renderer.
 *
 * @author Laurent Caillette
 */
public class PlainTextRenderer implements Renderer {

  public static String renderAsString( Tree tree ) {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream() ;
    new PlainTextRenderer().renderTree( tree, outputStream ) ;
    final String renderer = new String( outputStream.toByteArray() ) ;
    return renderer ; 
  }

  public RenditionMimeType render( Renderable rendered, OutputStream stream ) {
    final PrintWriter writer = new PrintWriter( stream ) ;
    if( rendered.hasProblem() ) {
      doRender( rendered.getProblems(), writer ) ;
    } else {
      doRender( rendered.getTree(), writer, 0 ) ;
    }
    writer.flush() ;
    return RenditionMimeType.TEXT ;
  }

  private void doRender( Iterable< Exception > problems, PrintWriter writer ) {
    for( final Exception exception : problems ) {
      if( exception instanceof ProblemDescription ) {
        writer.println( exception ) ; 
      } else {
        exception.printStackTrace( writer ) ;
      }
    }
  }

  private void renderTree( Tree tree, OutputStream stream ) {
    final PrintWriter writer = new PrintWriter( stream ) ;
    doRender( tree, writer, 0 ) ;
    writer.flush() ;
  }

  private void doRender( Tree tree, PrintWriter writer, int indent ) {
    final NodeKind nodeKind = NodeKind.getToken( tree ) ;
    switch( nodeKind ) {

      case _BOOK :
      case PART :
      case CHAPTER :
      case SECTION :
      case TITLE :
      case _SPEECH_SEQUENCE :
      case PARAGRAPH_PLAIN :
      case PARAGRAPH_SPEECH :
      case PARAGRAPH_SPEECH_CONTINUED :
      case PARAGRAPH_SPEECH_ESCAPED :
      case EMPHASIS :
      case PARENTHESIS :
      case QUOTE :
      case BLOCKQUOTE :
        doRenderContainer( tree, writer, nodeKind, indent ) ;
        break ;

      case WORD :
        for( Tree wordToken : tree.getChildren() ) {
          writer.append( wordToken.getText() ).append( " " ) ;
        }
        break ;

      case PUNCTUATION_SIGN :
        writer.append( RenderTools.generatePunctuationSign( tree, " " ) ) ;

      default :
        break ;

    }

  }

  private void doRenderContainer( Tree tree, PrintWriter writer, NodeKind token, int indent ) {
    final String indentPlus = RenderTools.spaces( indent + 1 ) ;
    final String indentMinus = RenderTools.spaces( indent - 1 ) ;
    writer.append( token.name() ).append( " { \n" ).append( indentPlus ) ;
    doRender( tree.getChildren(), writer, indent + 1 ) ;
    writer.append( "\n" ).append( indentMinus ).append( "}\n" ).append( indentMinus ) ;
  }


  private void doRender( Iterable< Tree > trees, PrintWriter writer, int indent ) {
    for( final Tree tree : trees ) {
      doRender( tree, writer, indent + 1 ) ;
    }
  }

}

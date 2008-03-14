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
import java.io.ByteArrayOutputStream;

import novelang.model.common.NodeKind;
import novelang.model.common.Tree;
import novelang.model.renderable.Renderable;

/**
 * A scratch version of a Renderer.
 *
 * @author Laurent Caillette
 */
public class PlainTextRenderer extends AbstractRenderer {

  public static String renderAsString( Tree tree ) {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream() ;
    new PlainTextRenderer().renderTree( tree, outputStream ) ;
    final String renderer = new String( outputStream.toByteArray() ) ;
    return renderer ; 
  }

  public void render( Renderable rendered, OutputStream stream ) {
    if( rendered.hasProblem() ) {
      renderProblems( rendered.getProblems(), stream ) ;
    } else {
      final PrintWriter writer = new PrintWriter( stream ) ;
      doRender( rendered.getTree(), writer, 0 ) ;
      writer.flush() ;
    }
  }

  public RenditionMimeType getMimeType() {
    return RenditionMimeType.TXT;
  }

  private void renderTree( Tree tree, OutputStream stream ) {
    final PrintWriter writer = new PrintWriter( stream ) ;
    doRender( tree, writer, 0 ) ;
    writer.flush() ;
  }

  private void doRender( Tree tree, PrintWriter writer, int indent ) {

    if( ! NodeKind.treeTextHasNodeKindName( tree ) ) {
      throw new IllegalArgumentException(
          "This tree has no NodeKind: " + tree + " => '" + tree.toStringTree() + "'" ) ;
    }

    final NodeKind nodeKind = NodeKind.getToken( tree ) ;
    switch( nodeKind ) {

      case WORD :
        for( Tree wordToken : tree.getChildren() ) {
          writer.append( wordToken.getText() ).append( " " ) ;
        }
        break ;

      case PUNCTUATION_SIGN :
        writer.append( RenderTools.generatePunctuationSign( tree, " " ) ) ;
        break ;

      default :
        doRenderContainer( tree, writer, nodeKind, indent ) ;
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

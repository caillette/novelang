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

import java.io.PrintWriter;
import java.io.OutputStream;

import novelang.model.common.Problem;
import novelang.model.common.NodeKind;
import novelang.model.common.Tree;
import novelang.model.renderable.Renderable;
import com.google.common.base.Objects;

/**
 * @author Laurent Caillette
 */
public class GenericRenderer implements Renderer {

  private final FragmentWriter fragmentWriter;

  public GenericRenderer( FragmentWriter fragmentWriter ) {
    this.fragmentWriter = Objects.nonNull( fragmentWriter ) ;
  }

  final public void render( Renderable rendered, OutputStream outputStream ) {
    if( rendered.hasProblem() ) {
      renderProblems( rendered.getProblems(), outputStream ) ;
    } else {
      try {
        fragmentWriter.startWriting( outputStream, rendered.getEncoding() ) ;
        final Tree root = rendered.getTree();
        renderTree( root, new FragmentWriter.Path( NodeKind.ofRoot( root ) ), null ) ;
        fragmentWriter.finishWriting() ;
      } catch( Exception e ) {
        throw new RuntimeException( e );
      }
    }
  }

  public RenditionMimeType getMimeType() {
    return fragmentWriter.getMimeType() ;
  }


  private void renderTree(
      Tree tree,
      FragmentWriter.Path kinship,
      NodeKind previous
  ) throws Exception {

    final NodeKind nodeKind = NodeKind.ofRoot( tree ) ;
    final FragmentWriter.Path newPath = new FragmentWriter.Path( kinship, nodeKind ) ;
    boolean declareNamespace = false ;

    switch( nodeKind ) {

      case WORD :
        maybeWriteWhitespace( previous, nodeKind ) ;
        final Tree wordTree = tree.getChildAt( 0 ) ;
        fragmentWriter.just( wordTree.getText() ) ;
        break ;

      case SIGN_COLON :
      case SIGN_COMMA :
      case SIGN_ELLIPSIS :
      case SIGN_EXCLAMATIONMARK :
      case SIGN_FULLSTOP :
      case SIGN_QUESTIONMARK :
      case SIGN_SEMICOLON :
        fragmentWriter.start( newPath, false ); ;
        fragmentWriter.end( newPath, false ) ;
        break ;

      case _BOOK :
      case PART :
        declareNamespace = true ;

      default :
        fragmentWriter.start( newPath, declareNamespace ) ;
        for( Tree subtree : tree.getChildren() ) {
          maybeWriteWhitespace( previous, nodeKind ) ;
          renderTree( subtree, newPath, nodeKind ) ;
          previous = nodeKind ;
        }
        fragmentWriter.end( newPath, declareNamespace ) ;
        break ;

    }

  }

  private void maybeWriteWhitespace( NodeKind previous, NodeKind nodeKind ) throws Exception {
    if( WhitespaceTrigger.isTrigger( previous, nodeKind ) ) {
      fragmentWriter.just( "¨" ) ;
    }
  }

  protected RenditionMimeType renderProblems(
      Iterable< Problem > problems,
      OutputStream outputStream
  ) {
    final PrintWriter writer = new PrintWriter( outputStream ) ;
    for( final Problem problem : problems ) {
      writer.println( problem.getLocation() ) ;
      writer.println( "    " + problem.getMessage() ) ;
    }
    writer.flush() ;
    return RenditionMimeType.TXT;
  }


}

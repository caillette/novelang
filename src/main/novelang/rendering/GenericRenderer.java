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
import novelang.model.common.NodePath;
import novelang.model.common.TreeMetadata;
import novelang.model.renderable.Renderable;
import novelang.model.implementation.MetadataHelper;
import com.google.common.base.Objects;

/**
 * @author Laurent Caillette
 */
public class GenericRenderer implements Renderer {

  private final FragmentWriter fragmentWriter ;
  private final String whitespace ;

  private static final String DEFAULT_WHITESPACE = " " ;

  public GenericRenderer( FragmentWriter fragmentWriter ) {
    this( fragmentWriter, DEFAULT_WHITESPACE ) ;
  }

  public GenericRenderer( FragmentWriter fragmentWriter, String whitespace ) {
    this.fragmentWriter = Objects.nonNull( fragmentWriter ) ;
    this.whitespace = whitespace ;
  }

  final public void render(
      Renderable rendered,
      OutputStream outputStream
  ) {
    if( rendered.hasProblem() ) {
      renderProblems( rendered.getProblems(), outputStream ) ;
    } else {
      try {
        fragmentWriter.startWriting(
            outputStream,
            rendered.getTreeMetadata(),
            rendered.getEncoding()
        ) ;
        final Tree root = rendered.getTree() ;
        renderTree( root, null, null ) ;
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
      NodePath kinship,
      NodeKind previous
  ) throws Exception {

    final NodeKind nodeKind = NodeKind.ofRoot( tree ) ;
    final NodePath newPath = (
        null == kinship ? new NodePath( nodeKind ) : new NodePath( kinship, nodeKind ) ) ;
    boolean rootElement = false ;

    switch( nodeKind ) {

      case WORD :
        final Tree wordTree = tree.getChildAt( 0 ) ;
        fragmentWriter.write( newPath, wordTree.getText() ) ;
        break ;

      case _META_TIMESTAMP :
        fragmentWriter.start( newPath, false ) ;
        final Tree timestampTree = tree.getChildAt( 0 ) ;
        fragmentWriter.write( newPath, timestampTree.getText() ) ;
        fragmentWriter.end( newPath ) ;
        break ;

      case SIGN_COLON :
      case SIGN_COMMA :
      case SIGN_ELLIPSIS :
      case SIGN_EXCLAMATIONMARK :
      case SIGN_FULLSTOP :
      case SIGN_QUESTIONMARK :
      case SIGN_SEMICOLON :
        fragmentWriter.start( newPath, false ); ;
        fragmentWriter.end( newPath ) ;
        break ;

      case _BOOK :
      case PART :
        rootElement = true ;

      default :
        fragmentWriter.start( newPath, rootElement ) ;
        previous = null ;
        for( Tree subtree : tree.getChildren() ) {
          final NodeKind subtreeNodeKind = NodeKind.ofRoot( subtree );
          maybeWriteWhitespace( newPath, previous, subtreeNodeKind ) ;
          renderTree( subtree, newPath, previous ) ;
          previous = subtreeNodeKind;
        }
        fragmentWriter.end( newPath ) ;
        break ;

    }

  }

  private void maybeWriteWhitespace(
      NodePath path,
      NodeKind previous,
      NodeKind nodeKind
  ) throws Exception {
    if( WhitespaceTrigger.isTrigger( previous, nodeKind ) ) {
      fragmentWriter.write( path, whitespace ) ;
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

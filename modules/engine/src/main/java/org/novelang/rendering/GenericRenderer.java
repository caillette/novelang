/*
 * Copyright (C) 2011 Laurent Caillette
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

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import static org.novelang.parser.NodeKind.*;

import org.novelang.common.Location;
import org.novelang.common.Nodepath;
import org.novelang.common.Problem;
import org.novelang.common.Renderable;
import org.novelang.common.SyntacticTree;
import org.novelang.common.TagBehavior;
import org.novelang.common.metadata.MetadataHelper;
import org.novelang.common.metadata.Page;
import org.novelang.common.metadata.PageIdentifier;
import org.novelang.parser.NodeKind;
import org.novelang.parser.NodeKindTools;
import org.novelang.rendering.multipage.PagesExtractor;

/**
 * The only implementation of {@code Renderer} making sense as it delegates all specific
 * tasks to {@link org.novelang.rendering.FragmentWriter}.
 *
 * @author Laurent Caillette
 */
public class GenericRenderer implements Renderer {

  private final FragmentWriter fragmentWriter ;
  private final String whitespace ;
  private final boolean renderLocation ;

  private static final String DEFAULT_WHITESPACE = " " ;

  public GenericRenderer( final FragmentWriter fragmentWriter ) {
    this( fragmentWriter, false, DEFAULT_WHITESPACE ) ;
  }

  protected GenericRenderer(
      final FragmentWriter fragmentWriter,
      final boolean renderLocation,
      final String whitespace
  ) {
    this.fragmentWriter = Preconditions.checkNotNull( fragmentWriter ) ;
    this.whitespace = whitespace ;
    this.renderLocation = renderLocation ;

  }


  public GenericRenderer( final FragmentWriter fragmentWriter, final String defaultWhitespace ) {
    this( fragmentWriter, false, defaultWhitespace ) ;
  }

  public GenericRenderer( final FragmentWriter fragmentWriter, final boolean renderLocation ) {
    this( fragmentWriter, renderLocation, DEFAULT_WHITESPACE ) ;
  }

  @Override
  final public void render(
      final Renderable rendered,
      final OutputStream outputStream,
      final Page page,
      final File contentDirectory
  ) throws Exception {
    if( rendered.hasProblem() ) {
      renderProblems( rendered.getProblems(), outputStream ) ;
    } else {
      fragmentWriter.startWriting(
          outputStream,
          MetadataHelper.createMetadata( rendered.getRenderingCharset(), page, contentDirectory )
      ) ;
      final SyntacticTree root = MetadataHelper
          .createMetadataDecoration( rendered.getDocumentTree(), page ) ;
      renderTreeInternal( root, null, null ) ;
      fragmentWriter.finishWriting() ;
    }
  }

  @Override
  public ImmutableMap< PageIdentifier, String > extractPages(
      final SyntacticTree documentTree
  ) throws Exception {
    if( fragmentWriter instanceof PagesExtractor ) {
      return ( ( PagesExtractor ) fragmentWriter ).extractPages( documentTree ) ;
    } else {
      return EMPTY_MAP ;
    }
  }

  @Override
  public RenditionMimeType getMimeType() {
    return fragmentWriter.getMimeType() ;
  }

  /**
   * TODO define a clearer contract forbidding nulls.
   * By now some parameters are null when rendering a multipage embedded stylesheet.
   * This works but gets things messy.
   * Maybe another method with no parameter could be OK.
   */
  public void renderTree(
      final SyntacticTree tree,
      final OutputStream outputStream,
      final Charset renderingCharset,
      final Page page,
      final File contentDirectoryForResources
  ) throws Exception {
    fragmentWriter.startWriting(
        outputStream,
        MetadataHelper.createMetadata( renderingCharset, page, contentDirectoryForResources )
    ) ;
    renderTreeInternal( MetadataHelper.createMetadataDecoration( tree, page ), null, null ) ;
    fragmentWriter.finishWriting() ;
  }

  private void renderTreeInternal(
      final SyntacticTree tree,
      final Nodepath kinship,
      final NodeKind previous
  ) throws Exception {

    final NodeKind nodeKind = NodeKindTools.ofRoot( tree ) ;
    final Nodepath newPath = ( createNodepath( kinship, nodeKind ) ) ;
    boolean isRootElement = false ;

    switch( nodeKind ) {

      case WORD_:
        final SyntacticTree wordTree = tree.getChildAt( 0 ) ;
        fragmentWriter.write( newPath, wordTree.getText() ) ;
        // Handle superscript
        if( tree.getChildCount() > 1 ) {
          for( int childIndex = 1 ; childIndex < tree.getChildCount() ; childIndex++ ) {
            final SyntacticTree child = tree.getChildAt( childIndex ) ;
            renderTreeInternal( child, newPath, WORD_ ) ;
          }
        }
        break ;

      case WORD_AFTER_CIRCUMFLEX_ACCENT:
        final SyntacticTree superscriptTree = tree.getChildAt( 0 ) ;
        fragmentWriter.start( newPath, isRootElement ) ;
        fragmentWriter.write( newPath, superscriptTree.getText() ) ;
        fragmentWriter.end( newPath ) ;
        break ;


      case BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS :
      case BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS :
        writeLiteral(
            fragmentWriter,
            newPath,
            Spaces.normalizeLiteral( tree.getChildAt( 0 ).getText() )
        ) ;
        break ;

      case ABSOLUTE_IDENTIFIER :
      case _EXPLICIT_IDENTIFIER :
      case _COLLIDING_EXPLICIT_IDENTIFIER :
      case _IMPLICIT_IDENTIFIER :
        fragmentWriter.start( newPath, isRootElement ) ;
        final StringBuilder builder = new StringBuilder() ;
        for( final SyntacticTree child : tree.getChildren() ) {
          builder.append( child.getText() ) ;
        }
        fragmentWriter.write( newPath, builder.toString() ) ;
        fragmentWriter.end( newPath ) ;
        break ;

      case URL_LITERAL :
      case TAG :
      case _IMPLICIT_TAG :
      case _EXPLICIT_TAG :
      case _PROMOTED_TAG :
      case _WORD_COUNT :
      case _PAGE_IDENTIFIER :
      case _PAGE_PATH :
      case _STYLE :
      case RAW_LINES :
        final SyntacticTree literalTree = tree.getChildAt( 0 ) ;
        writeLiteral( fragmentWriter, newPath, literalTree.getText() ) ;
        break ;

      case RESOURCE_LOCATION:
      case _IMAGE_WIDTH:
      case _IMAGE_HEIGHT:
      case APOSTROPHE_WORDMATE :
      case SIGN_COLON :
      case SIGN_COMMA :
      case SIGN_ELLIPSIS :
      case SIGN_EXCLAMATIONMARK :
      case SIGN_FULLSTOP :
      case SIGN_QUESTIONMARK :
      case SIGN_SEMICOLON :
        fragmentWriter.start( newPath, false ) ;
        fragmentWriter.write( newPath, tree.getChildAt( 0 ).getText() ) ;
        fragmentWriter.end( newPath ) ;
        break ;

      case LEVEL_INTRODUCER_INDENT_ :
        break ;

      // Is this still useful? EmbeddedListMangler should have discarded parsed tokens.
      case PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ :
      case PARAGRAPH_AS_LIST_ITEM_WITH_DOUBLE_HYPHEN_AND_NUMBER_SIGN:
        processByDefault( tree, createNodepath( kinship, _PARAGRAPH_AS_LIST_ITEM ), false ) ;
        break ;


      // Is this still useful? EmbeddedListMangler should have discarded parsed tokens.
      case EMBEDDED_LIST_ITEM_WITH_HYPHEN_ :
      case EMBEDDED_LIST_ITEM_NUMBERED_ :
        processByDefault( tree, createNodepath( kinship, _EMBEDDED_LIST_ITEM ), false ) ;
        break ;

      case LEVEL_TITLE:
        processByDefault( tree, createNodepath( kinship, LEVEL_TITLE ), false ) ;
        break ;

      case OPUS:
      case NOVELLA:
        isRootElement = true ;

      default :
        processByDefault( tree, newPath, isRootElement );
        break ;

    }

  }

  private static void writeLiteral(
      final FragmentWriter fragmentWriter,
      final Nodepath newPath,
      final String literal
  ) throws Exception {
    fragmentWriter.start( newPath, false ) ;
    fragmentWriter.writeLiteral( newPath, literal ) ;
    fragmentWriter.end( newPath ) ;
  }

  private Nodepath createNodepath( final Nodepath kinship, final NodeKind kind ) {
    return null == kinship ? new Nodepath( kind ) : new Nodepath( kinship, kind );
  }

  private void processByDefault( 
      final SyntacticTree tree, 
      final Nodepath path, 
      final boolean rootElement 
  ) throws Exception {
    NodeKind previous;
    fragmentWriter.start( path, rootElement ) ;
    maybeWriteLocation( tree, path ) ;
    previous = null ;
    for( final SyntacticTree subtree : tree.getChildren() ) {
      final NodeKind subtreeNodeKind = NodeKindTools.ofRoot( subtree );
      maybeWriteWhitespace( path, previous, subtreeNodeKind ) ;
      renderTreeInternal( subtree, path, previous ) ;
      previous = subtreeNodeKind;
    }
    fragmentWriter.end( path ) ;
  }

  private void maybeWriteWhitespace(
      final Nodepath path,
      final NodeKind previous,
      final NodeKind nodeKind
  ) throws Exception {
    if( ! hasBlockAfterTilde( path ) && Spaces.isTrigger( previous, nodeKind ) ) {
      fragmentWriter.write( path, whitespace ) ;
    }
  }
  
  private static final Set< TagBehavior > LOCATION_ENABLED_TAG_BEHAVIORS =
      ImmutableSet.of( TagBehavior.SCOPE, TagBehavior.TRAVERSABLE ) ;

  private void maybeWriteLocation( final SyntacticTree tree, final Nodepath path )
      throws Exception
  {
    final Location location = tree.getLocation() ;
    final NodeKind nodeKind = path.getCurrent();
    if( renderLocation &&
        location != null && 
        ( LOCATION_ENABLED_TAG_BEHAVIORS.contains( nodeKind.getTagBehavior() ) ||
            NodeKind.PARAGRAPH_REGULAR == tree.getNodeKind() ||
            NodeKind.LINES_OF_LITERAL == tree.getNodeKind() ||
            NodeKind.CELL_ROWS_WITH_VERTICAL_LINE == tree.getNodeKind()
        )
    ) {
      final Nodepath locationNodepath = new Nodepath( path, NodeKind._LOCATION ) ;

      fragmentWriter.start( locationNodepath, false ) ;

        fragmentWriter.write( locationNodepath, location.toHumanReadableForm() ) ;

      fragmentWriter.end( locationNodepath ) ;
    }
  }

  private static boolean hasBlockAfterTilde( final Nodepath path ) {
    if( path == null ) {
      return false ;
    }
    if( path.getCurrent() == BLOCK_AFTER_TILDE ) {
      return true ;
    }
    return hasBlockAfterTilde( path.getAncestor() ) ;

  }

  protected static void renderProblems(
      final Iterable< Problem > problems,
      final OutputStream outputStream
  ) {
    final PrintWriter writer = new PrintWriter( outputStream ) ;
    for( final Problem problem : problems ) {
      writer.println( problem.getLocation() ) ;
      writer.println( "    " + problem.getMessage() ) ;
    }
    writer.flush() ;
  }


}

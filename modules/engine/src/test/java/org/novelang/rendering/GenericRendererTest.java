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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.novelang.common.Location;
import org.novelang.common.Nodepath;
import org.novelang.common.Problem;
import org.novelang.common.Renderable;
import org.novelang.common.StylesheetMap;
import org.novelang.common.SyntacticTree;
import org.novelang.common.metadata.DocumentMetadata;
import org.novelang.common.metadata.MetadataHelper;
import org.novelang.outfit.DefaultCharset;
import org.novelang.parser.NodeKind;

import static org.junit.Assert.assertEquals;
import static org.novelang.parser.NodeKind.*;
import static org.novelang.parser.antlr.TreeFixture.tree;

/**
 * @author Laurent Caillette
 */
public class GenericRendererTest {

  @Test
  public void whitespace1() throws Exception {
    final SyntacticTree tree = tree(
        BLOCK_INSIDE_PARENTHESIS,
        tree( WORD_, "first" ),
        tree( WORD_, "second")
    ) ;
    final GenericRenderer renderer = new GenericRenderer( new SimpleFragmentWriter(), "^" ) ;
    renderer.render( createRenderable( tree ), outputStream ) ;
    assertEquals( "BLOCK_INSIDE_PARENTHESIS(first^second)", getRenderedText() ) ;
  }

  @Test
  public void superscript() throws Exception {
    final SyntacticTree tree = tree(
        BLOCK_INSIDE_PARENTHESIS,
        tree( WORD_, tree( "super"), tree( WORD_AFTER_CIRCUMFLEX_ACCENT, "script" ) )
    ) ;
    final GenericRenderer renderer = new GenericRenderer( new SimpleFragmentWriter(), "^" ) ;
    renderer.render( createRenderable( tree ), outputStream ) ;
    assertEquals( "BLOCK_INSIDE_PARENTHESIS(superWORD_AFTER_CIRCUMFLEX_ACCENT(script))", getRenderedText() ) ;
  }

  @Test
  public void noSpaceInsideBlockAfterTilde1() throws Exception {
    final SyntacticTree tree = tree(
        BLOCK_AFTER_TILDE,
        tree(
            SUBBLOCK,
            tree( WORD_, "x" ),
            tree( BLOCK_INSIDE_PARENTHESIS, tree( WORD_, "y" ) ),
            tree( WORD_, "z" )
        )
    ) ;
    final GenericRenderer renderer = new GenericRenderer( new SimpleFragmentWriter(), "^" ) ;
    renderer.render( createRenderable( tree ), outputStream ) ;
    assertEquals(
        "BLOCK_AFTER_TILDE(SUBBLOCK(xBLOCK_INSIDE_PARENTHESIS(y)z))",
        getRenderedText()
    ) ;
  }

  @Test
  public void noSpaceInsideBlockAfterTilde2() throws Exception {
    final SyntacticTree tree = tree(
        PARAGRAPH_REGULAR,
        tree(
            BLOCK_AFTER_TILDE,
            tree(
                SUBBLOCK,
                tree( WORD_, "x" ),
                tree( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS, "/" ),
                tree( WORD_, "y" )
            )
        ),
        tree( WORD_, "z" )
    ) ;
    final GenericRenderer renderer = new GenericRenderer( new SimpleFragmentWriter(), "^" ) ;
    renderer.render( createRenderable( tree ), outputStream ) ;
    assertEquals(
        "PARAGRAPH_REGULAR(BLOCK_AFTER_TILDE(SUBBLOCK(xBLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS(/)y))^z)",
        getRenderedText()
    ) ;
  }

  @Test
  public void spaceInsideTwoBlocksInsideGraveAccent() throws Exception {
    final SyntacticTree tree = tree(
        PARAGRAPH_REGULAR,
        tree( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS, "y" ),
        tree( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS, "z" )
    ) ;
    final GenericRenderer renderer = new GenericRenderer( new SimpleFragmentWriter(), "^" ) ;
    renderer.render( createRenderable( tree ), outputStream ) ;
    assertEquals(
        "PARAGRAPH_REGULAR(BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS(y)^BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS(z))",
        getRenderedText()
    ) ;
  }

  @Test
  public void whitespace2() throws Exception {
    final SyntacticTree tree = tree(
        NodeKind.PARAGRAPH_REGULAR,
        tree( WORD_, "w0" ),
        tree(
            BLOCK_INSIDE_PARENTHESIS,
            tree( WORD_, "w1" ),
            tree( WORD_, "w2"),
            tree( PUNCTUATION_SIGN, tree( SIGN_FULLSTOP, "." ) )
        ),
        tree( WORD_, "w3" )
    ) ;
    final GenericRenderer renderer = new GenericRenderer( new SimpleFragmentWriter(), "^" ) ;
    renderer.render( createRenderable( tree ), outputStream ) ;
    assertEquals(
        "PARAGRAPH_REGULAR(w0^BLOCK_INSIDE_PARENTHESIS(w1^w2PUNCTUATION_SIGN(SIGN_FULLSTOP(.)))^w3)",
        getRenderedText()
    ) ;
  }


  @Test
  public void identifiers() throws Exception {
    final SyntacticTree tree = tree(
        NodeKind._LEVEL,
        tree( _IMPLICIT_IDENTIFIER, "Implicit" ),
        tree( _EXPLICIT_IDENTIFIER, "Explicit" )
    ) ;
    final GenericRenderer renderer = new GenericRenderer( new SimpleFragmentWriter(), "^" ) ;
    renderer.render( createRenderable( tree ), outputStream ) ;
    assertEquals(
        "_LEVEL(_IMPLICIT_IDENTIFIER(Implicit)_EXPLICIT_IDENTIFIER(Explicit))",
        getRenderedText()
    ) ;
  }


  @Test
  public void promotedTag() throws Exception {
    final SyntacticTree tree = tree(
        NodeKind._LEVEL,
        tree( _PROMOTED_TAG, "Promoted" )
    ) ;
    final GenericRenderer renderer = new GenericRenderer( new SimpleFragmentWriter(), "^" ) ;
    renderer.render( createRenderable( tree ), outputStream ) ;
    assertEquals(
        "_LEVEL(_PROMOTED_TAG(Promoted))",
        getRenderedText()
    ) ;
  }

  @Test
  public void locationForLevel() throws Exception {
    final SyntacticTree tree = tree(
        NodeKind._LEVEL,
        new Location( "Here" ),
        tree( _IMPLICIT_IDENTIFIER, "Implicit" ),
        tree( _EXPLICIT_IDENTIFIER, "Explicit" )
    ) ;
    final GenericRenderer renderer = new GenericRenderer( new SimpleFragmentWriter(), true, "^" ) ;
    renderer.render( createRenderable( tree ), outputStream ) ;
    assertEquals(
        "_LEVEL(_LOCATION((?) Here)_IMPLICIT_IDENTIFIER(Implicit)_EXPLICIT_IDENTIFIER(Explicit))",
        getRenderedText()
    ) ;
  }

  @Test
  public void locationForParagraph() throws Exception {
    final SyntacticTree tree = tree(
        NodeKind.PARAGRAPH_REGULAR,
        new Location( "Here" )
    ) ;
    final GenericRenderer renderer = new GenericRenderer( new SimpleFragmentWriter(), true, "^" ) ;
    renderer.render( createRenderable( tree ), outputStream ) ;
    assertEquals(
        "PARAGRAPH_REGULAR(_LOCATION((?) Here))",
        getRenderedText()
    ) ;
  }

// =======
// Fixture
// =======

  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

  private String getRenderedText() {
    try {
      return new String( outputStream.toByteArray(), DefaultCharset.RENDERING.name() ) ;
    } catch( UnsupportedEncodingException e ) {
      throw new RuntimeException( e ) ;
    }
  }

  private static Renderable createRenderable( final SyntacticTree tree ) {
    final DocumentMetadata documentMetadata = MetadataHelper.createMetadata( DefaultCharset.RENDERING ) ;
    return new Renderable() {
      @Override
      public Iterable< Problem > getProblems() {
        return ImmutableList.of() ;
      }
      @Override
      public Charset getRenderingCharset() {
        return DefaultCharset.RENDERING ;
      }
      @Override
      public boolean hasProblem() {
        return false;
      }
      @Override
      public SyntacticTree getDocumentTree() {
        return tree ;
      }
      public DocumentMetadata getTreeMetadata() {
        return documentMetadata;
      }

      @Override
      public StylesheetMap getCustomStylesheetMap() {
        return StylesheetMap.EMPTY_MAP ;
      }
    } ;
  }

  private static class SimpleFragmentWriter implements FragmentWriter {

    private PrintWriter writer ;

    @Override
    public void startWriting(
        final OutputStream outputStream,
        final DocumentMetadata documentMetadata
    ) throws Exception {
      writer = new PrintWriter( outputStream ) ;
    }

    @Override
    public void finishWriting() throws Exception {
      writer.flush() ;
    }

    @Override
    public void start( final Nodepath kinship, final boolean wholeDocument ) throws Exception {
      writer.append( kinship.getCurrent().name() ).append( "(" ) ;
    }

    @Override
    public void end( final Nodepath kinship ) throws Exception {
      writer.append( ")" ) ;
    }

    @Override
    public void write( final Nodepath kinship, final String word ) throws Exception {
      writer.append( word ) ;
    }

    @Override
    public void writeLiteral( final Nodepath kinship, final String word ) throws Exception {
      write( kinship, word ) ;
    }

    @Override
    public RenditionMimeType getMimeType() {
      return null ;
    }
  }

}

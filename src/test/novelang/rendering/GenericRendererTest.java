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

package novelang.rendering;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import com.google.common.collect.ImmutableList;
import novelang.common.metadata.MetadataHelper;
import static novelang.common.NodeKind.*;
import novelang.common.Nodepath;
import novelang.common.Problem;
import novelang.common.metadata.TreeMetadata;
import novelang.common.SyntacticTree;
import novelang.common.Renderable;
import novelang.common.StylesheetMap;
import novelang.parser.Encoding;
import static novelang.parser.antlr.TreeFixture.tree;

/**
 * @author Laurent Caillette
 */
public class GenericRendererTest {

  @Test
  public void whitespace1() throws Exception {
    final SyntacticTree tree = tree( PARENTHESIS, tree( WORD, "first" ), tree( WORD, "second") ) ;
    final GenericRenderer renderer = new GenericRenderer( new SimpleFragmentWriter(), "^" ) ;
    renderer.render( createRenderable( tree ), outputStream ) ;
    assertEquals( "PARENTHESIS(first^second)", getRenderedText() ) ;
  }

  @Test
  public void whitespace2() throws Exception {
    final SyntacticTree tree = tree(
        PARAGRAPH_PLAIN,
        tree( WORD, "w0" ),
        tree(
            PARENTHESIS,
            tree( WORD, "w1" ),
            tree( WORD, "w2"),
            tree( PUNCTUATION_SIGN, tree( SIGN_FULLSTOP ) )
        ),
        tree( WORD, "w3" )
    ) ;
    final GenericRenderer renderer = new GenericRenderer( new SimpleFragmentWriter(), "^" ) ;
    renderer.render( createRenderable( tree ), outputStream ) ;
    assertEquals(
        "PARAGRAPH_PLAIN(w0^PARENTHESIS(w1^w2PUNCTUATION_SIGN(SIGN_FULLSTOP()))^w3)",
        getRenderedText()
    ) ;
  }


// =======
// Fixture
// =======

  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

  private String getRenderedText() {
    try {
      return new String( outputStream.toByteArray(), Encoding.DEFAULT.name() ) ;
    } catch( UnsupportedEncodingException e ) {
      throw new RuntimeException( e ) ;
    }
  }

  private static Renderable createRenderable( final SyntacticTree tree ) {
    final TreeMetadata treeMetadata = MetadataHelper.createMetadata( tree, Encoding.DEFAULT ) ;
    return new Renderable() {
      public Iterable< Problem > getProblems() {
        return ImmutableList.of() ;
      }
      public Charset getEncoding() {
        return Encoding.DEFAULT ;
      }
      public boolean hasProblem() {
        return false;
      }
      public SyntacticTree getDocumentTree() {
        return tree ;
      }
      public TreeMetadata getTreeMetadata() {
        return treeMetadata ;
      }

      public StylesheetMap getCustomStylesheetMap() {
        return StylesheetMap.EMPTY_MAP ;
      }
    } ;
  }

  private static class SimpleFragmentWriter implements FragmentWriter {

    private PrintWriter writer ;

    public void startWriting(
        OutputStream outputStream,
        TreeMetadata treeMetadata,
        Charset encoding
    ) throws Exception {
      writer = new PrintWriter( outputStream ) ;
    }

    public void finishWriting() throws Exception {
      writer.flush() ;
    }

    public void start( Nodepath kinship, boolean wholeDocument ) throws Exception {
      writer.append( kinship.getCurrent().name() ).append( "(" ) ;
    }

    public void end( Nodepath kinship ) throws Exception {
      writer.append( ")" ) ;
    }

    public void write( Nodepath kinship, String word ) throws Exception {
      writer.append( word ) ;
    }

    public void writeLitteral( Nodepath kinship, String word ) throws Exception {
      write( kinship, word ) ;
    }

    public RenditionMimeType getMimeType() {
      return null ;
    }
  }

}

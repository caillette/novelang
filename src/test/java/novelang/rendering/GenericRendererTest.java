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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import org.junit.Test;
import org.junit.Assert;
import static novelang.model.common.NodeKind.*;
import novelang.model.common.Tree;
import novelang.model.common.NodePath;
import novelang.model.common.Problem;
import novelang.model.common.TreeMetadata;
import novelang.model.implementation.Book;
import novelang.model.implementation.MetadataHelper;
import novelang.model.renderable.Renderable;
import static novelang.parser.antlr.TreeHelper.tree;
import com.google.common.collect.Lists;

/**
 * @author Laurent Caillette
 */
public class GenericRendererTest {

  @Test
  public void whitespace1() throws Exception {
    final Tree tree = tree( PARENTHESIS, tree( WORD, "first" ), tree( WORD, "second") ) ;
    final GenericRenderer renderer = new GenericRenderer( new SimpleFragmentWriter(), "¨" ) ;
    renderer.render( createRenderable( tree ), outputStream ) ;
    Assert.assertEquals( "PARENTHESIS(first¨second)", getRenderedText() ) ;
  }

  @Test
  public void whitespace2() throws Exception {
    final Tree tree = tree(
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
    final GenericRenderer renderer = new GenericRenderer( new SimpleFragmentWriter(), "¨" ) ;
    renderer.render( createRenderable( tree ), outputStream ) ;
    Assert.assertEquals(
        "PARAGRAPH_PLAIN(w0¨PARENTHESIS(w1¨w2PUNCTUATION_SIGN(SIGN_FULLSTOP()))¨w3)",
        getRenderedText()
    ) ;
  }


// =======
// Fixture
// =======

  private static final Charset ENCODING = Book.DEFAULT_ENCODING ;

  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

  private String getRenderedText() {
    try {
      return new String( outputStream.toByteArray(), ENCODING.name() ) ;
    } catch( UnsupportedEncodingException e ) {
      throw new RuntimeException( e ) ;
    }
  }

  private static Renderable createRenderable( final Tree tree ) {
    final TreeMetadata treeMetadata = MetadataHelper.createMetadata( tree ) ;
    return new Renderable() {
      public Iterable< Problem > getProblems() {
        return Lists.immutableList() ;
      }
      public Charset getEncoding() {
        return ENCODING ;
      }
      public boolean hasProblem() {
        return false;
      }
      public Tree getTree() {
        return tree ;
      }
      public TreeMetadata getTreeMetadata() {
        return treeMetadata ;
      }
    } ;
  }

  private static class SimpleFragmentWriter implements FragmentWriter {

    private PrintWriter writer ;


    public void configure( TreeMetadata treeMetadata ) { }

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

    public void start( NodePath kinship, boolean wholeDocument ) throws Exception {
      writer.append( kinship.getCurrent().name() ).append( "(" ) ;
    }

    public void end( NodePath kinship ) throws Exception {
      writer.append( ")" ) ;
    }

    public void write( NodePath kinship, String word ) throws Exception {
      writer.append( word ) ;
    }

    public RenditionMimeType getMimeType() {
      return null ;
    }
  }

}

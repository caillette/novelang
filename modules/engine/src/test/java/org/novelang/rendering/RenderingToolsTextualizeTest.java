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

import org.junit.Assert;
import org.junit.Test;
import static org.novelang.parser.NodeKind.*;
import static org.novelang.parser.antlr.TreeFixture.tree;

import org.novelang.common.SyntacticTree;
import org.novelang.outfit.DefaultCharset;

/**
 * Tests for 
 * {@link RenderingTools#textualize(org.novelang.common.SyntacticTree, java.nio.charset.Charset)}.
 *
 * @author Laurent Caillette
 */
public class RenderingToolsTextualizeTest {

  @Test
  public void renderJustAWord() throws Exception {
    final SyntacticTree tree = tree(
        LEVEL_TITLE,
        tree( WORD_, "word" )
    ) ;
    verify( "word", tree );
  }

  @Test
  public void renderWordsAndPunctuationSigns() throws Exception {
    final SyntacticTree tree = tree(
        LEVEL_TITLE,
        tree( WORD_, "y" ),
        tree( PUNCTUATION_SIGN, tree( SIGN_COMMA, tree( "," ) ) ),
        tree( WORD_, "z" ),
        tree( PUNCTUATION_SIGN, tree( SIGN_FULLSTOP, tree( "." ) ) )

    ) ;
    verify( "y, z.", tree );
  }

  @Test
  public void renderWordThenBlockInsideGraveAccents() throws Exception {
    final SyntacticTree tree = tree(
        LEVEL_TITLE,
        tree( WORD_, "z" ),
        tree( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS, tree( "0.1.2" ) )

    ) ;
    verify( "z 0.1.2", tree );
  }


// =======
// Fixture
// =======

  private static void verify( final String expected, final SyntacticTree tree ) throws Exception {
    final String rendered = RenderingTools.textualize(
        tree
        ,
        DefaultCharset.RENDERING
    ) ;
    Assert.assertEquals( expected, rendered ) ;
  }
}
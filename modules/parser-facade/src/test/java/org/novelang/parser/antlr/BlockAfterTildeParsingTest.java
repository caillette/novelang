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
package org.novelang.parser.antlr;

import org.junit.Test;

import static org.novelang.parser.NodeKind.*;
import static org.novelang.parser.antlr.TreeFixture.tree;

/**
 * @author Laurent Caillette
 */
public class BlockAfterTildeParsingTest {


  @Test
  public void simplestBlock() {
    PARSERMETHOD_BLOCK_AFTER_TILDE.checkTree(
        "~word",
        tree(
            BLOCK_AFTER_TILDE,
            tree( SUBBLOCK, tree( WORD_, "word" ) )
        )
    ) ;
  }

  @Test
  public void compositeBlock1() {
    PARSERMETHOD_BLOCK_AFTER_TILDE.checkTree(
        "~word!",
        tree(
            BLOCK_AFTER_TILDE,
            tree(
                SUBBLOCK,
                tree( WORD_, "word" ),
                tree( PUNCTUATION_SIGN, tree( SIGN_EXCLAMATIONMARK, "!" ) )
            )
        )
    ) ;
  }

  @Test
  public void compositeBlockWithParenthesis() {
    PARSERMETHOD_BLOCK_AFTER_TILDE.checkTree(
        "~w(x)!?",
        tree(
            BLOCK_AFTER_TILDE,
            tree(
                SUBBLOCK,
                tree( WORD_, "w" ),
                tree( BLOCK_INSIDE_PARENTHESIS, tree( WORD_, "x" ) ),
                tree( PUNCTUATION_SIGN, tree( SIGN_EXCLAMATIONMARK, "!" ) ),
                tree( PUNCTUATION_SIGN, tree( SIGN_QUESTIONMARK, "?" ) )
            )
        )
    ) ;
  }

  @Test
  public void compositeBlockWithAsterisksPair() {
    PARSERMETHOD_BLOCK_AFTER_TILDE.checkTree(
        "~w**x**",
        tree(
            BLOCK_AFTER_TILDE,
            tree(
                SUBBLOCK,
                tree( WORD_, "w" ),
                tree( BLOCK_INSIDE_ASTERISK_PAIRS, tree( WORD_, "x" ) )
            )
        )
    ) ;
  }

  @Test
  public void compositeBlockWithBlockInsideGraveAccents() {
    PARSERMETHOD_BLOCK_AFTER_TILDE.checkTree(
        "~w`/`x",
        tree(
            BLOCK_AFTER_TILDE,
            tree(
                SUBBLOCK,
                tree( WORD_, "w" ),
                tree( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS, "/" ),
                tree( WORD_, "x" )
            )
        )
    ) ;
  }

  @Test
  public void multipleSubblocks() {
    PARSERMETHOD_BLOCK_AFTER_TILDE.checkTree(
        "~w~x~y(z)",
        tree(
            BLOCK_AFTER_TILDE,
            tree(
                SUBBLOCK,
                tree( WORD_, "w" )
            ),
            tree(
                SUBBLOCK,
                tree( WORD_, "x" )
            ),
            tree(
                SUBBLOCK,
                tree( WORD_, "y" ),
                tree( BLOCK_INSIDE_PARENTHESIS, tree( WORD_, "z" ) )
            )
        )
    ) ;
  }

  @Test 
  public void simplestSubblock() {
    PARSERMETHOD_BLOCK_AFTER_TILDE.checkTree(
        "~word",
        tree(
            BLOCK_AFTER_TILDE,
            tree( SUBBLOCK, tree( WORD_, "word" ) ) 
        )
    ) ;
  }



// =======
// Fixture
// =======

  private static final ParserMethod PARSERMETHOD_BLOCK_AFTER_TILDE =
      new ParserMethod( "blockAfterTilde" ) ;

}

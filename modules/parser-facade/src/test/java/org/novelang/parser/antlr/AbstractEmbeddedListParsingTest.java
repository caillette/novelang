/*
 * Copyright (C) 2009 Laurent Caillette
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

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.novelang.parser.NodeKind;

import static org.novelang.parser.NodeKind.*;
import static org.novelang.parser.antlr.AntlrTestHelper.BREAK;
import static org.novelang.parser.antlr.TreeFixture.tree;

/**
 * Tests for embedded list parsing.
 *
 * @author Laurent Caillette
 */
public abstract class AbstractEmbeddedListParsingTest {
  
  @Test
  public void embeddedListItemMinimum() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTree(
        getMarker() + " w",
        tree(
            PARAGRAPH_REGULAR,
            tree(
                getNodeKind(),
                tree( WHITESPACE_, " " ),
                tree( WORD_, "w" )
            )
        )
    ) ;
  }

  @Test
  public void paragraphIsTwoSmallListItems()
      throws RecognitionException
  {
    getParserListMethod().createTree(
        getMarker() + " x" + BREAK +
        getMarker() + " y"
    ) ;
  }

  /**
   * Was a bug.
   */
  @Test
  public void embeddedListItemApostropheAndDot() throws RecognitionException {
    PARSERMETHOD_PART.checkTree(
        getMarker() + " y'z.",
        tree(
            NOVELLA,
            tree( PARAGRAPH_REGULAR,
                tree(
                    getNodeKind(),
                    tree( WHITESPACE_, " " ),
                    tree( WORD_, "y" ),
                    tree( APOSTROPHE_WORDMATE, "'" ),
                    tree( WORD_, "z" ),
                    tree( PUNCTUATION_SIGN, tree( SIGN_FULLSTOP, "." ) )
                )
            )
        )
    ) ;
  }

  @Test 
  public void embeddedListItemInsideParenthesis() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTree(
        "(" + BREAK +
        getMarker() + " w" + BREAK +
        getMarker() + " x" + BREAK +
        ")"
        ,
        tree(
            PARAGRAPH_REGULAR,
            tree(
                BLOCK_INSIDE_PARENTHESIS,
                tree( LINE_BREAK_ ),
                tree(
                    getNodeKind(),
                    tree( WHITESPACE_, " " ),
                    tree( WORD_, "w" )
                    ),
                tree( LINE_BREAK_ ),
                tree(
                    getNodeKind(),
                    tree( WHITESPACE_, " " ),
                    tree( WORD_, "x" )
                ),
                tree( LINE_BREAK_ )
            )
        )
    ) ;
  }

  @Test
  public void severalEmbeddedListItems() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTree(
        getMarker() + " w1" + BREAK +
        "  " + getMarker() + " w2" + BREAK,
        tree(
            PARAGRAPH_REGULAR,
            tree(
                getNodeKind(),
                tree( WHITESPACE_, " " ),
                tree( WORD_, "w1" )
            ),
            tree( LINE_BREAK_ ),
            tree( WHITESPACE_, "  " ),
            tree(
                getNodeKind(),
                tree( WHITESPACE_, " " ),
                tree( WORD_, "w2" )
            )
        )
    ) ;
  }


  @Test
  public void smallDashedListItemIsSingleWord() throws RecognitionException {
    getParserListMethod().createTree( getMarker() + " x" ) ;
  }

  @Test
  public void smallDashedListItemIsSeveralWords() throws RecognitionException {
    getParserListMethod().checkTree(
        getMarker() + " x y z",
        tree(
            getNodeKind(),
            tree( WHITESPACE_, " " ),
            tree( WORD_, "x" ),
            tree( WHITESPACE_, " " ),
            tree( WORD_, "y" ),
            tree( WHITESPACE_, " " ),
            tree( WORD_, "z" )
        )

    ) ;
  }

  @Test
  public void smallDashedListItemHasParenthesisAndDoubleQuotes() throws RecognitionException {
    getParserListMethod().createTree( getMarker() + " x (\"y \") z" ) ;
  }

  @Test
  public void smallDashedListItemHasBlockAfterTilde() throws RecognitionException {
    getParserListMethod().createTree( getMarker() + " ~x" ) ;
  }

// =======
// Fixture
// =======

  private static final ParserMethod PARSERMETHOD_PARAGRAPH =
      new ParserMethod( "paragraph" ) ;
  private static final ParserMethod PARSERMETHOD_PART =
      new ParserMethod( "novella" ) ;

  protected abstract char getMarker() ;
  protected abstract NodeKind getNodeKind() ;
  protected abstract ParserMethod getParserListMethod() ;

}

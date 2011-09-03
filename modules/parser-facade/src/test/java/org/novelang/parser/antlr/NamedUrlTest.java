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
package org.novelang.parser.antlr;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import static org.novelang.parser.NodeKind.*;
import static org.novelang.parser.antlr.AntlrTestHelper.BREAK;
import static org.novelang.parser.antlr.TreeFixture.tree;

/**
 * Tests various features that aim to attach a block inside double quotes to a URL as its name.
 * 
 * @author Laurent Caillette
 */
public class NamedUrlTest {


  @Test
  public void namedUrlWithIndentOutsideParagraph() throws RecognitionException {
    PARSERMETHOD_NOVELLA.checkTree(
        "  \"name\"" + BREAK +
        "http://foo.com"
        ,
        tree(
            NOVELLA,
            tree( WHITESPACE_, "  " ),
            tree( 
                PARAGRAPH_REGULAR,
                tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
                tree( LINE_BREAK_ ),
                tree( URL_LITERAL, "http://foo.com" )
            
            )
        )
    ) ;
  }

  @Test
  public void indentInsideParagraph() throws RecognitionException {
    PARSERMETHOD_NOVELLA.checkTree(
        "nothing" + BREAK +
        "  \"name\"" + BREAK +
        "http://foo.com"
        ,
        tree(
            NOVELLA,
            tree(
                PARAGRAPH_REGULAR,
                tree( WORD_, "nothing" ),
                tree( LINE_BREAK_ ),
                tree( WHITESPACE_, "  " ),
                tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
                tree( LINE_BREAK_ ),
                tree( URL_LITERAL, "http://foo.com" )
            
            )
        )
    ) ;
  }


  @Test
  public void namedUrlInsideSquareBrackets() throws RecognitionException {
    PARSERMETHOD_NOVELLA.checkTree(
        "[\"name\"" + BREAK +
        "http://foo.com" + BREAK +
        "]"
        ,
        tree(
            NOVELLA,
            tree(
                PARAGRAPH_REGULAR,
                tree( BLOCK_INSIDE_SQUARE_BRACKETS,
                    tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
                    tree( LINE_BREAK_ ),
                    tree( URL_LITERAL, "http://foo.com" ),
                    tree( LINE_BREAK_ )
                    )
            )
        )
    ) ;
  }


  /**
   * Get sure of what we get because {@code org.novelang.treemangling.UrlMangler} relies on this.
   */
  @Test
  public void partHasCorrectSeparatorsBetweenSectionIntroducerAndParagraph1()
      throws RecognitionException
  {
    PARSERMETHOD_NOVELLA.checkTree(
        "== t" + BREAK +
        BREAK +
        "  \"name\" " + BREAK +
        "http://foo.com",
        tree(
            NOVELLA,
            tree(
                LEVEL_INTRODUCER_,
                tree( LEVEL_INTRODUCER_INDENT_, "==" ),
                tree( LEVEL_TITLE, tree( WORD_, "t" ) )
            ),
            tree( LINE_BREAK_ ),
            tree( LINE_BREAK_ ),
            tree( WHITESPACE_, "  " ),
            tree(
                PARAGRAPH_REGULAR,
                tree(
                    BLOCK_INSIDE_DOUBLE_QUOTES,
                    tree( WORD_, "name" )
                ),
                tree( WHITESPACE_, " " ),
                tree( LINE_BREAK_ ),
                tree( URL_LITERAL, "http://foo.com")
            )
        )
    ) ;
  }

  /**
   * Get sure of what we get because {@code org.novelang.treemangling.UrlMangler} relies on this.
   */
  @Test
  public void partHasCorrectSeparatorsBetweenSectionIntroducerAndParagraph2()
      throws RecognitionException
  {
    PARSERMETHOD_NOVELLA.checkTree(
        "p" + BREAK +
        BREAK +
        "== t" + BREAK +
        BREAK +
        "  \"name\" " + BREAK +
        "http://foo.com",
        tree(
            NOVELLA,
            tree(
                PARAGRAPH_REGULAR,
                tree( WORD_, "p" )
            ),
            tree( LINE_BREAK_ ),
            tree( LINE_BREAK_ ),
            tree(
                LEVEL_INTRODUCER_,
                tree( LEVEL_INTRODUCER_INDENT_, "==" ),
                tree( LEVEL_TITLE, tree( WORD_, "t" ) )
            ),
            tree( LINE_BREAK_ ),
            tree( LINE_BREAK_ ),
            tree( WHITESPACE_, "  " ),
            tree(
                PARAGRAPH_REGULAR,
                tree(
                    BLOCK_INSIDE_DOUBLE_QUOTES,
                    tree( WORD_, "name" )
                ),
                tree( WHITESPACE_, " " ),
                tree( LINE_BREAK_ ),
                tree( URL_LITERAL, "http://foo.com")
            )
        )
    ) ;
  }

// =======
// Fixture
// =======

  private final ParserMethod PARSERMETHOD_NOVELLA =
      new ParserMethod( "novella" ) ;



}
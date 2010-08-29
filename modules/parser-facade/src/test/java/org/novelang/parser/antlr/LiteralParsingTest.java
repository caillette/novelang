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
import org.junit.Ignore;
import org.antlr.runtime.RecognitionException;
import static org.novelang.parser.antlr.AntlrTestHelper.BREAK;
import static org.novelang.parser.antlr.TreeFixture.tree;
import org.novelang.parser.SourceUnescape;
import static org.novelang.parser.NodeKind.*;
import static org.novelang.parser.NodeKind.NOVELLA;

/**
 * Tests for parsing various kinds of literal.
 *
 * @author Laurent Caillette
 */
public class LiteralParsingTest {

  @Test
  public void literalWithBreaksAndOtherSeparators() throws RecognitionException {
    final String verbatim = "  Here is some " + BREAK + "//literal//. " ;
    PARSERMETHOD_LITERAL.checkTreeAfterSeparatorRemoval(
        "<<<" + BREAK +
        verbatim + BREAK +
        ">>>", tree( LINES_OF_LITERAL, verbatim )
    ) ;
  }

  @Test
  public void literalWithEscapedCharacters() throws RecognitionException {
    PARSERMETHOD_LITERAL.checkTreeAfterSeparatorRemoval(
        "<<<" + BREAK +
        "2" +
            SourceUnescape.ESCAPE_START + "greater-than-sign" + SourceUnescape.ESCAPE_END +
            "1" + BREAK +
        ">>>", tree( LINES_OF_LITERAL, "2>1" )
    ) ;
  }

  @Test
  public void softInlineLiteralNoEscape() throws RecognitionException {
    final String literal = "azer()+&%?" ;
    PARSERMETHOD_SOFT_INLINE_LITERAL.checkTreeAfterSeparatorRemoval(
        "`" + literal + "`",
        tree( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS, literal )
    ) ;
  }

  @Test
  public void softInlineLiteralWithEscape() throws RecognitionException {
    PARSERMETHOD_SOFT_INLINE_LITERAL.checkTreeAfterSeparatorRemoval(
        "`" + SourceUnescape.ESCAPE_START + "greater-than-sign" + SourceUnescape.ESCAPE_END +"`",
        tree( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS, ">" )
    ) ;
  }

  @Test
  public void hardInlineLiteralNothingSpecial() throws RecognitionException {
    final String literal = "azer()+&%?";
    PARSERMETHOD_HARD_INLINE_LITERAL.checkTreeAfterSeparatorRemoval(
        "``" + literal +"``",
        tree( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS, literal )
    ) ;
  }


  @Test
  public void someLiteral() throws RecognitionException {
    PARSERMETHOD_NOVELLA.checkTreeAfterSeparatorRemoval(
      "<<<" + BREAK +
      "  Here is some " + BREAK +
      "  //Literal// " + BREAK +
      ">>>",
      tree(
          NOVELLA,
          tree( LINES_OF_LITERAL, "  Here is some " + BREAK + "  //Literal// " )
      )
    ) ;
  }

  @Test @Ignore
  public void someLiteralContainingLineComment() throws RecognitionException {
    PARSERMETHOD_NOVELLA.checkTreeAfterSeparatorRemoval(
        "<<<" + BREAK +
        "%% Not to be commented" +
        ">>>",
        tree(
            NOVELLA,
            tree( LINES_OF_LITERAL, "%% Not to be commented" )
        )
    ) ;
  }

  @Test
  public void someLiteralContainingLowerthanSign() throws RecognitionException {
    PARSERMETHOD_NOVELLA.checkTreeAfterSeparatorRemoval(
        "<<<" + BREAK +
        "<" + BREAK +
        ">>>", tree( NOVELLA, tree( LINES_OF_LITERAL, "<" )
      )
    ) ;
  }

  @Test
  public void someLiteralContainingGreaterthanSigns() throws RecognitionException {
    final String verbatim =
        " >>>" + BREAK +
        "> " + BREAK +
        ">> " + BREAK +
        ">> >>>"
    ;

    PARSERMETHOD_NOVELLA.checkTreeAfterSeparatorRemoval(
        "<<<" + BREAK +
        verbatim + BREAK +
        ">>>", tree( NOVELLA, tree( LINES_OF_LITERAL, verbatim ) )
    ) ;
  }


  @Test @Ignore
  public void taggedLiteral() throws RecognitionException {
    PARSERMETHOD_NOVELLA.checkTreeAfterSeparatorRemoval(
        "@t" + BREAK +
        "<<<" + BREAK +
        "L" +
        ">>>",
        tree(
            NOVELLA,
            tree( TAG, "t" ),
            tree( LINES_OF_LITERAL, "L" )
        )
    ) ;
  }



// =======
// Fixture
// =======

  private static final ParserMethod PARSERMETHOD_LITERAL =
      new ParserMethod( "literal" ) ;
  private static final ParserMethod PARSERMETHOD_SOFT_INLINE_LITERAL =
      new ParserMethod( "softInlineLiteral" ) ;
  private static final ParserMethod PARSERMETHOD_HARD_INLINE_LITERAL =
      new ParserMethod( "hardInlineLiteral" ) ;
  private static final ParserMethod PARSERMETHOD_NOVELLA =
      new ParserMethod( "novella" ) ;

}

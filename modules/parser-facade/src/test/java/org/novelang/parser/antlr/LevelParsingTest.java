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
package org.novelang.parser.antlr;

import org.junit.Test;
import org.antlr.runtime.RecognitionException;
import static org.novelang.parser.antlr.TreeFixture.tree;
import static org.novelang.parser.antlr.AntlrTestHelper.BREAK;
import static org.novelang.parser.NodeKind.*;
import org.novelang.parser.NodeKind;
import org.novelang.common.SyntacticTree;

/**
 * Tests for level parsing.
 *
 * @author Laurent Caillette
 */
public class LevelParsingTest {

  @Test
  public void levelHasQuote()
      throws RecognitionException
  {
    PARSERMETHOD_LEVEL_INTRODUCER.checkTreeAfterSeparatorRemoval(
        "=== \"q\" w",
        tree(
            LEVEL_INTRODUCER_,
            tree( LEVEL_INTRODUCER_INDENT_, "===" ),
            tree(
                LEVEL_TITLE,
                tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "q" ) ),
                tree( WORD_, "w")
            )
        )
    ) ;
  }

  @Test
  public void levelIsAnonymous() throws RecognitionException {
    PARSERMETHOD_LEVEL_INTRODUCER.checkTreeAfterSeparatorRemoval(
        "===",
        tree(
            LEVEL_INTRODUCER_,
            tree( LEVEL_INTRODUCER_INDENT_, "===" )
        )
    ) ;
  }

  
  @Test
  public void levelHasTag() throws RecognitionException {
    PARSERMETHOD_LEVEL_INTRODUCER.checkTreeAfterSeparatorRemoval(
        "@tag" + BREAK +
        "===",
        tree(
            LEVEL_INTRODUCER_,
            tree( LEVEL_INTRODUCER_INDENT_, "===" ),
            tree( TAG, "tag" )
        )
    ) ;
  }

  @Test
  public void levelHasAbsoluteIdentifier() throws RecognitionException {
    PARSERMETHOD_LEVEL_INTRODUCER.checkTreeAfterSeparatorRemoval(
        "\\\\foo" + BREAK +
        "===",
        tree(
            LEVEL_INTRODUCER_,
            tree( LEVEL_INTRODUCER_INDENT_, "===" ),
            tree( ABSOLUTE_IDENTIFIER, "foo" )
        )
    ) ;
  }



  @Test
  public void levelHasOneParagraphWithEmphasisThenWordOnTwoLines() throws RecognitionException {
    PARSERMETHOD_LEVEL_INTRODUCER.createTree(
        "===" + BREAK +
        BREAK +
        "//w0//" + BREAK +
        "w1"
    );
  }

  @Test
  public void levelHasOneParagraphWithParenthesisThenWordOnTwoLines()
      throws RecognitionException
  {
    PARSERMETHOD_LEVEL_INTRODUCER.createTree(
        "===" + BREAK +
        BREAK +
        "(w0)" + BREAK +
        "w1"
    );
  }

  @Test
  public void levelHasOneParagraphWithQuoteThenWordOnTwoLines() throws RecognitionException {
    PARSERMETHOD_LEVEL_INTRODUCER.createTree(
        "===" + BREAK +
        BREAK +
        "\"w0\"" + BREAK +
        "w1"
    );
  }

  @Test
  public void levelIsAnonymousWithSublevelContainingWordsWithPunctuationSigns1()
      throws RecognitionException
  {
    PARSERMETHOD_LEVEL_INTRODUCER.createTree(
        "==" + BREAK +
        BREAK +
        "===" + BREAK +
        BREAK +
        "w0, w1."
    );
  }

  @Test
  public void levelIsAnonymousWithSublevelContainingWordsWithPunctuationSigns2()
      throws RecognitionException
  {
    PARSERMETHOD_LEVEL_INTRODUCER.createTree(
        "==" + BREAK +
        BREAK +
        "===" + BREAK +
        BREAK +
        "w0 : w1."
    );
  }

  @Test
  public void levelTitleContainsUrl()
      throws RecognitionException
  {
    PARSERMETHOD_LEVEL_INTRODUCER.createTree(
        "==" + BREAK +
        BREAK +
        "===" + BREAK +
        BREAK +
        "http://google.com"
    );
  }


  @Test
  public void justLevelIntroducerIndent() throws RecognitionException {
    PARSERMETHOD_LEVEL_INTRODUCER.checkTreeAfterSeparatorRemoval(
        "=== w",
        tree(
            NodeKind.LEVEL_INTRODUCER_,
            tree( NodeKind.LEVEL_INTRODUCER_INDENT_, "===" ),
            tree( NodeKind.LEVEL_TITLE, tree( WORD_, "w" ) )
        )
    ) ;

  }


  @Test
  public void titleIsTwoWords() throws RecognitionException {
    PARSERMETHOD_TITLE.checkTreeAfterSeparatorRemoval( "some title", tree(
        LEVEL_TITLE,
        tree( WORD_, "some" ),
        tree( WORD_, "title" )
    ) ) ;
  }

  @Test
  public void titleIsTwoWordsAndExclamationMark() throws RecognitionException {
    PARSERMETHOD_TITLE.checkTreeAfterSeparatorRemoval( "some title !", tree(
        LEVEL_TITLE,
        tree( WORD_, "some"),
        tree( WORD_, "title"),
        TREE_SIGN_EXCLAMATION_MARK
    ) ) ;
  }

  @Test
  public void titleIsWordsAndParenthesisAndExclamationMark() throws RecognitionException {
    PARSERMETHOD_TITLE.checkTreeAfterSeparatorRemoval( "some (title) !", tree(
        LEVEL_TITLE,
        tree( WORD_, "some" ),
        tree( BLOCK_INSIDE_PARENTHESIS, tree( WORD_, "title" ) ),
        TREE_SIGN_EXCLAMATION_MARK
    ) ) ;
  }


  @Test
  public void levelIsAnonymousAndHasBlockquoteWithTwoParagraphs()
      throws RecognitionException
  {
    PARSERMETHOD_NOVELLA.checkTreeAfterSeparatorRemoval(
        "===" + BREAK +
        BREAK +
        "<< w0 w1" + BREAK +
        BREAK +
        "w2" + BREAK +
        ">>",
        tree(
            NOVELLA,
            tree( LEVEL_INTRODUCER_ , tree( LEVEL_INTRODUCER_INDENT_, "===" ) ),
            tree(
                PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS,
                tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "w0" ), tree( WORD_, "w1" ) ),
                tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "w2" ) )
            )
        )
    ) ;
  }

  @Test
  public void levelIsAnonymousAndHasBlockquoteWithBreakInside() throws RecognitionException {
    PARSERMETHOD_NOVELLA.createTree(
        "===" + BREAK +
        BREAK +
        "<< w0 w1" + BREAK +
        BREAK +
        ">>"
    );
  }

  @Test
  public void titleHasDoubleQuotesThenUrl()
      throws RecognitionException
  {
    PARSERMETHOD_TITLE.createTree(
        "a" + BREAK +
        "http://bar.com"
    ) ;
  }



// =======
// Fixture
// =======

  private static final ParserMethod PARSERMETHOD_LEVEL_INTRODUCER =
      new ParserMethod( "levelIntroducer" ) ;

  private static final ParserMethod PARSERMETHOD_TITLE =
      new ParserMethod( "levelTitle" ) ;

  private static final ParserMethod PARSERMETHOD_NOVELLA =
      new ParserMethod( "novella" ) ;

  private static final SyntacticTree TREE_SIGN_EXCLAMATION_MARK =
      tree( PUNCTUATION_SIGN, tree( SIGN_EXCLAMATIONMARK, "!" ) ) ;


}

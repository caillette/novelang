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
package novelang.parser.antlr;

import org.junit.Test;
import org.antlr.runtime.RecognitionException;
import static novelang.parser.antlr.TreeFixture.tree;
import static novelang.parser.antlr.AntlrTestHelper.BREAK;
import static novelang.parser.NodeKind.*;
import novelang.parser.NodeKind;
import novelang.common.SyntacticTree;

/**
 * Tests for level parsing.
 *
 * @author Laurent Caillette
 */
public class LevelParsingTest {

  @Test
  public void sectionHasQuote()
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
  public void sectionIsAnonymous() throws RecognitionException {
    PARSERMETHOD_LEVEL_INTRODUCER.checkTreeAfterSeparatorRemoval(
        "===",
        tree(
            LEVEL_INTRODUCER_,
            tree( LEVEL_INTRODUCER_INDENT_, "===" )
        )
    ) ;
  }

  @Test
  public void sectionHasOneParagraphWithEmphasisThenWordOnTwoLines() throws RecognitionException {
    PARSERMETHOD_LEVEL_INTRODUCER.createTree(
        "===" + BREAK +
        BREAK +
        "//w0//" + BREAK +
        "w1"
    );
  }

  @Test
  public void sectionHasOneParagraphWithParenthesisThenWordOnTwoLines()
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
  public void sectionHasOneParagraphWithQuoteThenWordOnTwoLines() throws RecognitionException {
    PARSERMETHOD_LEVEL_INTRODUCER.createTree(
        "===" + BREAK +
        BREAK +
        "\"w0\"" + BREAK +
        "w1"
    );
  }

  @Test
  public void chapterIsAnonymousWithSimpleSectionContainingWordsWithPunctuationSigns1()
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
  public void chapterIsAnonymousWithSimpleSectionContainingWordsWithPunctuationSigns2()
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
  public void chapterContainsUrl()
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
  public void sectionIsAnonymousAndHasBlockquoteWithTwoParagraphs()
      throws RecognitionException
  {
    PARSERMETHOD_PART.checkTreeAfterSeparatorRemoval(
        "===" + BREAK +
        BREAK +
        "<< w0 w1" + BREAK +
        BREAK +
        "w2" + BREAK +
        ">>",
        tree(
            PART,
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
  public void sectionIsAnonymousAndHasBlockquoteWithBreakInside() throws RecognitionException {
    PARSERMETHOD_PART.createTree(
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

  private static final ParserMethod PARSERMETHOD_PART =
      new ParserMethod( "part" ) ;

  private static final SyntacticTree TREE_SIGN_EXCLAMATION_MARK =
      tree( PUNCTUATION_SIGN, tree( SIGN_EXCLAMATIONMARK, "!" ) ) ;


}

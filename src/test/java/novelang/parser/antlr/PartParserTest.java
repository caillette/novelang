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
package novelang.parser.antlr;

import java.util.Map;

import static novelang.parser.antlr.AntlrTestHelper.BREAK;
import org.antlr.runtime.RecognitionException;
import org.junit.Assert;
import org.junit.Test;
import junit.framework.AssertionFailedError;
import static novelang.model.common.NodeKind.*;
import novelang.model.common.Tree;
import static novelang.parser.antlr.TreeHelper.tree;
import novelang.parser.SymbolUnescape;

/**
 * GUnit sucks as it has completely obscure failures and stupid reports,
 * but it has some nice ideas to borrow.
 *
 * @author Laurent Caillette
 */
public class PartParserTest {

  @Test
  public void titleIsTwoWords() throws RecognitionException {
    title( "'some title", tree(
        TITLE,
        tree( WORD, "some" ),
        tree( WORD, "title" )
    ) ) ;
  }

  @Test
  public void titleIsTwoWordsAndExclamationMark() throws RecognitionException {
    title( "'some title !", tree(
        TITLE,
        tree(WORD, "some"),
        tree(WORD, "title"),
        tree( PUNCTUATION_SIGN, SIGN_EXCLAMATIONMARK )
    ) ) ;
  }

  @Test
  public void titleIsWordsAndParenthesisAndExclamationMark() throws RecognitionException {
    title( "'some (title) !", tree(
        TITLE,
        tree( WORD, "some" ),
        tree( PARENTHESIS, tree( WORD, "title" ) ),
        tree( PUNCTUATION_SIGN, SIGN_EXCLAMATIONMARK )
    ) ) ;
  }

  @Test
  public void identifierIsSingleWord() throws RecognitionException {
    identifier( "myIdentifier", tree(
        IDENTIFIER,
        tree( WORD, "myIdentifier" )
    ) ) ;
  }

  @Test
  public void wordIsSingleLetter() throws RecognitionException {
    word( "w",       tree( WORD, "w" ) ) ;
  }

  @Test
  public void wordIsTwoLetters() throws RecognitionException {
    word( "Www",     tree( WORD, "Www" ) ) ;
  }

  @Test
  public void wordIsThreeDigits() throws RecognitionException {
    word( "123",     tree( WORD, "123" ) ) ;
  }

  @Test
  public void wordIsDigitsWithHyphenMinusInTheMiddle() throws RecognitionException {
    word( "123-456", tree( WORD, "123-456" ) ) ;
  }

  @Test
  public void wordFailsWithLeadingApostrophe() throws RecognitionException {
    wordFails( "'w" ) ;
  }

  @Test
  public void wordFailsWithTrailingHyphenMinus() throws RecognitionException {
    wordFails( "'w-" ) ;
  }

  @Test
  public void wordIsEveryEscapedCharacter() throws RecognitionException {
    final Map< String,String > map = SymbolUnescape.getDefinitions() ;
    for( String key : map.keySet() ) {
      word( "&" + key + ";", tree( WORD, map.get( key ) ) ) ;
    }
  }

  @Test
  public void paragraphIsSimplestSpeech() throws RecognitionException {
    paragraph( "--- w0", tree(
        PARAGRAPH_SPEECH,
        tree( WORD, "w0" )
    ) ) ;
  }

  @Test
  public void paragraphIsSimplestSpeechEscape() throws RecognitionException {
    paragraph( "--| w0", tree(
        PARAGRAPH_SPEECH_ESCAPED,
        tree( WORD, "w0" )
    ) ) ;

  }

  @Test
  public void paragraphIsSimplestSpeechContinued() throws RecognitionException {
    paragraph( "--+ w0", tree(
        PARAGRAPH_SPEECH_CONTINUED,
        tree( WORD, "w0" )
    ) ) ;

  }

  @Test
  public void paragraphIsSpeechWithLocutor() throws RecognitionException {
    paragraph( "--- w0 w1 :: w2", tree(
        PARAGRAPH_SPEECH,
        tree( LOCUTOR, tree( WORD, "w0" ), tree( WORD, "w1" ) ),
        tree( WORD, "w2" )
    ) ) ;
  }


  // Following tests are for paragraphBody rule. But we need to rely on a rule
  // returning a sole tree as test primitives don't assert on more than one.

  @Test
  public void paragraphIsWordThenComma() throws RecognitionException {
    paragraph( "w0,", tree(
        PARAGRAPH_PLAIN,
        tree( WORD, "w0" ),
        tree( PUNCTUATION_SIGN, SIGN_COMMA )
    ) ) ;

  }

  @Test
  public void paragraphIsWwordsWithCommaInTheMiddle1() throws RecognitionException {
    paragraph( "w0,w1", tree(
        PARAGRAPH_PLAIN,
        tree( WORD, "w0" ),
        tree( PUNCTUATION_SIGN, SIGN_COMMA ),
        tree( WORD, "w1" )
    ) ); ;

  }

  @Test
  public void paragraphIsWordThenApostrophe() throws RecognitionException {
    paragraph( "w0'", tree(
        PARAGRAPH_PLAIN,
        tree( WORD, "w0" ),
        tree( APOSTROPHE_WORDMATE )
    ) ) ;

  }

  @Test
  public void paragraphIsWordsWithApostropheInTheMiddle() throws RecognitionException {
    paragraph( "w0'w1", tree(
        PARAGRAPH_PLAIN,
        tree( WORD, "w0" ),
        tree( APOSTROPHE_WORDMATE ),
        tree( WORD, "w1" )
    ) ) ;

  }

  @Test
  public void paragraphIsWordThenSemicolon() throws RecognitionException {
    paragraph( "w0;", tree(
        PARAGRAPH_PLAIN,
        tree( WORD, "w0" ),
        tree( PUNCTUATION_SIGN, SIGN_SEMICOLON )
    ) ) ;

  }

  @Test
  public void paragraphIsWordThenFullStop() throws RecognitionException {
    paragraph( "w0.", tree(
        PARAGRAPH_PLAIN,
        tree( WORD, "w0" ),
        tree( PUNCTUATION_SIGN, SIGN_FULLSTOP )
    ) ) ;

  }

  @Test
  public void paragraphIsWordThenQuestionMark() throws RecognitionException {
    paragraph( "w0?", tree(
        PARAGRAPH_PLAIN,
        tree( WORD, "w0" ),
        tree( PUNCTUATION_SIGN, SIGN_QUESTIONMARK )
    ) ) ;

  }

  @Test
  public void paragraphIsWordThenExclamationMark() throws RecognitionException {
    paragraph( "w0!", tree(
        PARAGRAPH_PLAIN,
        tree( WORD, "w0" ),
        tree( PUNCTUATION_SIGN, SIGN_EXCLAMATIONMARK )
    ) ) ;

  }

  @Test
  public void paragraphIsWordThenColon() throws RecognitionException {
    paragraph( "w0:", tree(
        PARAGRAPH_PLAIN,
        tree( WORD, "w0" ),
        tree( PUNCTUATION_SIGN, SIGN_COLON )
    ) ) ;

  }

  @Test
  public void paragraphIsWordThenEllipsis() throws RecognitionException {
    paragraph( "w0...", tree(
        PARAGRAPH_PLAIN,
        tree( WORD, "w0" ),
        tree( PUNCTUATION_SIGN, SIGN_ELLIPSIS )
    ) ) ;

  }

  @Test
  public void paragraphIsWordsWithApostropheThenEmphasis() throws RecognitionException {
    paragraph( "w0 w1'w2/w3/.", tree(
        PARAGRAPH_PLAIN,
        tree( WORD, "w0" ),
        tree( WORD, "w1" ),
        tree( APOSTROPHE_WORDMATE ),
        tree( WORD, "w2" ),
        tree( EMPHASIS, tree( WORD, "w3" ) ),
        tree( PUNCTUATION_SIGN, SIGN_FULLSTOP )
    ) ) ;

  }

  @Test
  public void paragraphIsMultilineQuoteWithPunctuationSigns1() throws RecognitionException {
    paragraphBody(
        "\"w1 w2. w3 w4." + BREAK +
        "w5 !\"" + BREAK +
        "w6 w7."
    ) ;
  }

  @Test
  public void paragraphIsMultilineQuoteWithPunctuationSigns2() throws RecognitionException {
    paragraphBody(
        "/w1./" + BREAK +
        "w2. w3."
    ) ;
  }

  @Test
  public void paragraphIsEmphasisAndQuoteWithPunctuationSigns1() throws RecognitionException {
    paragraphBody(
        "/w0./ " + BREAK +
        "  w1. w2. w3. " + BREAK +
        "  w4 : w5 w6. " + BREAK +
        "  \"w7 w8 ?\"."
    ) ;
  }


  @Test
  public void paragraphIsJustEllipsis() throws RecognitionException {
    paragraphBody(
        "..."
    ) ;
  }

  @Test
  public void paragraphIsEllipsisThenWord() throws RecognitionException {
    paragraphBody(
        "...w0"
    ) ;
  }

  @Test
  public void paragraphIsEllipsisInsideBrackets() throws RecognitionException {
    paragraphBody(
        "[...]"
    ) ;
  }

  @Test
  public void paragraphIsWordsAndPunctuationSigns1() throws RecognitionException {
    paragraphBody( "w1 w2, w3 w4." ) ;
  }

  @Test
  public void paragraphIsParenthesizedWordsWithApostropheInTheMiddle() throws RecognitionException {
    paragraph( "(w0'w1)" ) ;
  }

  @Test
  public void paragraphIsParenthesizedWordsWithCommaInTheMiddle() throws RecognitionException {
    paragraph( "(w0,w1)" ) ;
  }

  @Test
  public void paragraphIsEmphasizedWordsWithApostropheInTheMiddle() throws RecognitionException {
    paragraph( "\"w0'w1\"" ) ;
  }

  @Test
  public void paragraphIsQuotedWordsWithCommaInTheMiddle() throws RecognitionException {
    paragraph( "\"w0,w1\"" ) ;
  }

  @Test
  public void paragraphIsInterpolatedWordsWithApostropheInTheMiddle() throws RecognitionException {
    paragraph( "--w0'w1--" ) ;
  }

  @Test
  public void paragraphIsInterpolatedWordsWithCommaInTheMiddle() throws RecognitionException {
    paragraph( "--w0,w1--" ) ;
  }

  @Test
  public void sectionHasIdentifierAndOneParagraphWithTwoWordsAndAPeriod() throws RecognitionException {
    section(
        "=== s00" + BREAK +
        BREAK +
        "p10 w11.",
        tree(
            SECTION,
            tree( IDENTIFIER, tree( WORD, "s00") ),
            tree( PARAGRAPH_PLAIN, tree( WORD, "p10" ), tree( WORD, "w11" ),
            tree( PUNCTUATION_SIGN, SIGN_FULLSTOP )
     ) ) ) ;
  }

  @Test
  public void sectionIsAnonymousWithOneParagraphWithOneWord() throws RecognitionException {
    section(
        "===" + BREAK +
        BREAK +
        "p0",
        tree(
            SECTION,
            tree( PARAGRAPH_PLAIN, tree( WORD, "p0" )
        )
     ) ) ;
  }

  @Test
  public void sectionIsAnonymousWithSeveralMultilineParagraphs() throws RecognitionException {
    section(
        "===" + BREAK +
        BREAK +
        "p0 w01" + BREAK +
        "w02" + BREAK +
        BREAK +
        "p1 w11" + BREAK +
        "w12",
        tree(
            SECTION,
            tree( PARAGRAPH_PLAIN, tree( WORD, "p0" ), tree( WORD, "w01" ), tree( WORD, "w02" ) ),
            tree( PARAGRAPH_PLAIN, tree( WORD, "p1" ), tree( WORD, "w11" ), tree( WORD, "w12" )
     ) ) ) ;
  }

  @Test
  public void sectionIsAnonymousAndHasTrailingSpacesEverywhere() throws RecognitionException {
    section(
        "===  " + BREAK +
        "  " + BREAK +
        " p0 w01  " + BREAK +
        "w02 " + BREAK +
        "  " + BREAK +
        "p1 w11  " + BREAK +
        " w12 ",
        tree(
            SECTION,
            tree( PARAGRAPH_PLAIN, tree( WORD, "p0" ), tree( WORD, "w01" ), tree( WORD, "w02" ) ),
            tree( PARAGRAPH_PLAIN, tree( WORD, "p1" ), tree( WORD, "w11" ), tree( WORD, "w12" )
     ) ) ) ;
  }

  @Test
  public void sectionIsAnonymousAndHasBlockquoteWithSingleParagraph() throws RecognitionException {
    section(
      "===" + BREAK +
      BREAK +
      "<<< w0 w1" + BREAK +
      ">>>",
      tree( SECTION,
          tree(
              BLOCKQUOTE,
              tree( PARAGRAPH_PLAIN, tree( WORD, "w0" ), tree( WORD, "w1" ) )
          )
      )
    ) ;
  }

  @Test
  public void sectionIsAnonymousAndHasBlockquoteWithTwoParagraphs() throws RecognitionException {
    section(
      "===" + BREAK +
      BREAK +
      "<<< w0 w1" + BREAK +
      BREAK +
      "w2" + BREAK +
      ">>>",
      tree( SECTION,
          tree(
              BLOCKQUOTE,
              tree( PARAGRAPH_PLAIN, tree( WORD, "w0" ), tree( WORD, "w1" ) ),
              tree( PARAGRAPH_PLAIN, tree( WORD, "w2" ) )
          )
      )
    ) ;
  }

  @Test
  public void sectionIsAnonymousAndHasBlockquoteWithBreakInside() throws RecognitionException {
    section(
        "===" + BREAK +
        BREAK +
        "<<< w0 w1" + BREAK +
        BREAK +
        ">>>"
    ) ;
  }

  @Test
  public void sectionHasOneParagraphWithEmphasisThenWordOnTwoLines() throws RecognitionException {
    section(
        "===" + BREAK +
        BREAK +
        "/w0/" + BREAK +
        "w1"
    ) ;
  }

  @Test
  public void sectionHasOneParagraphWithParenthesisThenWordOnTwoLines() throws RecognitionException {
    section(
        "===" + BREAK +
        BREAK +
        "(w0)" + BREAK +
        "w1"
    ) ;
  }

  @Test
  public void sectionHasOneParagraphWithQuoteThenWordOnTwoLines() throws RecognitionException {
    section(
        "===" + BREAK +
        BREAK +
        "\"w0\"" + BREAK +
        "w1"
    ) ;
  }

  @Test
  public void paragraphBodyHasThreeWordsOnThreeLinesAndFullStopAtEndOfFirstLine() throws RecognitionException {
    paragraphBody(
        "w0." + BREAK +
        "w1" + BREAK +
        "w2"
    ) ;
  }

  @Test
  public void paragraphBodyIsJustEmphasizedWord() throws RecognitionException {
    paragraphBody( "/w0/", tree(
        EMPHASIS, tree( WORD, "w0" )
    ) ) ;
  }

  @Test
  public void paragraphBodyIsJustParenthesizedWord() throws RecognitionException {
    paragraphBody( "(w0)", tree(
        PARENTHESIS, tree( WORD, "w0" )
    ) ) ;
  }

  @Test
  public void paragraphBodyIsJustQuotedWord() throws RecognitionException {
    paragraphBody( "\"w0\"", tree(
        QUOTE, tree( WORD, "w0" )
    ) ) ;
  }

  @Test
  public void paragraphBodyIsJustInterpolatedWord() throws RecognitionException {
    paragraphBody( "-- w0 --", tree(
        INTERPOLATEDCLAUSE, tree( WORD, "w0" )
    ) ) ;
  }

  @Test
  public void paragraphBodyIsJustInterpolatedWordWithSilentEnd() throws RecognitionException {
    paragraphBody( "-- w0 -_", tree(
        INTERPOLATEDCLAUSE_SILENTEND, tree( WORD, "w0" )
    ) ) ;
  }

  @Test
  public void paragraphBodyIsJustBracketedWord() throws RecognitionException {
    paragraphBody( "[w0]", tree(
        SQUARE_BRACKETS, tree( WORD, "w0" )
    ) ) ;
  }

  @Test
  public void paragraphBodyHasQuotesAndPunctuationSignsAndWordsInTheMiddle1() throws RecognitionException {
    paragraphBody( "\"w00\" w01 w02 \" w03 w04 ! \"." ) ;
  }

  @Test
  public void paragraphBodyHasQuotesAndPunctuationSignsAndWordsInTheMiddle2() throws RecognitionException {
    paragraphBody( "w10 \"w11\" \"w12\", \"w13\"" ) ;
  }

  @Test
  public void paragraphBodyHasQuotesAndPunctuationSignsAndWordsInTheMiddle3() throws RecognitionException {
    paragraphBody( "\"w20 w21... w22\" !" ) ;
  }

  @Test
  public void paragraphBodyHasQuotesAndParenthesisAndPunctuationSignsAndWordsInTheMiddle() throws RecognitionException {
    paragraphBody( "\"p00 (w01) w02.\" w04 (w05 \"w06 (w07)\".)." ) ;
  }

  @Test
  public void paragraphBodyHasQuotesAndParenthesisAndBracketsAndPunctuationSignsAndWordsInTheMiddle() throws RecognitionException {
    paragraphBody( "\"p00 (w01) w02.\"w04(w05 \"[w06] (w07)\".)." ) ;
  }

  @Test
  public void paragraphBodyHasWordThenInterpolatedClauseThenFullStop() throws RecognitionException {
    paragraphBody( "p10 -- w11 w12 --." ) ;
  }

  @Test
  public void paragraphBodyHasWordThenInterpolatedClauseSilentEndThenFullStop() throws RecognitionException {
    paragraphBody( "p20 -- w21 w22 -_." ) ;
  }

  @Test
  public void paragraphBodyIsQuoteWithWordThenParenthesis() throws RecognitionException {
    paragraphBody( "\"w0 (w1)\"") ;
  }

  @Test
  public void paragraphBodyIsNestingQuoteAndParenthesisAndEmphasis() throws RecognitionException {
    paragraphBody( "\"w0 (w1 /w2/)\"") ;
  }

  @Test
  public void paragraphBodyIsNestingQuoteAndParenthesisAndEmphasisAndParenthesisAgain()
      throws RecognitionException
  {
    paragraphBody( "\"w0 (w1 /w2 (w3)/)\"") ;
  }

  @Test
  public void
  paragraphBodyIsNestingQuoteAndParenthesisAndInterpolatedClauseAndParenthesisAgainAndBrackets()
      throws RecognitionException
  {
    paragraphBody( "\"(w0 -- w1 (w2 [w3]) --)\"") ;
  }

  @Test
  public void paragraphBodyIsNestingEmphasisAndParenthesis() throws RecognitionException {
    paragraphBody( "/w0 (w1)/.") ;
  }

  @Test
  public void paragraphBodyIsNestingEmphasisAndParenthesisAndQuotesAndHasQuestionMarkAtTheEnd()
      throws RecognitionException
  {
    paragraphBody( "/w0 (w1, \"w2\")/ ?") ;
  }

  @Test
  public void paragraphBodyIsParenthesisWithWordThenExclamationMark() throws RecognitionException {
    paragraphBody( "(w0 !)") ;
  }

  @Test
  public void paragraphBodyIsParenthesisWithWordAndQuotesAndEllipsisInside()
      throws RecognitionException
  {
    paragraphBody( "(w0 \"w1\"...)") ;
  }

  @Test
  public void
  paragraphBodyHasNestingParenthesisAndQuoteEmphasisThenSemiColonAndWordAndExclamationMark()
      throws RecognitionException
  {
    paragraphBody( "(w0 \"w1 /w2/\") : w3 !") ;
  }

  @Test
  public void
  paragraphBodyHasQuoteThenParenthesisThenEmphasisThenInterpolatedClauseThenBracketsNoSpace()
      throws RecognitionException
  {
    paragraphBody( "\"w00\"(w01)/w02/--w03--[w04]" ) ;
  }

  @Test
  public void
  paragraphBodyIsNestingEmphasisAndParenthesisAndInterpolatedClauseAndQuotesOnSeveralLines()
      throws RecognitionException
  {
    paragraphBody(
        "/w1" + BREAK +
        "(w2 " + BREAK +
        "-- w3  " + BREAK +
        "\"w4 " + BREAK +
        "w5\"--)/."
    ) ;
  }

  @Test
  public void paragraphBodyFailsOnQuoteDepthExceeding1() throws RecognitionException {
    paragraphBodyFails( "(w0 \"w1 (w2 \"w3\")\")" ) ;
  }

  @Test
  public void paragraphBodyFailsOnEmphasisDepthExceeding1() throws RecognitionException {
    paragraphBodyFails( "(w0 /w1 (w2 /w3/)/)" ) ;
  }

  @Test
  public void paragraphBodyFailsOnParenthesisDepthExceeding2() throws RecognitionException {
    paragraphBodyFails( "(w0 /w1 (w2 \"w3 (w4)\")/)" ) ;
  }

  @Test
  public void paragraphBodyFailsOnInterpolatedClauseDepthExceeding1() throws RecognitionException {
    paragraphBodyFails( "(w0 -- w1 (w2 -- w3 -- ) --)" ) ;
  }

  @Test
  public void paragraphBodyFailsOnSquareBracketsDepthExceeding1() throws RecognitionException {
    paragraphBodyFails( "[w0 -- w1 [w2] --]" ) ;
  }

  @Test public void partIsChapterThenSectionThenSingleWordParagraph() throws RecognitionException {
    part(
        "*** c0" + BREAK +
        BREAK +
        "=== s0" + BREAK +
        BREAK +
        "p0",
        tree(
          PART,
          tree(
              CHAPTER,
              tree( IDENTIFIER, tree(WORD, "c0" ) ),
              tree(
                  SECTION,
                  tree( IDENTIFIER, tree( WORD, "s0" ) ),
                  tree( PARAGRAPH_PLAIN, tree( WORD, "p0" ) )
              )
          )
        )
    ) ;
  }

  @Test
  public void partIsAnonymousSectionsWithLeadingBreaks() throws RecognitionException {
    part(
        BREAK +
        BREAK +
        "===" + BREAK +
        BREAK +
        "p0" + BREAK +
        BREAK +
        "===" + BREAK +
        BREAK +
        "p1",
        tree(
            PART,
            tree(
                SECTION,
                tree( PARAGRAPH_PLAIN, tree( WORD, "p0" ) )
            ),
            tree(
                SECTION,
                tree( PARAGRAPH_PLAIN, tree( WORD, "p1" ) )
            )
        )
    ) ;
  }

  @Test
  public void chapterIsAnonymousWithSimpleSectionContainingWordsWithPunctuationSigns1()
      throws RecognitionException
  {
    chapter( "***" + BREAK +
        BREAK +
        "===" + BREAK +
        BREAK +
        "w0, w1."
    ) ;
  }

  @Test
  public void chapterIsAnonymousWithSimpleSectionContainingWordsWithPunctuationSigns2()
      throws RecognitionException
  { 
    chapter( "***" + BREAK +
        BREAK +
        "===" + BREAK +
        BREAK +
        "w0 : w1."
    ) ;
  }


// ========================================
// Wrappers for parser rules.
// First-class methods in Java are welcome!
// Yes this is verbose but totally readable
// stuff. Reflexion would be a mess.
// ========================================

  private static void title( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = title( text ) ;
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
  }

  private static Tree title( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().title().getTree() ;
    checkSanity( parser );
    return tree;
  }

  private static void identifier( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = identifier( text ) ;
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
  }

  private static Tree identifier( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().identifier().getTree() ;
    checkSanity( parser );
    return tree;
  }


  private static void word( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = word( text ) ;
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
  }

  private static Tree word( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().word().getTree() ;
    checkSanity( parser );
    return tree;
  }

  private static void wordFails( String s ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( s ) ;
    parser.getAntlrParser().word() ;
    final String readableProblemList = AntlrTestHelper.createProblemList( parser.getProblems() ) ;
    final boolean parserHasProblem = parser.hasProblem();
    Assert.assertTrue( readableProblemList, parserHasProblem ) ;
  }

  private static void paragraph( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = paragraph( text ) ;
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
  }

  private static Tree paragraph( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().paragraph().getTree() ;
    checkSanity( parser );
    return tree;
  }

  private static void section( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = section( text ) ;
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
  }

  private static Tree section( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().section().getTree() ;
    checkSanity( parser );
    return tree;
  }

  private static void chapter( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = chapter( text ) ;
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
  }

  private static Tree chapter( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().chapter().getTree() ;
    checkSanity( parser );
    return tree;
  }

  private static void paragraphBody( String text, Tree expectedTree )
      throws RecognitionException
  {
    final Tree actualTree = paragraphBody( text ); ;
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
  }

  private static Tree paragraphBody( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().paragraphBody( NovelangParser.ENABLE_ALL ).getTree() ;
    checkSanity( parser );
    return tree;
  }

  private static void paragraphBodyFails( String s ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( s ) ;
    parser.getAntlrParser().paragraphBody( NovelangParser.ENABLE_ALL ) ;
    final String readableProblemList = AntlrTestHelper.createProblemList( parser.getProblems() ) ;
    final boolean parserHasProblem = parser.hasProblem() ;
    Assert.assertTrue( readableProblemList, parserHasProblem ) ;
  }

  private static void part( String text, Tree expectedTree )
      throws RecognitionException
  {
    final Tree actualTree = part( text ); ;
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
  }

  private static Tree part( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().part().getTree() ;
    checkSanity( parser );
    return tree;
  }


// ================
// Boring utilities
// ================

  private static void checkSanity( DelegatingPartParser parser ) {
    if( parser.hasProblem() ) {
      throw new AssertionFailedError(
          "Parser has problems. " + AntlrTestHelper.createProblemList( parser.getProblems() ) ) ;
    }
  }


  private static DelegatingPartParser createPartParser( String text ) {
    return ( DelegatingPartParser )
        new DefaultPartParserFactory().createParser( TreeHelper.LOCATION_FACTORY, text );
  }
}

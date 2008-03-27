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

import static novelang.parser.antlr.AntlrTestHelper.*;

import java.util.Map;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.Assert;
import static novelang.model.common.NodeKind.*;
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
  public void wordCausedABug1() throws RecognitionException {
    word( "myIdentifier", tree( WORD, "myIdentifier" ) ) ;
  }

  @Test
  /**
   * This one because {@code 'fi'} was recognized as the start of {@code 'file'}
   * and the parser generated this error: 
   * {@code line 1:10 mismatched character 'e' expecting 'l'}.
   */
  public void wordCausedABug2() throws RecognitionException {
    word( "fi", tree( WORD, "fi" ) ) ;
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
      final String escaped = "&" + key + ";" ;
      final String unescaped = map.get( key ) ;
      word( escaped, tree( WORD, unescaped ) ) ;
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
  // In addition, the ParagraphScope is declared in paragraph rule so we must get through it.

  @Test
  public void paragraphIsWordThenComma() throws RecognitionException {
    paragraph( "w0,", tree(
        PARAGRAPH_PLAIN,
        tree( WORD, "w0" ),
        tree( PUNCTUATION_SIGN, SIGN_COMMA )
    ) ) ;

  }

  @Test
  public void paragraphIsWordsWithCommaInTheMiddle1() throws RecognitionException {
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
  public void paragraphBodyIsEmphasizedWordThenWord()
      throws RecognitionException
  {
    paragraph(
        "//w0//w1"
    ) ;
  }

  @Test
  public void paragraphIsWordsWithApostropheThenEmphasis() throws RecognitionException {
    paragraph( "w0 w1'w2//w3//.", tree(
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
    paragraph(
        "\"w1 w2. w3 w4." + BREAK +
        "w5 !\"" + BREAK +
        "w6 w7."
    ) ;
  }

  @Test
  public void paragraphIsMultilineQuoteWithPunctuationSigns2() throws RecognitionException {
    paragraph(
        "//w1.//" + BREAK +
        "w2. w3."
    ) ;
  }

  @Test
  public void paragraphIsEmphasisAndQuoteWithPunctuationSigns1() throws RecognitionException {
    paragraph(
        "//w0.// " + BREAK +
        "  w1. w2. w3. " + BREAK +
        "  w4 : w5 w6. " + BREAK +
        "  \"w7 w8 ?\"."
    ) ;
  }


  @Test
  public void paragraphIsJustEllipsis() throws RecognitionException {
    paragraph(
        "..."
    ) ;
  }

  @Test
  public void paragraphIsEllipsisThenWord() throws RecognitionException {
    paragraph(
        "...w0"
    ) ;
  }

  @Test
  public void paragraphIsEllipsisInsideBrackets() throws RecognitionException {
    paragraph(
        "[...]"
    ) ;
  }

  @Test
  public void paragraphIsWordsAndPunctuationSigns1() throws RecognitionException {
    paragraph( "w1 w2, w3 w4." ) ;
  }

  @Test
  public void paragraphIsParenthesizedWordsWithApostropheInTheMiddle()
      throws RecognitionException
  {
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
  public void paragraphIsQuoteOfOneWordThenParenthesis() throws RecognitionException {
    paragraph(
        "\"w0(w1)\"",
        tree(
            PARAGRAPH_PLAIN,
            tree(
                QUOTE,
                tree( WORD, "w0" ),
                tree( PARENTHESIS, tree( WORD, "w1" ) )
            )
        )
    ) ;
  }

  @Test
  public void paragraphIsQuoteOfOneWordThenSpaceParenthesis() throws RecognitionException {
    paragraph(
        "\"w0 (w1)\"",
        tree(
            PARAGRAPH_PLAIN,
            tree(
                QUOTE,
                tree( WORD, "w0" ),
                tree( PARENTHESIS, tree( WORD, "w1" ) )
            )
        )
    ) ;
  }

  @Test
  public void sectionHasIdentifierAndOneParagraphWithTwoWordsAndAPeriod()
      throws RecognitionException
  {
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
        "//w0//" + BREAK +
        "w1"
    ) ;
  }

  @Test
  public void sectionHasOneParagraphWithParenthesisThenWordOnTwoLines()
      throws RecognitionException
  {
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
  public void paragraphBodyHasThreeWordsOnThreeLinesAndFullStopAtEndOfFirstLine()
      throws RecognitionException
  {
    paragraph(
        "w0." + BREAK +
        "w1" + BREAK +
        "w2"
    ) ;
  }

  @Test
  public void paragraphBodyIsJustEmphasizedWord() throws RecognitionException {
    paragraph( "//w0//", tree(
        PARAGRAPH_PLAIN,
        tree( EMPHASIS, tree( WORD, "w0" ) )
    ) ) ;
  }

  @Test
  public void paragraphBodyIsJustParenthesizedWord() throws RecognitionException {
    paragraph( "(w0)", tree(
        PARAGRAPH_PLAIN,
        tree( PARENTHESIS, tree( WORD, "w0" ) )
    ) ) ;
  }

  @Test
  public void paragraphBodyIsJustQuotedWord() throws RecognitionException {
    paragraph( "\"w0\"", tree(
        PARAGRAPH_PLAIN,
        tree( QUOTE, tree( WORD, "w0" ) )
    ) ) ;
  }

  @Test
  public void paragraphBodyIsJustInterpolatedWord() throws RecognitionException {
    paragraph( "-- w0 --", tree(
        PARAGRAPH_PLAIN,
        tree( INTERPOLATEDCLAUSE, tree( WORD, "w0" ) )
    ) ) ;
  }

  @Test
  public void paragraphBodyIsJustInterpolatedWordWithSilentEnd() throws RecognitionException {
    paragraph( "-- w0 -_", tree(
        PARAGRAPH_PLAIN,
        tree( INTERPOLATEDCLAUSE_SILENTEND, tree( WORD, "w0" ) )
    ) ) ;
  }

  @Test
  public void paragraphBodyIsJustBracketedWord() throws RecognitionException {
    paragraph( "[w0]", tree(
        PARAGRAPH_PLAIN,
        tree( SQUARE_BRACKETS, tree( WORD, "w0" ) )
    ) ) ;
  }

  @Test
  public void paragraphBodyHasQuotesAndWordAndSpaceAndQuotes()
      throws RecognitionException
  {
    paragraph( "\"w0\"w2 \"w3\"" ) ;
  }

  @Test
  public void paragraphBodyHasQuotesAndPunctuationSignsAndWordsInTheMiddle1()
      throws RecognitionException
  {
    paragraph( "\"w00\" w01 w02 \" w03 w04 ! \"." ) ;
  }

  @Test
  public void paragraphBodyHasQuotesAndPunctuationSignsAndWordsInTheMiddle2()
      throws RecognitionException
  {
    paragraph( "w10 \"w11\" \"w12\", \"w13\"" ) ;
  }

  @Test
  public void paragraphBodyHasQuotesAndPunctuationSignsAndWordsInTheMiddle3()
      throws RecognitionException
  {
    paragraph( "\"w20 w21... w22\" !" ) ;
  }

  @Test
  public void paragraphBodyHasQuotesAndParenthesisAndPunctuationSignsAndWordsInTheMiddle()
      throws RecognitionException
  {
    paragraph( "\"p00 (w01) w02.\" w04 (w05 \"w06 (w07)\".)." ) ;
  }

  @Test
  public void
  paragraphBodyHasQuotesAndParenthesisAndBracketsAndPunctuationSignsAndWordsInTheMiddle()
      throws RecognitionException
  {
    paragraph( "\"p00 (w01) w02.\"w04(w05 \"[w06] (w07)\".)." ) ;
  }

  @Test
  public void paragraphBodyHasWordThenInterpolatedClauseThenFullStop()
      throws RecognitionException
  {
    paragraph( "p10 -- w11 w12 --." ) ;
  }

  @Test
  public void paragraphBodyHasWordThenInterpolatedClauseSilentEndThenFullStop()
      throws RecognitionException
  {
    paragraph( "p20 -- w21 w22 -_." ) ;
  }

  @Test
  public void paragraphBodyIsQuoteWithWordThenParenthesis() throws RecognitionException {
    paragraph( "\"w0 (w1)\"") ;
  }

  @Test
  public void paragraphBodyIsNestingQuoteAndParenthesisAndEmphasis() throws RecognitionException {
    paragraph( "\"w0 (w1 //w2//)\"") ;
  }

  @Test
  public void paragraphBodyIsNestingQuoteAndParenthesisAndEmphasisAndParenthesisAgain()
      throws RecognitionException
  {
    paragraph( "\"w0 (w1 //w2 (w3)//)\"") ;
  }

  @Test
  public void
  paragraphBodyIsNestingQuoteAndParenthesisAndInterpolatedClauseAndParenthesisAgainAndBrackets()
      throws RecognitionException
  {
    paragraph( "\"(w0 -- w1 (w2 [w3]) --)\"") ;
  }

  @Test
  public void paragraphBodyIsNestingEmphasisAndParenthesis() throws RecognitionException {
    paragraph( "//w0 (w1)//.") ;
  }

  @Test
  public void paragraphBodyIsNestingEmphasisAndParenthesisAndQuotesAndHasQuestionMarkAtTheEnd()
      throws RecognitionException
  {
    paragraph( "//w0 (w1, \"w2\")// ?") ;
  }

  @Test
  public void paragraphBodyIsParenthesisWithWordThenExclamationMark() throws RecognitionException {
    paragraph( "(w0 !)") ;
  }

  @Test
  public void paragraphBodyIsParenthesisWithWordAndQuotesAndEllipsisInside()
      throws RecognitionException
  {
    paragraph( "(w0 \"w1\"...)") ;
  }

  @Test
  public void
  paragraphBodyHasNestingParenthesisAndQuoteEmphasisThenSemiColonAndWordAndExclamationMark()
      throws RecognitionException
  {
    paragraph( "(w0 \"w1 //w2//\") : w3 !") ;
  }

  @Test
  public void
  paragraphBodyHasQuoteThenParenthesisThenEmphasisThenInterpolatedClauseThenBracketsNoSpace()
      throws RecognitionException
  {
    paragraph( "\"w00\"(w01)//w02//--w03--[w04]" ) ;
  }

  @Test
  public void
  paragraphBodyIsNestingEmphasisAndParenthesisAndInterpolatedClauseAndQuotesOnSeveralLines()
      throws RecognitionException
  {
    paragraph(
        "//w1" + BREAK +
        "(w2 " + BREAK +
        "-- w3  " + BREAK +
        "\"w4 " + BREAK +
        "w5\"--)//."
    ) ;
  }

  @Test @Ignore
  public void paragraphBodyFailsOnQuoteDepthExceeding1() throws RecognitionException {
    paragraphFails( "(w0 \"w1 (w2 \"w3\")\")" ) ;
  }

  @Test @Ignore
  public void paragraphBodyFailsOnEmphasisDepthExceeding1() throws RecognitionException {
    paragraphFails( "(w0 /w1 (w2 /w3/)/)" ) ;
  }

  @Test @Ignore
  public void paragraphBodyFailsOnParenthesisDepthExceeding2() throws RecognitionException {
    paragraphFails( "(w0 /w1 (w2 \"w3 (w4)\")/)" ) ;
  }

  @Test @Ignore
  public void paragraphBodyFailsOnInterpolatedClauseDepthExceeding1() throws RecognitionException {
    paragraphFails( "(w0 -- w1 (w2 -- w3 -- ) --)" ) ;
  }

  @Test @Ignore
  public void paragraphBodyFailsOnSquareBracketsDepthExceeding1() throws RecognitionException {
    paragraphFails( "[w0 -- w1 [w2] --]" ) ;
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

  /**
   * This one because {@code 'lobs'} was recognized as the start of {@code 'localhost'}
   * and the parser generated this error:
   * {@code line 3:3 mismatched character 'b' expecting 'c' }.
   */
  @Test
  public void partMadeOfParticularContent() throws RecognitionException {
    part(
      "===" + BREAK +
      BREAK +
      " lobs "  // really.
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

  @Test
  public void chapterContainsUrl()
      throws RecognitionException
  {
    chapter( "***" + BREAK +
        BREAK +
        "===" + BREAK +
        BREAK +
        "http://google.com"
    ) ;
  }

  @Test
  public void urlHttpGoogleDotCom() throws RecognitionException {
    url( "http://google.com", tree( URL, "http://google.com" ) ) ;
  }

  @Test
  public void urlHttpLocalhost() throws RecognitionException {
    url( "http://localhost", tree( URL, "http://localhost" ) ) ;
  }

  @Test
  public void urlHttpLocalhost8080() throws RecognitionException {
    url( "http://localhost:8080", tree( URL, "http://localhost:8080" ) ) ;
  }

  @Test
  public void urlHttpGoogleQuery() throws RecognitionException {
    url(
        "http://www.google.com/search?q=url%20specification&sourceid=mozilla2&ie=utf-8&oe=utf-8",
        tree(
            URL,
            "http://www.google.com/search?q=url%20specification&sourceid=mozilla2&ie=utf-8&oe=utf-8"
        )
    ) ;
  }

  @Test
  public void urlFilePathFileDotNlp() throws RecognitionException {
    url( "file://path/file.nlp", tree( URL, "file://path/file.nlp" ) ) ;
  }


  @Test
  public void urlWithTilde() throws RecognitionException {
    url(
        "http://domain.org/path/file~tilde#anchor",
        tree(
            URL,
            "http://domain.org/path/file~tilde#anchor"
        )
    ) ;
  }



}

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

import java.util.Map;

import org.antlr.runtime.RecognitionException;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.common.SyntacticTree;
import novelang.parser.NodeKind;
import static novelang.parser.NodeKind.*;
import novelang.parser.SourceUnescape;
import static novelang.parser.antlr.AntlrTestHelper.BREAK;
import static novelang.parser.antlr.TreeFixture.tree;

/**
 * GUnit sucks as it has completely obscure failures and stupid reports,
 * but it has some nice ideas to borrow.
 *
 * @author Laurent Caillette
 */
public class PartParserTest {

  private static final Logger LOGGER = LoggerFactory.getLogger( PartParserTest.class ) ;
  /*package*/ static final ParserMethod PARSERMETHOD_TITLE = 
      new ParserMethod( "levelTitle" ) ;
  /*package*/ static final ParserMethod PARSERMETHOD_HEADER_IDENTIFIER = 
      new ParserMethod( "headerIdentifier" ) ;
  /*package*/ static final ParserMethod PARSERMETHOD_WORD = 
      new ParserMethod( "word" ) ;
  /*package*/ static final ParserMethod PARSERMETHOD_PARAGRAPH = 
      new ParserMethod( "paragraph" ) ;
  /*package*/ static final ParserMethod PARSERMETHOD_BIG_DASHED_LIST_ITEM = 
      new ParserMethod( "bigDashedListItem" ) ;
  /*package*/ static final ParserMethod PARSERMETHOD_LITERAL =
      new ParserMethod( "literal" ) ;
  /*package*/ static final ParserMethod PARSERMETHOD_SOFT_INLINE_LITERAL = 
      new ParserMethod( "softInlineLiteral" ) ;
  /*package*/ static final ParserMethod PARSERMETHOD_HARD_INLINE_LITERAL = 
      new ParserMethod( "hardInlineLiteral" ) ;
  /*package*/ static final ParserMethod PARSERMETHOD_CHAPTER = 
      new ParserMethod( "levelIntroducer" ) ;
  /*package*/ static final ParserMethod PARSERMETHOD_SECTION =
      new ParserMethod( "levelIntroducer" ) ;
  /*package*/ static final ParserMethod PARSERMETHOD_LEVEL_INTRODUCER =
      new ParserMethod( "levelIntroducer" ) ;
  /*package*/ static final ParserMethod PARSERMETHOD_PART =
      new ParserMethod( "part" ) ;
  /*package*/ static final ParserMethod PARSERMETHOD_URL = 
      new ParserMethod( "url" ) ;
  /*package*/ static final ParserMethod PARSERMETHOD_CELL_ROW_SEQUENCE = 
      new ParserMethod( "cellRowSequence" ) ;
  /*package*/ static final ParserMethod PARSERMETHOD_EMBEDDABLE_RESOURCE =
      new ParserMethod( "embeddableResource" ) ;

  private static final SyntacticTree TREE_APOSTROPHE_WORDMATE = tree( APOSTROPHE_WORDMATE, "'" ) ;

  private static final SyntacticTree TREE_SIGN_EXCLAMATION_MARK =
      tree( PUNCTUATION_SIGN, tree( SIGN_EXCLAMATIONMARK, "!" ) ) ;

  private static final SyntacticTree TREE_SIGN_SEMICOLON =
      tree( PUNCTUATION_SIGN, tree( SIGN_SEMICOLON, ";" ) );

  private static final SyntacticTree TREE_SIGN_FULLSTOP =
      tree( PUNCTUATION_SIGN, tree( SIGN_FULLSTOP, "." ) ) ;

  private static final SyntacticTree TREE_SIGN_QUESTIONMARK =
      tree( PUNCTUATION_SIGN, tree( SIGN_QUESTIONMARK, "?" ) );
  private static final SyntacticTree TREE_SIGN_COLON =
      tree( PUNCTUATION_SIGN, tree( SIGN_COLON, ":" ) ) ;

  private static final SyntacticTree TREE_SIGN_ELLIPSIS =
      tree( PUNCTUATION_SIGN, tree( SIGN_ELLIPSIS, "..." ) );

  @Test
  public void wordContainsOELigatured() throws RecognitionException {
    PARSERMETHOD_WORD.createTree( "\u0153\u0152" ) ;
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
  public void wordCausedABug1() throws RecognitionException {
    PARSERMETHOD_WORD.checkTreeAfterSeparatorRemoval( "myIdentifier", tree( WORD_, "myIdentifier" ) ) ;
  }

  @Test
  /**
   * This one because {@code 'fi'} was recognized as the start of {@code 'file'}
   * and the parser generated this error: 
   * {@code line 1:10 mismatched character 'e' expecting 'l'}.
   */
  public void wordCausedABug2() throws RecognitionException {
    PARSERMETHOD_WORD.checkTreeAfterSeparatorRemoval( "fi", tree( WORD_, "fi" ) ) ;
  }

  @Test
  public void wordIsSingleLetter() throws RecognitionException {
    PARSERMETHOD_WORD.checkTreeAfterSeparatorRemoval( "w", tree( WORD_, "w" ) ) ;
  }

  @Test
  public void wordIsTwoLetters() throws RecognitionException {
    PARSERMETHOD_WORD.checkTreeAfterSeparatorRemoval( "Www", tree( WORD_, "Www" ) ) ;
  }

  @Test
  public void wordIsThreeDigits() throws RecognitionException {
    PARSERMETHOD_WORD.checkTreeAfterSeparatorRemoval( "123", tree( WORD_, "123" ) ) ;
  }

  @Test
  public void wordIsDigitsWithHyphenMinusInTheMiddle() throws RecognitionException {
    PARSERMETHOD_WORD.checkTreeAfterSeparatorRemoval( "123-456", tree( WORD_, "123-456" ) ) ;
  }

  @Test
  public void wordFailsWithLeadingApostrophe() throws RecognitionException {
    PARSERMETHOD_WORD.checkFails( "'w" ) ;
  }

  @Test
  public void wordFailsWithTrailingHyphenMinus() throws RecognitionException {
    PARSERMETHOD_WORD.checkFails( "'w-" ) ;
  }

  @Test
  public void wordWithSuperscript() throws RecognitionException {
    PARSERMETHOD_WORD.checkTreeAfterSeparatorRemoval(
        "w^e",
        tree( WORD_, tree( "w" ), tree( WORD_AFTER_CIRCUMFLEX_ACCENT, "e" ) )
    ) ;
  }

  @Test
  public void wordIsEveryEscapedCharacter() throws RecognitionException {
    final Map< String, Character > map = SourceUnescape.getMainCharacterEscapes() ;
    for( String key : map.keySet() ) {
      final String escaped = SourceUnescape.ESCAPE_START + key + SourceUnescape.ESCAPE_END ;
      final Character unescaped = map.get( key ) ;
      PARSERMETHOD_WORD.checkTreeAfterSeparatorRemoval( escaped, tree( WORD_, "" + unescaped ) ) ;
    }
  }

  @Test
  public void failOnUnknownEscapedCharacter() throws RecognitionException {
    PARSERMETHOD_WORD.checkFails(
        SourceUnescape.ESCAPE_START + "does-not-exist" + SourceUnescape.ESCAPE_END ) ;
  }

  @Test
  public void paragraphIsSimplestList() throws RecognitionException {
    PARSERMETHOD_BIG_DASHED_LIST_ITEM.checkTreeAfterSeparatorRemoval( "--- w0", tree(
        PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_,
        tree( WORD_, "w0" )
    ) ) ;
  }


  
  // Following tests are for paragraphBody rule. But we need to rely on a rule
  // returning a sole tree as test primitives don't assert on more than one.
  // In addition, the ParagraphScope is declared in paragraph rule so we must get through it.

  @Test
  public void paragraphIsWordThenComma() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval( "w0,", tree(
        NodeKind.PARAGRAPH_REGULAR,
        tree( WORD_, "w0" ),
        tree( PUNCTUATION_SIGN, tree( SIGN_COMMA, "," ) )
    ) ) ;
  }



  @Test
  public void paragraphIsWordsWithCommaInTheMiddle1() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval( 
        "w0,w1", tree(
            NodeKind.PARAGRAPH_REGULAR,
            tree( WORD_, "w0" ),
            tree( PUNCTUATION_SIGN, tree( SIGN_COMMA, "," ) ),
            tree( WORD_, "w1" )
        ) 
    ) ;
  }

  @Test
  public void paragraphIsWordThenApostrophe() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval( 
        "w0'", 
        tree(
            NodeKind.PARAGRAPH_REGULAR,
            tree( WORD_, "w0" ),
            TREE_APOSTROPHE_WORDMATE
        ) 
    ) ;

  }

  @Test
  public void paragraphIsWordsWithApostropheInTheMiddle() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval( 
        "w0'w1", 
        tree(
            NodeKind.PARAGRAPH_REGULAR,
            tree( WORD_, "w0" ),
            TREE_APOSTROPHE_WORDMATE,
            tree( WORD_, "w1" )
        ) 
    ) ;
  }

  @Test
  public void paragraphIsWordThenSemicolon() throws RecognitionException {
    SyntacticTree tree = PARSERMETHOD_WORD.createTree( "w0" ) ;
    LOGGER.debug( tree.toStringTree() ) ;

    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval( 
        "w0;", 
        tree(
            NodeKind.PARAGRAPH_REGULAR,
            tree( WORD_, "w0" ),
            TREE_SIGN_SEMICOLON
        ) 
    ) ;

  }

  @Test
  public void paragraphIsWordThenFullStop() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval( 
        "w0.", 
        tree(
            NodeKind.PARAGRAPH_REGULAR,
            tree( WORD_, "w0" ),
            TREE_SIGN_FULLSTOP
        ) 
    ) ;

  }

  @Test
  public void paragraphIsWordThenQuestionMark() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval( 
        "w0?", 
        tree(
            NodeKind.PARAGRAPH_REGULAR,
            tree( WORD_, "w0" ),
            TREE_SIGN_QUESTIONMARK
        ) 
    ) ;

  }

  @Test
  public void paragraphIsWordThenExclamationMark() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval( 
        "w0!", 
        tree(
            NodeKind.PARAGRAPH_REGULAR,
            tree( WORD_, "w0" ),
            TREE_SIGN_EXCLAMATION_MARK
        ) 
    ) ;

  }

  @Test
  public void paragraphIsWordThenColon() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval( 
        "w0:", 
        tree(
            NodeKind.PARAGRAPH_REGULAR,
            tree( WORD_, "w0" ),
            TREE_SIGN_COLON
        ) 
    ) ;

  }

  @Test
  public void paragraphIsWordThenEllipsis() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval( 
        "w0...", 
        tree(
            NodeKind.PARAGRAPH_REGULAR,
            tree( WORD_, "w0" ),
            TREE_SIGN_ELLIPSIS
        ) 
    ) ;

  }

  @Test
  public void paragraphBodyIsEmphasizedWordThenWord()
      throws RecognitionException
  {
    PARSERMETHOD_PARAGRAPH.createTree( "//w0//w1" );
  }

  @Test
  public void paragraphIsWordsWithApostropheThenEmphasis() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval( 
        "w0 w1'w2//w3//.", 
        tree(
            NodeKind.PARAGRAPH_REGULAR,
            tree( WORD_, "w0" ),
            tree( WORD_, "w1" ),
            TREE_APOSTROPHE_WORDMATE,
            tree( WORD_, "w2" ),
            tree( BLOCK_INSIDE_SOLIDUS_PAIRS, tree( WORD_, "w3" ) ),
            TREE_SIGN_FULLSTOP
        ) 
    ) ;

  }

  @Test
  public void paragraphIsMultilineQuoteWithPunctuationSigns1() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.createTree( 
        "\"w1 w2. w3 w4." + BREAK +
        "w5 !\"" + BREAK +
        "w6 w7." );
  }

  @Test
  public void paragraphIsMultilineQuoteWithPunctuationSigns2() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.createTree( 
        "//w1.//" + BREAK +
        "w2. w3." 
    ) ;
  }

  @Test
  public void paragraphIsEmphasisAndQuoteWithPunctuationSigns1() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.createTree( 
        "//w0.// " + BREAK +
        "  w1. w2. w3. " + BREAK +
        "  w4 : w5 w6. " + BREAK +
        "  \"w7 w8 ?\"." 
    );
  }


  @Test
  public void paragraphIsJustEllipsis() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.createTree( "..." ) ;
  }

  @Test
  public void paragraphIsEllipsisThenWord() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.createTree( "...w0" ) ;
  }

  @Test
  public void paragraphIsEllipsisInsideBrackets() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.createTree( "[...]" ) ;
  }

  @Test
  public void paragraphIsWordsAndPunctuationSigns1() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.createTree( "w1 w2, w3 w4." ) ;
  }

  @Test
  public void paragraphIsParenthesizedWordsWithApostropheInTheMiddle()
      throws RecognitionException
  {
    PARSERMETHOD_PARAGRAPH.createTree( "(w0'w1)" ) ;
  }

  @Test
  public void paragraphIsParenthesizedWordsWithCommaInTheMiddle() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.createTree( "(w0,w1)" ) ;
  }

  @Test
  public void paragraphIsEmphasizedWordsWithApostropheInTheMiddle() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.createTree( "\"w0'w1\"" ) ;
  }

  @Test
  public void paragraphIsQuotedWordsWithCommaInTheMiddle() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.createTree( "\"w0,w1\"" ) ;
  }

  @Test
  public void paragraphIsInterpolatedWordsWithApostropheInTheMiddle() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.createTree( "--w0'w1--" );
  }

  @Test
  public void paragraphIsInterpolatedWordsWithCommaInTheMiddle() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.createTree( "--w0,w1--" );
  }
  
  @Test @Ignore
  public void paragraphHasLineBreaksInsideParenthensis() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval(  
        "(" + BREAK +
        "x" + BREAK +
        ")",
        tree( 
            PARAGRAPH_REGULAR,
            tree(
                BLOCK_INSIDE_PARENTHESIS,
                tree( WORD_, "x")
            )
        )
    ) ;
  }

  @Test
  public void paragraphIsQuoteOfOneWordThenParenthesis() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval( 
        "\"w0(w1)\"", tree(
            NodeKind.PARAGRAPH_REGULAR,
            tree(
                BLOCK_INSIDE_DOUBLE_QUOTES,
                tree( WORD_, "w0" ),
                tree( BLOCK_INSIDE_PARENTHESIS, tree( WORD_, "w1" ) )
            )
        ) ) ;
  }

  @Test
  public void paragraphIsQuoteOfOneWordThenSpaceParenthesis() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval( 
        "\"w0 (w1)\"", tree(
            NodeKind.PARAGRAPH_REGULAR,
            tree(
                BLOCK_INSIDE_DOUBLE_QUOTES,
                tree( WORD_, "w0" ),
                tree( BLOCK_INSIDE_PARENTHESIS, tree( WORD_, "w1" ) )
            )
        ) ) ;
  }
  
  @Test
  public void partIsJustImage() throws RecognitionException {
    PARSERMETHOD_PART.checkTreeAfterSeparatorRemoval(
      "./foo.jpg",
        tree(
          PART,
          tree( RASTER_IMAGE, tree( RESOURCE_LOCATION, tree( "./foo.jpg" ) ) )
        )        
    ) ;
  }
  
  
  @Test @Ignore
  public void paragraphIsTextThenImage() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval(
      "blah" + BREAK +
      "./foo.jpg",
        tree(
          PARAGRAPH_REGULAR,
          tree( WORD_, "blah"),
          tree( RASTER_IMAGE, tree( "./foo.jpg" ) )
        )        
    ) ;
  }
  
  @Test @Ignore
  public void paragraphIsTextThenImageThenText() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval(
      "w" + BREAK +
      "./foo.jpg" + BREAK +
      "x" + BREAK,      
        tree(
          PARAGRAPH_REGULAR,
          tree( WORD_, "w"),
          tree( RASTER_IMAGE, tree( "./foo.jpg" ) ),
          tree( WORD_, "x")
        )        
    ) ;
  }
  
  @Test @Ignore
  public void paragraphIsImageThenText() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval(
      "./foo.jpg" + BREAK +
      "x" + BREAK,      
        tree(
          PARAGRAPH_REGULAR,
          tree( RASTER_IMAGE, tree( "./foo.jpg" ) ),
          tree( WORD_, "x")
        )        
    ) ;
  }
  
  
  @Test
  public void bigListItemContainsUrl() throws RecognitionException {
    PARSERMETHOD_BIG_DASHED_LIST_ITEM.checkTreeAfterSeparatorRemoval(  
        "--- w" + BREAK +
        "http://novelang.sf.net"
        ,
        tree(
            PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_,
            tree( WORD_, "w" ),
            tree( URL_LITERAL, "http://novelang.sf.net" )
        )
    );
  }


  @Test
  public void sectionHasQuote()
      throws RecognitionException
  {
    PARSERMETHOD_SECTION.checkTreeAfterSeparatorRemoval( 
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
    PARSERMETHOD_SECTION.checkTreeAfterSeparatorRemoval( 
        "===", 
        tree( 
            LEVEL_INTRODUCER_,
            tree( LEVEL_INTRODUCER_INDENT_, "===" )
        ) 
    ) ;
  }

  @Test
  public void partWithSeveralMultilineParagraphs() throws RecognitionException {
    PARSERMETHOD_PART.checkTreeAfterSeparatorRemoval( 
        BREAK +
        "p0 w01" + BREAK +
        "w02" + BREAK +
        BREAK +
        "p1 w11" + BREAK +
        "w12", tree(
            PART,
            tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "p0" ), tree( WORD_, "w01" ), tree( WORD_, "w02" ) ),
            tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "p1" ), tree( WORD_, "w11" ), tree( WORD_, "w12" )
     ) ) ) ;
  }

  @Test
  public void partHasTrailingSpacesEverywhere() throws RecognitionException {
    PARSERMETHOD_PART.checkTreeAfterSeparatorRemoval( 
        BREAK +
        "  " + BREAK +
        " p0 w01  " + BREAK +
        "w02 " + BREAK +
        "  " + BREAK +
        "p1 w11  " + BREAK +
        " w12 ", tree(
            PART,
            tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "p0" ), tree( WORD_, "w01" ), tree( WORD_, "w02" ) ),
            tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "p1" ), tree( WORD_, "w11" ), tree( WORD_, "w12" ) )
        ) 
    ) ;
  }

  @Test
  public void someLiteral() throws RecognitionException {
    PARSERMETHOD_PART.checkTreeAfterSeparatorRemoval( 
      "<<<" + BREAK +
      "  Here is some " + BREAK +
      "  //Literal// " + BREAK +
      ">>>",
      tree(
          PART,
          tree( LINES_OF_LITERAL, "  Here is some " + BREAK + "  //Literal// " )
      ) 
    ) ;
  }

  @Test @Ignore
  public void someLiteralContainingLineComment() throws RecognitionException {
    PARSERMETHOD_PART.checkTreeAfterSeparatorRemoval( 
        "<<<" + BREAK +
        "%% Not to be commented" +
        ">>>",
        tree(
            PART,
            tree( LINES_OF_LITERAL, "%% Not to be commented" )
        ) 
    ) ;
  }

  @Test
  public void someLiteralContainingLowerthanSign() throws RecognitionException {
    PARSERMETHOD_PART.checkTreeAfterSeparatorRemoval( 
        "<<<" + BREAK +
        "<" + BREAK +
        ">>>", tree( PART, tree( LINES_OF_LITERAL, "<" )
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

    PARSERMETHOD_PART.checkTreeAfterSeparatorRemoval( 
        "<<<" + BREAK +
        verbatim + BREAK +
        ">>>", tree( PART, tree( LINES_OF_LITERAL, verbatim ) )
    ) ;
  }

  
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
  public void partHasAnonymousSectionAndHasBlockquoteWithSingleParagraph() 
      throws RecognitionException
  {
    PARSERMETHOD_PART.checkTreeAfterSeparatorRemoval( 
        "===" + BREAK +
        BREAK +
        "<< w0 w1" + BREAK +
        ">>", 
        tree( 
            PART,
            tree( LEVEL_INTRODUCER_, tree( LEVEL_INTRODUCER_INDENT_, "===" ) ),
            tree(
                PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS,
                tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "w0" ), tree( WORD_, "w1" ) )
            )
        ) 
    ) ;
  }

  @Test
  public void partIsSectionThenParagraphThenBlockquoteThenParagraph()
      throws RecognitionException
  {
    PARSERMETHOD_PART.checkTreeAfterSeparatorRemoval( 
        "===" + BREAK +
        BREAK +
        "p0" + BREAK +
        BREAK +
        "<< w0" + BREAK +
        ">>" + BREAK +
        BREAK +
        "p1", 
        tree( PART,
            tree( LEVEL_INTRODUCER_, tree( LEVEL_INTRODUCER_INDENT_, "===" ) ),
            tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "p0" ) ),
            tree(
                PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS,
                tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "w0" ) )
            ),
            tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "p1" ) )
        ) 
    ) ;
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
  public void sectionHasOneParagraphWithEmphasisThenWordOnTwoLines() throws RecognitionException {
    PARSERMETHOD_SECTION.createTree( 
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
    PARSERMETHOD_SECTION.createTree( 
        "===" + BREAK +
        BREAK +
        "(w0)" + BREAK +
        "w1" 
    );
  }

  @Test
  public void sectionHasOneParagraphWithQuoteThenWordOnTwoLines() throws RecognitionException {
    PARSERMETHOD_SECTION.createTree( 
        "===" + BREAK +
        BREAK +
        "\"w0\"" + BREAK +
        "w1" 
    );
  }

  @Test
  public void paragraphBodyHasThreeWordsOnThreeLinesAndFullStopAtEndOfFirstLine()
      throws RecognitionException
  {
    PARSERMETHOD_PARAGRAPH.createTree( 
        "w0." + BREAK +
        "w1" + BREAK +
        "w2" 
    );
  }

  @Test
  public void paragraphBodyIsJustEmphasizedWord() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval( 
        "//w0//", tree(
            NodeKind.PARAGRAPH_REGULAR,
        tree( BLOCK_INSIDE_SOLIDUS_PAIRS, tree( WORD_, "w0" ) ) )
    ) ;
  }

  @Test
  public void paragraphBodyIsJustParenthesizedWord() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval( 
        "(w0)", 
        tree(
            NodeKind.PARAGRAPH_REGULAR,
            tree( BLOCK_INSIDE_PARENTHESIS, tree( WORD_, "w0" ) )
        ) 
    ) ;
  }

  @Test
  public void paragraphBodyIsJustQuotedWord() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval( 
        "\"w0\"", tree(
            NodeKind.PARAGRAPH_REGULAR,
            tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "w0" ) )
        ) 
    ) ;
  }

  @Test
  public void paragraphBodyIsJustInterpolatedWord() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval( 
        "-- w0 --", 
        tree(
            NodeKind.PARAGRAPH_REGULAR,
            tree( BLOCK_INSIDE_HYPHEN_PAIRS, tree( WORD_, "w0" ) )
        ) 
    ) ;
  }

  @Test
  public void paragraphBodyIsJustInterpolatedWordWithSilentEnd() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval( 
        "-- w0 -_", 
        tree(
            NodeKind.PARAGRAPH_REGULAR,
            tree( BLOCK_INSIDE_TWO_HYPHENS_THEN_HYPHEN_LOW_LINE, tree( WORD_, "w0" ) )
        ) 
    );
  }

  @Test
  public void paragraphBodyIsJustBracketedWord() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTreeAfterSeparatorRemoval( 
        "[w0]", 
        tree(
            NodeKind.PARAGRAPH_REGULAR,
            tree( BLOCK_INSIDE_SQUARE_BRACKETS, tree( WORD_, "w0" ) )
        ) 
    ) ;
  }

  @Test
  public void paragraphBodyHasQuotesAndWordAndSpaceAndQuotes()
      throws RecognitionException
  {
    PARSERMETHOD_PARAGRAPH.createTree( "\"w0\"w2 \"w3\"" );
  }

  @Test
  public void paragraphBodyHasQuotesAndPunctuationSignsAndWordsInTheMiddle1()
      throws RecognitionException
  {
    PARSERMETHOD_PARAGRAPH.createTree( "\"w00\" w01 w02 \" w03 w04 ! \"." );
  }

  @Test
  public void paragraphBodyHasQuotesAndPunctuationSignsAndWordsInTheMiddle2()
      throws RecognitionException
  {
    PARSERMETHOD_PARAGRAPH.createTree( "w10 \"w11\" \"w12\", \"w13\"" );
  }

  @Test
  public void paragraphBodyHasQuotesAndPunctuationSignsAndWordsInTheMiddle3()
      throws RecognitionException
  {
    PARSERMETHOD_PARAGRAPH.createTree( "\"w20 w21... w22\" !" );
  }

  @Test
  public void paragraphBodyHasQuotesAndParenthesisAndPunctuationSignsAndWordsInTheMiddle()
      throws RecognitionException
  {
    PARSERMETHOD_PARAGRAPH.createTree( "\"p00 (w01) w02.\" w04 (w05 \"w06 (w07)\".)." );
  }

  @Test
  public void
  paragraphBodyHasQuotesAndParenthesisAndBracketsAndPunctuationSignsAndWordsInTheMiddle()
      throws RecognitionException
  {
    PARSERMETHOD_PARAGRAPH.createTree( "\"p00 (w01) w02.\"w04(w05 \"[w06] (w07)\".)." );
  }

  @Test
  public void paragraphBodyHasWordThenInterpolatedClauseThenFullStop()
      throws RecognitionException
  {
    PARSERMETHOD_PARAGRAPH.createTree( "p10 -- w11 w12 --." );
  }

  @Test
  public void paragraphBodyHasWordThenInterpolatedClauseSilentEndThenFullStop()
      throws RecognitionException
  {
    PARSERMETHOD_PARAGRAPH.createTree( "p20 -- w21 w22 -_." );
  }

  @Test
  public void paragraphBodyIsQuoteWithWordThenParenthesis() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.createTree( "\"w0 (w1)\"" );
  }

  @Test
  public void paragraphBodyIsNestingQuoteAndParenthesisAndEmphasis() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.createTree( "\"w0 (w1 //w2//)\"" );
  }

  @Test
  public void paragraphBodyIsNestingQuoteAndParenthesisAndEmphasisAndParenthesisAgain()
      throws RecognitionException
  {
    PARSERMETHOD_PARAGRAPH.createTree( "\"w0 (w1 //w2 (w3)//)\"" );
  }

  @Test
  public void
  paragraphBodyIsNestingQuoteAndParenthesisAndInterpolatedClauseAndParenthesisAgainAndBrackets()
      throws RecognitionException
  {
    PARSERMETHOD_PARAGRAPH.createTree( "\"(w0 -- w1 (w2 [w3]) --)\"" );
  }

  @Test
  public void paragraphBodyIsNestingEmphasisAndParenthesis() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.createTree( "//w0 (w1)//." );
  }

  @Test
  public void paragraphBodyIsNestingEmphasisAndParenthesisAndQuotesAndHasQuestionMarkAtTheEnd()
      throws RecognitionException
  {
    PARSERMETHOD_PARAGRAPH.createTree( "//w0 (w1, \"w2\")// ?" );
  }

  @Test
  public void paragraphBodyIsParenthesisWithWordThenExclamationMark() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.createTree( "(w0 !)" );
  }

  @Test
  public void paragraphBodyIsParenthesisWithWordAndQuotesAndEllipsisInside()
      throws RecognitionException
  {
    PARSERMETHOD_PARAGRAPH.createTree( "(w0 \"w1\"...)" );
  }

  @Test
  public void
  paragraphBodyHasNestingParenthesisAndQuoteEmphasisThenSemiColonAndWordAndExclamationMark()
      throws RecognitionException
  {
    PARSERMETHOD_PARAGRAPH.createTree( "(w0 \"w1 //w2//\") : w3 !" );
  }

  @Test
  public void
  paragraphBodyHasQuoteThenParenthesisThenEmphasisThenInterpolatedClauseThenBracketsNoSpace()
      throws RecognitionException
  {
    PARSERMETHOD_PARAGRAPH.createTree( "\"w00\"(w01)//w02//--w03--[w04]" );
  }

  @Test
  public void
  paragraphBodyIsNestingEmphasisAndParenthesisAndInterpolatedClauseAndQuotesOnSeveralLines()
      throws RecognitionException
  {
    PARSERMETHOD_PARAGRAPH.createTree( 
        "//w1" + BREAK +
        "(w2 " + BREAK +
        "-- w3  " + BREAK +
        "\"w4 " + BREAK +
        "w5\"--)//." 
    );
  }


  @Test
  public void partIsChapterThenSectionThenSingleWordParagraph() throws RecognitionException {
    PARSERMETHOD_PART.checkTreeAfterSeparatorRemoval( 
        "== c0" + BREAK +
        BREAK +
        "=== s0" + BREAK +
        BREAK +
        "p0", 
        tree(
            PART,
            tree(
                LEVEL_INTRODUCER_,
                tree( LEVEL_INTRODUCER_INDENT_, "=="),
                tree( LEVEL_TITLE, tree( WORD_, "c0" ) )
            ),
            tree(
                LEVEL_INTRODUCER_,
                tree( LEVEL_INTRODUCER_INDENT_, "===" ),
                tree( LEVEL_TITLE, tree( WORD_, "s0" ) )
            ),
            tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "p0" ) )
        ) 
    ) ;
  }

  @Test
  public void partIsAnonymousSectionsWithLeadingBreaks() throws RecognitionException {
    PARSERMETHOD_PART.checkTreeAfterSeparatorRemoval( 
        BREAK +
        BREAK +
        "===" + BREAK +
        BREAK +
        "p0" + BREAK +
        BREAK +
        "===" + BREAK +
        BREAK +
        "p1", tree( PART,
            tree( LEVEL_INTRODUCER_, tree( LEVEL_INTRODUCER_INDENT_, "===" ) ),
            tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "p0" ) ),
            tree( LEVEL_INTRODUCER_, tree( LEVEL_INTRODUCER_INDENT_, "===" ) ),
            tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "p1" ) )
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
    PARSERMETHOD_PART.createTree( 
        "===" + BREAK +
        BREAK +
        " lobs " 
    );
  }

  /**
   * Get sure of what we get because {@link novelang.hierarchy.UrlMangler} relies on this.
   */
  @Test
  public void partHasCorrectSeparatorsBetweenSectionIntroducerAndParagraph1() 
      throws RecognitionException 
  {
    PARSERMETHOD_PART.checkTree(  
        "== t" + BREAK +
        BREAK +
        "  \"name\" " + BREAK +
        "http://foo.com",
        tree( 
            PART,
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
   * Get sure of what we get because {@link novelang.hierarchy.UrlMangler} relies on this.
   */
  @Test
  public void partHasCorrectSeparatorsBetweenSectionIntroducerAndParagraph2() 
      throws RecognitionException 
  {
    PARSERMETHOD_PART.checkTree(  
        "p" + BREAK +
        BREAK +
        "== t" + BREAK +
        BREAK +
        "  \"name\" " + BREAK +
        "http://foo.com",
        tree( 
            PART,
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

  @Test
  public void chapterIsAnonymousWithSimpleSectionContainingWordsWithPunctuationSigns1()
      throws RecognitionException
  {
    PARSERMETHOD_CHAPTER.createTree( 
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
    PARSERMETHOD_CHAPTER.createTree( 
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
    PARSERMETHOD_CHAPTER.createTree( 
        "==" + BREAK +
        BREAK +
        "===" + BREAK +
        BREAK +
        "http://google.com" 
    );
  }

  @Test
  public void urlHttpGoogleDotCom() throws RecognitionException {
    PARSERMETHOD_URL.checkTreeAfterSeparatorRemoval( 
        "http://google.com", 
        tree( URL_LITERAL, "http://google.com" ) 
    ) ;
  }

  @Test
  public void urlHttpLocalhost() throws RecognitionException {
    PARSERMETHOD_URL.checkTreeAfterSeparatorRemoval( 
        "http://localhost", 
        tree( URL_LITERAL, "http://localhost" ) 
    ) ;
  }

  @Test
  public void urlHttpLocalhost8080() throws RecognitionException {
    PARSERMETHOD_URL.checkTreeAfterSeparatorRemoval( 
        "http://localhost:8080", 
        tree( URL_LITERAL, "http://localhost:8080" ) 
    ) ;
  }

  @Test
  public void urlFileWithHyphenMinus() throws RecognitionException {
    PARSERMETHOD_URL.checkTreeAfterSeparatorRemoval( 
        "file:/path/to-file.ext", 
        tree( URL_LITERAL, "file:/path/to-file.ext" ) 
    ) ;
  }

  @Test
  public void urlFileWithHyphenMinusNoPath() throws RecognitionException {
    PARSERMETHOD_URL.checkTreeAfterSeparatorRemoval( 
        "file:my-file.ext", 
        tree( URL_LITERAL, "file:my-file.ext" ) 
    ) ;
  }

  @Test
  public void urlHttpGoogleQuery() throws RecognitionException {
    PARSERMETHOD_URL.checkTreeAfterSeparatorRemoval( 
        "http://www.google.com/search?q=url%20specification&sourceid=mozilla2&ie=utf-8&oe=utf-8", 
        tree(
            URL_LITERAL,
            "http://www.google.com/search?q=url%20specification&sourceid=mozilla2&ie=utf-8&oe=utf-8"
        ) 
    ) ;
  }

  @Test
  public void urlFilePathFileDotNlp() throws RecognitionException {
    PARSERMETHOD_URL.checkTreeAfterSeparatorRemoval( 
        "file:/path/file.ppp", 
        tree( URL_LITERAL, "file:/path/file.ppp" ) 
    ) ;
  }


  @Test
  public void urlWithTilde() throws RecognitionException {
    PARSERMETHOD_URL.checkTreeAfterSeparatorRemoval( 
        "http://domain.org/path/file~tilde#anchor", 
        tree(
            URL_LITERAL,
            "http://domain.org/path/file~tilde#anchor"
        ) 
    ) ;
  }
  
  @Test
  public void urlWithSolidusInParameters() throws RecognitionException {
    PARSERMETHOD_URL.checkTreeAfterSeparatorRemoval(
        "http://foo.net/resources?x/y",
        tree(
            URL_LITERAL,
            "http://foo.net/resources?x/y"
        )
    ) ;
  }

  @Test
  public void embeddedListItemMinimum() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTree(
        "- w",
        tree(
            PARAGRAPH_REGULAR,
            tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "w" ) )
        )
    ) ;
  }

  /**
   * Was a bug.
   */
  @Test
  public void embeddedListItemApostropheAndDot() throws RecognitionException {
    PARSERMETHOD_PART.checkTree(
        "- y'z.",
        tree(
            PART,
            tree( PARAGRAPH_REGULAR,
                tree(
                    EMBEDDED_LIST_ITEM_WITH_HYPHEN_,
                    tree( WORD_, "y" ),
                    tree( APOSTROPHE_WORDMATE, "'" ),
                    tree( WORD_, "z" ),
                    tree( PUNCTUATION_SIGN, tree( SIGN_FULLSTOP, "." ) )
                )
            )
        )
    ) ;
  }

  @Test @Ignore
  public void embeddedListItemInsideParenthesis() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTree(
        "(" + BREAK +
        "- w" + BREAK +
        "- x" + BREAK +
        ")"
        ,
        tree(
            PARAGRAPH_REGULAR,
            tree(
                BLOCK_INSIDE_PARENTHESIS,
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "w" ) ),
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "x" ) )
            )
        )
    ); ;
  }

  @Test
  public void severalEmbeddedListItems() throws RecognitionException {
    PARSERMETHOD_PARAGRAPH.checkTree(
        "- w1" + BREAK +
        "  - w2" + BREAK,
        tree(
            PARAGRAPH_REGULAR,
            tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "w1" ) ),
            tree( LINE_BREAK_ ),
            tree( WHITESPACE_, "  " ),
            tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "w2" ) )
        )
    ) ;
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
  public void cellRowSequence1x1() throws RecognitionException {
    PARSERMETHOD_CELL_ROW_SEQUENCE.checkTreeAfterSeparatorRemoval(
        "| x |",
        tree(
            CELL_ROWS_WITH_VERTICAL_LINE,
            tree( 
                CELL_ROW, 
                tree( CELL, tree( WORD_, "x" ) ) 
            ) 
        )
    ) ;
  }
  
  @Test
  public void cellRowSequence1x1ContainsImage() throws RecognitionException {
    PARSERMETHOD_CELL_ROW_SEQUENCE.checkTreeAfterSeparatorRemoval(
        "| /foo.jpg |",
        tree(
            CELL_ROWS_WITH_VERTICAL_LINE,
            tree(
                CELL_ROW,
                tree( CELL, tree( RASTER_IMAGE, tree( RESOURCE_LOCATION, "/foo.jpg" ) ) )
            )
        )
    ) ;
  }

  @Test
  public void cellRowSequence2x2() throws RecognitionException {
    PARSERMETHOD_CELL_ROW_SEQUENCE.checkTreeAfterSeparatorRemoval(
        "| a | b   |" + BREAK +
        "|c  | d e |",
        tree(
            CELL_ROWS_WITH_VERTICAL_LINE,
            tree( 
                CELL_ROW, 
                tree( CELL, tree( WORD_, "a" ) ), 
                tree( CELL, tree( WORD_, "b" ) ) 
            ),
            tree( 
                CELL_ROW, 
                tree( CELL, tree( WORD_, "c" ) ),
                tree( CELL, tree( WORD_, "d" ), tree( WORD_, "e" ) )
            ) 
        )
    ) ;
  }
   

  @Test
  public void rasterImageIsAbsoluteFile() throws RecognitionException {
    PARSERMETHOD_EMBEDDABLE_RESOURCE.checkTreeAfterSeparatorRemoval(
        "/foo.jpg",
        tree(
            RASTER_IMAGE,
            tree( RESOURCE_LOCATION, tree( "/foo.jpg" ) )
        )
    ) ;
  }

  @Test
  public void rasterImageInAbsoluteSubdirectory() throws RecognitionException {
    PARSERMETHOD_EMBEDDABLE_RESOURCE.checkTreeAfterSeparatorRemoval(
        "/foo/bar.jpg",
        tree(
            RASTER_IMAGE,
            tree( RESOURCE_LOCATION, tree( "/foo/bar.jpg" ) ) 
        )
    ) ;
  }

  @Test
  public void rasterImageIsRelativeFile() throws RecognitionException {
    PARSERMETHOD_EMBEDDABLE_RESOURCE.checkTreeAfterSeparatorRemoval(
        "./bar.jpg",
        tree(
            RASTER_IMAGE,
            tree( RESOURCE_LOCATION, tree( "./bar.jpg" ) ) 
        )
    ) ;
  }

  @Test
  public void rasterImageIsRelativeFileInSubdirectory() throws RecognitionException {
    PARSERMETHOD_EMBEDDABLE_RESOURCE.checkTreeAfterSeparatorRemoval(
        "./foo/bar.jpg",
        tree(
            RASTER_IMAGE,
            tree( RESOURCE_LOCATION, tree( "./foo/bar.jpg" ) )
        )
    ) ;
  }

  @Test
  public void rasterImageIsInSuperdirectory() throws RecognitionException {
    PARSERMETHOD_EMBEDDABLE_RESOURCE.checkTreeAfterSeparatorRemoval(
        "../bar.jpg",
        tree(
            RASTER_IMAGE,
            tree( RESOURCE_LOCATION, tree( "../bar.jpg" ) ) 
        )
    ) ;
  }

  @Test
  public void rasterImageIsInSuperdirectory2() throws RecognitionException {
    PARSERMETHOD_EMBEDDABLE_RESOURCE.checkTreeAfterSeparatorRemoval(
        "../../bar.jpg",
        tree(
            RASTER_IMAGE,
            tree( RESOURCE_LOCATION, tree( "../../bar.jpg" ) )
        )
    ) ;
  }

  @Test
  public void rasterImageIsInSuperdirectory3() throws RecognitionException {
    PARSERMETHOD_EMBEDDABLE_RESOURCE.checkTreeAfterSeparatorRemoval(
        "../../../bar.jpg",
        tree(
            RASTER_IMAGE,
            tree( RESOURCE_LOCATION, tree( "../../../bar.jpg" ) ) 
        )
    ) ;
  }

  @Test
  public void rasterImageHasVariousCharactersInItsPath() throws RecognitionException {
    PARSERMETHOD_EMBEDDABLE_RESOURCE.checkTreeAfterSeparatorRemoval(
        "/f.o-o/b=a_r.jpg",
        tree(
            RASTER_IMAGE,
            tree( RESOURCE_LOCATION, tree( "/f.o-o/b=a_r.jpg" ) )
        )
    ) ;
  }

  @Test
  public void badlyFormedRasterImage1() throws RecognitionException {
    PARSERMETHOD_EMBEDDABLE_RESOURCE.checkFails(
        "/.foo.unknown"
    ) ;
  }

  @Test
  public void badlyFormedRasterImage2() throws RecognitionException {
    PARSERMETHOD_EMBEDDABLE_RESOURCE.checkFails(
        "/.foo.jpg"
    ) ;
  }


}

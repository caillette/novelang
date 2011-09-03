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
import org.novelang.parser.NodeKind;

import static org.novelang.parser.NodeKind.*;
import static org.novelang.parser.antlr.AntlrTestHelper.BREAK;
import static org.novelang.parser.antlr.TreeFixture.tree;

/**
 * Tests for parsing of a whole Novella.
 *
 * @author Laurent Caillette
 */
public class NovellaParsingTest {

  @Test
  public void partIsJustImage() throws RecognitionException {
    PARSERMETHOD_NOVELLA.checkTreeAfterSeparatorRemoval(
      "./foo.jpg",
        tree(
            NOVELLA,
          tree( RASTER_IMAGE, tree( RESOURCE_LOCATION, tree( "./foo.jpg" ) ) )
        )
    ) ;
  }


  /**
   * An attempt to reproduce bad behavior occuring with 
   * {@link org.novelang.opus.OpusWithImagesTest#imagesInPartsWithExplicitNames}.
   */
  @SuppressWarnings( { "JavadocReference" } )
  @Test
  public void partIsTwoImages() {
    PARSERMETHOD_NOVELLA.checkTreeAfterSeparatorRemoval(
        "./y.svg" + BREAK +
        BREAK +
        "../z.jpg"
        ,
        tree(
            NOVELLA,
            tree(
                VECTOR_IMAGE,
                tree( RESOURCE_LOCATION, "./y.svg" )
            ),
            tree(
                RASTER_IMAGE,
                tree( RESOURCE_LOCATION, "../z.jpg" )
            )
        )
    ) ;
  }

  @Test
  public void partWithSeveralMultilineParagraphs() throws RecognitionException {
    PARSERMETHOD_NOVELLA.checkTreeAfterSeparatorRemoval(
        BREAK +
        "p0 w01" + BREAK +
        "w02" + BREAK +
        BREAK +
        "p1 w11" + BREAK +
        "w12", tree(
            NOVELLA,
            tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "p0" ), tree( WORD_, "w01" ), tree( WORD_, "w02" ) ),
            tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "p1" ), tree( WORD_, "w11" ), tree( WORD_, "w12" )
     ) ) ) ;
  }

  @Test
  public void partHasTrailingSpacesEverywhere() throws RecognitionException {
    PARSERMETHOD_NOVELLA.checkTreeAfterSeparatorRemoval(
        BREAK +
        "  " + BREAK +
        " p0 w01  " + BREAK +
        "w02 " + BREAK +
        "  " + BREAK +
        "p1 w11  " + BREAK +
        " w12 ", tree(
            NOVELLA,
            tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "p0" ), tree( WORD_, "w01" ), tree( WORD_, "w02" ) ),
            tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "p1" ), tree( WORD_, "w11" ), tree( WORD_, "w12" ) )
        )
    ) ;
  }



  @Test
  public void paragraphsInsideAngledBracketPairsHaveTag()
      throws RecognitionException
  {
    PARSERMETHOD_NOVELLA.checkTreeAfterSeparatorRemoval(
        "@t" + BREAK +
        "<<w" + BREAK +
        ">>",
        tree(
            NOVELLA,
            tree(
                PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS,
                tree( TAG, "t" ),
                tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "w" ) )
            )
        )
    ) ;
  }

  @Test
  public void partHasAnonymousSectionAndHasBlockquoteWithSingleParagraph()
      throws RecognitionException
  {
    PARSERMETHOD_NOVELLA.checkTreeAfterSeparatorRemoval(
        "===" + BREAK +
        BREAK +
        "<< w0 w1" + BREAK +
        ">>",
        tree(
            NOVELLA,
            tree( LEVEL_INTRODUCER_, tree( LEVEL_INTRODUCER_INDENT_, "===" ) ),
            tree(
                PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS,
                tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "w0" ), tree( WORD_, "w1" ) )
            )
        )
    ) ;
  }

  @Test
  public void blockquoteHasLinesOfLiteral()
      throws RecognitionException
  {
    PARSERMETHOD_NOVELLA.checkTreeAfterSeparatorRemoval(
        "<< " + BREAK +
        "<<< " + BREAK +
      "literal" + BREAK +
        ">>> " + BREAK +
        ">>",
        tree(
            NOVELLA,
            tree(
                PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS,
                tree( LINES_OF_LITERAL, tree( RAW_LINES, "literal" ) )
            )
        )
    ) ;
  }

  @Test
  public void paragraphInsideAngledBracketPairsHasListWithTripleHyphen()
      throws RecognitionException
  {
    PARSERMETHOD_NOVELLA.checkTreeAfterSeparatorRemoval(
        "<< " + BREAK +
        "--- Foo" + BREAK +
        ">>",
        tree(
            NOVELLA,
            tree(
                PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS,
                tree(
                    PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_,
                    tree( WORD_, "Foo" )
                )
            )
        )
    ) ;
  }

  @Test
  public void paragraphInsideAngledBracketPairsHasListWithDoubleHyphenAndPlusSign()
      throws RecognitionException
  {
    PARSERMETHOD_NOVELLA.checkTreeAfterSeparatorRemoval(
        "<< " + BREAK +
        "--# Foo" + BREAK +
        ">>",
        tree(
            NOVELLA,
            tree(
                PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS,
                tree(
                    PARAGRAPH_AS_LIST_ITEM_WITH_DOUBLE_HYPHEN_AND_NUMBER_SIGN,
                    tree( WORD_, "Foo" )
                )
            )
        )
    ) ;
  }

  @Test
  public void partIsSectionThenParagraphThenBlockquoteThenParagraph()
      throws RecognitionException
  {
    PARSERMETHOD_NOVELLA.checkTreeAfterSeparatorRemoval(
        "===" + BREAK +
        BREAK +
        "p0" + BREAK +
        BREAK +
        "<< w0" + BREAK +
        ">>" + BREAK +
        BREAK +
        "p1",
        tree( NOVELLA,
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
  public void partIsChapterThenSectionThenSingleWordParagraph() throws RecognitionException {
    PARSERMETHOD_NOVELLA.checkTreeAfterSeparatorRemoval(
        "== c0" + BREAK +
        BREAK +
        "=== s0" + BREAK +
        BREAK +
        "p0",
        tree(
            NOVELLA,
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
  public void partIsLevelWithJustAnEllipsis() throws RecognitionException {
    PARSERMETHOD_NOVELLA.checkTreeAfterSeparatorRemoval(
        "== ...",
        tree(
            NOVELLA,
            tree(
                LEVEL_INTRODUCER_,
                tree( LEVEL_INTRODUCER_INDENT_, "=="),
                tree( LEVEL_TITLE, tree( PUNCTUATION_SIGN, tree( SIGN_ELLIPSIS, "..." ) ) )
            )
        )
    ) ;
  }

  @Test
  public void partIsAnonymousSectionsWithLeadingBreaks() throws RecognitionException {
    PARSERMETHOD_NOVELLA.checkTreeAfterSeparatorRemoval(
        BREAK +
        BREAK +
        "===" + BREAK +
        BREAK +
        "p0" + BREAK +
        BREAK +
        "===" + BREAK +
        BREAK +
        "p1", tree( NOVELLA,
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
    PARSERMETHOD_NOVELLA.createTree(
        "===" + BREAK +
        BREAK +
        " lobs "
    );
  }

  @Test
  public void partIsBigDashedListItem() throws RecognitionException {
    PARSERMETHOD_NOVELLA.createTree( "--- w." ) ;
  }

  @Test
  public void partIsBigListItemWithColumnAndSoftInlineLiteral() throws RecognitionException {
    PARSERMETHOD_NOVELLA.createTree( "--- w : `y`" ) ;
  }

  @Test
  public void partIsBigListItemWithSoftInlineLiteral() throws RecognitionException {
    PARSERMETHOD_NOVELLA.createTree( "--- `y`" ) ;
  }

  @Test
  public void partIsDoubleQuotedWordsWithApostropheAndPeriod() throws RecognitionException {
    PARSERMETHOD_NOVELLA.createTree( "\"x'y.\"" ) ;
  }




// =======
// Fixture
// =======

  private static final ParserMethod PARSERMETHOD_NOVELLA =
      new ParserMethod( "novella" ) ;

}

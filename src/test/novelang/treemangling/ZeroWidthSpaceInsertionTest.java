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
package novelang.treemangling;

import org.junit.Test;
import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import static novelang.parser.NodeKind.*;
import novelang.parser.antlr.TreeFixture;
import static novelang.parser.antlr.TreeFixture.tree;

/**
 * Tests for {@link SeparatorsMangler}.
 *
 * @author Laurent Caillette
 */
public final class ZeroWidthSpaceInsertionTest {


// =================
// Blocks of literal
// =================  

  @Test
  public void doNothingWithZeroSpaceInsertion() {
    final SyntacticTree tree = tree(
        PARAGRAPH_REGULAR,
        WHITESPACE_,
        BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS,
        PARAGRAPH_REGULAR,
        WHITESPACE_,
        BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS
        ) ;
    verifyZeroWidthSpaceInsertionBetweenBlocksOfLiteral(
        tree,
        tree
    ) ;
  }

  @Test
  public void addOneZeroWidthSpaceForGraveAccents() {
    verifyZeroWidthSpaceInsertionBetweenBlocksOfLiteral(
        tree(
            PARAGRAPH_REGULAR,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS,
            _ZERO_WIDTH_SPACE,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS
        ),
        tree(
            PARAGRAPH_REGULAR,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS
        )
    ) ;
  }

  @Test
  public void addOneZeroWidthSpaceForGraveAccentPairs() {
    verifyZeroWidthSpaceInsertionBetweenBlocksOfLiteral(
        tree(
            PARAGRAPH_REGULAR,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            _ZERO_WIDTH_SPACE,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS
        ),
        tree(
            PARAGRAPH_REGULAR,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS
        )
    ) ;
  }

  @Test
  public void addTwoZeroWidthSpacesForGraveAccents() {
    verifyZeroWidthSpaceInsertionBetweenBlocksOfLiteral(
        tree(
            PARAGRAPH_REGULAR,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            _ZERO_WIDTH_SPACE,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            _ZERO_WIDTH_SPACE,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            _ZERO_WIDTH_SPACE,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS
        ),
        tree(
            PARAGRAPH_REGULAR,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS
        )
    ) ;
  }

  @Test
  public void addTwoZeroWidthSpacesForGraveAccentPairs() {
    verifyZeroWidthSpaceInsertionBetweenBlocksOfLiteral(
        tree(
            PARAGRAPH_REGULAR,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS,
            _ZERO_WIDTH_SPACE,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS,
            _ZERO_WIDTH_SPACE,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS,
            _ZERO_WIDTH_SPACE,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS
        ),
        tree(
            PARAGRAPH_REGULAR,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS
        )
    ) ;
  }

  @Test
  public void dontMessGraveAccentsWithGraveAccentPairs() {
    verifyZeroWidthSpaceInsertionBetweenBlocksOfLiteral(
        tree(
            PARAGRAPH_REGULAR,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS
        ),
        tree(
            PARAGRAPH_REGULAR,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS
        )
    ) ;
  }


// ==========================
// Blocks of literal and word
// ==========================

  @Test
  public void doNothingWithWords() {
    final SyntacticTree tree = tree(
        PARAGRAPH_REGULAR,
        BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
        WHITESPACE_,
        WORD_,
        WHITESPACE_,
        BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
        WHITESPACE_,
        WORD_
    );
    verifyZeroWidthSpaceInsertionBetweenWordAndBlockOfLiteral(
        tree,
        tree
    ); ;
  }

  @Test
  public void wordPreviousToGraveAccents() {
    verifyZeroWidthSpaceInsertionBetweenWordAndBlockOfLiteral(
        tree(
            PARAGRAPH_REGULAR,
            WORD_,
            _ZERO_WIDTH_SPACE,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS
        ),
        tree(
            PARAGRAPH_REGULAR,
            WORD_,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS
        )
    ) ;
  }

  @Test
  public void wordNextToGraveAccents() {
    verifyZeroWidthSpaceInsertionBetweenWordAndBlockOfLiteral(
        tree(
            PARAGRAPH_REGULAR,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            _ZERO_WIDTH_SPACE,
            WORD_
            ),
        tree(
            PARAGRAPH_REGULAR,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            WORD_
        )
    ) ;
  }

  @Test
  public void intertwinedWordsAndGraveAccents() {
    verifyZeroWidthSpaceInsertionBetweenWordAndBlockOfLiteral(
        tree(
            PARAGRAPH_REGULAR,
            WORD_,
            _ZERO_WIDTH_SPACE,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            _ZERO_WIDTH_SPACE,
            WORD_,
            _ZERO_WIDTH_SPACE,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS
        ),
        tree(
            PARAGRAPH_REGULAR,
            WORD_,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            WORD_,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS
        )
    ) ;
  }



// =======
// Fixture
// =======

  private static void verifyZeroWidthSpaceInsertionBetweenBlocksOfLiteral(
      SyntacticTree expectedTree,
      SyntacticTree actualTree
  ) {

    final Treepath< SyntacticTree > tree = Treepath.create( actualTree );
    final SyntacticTree rehierarchized = 
        SeparatorsMangler.insertZeroWidthSpaceBetweenBlocksOfLiteral( tree ).getTreeAtEnd() ;

    TreeFixture.assertEqualsWithSeparators(
        expectedTree,
        rehierarchized
    ) ;

  }


  private static void verifyZeroWidthSpaceInsertionBetweenWordAndBlockOfLiteral(
      SyntacticTree expectedTree,
      SyntacticTree actualTree
  ) {

    final Treepath< SyntacticTree > tree = Treepath.create( actualTree );
    final SyntacticTree rehierarchized =
        SeparatorsMangler.insertZeroWidthSpaceBetweenWordAndLiteral( tree ).getTreeAtEnd() ;

    TreeFixture.assertEqualsWithSeparators(
        expectedTree,
        rehierarchized
    ) ;

  }

}
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


// ========================== 
// Zero-width space insertion
// ==========================  

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
    verifyZeroWidthSpaceInsertion(
        tree,
        tree
    ) ;
  }

  @Test
  public void addOneZeroWidthSpaceForGraveAccents() {
    verifyZeroWidthSpaceInsertion(
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
    verifyZeroWidthSpaceInsertion(
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
    verifyZeroWidthSpaceInsertion(
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
    verifyZeroWidthSpaceInsertion(
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
    verifyZeroWidthSpaceInsertion(
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

  
// =======
// Fixture
// =======

  private static void verifyZeroWidthSpaceInsertion(
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

}
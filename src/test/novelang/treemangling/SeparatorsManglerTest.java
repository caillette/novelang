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
import novelang.system.LogFactory;
import novelang.system.Log;

/**
 * Tests for {@link SeparatorsMangler}.
 *
 * @author Laurent Caillette
 */
public final class SeparatorsManglerTest {


  @Test
  public void doNothing() {
    final SyntacticTree tree = tree(
        PARAGRAPH_REGULAR,
        WHITESPACE_,
        BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS,
        BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS,
        BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS
    ) ;
    verifyMandatoryWhitespaceAddition(
        tree,
        tree
    ) ;
  }

  @Test
  public void addOneMandatorySpaceForGraveAccents() {
    verifyMandatoryWhitespaceAddition(
        tree(
            PARAGRAPH_REGULAR,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS,
            _ZERO_WIDTH_SPACE,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS
        ),
        tree(
            PARAGRAPH_REGULAR,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS,
            WHITESPACE_,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS
        )
    ) ;
  }

  @Test
  public void addOneMandatorySpaceForGraveAccentPairs() {
    verifyMandatoryWhitespaceAddition(
        tree(
            PARAGRAPH_REGULAR,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            _ZERO_WIDTH_SPACE,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS
        ),
        tree(
            PARAGRAPH_REGULAR,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            WHITESPACE_,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS
        )
    ) ;
  }

  @Test
  public void addTwoMandatorySpacesForGraveAccents() {
    verifyMandatoryWhitespaceAddition(
        tree(
            PARAGRAPH_REGULAR,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            _ZERO_WIDTH_SPACE,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            _ZERO_WIDTH_SPACE,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS
        ),
        tree(
            PARAGRAPH_REGULAR,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            WHITESPACE_,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            LINE_BREAK_,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS
        )
    ) ;
  }

  @Test
  public void addTwoMandatorySpacesForGraveAccentPairs() {
    verifyMandatoryWhitespaceAddition(
        tree(
            PARAGRAPH_REGULAR,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS,
            _ZERO_WIDTH_SPACE,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS,
            _ZERO_WIDTH_SPACE,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS
        ),
        tree(
            PARAGRAPH_REGULAR,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS,
            WHITESPACE_,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS,
            LINE_BREAK_,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS
        )
    ) ;
  }

  @Test
  public void dontMessGraveAccentsWithGraveAccentPairs() {
    verifyMandatoryWhitespaceAddition(
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
            WHITESPACE_,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS,
            WHITESPACE_,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
            WHITESPACE_,
            BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS
        )
    ) ;
  }

  
// =======
// Fixture
// =======

  private static final Log LOG = LogFactory.getLog( SeparatorsManglerTest.class ) ;

  private static void verifyMandatoryWhitespaceAddition(
      SyntacticTree expectedTree,
      SyntacticTree actualTree
  ) {
    LOG.info( "Expected tree: %s", TreeFixture.asString( expectedTree ) ) ;
    final Treepath< SyntacticTree > expectedTreepath = Treepath.create( expectedTree ) ;

    final Treepath< SyntacticTree > rehierarchized =
        SeparatorsMangler.addMandatoryWhitespace( Treepath.create( actualTree ) ) ;

    TreeFixture.assertEqualsWithSeparators(
        expectedTreepath,
        rehierarchized
    ) ;

  }

}
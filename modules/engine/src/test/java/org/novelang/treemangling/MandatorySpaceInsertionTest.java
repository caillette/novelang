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
package org.novelang.treemangling;

import org.junit.Test;
import org.junit.Ignore;
import org.novelang.common.SyntacticTree;
import org.novelang.common.tree.Treepath;
import static org.novelang.parser.NodeKind.*;
import org.novelang.parser.antlr.TreeFixture;
import static org.novelang.parser.antlr.TreeFixture.tree;

/**
 * Tests for {@link SeparatorsMangler}.
 *
 * @author Laurent Caillette
 */
public final class MandatorySpaceInsertionTest {

  @Test
  public void doNothingWithMandatorySpaceInsertionBefore() {
    final SyntacticTree tree = tree(
        PARAGRAPH_REGULAR,
        tree( WORD_ ),
        tree( APOSTROPHE_WORDMATE ),
        tree( WORD_ )
    ) ;
    verifyMandatoryWhitepaceInsertion(
        tree,
        tree
    ) ;
  }

  /**
   * This may never happen because of parsing rules but it's nice to check
   * {@link SeparatorsMangler}'s robustness anyways.
   */
  @Test @Ignore
  public void doNothingWithMandatorySpaceInsertionWithNoWordAround() {
    final SyntacticTree tree = tree(
        PARAGRAPH_REGULAR,
        tree( WORD_ ),
        tree( WHITESPACE_ ),
        tree( APOSTROPHE_WORDMATE ),
        tree( WHITESPACE_ ),
        tree( WORD_ )
        ) ;
    verifyMandatoryWhitepaceInsertion(
        tree,
        tree
    ) ;
  }

  @Test
  public void doNothingWithMandatorySpaceInsertionAfter() {
    final SyntacticTree tree = tree(
        PARAGRAPH_REGULAR,
        tree( WORD_ ),
        tree( APOSTROPHE_WORDMATE ),
        tree( WORD_ )
    ) ;
    verifyMandatoryWhitepaceInsertion(
        tree,
        tree
    ); ;
  }

  @Test
  public void addMandatorySpaceBeforeApostrohpe() {
    verifyMandatoryWhitepaceInsertion(
        tree(
            PARAGRAPH_REGULAR,
            tree( WORD_ ),
            tree( WHITESPACE_),
            tree( _PRESERVED_WHITESPACE ),
            tree( APOSTROPHE_WORDMATE ),
            tree( WORD_ )
            ),
        tree(
            PARAGRAPH_REGULAR,
            tree( WORD_ ),
            tree( WHITESPACE_),
            tree( APOSTROPHE_WORDMATE ),
            tree( WORD_ )
        )
    ) ;
  }

  @Test
  public void addMandatorySpaceAfterApostrohpe() {
    verifyMandatoryWhitepaceInsertion(
        tree(
            PARAGRAPH_REGULAR,
            tree( WORD_ ),
            tree( APOSTROPHE_WORDMATE ),
            tree( _PRESERVED_WHITESPACE ),
            tree( WHITESPACE_),
            tree( WORD_ )
            ),
        tree(
            PARAGRAPH_REGULAR,
            tree( WORD_ ),
            tree( APOSTROPHE_WORDMATE ),
            tree( WHITESPACE_),
            tree( WORD_ )
        )
    ); ;
  }




// =======
// Fixture
// =======

  private static void verifyMandatoryWhitepaceInsertion(
      final SyntacticTree expectedTree,
      final SyntacticTree actualTree
  ) {
    final Treepath< SyntacticTree > tree = Treepath.create( actualTree );
    final SyntacticTree rehierarchized =
        SeparatorsMangler.insertMandatoryWhitespaceNearApostrophe( tree ).getTreeAtEnd() ;

    TreeFixture.assertEqualsWithSeparators(
        expectedTree,
        rehierarchized
    ) ;
  }

}
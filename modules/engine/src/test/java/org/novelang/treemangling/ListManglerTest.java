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
package org.novelang.treemangling;

import org.junit.Test;
import static org.novelang.parser.NodeKind.*;
import static org.novelang.parser.antlr.TreeFixture.tree;

import org.novelang.common.SyntacticTree;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;

/**
 * Tests for {@link org.novelang.treemangling.ListMangler}.
 *
 * @author Laurent Caillette
 */
public class ListManglerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger( ListManglerTest.class ) ;



  @Test
  public void mixedLists() {
    final SyntacticTree expected = tree(
        NOVELLA,
        tree( PARAGRAPH_REGULAR ),
        tree(
            _LIST_WITH_TRIPLE_HYPHEN,
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ )
        ),
        tree(
            _LIST_WITH_DOUBLE_HYPHEN_AND_NUMBER_SIGN,
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_DOUBLE_HYPHEN_AND_NUMBER_SIGN ),
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_DOUBLE_HYPHEN_AND_NUMBER_SIGN )
        ),
        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
    ) ;

    final SyntacticTree toBeRehierarchized = tree(
        NOVELLA,
        tree( PARAGRAPH_REGULAR ),
        tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
        tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
        tree( PARAGRAPH_AS_LIST_ITEM_WITH_DOUBLE_HYPHEN_AND_NUMBER_SIGN ),
        tree( PARAGRAPH_AS_LIST_ITEM_WITH_DOUBLE_HYPHEN_AND_NUMBER_SIGN ),
        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
    ) ;
    AbstractListManglerTest.verifyRehierarchizeList( expected, toBeRehierarchized ) ;
  }




// =======
// Fixture
// =======




}
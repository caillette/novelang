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
import novelang.system.LogFactory;
import novelang.system.Log;
import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import static novelang.parser.NodeKind.*;
import novelang.parser.antlr.TreeFixture;
import static novelang.parser.antlr.TreeFixture.tree;

/**
 * Tests for {@link novelang.treemangling.ListMangler}.
 *
 * @author Laurent Caillette
 */
public class ListManglerTest {

  private static final Log LOG = LogFactory.getLog( ListManglerTest.class ) ;

  @Test
  public void doNothingWhenNothingToDo() {
    final SyntacticTree tree = tree(
        NOVELLA,
        tree( PARAGRAPH_REGULAR ),
        tree( PARAGRAPH_REGULAR )
    );
    verifyRehierarchizeList(
        tree,
        tree
    ) ;
  }


  @Test
  public void aggregateList() {
    final SyntacticTree expected = tree(
        NOVELLA,
        tree( PARAGRAPH_REGULAR ),
        tree(
            _LIST_WITH_TRIPLE_HYPHEN,
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ )
        ),
        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
    ) ;

    final SyntacticTree toBeRehierarchized = tree(
        NOVELLA,
        tree( PARAGRAPH_REGULAR ),
        tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
        tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
    ) ;
    verifyRehierarchizeList( expected, toBeRehierarchized ) ;
  }

  @Test
  public void aggregateListInsideLevelIntroducer() {
    final SyntacticTree expected = tree(
        NOVELLA,
        tree( LEVEL_INTRODUCER_,
          tree( PARAGRAPH_REGULAR ),
          tree(
              _LIST_WITH_TRIPLE_HYPHEN,
              tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
              tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ )
          ),
          tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
        ),
        tree( LEVEL_INTRODUCER_, tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS, "" ) )
    ) ;

    final SyntacticTree toBeRehierarchized = tree(
        NOVELLA,
        tree(
            LEVEL_INTRODUCER_,
            tree( PARAGRAPH_REGULAR ),
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
            tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
        ),
        tree(
            LEVEL_INTRODUCER_,
            tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS, "" )
        )
    ) ;
    verifyRehierarchizeList( expected, toBeRehierarchized ) ;
  }

  @Test
  public void aggregateListInsidePlainLevel() {
    final SyntacticTree expected = tree(
        NOVELLA,
        tree( _LEVEL,
          tree( PARAGRAPH_REGULAR ),
          tree(
              _LIST_WITH_TRIPLE_HYPHEN,
              tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
              tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ )
          ),
          tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
        ),
        tree( _LEVEL, tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS, "" ) )
    ) ;

    final SyntacticTree toBeRehierarchized = tree(
        NOVELLA,
        tree(
            _LEVEL,
            tree( PARAGRAPH_REGULAR ),
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
            tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
        ),
        tree(
            _LEVEL,
            tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS, "" )
        )
    ) ;
    verifyRehierarchizeList( expected, toBeRehierarchized ) ;
  }


  @Test
  public void aggregateSeveralLists() {
    final SyntacticTree expected = tree(
        NOVELLA,
        tree( PARAGRAPH_REGULAR ),
        tree(
            _LIST_WITH_TRIPLE_HYPHEN,
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ )
        ),
        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS ),
        tree(
            _LIST_WITH_TRIPLE_HYPHEN,
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ )
        ),
        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS, "" )

    ) ;
    final SyntacticTree toBeRehierarchized = tree(
        NOVELLA,
        tree( PARAGRAPH_REGULAR ),
        tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
        tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS ),
        tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
        tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
        tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS, "" )
    ) ;
    verifyRehierarchizeList( expected, toBeRehierarchized ) ;
  }


// =======
// Fixture
// =======



  private static void verifyRehierarchizeList(
      final SyntacticTree expectedTree,
      final SyntacticTree flatTree
  ) {
    LOG.info( "Flat tree: %s", TreeFixture.asString( flatTree ) ) ;
    LOG.info( "Expected tree: %s", TreeFixture.asString( expectedTree ) ) ;
    final Treepath< SyntacticTree > expectedTreepath = Treepath.create( expectedTree ) ;
    final Treepath< SyntacticTree > flatTreepath = Treepath.create( flatTree ) ;

    final Treepath rehierarchized = ListMangler.rehierarchizeLists( flatTreepath ) ;

    TreeFixture.assertEqualsWithSeparators(
        expectedTreepath,
        rehierarchized
    ) ;


  }

}
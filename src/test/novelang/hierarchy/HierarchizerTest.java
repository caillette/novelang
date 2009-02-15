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
package novelang.hierarchy;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import static novelang.parser.NodeKind.*;
import novelang.parser.antlr.TreeFixture;
import static novelang.parser.antlr.TreeFixture.tree;

/**
 * Tests for {@link Hierarchizer}.
 * 
 * @author Laurent Caillette
 */
public class HierarchizerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger( HierarchizerTest.class ) ;

  @Test
  public void doNothingWhenNothingToDo() {
    final SyntacticTree tree = tree(
        PART,
        tree( PARAGRAPH_REGULAR ),
        tree( PARAGRAPH_REGULAR )
    );
    verifyRehierarchizeLevels(
        tree,
        tree
    ) ;
  }

  @Test
  public void justLevel1() {
    verifyRehierarchizeLevels(
        tree(
            PART,
            tree(
                _LEVEL,
                tree( PARAGRAPH_REGULAR )
            )
        ),
        tree(
            PART,
            tree( LEVEL_INTRODUCER_, tree( LEVEL_INTRODUCER_INDENT_, "==" ) ),
            tree( PARAGRAPH_REGULAR )
        )
    ) ;
  }

  @Test
  public void keepTitle() {
    verifyRehierarchizeLevels(
        tree(
            PART,
            tree(
                _LEVEL,
                tree( LEVEL_TITLE, tree( WORD_ ) ) ,
                tree( PARAGRAPH_REGULAR )
            )
        ),
        tree(
            PART,
            tree(
                LEVEL_INTRODUCER_,
                tree( LEVEL_INTRODUCER_INDENT_, "==" ),
                tree( LEVEL_TITLE, tree( WORD_ ) )
            ),
            tree( PARAGRAPH_REGULAR )
        )
    ) ;
  }

  @Test
  public void somethingPrecedingLevel1() {
    verifyRehierarchizeLevels(
        tree(
            PART,
            tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS ),
            tree(
                _LEVEL,
                tree( PARAGRAPH_REGULAR )
            )
        ),
        tree(
            PART,
            tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS ),
            tree( LEVEL_INTRODUCER_, tree( LEVEL_INTRODUCER_INDENT_, "==" ) ),
            tree( PARAGRAPH_REGULAR )
        )
    ) ;
  }

  @Test
  public void level1NestingLevel2() {
    verifyRehierarchizeLevels(
        tree(
            PART,
            tree(
                _LEVEL,
                tree( PARAGRAPH_REGULAR ),
                tree(
                    _LEVEL,
                    tree( _PARAGRAPH_AS_LIST_ITEM )
                )
            )
        ),
        tree(
            PART,
            tree( LEVEL_INTRODUCER_, tree( LEVEL_INTRODUCER_INDENT_, "==" ) ),
            tree( PARAGRAPH_REGULAR ),
            tree( LEVEL_INTRODUCER_, tree( LEVEL_INTRODUCER_INDENT_, "===" ) ),
            tree( _PARAGRAPH_AS_LIST_ITEM )
        )
    ) ;
  }

  @Test(expected = IllegalArgumentException.class )
  public void badDepthOrder() {
    rehierarchizeLevels(
        tree(
            PART,
            tree( LEVEL_INTRODUCER_, tree( LEVEL_INTRODUCER_INDENT_, "===" ) ),
            tree( _PARAGRAPH_AS_LIST_ITEM ),
            tree( LEVEL_INTRODUCER_, tree( LEVEL_INTRODUCER_INDENT_, "==" ) ),
            tree( PARAGRAPH_REGULAR )
        )
    ) ;
  }

  @Test
  public void level1NestingLevel2NestingLevel3() {
    verifyRehierarchizeLevels(
        tree(
            PART,
            tree(
                _LEVEL,
                tree( PARAGRAPH_REGULAR ),
                tree(
                    _LEVEL,
                    tree( _PARAGRAPH_AS_LIST_ITEM ),
                    tree(
                        _LEVEL,
                        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
                    )
                )
            )
        ),
        tree(
            PART,
            tree( LEVEL_INTRODUCER_, tree( LEVEL_INTRODUCER_INDENT_, "==" ) ),
            tree( PARAGRAPH_REGULAR ),
            tree( LEVEL_INTRODUCER_, tree( LEVEL_INTRODUCER_INDENT_, "===" ) ),
            tree( _PARAGRAPH_AS_LIST_ITEM ),
            tree( LEVEL_INTRODUCER_, tree( LEVEL_INTRODUCER_INDENT_, "====" ) ),
            tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
        )
    ) ;
  }

  @Test
  public void level1NestingLevel2ThenLevel1Again() {
    verifyRehierarchizeLevels(
        tree(
            PART,
            tree(
                _LEVEL,
                tree( PARAGRAPH_REGULAR ),
                tree(
                    _LEVEL,
                    tree( _PARAGRAPH_AS_LIST_ITEM )
                )
            ),
            tree(
                _LEVEL,
                tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
            )
        ),
        tree(
            PART,
            tree( LEVEL_INTRODUCER_, tree( LEVEL_INTRODUCER_INDENT_, "==" ) ),
            tree( PARAGRAPH_REGULAR ),
            tree( LEVEL_INTRODUCER_, tree( LEVEL_INTRODUCER_INDENT_, "===" ) ),
            tree( _PARAGRAPH_AS_LIST_ITEM ),
            tree( LEVEL_INTRODUCER_, tree( LEVEL_INTRODUCER_INDENT_, "==" ) ),
            tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
        )
    ) ;
  }



  @Test
  public void aggregateList() {
    final SyntacticTree expected = tree(
        PART,
        tree( PARAGRAPH_REGULAR ),
        tree(
            _LIST_WITH_TRIPLE_HYPHEN,
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ )
        ),
        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
    ) ;

    final SyntacticTree toBeRehierarchized = tree(
        PART,
        tree( PARAGRAPH_REGULAR ),
        tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
        tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
    ) ;
    verifyRehierarchizeList( expected, toBeRehierarchized ) ;
  }

  @Test
  public void aggregateListInsideChapter() {
    final SyntacticTree expected = tree(
        PART,
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
        PART,
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
  public void aggregateSeveralLists() {
    final SyntacticTree expected = tree(
        PART,
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
        PART,
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

  private static void verifyRehierarchizeLevels(
      SyntacticTree expectedTree,
      SyntacticTree flatTree
  ) {
    LOGGER.info( "Expected tree: " + TreeFixture.asString( expectedTree ) ) ;
    final Treepath< SyntacticTree > expectedTreepath = Treepath.create( expectedTree ) ;

    final Treepath rehierarchized = rehierarchizeLevels( flatTree ) ;

    TreeFixture.assertEquals(
        expectedTreepath,
        rehierarchized
    ) ;

  }


  private static Treepath< SyntacticTree > rehierarchizeLevels(
      SyntacticTree flatTree
  ) {
    LOGGER.info( "Flat tree: " + TreeFixture.asString( flatTree ) ) ;
    final Treepath< SyntacticTree > flatTreepath = Treepath.create( flatTree ) ;

    return Hierarchizer.rehierarchizeLevels( flatTreepath ) ;

  }


  private static void verifyRehierarchizeList(
      SyntacticTree expectedTree,
      SyntacticTree flatTree
  ) {
    LOGGER.info( "Flat tree: " + TreeFixture.asString( flatTree ) ) ;
    LOGGER.info( "Expected tree: " + TreeFixture.asString( expectedTree ) ) ;
    final Treepath< SyntacticTree > expectedTreepath = Treepath.create( expectedTree ) ;
    final Treepath< SyntacticTree > flatTreepath = Treepath.create( flatTree ) ;

    final Treepath rehierarchized = Hierarchizer.rehierarchizeLists( flatTreepath ) ;

    TreeFixture.assertEquals(
        expectedTreepath,
        rehierarchized
    ) ;


  }

}

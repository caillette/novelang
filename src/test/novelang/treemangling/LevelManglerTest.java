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

import novelang.common.Location;
import novelang.system.LogFactory;
import novelang.system.Log;
import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import static novelang.parser.NodeKind.*;
import novelang.parser.antlr.TreeFixture;
import static novelang.parser.antlr.TreeFixture.tree;

import junit.framework.AssertionFailedError;

/**
 * Tests for {@link LevelMangler}.
 * 
 * @author Laurent Caillette
 */
public class LevelManglerTest {

  private static final Log LOG = LogFactory.getLog( LevelManglerTest.class ) ;

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
  public void keepTag() {
    verifyRehierarchizeLevels(
        tree(
            PART,
            tree(
                _LEVEL,
                tree( LEVEL_TITLE, tree( WORD_ ) ) ,
                tree( TAG, "some-tag" ) ,
                tree( PARAGRAPH_REGULAR )
            )
        ),
        tree(
            PART,
            tree(
                LEVEL_INTRODUCER_,
                tree( LEVEL_INTRODUCER_INDENT_, "==" ),
                tree( TAG, "some-tag" ),
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
  public void justLevel1WithLocation() {
    verifyRehierarchizeLevelsWithLocation(
        tree(
            PART,
            tree(
                _LEVEL,
                LOCATION_1,
                tree( PARAGRAPH_REGULAR )
            )
        ),
        tree(
            PART,
            tree(
                LEVEL_INTRODUCER_,
                LOCATION_1,
                tree( LEVEL_INTRODUCER_INDENT_, "==" )
            ),
            tree( PARAGRAPH_REGULAR )
        )
    ) ;
  }


  @Test( expected = AssertionError.class )
  public void detectLocationDifference() {
    verifyRehierarchizeLevelsWithLocation(
        tree(
            PART,
            tree(
                _LEVEL,
                LOCATION_1,
                tree( PARAGRAPH_REGULAR )
            )
        ),
        tree(
            PART,
            tree(
                LEVEL_INTRODUCER_,
                LOCATION_2, // Get sure we detect different locations.
                tree( LEVEL_INTRODUCER_INDENT_, "==" )
            ),
            tree( PARAGRAPH_REGULAR )
        )
    ) ;
  }


// =======
// Fixture
// =======

  private static final Location LOCATION_1 = new Location( "LocationOne" ) ;
  private static final Location LOCATION_2 = new Location( "LocationTwo" ) ;

  private static void verifyRehierarchizeLevelsWithLocation(
      final SyntacticTree expectedTree,
      final SyntacticTree flatTree
  ) {
    verifyRehierarchizeLevels( expectedTree, flatTree, true ) ;
  }

  private static void verifyRehierarchizeLevels(
      final SyntacticTree expectedTree,
      final SyntacticTree flatTree
  ) {
    verifyRehierarchizeLevels( expectedTree, flatTree, false ) ;
  }

  private static void verifyRehierarchizeLevels(
      final SyntacticTree expectedTree,
      final SyntacticTree flatTree,
      final boolean checkLocation

  ) {
    LOG.info( "Expected tree: %s", TreeFixture.asString( expectedTree ) ) ;
    final Treepath< SyntacticTree > expectedTreepath = Treepath.create( expectedTree ) ;

    final Treepath< SyntacticTree > rehierarchized = rehierarchizeLevels( flatTree ) ;

    TreeFixture.assertEqualsWithSeparators(
        expectedTreepath,
        rehierarchized,
        checkLocation
    ) ;

  }


  private static Treepath< SyntacticTree > rehierarchizeLevels(
      final SyntacticTree flatTree
  ) {
    LOG.info( "Flat tree: %s", TreeFixture.asString( flatTree ) ) ;
    final Treepath< SyntacticTree > flatTreepath = Treepath.create( flatTree ) ;

    return LevelMangler.rehierarchizeLevels( flatTreepath ) ;

  }



}

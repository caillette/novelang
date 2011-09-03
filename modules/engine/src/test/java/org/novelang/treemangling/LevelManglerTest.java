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
import org.novelang.common.Location;
import org.novelang.common.SyntacticTree;
import org.novelang.common.tree.Treepath;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.parser.antlr.TreeFixture;

import static org.novelang.parser.NodeKind.*;
import static org.novelang.parser.antlr.TreeFixture.tree;

/**
 * Tests for {@link LevelMangler}.
 * 
 * @author Laurent Caillette
 */
public class LevelManglerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger( LevelManglerTest.class );

  @Test
  public void doNothingWhenNothingToDo() {
    final SyntacticTree tree = tree(
        NOVELLA,
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
            NOVELLA,
            tree(
                _LEVEL,
                tree( PARAGRAPH_REGULAR )
            )
        ),
        tree(
            NOVELLA,
            tree( LEVEL_INTRODUCER_, tree( LEVEL_INTRODUCER_INDENT_, "==" ) ),
            tree( PARAGRAPH_REGULAR )
        )
    ) ;
  }

  @Test
  public void keepTitle() {
    verifyRehierarchizeLevels(
        tree(
            NOVELLA,
            tree(
                _LEVEL,
                tree( LEVEL_TITLE, tree( WORD_ ) ) ,
                tree( PARAGRAPH_REGULAR )
            )
        ),
        tree(
            NOVELLA,
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
            NOVELLA,
            tree(
                _LEVEL,
                tree( LEVEL_TITLE, tree( WORD_ ) ) ,
                tree( TAG, "some-tag" ) ,
                tree( PARAGRAPH_REGULAR )
            )
        ),
        tree(
            NOVELLA,
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
            NOVELLA,
            tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS ),
            tree(
                _LEVEL,
                tree( PARAGRAPH_REGULAR )
            )
        ),
        tree(
            NOVELLA,
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
            NOVELLA,
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
            NOVELLA,
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
            NOVELLA,
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
            NOVELLA,
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
            NOVELLA,
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
            NOVELLA,
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
            NOVELLA,
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
            NOVELLA,
            tree(
                _LEVEL,
                LOCATION_1,
                tree( PARAGRAPH_REGULAR )
            )
        ),
        tree(
            NOVELLA,
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
            NOVELLA,
            tree(
                _LEVEL,
                LOCATION_1,
                tree( PARAGRAPH_REGULAR )
            )
        ),
        tree(
            NOVELLA,
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
    LOGGER.info( "Expected tree: ", TreeFixture.asString( expectedTree ) ) ;
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
    LOGGER.info( "Flat tree: ", TreeFixture.asString( flatTree ) ) ;
    final Treepath< SyntacticTree > flatTreepath = Treepath.create( flatTree ) ;

    return LevelMangler.rehierarchizeLevels( flatTreepath ) ;

  }



}

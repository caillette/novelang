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
import org.novelang.common.SyntacticTree;
import org.novelang.common.tree.Treepath;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.parser.NodeKind;
import org.novelang.parser.antlr.TreeFixture;

import static org.novelang.parser.NodeKind.*;
import static org.novelang.parser.antlr.TreeFixture.tree;

/**
 * Tests for {@link ListMangler}.
 *
 * @author Laurent Caillette
 */
public abstract class AbstractListManglerTest {

  @Test
  public void aggregateList() {
    final SyntacticTree expected = tree(
        NOVELLA,
        tree( PARAGRAPH_REGULAR ),
        tree(
            getSyntheticToken(),
            tree( getParsedToken() ),
            tree( getParsedToken() )
        ),
        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
    ) ;

    final SyntacticTree toBeRehierarchized = tree(
        NOVELLA,
        tree( PARAGRAPH_REGULAR ),
        tree( getParsedToken() ),
        tree( getParsedToken() ),
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
              getSyntheticToken(),
              tree( getParsedToken() ),
              tree( getParsedToken() )
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
            tree( getParsedToken() ),
            tree( getParsedToken() ),
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
              getSyntheticToken(),
              tree( getParsedToken() ),
              tree( getParsedToken() )
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
            tree( getParsedToken() ),
            tree( getParsedToken() ),
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
            getSyntheticToken(),
            tree( getParsedToken() ),
            tree( getParsedToken() )
        ),
        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS ),
        tree(
            getSyntheticToken(),
            tree( getParsedToken() ),
            tree( getParsedToken() ),
            tree( getParsedToken() )
        ),
        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS, "" )

    ) ;
    final SyntacticTree toBeRehierarchized = tree(
        NOVELLA,
        tree( PARAGRAPH_REGULAR ),
        tree( getParsedToken() ),
        tree( getParsedToken() ),
        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS ),
        tree( getParsedToken() ),
        tree( getParsedToken() ),
        tree( getParsedToken() ),
        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS, "" )
    ) ;
    verifyRehierarchizeList( expected, toBeRehierarchized ) ;
  }



  @Test
  public void listInsideAngledBracketPairs() {
    final SyntacticTree expected = tree(
        NOVELLA,
        tree( _LEVEL,
          tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS,
              tree(
                  getSyntheticToken(),
                  tree( getParsedToken() ),
                  tree( getParsedToken() )
              )
          )
        )
    ) ;

    final SyntacticTree toBeRehierarchized = tree(
        NOVELLA,
        tree(
            _LEVEL,
            tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS,
                tree( getParsedToken() ),
                tree( getParsedToken() )
            )
        )
    ) ;
    verifyRehierarchizeList( expected, toBeRehierarchized ) ;
  }


// =======
// Fixture
// =======

  private static final Logger LOGGER = LoggerFactory.getLogger( AbstractListManglerTest.class ) ;


  static void verifyRehierarchizeList(
      final SyntacticTree expectedTree,
      final SyntacticTree flatTree
  ) {
    LOGGER.info( "Flat tree: ", TreeFixture.asString( flatTree ) ) ;
    LOGGER.info( "Expected tree: ", TreeFixture.asString( expectedTree ) ) ;
    final Treepath< SyntacticTree > expectedTreepath = Treepath.create( expectedTree ) ;
    final Treepath< SyntacticTree > flatTreepath = Treepath.create( flatTree ) ;

    final Treepath rehierarchized = ListMangler.rehierarchizeLists( flatTreepath ) ;

    TreeFixture.assertEqualsWithSeparators(
        expectedTreepath,
        rehierarchized
    ) ;


  }


  protected abstract NodeKind getSyntheticToken() ;

  /**
   * This token appears in expected trees because that's the
   * {@link org.novelang.rendering.GenericRenderer}
   * which finally transforms it into a token for rendition.
   * TODO: move that into the {@link org.novelang.treemangling.ListMangler}
   */
  protected abstract NodeKind getParsedToken() ;

}
/*
 * Copyright (C) 2009 Laurent Caillette
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
 * Tests for {@link org.novelang.treemangling.EmbeddedListMangler}.
 *
 * @author Laurent Caillette
 */
public abstract class AbstractEmbeddedListManglerTest {

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
  public void justOneItem() {
    verifyRehierarchizeList(
        tree(
            NOVELLA,
            tree(
                getSyntheticToken(),
                tree(
                    _EMBEDDED_LIST_ITEM,
                    tree( WORD_, "y" )
                )
            ),
            tree( WORD_, "z" )
        ),
        tree(
            NOVELLA,
            tree( getParsedToken(), tree( WORD_, "y" ) ),
            tree( LINE_BREAK_ ),
            tree( WORD_, "z" )
        )
    ) ;
  }

  @Test
  public void twoListsOfOneItemEach() {
    verifyRehierarchizeList(
        tree(
            NOVELLA,
            tree( 
                PARAGRAPH_REGULAR,
                tree(
                    getSyntheticToken(),
                    tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "x" ) )
                )
            ),
            tree( 
                PARAGRAPH_REGULAR,
                tree(
                    getSyntheticToken(),
                    tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "y" ) )
                )
            ),
            tree( PARAGRAPH_REGULAR, tree( WORD_, "z" ) )
        ),
        tree(
            NOVELLA,
            tree( 
                PARAGRAPH_REGULAR,  
                tree( getParsedToken(), tree( WORD_, "x" ) )
             ),
            tree( LINE_BREAK_ ),
            tree( 
                PARAGRAPH_REGULAR,  
                tree( getParsedToken(), tree( WORD_, "y" ) )
             ),
            tree( 
                PARAGRAPH_REGULAR,  
                tree( WORD_, "z" )
            )
        )
    ) ;
  }

  @Test
  public void twoItemsOfSameLevel() {
    verifyRehierarchizeList(
        tree(
            NOVELLA,
            tree(
                getSyntheticToken(),
                tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "y" ) ),
                tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "z" ) )
            )
        ),
        tree(
            NOVELLA,
                tree( getParsedToken(), tree( WORD_, "y" ) ),
                tree( LINE_BREAK_ ),
                tree( getParsedToken(), tree( WORD_, "z" ) )
        )
    ) ;
  }

  @Test
  public void twoItemsOfSameLevelWithIndent() {
    verifyRehierarchizeList(
        tree(
            NOVELLA,
            tree( 
                PARAGRAPH_REGULAR, 
                tree(
                    getSyntheticToken(),
                    tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "y" ) ),
                    tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "z" ) )
                )
            )
        ),
        tree(
            NOVELLA,
            tree( WHITESPACE_, "  " ),
            tree( PARAGRAPH_REGULAR, 
                tree( getParsedToken(), tree( WORD_, "y" ) ),
                tree( LINE_BREAK_ ),
                tree( WHITESPACE_, "  " ),
                tree( getParsedToken(), tree( WORD_, "z" ) )
            )
        )
    ) ;
  }

  @Test
  public void twoItemsOfDifferentLevel() {
    verifyRehierarchizeList(
        tree(
            NOVELLA,
            tree(
                getSyntheticToken(),
                tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "y" ) ),
                tree(
                    getSyntheticToken(),
                    tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "z" ) )
                )                
            )
        ),
        tree(
            NOVELLA,
                tree( getParsedToken(), tree( WORD_, "y" ) ),
                tree( LINE_BREAK_ ),
                tree( WHITESPACE_, "  " ),
                tree( getParsedToken(), tree( WORD_, "z" ) )
        )
    ) ;
  }

  @Test
  public void depth1Then2Then1() {
    verifyRehierarchizeList(
        tree(
            NOVELLA,
            tree(
                getSyntheticToken(),
                tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "x" ) ),
                tree(
                    getSyntheticToken(),
                    tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "y" ) )
                ),
                tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "z" ) )
            )
        ),
        tree(
            NOVELLA,
                tree( getParsedToken(), tree( WORD_, "x" ) ),
                tree( LINE_BREAK_ ),
                tree( WHITESPACE_, "  " ),
                tree( getParsedToken(), tree( WORD_, "y" ) ),
                tree( LINE_BREAK_ ),
                tree( getParsedToken(), tree( WORD_, "z" ) )
        )
    ) ;
  }

  @Test
  public void depth1Then2Then2Then1() {
    verifyRehierarchizeList(
        tree(
            NOVELLA,
            tree(
                getSyntheticToken(),
                tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "w" ) ),
                tree(
                    getSyntheticToken(),
                    tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "x" ) ),
                    tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "y" ) )
                ),
                tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "z" ) )
            )
        ),
        tree(
            NOVELLA,
                tree( getParsedToken(), tree( WORD_, "w" ) ),
                tree( LINE_BREAK_ ),
                tree( WHITESPACE_, "  " ),
                tree( getParsedToken(), tree( WORD_, "x" ) ),
                tree( LINE_BREAK_ ),
                tree( WHITESPACE_, "  " ),
                tree( getParsedToken(), tree( WORD_, "y" ) ),
                tree( LINE_BREAK_ ),
                tree( getParsedToken(), tree( WORD_, "z" ) )
        )
    ) ;
  }
  
  

  @Test ( expected = IllegalArgumentException.class )
  public void detectInconsistentIndent() {
    EmbeddedListMangler.rehierarchizeEmbeddedLists( Treepath.create( 
        tree(
            NOVELLA,
                tree( getParsedToken(), tree( WORD_, "x" ) ),
                tree( LINE_BREAK_ ),
                tree( WHITESPACE_, "  " ),  // indent = 2
                tree( getParsedToken(), tree( WORD_, "y" ) ),
                tree( LINE_BREAK_ ),
                tree( WHITESPACE_, " " ),   // indent = 1
                tree( getParsedToken(), tree( WORD_, "z" ) )
        )
    ) ) ;
  }
  
  

// =======  
// Fixture
// =======  
  
  private static final Logger LOGGER = LoggerFactory.getLogger( AbstractEmbeddedListManglerTest.class );
  
  private static void verifyRehierarchizeList(
      final SyntacticTree expectedTree,
      final SyntacticTree flatTree
  ) {
    LOGGER.info( "Flat tree: ", TreeFixture.asString( flatTree ) ) ;
    LOGGER.info( "Expected tree: ", TreeFixture.asString( expectedTree ) ) ;
    final Treepath< SyntacticTree > expectedTreepath = Treepath.create( expectedTree ) ;
    final Treepath< SyntacticTree > flatTreepath = Treepath.create( flatTree ) ;

    final Treepath< SyntacticTree > rehierarchized = 
        EmbeddedListMangler.rehierarchizeEmbeddedLists( flatTreepath ) ;

    TreeFixture.assertEqualsNoSeparators(
        expectedTreepath.getTreeAtEnd(), 
        rehierarchized.getTreeAtEnd()
    ) ;
  }

  
  /**
   * Originally, was {@link NodeKind#EMBEDDED_LIST_ITEM_WITH_HYPHEN_}
   */
  protected abstract NodeKind getParsedToken() ;

  /**
   * Originally, was {@link NodeKind#_EMBEDDED_LIST_WITH_HYPHEN}
   */
  protected abstract NodeKind getSyntheticToken() ;
  
}

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
package novelang.hierarchy;

import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import static novelang.parser.NodeKind.*;
import novelang.parser.antlr.TreeFixture;
import static novelang.parser.antlr.TreeFixture.tree;
import org.junit.Test;
import novelang.system.LogFactory;
import novelang.system.Log;

/**
 * Tests for {@link EmbeddedListMangler}.
 * 
 * @author Laurent Caillette
 */
public class EmbeddedListManglerTest {

  @Test
  public void doNothingWhenNothingToDo() {
    final SyntacticTree tree = tree(
        PART,
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
            PART,
            tree(
                _EMBEDDED_LIST_WITH_HYPHEN,
                tree(
                    _EMBEDDED_LIST_ITEM,
                    tree( WORD_, "y" )
                )
            ),
            tree( WORD_, "z" )
        ),
        tree(
            PART,
            tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "y" ) ),
            tree( LINE_BREAK_ ),
            tree( WORD_, "z" )
        )
    ) ;
  }

  @Test
  public void twoListsOfOneItemEach() {
    verifyRehierarchizeList(
        tree(
            PART,
            tree( 
                PARAGRAPH_REGULAR,
                tree(
                    _EMBEDDED_LIST_WITH_HYPHEN,
                    tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "x" ) )
                )
            ),
            tree( 
                PARAGRAPH_REGULAR,
                tree(
                    _EMBEDDED_LIST_WITH_HYPHEN,
                    tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "y" ) )
                )
            ),
            tree( PARAGRAPH_REGULAR, tree( WORD_, "z" ) )
        ),
        tree(
            PART,
            tree( 
                PARAGRAPH_REGULAR,  
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "x" ) )
             ),
            tree( LINE_BREAK_ ),
            tree( 
                PARAGRAPH_REGULAR,  
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "y" ) )
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
            PART,
            tree(
                _EMBEDDED_LIST_WITH_HYPHEN,
                tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "y" ) ),
                tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "z" ) )
            )
        ),
        tree(
                PART,
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "y" ) ),
                tree( LINE_BREAK_ ),
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "z" ) )
        )
    ) ;
  }

  @Test
  public void twoItemsOfSameLevelWithIndent() {
    verifyRehierarchizeList(
        tree(
            PART,
            tree( 
                PARAGRAPH_REGULAR, 
                tree(
                    _EMBEDDED_LIST_WITH_HYPHEN,
                    tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "y" ) ),
                    tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "z" ) )
                )
            )
        ),
        tree(
            PART,
            tree( WHITESPACE_, "  " ),
            tree( PARAGRAPH_REGULAR, 
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "y" ) ),
                tree( LINE_BREAK_ ),
                tree( WHITESPACE_, "  " ),
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "z" ) )
            )
        )
    ) ;
  }

  @Test
  public void twoItemsOfDifferentLevel() {
    verifyRehierarchizeList(
        tree(
            PART,
            tree(
                _EMBEDDED_LIST_WITH_HYPHEN,
                tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "y" ) ),
                tree(
                    _EMBEDDED_LIST_WITH_HYPHEN,
                    tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "z" ) )
                )                
            )
        ),
        tree(
                PART,
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "y" ) ),
                tree( LINE_BREAK_ ),
                tree( WHITESPACE_, "  " ),
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "z" ) )
        )
    ) ;
  }

  @Test
  public void depth1Then2Then1() {
    verifyRehierarchizeList(
        tree(
            PART,
            tree(
                _EMBEDDED_LIST_WITH_HYPHEN,
                tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "x" ) ),
                tree(
                    _EMBEDDED_LIST_WITH_HYPHEN,
                    tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "y" ) )
                ),
                tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "z" ) )
            )
        ),
        tree(
                PART,
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "x" ) ),
                tree( LINE_BREAK_ ),
                tree( WHITESPACE_, "  " ),
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "y" ) ),
                tree( LINE_BREAK_ ),
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "z" ) )
        )
    ) ;
  }

  @Test
  public void depth1Then2Then2Then1() {
    verifyRehierarchizeList(
        tree(
            PART,
            tree(
                _EMBEDDED_LIST_WITH_HYPHEN,
                tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "w" ) ),
                tree(
                    _EMBEDDED_LIST_WITH_HYPHEN,
                    tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "x" ) ),
                    tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "y" ) )
                ),
                tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "z" ) )
            )
        ),
        tree(
                PART,
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "w" ) ),
                tree( LINE_BREAK_ ),
                tree( WHITESPACE_, "  " ),
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "x" ) ),
                tree( LINE_BREAK_ ),
                tree( WHITESPACE_, "  " ),
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "y" ) ),
                tree( LINE_BREAK_ ),
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "z" ) )
        )
    ) ;
  }
  
  

  @Test ( expected = IllegalArgumentException.class )
  public void detectInconsistentIndent() {
    EmbeddedListMangler.rehierarchizeEmbeddedLists( Treepath.create( 
        tree(
                PART,
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "x" ) ),
                tree( LINE_BREAK_ ),
                tree( WHITESPACE_, "  " ),  // indent = 2
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "y" ) ),
                tree( LINE_BREAK_ ),
                tree( WHITESPACE_, " " ),   // indent = 1
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "z" ) )
        )
    ) ) ;
  }
  
  

// =======  
// Fixture
// =======  
  
  private static Log LOG = LogFactory.getLog( EmbeddedListMangler.class ) ;
  
  private static void verifyRehierarchizeList(
      SyntacticTree expectedTree,
      SyntacticTree flatTree
  ) {
    LOG.info( "Flat tree: %s", TreeFixture.asString( flatTree ) ) ;
    LOG.info( "Expected tree: %s", TreeFixture.asString( expectedTree ) ) ;
    final Treepath< SyntacticTree > expectedTreepath = Treepath.create( expectedTree ) ;
    final Treepath< SyntacticTree > flatTreepath = Treepath.create( flatTree ) ;

    final Treepath< SyntacticTree > rehierarchized = 
        EmbeddedListMangler.rehierarchizeEmbeddedLists( flatTreepath ) ;

    TreeFixture.assertEqualsNoSeparators(
        expectedTreepath.getTreeAtEnd(), 
        rehierarchized.getTreeAtEnd()
    ) ;
  }
  
}

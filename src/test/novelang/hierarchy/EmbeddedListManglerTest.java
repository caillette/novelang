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
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                    tree( WORD_, "w" )
                )
            ),
            tree( WORD_, "x" )
        ),
        tree(
            PART,
            tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "w" ) ),
            tree( LINE_BREAK_ ),
            tree( WHITESPACE_ ),
            tree( WORD_, "x" )
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
                tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "w" ) ),
                tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "x" ) )
            )
        ),
        tree(
                PART,
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "w" ) ),
                tree( LINE_BREAK_ ),
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "x" ) )
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
                tree( 
                    _EMBEDDED_LIST_ITEM, 
                    tree( WORD_, "w" ),
                    tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "x" ) )
                )
            )
        ),
        tree(
                PART,
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "w" ) ),
                tree( LINE_BREAK_ ),
                tree( WHITESPACE_ ),
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "x" ) )
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
                tree( 
                    _EMBEDDED_LIST_ITEM, 
                    tree( WORD_, "w" ),
                    tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "x" ) ),
                    tree( _EMBEDDED_LIST_ITEM, tree( WORD_, "y" ) )
                ),
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "z" ) )
            )
        ),
        tree(
                PART,
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "w" ) ),
                tree( LINE_BREAK_ ),
                tree( WHITESPACE_ ),
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "x" ) ),
                tree( LINE_BREAK_ ),
                tree( WHITESPACE_ ),
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "y" ) ),
                tree( LINE_BREAK_ ),
                tree( EMBEDDED_LIST_ITEM_WITH_HYPHEN_, tree( WORD_, "z" ) )
        )
    ) ;
  }

// =======  
// Fixture
// =======  
  
  private static Logger LOGGER = LoggerFactory.getLogger( EmbeddedListMangler.class ) ; 
  
  private static void verifyRehierarchizeList(
      SyntacticTree expectedTree,
      SyntacticTree flatTree
  ) {
    LOGGER.info( "Flat tree: " + TreeFixture.asString( flatTree ) ) ;
    LOGGER.info( "Expected tree: " + TreeFixture.asString( expectedTree ) ) ;
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

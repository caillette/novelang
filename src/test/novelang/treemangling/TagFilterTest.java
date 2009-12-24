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

import java.util.Set;

import org.junit.Test;

import novelang.designator.Tag;
import novelang.parser.NodeKind;
import novelang.system.Log;
import novelang.system.LogFactory;
import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import static novelang.parser.antlr.TreeFixture.tree;
import novelang.parser.antlr.TreeFixture;
import static novelang.parser.NodeKind.*;
import com.google.common.collect.ImmutableSet;

/**
 * Tests for {@link TagFilter}.
 * 
 * @author Laurent Caillette
 */
public class TagFilterTest {

  private static final Log LOG = LogFactory.getLog( LevelManglerTest.class ) ;

  @Test
  public void doNothingWhenNothingToDo() {
    final SyntacticTree tree = tree(
        PART,
        tree( PARAGRAPH_REGULAR ),
        tree( PARAGRAPH_REGULAR )
    );
    verifyFilterTags(
        tree,
        tree,
        tags()
    ) ;
  }

  @Test
  public void filterJustOneParagraphOnTwo() {
    verifyFilterTags(
        tree(
            PART,
            tree(
                PARAGRAPH_REGULAR,
                TAG1_TREE ,
                tree( WORD_, "w" )
            )
        ),
        tree(
            PART,
            tree(
                PARAGRAPH_REGULAR,
                TAG1_TREE,
                tree( WORD_, "w" )
            ),
            tree(
                PARAGRAPH_REGULAR,
                tree( WORD_, "x" )
            )
        ),
        tags( TAG_1 )
    ) ;
  }

  @Test
  public void keepUntaggedContentWhenNoMatchingTag() {
    verifyFilterTags(
        tree(
            PART,
            tree( _URL )
        ),
        tree(
            PART,
            tree( _URL ), // Must be NON_TRAVERSABLE!
            tree(
                PARAGRAPH_REGULAR,
                TAG1_TREE,
                tree( WORD_, "w" )
            )
        ),
        tags( TAG_2 )
    ) ;
  }

  @Test
  public void dontRetainUntaggedPrecedingSiblingWhenInsideAScope() {
    verifyFilterTags(
        tree(
            PART,
            tree(
                _LEVEL,
                tree( LEVEL_TITLE, tree( WORD_, ( "level" ) ) ),
                tree( PARAGRAPH_REGULAR, TAG1_TREE ,tree( WORD_, "1" ) )
            )
        ),
        tree(
            PART,
            tree(
                _LEVEL,
                tree( LEVEL_TITLE, tree( WORD_, ( "level" ) ) ),
                tree( PARAGRAPH_REGULAR, tree( WORD_, "0" ) ),
                tree( PARAGRAPH_REGULAR, TAG1_TREE ,tree( WORD_, "1" ) )
            )
        ),
        tags( TAG_1 )
    ) ;
  }

  @Test
  public void retainParentLevel() {
    verifyFilterTags(
        tree(
            PART,
            tree(
                _LEVEL,
                tree(
                    PARAGRAPH_REGULAR,
                    TAG1_TREE,
                    tree( WORD_, "w" )
                )
            )
        ),
        tree(
            PART,
            tree(
                _LEVEL,
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "x" )
                )
            ),
            tree(
                _LEVEL,
                tree(
                    PARAGRAPH_REGULAR,
                    TAG1_TREE,
                    tree( WORD_, "w" )
                )
            )
        ),
        tags( TAG_1 )
    ) ;
  }


  @Test
  public void retainLevelWithImplicitTag() {
    verifyFilterTags(
        tree(
            PART,
            tree(
                _LEVEL,
                tree( _IMPLICIT_TAG, "tag-1" ),
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "w" )
                )
            )
        ),
        tree(
            PART,
            tree(
                _LEVEL,
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "x" )
                )
            ),
            tree(
                _LEVEL,
                tree( _IMPLICIT_TAG, "tag-1" ),
                // No title to get sure that check happens on _IMPLICIT_TAG.
                // tree( LEVEL_TITLE, tree( WORD_, "tag-1" ) ), 
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "w" )
                )
            )
        ),
        tags( TAG_1 )
    ) ;
  }

  /**
   * Verify that {@link novelang.parser.NodeKind#PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_}
   * is correctly handled because it is the one to be processed by the {@code ListMangler}.
   * The {@link novelang.parser.NodeKind#_PARAGRAPH_AS_LIST_ITEM} appears later, upon
   * {@link novelang.rendering.GenericRenderer} transformation. 
   */
  @Test
  public void retainParagraphsInsideListWithTripleHyphen() {
    verifyFilterTags(
        tree(
            PART,
            tree(
                _LEVEL,
                tree( LEVEL_INTRODUCER_, tree( WORD_, "x" ) ),
                tree( 
                    _LIST_WITH_TRIPLE_HYPHEN,
                    tree(
                        PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_,
                        TAG2_TREE,
                        tree( WORD_, "z" )
                    )
                )
            )
        ),
        tree(
            PART,
            tree(
                _LEVEL,
                tree( LEVEL_INTRODUCER_, tree( WORD_, "x" ) ),
                tree(
                    PARAGRAPH_REGULAR,
                    TAG1_TREE,
                    tree( WORD_, "y" )
                ),
                tree(
                    _LIST_WITH_TRIPLE_HYPHEN,
                    tree(
                        PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_,
                        TAG2_TREE,
                        tree( WORD_, "z" )
                    )
                )
            )
        ),
        tags( TAG_2 )
    ) ;
  }

// =======
// Fixture
// =======

  private static final Tag TAG_1 = new Tag( "tag-1" ) ;
  private static final Tag TAG_2 = new Tag( "tag-2" ) ;
  private static final SyntacticTree TAG1_TREE = TAG_1.asSyntacticTree( NodeKind._EXPLICIT_TAG ) ;
  private static final SyntacticTree TAG2_TREE = TAG_2.asSyntacticTree( NodeKind._EXPLICIT_TAG ) ;

  private static Set< Tag > tags( final Tag... tags ) {
    return ImmutableSet.of( tags ) ;
  }

  private static void verifyFilterTags(
      final SyntacticTree expectedTree,
      final SyntacticTree actualTree,
      final Set< Tag > tags
  ) {
    LOG.info( "Expected tree: %s", TreeFixture.asString( expectedTree ) ) ;
    final Treepath< SyntacticTree > expectedTreepath = Treepath.create( expectedTree ) ;

    final Treepath< SyntacticTree > rehierarchized = 
        TagFilter.filter( Treepath.create( actualTree ), tags ) ;

    TreeFixture.assertEqualsWithSeparators(
        expectedTreepath,
        rehierarchized
    ) ;

  }


}

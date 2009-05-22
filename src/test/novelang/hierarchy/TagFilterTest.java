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

import java.util.Set;

import org.junit.Test;
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

  private static final Log LOG = LogFactory.getLog( HierarchizerTest.class ) ;

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

// =======
// Fixture
// =======

  private static final String TAG_1 = "tag-1";
  private static final String TAG_2 = "tag-2";
  private static final String TAG_3 = "tag-3";
  private static final SyntacticTree TAG1_TREE = tree( TAG, TAG_1 ) ;

  private static Set< String > tags( String... tags ) {
    return ImmutableSet.of( tags ) ;
  }

  private static void verifyFilterTags(
      SyntacticTree expectedTree,
      SyntacticTree actualTree,
      Set< String > tags
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

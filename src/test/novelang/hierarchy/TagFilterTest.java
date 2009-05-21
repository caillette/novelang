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
import static novelang.parser.NodeKind.PART;
import static novelang.parser.NodeKind.PARAGRAPH_REGULAR;
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

// =======
// Fixture
// =======

  private static Set< String > tags( String... tags ) {
    return ImmutableSet.of( tags ) ;
  }

  private static void verifyFilterTags(
      SyntacticTree expectedTree,
      SyntacticTree flatTree,
      Set< String > tags
  ) {
    LOG.info( "Expected tree: %s", TreeFixture.asString( expectedTree ) ) ;
    final Treepath< SyntacticTree > expectedTreepath = Treepath.create( expectedTree ) ;

    final Treepath rehierarchized = TagFilter.filter( Treepath.create( flatTree ), tags ) ;

    TreeFixture.assertEqualsWithSeparators(
        expectedTreepath,
        rehierarchized
    ) ;

  }


}

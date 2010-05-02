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
package novelang.common.tree;

import java.util.Arrays;

import junit.framework.AssertionFailedError;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests for {@link novelang.common.tree.Treepath}.
 *
 * @author Laurent Caillette
 */
public class TreepathTest {

  @Test
  public void testTreepathLength1() {
    final MyTree tree = MyTree.create( "tree" ) ;
    final Treepath< MyTree > treepath = Treepath.create( tree ) ;

    assertEquals( 1, treepath.getLength() ) ;
    assertSame( tree, treepath.getTreeAtStart() ) ;
    assertSame( tree, treepath.getTreeAtEnd() ) ;
    assertSame( tree, treepath.getTreeAtDistance( 0 ) ) ;
    assertSame( treepath, treepath.getTreepathAtDistanceFromStart( 0 ) ) ;
  }

  @Test( expected = IllegalArgumentException.class )
  public void testTreepathLength1Bad() {
    final MyTree tree = MyTree.create( "tree" ) ;
    final Treepath< MyTree > treepath = Treepath.create( tree ) ;
    assertSame( tree, treepath.getTreeAtDistance( 1 ) ) ;
  }

  @Test
  public void testTreepathLength2() {
    final MyTree child = MyTree.create( "child" ) ;
    final MyTree parent = MyTree.create( "parent", child ) ;

    final Treepath< MyTree > pathToParent = Treepath.create( parent ) ;
    final Treepath< MyTree > pathToChild = Treepath.create( pathToParent, 0 ) ;

    assertEquals( 2, pathToChild.getLength() ) ;
    assertSame( parent, pathToChild.getTreeAtDistance( 1 ) ) ;
    assertSame( child, pathToChild.getTreeAtDistance( 0 )) ;
    assertSame( parent, pathToChild.getTreeAtStart() ) ;
    assertSame( child, pathToChild.getTreeAtEnd() ) ;
    assertSame( pathToChild, pathToChild.getTreepathAtDistanceFromStart( 1 ) ) ;
    assertSame( pathToParent, pathToChild.getTreepathAtDistanceFromStart( 0 ) ) ;
  }

  @Test
  public void testTreepathLength3() {
    final MyTree grandChild = MyTree.create( "grandChild" ) ;
    final MyTree child = MyTree.create( "child", grandChild ) ;
    final MyTree parent = MyTree.create( "parent", child ) ;

    final Treepath< MyTree > pathToParent = Treepath.create( parent ) ;
    final Treepath< MyTree > pathToChild = Treepath.create( pathToParent, 0 ) ;
    final Treepath< MyTree > pathToGrandChild = Treepath.create( pathToChild, 0 ) ;

    assertEquals( 3, pathToGrandChild.getLength() ) ;
    assertSame( parent, pathToGrandChild.getTreeAtStart() ) ;
    assertSame( grandChild, pathToGrandChild.getTreeAtEnd() ) ;
    assertSame( parent, pathToGrandChild.getTreeAtDistance( 2 ) ) ;
    assertSame( child, pathToGrandChild.getTreeAtDistance( 1 ) ) ;
    assertSame( grandChild, pathToGrandChild.getTreeAtDistance( 0 ) ) ;

    assertSame( pathToParent, pathToGrandChild.getTreepathAtDistanceFromStart( 0 ) ) ;
    assertSame( pathToChild, pathToGrandChild.getTreepathAtDistanceFromStart( 1 ) ) ;
    assertSame( pathToGrandChild, pathToGrandChild.getTreepathAtDistanceFromStart( 2 ) ) ;

  }

  @Test
  public void indicesForLengthOf1() {
    final MyTree tree = MyTree.create( "tree" ) ;
    final Treepath< MyTree > treepath = Treepath.create( tree ) ;
    assertNull( treepath.getIndicesInParent() ) ;
  }

  @Test
  public void indicesForLengthOf3() {
    final MyTree grandChild = MyTree.create( "grandChild" ) ;
    final MyTree child = MyTree.create( "child", grandChild ) ;
    final MyTree parent = MyTree.create( "parent", child ) ;

    final Treepath< MyTree > origin = Treepath.create( parent, 0, 0 ) ;

    assertEquals( 3, origin.getLength() ) ; // Verify test's own health.
    final int[] indices = origin.getIndicesInParent() ;
    assertEquals( 2, indices.length ) ;
    assertEquals( 0, indices[ 0 ] ) ;
    assertEquals( 0, indices[ 1 ] ) ;

    final Treepath< MyTree > recreated = Treepath.create( parent, indices ) ;
    assertSame( grandChild, recreated.getTreeAtDistance( 0 ) ) ;
    assertSame( child, recreated.getTreeAtDistance( 1 ) ) ;
    assertSame( parent, recreated.getTreeAtDistance( 2 ) ) ;

  }

  /**
   * <pre>
   *           grandParent
   *                |
   *             parent
   *           /    |   \
   *     child0  child1  child2
   *                   /       \
   *          grandChild10    grandChild11
   * </pre>
   */
  @Test
  public void indicesForLengthOf3AndIndicesWhichAreNotAlwaysZero() {
    final MyTree grandChild20 = MyTree.create( "grandChild20" ) ;
    final MyTree grandChild21 = MyTree.create( "grandChild21" ) ;
    final MyTree child0 = MyTree.create( "child0" ) ;
    final MyTree child1 = MyTree.create( "child1" ) ;
    final MyTree child2 = MyTree.create( "child2", grandChild20, grandChild21 ) ;
    final MyTree parent = MyTree.create( "parent", child0, child1, child2 ) ;
    final MyTree grandParent = MyTree.create( "grandParent", parent ) ;

    final Treepath< MyTree > origin = Treepath.create( grandParent, 0, 2, 1 ) ;

    assertEquals( 4, origin.getLength() ) ; // Verify test's own health.
    final int[] indices = origin.getIndicesInParent() ;
    assertEquals( "Indices: " + Arrays.toString( indices ), 3, indices.length ) ;
    assertEquals( "Indices: " + Arrays.toString( indices ), 0, indices[ 0 ] ) ;
    assertEquals( "Indices: " + Arrays.toString( indices ), 2, indices[ 1 ] ) ;
    assertEquals( "Indices: " + Arrays.toString( indices ), 1, indices[ 2 ] ) ;

    final Treepath< MyTree > recreated = Treepath.create( grandParent, indices ) ;
    assertSame( parent, recreated.getTreeAtDistance( 2 ) ) ;
    assertSame( child2, recreated.getTreeAtDistance( 1 ) ) ;
    assertSame( grandChild21, recreated.getTreeAtDistance( 0 ) ) ;

  }

// =======
// Fixture
// =======


  private static void assertSame( final MyTree expected, final MyTree actual ) {
    final String message =
        "Expected: {" + ( null == expected ? "null" : expected.getPayload() ) + "} " +
        "got {" + ( null == actual ? "null" : actual.getPayload() ) + "}"
    ;
    if( expected != actual ) {
      throw new AssertionFailedError( message ) ;
    }
  }

  private static void assertSame( 
      final Treepath< MyTree > expected, 
      final Treepath< MyTree > actual 
  ) {
    final String message =
        "Expected: {" + ( null == expected ? "null" : expected ) + "} " +
        "got {" + ( null == actual ? "null" : actual ) + "}"
    ;
    if( expected != actual ) {
      throw new AssertionFailedError( message ) ;
    }
  }

}
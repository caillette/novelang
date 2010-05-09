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


import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Tests for {@link Treepath} comparisons.
 *
 * @author Laurent Caillette
 */
public class TreepathComparisonTest {


  @Test( expected = IllegalArgumentException.class )
  public void failOnDifferentStarts() {
    Treepath.create( child0 ).compareTo( Treepath.create( child2 ) ) ;
  }  

  @Test
  public void compareSameWithLength1() {
    verifyEqual( pathToGrandParent, pathToGrandParent ); ;
  }

  @Test
  public void compareSameWithLength2() {
    verifyEqual( pathToParent, pathToParent ) ;
  }

  @Test
  public void compareSameWithLength3() {
    verifyEqual( pathToChild0, pathToChild0 ) ;
  }

  @Test
  public void compareSameWithLength3Again() {
    verifyEqual( pathToChild1, pathToChild1 ) ;
  }

  @Test
  public void differenceWithLength2() {
    verifySecondGreater( Treepath.create( parent, 0 ), Treepath.create( parent, 1 ) ); ;
  }
  
  @Test
  public void longerIsGreater() {
    verifySecondGreater( pathToParent, pathToChild0 ) ;
  }
  
  @Test
  public void sort() {
    final List< Treepath< MyTree > > list = Arrays.asList(
        pathToGrandParent,
        pathToParent,
        pathToChild0,
        pathToChild1,
        pathToChild2,
        pathToGrandChild20,
        pathToGrandChild21,
        pathToGrandParent,
        pathToParent,
        pathToChild0,
        pathToChild1,
        pathToChild2,
        pathToGrandChild20,
        pathToGrandChild21
    ) ;
    Collections.sort( list ) ;
    
    assertEquals( 
        Arrays.asList(
            pathToGrandParent,
            pathToGrandParent,
            pathToParent,
            pathToParent,
            pathToChild0,
            pathToChild0,
            pathToChild1,
            pathToChild1,
            pathToChild2,
            pathToChild2,
            pathToGrandChild20,
            pathToGrandChild20,
            pathToGrandChild21,
            pathToGrandChild21            
        ),
        list
    ) ;
  }


// =======
// Fixture
// =======
  
  /*
   * <pre>
   *           grandParent
   *                |
   *             parent
   *           /    |   \
   *     child0  child1  child2
   *                   /       \
   *          grandChild20    grandChild21
   * </pre>
   */
  
  private final MyTree grandChild20 = MyTree.create( "grandChild20" ) ;
  private final MyTree grandChild21 = MyTree.create( "grandChild21" ) ;
  private final MyTree child0 = MyTree.create( "child0" ) ;
  private final MyTree child1 = MyTree.create( "child1" ) ;
  private final MyTree child2 = MyTree.create( "child2", grandChild20, grandChild21 ) ;
  private final MyTree parent = MyTree.create( "parent", child0, child1, child2 ) ;
  private final MyTree grandParent = MyTree.create( "grandParent", parent ) ;

  private final Treepath< MyTree > pathToGrandParent = Treepath.create( grandParent ) ;
  private final Treepath< MyTree > pathToParent = Treepath.create( grandParent, 0 ) ;
  private final Treepath< MyTree > pathToChild0 = Treepath.create( grandParent, 0, 0 ) ;
  private final Treepath< MyTree > pathToChild1 = Treepath.create( grandParent, 0, 1 ) ;
  private final Treepath< MyTree > pathToChild2 = Treepath.create( grandParent, 0, 2 ) ;
  private final Treepath< MyTree > pathToGrandChild20 = Treepath.create( grandParent, 0, 2, 0 ) ;
  private final Treepath< MyTree > pathToGrandChild21 = Treepath.create( grandParent, 0, 2, 1 ) ;

  
  private static void verifyEqual( 
      final Treepath< MyTree > first, 
      final Treepath< MyTree > second 
  ) {
    assertEquals( 0, first.compareTo( second ) ) ;
  }

  private static void verifySecondGreater( 
      final Treepath< MyTree > first, 
      final Treepath< MyTree > second 
  ) {
    Assert.assertTrue( first.compareTo( second ) < 0 ) ;
  }

  


}
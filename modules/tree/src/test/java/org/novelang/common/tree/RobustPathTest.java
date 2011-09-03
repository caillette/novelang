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
package org.novelang.common.tree;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link RobustPath}.
 *
 * @author Laurent Caillette
 */
public class RobustPathTest {

  @Test
  public void singleton() {
    final MyTree tree = new MyTree( "root" ) ;
    final Treepath< MyTree > treepath = Treepath.create( tree ) ;
    final RobustPath< MyTree > robustPath = RobustPath.create( treepath ) ;
    assertEquals(
        tree,
        robustPath.apply( tree ).getTreeAtEnd()
    ) ;
  }


  /**
   * <pre>
   * parent
   *   |
   * child
   * </pre>
   */
  @Test
  public void parentChild() {
    final MyTree child =new MyTree( "child" ) ;
    final MyTree parent = new MyTree( "parent", child ) ;
    final Treepath< MyTree > treepath = Treepath.create( parent, 0 ) ;
    final RobustPath< MyTree > robustPath = RobustPath.create( treepath ) ;
    final Treepath< MyTree > treepathRebuilt = robustPath.apply( parent ) ;
    assertEquals(
        parent,
        treepathRebuilt.getTreeAtStart()  
    ) ;
    assertEquals(
        child,
        treepathRebuilt.getTreeAtEnd()
    ) ;
  }

  /**
   * <pre>
   *       parent
   *     /       \
   * [-x]         child
   * </pre>
   */
  @Test
  public void parentWithOneIgnoredChild() {
    final MyTree child = new MyTree( "child" ) ;
    final MyTree ignored = new MyTree( "-x" ) ;
    final MyTree parent = new MyTree( "parent", ignored, child ) ;
    final Treepath< MyTree > treepath = Treepath.create( parent, 1 ) ;
    final RobustPath< MyTree > robustPath = RobustPath.create(
        treepath,
        PAYLOD_STARTS_BY_LETTER
    ) ;
    final Treepath< MyTree > treepathMinusIgnored =
        TreepathTools.removePreviousSibling( treepath ) ;
    final Treepath< MyTree > treepathRebuilt =
        robustPath.apply( treepathMinusIgnored.getTreeAtStart() ) ;
    assertEquals(
        child,
        treepathRebuilt.getTreeAtEnd()
    ) ;
  }

  /**
   * <pre>
   *       a
   *     /   \
   * [-b]     c
   *       /  |  \
   *     d  [-e]  f
   *              ^
   * </pre>
   */
  @Test
  public void depth3() {
    final MyTree dTree = new MyTree( "d" ) ;
    final MyTree eTree = new MyTree( "-e" ) ;
    final MyTree fTree = new MyTree( "f" ) ;
    final MyTree cTree = new MyTree( "c", dTree, eTree, fTree ) ;
    final MyTree bTree = new MyTree( "-b" ) ;
    final MyTree aTree = new MyTree( "a", bTree, cTree ) ;
    final Treepath< MyTree > treepath = Treepath.create( aTree, 1, 2 ) ;
    final RobustPath< MyTree > robustPath = RobustPath.create(
        treepath,
        PAYLOD_STARTS_BY_LETTER
    ) ;
    final Treepath< MyTree > treepathMinusIgnored =
        TreepathTools.removePreviousSibling( treepath ) ;
    final Treepath< MyTree > treepathRebuilt =
        robustPath.apply( treepathMinusIgnored.getTreeAtStart() ) ;
    assertEquals(
        fTree,
        treepathRebuilt.getTreeAtEnd()
    ) ;
  }





  @Test( expected = FilterException.class )
  public void detectExcessiveFilteringAtCreation() {
    final MyTree child = new MyTree( "child" ) ;
    final MyTree parent = new MyTree( "parent", child ) ;
    final Treepath< MyTree > treepath = Treepath.create( parent, 0 ) ;
    RobustPath.create( treepath, Predicates.< MyTree >alwaysFalse() ) ;
    
  }

  @Test( expected = FilterException.class )
  public void detectExcessiveFilteringWhenApplying() {
    final MyTree firstChild = new MyTree( "child" ) ;
    final MyTree firstParent = new MyTree( "parent", firstChild ) ;
    final Treepath< MyTree > firstTreepath = Treepath.create( firstParent, 0 ) ;
    final RobustPath< MyTree > robustPath = 
        RobustPath.create( firstTreepath, PAYLOD_STARTS_BY_LETTER ) ;
    
    final MyTree secondChild = new MyTree( "-child" ) ;
    final MyTree secondParent = new MyTree( "parent", secondChild ) ;
    robustPath.apply( secondParent ) ;
    
    
  }
// =======
// Fixture
// =======


  private static final Predicate< MyTree > PAYLOD_STARTS_BY_LETTER =
      new Predicate< MyTree >() {
          @Override
          public boolean apply( final MyTree MyTree ) {
            return Character.isLetter( MyTree.getPayload().charAt( 0 ) ) ;
          }
      }
  ;

}

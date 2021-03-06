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

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Tests for {@link TreepathTools}.
 * 
 * @author Laurent Caillette
 */
public class TreepathToolsTest {

  @Test
  public void replaceEnd() {

    final MyTree grandChild = MyTree.create( "grandChild" ) ;  //     parent
    final MyTree parent = MyTree.create(                       //    /     \
        "parent",                                              // child0  child1
        MyTree.create( "child0", grandChild ),                 //   |
        MyTree.create( "child1" )                              // grandChild (becomes newGrandChild)
    ) ;

    final MyTree newGrandChild = MyTree.create( "newGrandChild" ) ;

    // original: parent <- child0 <- grandChild
    final Treepath< MyTree > original = Treepath.create( parent, 0, 0 ) ;

    final Treepath< MyTree > reparented = TreepathTools.replaceTreepathEnd( original, newGrandChild ) ;

    assertEquals( 3, reparented.getLength() ) ;

    assertEquals( "parent", reparented.getTreeAtDistance( 2 ).getPayload() ) ;
    assertEquals( 2, reparented.getTreeAtDistance( 2 ).getChildCount() ) ;

    assertEquals( "child0", reparented.getTreeAtDistance( 1 ).getPayload() ) ;
    assertEquals( 1, reparented.getTreeAtDistance( 1 ).getChildCount() ) ;

    assertEquals( "newGrandChild", reparented.getTreeAtDistance( 0 ).getPayload() ) ;
    assertEquals( 0, reparented.getTreeAtDistance( 0 ).getChildCount() ) ;

  }

  @Test
  public void addSiblingLast() {
                                                               // parent
    final MyTree child0 = MyTree.create( "child0" ) ;          //   |
    final MyTree parent = MyTree.create( "parent", child0 ) ;  // child0


    final MyTree child1 = MyTree.create( "child1" ) ;

    final Treepath< MyTree > treepath = TreepathTools.addSiblingLast(  //   parent
        Treepath.< MyTree >create( parent, 0 ),                        //   |     \
        child1// ^ IntelliJ IDEA 7.0.3 requires this.                  // child0  child1
    ) ;

    assertEquals( "parent", treepath.getTreeAtStart().getPayload() ) ;
    assertEquals( 2, treepath.getTreeAtStart().getChildCount() ) ;

    assertEquals( "child0", treepath.getTreeAtStart().getChildAt( 0 ).getPayload() ) ;
    assertEquals( 0, treepath.getTreeAtStart().getChildAt( 0 ).getChildCount() ) ;

    assertEquals( "child1", treepath.getTreeAtStart().getChildAt( 1 ).getPayload() ) ;
    assertEquals( 0, treepath.getTreeAtStart().getChildAt( 1 ).getChildCount() ) ;

  }

  @Test
  public void removeEnd() {
                                                                         //   grandParent
    final MyTree child0 = MyTree.create( "child0" ) ;                    //        |
    final MyTree child1 = MyTree.create( "child1" ) ;                    //     parent
    final MyTree parent = MyTree.create( "parent", child0, child1 ) ;    //     /    \
    final MyTree grandParent = MyTree.create( "grandParent", parent ) ;  // child0  child1

    // treepath: grandParent <- parent <- child0
    final Treepath< MyTree > treepath = Treepath.create( grandParent, 0, 0 ) ;

    // afterRemoval: grandParent <- parent
    final Treepath< MyTree > afterRemoval = TreepathTools.removeEnd( treepath ) ;

    assertEquals( 2, afterRemoval.getLength() ) ;

    assertEquals( 1, afterRemoval.getTreeAtDistance( 1 ).getChildCount() ) ;
    assertEquals( "grandParent", afterRemoval.getTreeAtDistance( 1 ).getPayload() ) ;

    assertEquals( 1, afterRemoval.getTreeAtDistance( 0 ).getChildCount() ) ;
    assertEquals( "parent", afterRemoval.getTreeAtDistance( 0 ).getPayload() ) ;

    assertSame( child1, afterRemoval.getTreeAtDistance( 0 ).getChildAt( 0 ) ) ;

  }

  @Test
  public void removeNextSibling() {

    final MyTree child0 = MyTree.create( "child0" ) ;                       //         parent
    final MyTree child1 = MyTree.create( "child1" ) ;                       //       /   |   \
    final MyTree child2 = MyTree.create( "child2" ) ;                       // child0 child1 child2
    final MyTree parent = MyTree.create( "parent", child0, child1, child2 ) ;

    // treepath: parent <- child0
    final Treepath< MyTree > treepath = Treepath.create( parent, 0 ) ;

    // afterRemoval: parent <- child0
    final Treepath< MyTree > afterRemoval = TreepathTools.removeNextSibling( treepath ) ;

    assertEquals( 2, afterRemoval.getLength() ) ;

    assertEquals( 2, afterRemoval.getTreeAtDistance( 1 ).getChildCount() ) ;
    assertEquals( "parent", afterRemoval.getTreeAtDistance( 1 ).getPayload() ) ;

    assertEquals( 0, afterRemoval.getTreeAtDistance( 0 ).getChildCount() ) ;
    assertSame( child0, afterRemoval.getTreeAtDistance( 1 ).getChildAt( 0 ) ) ;
    assertSame( child2, afterRemoval.getTreeAtDistance( 1 ).getChildAt( 1 ) ) ;

  }

  @Test
  public void removePreviousSibling3() {

    final MyTree child0 = MyTree.create( "child0" ) ;                       //         parent
    final MyTree child1 = MyTree.create( "child1" ) ;                       //       /   |   \
    final MyTree child2 = MyTree.create( "child2" ) ;                       // child0 child1 child2
    final MyTree parent = MyTree.create( "parent", child0, child1, child2 ) ;

    // treepath: parent <- child1
    final Treepath< MyTree > treepath = Treepath.create( parent, 1 ) ;

    // afterRemoval: parent <- child1
    final Treepath< MyTree > afterRemoval = TreepathTools.removePreviousSibling( treepath ) ;

    assertEquals( 2, afterRemoval.getLength() ) ;

    assertEquals( 2, afterRemoval.getTreeAtDistance( 1 ).getChildCount() ) ;
    assertEquals( "parent", afterRemoval.getTreeAtDistance( 1 ).getPayload() ) ;

    assertEquals( 0, afterRemoval.getTreeAtDistance( 0 ).getChildCount() ) ;
    assertSame( child1, afterRemoval.getTreeAtDistance( 1 ).getChildAt( 0 ) ) ;
    assertSame( child2, afterRemoval.getTreeAtDistance( 1 ).getChildAt( 1 ) ) ;

  }

  @Test
  public void removePreviousSibling2() {

    final MyTree child0 = MyTree.create( "child0" ) ;                  //      parent
    final MyTree child1 = MyTree.create( "child1" ) ;                  //     /      \
    final MyTree parent = MyTree.create( "parent", child0, child1 ) ;  // child0   child1

    // treepath: child1 -> parent
    final Treepath< MyTree > treepath = Treepath.create( parent, 1 ) ;

    // afterRemoval: child1 -> parent
    final Treepath< MyTree > afterRemoval = TreepathTools.removePreviousSibling( treepath ) ;

    assertEquals( 2, afterRemoval.getLength() ) ;

    assertEquals( 1, afterRemoval.getTreeAtDistance( 1 ).getChildCount() ) ;
    assertEquals( "parent", afterRemoval.getTreeAtDistance( 1 ).getPayload() ) ;

    assertEquals( 0, afterRemoval.getTreeAtDistance( 0 ).getChildCount() ) ;
    assertSame( child1, afterRemoval.getTreeAtDistance( 1 ).getChildAt( 0 ) ) ;

  }

  @Test
  public void getSiblingAt() {

    final MyTree child0 = MyTree.create( "child0" ) ;                       //         parent
    final MyTree child1 = MyTree.create( "child1" ) ;                       //       /   |   \
    final MyTree child2 = MyTree.create( "child2" ) ;                       // child0 child1 child2
    final MyTree parent = MyTree.create( "parent", child0, child1, child2 ) ;

    // treepath: parent <- child0
    final Treepath< MyTree > treepath = Treepath.create( parent, 0 ) ;

    final Treepath< MyTree > sibling = TreepathTools.getSiblingAt( treepath, 2 ) ;

    assertEquals( "child2", sibling.getTreeAtEnd().getPayload() ) ;

  }

  @Test
  public void becomeLastChildOfPreviousSibling() {
    
    final MyTree child = MyTree.create( "child" ) ;                   //   parent
    final MyTree moving = MyTree.create( "moving" ) ;                 //    |   \
    final MyTree parent = MyTree.create( "parent", child, moving ) ;  // child  moving

    final Treepath< MyTree > original = Treepath.create( parent, 1 ) ;

                                                                      //   parent
                                                                      //   |   \
                                                                      // child  moving
    final Treepath< MyTree > moved =                                  //   |
        TreepathTools.becomeLastChildOfPreviousSibling( original ) ;  // moving

    assertEquals( 3, moved.getLength() ) ;

    assertEquals( "parent", moved.getTreeAtDistance( 2 ).getPayload() ) ;
    assertEquals( 1, moved.getTreeAtDistance( 2 ).getChildCount() ) ;

    assertEquals( "child", moved.getTreeAtDistance( 1 ).getPayload() ) ;
    assertEquals( 1, moved.getTreeAtDistance( 1 ).getChildCount() ) ;

    assertEquals( "moving", moved.getTreeAtEnd().getPayload() ) ;
    assertEquals( "moving", moved.getTreeAtDistance( 0 ).getPayload() ) ;
    assertEquals( 0, moved.getTreeAtDistance( 0 ).getChildCount() ) ;


  }


  @Test ( expected = IllegalArgumentException.class )
  public void removeSubtreeDetectsUnrelatedTreepaths() {
    final Treepath< MyTree > treepath1 = Treepath.create( MyTree.create( "1" ) ) ;
    final Treepath< MyTree > treepath2 = Treepath.create( MyTree.create( "2" ) ) ;
    TreepathTools.removeSubtree( treepath1, treepath2 ) ;
  }

  @Test (expected = IllegalArgumentException.class )
  public void removeSubtreeDetectsSubtreeContainingContainer() {
                                                                         //   grandParent
                                                                         //        |
    final MyTree child = MyTree.create( "child" ) ;                      //     parent
    final MyTree parent = MyTree.create( "parent", child ) ;             //        |
    final MyTree grandParent = MyTree.create( "grandParent", parent ) ;  //     child

    // treepath: grandParent <- parent
    final Treepath< MyTree > sub = Treepath.create( grandParent, 0, 0 ) ;

    // treepath: grandParent <- parent <- child
    final Treepath< MyTree > container = Treepath.create( grandParent, 0, 0 ) ;

    TreepathTools.removeSubtree( container, sub ) ;
  }

  @Test
  public void removeSubtreeWithSiblings() {
                                                                         //   grandParent
    final MyTree child0 = MyTree.create( "child0" ) ;                    //        |
    final MyTree child1 = MyTree.create( "child1" ) ;                    //     parent
    final MyTree parent = MyTree.create( "parent", child0, child1 ) ;    //     /    \
    final MyTree grandParent = MyTree.create( "grandParent", parent ) ;  // child0  child1

    // treepath: grandParent <- parent <- child1
    final Treepath< MyTree > container = Treepath.create( grandParent, 0, 1 ) ;

    // treepath: grandParent <- parent <- child0
    final Treepath< MyTree > subtree = Treepath.create( grandParent, 0, 0 ) ;

    final Treepath< MyTree > afterRemoval = TreepathTools.removeSubtree( container, subtree ) ;


    assertEquals( 3, afterRemoval.getLength() ) ;

    assertEquals( 1, afterRemoval.getTreeAtDistance( 2 ).getChildCount() ) ;
    assertEquals( "grandParent", afterRemoval.getTreeAtDistance( 2 ).getPayload() ) ;

    assertEquals( 1, afterRemoval.getTreeAtDistance( 1 ).getChildCount() ) ;
    assertEquals( "parent", afterRemoval.getTreeAtDistance( 1 ).getPayload() ) ;

    assertSame( child1, afterRemoval.getTreeAtDistance( 0 ) ) ;
  }

}
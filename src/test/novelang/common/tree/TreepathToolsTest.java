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
import org.junit.Test;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNull;

/**
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

    Assert.assertEquals( 3, reparented.getLength() ) ;

    Assert.assertEquals( "parent", reparented.getTreeAtDistance( 2 ).getPayload() ) ;
    Assert.assertEquals( 2, reparented.getTreeAtDistance( 2 ).getChildCount() ) ;

    Assert.assertEquals( "child0", reparented.getTreeAtDistance( 1 ).getPayload() ) ;
    Assert.assertEquals( 1, reparented.getTreeAtDistance( 1 ).getChildCount() ) ;

    Assert.assertEquals( "newGrandChild", reparented.getTreeAtDistance( 0 ).getPayload() ) ;
    Assert.assertEquals( 0, reparented.getTreeAtDistance( 0 ).getChildCount() ) ;

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

    Assert.assertEquals( "parent", treepath.getTreeAtStart().getPayload() ) ;
    Assert.assertEquals( 2, treepath.getTreeAtStart().getChildCount() ) ;

    Assert.assertEquals( "child0", treepath.getTreeAtStart().getChildAt( 0 ).getPayload() ) ;
    Assert.assertEquals( 0, treepath.getTreeAtStart().getChildAt( 0 ).getChildCount() ) ;

    Assert.assertEquals( "child1", treepath.getTreeAtStart().getChildAt( 1 ).getPayload() ) ;
    Assert.assertEquals( 0, treepath.getTreeAtStart().getChildAt( 1 ).getChildCount() ) ;

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

    Assert.assertEquals( 2, afterRemoval.getLength() ) ;

    Assert.assertEquals( 1, afterRemoval.getTreeAtDistance( 1 ).getChildCount() ) ;
    Assert.assertEquals( "grandParent", afterRemoval.getTreeAtDistance( 1 ).getPayload() ) ;

    Assert.assertEquals( 1, afterRemoval.getTreeAtDistance( 0 ).getChildCount() ) ;
    Assert.assertEquals( "parent", afterRemoval.getTreeAtDistance( 0 ).getPayload() ) ;

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

    Assert.assertEquals( 2, afterRemoval.getLength() ) ;

    Assert.assertEquals( 2, afterRemoval.getTreeAtDistance( 1 ).getChildCount() ) ;
    Assert.assertEquals( "parent", afterRemoval.getTreeAtDistance( 1 ).getPayload() ) ;

    Assert.assertEquals( 0, afterRemoval.getTreeAtDistance( 0 ).getChildCount() ) ;
    assertSame( child0, afterRemoval.getTreeAtDistance( 1 ).getChildAt( 0 ) ) ;
    assertSame( child2, afterRemoval.getTreeAtDistance( 1 ).getChildAt( 1 ) ) ;

  }

  @Test
  public void removePreviousSibling() {

    final MyTree child0 = MyTree.create( "child0" ) ;                       //         parent
    final MyTree child1 = MyTree.create( "child1" ) ;                       //       /   |   \
    final MyTree child2 = MyTree.create( "child2" ) ;                       // child0 child1 child2
    final MyTree parent = MyTree.create( "parent", child0, child1, child2 ) ;

    // treepath: parent <- child1
    final Treepath< MyTree > treepath = Treepath.create( parent, 1 ) ;

    // afterRemoval: parent <- child1
    final Treepath< MyTree > afterRemoval = TreepathTools.removePreviousSibling( treepath ) ;

    Assert.assertEquals( 2, afterRemoval.getLength() ) ;

    Assert.assertEquals( 2, afterRemoval.getTreeAtDistance( 1 ).getChildCount() ) ;
    Assert.assertEquals( "parent", afterRemoval.getTreeAtDistance( 1 ).getPayload() ) ;

    Assert.assertEquals( 0, afterRemoval.getTreeAtDistance( 0 ).getChildCount() ) ;
    assertSame( child1, afterRemoval.getTreeAtDistance( 1 ).getChildAt( 0 ) ) ;
    assertSame( child2, afterRemoval.getTreeAtDistance( 1 ).getChildAt( 1 ) ) ;

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

    Assert.assertEquals( "child2", sibling.getTreeAtEnd().getPayload() ) ;

  }

  @Test
  public void becomeLastChildOfPreviousSibling() {
    
    final MyTree child = MyTree.create( "child" ) ;                   //   parent
    final MyTree moving = MyTree.create( "moving" ) ;                 //    |   \
    final MyTree parent = MyTree.create( "parent", child, moving ) ;  // child  moving

    final Treepath< MyTree > original = Treepath.< MyTree >create( parent, 1 ) ;
                                              // ^ IntelliJ IDEA 7.0.3 requires this.

                                                                      //   parent
                                                                      //   |   \
                                                                      // child  moving
    final Treepath< MyTree > moved =                                  //   |
        TreepathTools.becomeLastChildOfPreviousSibling( original ) ;  // moving

    Assert.assertEquals( 3, moved.getLength() ) ;

    Assert.assertEquals( "parent", moved.getTreeAtDistance( 2 ).getPayload() ) ;
    Assert.assertEquals( 1, moved.getTreeAtDistance( 2 ).getChildCount() ) ;

    Assert.assertEquals( "child", moved.getTreeAtDistance( 1 ).getPayload() ) ;
    Assert.assertEquals( 1, moved.getTreeAtDistance( 1 ).getChildCount() ) ;

    Assert.assertEquals( "moving", moved.getTreeAtEnd().getPayload() ) ;
    Assert.assertEquals( "moving", moved.getTreeAtDistance( 0 ).getPayload() ) ;
    Assert.assertEquals( 0, moved.getTreeAtDistance( 0 ).getChildCount() ) ;


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


    Assert.assertEquals( 3, afterRemoval.getLength() ) ;

    Assert.assertEquals( 1, afterRemoval.getTreeAtDistance( 2 ).getChildCount() ) ;
    Assert.assertEquals( "grandParent", afterRemoval.getTreeAtDistance( 2 ).getPayload() ) ;

    Assert.assertEquals( 1, afterRemoval.getTreeAtDistance( 1 ).getChildCount() ) ;
    Assert.assertEquals( "parent", afterRemoval.getTreeAtDistance( 1 ).getPayload() ) ;

    assertSame( child1, afterRemoval.getTreeAtDistance( 0 ) ) ;
  }

  /**
   * <pre>
   *      t0
   *      |
   *      t1
   *     /  \
   *   t2    t5
   *  /  \   |
   * t3  t4  t6
   * 
   * Postorder traversal:
   * t3, t4, t2, t6, t5, t1, t0.
   *
   * Reverse postorder traversal:
   * t0, t1, t5, t6, t2, t4, t3.
   *
   * Mirror postorder traversal:
   * t6, t5, t4, t3, t2, t1, t0.
   * </pre>
   *
   */
  @Test
  public void previousInMirrorPostorder() {

    final MyTree t3 = MyTree.create( "3" ) ;
    final MyTree t4 = MyTree.create( "4" ) ;
    final MyTree t6 = MyTree.create( "6" ) ;
    final MyTree t2 = MyTree.create( "2", t3, t4 ) ;
    final MyTree t5 = MyTree.create( "5", t6 ) ;
    final MyTree t1 = MyTree.create( "1", t2, t5 ) ;
    final MyTree t0 = MyTree.create( "0", t1 ) ;

    final Treepath< MyTree > treepathTo6 = Treepath.create( t0, 0, 1, 0 ) ;

    final Treepath< MyTree > treepathTo5 = TreepathTools.getNextInMirrorPostorder( treepathTo6 ) ;
    assertSame( t5, treepathTo5.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathTo4 = TreepathTools.getNextInMirrorPostorder( treepathTo5 ) ;
    assertSame( t4, treepathTo4.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathTo3 = TreepathTools.getNextInMirrorPostorder( treepathTo4 ) ;
    assertSame( t3, treepathTo3.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathTo2 = TreepathTools.getNextInMirrorPostorder( treepathTo3 ) ;
    assertSame( t2, treepathTo2.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathTo1 = TreepathTools.getNextInMirrorPostorder( treepathTo2 ) ;
    assertSame( t1, treepathTo1.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathTo0 = TreepathTools.getNextInMirrorPostorder( treepathTo1 ) ;
    assertSame( t0, treepathTo0.getTreeAtEnd() ) ;

    final Treepath< MyTree > nextTo0 = TreepathTools.getNextInMirrorPostorder( treepathTo0 ) ;
    assertNull( nextTo0 ) ;

  }

  @Test
  public void lastInPostorder() {

    final MyTree t3 = MyTree.create( "3" ) ;           //      t0
    final MyTree t4 = MyTree.create( "4" ) ;           //      |
    final MyTree t6 = MyTree.create( "6" ) ;           //      t1
    final MyTree t2 = MyTree.create( "2", t3, t4 ) ;   //     /  \
    final MyTree t5 = MyTree.create( "5", t6 ) ;       //   t2    t5
    final MyTree t1 = MyTree.create( "1", t2, t5 ) ;   //  /  \   |
    final MyTree t0 = MyTree.create( "0", t1 ) ;       // t3  t4  t6

    final Treepath< MyTree > treepathTo0 = Treepath.create( t0 ) ;
    final Treepath< MyTree > last6 = TreepathTools.getLastInPostorder( treepathTo0 ) ;
    assertSame( t6, last6.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathFrom5To5 = Treepath.create( t5 ) ;
    final Treepath< MyTree > lastFrom5To5 = TreepathTools.getLastInPostorder( treepathFrom5To5 ) ;
    assertSame( t6, lastFrom5To5.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathFrom6To6 = Treepath.create( t6 ) ;
    final Treepath< MyTree > lastFrom6To6 = TreepathTools.getLastInPostorder( treepathFrom6To6 ) ;
    assertSame( t6, lastFrom6To6.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathFrom0To2 = Treepath.create( t0, 0, 0 ) ;
    final Treepath< MyTree > lastFrom0To2 = TreepathTools.getLastInPostorder( treepathFrom0To2 ) ;
    assertSame( t4, lastFrom0To2.getTreeAtEnd() ) ;


  }
  
  
  @Test
  public void nextInPreorder() {

    final MyTree t3 = MyTree.create( "3" ) ;           //      t0
    final MyTree t4 = MyTree.create( "4" ) ;           //      |
    final MyTree t6 = MyTree.create( "6" ) ;           //      t1
    final MyTree t2 = MyTree.create( "2", t3, t4 ) ;   //     /  \
    final MyTree t5 = MyTree.create( "5", t6 ) ;       //   t2    t5
    final MyTree t1 = MyTree.create( "1", t2, t5 ) ;   //  /  \   |
    final MyTree t0 = MyTree.create( "0", t1 ) ;       // t3  t4  t6

    final Treepath< MyTree > treepathTo0 = Treepath.create( t0 ) ;

    final Treepath< MyTree > treepathTo1 = TreepathTools.getNextInPreorder( treepathTo0 ) ;
    assertSame( t1, treepathTo1.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathTo2 = TreepathTools.getNextInPreorder( treepathTo1 ) ;
    assertSame( t2, treepathTo2.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathTo3 = TreepathTools.getNextInPreorder( treepathTo2 ) ;
    assertSame( t3, treepathTo3.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathTo4 = TreepathTools.getNextInPreorder( treepathTo3 ) ;
    assertSame( t4, treepathTo4.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathTo5 = TreepathTools.getNextInPreorder( treepathTo4 ) ;
    assertSame( t5, treepathTo5.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathTo6 = TreepathTools.getNextInPreorder( treepathTo5 ) ;
    assertSame( t6, treepathTo6.getTreeAtEnd() ) ;

    final Treepath< MyTree > nextTo6 = TreepathTools.getNextInPreorder( treepathTo6 );
    assertNull( nextTo6 ) ;


  }
}
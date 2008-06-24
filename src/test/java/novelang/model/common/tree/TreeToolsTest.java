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
package novelang.model.common.tree;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Laurent Caillette
 */
public class TreeToolsTest {

  @Test
  public void reparent() {

    final MyTree grandChild = MyTree.create( "grandChild" ) ;  //     parent
    final MyTree parent = MyTree.create(                       //    /     \
        "parent",                                              // child0  child1
        MyTree.create( "child0", grandChild ),                 //   |
        MyTree.create( "child1" )                              // grandChild (becomes newGrandChild)
    ) ;

    final MyTree newGrandChild = MyTree.create( "newGrandChild" ) ;

    // original: parent <- child0 <- grandChild
    final Treepath< MyTree > original = Treepath.create( parent, 0, 0 ) ;

    final Treepath< MyTree > reparented = TreeTools.updateBottom( original, newGrandChild ) ;

    Assert.assertEquals( 3, reparented.getHeight() ) ;

    Assert.assertEquals( "parent", reparented.getTreeAtHeight( 2 ).getPayload() ) ;
    Assert.assertEquals( 2, reparented.getTreeAtHeight( 2 ).getChildCount() ) ;

    Assert.assertEquals( "child0", reparented.getTreeAtHeight( 1 ).getPayload() ) ;
    Assert.assertEquals( 1, reparented.getTreeAtHeight( 1 ).getChildCount() ) ;

    Assert.assertEquals( "newGrandChild", reparented.getTreeAtHeight( 0 ).getPayload() ) ;
    Assert.assertEquals( 0, reparented.getTreeAtHeight( 0 ).getChildCount() ) ;

  }

  @Test
  public void addSiblingLast() {
                                                               // parent
    final MyTree child0 = MyTree.create( "child0" ) ;          //   |
    final MyTree parent = MyTree.create( "parent", child0 ) ;  // child0


    final MyTree child1 = MyTree.create( "child1" ) ;

    final Treepath< MyTree > treepath = TreeTools.addSiblingLast(  //   parent
        Treepath.< MyTree >create( parent, 0 ),                    //   |     \
        child1// ^ IntelliJ IDEA 7.0.3 requires this.              // child0  child1
    ) ;

    Assert.assertEquals( "parent", treepath.getTop().getPayload() ) ;
    Assert.assertEquals( 2, treepath.getTop().getChildCount() ) ;

    Assert.assertEquals( "child0", treepath.getTop().getChildAt( 0 ).getPayload() ) ;
    Assert.assertEquals( 0, treepath.getTop().getChildAt( 0 ).getChildCount() ) ;

    Assert.assertEquals( "child1", treepath.getTop().getChildAt( 1 ).getPayload() ) ;
    Assert.assertEquals( 0, treepath.getTop().getChildAt( 1 ).getChildCount() ) ;

  }

  @Test
  public void removeBottom() {
                                                                         //   grandParent
    final MyTree child0 = MyTree.create( "child0" ) ;                    //        |
    final MyTree child1 = MyTree.create( "child1" ) ;                    //     parent
    final MyTree parent = MyTree.create( "parent", child0, child1 ) ;    //     /    \
    final MyTree grandParent = MyTree.create( "grandParent", parent ) ;  // child0  child1

    // treepath: grandParent <- parent <- child0
    final Treepath< MyTree > treepath = Treepath.create( grandParent, 0, 0 ) ;

    // afterRemoval: grandParent <- parent
    final Treepath< MyTree > afterRemoval = TreeTools.removeBottom( treepath ) ;

    Assert.assertEquals( 2, afterRemoval.getHeight() ) ;

    Assert.assertEquals( 1, afterRemoval.getTreeAtHeight( 1 ).getChildCount() ) ;
    Assert.assertEquals( "grandParent", afterRemoval.getTreeAtHeight( 1 ).getPayload() ) ;

    Assert.assertEquals( 1, afterRemoval.getTreeAtHeight( 0 ).getChildCount() ) ;
    Assert.assertEquals( "parent", afterRemoval.getTreeAtHeight( 0 ).getPayload() ) ;

    Assert.assertSame( child1, afterRemoval.getTreeAtHeight( 0 ).getChildAt( 0 ) ) ;

  }

  @Test
  public void moveAsLastChildOfPreviousSibling() {
    
    final MyTree child = MyTree.create( "child" ) ;                   //   parent
    final MyTree moving = MyTree.create( "moving" ) ;                 //    |   \
    final MyTree parent = MyTree.create( "parent", child, moving ) ;  // child  moving

    final Treepath< MyTree > original = Treepath.< MyTree >create( parent, 1 ) ;
                                              // ^ IntelliJ IDEA 7.0.3 requires this.

                                                                  //   parent
                                                                  //   |   \
                                                                  // child  moving
    final Treepath< MyTree > moved =                              //   |
        TreeTools.moveAsLastChildOfPreviousSibling( original ) ;  // moving

    Assert.assertEquals( 3, moved.getHeight() ) ;

    Assert.assertEquals( "parent", moved.getTreeAtHeight( 2 ).getPayload() ) ;
    Assert.assertEquals( 1, moved.getTreeAtHeight( 2 ).getChildCount() ) ;

    Assert.assertEquals( "child", moved.getTreeAtHeight( 1 ).getPayload() ) ;
    Assert.assertEquals( 1, moved.getTreeAtHeight( 1 ).getChildCount() ) ;

    Assert.assertEquals( "moving", moved.getBottom().getPayload() ) ;
    Assert.assertEquals( "moving", moved.getTreeAtHeight( 0 ).getPayload() ) ;
    Assert.assertEquals( 0, moved.getTreeAtHeight( 0 ).getChildCount() ) ;


  }


}
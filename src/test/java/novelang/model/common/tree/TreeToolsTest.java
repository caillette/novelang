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

import org.junit.Test;
import org.junit.Assert;
import static novelang.parser.antlr.TreeFixture.tree;
import novelang.model.common.SyntacticTree;

/**
 * @author Laurent Caillette
 */
public class TreeToolsTest {

  @Test
  public void reparent() {
    final SyntacticTree grandChild = tree( "grandChild" ) ;
    final SyntacticTree parent = tree(
        "parent",
        tree( "child0", grandChild ),
        tree( "child1" )
    ) ;

    final SyntacticTree newGrandChild = tree( "newGrandChild" ) ;
    final Treepath<SyntacticTree> original = Treepath.create( parent, grandChild ) ;

    final Treepath<SyntacticTree> reparented = TreeTools.updateBottom( original, newGrandChild ) ;

    Assert.assertEquals( 3, reparented.getHeight() ) ;

    Assert.assertEquals( "parent", reparented.getTreeAtHeight( 2 ).getText() ) ;
    Assert.assertEquals( 2, reparented.getTreeAtHeight( 2 ).getChildCount() ) ;

    Assert.assertEquals( "child0", reparented.getTreeAtHeight( 1 ).getText() ) ;
    Assert.assertEquals( 1, reparented.getTreeAtHeight( 1 ).getChildCount() ) ;

    Assert.assertEquals( "newGrandChild", reparented.getTreeAtHeight( 0 ).getText() ) ;
    Assert.assertEquals( 0, reparented.getTreeAtHeight( 0 ).getChildCount() ) ;


  }

  @Test
  public void addSiblingAtRight() {
    final MyTree child0 = MyTree.create( "child0" ) ;
    final MyTree child1 = MyTree.create( "child1" ) ;
    final MyTree parent = MyTree.create( "parent", child0 ) ;

    final Treepath< MyTree > treepath = TreeTools.addSiblingAtRight(
        Treepath.create( parent, child0 ), child1 ) ;

    Assert.assertEquals( "parent", treepath.getTop().getPayload() ) ;
    Assert.assertEquals( 2, treepath.getTop().getChildCount() ) ;

    Assert.assertEquals( "child0", treepath.getTop().getChildAt( 0 ).getPayload() ) ;
    Assert.assertEquals( 0, treepath.getTop().getChildAt( 0 ).getChildCount() ) ;

    Assert.assertEquals( "child1", treepath.getTop().getChildAt( 1 ).getPayload() ) ;
    Assert.assertEquals( 0, treepath.getTop().getChildAt( 1 ).getChildCount() ) ;

  }

  @Test
  public void removeBottom() {
    final MyTree child0 = MyTree.create( "child0" ) ;
    final MyTree child1 = MyTree.create( "child1" ) ;
    final MyTree parent = MyTree.create( "parent", child0, child1 ) ;
    final MyTree grandParent = MyTree.create( "grandParent", parent ) ;


    final Treepath< MyTree > treepath = Treepath.create( grandParent, child0 ) ;
    final Treepath< MyTree > afterRemoval = TreeTools.removeBottom( treepath ) ;


    Assert.assertEquals( 2, afterRemoval.getHeight() ) ;

    Assert.assertEquals( 1, afterRemoval.getTreeAtHeight( 1 ).getChildCount() ) ;
    Assert.assertEquals( "grandParent", afterRemoval.getTreeAtHeight( 1 ).getPayload() ) ;

    Assert.assertEquals( 1, afterRemoval.getTreeAtHeight( 0 ).getChildCount() ) ;
    Assert.assertEquals( "parent", afterRemoval.getTreeAtHeight( 0 ).getPayload() ) ;

    Assert.assertSame( child1, afterRemoval.getTreeAtHeight( 0 ).getChildAt( 0 ) ) ;



  }

  @Test
  public void moveLeftDown() {
    final MyTree child = MyTree.create( "child" ) ;
    final MyTree moving = MyTree.create( "moving" ) ;
    final MyTree parent = MyTree.create( "parent", child, moving ) ;

    final Treepath< MyTree > original = Treepath.create( parent, moving ) ;

    final Treepath< MyTree > moved = TreeTools.moveLeftDown( original ) ;

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
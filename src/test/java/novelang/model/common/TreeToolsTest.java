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
package novelang.model.common;

import org.junit.Test;
import org.junit.Assert;
import novelang.model.implementation.DefaultMutableTree;

/**
 * @author Laurent Caillette
 */
public class TreeToolsTest {

  @Test
  public void reparent() {
    final MutableTree parent = new DefaultMutableTree( "parent" ) ;
    final MutableTree child0 = new DefaultMutableTree( "child0" ) ;
    final MutableTree child1 = new DefaultMutableTree( "child1" ) ;
    final MutableTree grandChild = new DefaultMutableTree( "grandChild" ) ;
    child0.addChild( grandChild ) ;
    parent.addChild( child0 ) ;
    parent.addChild( child1 ) ;
    final Treepath original = Treepath.create( parent, grandChild ) ;

    final Tree newGrandChild = new DefaultMutableTree( "newGrandChild" ) ;
    final Treepath reparented = TreeTools.updateBottom( original, newGrandChild ) ;

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
    final MutableTree parent = new DefaultMutableTree( "parent" ) ;
    final MutableTree child0 = new DefaultMutableTree( "child0" ) ;
    final Tree child1 = new DefaultMutableTree( "child1" ) ;
    parent.addChild( child0 ) ;

    final Treepath treepath = TreeTools.addSiblingAtRight(
        Treepath.create( parent, child0 ), child1 ) ;

    Assert.assertEquals( "parent", treepath.getTop().getText() ) ;
    Assert.assertEquals( 2, treepath.getTop().getChildCount() ) ;

    Assert.assertEquals( "child0", treepath.getTop().getChildAt( 0 ).getText() ) ;
    Assert.assertEquals( 0, treepath.getTop().getChildAt( 0 ).getChildCount() ) ;

    Assert.assertEquals( "child1", treepath.getTop().getChildAt( 1 ).getText() ) ;
    Assert.assertEquals( 0, treepath.getTop().getChildAt( 1 ).getChildCount() ) ;

  }

  @Test
  public void removeBottom() {
    final MutableTree grandParent = new DefaultMutableTree( "grandParent" ) ;
    final MutableTree parent = new DefaultMutableTree( "parent" ) ;
    final MutableTree child1 = new DefaultMutableTree( "child1" ) ;
    final MutableTree child2 = new DefaultMutableTree( "child2" ) ;
    parent.addChild( child1 ) ;
    parent.addChild( child2 ) ;
    grandParent.addChild( parent ) ;

    final Treepath treepath = Treepath.create( grandParent, child2 ) ;
    final Treepath afterRemoval = TreeTools.removeBottom( treepath ) ;


    Assert.assertEquals( 2, afterRemoval.getHeight() ) ;

    Assert.assertEquals( 1, afterRemoval.getTreeAtHeight( 1 ).getChildCount() ) ;
    Assert.assertEquals( "grandParent", afterRemoval.getTreeAtHeight( 1 ).getText() ) ;

    Assert.assertEquals( 1, afterRemoval.getTreeAtHeight( 0 ).getChildCount() ) ;
    Assert.assertEquals( "parent", afterRemoval.getTreeAtHeight( 0 ).getText() ) ;

    Assert.assertSame( child1, afterRemoval.getTreeAtHeight( 0 ).getChildAt( 0 ) ) ;



  }

  @Test
  public void moveLeftDown() {
    final MutableTree parent = new DefaultMutableTree( "parent" ) ;
    final MutableTree child = new DefaultMutableTree( "child" ) ;
    final MutableTree moving = new DefaultMutableTree( "moving" ) ;
    parent.addChild( child ) ;
    parent.addChild( moving ) ;
    final Treepath original = Treepath.create( parent, moving ) ;

    final Treepath moved = TreeTools.moveLeftDown( original ) ;

    // Moved child not included in new treepath!
    Assert.assertEquals( 2, moved.getHeight() ) ;

    Assert.assertEquals( "parent", moved.getTreeAtHeight( 1 ).getText() ) ;
    Assert.assertEquals( 1, moved.getTreeAtHeight( 1 ).getChildCount() ) ;

    Assert.assertEquals( "child", moved.getTreeAtHeight( 0 ).getText() ) ;
    Assert.assertEquals( 1, moved.getTreeAtHeight( 0 ).getChildCount() ) ;

    Assert.assertEquals( "moving", moved.getTreeAtHeight( 0 ).getChildAt( 0 ).getText() ) ;
    Assert.assertEquals( 0, moved.getTreeAtHeight( 0 ).getChildAt( 0 ).getChildCount() ) ;


  }


}

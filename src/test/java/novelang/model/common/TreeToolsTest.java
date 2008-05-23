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
  public void testReparentOk() {
    final MutableTree parent = new DefaultMutableTree( "parent" ) ;
    final MutableTree child = new DefaultMutableTree( "child" ) ;
    final MutableTree grandChild = new DefaultMutableTree( "grandChild" ) ;
    child.addChild( grandChild ) ;
    parent.addChild( child ) ;
    final Treepath original = Treepath.create( parent, grandChild ) ;

    final Tree newGrandChild = new DefaultMutableTree( "newGrandChild" ) ;
    final Treepath reparented = TreeTools.reparent( original, newGrandChild ) ;

    Assert.assertEquals( 3, reparented.getHeight() ) ;

    Assert.assertEquals( "parent", reparented.getTreeAtHeight( 2 ).getText() ) ;
    Assert.assertEquals( 1, reparented.getTreeAtHeight( 2 ).getChildCount() ) ;

    Assert.assertEquals( "child", reparented.getTreeAtHeight( 1 ).getText() ) ;
    Assert.assertEquals( 1, reparented.getTreeAtHeight( 1 ).getChildCount() ) ;

    Assert.assertEquals( "newGrandChild", reparented.getTreeAtHeight( 0 ).getText() ) ;
    Assert.assertEquals( 0, reparented.getTreeAtHeight( 0 ).getChildCount() ) ;


  }

  @Test
  public void addSiblingAtRightOk() {
    final MutableTree parent = new DefaultMutableTree( "parent" ) ;
    final MutableTree child0 = new DefaultMutableTree( "child0" ) ;
    final Tree child1 = new DefaultMutableTree( "child1" ) ;
    parent.addChild( child0 ) ;

    final Treepath treepath = TreeTools.addSiblingAtRight( Treepath.create( parent ), child1 ) ;

    Assert.assertEquals( 2, treepath.getStart().getChildCount() ) ;
    Assert.assertEquals( "parent", treepath.getStart().getText() ) ;

    Assert.assertEquals( 0, treepath.getStart().getChildAt( 0 ).getChildCount() ) ;
    Assert.assertEquals( "child0", treepath.getStart().getChildAt( 0 ).getText() ) ;

    Assert.assertEquals( 0, treepath.getStart().getChildAt( 1 ).getChildCount() ) ;
    Assert.assertEquals( "child1", treepath.getStart().getChildAt( 1 ).getText() ) ;

  }
}

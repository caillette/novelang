/*
 * Copyright (C) 2010 Laurent Caillette
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

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests for {@link TreeTools}.
 *
 * @author Laurent Caillette
 */
public class TreeToolsTest {

  @Test
  public void testAddChildAtPosition0() {
    final MyTree root = MyTree.create(
        "root",
        MyTree.create( "child0" ),
        MyTree.create( "child1" )
    ) ;
    final MyTree modified = TreeTools.add( root, MyTree.create( "new" ), 0 ) ;

    assertEquals( 3, modified.getChildCount() ) ;
    assertEquals( "new", modified.getChildAt( 0 ).getPayload() ) ;
    assertEquals( "child0", modified.getChildAt( 1 ).getPayload() ) ;
    assertEquals( "child1", modified.getChildAt( 2 ).getPayload() ) ;
  }

  @Test
  public void testAddChildAtPosition1() {
    final MyTree root = MyTree.create(
        "root",
        MyTree.create( "child0" ),
        MyTree.create( "child1" )
    ) ;
    final MyTree modified = TreeTools.add( root, MyTree.create( "new" ), 1 ) ;

    assertEquals( 3, modified.getChildCount() ) ;
    assertEquals( "child0", modified.getChildAt( 0 ).getPayload() ) ;
    assertEquals( "new", modified.getChildAt( 1 ).getPayload() ) ;
    assertEquals( "child1", modified.getChildAt( 2 ).getPayload() ) ;
  }

  @Test
  public void testAddChildAtPosition2() {
    final MyTree root = MyTree.create(
        "root",
        MyTree.create( "child0" ),
        MyTree.create( "child1" )
    ) ;
    final MyTree modified = TreeTools.add( root, MyTree.create( "new" ), 2 ) ;

    assertEquals( 3, modified.getChildCount() ) ;
    assertEquals( "child0", modified.getChildAt( 0 ).getPayload() ) ;
    assertEquals( "child1", modified.getChildAt( 1 ).getPayload() ) ;
    assertEquals( "new", modified.getChildAt( 2 ).getPayload() ) ;
  }


}

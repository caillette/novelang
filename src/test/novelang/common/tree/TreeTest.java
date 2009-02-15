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

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tets for {@link ImmutableTree}.
 * 
 * @author Laurent Caillette
 */
public class TreeTest {

  @Test
  public void childlessCreation() {
    final MyTree myTree = MyTree.create( "childless" ) ;
    assertEquals( 0, myTree.getChildCount() ) ;
  }

  @Test( expected = ArrayIndexOutOfBoundsException.class ) 
  public void detectIllegalChildAccess() {
    final MyTree myTree = MyTree.create( "childless" ) ;
    myTree.getChildAt( 1 ) ;
  }

  @Test
  public void simpleCreation() {
    final MyTree root = MyTree.create(
        "root",
        MyTree.create( "left" ),
        MyTree.create( "right" )
    ) ;
    assertEquals( "root", root.getPayload() ) ;
    assertEquals( "left", root.getChildAt( 0 ).getPayload() ) ;
    assertEquals( "right", root.getChildAt( 1 ).getPayload() ) ;
  }

  @Test
  public void addChildFist() {
    MyTree root = MyTree.create( "root", MyTree.create( "initial" ) ) ;
    root = TreeTools.addFirst( root, MyTree.create( "new" ) ) ;

    assertEquals( "root", root.getPayload() ) ;
    assertEquals( "new", root.getChildAt( 0 ).getPayload() ) ;
    assertEquals( "initial", root.getChildAt( 1 ).getPayload() ) ;

  }

  @Test
  public void addChildLast() {
    MyTree root = MyTree.create( "root", MyTree.create( "initial" ) ) ;
    root = TreeTools.addLast( root, MyTree.create( "new" ) ) ;

    assertEquals( "root", root.getPayload() ) ;
    assertEquals( "initial", root.getChildAt( 0 ).getPayload() ) ;
    assertEquals( "new", root.getChildAt( 1 ).getPayload() ) ;

  }

  @Test
  public void remove() {
    final MyTree root = MyTree.create(
        "root",
        MyTree.create( "0" ),
        MyTree.create( "1" ),
        MyTree.create( "2" )
    ) ;

    final MyTree remove0 = TreeTools.remove( root, 0 ) ;

    assertEquals( 2, remove0.getChildCount() ) ;
    assertEquals( "1", remove0.getChildAt( 0 ).getPayload() ) ;
    assertEquals( "2", remove0.getChildAt( 1 ).getPayload() ) ;

    final MyTree remove02 = TreeTools.remove( remove0, 1 ) ;

    assertEquals( 1, remove02.getChildCount() ) ;
    assertEquals( "1", remove0.getChildAt( 0 ).getPayload() ) ;
  }

  @Test
  public void replace() {
    final MyTree root = MyTree.create(
        "root",
        MyTree.create( "0" ),
        MyTree.create( "1" ),
        MyTree.create( "2" )
    ) ;
    final MyTree new1 = MyTree.create( "new1" ) ;
    final MyTree replace1 = TreeTools.replace( root, 1, new1 ) ;

    assertEquals( 3, replace1.getChildCount() ) ;
    assertEquals( "0", replace1.getChildAt( 0 ).getPayload() ) ;
    assertEquals( "new1", replace1.getChildAt( 1 ).getPayload() ) ;
    assertEquals( "2", replace1.getChildAt( 2 ).getPayload() ) ;

    final MyTree new2 = MyTree.create( "new2" ) ;
    final MyTree replace12 = TreeTools.replace( replace1, 2, new2 ) ;

    assertEquals( 3, replace12.getChildCount() ) ;
    assertEquals( "0", replace12.getChildAt( 0 ).getPayload() ) ;
    assertEquals( "new1", replace12.getChildAt( 1 ).getPayload() ) ;
    assertEquals( "new2", replace12.getChildAt( 2 ).getPayload() ) ;

    final MyTree new0 = MyTree.create( "new0" ) ;
    final MyTree replace120 = TreeTools.replace( replace12, 0, new0 ) ;

    assertEquals( 3, replace120.getChildCount() ) ;
    assertEquals( "new0", replace120.getChildAt( 0 ).getPayload() ) ;
    assertEquals( "new1", replace120.getChildAt( 1 ).getPayload() ) ;
    assertEquals( "new2", replace120.getChildAt( 2 ).getPayload() ) ;

  }

  @Test @Ignore
  public void getChildren() {
    final MyTree child0 = MyTree.create( "0" ) ;
    final MyTree child1 = MyTree.create( "1" ) ;
    final MyTree child2 = MyTree.create( "2" ) ;
    final MyTree root = MyTree.create( "root", child0, child1, child2 ) ;
    final Iterator< ? extends MyTree > iterator = root.getChildren().iterator() ;

    assertTrue( iterator.hasNext() ) ;
    assertEquals( "0", iterator.next().getPayload() ) ;

    assertTrue( iterator.hasNext() ) ;
    assertEquals( "1", iterator.next().getPayload() ) ;

    assertTrue( iterator.hasNext() ) ;
    assertEquals( "2", iterator.next().getPayload() ) ;

    assertFalse( iterator.hasNext() ) ;

  }

  @Test( expected = NoSuchElementException.class )
  public void getChildrenNoNext() {
    final MyTree root = MyTree.create( "root" ) ;
    root.getChildren().iterator().next() ;
  }

}

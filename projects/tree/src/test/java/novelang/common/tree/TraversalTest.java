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

import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Tests for {@link Traversal}.
 *
 * @author Laurent Caillette
 */
public class TraversalTest {


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

    final Traversal.MirroredPostorder< MyTree > mirroredPostorder =
        Traversal.MirroredPostorder.create() ;

    final Treepath< MyTree > treepathTo5 = mirroredPostorder.next( treepathTo6 ) ;
    assertSame( t5, treepathTo5.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathTo4 = mirroredPostorder.next( treepathTo5 ) ;
    assertSame( t4, treepathTo4.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathTo3 = mirroredPostorder.next( treepathTo4 ) ;
    assertSame( t3, treepathTo3.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathTo2 = mirroredPostorder.next( treepathTo3 ) ;
    assertSame( t2, treepathTo2.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathTo1 = mirroredPostorder.next( treepathTo2 ) ;
    assertSame( t1, treepathTo1.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathTo0 = mirroredPostorder.next( treepathTo1 ) ;
    assertSame( t0, treepathTo0.getTreeAtEnd() ) ;

    final Treepath< MyTree > nextTo0 = mirroredPostorder.next( treepathTo0 ) ;
    assertNull( nextTo0 ) ;

  }

  @Test
  public void lastInMirroredPostorder() {

    final MyTree t3 = MyTree.create( "3" ) ;           //      t0
    final MyTree t4 = MyTree.create( "4" ) ;           //      |
    final MyTree t6 = MyTree.create( "6" ) ;           //      t1
    final MyTree t2 = MyTree.create( "2", t3, t4 ) ;   //     /  \
    final MyTree t5 = MyTree.create( "5", t6 ) ;       //   t2    t5
    final MyTree t1 = MyTree.create( "1", t2, t5 ) ;   //  /  \   |
    final MyTree t0 = MyTree.create( "0", t1 ) ;       // t3  t4  t6

    final Treepath< MyTree > treepathTo0 = Treepath.create( t0 ) ;

    final Traversal.MirroredPostorder< MyTree > mirroredPostorder =
        Traversal.MirroredPostorder.create() ;

    final Treepath< MyTree > last6 = mirroredPostorder.first( treepathTo0 ) ;
    assertSame( t6, last6.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathFrom5To5 = Treepath.create( t5 ) ;
    final Treepath< MyTree > lastFrom5To5 = mirroredPostorder.first( treepathFrom5To5 ) ;
    assertSame( t6, lastFrom5To5.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathFrom6To6 = Treepath.create( t6 ) ;
    final Treepath< MyTree > lastFrom6To6 = mirroredPostorder.first( treepathFrom6To6 ) ;
    assertSame( t6, lastFrom6To6.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathFrom0To2 = Treepath.create( t0, 0, 0 ) ;
    final Treepath< MyTree > lastFrom0To2 = mirroredPostorder.first( treepathFrom0To2 ) ;
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

    final Traversal.Preorder< MyTree > preorder = Traversal.Preorder.create() ;

    final Treepath< MyTree > first = preorder.first( treepathTo0 ) ;
    assertSame( t0, first.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathTo1 = preorder.next( treepathTo0 ) ;
    assertSame( t1, treepathTo1.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathTo2 = preorder.next( treepathTo1 ) ;
    assertSame( t2, treepathTo2.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathTo3 = preorder.next( treepathTo2 ) ;
    assertSame( t3, treepathTo3.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathTo4 = preorder.next( treepathTo3 ) ;
    assertSame( t4, treepathTo4.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathTo5 = preorder.next( treepathTo4 ) ;
    assertSame( t5, treepathTo5.getTreeAtEnd() ) ;

    final Treepath< MyTree > treepathTo6 = preorder.next( treepathTo5 ) ;
    assertSame( t6, treepathTo6.getTreeAtEnd() ) ;

    final Treepath< MyTree > nextTo6 = preorder.next( treepathTo6 ) ;
    assertNull( nextTo6 ) ;


  }  
}

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import junit.framework.AssertionFailedError;

/**
 * Tests for {@link novelang.model.common.tree.Treepath}.
 *
 * @author Laurent Caillette
 */
public class TreepathTest {

  private static final Logger LOGGER = LoggerFactory.getLogger( TreepathTest.class ) ;

  @Test
  public void testTreepathLength1() {
    final MyTree tree = MyTree.create( "tree" ) ;
    final Treepath< MyTree > treepath = Treepath.create( tree ) ;

    Assert.assertEquals( 1, treepath.getLength() ) ;
    assertSame( tree, treepath.getStart() ) ;
    assertSame( tree, treepath.getTreeAtEnd() ) ;
    assertSame( tree, treepath.getTreeAtDistance( 0 ) ) ;
  }

  @Test( expected = IllegalArgumentException.class )
  public void testTreepathLength1Bad() {
    final MyTree tree = MyTree.create( "tree" ) ;
    final Treepath< MyTree > treepath = Treepath.create( tree ) ;
    assertSame( tree, treepath.getTreeAtDistance( 1 ) ) ;
  }

  @Test
  public void testTreepathLength2() {
    final MyTree child = MyTree.create( "child" ) ;
    final MyTree parent = MyTree.create( "parent", child ) ;

    final Treepath< MyTree > treepath = Treepath.< MyTree >create( parent, 0 ) ;
                                              // ^ IntelliJ IDEA 7.0.3 breaks without this.
    print("Treepath: ", treepath ) ;

    Assert.assertEquals( 2, treepath.getLength() ) ;
    assertSame( parent, treepath.getTreeAtDistance( 1 ) ) ;
    assertSame( child, treepath.getTreeAtDistance( 0 )) ;
    assertSame( parent, treepath.getStart() ) ;
    assertSame( child, treepath.getTreeAtEnd() ) ;
  }

  @Test
  public void testTreepathLength3() {
    final MyTree grandChild = MyTree.create( "grandChild" ) ;
    final MyTree child = MyTree.create( "child", grandChild ) ;
    final MyTree parent = MyTree.create( "parent", child ) ;

    final Treepath< MyTree > treepath = Treepath.create( parent, 0, 0 ) ;
    print("Treepath: ", treepath ) ;

    Assert.assertEquals( 3, treepath.getLength() ) ;
    assertSame( parent, treepath.getStart() ) ;
    assertSame( grandChild, treepath.getTreeAtEnd() ) ;
    assertSame( parent, treepath.getTreeAtDistance( 2 ) ) ;
    assertSame( child, treepath.getTreeAtDistance( 1 ) ) ;
    assertSame( grandChild, treepath.getTreeAtDistance( 0 ) ) ;
  }

// =======
// Fixture
// =======

  private static void print( String message, Treepath< MyTree > treepath ) {
    boolean first = true ;
    final StringBuffer buffer = new StringBuffer() ;
    for( int i = 0 ; i < treepath.getLength() ; i++ ) {
      if( first ) {
        first = false ;
      } else {
        buffer.append( " -> " ) ;
      }
      buffer.append( "{" ).append( treepath.getTreeAtDistance( i ).getPayload() ).append( "}" ) ;
    }
    LOGGER.debug( message + buffer.toString() ) ;
  }

  private static void assertSame( MyTree expected, MyTree actual ) {
    final String message =
        "Expected: {" + ( null == expected ? "null" : expected.getPayload() ) + "} " +
        "got {" + ( null == actual ? "null" : actual.getPayload() ) + "}"
    ;
    if( expected != actual ) {
      throw new AssertionFailedError( message ) ;
    }
  }

}
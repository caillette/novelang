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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.model.implementation.DefaultMutableTree;
import junit.framework.AssertionFailedError;

/**
 * Tests for {@link Treepath}.
 *
 * @author Laurent Caillette
 */
public class TreepathTest {

  private static final Logger LOGGER = LoggerFactory.getLogger( TreepathTest.class ) ;

  @Test
  public void testTreepathHeight1() {
    final MutableTree tree = new DefaultMutableTree( "tree" ) ;
    final Treepath treepath = Treepath.create( tree ) ;

    Assert.assertEquals( 1, treepath.getHeight() ) ;
    assertSame( tree, treepath.getTop() ) ;
    assertSame( tree, treepath.getBottom() ) ;
    assertSame( tree, treepath.getTreeAtHeight( 0 ) ) ;
  }

  @Test( expected = IllegalArgumentException.class )
  public void testTreepathHeight1Bad() {
    final MutableTree tree = new DefaultMutableTree( "tree" ) ;
    final Treepath treepath = Treepath.create( tree ) ;
    assertSame( tree, treepath.getTreeAtHeight( 1 ) ) ;
  }

  @Test
  public void testTreepathHeight2() {
    final MutableTree parent = new DefaultMutableTree( "parent" ) ;
    final MutableTree child = new DefaultMutableTree( "child" ) ;
    parent.addChild( child ) ;

    final Treepath treepath = Treepath.create( parent, child ) ;
    print("Treepath: ", treepath ) ;

    Assert.assertEquals( 2, treepath.getHeight() ) ;
    assertSame( parent, treepath.getTreeAtHeight( 1 ) ) ;
    assertSame( child, treepath.getTreeAtHeight( 0 )) ;
    assertSame( parent, treepath.getTop() ) ;
    assertSame( child, treepath.getBottom() ) ;
  }

  @Test
  public void testFindHeight4() {
    final MutableTree grandParent = new DefaultMutableTree( "grandParent" ) ;
    final MutableTree parent = new DefaultMutableTree( "parent" ) ;
    final MutableTree child = new DefaultMutableTree( "child" ) ;
    final MutableTree grandChild = new DefaultMutableTree( "grandchild" ) ;
    grandParent.addChild( parent ) ;
    parent.addChild( child ) ;
    child.addChild( grandChild ) ;

    final Treepath findResult = Treepath.find( grandParent, child ) ;
    print("Found: ", findResult ) ;

    Assert.assertEquals( 3, findResult.getHeight() ) ;
    assertSame( grandParent, findResult.getBottom() ) ;
    assertSame( child, findResult.getTop() ) ;

    // Warning: inverted path!
    assertSame( grandParent, findResult.getTreeAtHeight( 0 ) ) ;
    assertSame( parent, findResult.getTreeAtHeight( 1 ) ) ;
    assertSame( child, findResult.getTreeAtHeight( 2 ) ) ;
  }

  @Test
  public void testTreepathHeight3() {
    final MutableTree parent = new DefaultMutableTree( "parent" ) ;
    final MutableTree child = new DefaultMutableTree( "child" ) ;
    final MutableTree grandChild = new DefaultMutableTree( "grandchild" ) ;
    parent.addChild( child ) ;
    child.addChild( grandChild ) ;

    final Treepath treepath = Treepath.create( parent, grandChild ) ;
    print("Treepath: ", treepath ) ;

    Assert.assertEquals( 3, treepath.getHeight() ) ;
    assertSame( parent, treepath.getTop() ) ;
    assertSame( grandChild, treepath.getBottom() ) ;
    assertSame( parent, treepath.getTreeAtHeight( 2 ) ) ;
    assertSame( child, treepath.getTreeAtHeight( 1 ) ) ;
    assertSame( grandChild, treepath.getTreeAtHeight( 0 ) ) ;
  }

  @Test
  public void testInvert() {
    final MutableTree parent = new DefaultMutableTree( "parent" ) ;
    final MutableTree child = new DefaultMutableTree( "child" ) ;
    final MutableTree grandChild = new DefaultMutableTree( "grandchild" ) ;
    parent.addChild( child ) ;
    child.addChild( grandChild ) ;
    final Treepath treepath =
        new Treepath( new Treepath( new Treepath( null, parent ), child ), grandChild ) ;
    final Treepath inverted = Treepath.invert( treepath ) ;

    print( "Treepath: ", treepath ) ;
    print( "Inverted treepath: ", inverted ) ;

    Assert.assertEquals( 3, inverted.getHeight() ) ;
    assertSame( parent, inverted.getTreeAtHeight( 0 ) ) ;
    assertSame( child, inverted.getTreeAtHeight( 1 ) ) ;
    assertSame( grandChild, inverted.getTreeAtHeight( 2 ) ) ;
    assertSame( grandChild, inverted.getTop() ) ;
    assertSame( parent, inverted.getBottom() ) ;
  }

// =======
// Fixture
// =======

  private static void print( String message, Treepath treepath ) {
    boolean first = true ;
    final StringBuffer buffer = new StringBuffer() ;
    for( int i = 0 ; i < treepath.getHeight() ; i++ ) {
      if( first ) {
        first = false ;
      } else {
        buffer.append( " -> " ) ;
      }
      buffer.append( "{" ).append( treepath.getTreeAtHeight( i ).getText() ).append( "}" ) ;
    }
    LOGGER.debug( message + buffer.toString() ) ;
  }

  private void assertSame( Tree expected, Tree actual ) {
    final String message =
        "Expected: {" + ( null == expected ? "null" : expected.getText() ) + "} " +
        "got {" + ( null == actual ? "null" : actual.getText() ) + "}"
    ;
    if( expected != actual ) {
      throw new AssertionFailedError( message ) ;
    }
  }

}

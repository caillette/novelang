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

package org.novelang.parser.antlr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.novelang.common.Location;
import org.novelang.common.LocationFactory;
import org.novelang.common.SimpleTree;
import org.novelang.common.SyntacticTree;
import org.novelang.common.tree.Treepath;
import org.novelang.parser.NodeKind;
import org.novelang.treemangling.SeparatorsMangler;

/**
 * Helps building {@link org.novelang.common.SyntacticTree}s for tests.
 * Dependency towards {@link org.novelang.parser.antlr.CustomTree} is just for implementation,
 * it helps tree comparison.
 *
 * @author Laurent Caillette
 */
public class TreeFixture {

  public static final LocationFactory LOCATION_FACTORY = new LocationFactory() {
    @Override
    public Location createLocation( final int line, final int column ) {
      return new Location( "", line, column ) ;
    }
    @Override
    public Location createLocation() {
      return new Location( "" ) ;
    }
  } ;

  private TreeFixture() {}

  public static SyntacticTree tree( final NodeKind nodeKind, final SyntacticTree... children ) {
    return new SimpleTree( nodeKind, children ) ;
  }

  public static SyntacticTree tree( final NodeKind nodeKind, final String text ) {
    return new SimpleTree( nodeKind, new SimpleTree( text ) ) ;
  }

  public static SyntacticTree tree( final NodeKind nodeKind ) {
    return new SimpleTree( nodeKind ) ;

  }

  public static SyntacticTree tree(
      final NodeKind nodeKind,
      final Location location,
      final SyntacticTree... children
  ) {
    return new SimpleTree( nodeKind, location, children ) ;
  }

  public static SyntacticTree tree(
      final NodeKind nodeKind,
      final Location location,
      final String text
  ) {
    return new SimpleTree( nodeKind, location, new SimpleTree( text ) ) ;
  }


  public static SyntacticTree tree( final NodeKind nodeKind, final NodeKind... children ) {
    final SyntacticTree[] childTrees = new SyntacticTree[ children.length ] ;
    for( int i = 0; i < children.length ; i++ ) {
      final NodeKind child = children[ i ] ;
      childTrees[ i ] = new SimpleTree( child ) ;
    }
    return new SimpleTree( nodeKind, childTrees ) ;
  }

  public static SyntacticTree tree( final String text ) {
    return new SimpleTree( text ) ;
  }

  public static SyntacticTree tree( final String text, final SyntacticTree... children ) {
    return new SimpleTree( text, children ) ;
  }

  public static SyntacticTree multiTokenTree( final String text ) {
    final SyntacticTree[] children = new SyntacticTree[ text.length() ] ;
    for( int i = 0 ; i < text.length() ; i++ ) {
      final String s = String.valueOf( text.charAt( i ) ) ;
      children[ i ] = tree( s ) ;
    }
    return new SimpleTree( "", children ) ;
  }

  public static void assertEqualsWithSeparators(
      final SyntacticTree expected,
      final SyntacticTree actual
  ) {
    assertEqualsWithSeparators( Treepath.create( expected ), Treepath.create( actual ) ) ;
  }
  
  public static void assertEqualsWithSeparators(
      final Treepath< SyntacticTree > expected,
      final Treepath< SyntacticTree > actual
  ) {
    assertEqualsWithSeparators( expected, actual, false ) ;
  }

  public static void assertEqualsWithSeparators(
      final Treepath< SyntacticTree > expected,
      final Treepath< SyntacticTree > actual,
      final boolean checkLocation
  ) {
    final int expectedLength = expected.getLength();
    final int actualLength = actual.getLength();
    Assert.assertEquals( "Treepath height", expectedLength, actualLength ) ;
    for( int i = 0 ; i < expected.getLength() ; i++ ) {
      assertEquals(
          expected.getTreeAtDistance( i ),
          actual.getTreeAtDistance( i ),
          checkLocation
      ) ;
    }
  }

  public static void assertEqualsNoSeparators( 
      final SyntacticTree expected, 
      final SyntacticTree actual 
  ) {
    assertEquals( expected, SeparatorsMangler.removeSeparators( actual ) ) ;
  }
  
  public static void assertEquals( final SyntacticTree expected, final SyntacticTree actual ) {
    assertEquals( expected, actual, false ) ;
  }

  public static void assertEquals(
      final SyntacticTree expected,
      final SyntacticTree actual,
      final boolean checkLocation
  ) {
    try {
      assertEqualsNoMessage( expected, actual, checkLocation ) ;
    } catch( AssertionError e ) {
      final AssertionError assertionError = new AssertionError(
          e.getMessage() +
              "\nExpected:\n  " + asString( expected ) +
              "\nActual:\n  " + asString( actual )
      ) ;
      assertionError.setStackTrace( e.getStackTrace() ) ;
      throw assertionError;
    }
  }
  private static void assertEqualsNoMessage( 
      final SyntacticTree expected, 
      final SyntacticTree actual 
  ) {
    assertEqualsNoMessage( expected, actual, false ) ;
  }

  private static void assertEqualsNoMessage(
      final SyntacticTree expected,
      final SyntacticTree actual,
      boolean checkLocation
  ) {
    checkLocation = checkLocation && ! expected.isOneOf( NodeKind.WORD_ ) ;
    if( NodeKind.LINES_OF_LITERAL == expected.getNodeKind()
     && NodeKind.LINES_OF_LITERAL == actual.getNodeKind()
    ) {
/*
      Assert.assertEquals(
          "Ill-formed test: expected LITERAL node must have exactly one child",
          1,
          expected.getChildCount()
      ) ;
      Assert.assertEquals( 1, actual.getChildCount() ) ;
*/
      Assert.assertTrue(
          "Ill-formed test: expected LITERAL node must have at least one child",
          expected.getChildCount() >= 1
      ) ;
      Assert.assertEquals( expected.getChildCount(), actual.getChildCount() ) ;

      final int lastIndex = expected.getChildCount() - 1 ;

      for( int index = 0 ; index < expected.getChildCount() ; index++ ) {
        final SyntacticTree expectedChild = expected.getChildAt( index ) ;
        final SyntacticTree actualChild = actual.getChildAt( index ) ;
        assertEqualsNoMessage( expectedChild, actualChild, checkLocation ) ;
      }

      assertPayloadEquals(
          expected.getChildAt( lastIndex ),
          actual.getChildAt( lastIndex ),
          checkLocation
      ) ;
    } else {
      assertPayloadEquals( expected, actual, checkLocation ) ;
      Assert.assertEquals( expected.getChildCount(), actual.getChildCount() ) ;
      for( int index = 0 ; index < expected.getChildCount() ; index++ ) {
        final SyntacticTree expectedChild = expected.getChildAt( index ) ;
        final SyntacticTree actualChild = actual.getChildAt( index ) ;
        assertEqualsNoMessage( expectedChild, actualChild, checkLocation ) ;
      }
    }
  }

  private static void assertPayloadEquals(
      final SyntacticTree expected,
      final SyntacticTree actual,
      final boolean checkLocation
  ) {
    Assert.assertEquals( expected.getText(), actual.getText() ) ;
    if( checkLocation ) {
      Assert.assertEquals(
          "Unexpected location object on " + TreeFixture.asString( actual ),
          expected.getLocation(), 
          actual.getLocation() 
      ) ;
    }
  }


  private static final Pattern SPACE_NORMALIZER_PATTERN = Pattern.compile( " +" ) ;

  /**
   * Replace sequence of spaces by a single one.
   */
  public static String normalizeSpaces( final String s ) {
    final Matcher matcher = SPACE_NORMALIZER_PATTERN.matcher( s ) ;
    final StringBuffer buffer = new StringBuffer() ;
    while( matcher.find() ) {
      matcher.appendReplacement( buffer, " " ) ;
    }
    matcher.appendTail( buffer ) ;
    return buffer.toString() ;
  }

  public static String asString( final SyntacticTree tree ) {
    if( null == tree ) {
      return "<null>" ;
    } else {
      return tree.toStringTree() ;
    }

  }


}

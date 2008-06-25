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

package novelang.parser.antlr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import novelang.model.common.Location;
import novelang.model.common.LocationFactory;
import novelang.model.common.NodeKind;
import novelang.model.common.SyntacticTree;
import novelang.model.common.tree.Treepath;
import novelang.model.common.SimpleTree;

/**
 * Helps building {@link novelang.model.common.SyntacticTree}s for tests.
 * Dependency towards {@link novelang.parser.antlr.CustomTree} is just for implementation,
 * it helps tree comparison.
 *
 * @author Laurent Caillette
 */
public class TreeFixture {

  public static final LocationFactory LOCATION_FACTORY = new LocationFactory() {
    public Location createLocation( int line, int column ) {
      return new Location( "", line, column ) ;
    }
  } ;

  public static SyntacticTree tree( NodeKind nodeKind, SyntacticTree... children ) {
    return new SimpleTree( nodeKind.name(), children ) ;
  }

  public static SyntacticTree tree( NodeKind nodeKind, String text ) {
    return new SimpleTree( nodeKind.name(), new SimpleTree( text ) ) ;
  }

  public static SyntacticTree tree( NodeKind nodeKind ) {
    return new SimpleTree( nodeKind.name() ) ;

  }

  public static SyntacticTree tree( NodeKind nodeKind, NodeKind... children ) {
    final SyntacticTree[] childTrees = new SyntacticTree[ children.length ] ;
    for( int i = 0; i < children.length ; i++ ) {
      final NodeKind child = children[ i ] ;
      childTrees[ i ] = new SimpleTree( child.name() ) ;
    }
    return new SimpleTree( nodeKind.name(), childTrees ) ;
  }

  public static SyntacticTree tree( String text ) {
    return new SimpleTree( text ) ;
  }

  public static SyntacticTree tree( String text, SyntacticTree... children ) {
    return new SimpleTree( text, children ) ;
  }

  public static SyntacticTree multiTokenTree( String text ) {
    final SyntacticTree[] children = new SyntacticTree[ text.length() ] ;
    for( int i = 0 ; i < text.length() ; i++ ) {
      final String s = String.valueOf( text.charAt( i ) ) ;
      children[ i ] = tree( s ) ;
    }
    return new SimpleTree( "", children ) ;
  }

  public static void assertEquals(
      Treepath<SyntacticTree> expected,
      Treepath<SyntacticTree> actual
  ) {
    Assert.assertEquals( "Treepath height", expected.getLength(), actual.getLength() ) ;
    for( int i = 0 ; i < expected.getLength() ; i++ ) {
      assertEquals(
          expected.getTreeAtDistance( i ),
          actual.getTreeAtDistance( i )
      ) ;
    }
  }

  public static void assertEquals( SyntacticTree expected, SyntacticTree actual ) {
    try {
      assertEqualsNoMessage( expected, actual ) ;
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
  private static void assertEqualsNoMessage( SyntacticTree expected, SyntacticTree actual ) {
    if( NodeKind.LITTERAL.isRoot( expected ) && NodeKind.LITTERAL.isRoot( actual ) ) {
      Assert.assertEquals(
          "Ill-formed test: expected LITTERAL node must have exactly one child",
          1,
          expected.getChildCount()
      ) ;
      Assert.assertEquals( 1, actual.getChildCount() ) ;
      Assert.assertEquals( expected.getChildAt( 0 ).getText(), actual.getChildAt( 0 ).getText() ) ;
    } else {
      Assert.assertEquals( expected.getText(), actual.getText() ) ;
      Assert.assertEquals( expected.getChildCount(), actual.getChildCount() ) ;
      for( int index = 0 ; index < expected.getChildCount() ; index++ ) {
        final SyntacticTree expectedChild = expected.getChildAt( index ) ;
        final SyntacticTree actualChild = actual.getChildAt( index ) ;
        assertEqualsNoMessage( expectedChild, actualChild ); ;
      }
    }
  }


  private static final Pattern SPACE_NORMALIZER_PATTERN = Pattern.compile( " +" ) ;

  /**
   * Replace sequence of spaces by a single one.
   */
  public static String normalizeSpaces( String s ) {
    final Matcher matcher = SPACE_NORMALIZER_PATTERN.matcher( s ) ;
    final StringBuffer buffer = new StringBuffer() ;
    while( matcher.find() ) {
      matcher.appendReplacement( buffer, " " ) ;
    }
    matcher.appendTail( buffer ) ;
    return buffer.toString() ;
  }

  public static String asString( SyntacticTree tree ) {
    if( null == tree ) {
      return "<null>" ;
    } else {
      return tree.toStringTree() ;
    }

  }


}

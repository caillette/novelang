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
import novelang.common.Location;
import novelang.common.LocationFactory;
import novelang.common.SimpleTree;
import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.parser.NodeKind;

/**
 * Helps building {@link novelang.common.SyntacticTree}s for tests.
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

  public static void assertEqualsWithSeparators(
      Treepath< SyntacticTree > expected,
      Treepath< SyntacticTree > actual
  ) {
    int expectedLength = expected.getLength();
    final int actualLength = actual.getLength();
    Assert.assertEquals( "Treepath height", expectedLength, actualLength ) ;
    for( int i = 0 ; i < expected.getLength() ; i++ ) {
      assertEquals(
          expected.getTreeAtDistance( i ),
          actual.getTreeAtDistance( i )
      ) ;
    }
  }

  public static void assertEqualsNoSeparators( 
      SyntacticTree expected, 
      SyntacticTree actual 
  ) {
    assertEquals( expected, TreeFixture.removeSeparators( actual ) ) ;
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
    if( NodeKind.LINES_OF_LITERAL.isRoot( expected ) && NodeKind.LINES_OF_LITERAL.isRoot( actual ) ) {
      Assert.assertEquals(
          "Ill-formed test: expected LITERAL node must have exactly one child",
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
        assertEqualsNoMessage( expectedChild, actualChild ) ;
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


  /**
   * Removes {@link NodeKind#WHITESPACE_} and {@link NodeKind#LINE_BREAK_}
   * tokens in order to ease comparison.
   */
  public static SyntacticTree removeSeparators( SyntacticTree tree ) {
    return removeSeparators( Treepath.create( tree ) ).getTreeAtEnd() ;
  }
  
  public static Treepath< SyntacticTree > removeSeparators( Treepath< SyntacticTree > treepath ) {
    int index = 0 ;
    while( index < treepath.getTreeAtEnd().getChildCount() ) {
      final SyntacticTree child = treepath.getTreeAtEnd().getChildAt( index ) ;
      final Treepath< SyntacticTree > childTreepath = Treepath.create( treepath, index ) ;
      if( child.isOneOf( NodeKind.WHITESPACE_, NodeKind.LINE_BREAK_ ) ) {
        treepath = TreepathTools.removeEnd( childTreepath ) ;
      } else {
        treepath = removeSeparators( childTreepath ).getPrevious() ;
        index++ ;
      }
    }
    return treepath ;
  }

}

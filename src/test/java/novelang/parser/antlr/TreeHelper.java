/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.parser.antlr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.ClassicToken;
import org.junit.Assert;
import novelang.model.common.Location;
import novelang.model.common.LocationFactory;
import novelang.model.common.NodeKind;
import novelang.model.common.Tree;

/**
 * Helps building {@link Tree}s for tests.
 * Dependency towards {@link novelang.parser.antlr.CustomTree} is just for implementation,
 * it helps tree comparison.
 *
 * @author Laurent Caillette
 */
public class TreeHelper {

  public static final LocationFactory LOCATION_FACTORY = new LocationFactory() {
    public Location createLocation( int line, int column ) {
      return new Location( "", line, column ) ;
    }
  } ;

  public static Tree tree( NodeKind nodeKind, Tree... children ) {
    final CustomTree tree = new CustomTree(
        LOCATION_FACTORY,
        new ClassicToken( 0, nodeKind.name() )
    ) ;
    for( final Tree child : children ) {
      tree.addChild( child ) ;
    }
    return tree ;
  }

  public static Tree tree( NodeKind nodeKind, String text ) {
    final CustomTree tree = new CustomTree(
        LOCATION_FACTORY,
        new ClassicToken( 0, nodeKind.name() )
    ) ;
    final Tree child = tree( text ) ;
    tree.addChild( child ) ;
    return tree ;

  }

  public static Tree tree( NodeKind nodeKind ) {
    final CustomTree tree = new CustomTree(
        LOCATION_FACTORY,
        new ClassicToken( 0, nodeKind.name() )
    ) ;
    return tree ;

  }

  public static Tree tree( NodeKind nodeKind, NodeKind... children ) {
    final CustomTree tree = new CustomTree(
        LOCATION_FACTORY,
        new ClassicToken( 0, nodeKind.name() )
    ) ;
    for( final NodeKind child : children ) {
      tree.addChild( tree( child ) ) ;
    }
    return tree ;

  }

  public static Tree tree( String text ) {
    final CustomTree tree = new CustomTree(
        LOCATION_FACTORY,
        new ClassicToken( 0, text )
    ) ;
    return tree ;
  }

  public static Tree multiTokenTree( String text ) {
    final CustomTree tree = new CustomTree( LOCATION_FACTORY, null ) ;
    for( int i = 0 ; i < text.length() ; i++ ) {
      final String s = String.valueOf( text.charAt( i ) ) ;
      final Tree child = tree( s ) ;
      tree.addChild( child ) ;
    }
    return tree ;
  }

  public static void assertEquals( Tree expected, Tree actual ) {
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
        final Tree expectedChild = expected.getChildAt( index ) ;
        final Tree actualChild = actual.getChildAt( index ) ;
        assertEquals( expectedChild, actualChild ) ;
      }
    }

//    Assert.assertEquals(
//        normalizeSpaces( expected.toStringTree() ),
//        normalizeSpaces( actual.toStringTree() )
//    ) ;
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



}

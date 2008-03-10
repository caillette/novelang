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

import org.antlr.runtime.ClassicToken;
import org.antlr.runtime.tree.Tree;
import org.junit.Assert;
import novelang.model.common.Location;
import novelang.model.common.LocationFactory;
import novelang.model.common.NodeKind;
import novelang.parser.antlr.CustomTree;

/**
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
    final CustomTree child = ( CustomTree ) tree( text ) ;
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

  public static void assertEquals( Tree expected, Tree actual ) {
    Assert.assertEquals( expected.toStringTree(), actual.toStringTree() ) ;
  }



}

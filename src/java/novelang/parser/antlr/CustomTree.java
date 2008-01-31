/*
 * Copyright (C) 2006 Laurent Caillette
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

import java.util.List;
import java.util.Iterator;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.Token;
import novelang.model.common.Tree;
import novelang.model.common.LocationFactory;
import novelang.model.common.Location;
import novelang.model.common.NodeKind;

/**
 * @author Laurent Caillette
 */
public class CustomTree extends CommonTree implements Tree {

  private final LocationFactory locationFactory ;
  private Location location ;

  public CustomTree( LocationFactory locationFactory, Token token ) {
		super( token ) ;
    this.locationFactory = locationFactory ;
  }

  public Location getLocation() {
    if( location == null ) {
      location = locationFactory.createLocation( getLine(), getCharPositionInLine() ) ;
    }
    return location ;
  }

  public boolean isOneOf( NodeKind... kinds ) {
    for( NodeKind kind : kinds ) {
      if( NodeKind.is( this, kind ) ) {
        return true ;
      }
    }
    return false ;
  }

  public Tree getChildAt( int i ) {
    return ( Tree ) getChild( i ) ;
  }

  public Iterable< Tree > getChildren() {
    return new Iterable< Tree >() {

      public Iterator< Tree > iterator() {
        return new Iterator< Tree >() {

          private int position = 0 ;

          public boolean hasNext() {
            return position < getChildCount() ;
          }

          public Tree next() {
            final Tree next = getChildAt( position ) ;
            position++ ;
            return next ;
          }

          public void remove() {
            throw new UnsupportedOperationException( "remove()" ) ;
          }
        } ;
      }
    } ;
  }

}

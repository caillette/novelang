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

import org.apache.commons.lang.NullArgumentException;
import com.google.common.collect.ObjectArrays;

/**
 * Immutable implementation of a tree.
 * <p>
 * Please avoid being stupid using reflection to poke into the array.
 *
 * 
 * @author Laurent Caillette
 */
public abstract class Tree< T extends Tree > {

  private final T[] children ;

  public Tree( T[] children ) {
    if( null == children ) {
      this.children = null ;
    } else {
      this.children = children.clone() ;
    }
  }

  protected abstract T adopt( T[] newChildren ) ;

  public final int getChildCount() {
    return null == children ? 0 : children.length ;
  }

  public final T getChildAt( int index ) {
    if( index >= getChildCount() ) {
      throw new IllegalArgumentException(
          "Unsupported index: " + index + " (child count=" + getChildCount() + ")" ) ;
    }
    return children[ index ] ;
  }

  public final T addOnLeft( T newChild ) {
    if( null == newChild ) {
      throw new NullArgumentException( "newChild" ) ;
    }
    final T[] newArray = ObjectArrays.newArray(
        ( Class< T > ) newChild.getClass(), getChildCount() + 1 ) ;
    newArray[ 0 ] = newChild ;
    for( int i = 0 ; i < getChildCount() ; i++ ) {
      newArray[ i + 1 ] = getChildAt( i ) ;
    }
    return adopt( newArray ) ;
  }

  public final T addOnRight( T newChild ) {
    if( null == newChild ) {
      throw new NullArgumentException( "newChild") ;
    }
    final T[] newArray = ObjectArrays.newArray(
        ( Class< T > ) newChild.getClass(), getChildCount() + 1 ) ;
    newArray[ getChildCount() ] = newChild ;
    for( int i = 0 ; i < getChildCount() ; i++ ) {
      newArray[ i ] = getChildAt( i ) ;
    }
    return adopt( newArray ) ;
  }

  public final void remove( int index ) {
    if( index < 0 ) {
      throw new IllegalArgumentException( "Negative index: " + index ) ;
    }
    if( getChildCount() < index ) {
      throw new IllegalArgumentException(
          "Cannot remove child at index " + index + " (child count=" + getChildCount() + ")" ) ;
    }
    final T[] newArray = ObjectArrays.newArray( children, getChildCount() + 1 ) ;
  }

}

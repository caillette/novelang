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

import java.util.List;

import org.apache.commons.lang.NullArgumentException;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Lists;

/**
 * Utility class for manipulating {@link Tree}s through copy-on-change.
 *
 * @author Laurent Caillette
 */
public class TreeTools {

  private TreeTools() {
    throw new Error( "Don't instantiate this class" ) ;
  }


  /**
   * Returns a copy of a {@code Tree} with the {@code newChild} added as first child.
   *
   * @param tree a non-null object.
   * @param newChild a non-null object.
   * @return a non-null object.
   */
  public static < T extends Tree > T addFirst( T tree, T newChild ) {
    if( null == newChild ) {
      throw new NullArgumentException( "newChild" ) ;
    }
    final T[] newArray = ObjectArrays.newArray(
        ( Class< T > ) newChild.getClass(),
        tree.getChildCount() + 1
    ) ;

    newArray[ 0 ] = newChild ;
    for( int i = 0 ; i < tree.getChildCount() ; i++ ) {
      newArray[ i + 1 ] = ( T ) tree.getChildAt( i );
    }
    return ( T ) tree.adopt( newArray ) ;
  }

  /**
   * Returns a copy of a {@code Tree} with the {@code newChild} added as last child.
   *
   * @param tree a non-null object.
   * @param newChild a non-null object.
   * @return a non-null object.
   */
  public static < T extends Tree > T addLast( T tree, T newChild ) {
    if( null == newChild ) {
      throw new NullArgumentException( "newChild") ;
    }
    final T[] newArray = ObjectArrays.newArray(
        ( Class< T > ) newChild.getClass(),
        tree.getChildCount() + 1
    ) ;

    newArray[ tree.getChildCount() ] = newChild ;
    for( int i = 0 ; i < tree.getChildCount() ; i++ ) {
      newArray[ i ] = ( T ) tree.getChildAt( i );
    }
    return ( T ) tree.adopt( newArray );
  }

  /**
   * @param tree a non-null object.
   * @param newChildren a non-null object iterating over non-null objects.
   * @return non-null object.
   * @throws org.apache.commons.lang.NullArgumentException if at least one of {@code newChildren} is null.
   */
  public static < T extends Tree > T addLast( T tree, Iterable< ? extends T > newChildren )
      throws NullArgumentException
  {
    final List< ? extends T > newChildrenList = Lists.newArrayList( newChildren ) ;
    if( 0 >= newChildrenList.size() ) {
      return tree ;
    } else {
      // We treat the first child in a special way because it is used to guess the
      // typeof the array. By not performing the check below, caller would get
      // an obscure NullPointerException when attempting to get the class of the null object.
      final T firstChild = newChildrenList.get( 0 ) ;
      if( null == firstChild ) {
        throw new NullArgumentException( "Null child at index 0" ) ;
      }

      final T[] newArray = ObjectArrays.newArray(
          ( Class< T > ) firstChild.getClass(),
          tree.getChildCount() + newChildrenList.size()
      ) ;

      int i ;
      for( i = 0 ; i < tree.getChildCount() ; i++ ) {
        final T child = ( T ) tree.getChildAt( i ) ;
        if( null == child ) {
          throw new NullArgumentException( "Null child at index " + i ) ;
        }
        newArray[ i ] = child;
      }
      for( int j = 0 ; j < newChildrenList.size() ; j++ ) {
        newArray[ i + j ] = newChildrenList.get( j ) ;
      }

      return ( T ) tree.adopt( newArray );

    }

  }

  /**
   * Returns a copy of this {@code Tree} minus the child of given index.
   * @param tree
   * @param index a value between [0, {@link Tree#getChildCount()}[.
   * @return a non-null object.
   * @throws ArrayIndexOutOfBoundsException
   */
  public static < T extends Tree > T remove( T tree, int index )
      throws ArrayIndexOutOfBoundsException
  {
    if( index < 0 ) {
      throw new ArrayIndexOutOfBoundsException( "Negative index: " + index ) ;
    }
    if( tree.getChildCount() < index ) {
      throw new ArrayIndexOutOfBoundsException(
          "Cannot remove child at index " + index +
          " (child count: " + tree.getChildCount() + ")"
      ) ;
    }
    final T[] newArray = ObjectArrays.newArray(
        ( Class < T > ) tree.getChildAt( 0 ).getClass(),
        tree.getChildCount() - 1
    ) ;

    int keep = 0 ;
    for( int i = 0 ; i < tree.getChildCount() ; i++ ) {
      if( i != index ) {
        newArray[ keep ++ ] = ( T ) tree.getChildAt( i );
      }
    }
    return ( T ) tree.adopt( newArray ) ;
  }

  /**
   * Returns a copy of this {@code Tree} minus the child of given index.
   * @param parent non-null object.
   * @param index a value between [0, {@link Tree#getChildCount()}[.
   * @return a non-null object.
   * @throws ArrayIndexOutOfBoundsException
   */
  public static < T extends Tree > T replace( T parent, int index, T newChild )
      throws ArrayIndexOutOfBoundsException
  {
    if( index < 0 ) {
      throw new ArrayIndexOutOfBoundsException( "Negative index: " + index ) ;
    }
    if( parent.getChildCount() < index ) {
      throw new ArrayIndexOutOfBoundsException(
          "Cannot remove child at index " + index +
          " (child count: " + parent.getChildCount() + ")"
      ) ;
    }
    final T[] newArray = ObjectArrays.newArray(
        ( Class < T > ) parent.getChildAt( 0 ).getClass(),
        parent.getChildCount()
    ) ;

    for( int i = 0 ; i < parent.getChildCount() ; i++ ) {
      if( i == index ) {
        newArray[ i ] = newChild ;
      } else {
        newArray[ i ] = ( T ) parent.getChildAt( i );
      }
    }
    return ( T ) parent.adopt( newArray ) ;
  }
}

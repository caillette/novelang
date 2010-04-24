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
package novelang.common.tree;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import org.apache.commons.lang.NullArgumentException;
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
   * @param tree a non-null object that may implement {@link StorageTypeProvider}.
   * @param newChild a non-null object.
   * @return a non-null object.
   */
  public static < T extends Tree< T > > T addFirst( final T tree, final T newChild ) {
    final List< T > newChildList = Lists.newArrayListWithCapacity( tree.getChildCount() + 1 ) ;
    newChildList.add( Preconditions.checkNotNull( newChild ) ) ;
    for( int i = 0 ; i < tree.getChildCount() ; i++ ) {
      newChildList.add( tree.getChildAt( i ) ) ;
    }
    return tree.adopt( newChildList ) ;
  }

  /**
   * Returns a copy of a {@code Tree} with the {@code newChild} added as first child.
   *
   * @param tree a non-null object that may implement {@link StorageTypeProvider}.
   * @param newChild a non-null object.
   * @param position a value between [0, {@link Tree#getChildCount()}[.
   * @return a non-null object.
   */
  public static < T extends Tree< T > > T add( final T tree, final T newChild, final int position ) {
    Preconditions.checkNotNull( newChild ) ;
    if( position < 0 || position > tree.getChildCount()  ) {
      throw new IllegalArgumentException(
          "Invalid position:" + position + " as childcount=" + tree.getChildCount() ) ;
    }
    final List< T > newChildList = Lists.newArrayListWithCapacity( tree.getChildCount() + 1 ) ;
    int oldArrayIndex = 0 ;
    for( int newArrayIndex = 0 ; newArrayIndex <= tree.getChildCount() ; newArrayIndex ++ ) {
      if( position == newArrayIndex  ) {
        newChildList.add( newArrayIndex, newChild ) ;
      } else {
        newChildList.add( newArrayIndex, tree.getChildAt( oldArrayIndex ) ) ;
        oldArrayIndex++ ;
      }
    }
    return tree.adopt( newChildList ) ;
  }

  /**
   * Returns a copy of a {@code Tree} with the {@code newChild} added as last child.
   *
   * @param tree a non-null object that may implement {@link StorageTypeProvider}.
   * @param newChild a non-null object.
   * @return a non-null object.
   */
  public static < T extends Tree< T > > T addLast( final T tree, final T newChild ) {
    final List< T > newChildList = Lists.newArrayListWithCapacity( tree.getChildCount() + 1 ) ;
    for( int i = 0 ; i < tree.getChildCount() ; i++ ) {
      newChildList.add( i, tree.getChildAt( i ) ) ;
    }
    newChildList.add( Preconditions.checkNotNull( newChild ) ) ;
    return tree.adopt( newChildList ) ;
  }

  /**
   * Returns a copy of a {@code Tree} with the {@code newChildren} added as last children.
   *
   * @param tree a non-null object that may implement {@link StorageTypeProvider}.
   * @param newChildren a non-null object iterating over non-null objects.
   * @return non-null object.
   */
  public static < T extends Tree< T > > T addLast( 
      final T tree, 
      final Iterable< ? extends T > newChildren 
  ) {
    final List< ? extends T > newChildrenList = Lists.newArrayList( newChildren ) ;
    if( 0 >= newChildrenList.size() ) {
      return tree ;
    } else {
      // We treat the first child in a special way because it is used to guess the
      // typeof the array. Without the check below, caller would get
      // an obscure NullPointerException when attempting to get the class of the null object.
      final T firstChild = newChildrenList.get( 0 ) ;
      if( null == firstChild ) {
        throw new NullArgumentException( "Null child at index 0" ) ;
      }

      final List< T > newChildList = Lists.newArrayListWithCapacity( tree.getChildCount() + 1 ) ;

      int i ;
      for( i = 0 ; i < tree.getChildCount() ; i++ ) {
        final T child = tree.getChildAt( i ) ;
        if( null == child ) {
          throw new NullArgumentException( "Null child at index " + i ) ;
        }
        newChildList.add( i, child ) ;
      }
      for( int j = 0 ; j < newChildrenList.size() ; j++ ) {
        newChildList.add( i + j, newChildrenList.get( j ) ) ;
      }

      return tree.adopt( newChildList ) ;
    }

  }

  /**
   * Returns a copy of this {@code Tree} minus the child of given index.
   * @param tree a non-null object that may implement {@link StorageTypeProvider}.
   * @param index a value between [0, {@link Tree#getChildCount()}[.
   * @return a non-null object.
   * @throws ArrayIndexOutOfBoundsException
   */
  public static < T extends Tree< T > > T remove( final T tree, final int index )
      throws ArrayIndexOutOfBoundsException
  {
    Preconditions.checkArgument( index >= 0 ) ;

    if( tree.getChildCount() < index ) {
      throw new ArrayIndexOutOfBoundsException(
          "Cannot remove child at index " + index +
          " (child count: " + tree.getChildCount() + ")"
      ) ;
    }

    final List< T > newChildList = Lists.newArrayListWithCapacity( tree.getChildCount() - 1 ) ;

    int keep = 0 ;
    for( int i = 0 ; i < tree.getChildCount() ; i++ ) {
      if( i != index ) {
        newChildList.add( keep ++, tree.getChildAt( i ) ) ;
      }
    }
    return tree.adopt( newChildList ) ;
  }

  /**
   * Returns a copy of this {@code Tree} minus the child of given index.
   * @param tree a non-null object that may implement {@link StorageTypeProvider}.
   * @param predicate a {@code Predicate} returning {@code true} for children to keep.
   * @return a non-null object.
   * @throws ArrayIndexOutOfBoundsException
   */
  public static < T extends Tree< T > > T remove(
      final T tree, 
      final Predicate< ? super T > predicate
  )
      throws ArrayIndexOutOfBoundsException
  {
    final List< T > newChildList = Lists.newArrayListWithCapacity( tree.getChildCount() ) ;

    for( int i = 0 ; i < tree.getChildCount() ; i++ ) {
      final T child = tree.getChildAt( i );
      if( ! predicate.apply( child ) ) {
        newChildList.add( child ) ;
      }
    }
    return tree.adopt( newChildList ) ;
  }

  /**
   * Returns a copy of this {@code Tree} minus the child of given index.
   * @param parent non-null object that may implement {@link StorageTypeProvider}.
   * @param index a value between [0, {@link Tree#getChildCount()}[.
   * @return a non-null object.
   * @throws ArrayIndexOutOfBoundsException
   */
  public static < T extends Tree< T > > T replace( final T parent, final int index, final T newChild )
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

    final List< T > newChildList = Lists.newArrayListWithCapacity( parent.getChildCount() ) ;

    for( int i = 0 ; i < parent.getChildCount() ; i++ ) {
      if( i == index ) {
        newChildList.add( i, newChild ) ;
      } else {
        newChildList.add( i, parent.getChildAt( i ) ) ;
      }
    }

    return parent.adopt( newChildList ) ;

  }


}

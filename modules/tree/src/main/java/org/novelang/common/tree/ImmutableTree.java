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
package org.novelang.common.tree;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang.NullArgumentException;
import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;

/**
 * Immutable base class for homogeneous n-ary trees.
 * <p>
 * This class is generic for strong-typing the {@link Tree#adopt(Iterable)}} method.
 * 
 * @author Laurent Caillette
 */
public abstract class ImmutableTree< T extends Tree< T > > implements Tree< T > {

  /**
   * Don't be stupid using reflection to make this mutable!
   */
  private final T[] children ;

  /**
   * Constructor.
   * @param children may be null but may not contain nulls.
   * @throws NullArgumentException
   */
  protected ImmutableTree( final T... children ) throws NullArgumentException {
    this( Lists.newArrayList( children ) ) ;
  }

  /**
   * Constructor.
   * @param children a non-null iterable returning non-null iterators iterating over
   *     non-null objects.
   * @throws NullArgumentException
   */
  protected ImmutableTree( final Iterable< ? extends T > children ) throws NullArgumentException {
    final List< T > childList = Lists.newArrayList( children ) ;
    if( childList.isEmpty() ) {
      this.children = null ;
    } else {
      this.children = ( T[] ) createArray( this, childList.get( 0 ), childList.size() ) ;
      for( int i = 0 ; i < childList.size() ; i++ ) {
        final T child = childList.get( i ) ;
        if( null == child ) {
          throw new NullArgumentException( "Null child at index " + i ) ;
        }
        this.children[ i ] = childList.get( i ) ;
      }
    }
  }

  public final int getChildCount() {
    return null == children ? 0 : children.length ;
  }

  public final T getChildAt( final int index ) {
    if( index >= getChildCount() ) {
      throw new ArrayIndexOutOfBoundsException(
          "Unsupported index: " + index + " (child count: " + getChildCount() + ")" ) ;
    }
    return children[ index ] ;
  }

  /**
   * Convenience method (not a part of {@link Tree} contract).
   *
   * @return a non-null iterable returning non-null iterators, which iterate on non-null objects.
   */
  public Iterable< ? extends T > getChildren() {
    return new Iterable() {
      public Iterator< T > iterator() {
        return new ChildrenIterator() ;
      }
    } ;
  }

  private class ChildrenIterator implements Iterator< T > {

    private int current = 0 ;

    public boolean hasNext() {
      return current < getChildCount() ;
    }

    public T next() throws NoSuchElementException {
      if( hasNext() ) {
        return ImmutableTree.this.getChildAt( current++ ) ;
      } else {
        throw new NoSuchElementException(
            "No more children (child count: " + getChildCount() + ")" ) ;
      }
    }

    public void remove() {
      throw new UnsupportedOperationException( "remove" ) ;
    }
  }

  /**
   * Creates an array for storing children.
   * If the concrete instance ({@code this}) is an instance of 
   * {@link org.novelang.common.tree.StorageTypeProvider}, then returned array is of type
   * returned by {@link StorageTypeProvider#getStorageType()}.
   * Otherwise, it is of the type of the {@code fallback} parameter.
   * <p>
   * This method is used internally.
   * It is made a member of {@code ImmutableTree} in order to avoid a cyclic depedency
   * with {@code TreeTools} (which would be the logical place).
   *
   * @param fallback a non-null object.
   * @param arraySize a non-negative number.
   * @return a non-null array of the given size.
   *
   * @see org.novelang.common.tree.StorageTypeProvider
   */
  protected static< T extends Tree > T[] createArray( 
      final T tree, 
      final T fallback, 
      final int arraySize 
  ) {
    final Class< T > concreteClass ;
    if( tree instanceof StorageTypeProvider ) {
      concreteClass = ( ( StorageTypeProvider ) tree ).getStorageType() ;
    } else {
      concreteClass = ( Class< T > ) fallback.getClass() ;
    }
    return ObjectArrays.newArray( concreteClass, arraySize ) ;
  }

}

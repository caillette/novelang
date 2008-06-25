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

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang.NullArgumentException;
import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;

/**
 * Immutable base class for homogeneous n-ary trees.
 * <p>
 * This class is generic for strong-typing the {@link Tree#adopt(Tree[]) adopt} method.
 * Subclass {@code Tree} like this:
 * <pre>
 public class MyTree extends ImmutableTree< MyTree > {

   private final String payload ; // New stuff!

   public MyTree( String payload, MyTree... children ) {
     super( children ) ;
     this.payload = payload ;
   }

   protected MyTree adopt( MyTree[] newChildren ) {
     return new MyTree( payload, newChildren ) ;
   }

   // ... Additional behaviors.
}
 * </pre>
 * 
 * @author Laurent Caillette
 */
public abstract class ImmutableTree< T extends Tree > implements Tree< T > {

  /**
   * Don't be stupid using reflection to make this mutable!
   */
  private final T[] children ;

  /**
   * Constructor.
   * @param children may be null but may not contain nulls.
   * @throws NullArgumentException
   */
  public ImmutableTree( T... children ) throws NullArgumentException {
    this( Lists.newArrayList( children ) ) ;
  }

  /**
   * Constructor.
   * @param children a non-null iterable returning non-null iterators iterating over
   *     non-null objects.
   * @throws NullArgumentException
   */
  public ImmutableTree( final Iterable< ? extends T > children ) throws NullArgumentException {
    final List< T > childList = Lists.newArrayList( children ) ;
    if( 0 == childList.size() ) {
      this.children = null ;
    } else {
      this.children = ( T[] ) ObjectArrays.newArray(
          childList.get( 0 ).getClass(),
          childList.size()
      ) ;
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

  public final T getChildAt( int index ) {
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
}

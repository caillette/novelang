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
 * This class is generic for strong-typing the {@link Tree#adopt(Tree[])} adopt} method.
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
//    if( null == children ) {
//      this.children = null ;
//    } else {
//      for( int i = 0 ; i < children.length ; i++ ) {
//        final Tree child = children[ i ] ;
//        if( null == child ) {
//          throw new NullArgumentException( "Null child at index " + i ) ;
//        }
//      }
//      this.children = children.clone() ;
//    }
  }

  public ImmutableTree( final Iterable< ? extends T > children ) {
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

  /**
   * Returns the the child of given index.
   * @param index a value between [0, {@link #getChildCount()}[.
   * @return a non-null object.
   * @throws ArrayIndexOutOfBoundsException
   */
  public final T getChildAt( int index ) {
    if( index >= getChildCount() ) {
      throw new ArrayIndexOutOfBoundsException(
          "Unsupported index: " + index + " (child count: " + getChildCount() + ")" ) ;
    }
    return children[ index ] ;
  }

  public Iterable< T > getChildren() {
    return new Iterable() {
      public Iterator< T > iterator() {
        return new ChildrenIterator() ;
      }
    } ;
  }


// ========
// Mutators
// ========

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
   * @throws NullArgumentException if at least one of {@code newChildren} is null.
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
   * @param index a value between [0, {@link #getChildCount()}[.
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

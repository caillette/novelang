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
import java.util.Arrays;

import com.google.common.base.Objects;
import com.google.common.collect.PrimitiveArrays;

/**
 * Represents where a {@link Tree} lies inside a bigger owning {@link Tree}.
 * <p>
 * The root is called the "top" and the n<sup>th</sup> child is called "bottom".
 *
 * @author Laurent Caillette
 */
public final class Treepath< T extends Tree > {

  private final Treepath< T > parent ;
  private final int indexInParent ;
  private final T bottom ;

  @Deprecated
  private Treepath( Treepath parent, T bottom ) {
    this.parent = parent ;
    this.bottom = Objects.nonNull( bottom ) ;
    this.indexInParent = -1 ;
  }

  private Treepath( Treepath< T > parent, int indexInParent ) {
    this.parent = parent ;
    this.bottom = ( T ) parent.getBottom().getChildAt( indexInParent ) ;
    this.indexInParent = indexInParent ;
  }

  private Treepath( T tree ) {
    parent = null ;
    indexInParent = -1 ;
    bottom = tree ;
  }

  public T getTop() {
    if( null == parent ) {
      return bottom;
    } else {
      return parent.getTop() ;
    }
  }

  public T getBottom() {
    return bottom;
  }

  public int getHeight() {
    if( null == parent ) {
      return 1 ;
    } else {
      return parent.getHeight() + 1 ;
    }
  }

  public Treepath< T > getParent() {
    return parent ;
  }

  public int getIndexInParent() {
    return indexInParent ;
  }

  /**
   * Returns the {@code Tree} at a given height, which is the n<sup>th</sup>
   * from the end (starting at 0).
   * Invariant: {@code getTreeAtHeight( 0 ) == getEnd()}.
   *
   * @param height 0 or more.
   * @return a non-null object.
   * @throws IllegalArgumentException if negative height or heigt greater than or
   *     or equal to {@link #getHeight()}.
   */
  public T getTreeAtHeight( int height ) throws IllegalPathHeightException {
    if( -1 == height ) {
      throw new IllegalPathHeightException( height ) ;
    }
    if( 0 == height ) {
      return bottom;
    } else {
      if( null == parent ) {
        throw new IllegalPathHeightException( height ) ;
      } else {
        try {
          return parent.getTreeAtHeight( height - 1 ) ;
        } catch( IllegalPathHeightException e ) {
          throw new IllegalPathHeightException( height + 1 ) ;
        }
      }
    }
  }

  /**
   * Finds a {@code Tree} inside another one and returns the inverted {@code Treepath}.
   * This inversion occurs because only the function call at the top of call stack
   * "knows" it has found the {@code Tree}. It creates some immutable object with a
   * list-like structure which already exists with the {@code Treepath} except
   * that semantics are different. This should not bother anybody since this method
   * is for internal use only.
   *
   * @param inside a non-null object.
   * @param target a non-null object.
   * @return an inverted {@code Treepath} or null if {@code target} was not found.
   */
  protected static< T extends Tree > Treepath< T > find( T inside, T target ) {
    if( target == inside ) {
      return create( inside ) ;
    }
    for( int i = 0 ; i < inside.getChildCount() ; i++ ) {
      final Tree child = inside.getChildAt( i ); ;
      final Treepath found = find( child, target ) ;
      if( null != found ) {
        return create( found, inside ) ;
      }
    } ;
    return null ;
  }

  protected static< T extends Tree > Treepath invert( Treepath< T > treepath ) {
    Treepath result = create( treepath.getBottom() ) ;
    for( int height = 1 ; height < treepath.getHeight() ; height++ ) {
      result = create( result, treepath.getTreeAtHeight( height ) ) ;
    }
    return result ;
  }

// ===============
// Factory methods
// ===============

  /**
   * Creates a {@code Treepath} out from a start and an End.
   * <p>
   * Checks that given {@code start} is (possibly indirect) parent of {@code end}
   * by performing a full tree traversal.
   * <p>
   * <em>
   * This method assumes all {@code Tree}s are unique under {@code start}
   * (there is no shared {@code Tree}) object). Expect unwanted results otherwise.
   * </em>
   *
   * @param top a non-null object.
   * @param bottom a non-null object, must be {@code start} or one of its (possibly indirect)
   *     children.
   * @return a non-null object.
   *
   */
  @Deprecated
  public static< T extends Tree > Treepath< T > create( T top, T bottom )
      throws IllegalArgumentException
  {
    final Treepath< T > inverted = find( top, bottom ) ;
    if( null == inverted ) {
      throw new IllegalArgumentException(
          "Could not locate tree: " + top + " doesn't contain " + bottom ) ;
    }
    return invert( inverted ) ;
  }

  @Deprecated
  public static< T extends Tree > Treepath< T > create(
      Treepath< T > treepath,
      T newBottom
  ) {
    return new Treepath< T >( treepath, newBottom ) ;
  }

  public static< T extends Tree > Treepath< T > create(
      Treepath< T > treepath,
      int indexInParent
  ) {
    return new Treepath< T >( treepath, indexInParent ) ;
  }

  public static< T extends Tree > Treepath< T > create( T root, int... indexes ) {
    return create( create( root ), indexes ) ;
  }

  public static< T extends Tree > Treepath< T > create(
      Treepath< T > parent,
      int... indexes
  ) {
    if( null == indexes || 0 == indexes.length ) {
      return parent ;
    } else {
      final int newLength = indexes.length - 1 ;
      final int[] newIndexes = new int[ newLength ] ;
      System.arraycopy( indexes, 1, newIndexes, 0, newLength ) ;
      return create( create( parent, indexes[ 0 ] ), newIndexes ) ;
    }
  }

  /**
   * Creates a {@code Treepath} out from a single {@code Tree}.
   * @param tree a non-null object.
   * @return a non-null object.
   */
  public static< T extends Tree > Treepath create( T tree ) {
    return new Treepath( tree ) ;
  }

  private class IllegalPathHeightException extends IllegalArgumentException {
    public IllegalPathHeightException( int height ) {
      super( "height=" + height ) ;
    }
  }
}
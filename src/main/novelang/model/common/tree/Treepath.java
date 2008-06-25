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

/**
 * An immutable structure representing where a {@link Tree} lies inside a bigger owning
 * {@link Tree}.
 * <p>
 * A {@code Treepath} object is made of:
 * <ul>
 *   <li>A reference to the {@code Tree} itself.
 *   <li>a reference to another {@code Treepath} which references the parent of our
 *     {@code Tree} and so on.
 * </ul>
 * <p>
 * Each reference to a parent tree is like a previous step on the path from the top to the bottom.
 * The top tree is the <strong>start</strong> of the path and the lowest-level tree
 * is the <strong>end</strong> (the terms "start" and "end" are parts of the API).
 * <p>
 * Each previous step includes the reference to the owning tree and the <strong>index</strong>
 * of the child inside its parent. This is required in case one {@code Tree} having
 * multiple references on the same child object.
 * <p>
 * The backward chaining makes possible to have immutable {@code Treepath} objects.
 * <p>
 * Given this tree below:
 * <pre>
 *     t0
 *    /  \
 *  t1   t2
 *      /  \
 *     t3   t7
 *   / | \
 * t4 t5  t6
 * </pre>
 * <p>
 * The {@code Treepath} starting on {@code t0} and ending on {@code t6} can be represented
 * like this:
 * <pre>
 * t0 &lt;-[1]- t2 &lt;-[0]- t3 &lt;-[2]- t6
 * ^ start                       ^ end
 * </pre>
 * <p>
 * While the backward chaining may seem confusing, most of {@code Treepath}-related methods
 * allow to thing "from start to end".
 *
 * @see javax.swing.tree.TreePath from which this class was inspired from. 
 *
 * @author Laurent Caillette
 */
public final class Treepath< T extends Tree > {

  private final Treepath< T > previous;
  private final int indexInPrevious;
  private final T treeAtEnd;

  private Treepath( Treepath< T > previous, int indexInPrevious ) {
    if( null == previous ) {
      throw new NullArgumentException( "Cannot define null previous path with this constructor" ) ;
    }
    this.previous = previous;
    this.treeAtEnd = ( T ) previous.getTreeAtEnd().getChildAt( indexInPrevious ) ;
    this.indexInPrevious = indexInPrevious;
  }

  private Treepath( T tree ) {
    previous = null ;
    indexInPrevious = -1 ;
    treeAtEnd = tree ;
  }

  /**
   * Returns the reference to the start of the path, corresponding to the root tree.
   * @return a non-null object.
   */
  public T getStart() {
    if( null == previous ) {
      return treeAtEnd;
    } else {
      return previous.getStart() ;
    }
  }

  /**
   * Returns the {@code Tree} at the end of this path.
   * @return a non-null object.
   */
  public T getTreeAtEnd() {
    return treeAtEnd;
  }

  /**
   * Returns the length of the path, corresponding to the number of chained parent {@code Treepath}
   * instances plus one.
   *
   * @return an integer equal to or greater than 1.
   */
  public int getLength() {
    if( null == previous ) {
      return 1 ;
    } else {
      return previous.getLength() + 1 ;
    }
  }

  /**
   * Returns a reference to the {@code Treepath} object representing the whole path to the
   * previous step.
   * @return a possibly null object when this {@code Treepath} represents the start of the path.
   */
  public Treepath< T > getPrevious() {
    return previous;
  }

  /**
   * Returns the index inside the parent tree which corresponds to the previous step of the path.
   *
   * @return -1 if this {@code Treepath} represents the start of the path, the index of the tree
   *     in its parent otherwise.
   */
  public int getIndexInPrevious() {
    return indexInPrevious;
  }

  /**
   * Returns the {@code Tree} at a given distance, which is the n<sup>th</sup>
   * from the end.
   * Invariant: {@code getTreeAtHeight( 0 ) == getEnd()}.
   *
   * @param distance 0 or more.
   * @return a non-null object.
   * @throws IllegalArgumentException if negative distance or distance greater than or
   *     or equal to {@link #getLength()}.
   */
  public T getTreeAtDistance( int distance ) throws IllegalDistanceException {
    if( -1 == distance ) {
      throw new IllegalDistanceException( distance ) ;
    }
    if( 0 == distance ) {
      return treeAtEnd;
    } else {
      if( null == previous ) {
        throw new IllegalDistanceException( distance ) ;
      } else {
        try {
          return previous.getTreeAtDistance( distance - 1 ) ;
        } catch( IllegalDistanceException e ) {
          throw new IllegalDistanceException( distance + 1 ) ;
        }
      }
    }
  }


// ===============
// Factory methods
// ===============

  /**
   * Creates a {@code Treepath} from a parent {@code Treepath},
   * adding the {@code indexInParent}<sup>th</sup> child from parent's end.
   *
   * @param root non-null object.
   * @param indexInParent positive integer, must be a valid index.
   * @return a non-null object.
   */
  public static< T extends Tree > Treepath< T > create(
      Treepath< T > root,
      int indexInParent
  ) {
    return new Treepath< T >( root, indexInParent ) ;
  }

  /**
   * Creates a {@code Treepath} from a root {@code Tree}, adding a child, a
   * grandchild and so on in order, given their respective position.
   *
   * @param root non-null object
   * @param indexes must be a valid index in each of their respective tree.
   * @return a non-null object.
   * @see #create(Treepath, int...)
   */
  public static< T extends Tree > Treepath< T > create( T root, int... indexes ) {
    return create( create( root ), indexes ) ;
  }

  /**
   * Creates a {@code Treepath} extending another {@code Treepath}, adding a child, a
   * grandchild and so on in order, given their respective position.
   * <p>
   * Given this tree below:
   * <pre>
   *     *t0
   *    /  \
   *  t1  *t2
   *      /  \
   *    *t3   t7
   *   / | \
   * t4 t5 *t6
   * </pre>
   * The {@code Treepath} indicated with asterisks ({@code t0<-t2<-t3<-t6}) is formed
   * by calling:
   * <pre>
   * Treepath< T >.create( t0, 1, 0, 2) ;
   * </pre>
   *
   * @param parent non-null object
   * @param indexes must be a valid index in each of their respective tree.
   * @return a non-null object.
   * @see #create(Treepath, int...)
   */
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

  private class IllegalDistanceException extends IllegalArgumentException {
    public IllegalDistanceException( int distance ) {
      super( "distance=" + distance ) ;
    }
  }
}
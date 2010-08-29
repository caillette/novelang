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

import com.google.common.base.Preconditions;
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
 * @see javax.swing.tree.TreePath Swing's {@code TreePath} from which this class was inspired from. 
 *
 * @author Laurent Caillette
 */
public final class Treepath< T extends Tree< T > > implements Comparable< Treepath< T > > {

  private final Treepath< T > previous;
  private final int indexInPrevious;
  private final T treeAtEnd;

  private Treepath( final Treepath< T > previous, final int indexInPrevious ) {
    if( null == previous ) {
      throw new NullArgumentException( "Cannot define null previous path with this constructor" ) ;
    }
    this.previous = previous;
    this.treeAtEnd = previous.getTreeAtEnd().getChildAt( indexInPrevious );
    this.indexInPrevious = indexInPrevious;
  }

  private Treepath( final T tree ) {
    previous = null ;
    indexInPrevious = -1 ;
    treeAtEnd = tree ;
  }

  /**
   * Returns the reference to the start of the path, corresponding to the root tree.
   * @return a non-null object.
   */
  public T getTreeAtStart() {
    if( null == previous ) {
      return treeAtEnd;
    } else {
      return previous.getTreeAtStart() ;
    }
  }

  /**
   * Returns the reference to the start of the path, corresponding to the root tree.
   * @return a non-null object.
   */
  public Treepath< T > getStart() {
    if( null == previous ) {
      return this ;
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
   * Returns the indices in parent tree, from the second treepath to the end.
   *
   * @return null if this {@code Treepath} has a {@link #getLength() length} of 1, or an array
   *     of {@code int}s of {@link #getLength() length - 1} elements, corresponding to the
   *     index in parent tree of each referenced tree.
   *
   * @see Treepath#getIndexInPrevious()
   * @see Treepath#create(Tree, int...)
   * @see Treepath#create(Treepath, int...)
   */
  public int[] getIndicesInParent() {
    if( getLength() == 1 ) {
      return null ;
    }
    final int[] indices = new int[ getLength() - 1 ] ;
    for( int distance = indices.length - 1 ; distance >= 0 ; distance -- ) {
      indices[ indices.length - distance - 1 ] = 
          getTreepathAtDistance( distance ).getIndexInPrevious() ; 
    }
    return indices ;
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
  public T getTreeAtDistance( final int distance ) throws IllegalDistanceException {
    return getTreepathAtDistance( distance ).getTreeAtEnd() ;
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
  public Treepath< T > getTreepathAtDistance( final int distance ) throws IllegalDistanceException {
    if( 0 > distance ) {
      throw new IllegalDistanceException( distance ) ;
    }
    if( 0 == distance ) {
      return this ;
    } else {
      if( null == previous ) {
        throw new IllegalDistanceException( distance ) ;
      } else {
        try {
          return getPrevious().getTreepathAtDistance( distance - 1 ) ;
        } catch( IllegalDistanceException e ) {
          throw new IllegalDistanceException( distance + 1 ) ;
        }
      }
    }
  }
  
  /**
   * Returns the {@code Tree} at a given distance, which is the n<sup>th</sup>
   * from the start.
   * Invariant: {@code getTreeAtHeight( 0 ) == getStart()}.
   *
   * @param distance 0 or more.
   * @return a non-null object.
   * @throws IllegalArgumentException if negative distance or distance greater than or
   *     or equal to {@link #getLength()}.
   */
  public Treepath< T > getTreepathAtDistanceFromStart( final int distance ) 
      throws IllegalDistanceException 
  {
    return getTreepathAtDistance( getLength() - distance - 1 ) ; 
  }
  

  @Override
  public String toString() {
    boolean first = true ;
    final StringBuilder buffer = new StringBuilder();
    for( int i = 0 ; i < this.getLength() ; i++ ) {
      if( first ) {
        first = false ;
      } else {
        buffer.append( " -> " ) ;
      }
      buffer.append( "{" ).append( this.getTreeAtDistance( i ) ).append( "}" ) ;
    }
    return buffer.toString() ;
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
  public static< T extends Tree< T > > Treepath< T > create(
      final Treepath< T > root,
      final int indexInParent
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
  public static< T extends Tree< T > > Treepath< T > create( final T root, final int... indexes ) {
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
  public static< T extends Tree< T > > Treepath< T > create(
      final Treepath< T > parent,
      final int... indexes
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
  public static< T extends Tree< T > > Treepath< T > create( final T tree ) {
    return new Treepath< T >( tree ) ;
  }


  private static class IllegalDistanceException extends IllegalArgumentException {
    public IllegalDistanceException( final int distance ) {
      super( "distance=" + distance ) ;
    }
  }
  
// ==========  
// Comparable  
// ==========


  /**
   * Compares with another {@code Treepath}.
   * For a whole tree, sorting the {@code Treepath} objects with one for each node gives the
   * same node order as for pre-order traversal.
   * <p>
   * Implementation note: comparison occurs on indexes; node equality is not good because
   * a tree may reference the same child object more than once.
   * 
   * @param other a non-null object with the same {@link #getTreeAtStart()} reference.
   * @return 0 if both {@code Treepath} objects have the same length and same indices in 
   *     parent {@code Treepath}s;  
   *     &lt;1 if this  {@code Treepath} is "on the left" of the other; 
   *     >1 if this  {@code Treepath} is "on the right" of the other.
   * 
   * @throws NullPointerException if {@code other} is null. 
   * @throws IllegalArgumentException if {@code other} doesn't refer to the same 
   *     {@link #getTreeAtStart() start tree} . 
   */
  public int compareTo( final Treepath< T > other ) {
    Preconditions.checkNotNull( other ) ;
    Preconditions.checkArgument( other.getTreeAtStart() == this.getTreeAtStart() ) ;
    final int shortestLength = Math.min( this.getLength(), other.getLength() ) ;
    for( int distance = 1 ; distance < shortestLength ; distance ++ ) {
      final Treepath< T > thisIntermediateTreepath = 
          this.getTreepathAtDistanceFromStart( distance ) ;
      final Treepath< T > otherIntermediateTreepath = 
          other.getTreepathAtDistanceFromStart( distance ) ;
      final int localDifference = 
          thisIntermediateTreepath.getIndexInPrevious() -
          otherIntermediateTreepath.getIndexInPrevious() ; 
      if( localDifference != 0 ) {
        return localDifference ;
      }
    }
    if( this.getLength() == other.getLength() ) {
      // Same length at this point with every indexes equal means we're on the same node.
      if( this.getTreeAtEnd() != other.getTreeAtEnd() ) {
        throw new Error( "Implementation problem!" ) ;
      }
      return 0 ;
    } else {
      return this.getLength() - other.getLength() ;      
    }
  }
  
}
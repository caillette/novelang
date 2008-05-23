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
package novelang.model.common;

import com.google.common.base.Objects;

/**
 * Represents where a {@link Tree} lies inside a bigger owning {@link Tree}.
 * <p>
 * The root is called the "start" and the n<sup>th</sup> child is called "end".
 *
 * @author Laurent Caillette
 */
public final class Treepath {

  private final Treepath parent ;
  private final Tree end ;

  protected Treepath( Treepath parent, Tree end ) {
    this.parent = parent ;
    this.end = Objects.nonNull( end ) ;
  }

  public Tree getStart() {
    if( null == parent ) {
      return end ;
    } else {
      return parent.getStart() ;
    }
  }

  public Tree getEnd() {
    return end ;
  }

  public int getHeight() {
    if( null == parent ) {
      return 1 ;
    } else {
      return parent.getHeight() + 1 ;
    }
  }

  public Treepath getParent() {
    return parent ;
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
  public Tree getTreeAtHeight( int height ) throws IllegalPathHeightException {
    if( -1 == height ) {
      throw new IllegalPathHeightException( height ) ;
    }
    if( 0 == height ) {
      return end ;
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
  protected static Treepath find( Tree inside, Tree target ) {
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

  protected static Treepath invert( Treepath treepath ) {
    Treepath result = create( treepath.getEnd() ) ;
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
   * @param start a non-null object.
   * @param end a non-null object, must be {@code start} or one of its (possibly indirect)
   *     children.
   * @return a non-null object.
   */
  public static Treepath create( Tree start, Tree end ) throws IllegalArgumentException {
    final Treepath inverted = find( start, end ) ;
    if( null == inverted ) {
      throw new IllegalArgumentException(
          "Could not locate tree: " + start + " doesn't contain " + end ) ;
    }
    return invert( inverted ) ;
  }

  public static Treepath create( Treepath treepath, Tree newEnd ) {
    return new Treepath( treepath, newEnd ) ;
  }

  /**
   * Creates a {@code Treepath} out from a single {@code Tree}.
   * @param tree a non-null object.
   * @return a non-null object.
   */
  public static Treepath create( Tree tree ) {
    return new Treepath( null, tree ) ;
  }

  private class IllegalPathHeightException extends IllegalArgumentException {
    public IllegalPathHeightException( int height ) {
      super( "height=" + height ) ;
    }
  }
}

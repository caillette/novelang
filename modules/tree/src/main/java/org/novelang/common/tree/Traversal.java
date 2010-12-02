/*
 * Copyright (C) 2010 Laurent Caillette
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.novelang.common.tree;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * {@link Treepath}-based tree traversal functions.
 *
 * @author Laurent Caillette
 */
public abstract class Traversal< T extends Tree< T > > {

  protected final Predicate< T > treeFilter ;

  private Traversal( final Predicate< T > treeFilter ) {
    this.treeFilter = treeFilter ;
  }

  /**
   * Returns the {@link Treepath} to the next {@link Tree} object for this traversal
   * algorithm, or null if there is none.
   *
   * @param treepath a non-null object.
   * @return a possibly null object.
   */
  public abstract Treepath< T > next( final Treepath< T > treepath ) ;


  /**
   * Returns the first eleemnt for this traversal, or null if there is none.
   * @param treepath a non-null object.
   * @return a possibly null object.
   */
  public abstract Treepath< T > first( final Treepath< T > treepath ) ;


// =================
// MirroredPostorder
// =================

  /**
   * Function object capturing a tree filter.
   */
  public static final class MirroredPostorder< T extends Tree< T > > extends Traversal< T > {

    private MirroredPostorder( final Predicate< T > treeFilter ) {
      super( treeFilter ) ;
    }

    private MirroredPostorder() {
      this( Predicates.< T >alwaysTrue() ) ;
    }

    /**
     * Factory method for type inference.
     */
    public static< T extends Tree< T > > MirroredPostorder< T > create() {
      return new MirroredPostorder< T >() ;
    }

    /**
     * Factory method for type inference.
     */
    public static< T extends Tree< T > > MirroredPostorder< T > create(
        final Predicate< T > treeFilter
    ) {
      return new MirroredPostorder< T >( treeFilter ) ;
    }

    /**
     * Returns a {@code Treepath} corresponding to the last tree in a
     * {@link #next(org.novelang.common.tree.Treepath) postorder traversal}
     * in the {@link Treepath#getTreeAtEnd()} end} tree.
     *
     * @see #next (Treepath)
     */
    @Override
    public Treepath< T > first( final Treepath< T > treepath ) {
      Treepath< T > result = treepath ;
      while( true ) {
        final T tree = result.getTreeAtEnd() ;
        final int childCount = tree.getChildCount() ;
        if( childCount > 0 && treeFilter.apply( tree ) ) {
          result = Treepath.create( result, childCount - 1 ) ;
        } else {
          return result ;
        }
      }
    }

    /**
     * Returns a {@code Treepath} object corresponding to the previous tree in a
     * <a href="http://en.wikipedia.org/wiki/Tree_traversal">postorder</a> traversal.
     * <pre>
     *  *t0            *t0            *t0            *t0
     *   |      next    |      next    |     next     |      next
     *  *t1     -->    *t1     -->    *t1     -->     t1     -->    null
     *  /  \           /  \           /  \           /  \
     * t2  *t3       *t2   t3        t2   t3       t2    t3
     * </pre>
     *
     * This is a valuable traversal algorithm that preserves indexes of unmodified treepaths.
     *
     * @param treepath a non-null object.
     * @return the treepath to the next tree, or null.
     */
    @Override
    public Treepath< T > next( final Treepath< T > treepath ) {
      if( treepath.getLength() > 1 ) {
        if( TreepathTools.hasPreviousSibling( treepath ) ) {
          final Treepath< T > previousSibling = TreepathTools.getPreviousSibling( treepath ) ;
          if( previousSibling.getTreeAtEnd().getChildCount() == 0 ) {
            return previousSibling ;
          } else {
            return first(previousSibling) ;
          }
        } else {
          return treepath.getPrevious() ;
        }
      } else {
        return null ;
      }

    }

  }





// ========
// Preorder
// ========

  /**
   * Function object capturing a tree filter.
   */
  public static final class Preorder< T extends Tree< T > > extends Traversal< T > {

    private Preorder( final Predicate< T > treeFilter ) {
      super( treeFilter ) ;
    }

    private Preorder() {
      this( Predicates.< T >alwaysTrue() ) ;
    }

    /**
     * Factory method for type inference.
     */
    public static< T extends Tree< T > > Preorder< T > create() {
      return new Preorder< T >() ;
    }


    @Override
    public Treepath< T > first( final Treepath< T > treepath ) {
      return treepath.getStart() ;
    }

    /**
     * Returns a {@code Treepath} object to the next tree in a
     * <a href="http://en.wikipedia.org/wiki/Tree_traversal">preorder</a> traversal.
     * <pre>
     *  *t0            *t0            *t0            *t0
     *   |      next    |      next    |     next     |      next
     *   t1     -->    *t1     -->    *t1     -->    *t1     -->    null
     *  /  \           /  \           /  \           /  \
     * t2   t3        t2   t3       *t2   t3       t2   *t3
     * </pre>
     *
     * @param treepath a non-null object.
     * @return the treepath to the next tree, or null.
     */

    @Override
    public Treepath< T > next( final Treepath< T > treepath ) {
      final T tree = treepath.getTreeAtEnd();
      if( tree.getChildCount() > 0 ) {
        return Treepath.create( treepath, 0 ) ;
      }
      return nextUp( treepath ) ;
    }


    private static < T extends Tree< T > > Treepath< T > upNext( final Treepath< T > treepath ) {
      Treepath< T > previousTreepath = treepath.getPrevious() ;
      while( previousTreepath != null && previousTreepath.getPrevious() != null ) {
        if( TreepathTools.hasNextSibling( previousTreepath ) ) {
          return TreepathTools.getNextSibling( previousTreepath ) ;
        } else {
          previousTreepath = previousTreepath.getPrevious() ;
        }
      }
      return null ;
    }

    /**
     * Navigates towards the next sibling or the next sibling of a parent tree.
     * @param treepath a non-null object.
     * @return the next tree, or null if there is no other tree to navigate to.
     *
     */
    public static < T extends Tree< T > > Treepath< T > nextUp( final Treepath< T > treepath ) {
      if( TreepathTools.hasNextSibling( treepath ) ) {
        return TreepathTools.getNextSibling( treepath ) ;
      } else {
        return upNext( treepath ) ;
      }
    }

  }
}

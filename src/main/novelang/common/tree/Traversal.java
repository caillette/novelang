/*
 * Copyright (C) 2009 Laurent Caillette
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
package novelang.common.tree;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * Traversal functions.
 *
 * @author Laurent Caillette
 */
public class Traversal {

  private Traversal() { }


  /**
   * Function object capturing a tree filter.
   */
  public static final class MirroredPostorder< T extends Tree > {

    private final Predicate< T > treeFilter ;

    private MirroredPostorder( final Predicate< T > treeFilter ) {
      this.treeFilter = treeFilter ;
    }

    private MirroredPostorder() {
      this( Predicates.< T >alwaysTrue() ) ;
    }

    /**
     * Factory method for type inference.
     */
    public static< T extends Tree > MirroredPostorder< T > create() {
      return new MirroredPostorder< T >() ;
    }

    /**
     * Factory method for type inference.
     */
    public static< T extends Tree > MirroredPostorder< T > create(
        final Predicate< T > treeFilter
    ) {
      return new MirroredPostorder< T >( treeFilter ) ;
    }

    /**
     * Returns a {@code Treepath} corresponding to the last tree in a
     * {@link #getNext(novelang.common.tree.Treepath) postorder traversal}
     * in the {@link novelang.common.tree.Treepath#getTreeAtStart()} end} tree.
     *
     * @see #getNext (Treepath)
     */
    public Treepath< T > getFirst( final Treepath< T > treepath ) {
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
     * t2  *t3       *t2   t3        t2   t3       t2   *t3
     * </pre>
     *
     * This is a valuable traversal algorithm that preserves indexes of unmodified treepaths.
     *
     * @param treepath a non-null object.
     * @return the treepath to the next tree, or null.
     */
    public Treepath< T > getNext(
        final Treepath< T > treepath
    ) {
      if( treepath.getLength() > 1 ) {
        if( TreepathTools.hasPreviousSibling( treepath ) ) {
          final Treepath< T > previousSibling = TreepathTools.getPreviousSibling( treepath ) ;
          if( previousSibling.getTreeAtEnd().getChildCount() == 0 ) {
            return previousSibling ;
          } else {
            return getFirst(previousSibling) ;
          }
        } else {
          return treepath.getPrevious() ;
        }
      } else {
        return null ;
      }

    }

  }
}

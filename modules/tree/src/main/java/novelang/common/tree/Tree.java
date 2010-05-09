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

import org.apache.commons.lang.NullArgumentException;

/**
 * This interface captures the essential behavior of an immutable tree.
 * <p>
 * The strange recursive definition using generics is required for strong-typing the
 * {@link #adopt(Iterable)} method across implementors.
 *
 * @see TreeTools manipulation primitives
 * @see ImmutableTree a base implementation
 *
 * @author Laurent Caillette
 */
public interface Tree< T extends Tree > {

  /**
   * Returns the the child of given index.
   * <p>
   * Note about Novelang implementation:
   * This can't be called {@code getChild()} because it would clash with
   * {@link org.antlr.runtime.tree.Tree#getChild(int)} which returns a
   * {@link org.antlr.runtime.tree.Tree}.
   *
   * @param index a value between [0, {@link #getChildCount()}[.
   * @return a non-null object.
   * @throws ArrayIndexOutOfBoundsException
   */
  T getChildAt( int index ) ;

  /**
   * Returns the count of children.
   *
   * @return An integer equal to or greater than 0.
   */
  int getChildCount() ;

  /**
   * Returns an iterable over children.
   *
   * @return a non-null object returning a non-null iterator, even if there are no children.
   */
//  Iterable< ? extends T > getChildren() ;

  /**
   * This method clones the node-related values while new children are set
   * (like when adding or removing children in a copy-on-change operation).
   *
   * Implementations should pass the {@code newChildren} parameter to their constructor
   * without modification.
   *
   * @param newChildren a non-null array containing no nulls.
   * @return a non-null object.
   *
   */
  T adopt( Iterable< T > newChildren ) throws NullArgumentException ;

  /**
   * Experimental: behavior of a function object for mutating {@link Tree}'s children.
   * <p>
   * This interface defines the contract for changing a list in every possible way:
   * <ul>
   *   <li>Insert any number of elements at any place.
   *   <li>Remove any element.
   *   <li>Update (change reference to) any element.
   * </ul>
   *
   * @author Laurent Caillette
   */
  public static interface Evolver< T > {

    /**
     * Gives a hint to avoid full list traversal whenever possible.
     *
     * @param listSize a value inside [ 1, {@link Integer#MAX_VALUE} ] interval.
     * @return a value inside [ -1, {@code listSize} ] interval.
     */
//    int startsAt( int listSize ) ;

    /**
     * Returns the updated element.
     *
     * @param listSize number of elements, a value inside [ 0, {@link Integer#MAX_VALUE} ] interval.
     * @param index a value inside [ -1, {@code listSize} ] interval.
     * @param original a possibly null object.
     * @return a possibly null object, meaning removal (when {@code original} wasn't null) or
     *         do-nothing (when {@code original} was null, at the extremi of the traversal).
     */
    T apply( int index, T original, int listSize ) ;


  }
}

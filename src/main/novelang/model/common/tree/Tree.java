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
 * This interface captures the essential behavior of an immutable tree.
 *
 * @author Laurent Caillette
 */
public interface Tree< T extends Tree > {
  /**
   * This can't be called {@code getChild()} because it would clash with
   * {@link org.antlr.runtime.tree.Tree#getChild(int)} which returns a
   * {@link org.antlr.runtime.tree.Tree}.
   */
  T getChildAt( int i ) ;

  int getChildCount() ;

  Iterable< ? extends T > getChildren() ;

  /**
   * This method clones the node-related values while new children are set
   * (like when adding or removing children in a copy-on-change operation).
   *
   * Implementations should pass the {@code newChildren} parameter to their constructor
   * without modification.
   *
   * @param newChildren a non-null array containing no nulls.
   * @return a non-null object.
   */
  T adopt( T... newChildren ) throws NullArgumentException ;

}

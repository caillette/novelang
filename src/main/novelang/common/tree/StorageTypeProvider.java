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

/**
 * This interface provides a workaround to the {@code java.lang.ArrayStoreException}
 * occuring when adding a {@code Tree} of an incompatible type to an existing {@code Tree}.
 * <p>
 * For an interface {@code I} which extends {@code Tree} there can be two concrete classes
 * {@code A} and {@code B} implementing {@code I}. Unless specified otherwise, the
 * mutators in {@link novelang.common.tree.TreeTools} create a new array of the type
 * of the tree passed as parameter, a concrete type which can be {@code A} or {@code B}.
 * So when storing {@code B}s inside a {@code A[]} an {@code java.lang.ArrayStoreException} occurs.
 * <p>
 * When the {@code Tree} to be modified implements this interface, the mutators of
 * {@code TreeTools} take it in account for the creation of the new structure
 * (currently an array).
 * <p>
 * So in the example above, {@code A} and {@code B} should implement this interface and
 * return {@code Class<I>}.
 *
 * @author Laurent Caillette
 */
public interface StorageTypeProvider< T extends Tree > {

  /**
   * Returns the class of the container for children of heterogeneous types.
   * 
   * @return a non-null object.
   */
  Class< ? extends T > getStorageType() ;
}

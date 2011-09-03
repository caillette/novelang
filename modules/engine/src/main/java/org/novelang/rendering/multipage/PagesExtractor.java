/*
 * Copyright (C) 2011 Laurent Caillette
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
package org.novelang.rendering.multipage;

import com.google.common.collect.ImmutableMap;

import org.novelang.common.SyntacticTree;
import org.novelang.common.metadata.PageIdentifier;

/**
 * Given a {@link SyntacticTree}, returns an abstraction of subtrees.
 * The {@code Map} has {@link org.novelang.common.metadata.PageIdentifier}s for keys, and for values a {@code String}
 * that a {@link org.novelang.rendering.Renderer} may use for finding the same subtree again.
 *
 * @see org.novelang.common.metadata.Page
 *
 * @author Laurent Caillette
 */
public interface PagesExtractor {

  /**
   * Extracts the identifiers along with associated path, each pair forming a
   * {@link org.novelang.common.metadata.Page}.
   * 
   * Returns a {@code Map} rather than a set of {@link org.novelang.common.metadata.Page} because the latter wouldn't
   * guarantee key uniqueness (unless some severe twist to the {@code Set} contract).
   *
   * @param documentTree a non-null object.
   * @return a non-null but possibly empty {@code Map}.
   */
  ImmutableMap< PageIdentifier, String > extractPages( SyntacticTree documentTree )
      throws Exception ;

  ImmutableMap< PageIdentifier, String > EMPTY_MAP = ImmutableMap.of() ;

}

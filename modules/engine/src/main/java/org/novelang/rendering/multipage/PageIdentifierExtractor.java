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
package org.novelang.rendering.multipage;

import com.google.common.collect.ImmutableMap;
import org.novelang.common.SyntacticTree;

/**
 * Given a {@link SyntacticTree}, returns an abstraction of subtrees.
 * The {@code Map} has {@link PageIdentifier}s for keys, and for values a {@code String}
 * that a {@link org.novelang.rendering.Renderer} may use for finding the same subtree again.
 *
 * @author Laurent Caillette
 */
public interface PageIdentifierExtractor {

  /**
   * Extracts the identifiers.
   *
   * @param documentTree a non-null object.
   * @return a non-null but possibly empty {@code Map}.
   */
  ImmutableMap< PageIdentifier, String > extractPageIdentifiers( SyntacticTree documentTree ) ;

}

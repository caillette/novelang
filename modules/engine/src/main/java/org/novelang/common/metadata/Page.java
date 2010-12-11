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
package org.novelang.common.metadata;

import com.google.common.collect.ImmutableMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Holds a consistent pair of
 * ({@link PageIdentifier}, {@code String})
 * as returned by
 * {@link org.novelang.rendering.multipage.PagesExtractor#extractPages(org.novelang.common.SyntacticTree)}.
 *
 * @author Laurent Caillette
 */
public final class Page {

  private final PageIdentifier pageIdentifier ;
  private final String path ;

  private Page( final PageIdentifier pageIdentifier, final String path ) {
    this.pageIdentifier = checkNotNull( pageIdentifier ) ;
    this.path = checkNotNull( path ) ;
  }

  /**
   * @return a non-null object.
   */
  public PageIdentifier getPageIdentifier() {
    return pageIdentifier ;
  }

  /**
   * @return a non-null, but possibly empty {@code String}.
   */
  public String getPath() {
    return path ;
  }

  /**
   * @return a non-null object.
   * @throws NoSuchPageIdentifierException if no page found for this identifier.
   */
  public static Page get(
      final ImmutableMap< PageIdentifier, String > map,
      final PageIdentifier pageIdentifier
  ) throws NoSuchPageIdentifierException {
    final String path = map.get( pageIdentifier ) ;

    if( path == null ) {
      throw new NoSuchPageIdentifierException( pageIdentifier, map ) ;
    } else {
      return new Page( pageIdentifier, path ) ;
    }
  }

}

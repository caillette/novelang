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
package org.novelang.rendering;

import java.util.regex.Pattern;

/**
 * A page name for a multipage document.
 * The name is filesystem and URL friendly.
 *
 * @author Laurent Caillette
 */
public class PageIdentifier {

  private final String name ;

  public PageIdentifier( final String name ) {
    this.name = name == null ? "" : name ;
  }

  /**
   * Returns a page identifier that will appear after originating document name in the URL.
   *
   * @return a non-null, possibly empty {@code String}.
   */
  public String getName() {
    return name;
  }

  /**
   * Identical to Tag syntax.
   */
  private static final Pattern PATTERN = Pattern.compile( "[a-zA-Z0-9]+(?:[_\\-][a-zA-Z0-9]+)*" ) ;

}

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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.novelang.outfit.loader;

import org.apache.commons.lang.StringUtils;

/**
 * Thrown by {@link ResourceLoader} when expected resource wasn't there.
 *
 * @author Laurent Caillette
 */
public class ResourceNotFoundException extends RuntimeException {

  final String searchPath ;

  public ResourceNotFoundException( final String resourceName, final String searchPath ) {
    super( "Not found: '" + resourceName + "'" + searchPathAsFragmentMessage( searchPath ) ) ;
    this.searchPath = searchPath ;
  }

  public ResourceNotFoundException(
      final String resourceName, 
      final String searchPath, 
      final Exception cause 
  ) {
    super(
        "Not found: '" + resourceName + "'" + searchPathAsFragmentMessage( searchPath ),
        cause
    ) ;
    this.searchPath = searchPath ;
  }

  public ResourceNotFoundException( final ResourceName resourceName, final String searchPath ) {
    this( resourceName.getName(), searchPath ) ;
  }

  public ResourceNotFoundException( 
      final ResourceName resourceName, 
      final String searchPath, 
      final Exception cause 
  ) {
    this( resourceName.getName(), searchPath, cause ) ;
  }

  /**
   * @return a possibly null {@code String}.
   */
  public final String getSearchPath() {
    return searchPath ;
  }

  public static String concatenateSearchPaths(
      final ResourceNotFoundException first,
      final ResourceNotFoundException second
  ) {
    return first.getSearchPath() + second.getSearchPath() ;
  }

  private static String searchPathAsFragmentMessage( final String searchPath ) {
    return ( StringUtils.isBlank( searchPath ) ? "" : " in \n" + searchPath );
  }


}

/*
 * Copyright (C) 2006 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.loader;

import java.net.URL;

import org.apache.commons.lang.StringUtils;

/**
 * @author Laurent Caillette
 */
public class ResourceNotFoundException extends RuntimeException {

  final String searchPath ;

  public ResourceNotFoundException( String resourceName, String searchPath ) {
    super( "Not found: '" + resourceName + "' in \n" + searchPath ) ;
    this.searchPath = searchPath ;
  }

  public ResourceNotFoundException( String resourceName, String searchPath, Exception cause ) {
    super( "Not found: '" + resourceName + "' in \n" + searchPath, cause ) ;
    this.searchPath = searchPath ;
  }

  public ResourceNotFoundException( URL baseUrl, String resourceName, Exception cause ) {
    super( "Not found: '" + baseUrl.toExternalForm() + "/" + resourceName + "'", cause ) ;
    this.searchPath = "\n    " + baseUrl.toExternalForm() ;
  }

  public final String getSearchPath() {
    return searchPath ;
  }

  public static final String concatenateSearchPaths(
      ResourceNotFoundException first,
      ResourceNotFoundException second
  ) {
    return first.getSearchPath() + second.getSearchPath() ;
  }

}
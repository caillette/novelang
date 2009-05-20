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

package novelang.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import novelang.system.LogFactory;
import novelang.system.Log;

/**
 * Loads resources relative to a given URL.
 * 
 * @author Laurent Caillette
 */
public class UrlResourceLoader implements ResourceLoader {

  private static final Log LOG = LogFactory.getLog( UrlResourceLoader.class ) ;

  private final URL base ;
  private final String searchPath ;

  public UrlResourceLoader( URL base ) {
    final String urlAsString = base.toExternalForm();
    if( urlAsString.endsWith( "/" ) ) {
      this.base = base ;
    } else {
      try {
        this.base = new URL( urlAsString + "/" ) ;
      } catch( MalformedURLException e ) {
        throw new RuntimeException( e ) ;
      }
    }
    this.searchPath = createSearchPath( base ) ;
  }

  public InputStream getInputStream( ResourceName resourceName ) throws ResourceNotFoundException {

    final URL resourceUrl ;
    try {
      resourceUrl = new URL( base, resourceName.getName() ) ;
    } catch( MalformedURLException e ) {
      LOG.debug( "Could not find resource '%s' from %s", resourceName.getName(), this ) ;
      throw new ResourceNotFoundException( resourceName, searchPath, e );
    }
    try {
      final InputStream inputStream = resourceUrl.openStream() ;
      LOG.debug( "Opened stream '%s'", resourceUrl.toExternalForm() ) ;
      return inputStream;
    } catch( IOException e ) {
      LOG.debug( "Could not find resource '%s' from %s", resourceUrl, this ) ;
      throw new ResourceNotFoundException( resourceName, searchPath, e );
    }
  }

  private final String createSearchPath( URL baseUrl ) {
    final StringBuffer buffer = new StringBuffer( "  " + toString() + "\n" ) ;
    buffer.append( "      " ) ;
    buffer.append( baseUrl.toExternalForm() ) ;
    buffer.append( "\n" ) ;
    return buffer.toString() ;
  }
}

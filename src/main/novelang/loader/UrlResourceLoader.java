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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Loads resources relative to a given URL.
 * 
 * @author Laurent Caillette
 */
public class UrlResourceLoader implements ResourceLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger( UrlResourceLoader.class ) ;

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

  public InputStream getInputStream( String resourceName ) throws ResourceNotFoundException {
    if( StringUtils.isBlank( resourceName ) ) {
      throw new IllegalArgumentException( "resourceName=" + resourceName ) ;
    }
    final String normalizedResourceName =
        resourceName.startsWith( "/" ) ?
        resourceName.substring( 1, resourceName.length() ) :
        resourceName
    ;

    final URL resourceUrl ;
    try {
      resourceUrl = new URL( base, normalizedResourceName );
    } catch( MalformedURLException e ) {
      LOGGER.debug( "Could not find resource '{}' from {}", normalizedResourceName, this ) ;
      throw new ResourceNotFoundException( resourceName, searchPath, e );
    }
    try {
      final InputStream inputStream = resourceUrl.openStream() ;
      LOGGER.debug( "Opened stream '{}'", resourceUrl.toExternalForm() ) ;
      return inputStream;
    } catch( IOException e ) {
      LOGGER.debug( "Could not find resource '{}' from {}", resourceUrl, this ) ;
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

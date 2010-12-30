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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;

/**
 * Loads resources relative to a given URL.
 * 
 * @author Laurent Caillette
 */
public class UrlResourceLoader extends AbstractResourceLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger( UrlResourceLoader.class ) ;

  private final URL base ;
  private final String multilineDescription;

  public UrlResourceLoader( final URL base ) {
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
    this.multilineDescription = getClass().getSimpleName() + " " + base.toExternalForm() ;
  }

  @Override
  protected String getMultilineDescription() {
    return multilineDescription ;
  }

  @Override
  public InputStream maybeGetInputStream( final ResourceName resourceName ) {

    final URL resourceUrl ;
    try {
      resourceUrl = new URL( base, resourceName.getName() ) ;
    } catch( MalformedURLException e ) {
      throw new RuntimeException( e ) ;
    }
    if( resourceUrl == null ) {
      return null ;
    }
    try {
      final InputStream inputStream = resourceUrl.openStream() ;
      LOGGER.debug( "Opened stream '", resourceUrl.toExternalForm(), "'" );
      return inputStream ;
    } catch( IOException e ) {
      // Is there a case where this makes more sense than rethrowing in a RuntimeException?
      return null ;
    }
  }

}

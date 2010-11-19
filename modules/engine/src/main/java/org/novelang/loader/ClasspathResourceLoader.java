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

package org.novelang.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

import com.google.common.base.Preconditions;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.apache.commons.lang.SystemUtils;

/**
 * Loads resources relative to the root package of some given class
 * using its classloader.
 *
 * @author Laurent Caillette
 */
public class ClasspathResourceLoader implements ResourceLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger( ClasspathResourceLoader.class ) ;
  private final Class reference ;
  private final String path ;

  protected ClasspathResourceLoader( final Class reference, final String path ) {
    this.reference = Preconditions.checkNotNull( reference ) ;
    this.path = null == path ? "" : normalize( path ) ;
  }

  public ClasspathResourceLoader() {
    this( ClasspathResourceLoader.class, null ) ;
  }

  public ClasspathResourceLoader( final String path ) {
    this( ClasspathResourceLoader.class, path ) ;
  }

  @Override
  public InputStream getInputStream( final ResourceName resourceName ) {
    final String absoluteName = path + "/" + resourceName.getName() ; // normalize( resourceName ) ;
    LOGGER.debug( "Attempting to load '", absoluteName, "'" ) ;

    final URL url = reference.getResource( absoluteName ) ;
    if( null == url ) {
      LOGGER.debug( "Could not find resource '", absoluteName, "' from ", this ) ;
      throw new ResourceNotFoundException( absoluteName, getBestDescriptorForClassloader() ) ;
    }
    final String urlAsString = url.toExternalForm();
    try {
      final InputStream inputStream = url.openStream();
      LOGGER.info( "Opened stream '", urlAsString, "'" ) ;
      return inputStream;
    } catch( IOException e ) {
      LOGGER.debug( "Could not find resource '", urlAsString, "' from ", this ) ;
      throw new ResourceNotFoundException( absoluteName, getBestDescriptorForClassloader(), e ) ;
    }
  }


  /**
   * Force "/" at beginning, removes "/" at end.
   */
  private static String normalize( final String path ) {
    String newPath = path ;
    if( ! path.startsWith( "/" ) ) {
      newPath = "/" + path ;
    }
    if( path.endsWith( "/" ) ) {
      newPath = path.substring( 0, path.length() - 1 ) ;
    }
    return newPath ;
  }

  private static final String SYSTEM_CLASSPATH = "file:" + SystemUtils.JAVA_HOME ;

  private String getBestDescriptorForClassloader() {
    final ClassLoader classLoader = getClass().getClassLoader();
    final StringBuffer buffer = new StringBuffer( "  " + toString() + "\n" ) ;
    if( classLoader instanceof URLClassLoader ) {
      final URLClassLoader urlClassLoader = ( URLClassLoader ) classLoader ;
      final URL[] urls = urlClassLoader.getURLs() ;
      for( int i = 0 ; i < urls.length ; i++ ) {
        final URL url = urls[ i ] ;
        final String urlAsString = url.toExternalForm();
        if( ! urlAsString.startsWith( SYSTEM_CLASSPATH ) ) {
          buffer.append( "      " ) ;
          buffer.append( urlAsString ) ;
          buffer.append( "\n" ) ;
        }
      }
    } else {
      buffer.append( "    " ).append( classLoader.toString() );
    }
    return buffer.toString() ;
  }
}

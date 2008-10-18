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
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

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

  protected ClasspathResourceLoader( Class reference, String path ) {
    this.reference = Preconditions.checkNotNull( reference ) ;
    this.path = null == path ? "" : normalize( path ) ;
  }

  public ClasspathResourceLoader() {
    this( ClasspathResourceLoader.class, null ) ;
  }

  public ClasspathResourceLoader( String path ) {
    this( ClasspathResourceLoader.class, path ) ;
  }

  public InputStream getInputStream( ResourceName resourceName ) {
    final String absoluteName = path + "/" + resourceName.getName() ; // normalize( resourceName ) ;

    final URL url = reference.getResource( absoluteName ) ;
    if( null == url ) {
      LOGGER.debug( "Could not find resource '{}' from {}", absoluteName, this ) ;
      throw new ResourceNotFoundException( absoluteName, getBestDescriptorForClassloader() ) ;
    }
    final String urlAsString = url.toExternalForm();
    try {
      final InputStream inputStream = url.openStream();
      LOGGER.info( "Opened stream '{}'", urlAsString ) ;
      return inputStream;
    } catch( IOException e ) {
      LOGGER.debug( "Could not find resource '{}' from {}", urlAsString, this ) ;
      throw new ResourceNotFoundException( absoluteName, getBestDescriptorForClassloader(), e ) ;
    }
  }


  /**
   * Force "/" at beginning, removes "/" at end.
   */
  private static String normalize( String path ) {
    if( ! path.startsWith( "/" ) ) {
      path = "/" + path ;
    }
    if( path.endsWith( "/" ) ) {
      path = path.substring( 0, path.length() - 1 ) ;
    }
    return path ;
  }

  private static final String SYSTEM_CLASSPATH = "file:" + SystemUtils.JAVA_HOME ;

  private final String getBestDescriptorForClassloader() {
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

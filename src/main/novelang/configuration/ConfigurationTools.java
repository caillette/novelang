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
package novelang.configuration;

import java.io.IOException;
import java.io.File;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.loader.ResourceLoader;
import novelang.loader.ClasspathResourceLoader;
import novelang.loader.ResourceLoaderTools;
import novelang.loader.UrlResourceLoader;

/**
 * Builds objects describing how to configure other components.
 *
 * <p>
 * {@link #buildRenderingConfiguration()} creates a {@link ResourceLoader} which attempts to
 * load resources by looking at, in order:
 * <ol>
 *   <li>Directory as given by System property <tt>{@value #NOVELANG_STYLE_DIR_PROPERTYNAME}</tt>
 *       (maybe absolute or relative to System property {@code user.dir}).
 *   <li>Directory named <tt>{@value #USER_STYLE_DIR}</tt> under current directory
 *       (relative to System property {@code user.dir}).
 *   <li>Application classpath, looking for a resource inside <tt>/{@value #BUNDLED_STYLE_DIR}</tt>
 *       directory.
 * </ol>
 *
 * @author Laurent Caillette
 */
public class ConfigurationTools {

  private static final Logger LOGGER = LoggerFactory.getLogger( ConfigurationTools.class ) ;

  private ConfigurationTools() { }


// =========
// Rendering
// =========

  private static final String BUNDLED_STYLE_DIR = "style" ;
  private static final String USER_STYLE_DIR = "style" ;
  protected static final String NOVELANG_STYLE_DIR_PROPERTYNAME = "novelang.style.dir" ;

  private static final ResourceLoader STYLE_RESOURCE_LOADER;
  
  static {
    final URL userStyleDir ;

    // First use system property.
    final String stylesDirNameBySystemProperty =
        System.getProperty( NOVELANG_STYLE_DIR_PROPERTYNAME ) ;

    if( StringUtils.isBlank( stylesDirNameBySystemProperty ) ) {
      // Second use {user.dir}/style
      final File styleDirAsSubdirectory = new File( USER_STYLE_DIR ) ;
      if( styleDirAsSubdirectory.exists() ) {
        try {
          userStyleDir = styleDirAsSubdirectory.getCanonicalFile().toURL() ;
          LOGGER.info( "Styles directory set to '{}'", userStyleDir.toExternalForm() ) ;
          
        } catch( IOException e ) {
          throw new RuntimeException( e ) ;
        }
      } else {
        userStyleDir = null ;
        LOGGER.warn( "Styles directory '{}' does not exist", stylesDirNameBySystemProperty ) ;
      }
    } else {
      final File dir = new File( stylesDirNameBySystemProperty ) ;
      if( dir.exists() ) {
        try {
          userStyleDir = dir.getCanonicalFile().toURL() ;
        } catch( IOException e ) {
          throw new RuntimeException( e ) ;
        }
        LOGGER.info( "Styles directory set to '{}'", userStyleDir.toExternalForm() ) ;
      } else {
        userStyleDir = null ;
        LOGGER.warn( "Styles directory '{}' does not exist", stylesDirNameBySystemProperty ) ;
      }

    }

    final ResourceLoader classpathResourceLoader = new ClasspathResourceLoader( BUNDLED_STYLE_DIR ) ;
    if( null == userStyleDir ) {
      STYLE_RESOURCE_LOADER = classpathResourceLoader ;
    } else {
      STYLE_RESOURCE_LOADER = ResourceLoaderTools.compose(
          new UrlResourceLoader( userStyleDir ), classpathResourceLoader ) ;
    }
  }


  public static RenderingConfiguration buildRenderingConfiguration() {
    return new RenderingConfiguration() {
      public ResourceLoader getResourceLoader() {
        return STYLE_RESOURCE_LOADER;
      }
    } ;
  }

}

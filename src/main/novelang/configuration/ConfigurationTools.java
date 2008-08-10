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

package novelang.configuration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FOPException;
import org.apache.avalon.framework.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.loader.ClasspathResourceLoader;
import novelang.loader.ResourceLoader;
import novelang.loader.ResourceLoaderTools;
import novelang.loader.UrlResourceLoader;
import com.google.common.collect.Iterables;

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
 * <p>
 * {@link #buildRenderingConfiguration()} also creates a {@link FopFactory} relying on fonts
 * in {@value #FONTS_DEFAULT_DIRECTORYNAME} if such directory is declared through a mechanism
 * similar to the one defined above.
 * <p>
 * TODO Use some kind of bean to encapsulate command-line arguments instead of relying
 *   on system properties.
 *
 *
 * @author Laurent Caillette
 */
public class ConfigurationTools {

  private static final Logger LOGGER = LoggerFactory.getLogger( ConfigurationTools.class ) ;

  private ConfigurationTools() { }


// =========
// Rendering
// =========

  public static final String BUNDLED_STYLE_DIR = "style" ;
  private static final String USER_STYLE_DIR = "style" ;
  protected static final String NOVELANG_STYLE_DIR_PROPERTYNAME = "novelang.style.dir" ;


  protected static final String FONTS_DEFAULT_DIRECTORYNAME = "fonts" ;
  protected static final String FONTS_DIR_PROPERTYNAME = "novelang.fonts.dir" ;

  protected static final String FOP_FONTMETRICS_DEFAULT_DIRECTORYNAME = "fop-metrics" ;
  protected static final String FOP_METRICS_DIR_PROPERTYNAME = "novelang.fop.fontmetrics.dir" ;

  protected static final FopFactory FOP_FACTORY ;
  protected static final Iterable< FontDescriptor > USER_FONTS ;

  private static final ResourceLoader STYLE_RESOURCE_LOADER;

  private static File resolveDirectory(
      String topicName,
      String customDirectoryPropertyName,
      String defaultSubdirectoryName,
      boolean create
  ) {

    final File directory;

    // First use system property.
    final String dirNameBySystemProperty =
        System.getProperty( customDirectoryPropertyName ) ;


    if( StringUtils.isBlank( dirNameBySystemProperty ) ) {

      final File styleDirAsSubdirectory = new File( defaultSubdirectoryName ) ;

      // Second use {user.dir}/xxx
      if( styleDirAsSubdirectory.exists() ) {
        try {
          directory = styleDirAsSubdirectory.getCanonicalFile() ;
          LOGGER.info(
              "Directory for " + topicName + " set to '" + directory + "' "
            + "(found '" + defaultSubdirectoryName + "' directory under [user.dir] "
            + "and no system property [" + customDirectoryPropertyName + "])."
          ) ;

        } catch( IOException e ) {
          throw new RuntimeException( e ) ;
        }
      } else {
        LOGGER.warn(
            "Cannot find directory for " + topicName + " "
          + "(no '" + defaultSubdirectoryName + "' directory found under [user.dir], "
          + "nor system property [" + customDirectoryPropertyName + "])."

        ) ;
        directory = createIfRequired( styleDirAsSubdirectory, create ) ;
      }
    } else {
      final File dir = new File( dirNameBySystemProperty ) ;
      if( dir.exists() ) {
        try {
          directory = dir.getCanonicalFile() ;
        } catch( IOException e ) {
          throw new RuntimeException( e ) ;
        }
        LOGGER.info(
            "Directory for " + topicName + " set to '" + directory.getAbsolutePath() + "' " +
            "(from system property [" + customDirectoryPropertyName + "])."
        ) ;
      } else {
        LOGGER.warn(
            "Directory '" + dirNameBySystemProperty + "' for " + topicName + " does not exist "
          + "(was set as system property [" + customDirectoryPropertyName + "])."
        ) ;
        directory = createIfRequired( dir, create ) ;
      }

    }

    return directory;
  }

  private static File createIfRequired(
      File styleDirAsSubdirectory,
      boolean create
  ) {
    if( create ) {
      if( styleDirAsSubdirectory.mkdir() ) {
        LOGGER.error(
            "Created '{}' anyways.",
            styleDirAsSubdirectory.getAbsolutePath()
        ) ;
        return styleDirAsSubdirectory ;
      } else {
        LOGGER.error(
            "Creation of '{}' failed for an unknown reason.",
            styleDirAsSubdirectory.getAbsolutePath()
        ) ;
        return null ;
      }
    } else {
      return null ;
    }
  }


  static {

    LOGGER.info(
        "Configuration resolving relative directories from '{}'.",
        new File( System.getProperty( "user.dir" ) ).getAbsolutePath()
    ) ;

    final File fontsDirectory = resolveDirectory(
        "fonts",
        FONTS_DIR_PROPERTYNAME,
        FONTS_DEFAULT_DIRECTORYNAME,
        false
    );

    final File fopFontMetricsDirectory ;
    if( null == fontsDirectory ) {
      fopFontMetricsDirectory = null ;
    } else {
      fopFontMetricsDirectory = resolveDirectory(
        "font metrics",
          FOP_METRICS_DIR_PROPERTYNAME,
          FOP_FONTMETRICS_DEFAULT_DIRECTORYNAME,
          true
      );
    }

    USER_FONTS = createFontDescriptors( fontsDirectory, fopFontMetricsDirectory ) ;
    FOP_FACTORY = createFopFactory( USER_FONTS ) ;
    

    final File userStyleDirectory = resolveDirectory(
        "style",
        NOVELANG_STYLE_DIR_PROPERTYNAME,
        USER_STYLE_DIR,
        false
    ) ;
    
    final URL userStyleUrl ;
    try {
      userStyleUrl = userStyleDirectory.toURI().toURL();
    } catch( MalformedURLException e ) {
      throw new RuntimeException( e );
    }

    final ResourceLoader classpathResourceLoader =
        new ClasspathResourceLoader( BUNDLED_STYLE_DIR ) ;
    if( null == userStyleDirectory ) {
      STYLE_RESOURCE_LOADER = classpathResourceLoader ;
    } else {
      STYLE_RESOURCE_LOADER = ResourceLoaderTools.compose(
          new UrlResourceLoader( userStyleUrl ), classpathResourceLoader ) ;
    }


  }


  public static RenderingConfiguration buildRenderingConfiguration() {
    return new RenderingConfiguration() {
      public ResourceLoader getResourceLoader() {
        return STYLE_RESOURCE_LOADER;
      }
      public FopFactory getFopFactory() {
        return FOP_FACTORY ;
      }
      public Iterable< FontDescriptor > getFontDescriptors() {
        return USER_FONTS ;
      }
    } ;
  }

  private static Iterable< FontDescriptor > createFontDescriptors(
      File fontsDirectory,
      File fopMetricsDirectory
  ) {
    Iterable< FontDescriptor > fontFileDescriptors = Iterables.emptyIterable() ;
    if( fontsDirectory != null && fopMetricsDirectory != null ) {
      try {
        fontFileDescriptors = FopFontsTools.createFopMetrics(
            fontsDirectory, fopMetricsDirectory ) ;
      } catch( IOException e ) {
        fontFileDescriptors = Iterables.emptyIterable() ;
        LOGGER.error( "Could not use custom fonts", e ) ;
      }
    }
    return fontFileDescriptors ;
  }

  private static FopFactory createFopFactory( Iterable< FontDescriptor > fontDescriptors ) {
    final FopFactory fopFactory = FopFactory.newInstance();
    if( fontDescriptors.iterator().hasNext() ) {
      try {
        final Configuration configuration =
            FopFontsTools.createFopConfiguration( fontDescriptors ) ;
        fopFactory.setUserConfig( configuration ) ;
      } catch( FOPException e ) {
        LOGGER.error( "Could not use custom fonts", e ) ;
      }
    }
    return fopFactory ;
  }

// =======
// Content
// =======

  private static final File CONTENT_ROOT = new File( SystemUtils.USER_DIR ) ;

  public static ContentConfiguration buildContentConfiguration() {
    return new ContentConfiguration() {
      public File getContentRoot() {
        return CONTENT_ROOT ;
      }
    } ;
  }


// ==========
// HttpServer
// ==========

  private static final RenderingConfiguration RENDERING_CONFIGURATION =
      buildRenderingConfiguration() ;
  private static final ContentConfiguration CONTENT_CONFIGURATION =
      buildContentConfiguration() ;

  public static ServerConfiguration buildServerConfiguration() {
    return new ServerConfiguration() {
      public RenderingConfiguration getRenderingConfiguration() {
        return RENDERING_CONFIGURATION ;
      }

      public ContentConfiguration getContentConfiguration() {
        return CONTENT_CONFIGURATION ;
      }
    } ;
  }

}

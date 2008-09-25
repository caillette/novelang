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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.MutableConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FopFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList;
import novelang.loader.ClasspathResourceLoader;
import novelang.loader.ResourceLoader;
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
  protected static final String FONTS_DIRS_PROPERTYNAME = "novelang.fonts.dir" ;

  protected static final String FOP_HYPHENATION_DEFAULT_DIRECTORYNAME = "hyphenation" ;
  protected static final String FOP_HYPHENATION_DIR_PROPERTYNAME = "novelang.fop.hyphenation.dir" ;

  protected static final FopFactory FOP_FACTORY ;

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


  private static final Iterable< File > FONTS_DIRECTORIES ;

  static {

    final File userDirectory = new File( System.getProperty( "user.dir" ) ) ;

    LOGGER.info(
        "Configuration resolving relative directories from '{}'.",
        userDirectory.getAbsolutePath()
    ) ;


    final String declaredFontDirectories = System.getProperty( FONTS_DIRS_PROPERTYNAME ) ;
    if( ! StringUtils.contains( declaredFontDirectories, SystemUtils.PATH_SEPARATOR ) ) {
      FONTS_DIRECTORIES = Lists.newArrayList( resolveDirectory(
          "fonts",
          FONTS_DIRS_PROPERTYNAME,
          FONTS_DEFAULT_DIRECTORYNAME,
          false
      ) ) ;
    } else {
      if( null == declaredFontDirectories ) {
        FONTS_DIRECTORIES = Iterables.emptyIterable() ;
      } else {
        FONTS_DIRECTORIES = parseDirectoryList( declaredFontDirectories ) ;
        for( File fontDirectory : FONTS_DIRECTORIES ) {
          if( fontDirectory.exists() ) {
            LOGGER.info(
                "Found font directory '" + fontDirectory.getAbsolutePath() + "' " +
                "(from system property [" + FONTS_DIRS_PROPERTYNAME + "])."
            ) ;
          } else {
            LOGGER.info(
                "Not found: font directory '" + fontDirectory.getAbsolutePath() + "' " +
                "(from system property [" + FONTS_DIRS_PROPERTYNAME + "])."
            ) ;
          }
        }
      }
    }

    final File hyphenationDirectory = resolveDirectory(
        "hyphenation",
        FOP_HYPHENATION_DIR_PROPERTYNAME,
        FOP_HYPHENATION_DEFAULT_DIRECTORYNAME,
        false
    ) ;

    FOP_FACTORY = createFopFactory( FONTS_DIRECTORIES, hyphenationDirectory ) ;
    

    final File userStyleDirectory = resolveDirectory(
        "style",
        NOVELANG_STYLE_DIR_PROPERTYNAME,
        USER_STYLE_DIR,
        false
    ) ;
    
    final URL userStyleUrl ;
    if( null == userStyleDirectory ) {
      userStyleUrl = null ;
    } else {
      try {
        userStyleUrl = userStyleDirectory.toURI().toURL();
      } catch( MalformedURLException e ) {
        throw new RuntimeException( e );
      }
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

  /**
   * Returns directories known to contain fonts.
   * @return a non-null object that may be empty but contains no nulls.
   */
  public static Iterable< File > getFontsDirectories() {
    return FONTS_DIRECTORIES ;
  }



  public static RenderingConfiguration buildRenderingConfiguration() {
    return new RenderingConfiguration() {
      public ResourceLoader getResourceLoader() {
        return STYLE_RESOURCE_LOADER;
      }
      public FopFactory getFopFactory() {
        return FOP_FACTORY ;
      }
    } ;
  }

  private static Iterable< File > parseDirectoryList( String directories ) {
    final StringTokenizer tokenizer =
        new StringTokenizer( directories, SystemUtils.PATH_SEPARATOR ) ;
    final List< File > directoryList = Lists.newLinkedList() ;
    while( tokenizer.hasMoreTokens() ) {
      directoryList.add( new File( tokenizer.nextToken() ) ) ;
    }
    return ImmutableList.copyOf( directoryList ) ;
  }

  private static FopFactory createFopFactory(
      Iterable< File > fontsDirectories,
      File hyphenationDirectory
  ) {
    final FopFactory fopFactory = FopFactory.newInstance() ;
    Configuration renderers = null ;
    Configuration hyphenationBase = null ;
    boolean configure = false ;

    if( null != fontsDirectories ) {
      renderers = FopTools.createRenderersConfiguration( fontsDirectories ) ;
      configure = true ;
    }

    if( null != hyphenationDirectory ) {
      hyphenationBase = FopTools.createHyphenationConfiguration( hyphenationDirectory ) ;
      configure = true ;
    }

    if( configure ) {

      final MutableConfiguration configuration = new DefaultConfiguration( "fop" ) ;
      configuration.setAttribute( "version", "1.0" ) ;

      if( null != renderers ) {
        configuration.addChild( renderers ) ;
      }

      if( null != hyphenationBase ) {
        configuration.addChild( hyphenationBase ) ;
      }

      LOGGER.debug( "Created configuration: \n{}",
          FopTools.configurationAsString( configuration ) ) ;

      try {
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

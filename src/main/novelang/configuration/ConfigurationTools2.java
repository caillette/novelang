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
import java.net.URL;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FOPException;
import novelang.configuration.parse.DaemonParameters;
import novelang.configuration.parse.GenericParameters;
import novelang.loader.ResourceLoader;
import novelang.loader.ClasspathResourceLoader;
import novelang.loader.ResourceLoaderTools;
import novelang.loader.UrlResourceLoader;
import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;

/**
 * Creates various Configuration objects from {@link GenericParameters}.
 * The main contract of this class is that for each created Configuration object, all information
 * needed is held by the Parameters object. No access to system property, no static variable, no
 * hidden state.
 *  
 * @author Laurent Caillette
 */
public class ConfigurationTools2 {

  private static final Logger LOGGER = LoggerFactory.getLogger( ConfigurationTools2.class ) ;

  public static final int DEFAULT_HTTP_DAEMON_PORT = 8080 ;
  public static final String DEFAULT_FONTS_DIRECTORY_NAME = "fonts" ;
  public static final String DEFAULT_HYPHENATION_DIRECTORY_NAME = "hyphenation" ;
  public static final String BUNDLED_STYLE_DIR = "style" ;
  private static final String DEFAULT_STYLE_DIR = "style" ;

  private static final String DAEMON_CONFIGURATION_SHORTCLASSNAME =
      ClassUtils.getShortClassName( DaemonConfiguration.class );

  public static ProducerConfiguration createProducerConfiguration( GenericParameters parameters )
      throws FOPException
  {
    final RenderingConfiguration renderingConfiguration =
        createRenderingConfiguration( parameters ) ;
    final ContentConfiguration contentConfiguration =
        createContentConfiguration( parameters ) ;

    return new ProducerConfiguration() {
      public RenderingConfiguration getRenderingConfiguration() {
        return renderingConfiguration ;
      }
      public ContentConfiguration getContentConfiguration() {
        return contentConfiguration ;
      }
    };
  }

  public static DaemonConfiguration createDaemonConfiguration( DaemonParameters parameters ) {
    final int port ;
    final Integer customPort = parameters.getHttpDaemonPort() ;
    if( null == customPort ) {
      port = DEFAULT_HTTP_DAEMON_PORT ;
      LOGGER.info(
          "Creating " + DAEMON_CONFIGURATION_SHORTCLASSNAME
        + " from default value [" + DEFAULT_HTTP_DAEMON_PORT + "] "
        + "(option not set: " + parameters.getHttpDaemonPortOptionDescription() + ")."

      ) ;
    } else {
      port = customPort ;
      LOGGER.info(
          "Creating " + DAEMON_CONFIGURATION_SHORTCLASSNAME + " "
        + "with custom value '" + customPort + "' "
        + "(from option: " + parameters.getHttpDaemonPortOptionDescription() + ")."
      ) ;
    }
    return new DaemonConfiguration() {
      public int getPort() {
        return port ;
      }
    } ;
  }

  public static ContentConfiguration createContentConfiguration( GenericParameters parameters ) {
    return new ContentConfiguration() {
      public File getContentRoot() {
        return new File( SystemUtils.USER_DIR ) ;
      }
    } ;
  }

  public static RenderingConfiguration createRenderingConfiguration( GenericParameters parameters )
      throws FOPException
  {
    final Iterable< File > fontDirectories ;
    final Iterable< File > userFontDirectories = parameters.getFontDirectories() ;
    if( userFontDirectories.iterator().hasNext() ) {
      fontDirectories = userFontDirectories ;
    } else {
      final File maybeDefaultDirectory = findDefaultDirectoryIfNeeded(
          parameters.getBaseDirectory(),
          null,
          parameters.getFontDirectoriesOptionDescription(),
          DEFAULT_FONTS_DIRECTORY_NAME
      ) ;

      if( null == maybeDefaultDirectory ) {
        fontDirectories = Iterables.emptyIterable() ;
      } else {
        fontDirectories = Lists.newArrayList( maybeDefaultDirectory ) ;
      }
    }

    final File hyphenationDirectory = findDefaultDirectoryIfNeeded(
        parameters.getBaseDirectory(),
        parameters.getHyphenationDirectory(),
        parameters.getHyphenationDirectoryOptionDescription(),
        DEFAULT_HYPHENATION_DIRECTORY_NAME
    ) ;


    final FopFactory fopFactory = FopTools
        .createFopFactory( fontDirectories, hyphenationDirectory ) ;

    final ResourceLoader resourceLoader = createResourceLoader( parameters ) ;

    return new RenderingConfiguration() {
      public ResourceLoader getResourceLoader() {
        return resourceLoader ;
      }
      public FopFactory getFopFactory() {
        return fopFactory ;
      }

      public FopFontStatus getCurrentFopFontStatus() {
        try {
          return FopTools.createGlobalFontStatus( fopFactory, fontDirectories ) ;
        } catch( FOPException e ) {
          throw new RuntimeException( e ) ;
        }
      }
    } ;

  }

  private static ResourceLoader createResourceLoader( GenericParameters parameters ) {

    final File userStyleDirectory = findDefaultDirectoryIfNeeded(
        parameters.getBaseDirectory(),
        parameters.getStyleDirectory(),
        parameters.getStyleDirectoryDescription(),
        DEFAULT_STYLE_DIR
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

    final ResourceLoader resourceLoader ;
    if( null == userStyleDirectory ) {
      resourceLoader = classpathResourceLoader ;
    } else {
      resourceLoader = ResourceLoaderTools.compose(
          new UrlResourceLoader( userStyleUrl ), classpathResourceLoader ) ;
    }

    return resourceLoader ;

  }

  protected static File findDefaultDirectoryIfNeeded(
      File baseDirectory,
      File userDefinedDirectory,
      String directoryDescription,
      String defaultDirectoryName
  ) {
    if( null == userDefinedDirectory ) {
      final File defaultDirectory = new File( baseDirectory, defaultDirectoryName ) ;
      if( defaultDirectory.exists() ) {
        LOGGER.info(
            "Using default directory '" + defaultDirectory.getAbsolutePath() + "' "
          + "(option not set: " + directoryDescription + ")."
        ) ;
        return defaultDirectory ;
      } else {
        LOGGER.info(
            "Found no default directory '" + defaultDirectoryName + "' "
          + "nor was set this option: " + directoryDescription + "."
        ) ;

        return null ;
      }
    } else {
      LOGGER.info(
          "Recognized user-defined directory '" + userDefinedDirectory + "' "
       +  "(from option: " + directoryDescription + ")." ) ;
      return userDefinedDirectory ;
    }
  }


}

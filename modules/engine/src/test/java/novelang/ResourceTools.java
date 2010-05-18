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

package novelang;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.MissingResourceException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.fonts.EmbedFontInfo;
import novelang.system.LogFactory;
import novelang.system.Log;
import novelang.system.DefaultCharset;
import novelang.loader.ResourceName;
import novelang.loader.ResourceLoader;
import novelang.loader.ClasspathResourceLoader;
import novelang.loader.UrlResourceLoader;
import novelang.loader.ResourceLoaderTools;
import novelang.configuration.ProducerConfiguration;
import novelang.configuration.RenderingConfiguration;
import novelang.configuration.FopFontStatus;
import novelang.configuration.ContentConfiguration;
import novelang.configuration.ConfigurationTools;
import novelang.configuration.DaemonConfiguration;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.base.Preconditions;

/**
 * Utility class for dealing with test-dedicated resources.
 * Some tests require files on the filesystem, while they are resources that are
 * only accessible from a classloader. So we have boring stuff with URLs, byte arrays
 * and so on. 
 *
 * @author Laurent Caillette
 */
public final class ResourceTools {

  private static final Log LOG = LogFactory.getLog( ResourceTools.class ) ;
  private static final ExecutorService EXECUTOR_SERVICE =
      Executors.newSingleThreadExecutor( ConfigurationTools.getExecutorThreadFactory() ) ;

  private ResourceTools() { }

  public static URL getResourceUrl( final Class owningClass, final String resourceName ) {
    final String fullName ;
    if( resourceName.startsWith( "/" ) ) {
      fullName = resourceName ;
    } else {
      fullName =
              "/" +
              ClassUtils. getPackageName( owningClass ). replace( '.', '/' ) +
              "/" +
              resourceName
          ;
    }
    return owningClass.getResource( fullName ) ;
  }

  public static byte[] readResource( final Class owningClass, final String resourceName ) {

    final URL url = getResourceUrl( owningClass, resourceName ) ;

    if( null == url ) {
      throw new MissingResourceException(
          owningClass.toString() + " key '" + resourceName + "'",
          owningClass.toString(),
          resourceName
      ) ;
    }

    LOG.info( "Loading resource '%s' from %s", url.toExternalForm(), owningClass ) ;
    final InputStream inputStream;
    try {
      inputStream = url.openStream();
    } catch( IOException e ) {
      throw new RuntimeException( "Should not happpen", e );
    }
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream() ;
    try {
      IOUtils.copy( inputStream, outputStream ) ;
    } catch( IOException e ) {
      throw new RuntimeException( "Should not happpen", e );
    }
    return outputStream.toByteArray() ;

  }


  /**
   * Copy a resource into given directory, creating subdirectories if resource name includes
   * a directory.
   */
  public static File copyResourceToDirectory(
      final Class owningClass,
      final ResourceName resourceName,
      final File destinationDir
  ) {
    return copyResourceToDirectory( owningClass, "/" + resourceName.getName(), destinationDir ) ;
  }


  /**
   * Copy a resource into given directory, creating subdirectories if resource name includes
   * a directory.
   */
  public static File copyResourceToDirectory(
      final Class owningClass,
      final String resourceName,
      final File destinationDir
  ) {
    final byte[] resourceBytes = readResource( owningClass, resourceName ) ;
    final ByteArrayInputStream inputStream =
        new ByteArrayInputStream( resourceBytes );

    final File expandedDestinationDir =
        new File( destinationDir, FilenameUtils.getPath( resourceName ) ) ;

    final File destinationFile = new File( destinationDir, resourceName ) ;
    final FileOutputStream fileOutputStream ;
    try {
      expandedDestinationDir.mkdirs() ;
      fileOutputStream = new FileOutputStream( destinationFile ) ;
    } catch( FileNotFoundException e ) {
      throw new RuntimeException( e );
    }
    try {
      IOUtils.copy( inputStream, fileOutputStream ) ;
    } catch( IOException e ) {
      throw new RuntimeException( e );
    }
    return destinationFile ;
  }

  /**
   * Copy a resource into given directory, creating no directory above target file.
   */
  public static File copyResourceToDirectoryFlat(
      final Class owningClass,
      final ResourceName resourceName,
      final File destinationDir
  ) {
    return copyResourceToDirectoryFlat( owningClass, "/" + resourceName.getName(), destinationDir ) ;
  }

  /**
   * Copy a resource into given directory, creating no directory above target file.
   */
  public static File copyResourceToDirectoryFlat(
      final Class owningClass,
      final String resourceName,
      final File destinationDir
  ) {
    final byte[] resourceBytes = readResource( owningClass, resourceName ) ;
    final ByteArrayInputStream inputStream =
        new ByteArrayInputStream( resourceBytes );

    final File destinationFile = new File( destinationDir, FilenameUtils.getName( resourceName ) ) ;
    final FileOutputStream fileOutputStream ;
    try {
      fileOutputStream = new FileOutputStream( destinationFile ) ;
    } catch( FileNotFoundException e ) {
      throw new RuntimeException( e );
    }
    try {
      IOUtils.copy( inputStream, fileOutputStream ) ;
    } catch( IOException e ) {
      throw new RuntimeException( e );
    }
    return destinationFile ;
  }

  public static File createDirectory( final File parent, final String name ) {
    final File directory = new File( parent, name ) ;
    if( ! directory.exists() ) {
      directory.mkdirs() ;
    }
    org.junit.Assert.assertTrue(
        "Could not create: '" + directory.getAbsolutePath() + "'",
        FileUtils.waitFor( directory, 1 )
    ) ;
    return directory ;
  }

    public static ProducerConfiguration createProducerConfiguration(
        final File contentDirectory,
        final ResourceLoader resourceLoader,
        final Charset renderingCharset
    ) {
      return new ProducerConfiguration() {

        public RenderingConfiguration getRenderingConfiguration() {
          return new RenderingConfiguration() {
            public ResourceLoader getResourceLoader() {
              return resourceLoader ;
            }
            public FopFactory getFopFactory() {
              return FopFactory.newInstance() ;
            }

            public FopFontStatus getCurrentFopFontStatus() {
              final Iterable<EmbedFontInfo> fontInfo = ImmutableList.of() ;
              final Map< String, EmbedFontInfo > failedFonts = ImmutableMap.of() ;
              return new FopFontStatus(
                  fontInfo,
                  failedFonts
              ) ;
            }
            public Charset getDefaultCharset() {
              return renderingCharset ;
            }

          } ;
        }

        public ContentConfiguration getContentConfiguration() {
          return new ContentConfiguration() {
            public File getContentRoot() {
              return contentDirectory;
            }
            public Charset getSourceCharset() {
              return DefaultCharset.SOURCE ;
            }
          } ;
        }

        public ExecutorService getExecutorService() {
          return EXECUTOR_SERVICE ;          
        }
      } ;

    }

  public static ExecutorService getExecutorService() {
    return EXECUTOR_SERVICE ;
  }

  public static ProducerConfiguration createProducerConfiguration(
        final File contentDirectory,
        final File styleDirectory,
        final boolean shouldAddClasspathResourceLoader,
        final Charset renderingCharset
    ) {
      Preconditions.checkNotNull( styleDirectory ) ;
      return doCreateProducerConfiguration(
          contentDirectory,
          styleDirectory,
          shouldAddClasspathResourceLoader,
          renderingCharset
      ) ;
    }

    public static ProducerConfiguration createProducerConfiguration(
        final File contentDirectory,
        final Charset renderingCharset
    ) {
      return doCreateProducerConfiguration(
          contentDirectory,
          null,
          true,
          renderingCharset
      ) ;
    }

    public static ProducerConfiguration doCreateProducerConfiguration(
        final File contentDirectory,
        final File styleDirectory,
        final boolean shouldAddClasspathResourceLoader,
        final Charset renderingCharset
    ) {
      final ResourceLoader resourceLoader ;
      final ResourceLoader customResourceLoader ;
      if( styleDirectory == null ) {
        resourceLoader = new ClasspathResourceLoader( ConfigurationTools.BUNDLED_STYLE_DIR ) ;
      } else {
        try {
          customResourceLoader = new UrlResourceLoader( styleDirectory.toURI().toURL() ) ;
        } catch( MalformedURLException e ) {
          throw new Error( e ) ;
        }
        if( shouldAddClasspathResourceLoader ) {
          resourceLoader = ResourceLoaderTools.compose(
              customResourceLoader,
              new ClasspathResourceLoader( ConfigurationTools.BUNDLED_STYLE_DIR )
          ) ;
        } else {
          resourceLoader = customResourceLoader ;
        }
      }
      return createProducerConfiguration( contentDirectory, resourceLoader, renderingCharset ) ;

    }

    public static DaemonConfiguration createDaemonConfiguration(
        final int httpDaemonPort,
        final File contentDirectory,
        final File styleDirectory,
        final Charset renderingCharset
    ) {
      final ProducerConfiguration producerConfiguration = createProducerConfiguration(
          contentDirectory,
          styleDirectory,
          false,
          renderingCharset
      ) ;

      return new DaemonConfiguration() {
        public int getPort() {
          return httpDaemonPort ;
        }
        public ProducerConfiguration getProducerConfiguration() {
          return producerConfiguration ;
        }

        public boolean getServeRemotes() {
          return true ;
        }
      } ;

    }

    public static DaemonConfiguration createDaemonConfiguration(
        final int httpDaemonPort,
        final File contentDirectory,
        final Charset renderingCharset
    ) {
      final ProducerConfiguration producerConfiguration = createProducerConfiguration(
          contentDirectory,
          renderingCharset
      ) ;

      return new DaemonConfiguration() {
        public int getPort() {
          return httpDaemonPort ;
        }
        public ProducerConfiguration getProducerConfiguration() {
          return producerConfiguration ;
        }

        public boolean getServeRemotes() {
          return true ;
        }
      } ;

    }

    public static DaemonConfiguration createDaemonConfiguration(
        final int httpDaemonPort,
        final File contentDirectory,
        final ResourceLoader resourceLoader
    ) {
      final ProducerConfiguration producerConfiguration = createProducerConfiguration(
          contentDirectory,
          resourceLoader,
          DefaultCharset.RENDERING
      ) ;

      return new DaemonConfiguration() {
        public int getPort() {
          return httpDaemonPort ;
        }
        public ProducerConfiguration getProducerConfiguration() {
          return producerConfiguration ;
        }

        public boolean getServeRemotes() {
          return true ;
        }
      } ;

    }
}
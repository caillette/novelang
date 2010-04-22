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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FopFactory;
import novelang.system.LogFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList;
import novelang.configuration.parse.DocumentGeneratorParameters;
import novelang.configuration.parse.DaemonParameters;
import novelang.configuration.parse.GenericParameters;
import novelang.configuration.parse.LevelExploderParameters;
import novelang.configuration.parse.BatchParameters;
import novelang.loader.ClasspathResourceLoader;
import novelang.loader.ResourceLoader;
import novelang.loader.ResourceLoaderTools;
import novelang.loader.UrlResourceLoader;
import novelang.produce.DocumentRequest;
import novelang.system.DefaultCharset;
import novelang.system.Log;

/**
 * Creates various Configuration objects from {@link GenericParameters}.
 * The main contract of this class is that for each created Configuration object, all information
 * needed is held by the Parameters object. No access to system property, no static variable, no
 * hidden state.
 * <p>
 * Each configured value comes from (in order):
 * <ol>
 *   <li>User-defined value (through some parameter).</li>
 *   <li>Fallback value (applies to a directory with known name).</li>
 *   <li>Default value (null or default number).</li>
 * </ol>
 * Each time parameters provide a value, log this origin at INFO level.
 *
 * @author Laurent Caillette
 */
public class ConfigurationTools {

  private static final Log LOG = LogFactory.getLog( ConfigurationTools.class ) ;

  public static final int DEFAULT_HTTP_DAEMON_PORT = 8080 ;
  public static final boolean DEFAULT_HTTP_DAEMON_SERVE_REMOTES = false ;
  public static final String DEFAULT_FONTS_DIRECTORY_NAME = "fonts" ;
  public static final String DEFAULT_HYPHENATION_DIRECTORY_NAME = "hyphenation" ;
  public static final String BUNDLED_STYLE_DIR = "style" ;
  private static final String DEFAULT_STYLE_DIR = "style" ;
  public static final String DEFAULT_OUTPUT_DIRECTORY_NAME = "output" ;
  public static final Charset DEFAULT_RENDERING_CHARSET = DefaultCharset.RENDERING ;


  private static final ThreadGroup EXECUTOR_THREAD_GROUP = new ThreadGroup( "Executor" ) ;

  private static final ThreadFactory EXECUTOR_THREAD_FACTORY = new ThreadFactory() {
    public Thread newThread( final Runnable runnable ) {
      final Thread thread = new Thread( EXECUTOR_THREAD_GROUP, runnable ) ;
      thread.setDaemon( true ) ;
      return thread ;
    }
  } ;

  public static ThreadFactory getExecutorThreadFactory() {
    return EXECUTOR_THREAD_FACTORY ;
  }

  private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool( 
      Runtime.getRuntime().availableProcessors(), EXECUTOR_THREAD_FACTORY ) ;

  public static ProducerConfiguration createProducerConfiguration( 
      final GenericParameters parameters 
  )
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

      public ExecutorService getExecutorService() {
        return EXECUTOR_SERVICE;
      }
    };
  }

  public static DaemonConfiguration createDaemonConfiguration( final DaemonParameters parameters )
      throws FOPException
  {

    final ProducerConfiguration producerConfiguration = createProducerConfiguration( parameters ) ;

    final int port ;
    final Integer customPort = parameters.getHttpDaemonPort() ;
    if( null == customPort ) {
      port = DEFAULT_HTTP_DAEMON_PORT ;
     LOG.info(
         "Got port number from default value [%s] (option not set: %s).",
         DEFAULT_HTTP_DAEMON_PORT,
         parameters.getHttpDaemonPortOptionDescription()
     ) ;
    } else {
      port = customPort ;
      LOG.info(
          "Got port number from custom value '%s' (from option: %s"  + ").",
          customPort,
          parameters.getHttpDaemonPortOptionDescription()
      ) ;
    }

    final boolean serveRemotes ;
    final Boolean serveRemotesAsBoolean = parameters.getServeRemotes() ;
    if( null == serveRemotesAsBoolean ) {
      serveRemotes = DEFAULT_HTTP_DAEMON_SERVE_REMOTES;
     LOG.info(
         "Got restriction to localhost from default value [%s] (option not set: %s).",
         DEFAULT_HTTP_DAEMON_SERVE_REMOTES,
         parameters.getHttpDaemonServeRemotesOptionDescription()
     ) ;
    } else {
      serveRemotes = serveRemotesAsBoolean ;
      LOG.info(
          "Got restriction to localhost from custom value '%s' (from option: %s"  + ").",
          customPort,
          parameters.getHttpDaemonServeRemotesOptionDescription()
      ) ;
    }

    return new DaemonConfiguration() {
      public int getPort() {
        return port ;
      }

      public boolean getServeRemotes() {
        return serveRemotes ;
      }

      public ProducerConfiguration getProducerConfiguration() {
        return producerConfiguration ;
      }
    } ;
  }

  public static LevelExploderConfiguration createExplodeLevelsConfiguration(
      final LevelExploderParameters parameters
  ) throws FOPException {
    final ProducerConfiguration producerConfiguration = createProducerConfiguration( parameters ) ;

    final File outputDirectory ;
    outputDirectory = extractOutputDirectory( parameters ) ;

    return new LevelExploderConfiguration() {
      public ProducerConfiguration getProducerConfiguration() {
        return producerConfiguration ;
      }
      public DocumentRequest getDocumentRequest() {
        return parameters.getDocumentRequest() ;
      }
      public File getOutputDirectory() {
        return outputDirectory ;
      }
    } ;

  }

  public static DocumentGeneratorConfiguration createDocumentGeneratorConfiguration(
      final DocumentGeneratorParameters parameters
  )
      throws FOPException, IllegalArgumentException
  {
    final ProducerConfiguration producerConfiguration = createProducerConfiguration( parameters ) ;

    final File outputDirectory ;
    outputDirectory = extractOutputDirectory( parameters ) ;

    return new DocumentGeneratorConfiguration() {
      public ProducerConfiguration getProducerConfiguration() {
        return producerConfiguration ;
      }
      public Iterable< DocumentRequest > getDocumentRequests() {
        return parameters.getDocumentRequests() ;
      }
      public File getOutputDirectory() {
        return outputDirectory ;
      }
    } ;

  }

  private static File extractOutputDirectory( final BatchParameters parameters ) {
    final File outputDirectory;
    if( null == parameters.getOutputDirectory() ) {
      outputDirectory = new File( parameters.getBaseDirectory(), DEFAULT_OUTPUT_DIRECTORY_NAME ) ;
     LOG.info(
         "Got output directory from default value '%s' (option not set: %s).",
         DEFAULT_OUTPUT_DIRECTORY_NAME,
         parameters.getOutputDirectoryOptionDescription()
     ) ;
    } else {
      outputDirectory = parameters.getOutputDirectory() ;
     LOG.info(
         "Got output directory from custom value '%s' (from option: %s).",
         outputDirectory,
         parameters.getOutputDirectoryOptionDescription()
     ) ;
    }
    return outputDirectory;
  }

  public static ContentConfiguration createContentConfiguration(
      final GenericParameters parameters
  ) {
    final Charset defaultSourceCharset ;
    {
      final Charset charset = parameters.getDefaultSourceCharset() ;
      if( null == charset ) {
        defaultSourceCharset = DefaultCharset.SOURCE ;
        LOG.info(
            "Default source charset is %s (from option %s).",
            defaultSourceCharset.name(),
            parameters.getDefaultSourceCharsetOptionDescription()
        ) ;
      } else {
        defaultSourceCharset = charset ;
        LOG.info( "Default source charset set as %s",
            defaultSourceCharset.name() ) ;
      }
    }
    
    final File contentRoot ;
    {
      if( null == parameters.getContentRoot() ) {
        contentRoot = parameters.getBaseDirectory() ;
        LOG.info( "Content root set as %s", contentRoot ) ;
      } else {
        contentRoot = parameters.getContentRoot() ;
        LOG.info( 
            "Content root is '%s' (from option '%s')", 
            contentRoot,
            parameters.getContentRootOptionDescription()
        ) ;
      }
    }

    return new ContentConfiguration() {
      public File getContentRoot() {
        return contentRoot ;
      }
      public Charset getSourceCharset() {
        return defaultSourceCharset ;
      }
    } ;
  }

  public static RenderingConfiguration createRenderingConfiguration( 
      final GenericParameters parameters 
  )
      throws FOPException
  {
    final Iterable< File > fontDirectories = findMultipleDirectoriesWithDefault(
        parameters.getBaseDirectory(),
        parameters.getFontDirectories(),
        "font directories",
        DEFAULT_FONTS_DIRECTORY_NAME,
        parameters.getFontDirectoriesOptionDescription() 
    ) ;

    final File hyphenationDirectory = findDefaultDirectoryIfNeeded(
        parameters.getBaseDirectory(),
        parameters.getHyphenationDirectory(),
        "hyphenation directory",
        parameters.getHyphenationDirectoryOptionDescription(),
        DEFAULT_HYPHENATION_DIRECTORY_NAME
    ) ;

    final FopFactory fopFactory = FopTools
        .createFopFactory( fontDirectories, hyphenationDirectory ) ;

    final ResourceLoader resourceLoader = createResourceLoader( parameters ) ;

    final Charset defaultRenderingCharset ;
    {
      final Charset charset = parameters.getDefaultRenderingCharset() ;
      if( null == charset ) {
        defaultRenderingCharset = DefaultCharset.RENDERING ;
        LOG.info(
            "Default rendering charset is %s (from option %s).",
            defaultRenderingCharset.name(),
            parameters.getDefaultRenderingCharsetOptionDescription()
        ) ;
      } else {
        defaultRenderingCharset = charset ;
        LOG.info( "Default rendering charset set as %s",
            defaultRenderingCharset.name() ) ;
      }
    }

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

      public Charset getDefaultCharset() {
        return defaultRenderingCharset ;
      }
    } ;

  }

  private static Iterable<File> findMultipleDirectoriesWithDefault( 
      final File baseDirectory, 
      final Iterable<File> userFontDirectories, 
      final String directoriesName, 
      final String defaultSingleDirectoryName, 
      final String directoriesOptionDescription 
  ) {
    final Iterable<File> fontDirectories;
    if( userFontDirectories.iterator().hasNext() ) {
      fontDirectories = userFontDirectories ;
      LOG.info(
          "Got font directories from custom value '%s" + "' (from option: %s).",
          fontDirectories,
          directoriesOptionDescription
      ) ;
    } else {
      final File maybeDefaultDirectory = findDefaultDirectoryIfNeeded(
          baseDirectory,
          null,
          directoriesName,
          directoriesOptionDescription,
          defaultSingleDirectoryName
      ) ;

      if( null == maybeDefaultDirectory ) {
        fontDirectories = ImmutableList.of() ;
      } else {
        fontDirectories = Lists.newArrayList( maybeDefaultDirectory ) ;
      }
    }
    return fontDirectories;
  }

  public static ResourceLoader createResourceLoader( final GenericParameters parameters ) {
    final Iterable< File > userDefinedDirectories = findMultipleDirectoriesWithDefault(
        parameters.getBaseDirectory(),
        parameters.getStyleDirectories(),
        "style directories",
        DEFAULT_STYLE_DIR,
        parameters.getStyleDirectoriesDescription() 
    ) ;
    return createResourceLoader( userDefinedDirectories ) ;
  }

  public static ResourceLoader createResourceLoader(
      final File contentDirectory,
      final File userDefinedStyleDirectory,
      final String description
  ) {
    // Let's wreck the Ant task for a moment!
    throw new UnsupportedOperationException( "createResourceLoader" ) ;
  }
  
  public static ResourceLoader createResourceLoader( 
      final Iterable< File > userDefinedDirectories
  ) {
    final ResourceLoader classpathResourceLoader = 
        new ClasspathResourceLoader( BUNDLED_STYLE_DIR ) ;
    final Iterator< File > userDefinedDirectoryIterator = userDefinedDirectories.iterator() ;
    
    if( userDefinedDirectoryIterator.hasNext() ) {
      ResourceLoader resultingResourceLoader = classpathResourceLoader ;
      
      while ( userDefinedDirectoryIterator.hasNext() ) {
        final File userStyleDirectory = userDefinedDirectoryIterator.next() ;
        final URL userStyleUrl ;
        try {
          userStyleUrl = userStyleDirectory.toURI().toURL();
        } catch( MalformedURLException e ) {
          throw new RuntimeException( e );
        }
        resultingResourceLoader = ResourceLoaderTools.compose( 
            new UrlResourceLoader( userStyleUrl ), 
            resultingResourceLoader 
        ) ;
      }

      return resultingResourceLoader ;
      
    } else {
      return classpathResourceLoader ;
    }
    
    
  }

  protected static File findDefaultDirectoryIfNeeded(
      final File baseDirectory,
      final File userDefinedDirectory,
      final String topic,
      final String directoryDescription,
      final String defaultDirectoryName
  ) {
    if( null == userDefinedDirectory ) {
      final File defaultDirectory = new File( baseDirectory, defaultDirectoryName ) ;
      if( defaultDirectory.exists() ) {
        LOG.info(
            "Got %s from default value '%s' (option not set: %s).",
            topic,
            defaultDirectory.getAbsolutePath(),
            directoryDescription
        ) ;
        return defaultDirectory ;
      } else {
        LOG.info(
            "Got no %s (no default directory '%s' nor was set this option: %s).",
            topic,
            defaultDirectoryName,
            directoryDescription
        ) ;

        return null ;
      }
    } else {
      LOG.info(
          "Got %s from user value '%s' (from option: %s).",
          topic,
          userDefinedDirectory,
          directoryDescription
      ) ;
      return userDefinedDirectory ;
    }
  }


}

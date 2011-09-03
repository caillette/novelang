/*
 * Copyright (C) 2011 Laurent Caillette
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
package org.novelang.build.batch;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FopFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.novelang.common.FileTools;
import org.novelang.configuration.ConfigurationTools;
import org.novelang.configuration.ContentConfiguration;
import org.novelang.configuration.FopFontStatus;
import org.novelang.configuration.FopTools;
import org.novelang.configuration.ProducerConfiguration;
import org.novelang.configuration.RenderingConfiguration;
import org.novelang.configuration.RenditionKinematic;
import org.novelang.logger.ConcreteLoggerFactory;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.DefaultCharset;
import org.novelang.outfit.loader.ClasspathResourceLoader;
import org.novelang.outfit.loader.CompositeResourceLoader;
import org.novelang.outfit.loader.ResourceLoader;
import org.novelang.outfit.loader.UrlResourceLoader;
import org.novelang.produce.DocumentProducer;
import org.novelang.produce.DocumentRequest;
import org.novelang.produce.GenericRequest;
import org.novelang.produce.MalformedRequestException;
import org.novelang.produce.StreamDirector;

/**
 * Starts a {@link org.novelang.batch.DocumentGenerator} inside Maven's JVM.
 *
 * Running the {@link org.novelang.produce.DocumentProducer} in the same JVM doesn't work because of
 * <a href="http://forums.sun.com/thread.jspa?threadID=5134880">some Xalan bug</a>
 * which wrecks XSL-based document rendering.
 *
 * TODO: remove duplicated logic. Involves finding a decent API.
 *
 * @goal produceNoFork
 * @requiresDependencyResolution runtime
 * @threadSafe
 *
 * @author Laurent Caillette
 */
@SuppressWarnings( { "UnusedDeclaration" } )
public class NoForkBatchProducerMojo extends AbstractProducerMojo {


  /**
   * Base directory for rendered documents.
   *
   * @parameter expression="${produce.outputDirectory}"
   * @required
   */
  private File outputDirectory = null ;

  /**
   * Working directory, which serves as reference for default directories like style or fonts.
   *
   * @parameter expression="${produce.workingDirectory}"
   * @required
   */
  private File workingDirectory = null ;


  /**
   * List of documents to render.
   *
   * @parameter
   * @required
   */
  private List< String > documentsToRender = null ;


  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {

    ConcreteLoggerFactory.setMojoLog( getLog() ) ;
    LoggerFactory.configurationComplete() ;

    try {
      FileTools.createFreshDirectory( outputDirectory ) ;
    } catch( IOException e ) {
      throw new MojoExecutionException( "Could not create/delete output directory", e ) ;
    }

    final ProducerConfiguration producerConfiguration = createProducerConfiguration() ;

    final DocumentProducer producer = new DocumentProducer( producerConfiguration ) ;
    for( final String requestAsString : documentsToRender ) {
      final DocumentRequest documentRequest ;
      try {
        documentRequest = ( DocumentRequest )
            GenericRequest.parse( requestAsString ) ;
      } catch( MalformedRequestException e ) {
        throw new MojoExecutionException( "Bad documentRequest: '" + requestAsString + "'", e ) ;
      }
      final StreamDirector streamDirector =
          StreamDirector.forDirectory( documentRequest, outputDirectory ) ;
      try {
        producer.produce(
            documentRequest,
            producer.createRenderable( documentRequest ),
            streamDirector
        ) ;
      } catch( Exception e ) {
        throw new MojoExecutionException(
            "Failed to produce document for '" + requestAsString + "'", e ) ;
      }
    }
  }

  private ProducerConfiguration createProducerConfiguration() throws MojoExecutionException {
    final ResourceLoader resourceLoader ;
    final File styleDirectory = new File( workingDirectory, ConfigurationTools.DEFAULT_STYLE_DIR ) ;
    final ClasspathResourceLoader classpathResourceLoader =
        new ClasspathResourceLoader( ConfigurationTools.BUNDLED_STYLE_DIR ) ;

    if( styleDirectory.exists() ) {
      final UrlResourceLoader urlResourceLoader;
      try {
        urlResourceLoader = new UrlResourceLoader( styleDirectory.toURI().toURL() );
      } catch( MalformedURLException e ) {
        throw new MojoExecutionException( "Couldn't parse '" + styleDirectory +"'", e ) ;
      }
      resourceLoader = new CompositeResourceLoader( urlResourceLoader, classpathResourceLoader ) ;
    } else {
      resourceLoader = classpathResourceLoader;
    }

    final FopFactory fopFactory;
    try {
      fopFactory = FopTools.createFopFactory( null, null ) ;
    } catch( FOPException e ) {
      throw new MojoExecutionException( "Problem when configuring FOP", e ) ;
    }

    final RenderingConfiguration renderingConfiguration = new RenderingConfiguration() {
      @Override
      public ResourceLoader getResourceLoader() {
        return resourceLoader ;
      }

      @Override
      public FopFactory getFopFactory() {
        return fopFactory ;
      }

      @Override
      public FopFontStatus getCurrentFopFontStatus() {
        return null;
      }

      @Override
      public Charset getDefaultCharset() {
        return DefaultCharset.RENDERING ;
      }

      @Override
      public RenditionKinematic getRenderingKinematic() {
        return RenditionKinematic.BATCH ;
      }
    } ;

    final ContentConfiguration contentConfiguration = new ContentConfiguration() {
      @Override
      public File getContentRoot() {
        return workingDirectory ;
      }

      @Override
      public Charset getSourceCharset() {
        return DefaultCharset.SOURCE ;
      }
    } ;

    final ExecutorService executorService = Executors.newSingleThreadExecutor(
        ConfigurationTools.getExecutorThreadFactory() ) ;

    final ProducerConfiguration producerConfiguration = new ProducerConfiguration() {
      @Override
      public RenderingConfiguration getRenderingConfiguration() {
        return renderingConfiguration ;
      }

      @Override
      public ContentConfiguration getContentConfiguration() {
        return contentConfiguration ;
      }

      @Override
      public ExecutorService getExecutorService() {
        return executorService ;
      }
    } ;
    return producerConfiguration;
  }

}
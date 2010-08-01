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
package novelang.build.batch;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import novelang.batch.AbstractDocumentGenerator;
import novelang.batch.DocumentGenerator;
import novelang.common.Problem;
import novelang.configuration.ConfigurationTools;
import novelang.configuration.DocumentGeneratorConfiguration;
import novelang.configuration.parse.ArgumentException;
import novelang.configuration.parse.DocumentGeneratorParameters;
import novelang.nhovestone.driver.DocumentGeneratorDriver;
import novelang.nhovestone.driver.ProcessDriver;
import novelang.produce.DocumentProducer;
import novelang.system.Husk;
import novelang.system.Log;
import novelang.system.LogFactory;
import org.apache.fop.apps.FOPException;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Starts a {@link novelang.batch.DocumentGenerator} through a
 * {@link novelang.nhovestone.driver.DocumentGeneratorDriver}.
 *
 * Running the {@link DocumentProducer} in the same JVM doesn't work because of
 * <a href="http://forums.sun.com/thread.jspa?threadID=5134880">some Xalan bug</a>
 * which wrecks XSL-based document rendering.
 *
 * @goal produce
 *
 * @author Laurent Caillette
 */
@SuppressWarnings( { "UnusedDeclaration" } )
public class BatchProducerMojo extends AbstractMojo {

  /**
   * The classpath for the standalone JVM.
   *
   * @parameter expression="${produce.jvmClasspath}"
//   * @required
   */
//  private String jvmClasspath = null ;

  /**
   * The heap size in megabytes for the standalone JVM.
   *
   * @parameter expression="${produce.jvmHeapSizeMegabytes}"
   */
  @SuppressWarnings( { "FieldCanBeLocal" } )
  private Integer jvmHeapSizeMegabytes = 512 ;

  /**
   * Base directory for documents sources.
   *
   * @parameter expression="${produce.contentRootDirectory}"
   * @required
   */
  private File contentRootDirectory = null ;

  /**
   * Base directory for rendered documents.
   *
   * @parameter expression="${produce.outputDirectory}"
   * @required
   */
  private File outputDirectory = null ;

  /**
   * Directory where to write log file(s) to.
   *
   * @parameter expression="${produce.logDirectory}"
   */
  private File logDirectory = null ;

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
  

  /**
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  private MavenProject project ;


  public void execute() throws MojoExecutionException, MojoFailureException {
    LogFactory.setMavenPluginLog( getLog() ) ;

    final Log log = LogFactory.getLog( getClass() ) ;

    final List< String > classpathElements;
    try {
      classpathElements = project.getRuntimeClasspathElements();
    } catch( DependencyResolutionRequiredException e ) {
      throw new MojoExecutionException( "Something got wrong", e ) ;
    }
/*
    for( final String classpathElement : classpathElements ) {
      log.info( "Classpath element %s", classpathElement ) ;
    }
*/

    DocumentGeneratorDriver.Configuration configuration =
        Husk.create( DocumentGeneratorDriver.Configuration.class )
        .withAbsoluteClasspath( Joiner.on( File.pathSeparator ).join( classpathElements ) )
        .withContentRootDirectory( contentRootDirectory )
        .withOutputDirectory( outputDirectory )
        .withWorkingDirectory( workingDirectory )
    ;

    if( logDirectory != null ) {
      configuration = configuration.withLogDirectory( logDirectory ) ;
    }
    if( jvmHeapSizeMegabytes != null ) {
      configuration = configuration.withJvmHeapSizeMegabytes( jvmHeapSizeMegabytes ) ;
    }

    final DocumentGeneratorDriver driver = new DocumentGeneratorDriver( configuration ) ;
    try {
      driver.start( 1L, TimeUnit.MINUTES ) ;
      driver.shutdown( false ) ;
    } catch( Exception e ) {
      throw new MojoExecutionException( "Driver execution failed", e );
    }
  }
}
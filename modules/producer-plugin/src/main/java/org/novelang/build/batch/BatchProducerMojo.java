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
package org.novelang.build.batch;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.novelang.Version;
import org.novelang.logger.ConcreteLoggerFactory;
import org.novelang.nhovestone.driver.DocumentGeneratorDriver;
import org.novelang.nhovestone.driver.EngineDriver;
import org.novelang.produce.DocumentProducer;
import org.novelang.outfit.Husk;
import org.novelang.outfit.shell.JavaClasses;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Starts a {@link org.novelang.batch.DocumentGenerator} through a
 * {@link org.novelang.nhovestone.driver.DocumentGeneratorDriver}.
 *
 * Running the {@link DocumentProducer} in the same JVM doesn't work because of
 * <a href="http://forums.sun.com/thread.jspa?threadID=5134880">some Xalan bug</a>
 * which wrecks XSL-based document rendering.
 *
 * @goal produce
 * @requiresDependencyResolution runtime
 * 
 * @author Laurent Caillette
 */
@SuppressWarnings( { "UnusedDeclaration" } )
public class BatchProducerMojo extends AbstractProducerMojo {

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

    final List< String > classpathElements;
    try {
      classpathElements = project.getRuntimeClasspathElements() ;
    } catch( DependencyResolutionRequiredException e ) {
      throw new MojoExecutionException( "Something got wrong", e ) ;
    }
/*
    for( final String classpathElement : classpathElements ) {
      log.info( "%s - Classpath element: %s", getClass().getSimpleName(), classpathElement ) ;
    }
*/
    final Version version = getVersion() ;

    final String[] documentNames = documentsToRender.toArray(
        new String[ documentsToRender.size() ] ) ;

    DocumentGeneratorDriver.Configuration configuration =
        Husk.create( DocumentGeneratorDriver.Configuration.class )
        .withJavaClasses( new JavaClasses.ClasspathAndMain(
            EngineDriver.NOVELANG_BOOTSTRAP_MAIN_CLASS_NAME, classpathElements ) )
        .withContentRootDirectory( contentRootDirectory )
        .withOutputDirectory( outputDirectory )
        .withWorkingDirectory( workingDirectory )
        .withProgramArguments( documentNames )
        .withVersion( version )
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
      final int exitCode = driver.shutdown( false );
      if( exitCode != 0 ) {
        throw new Exception( "Process ended with exit code " + exitCode ) ;
      }
    } catch( Exception e ) {
      throw new MojoExecutionException( "Driver execution failed", e );
    }
  }

}
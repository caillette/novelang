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

import com.google.common.collect.Lists;
import novelang.batch.AbstractDocumentGenerator;
import novelang.batch.DocumentGenerator;
import novelang.common.Problem;
import novelang.configuration.ConfigurationTools;
import novelang.configuration.DocumentGeneratorConfiguration;
import novelang.configuration.parse.ArgumentException;
import novelang.configuration.parse.DocumentGeneratorParameters;
import novelang.produce.DocumentProducer;
import novelang.system.LogFactory;
import org.apache.fop.apps.FOPException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Doesn't work, probably because of
 * <a href="http://forums.sun.com/thread.jspa?threadID=5134880">some Xalan bug</a>.
 * Generates Java source containing lexemes extracted from an ANTLR grammar.
 *
 * @goal produce
 *
 * @author Laurent Caillette
 */
@SuppressWarnings( { "UnusedDeclaration" } )
public class BatchProducerMojo extends AbstractMojo {

  /**
   * Base directory for documents.
   *
   * @parameter expression="${produce.baseDirectory}"
   * @required
   */
  @SuppressWarnings( { "InstanceVariableMayNotBeInitialized" } )
  private File baseDirectory ;

  /**
   * Raw parameters.
   * TODO: use Mojo properties.
   *
   * @parameter expression="${produce.commandLineArguments}"
   */
  @SuppressWarnings( { "InstanceVariableMayNotBeInitialized" } )
  private String[] commandLineArguments ;

  public void execute() throws MojoExecutionException, MojoFailureException {
    LogFactory.setMavenPluginLog( getLog() ) ;

    // Code more or less copy-pasted from DocumentGenerator. TODO: refactor both.

    final List< Problem > allProblems = Lists.newArrayList() ;

    try {
      final DocumentGeneratorParameters parameters = new DocumentGeneratorParameters(
          baseDirectory,
          commandLineArguments
      ) ;
      final DocumentGeneratorConfiguration configuration =
          ConfigurationTools.createDocumentGeneratorConfiguration( parameters ) ;
      final File outputDirectory = configuration.getOutputDirectory();
      AbstractDocumentGenerator.resetTargetDirectory( outputDirectory ) ;


      final DocumentProducer documentProducer =
          new DocumentProducer( configuration.getProducerConfiguration() ) ;
      DocumentGenerator.processDocumentRequests(
          configuration,
          outputDirectory,
          documentProducer,
          allProblems
      ) ;

    } catch( ArgumentException e ) {
      throw new MojoExecutionException( "Incorrect arguments", e ) ;
    } catch( FOPException e ) {
      throw new MojoExecutionException( "Could not configure FOP", e ) ;
    } catch( IOException e ) {
      throw new MojoExecutionException( "IO problem", e ) ;
    } catch( Exception e ) {
      getLog().error( e ) ;
      throw new MojoExecutionException( "Something bad happened", e );
    }

    if( ! allProblems.isEmpty() ) {
      throw new MojoExecutionException( "Generation problem: " + allProblems.toString() ) ;
    }

  }
}
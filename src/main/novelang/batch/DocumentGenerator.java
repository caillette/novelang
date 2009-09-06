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

package novelang.batch;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import novelang.system.LogFactory;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import novelang.common.Problem;
import novelang.configuration.DocumentGeneratorConfiguration;
import novelang.configuration.ConfigurationTools;
import novelang.configuration.parse.ArgumentException;
import novelang.configuration.parse.DocumentGeneratorParameters;
import novelang.produce.DocumentProducer;
import novelang.produce.DocumentRequest;
import novelang.system.StartupTools;
import novelang.system.Log;

/**
 * The main class for running document generation in command-line mode.
 *
 * The {@code Logger} instance is NOT held in statically-initialized final field as it would
 * trigger premature initialization, we want a call to {@link StartupTools} to happen first.
 *
 * @author Laurent Caillette
 */
public class DocumentGenerator extends AbstractDocumentGenerator< DocumentGeneratorParameters > {

  private static final Log LOG = LogFactory.getLog( DocumentGenerator.class ) ;
  public static final String COMMAND_NAME = "generate";

  public void main(
      String commandName,
      boolean mayTerminateJvm,
      String[] arguments,
      File baseDirectory
  ) throws Exception {

    final DocumentGeneratorParameters parameters =
        createParametersOrExit( commandName, mayTerminateJvm, arguments, baseDirectory ) ;

    try {
      LOG.info(
          "Starting %s with arguments %s",
          getClass().getSimpleName(),
          asString( arguments )
      ) ;

      final DocumentGeneratorConfiguration configuration =
          ConfigurationTools.createDocumentGeneratorConfiguration( parameters ) ;
      final File outputDirectory = configuration.getOutputDirectory();
      resetTargetDirectory( outputDirectory ) ;
      final DocumentProducer documentProducer =
          new DocumentProducer( configuration.getProducerConfiguration() ) ;
      final List< Problem > allProblems = Lists.newArrayList() ;

      processDocumentRequests( configuration, outputDirectory, documentProducer, allProblems ) ;

      if( ! allProblems.isEmpty() ) {
        reportProblems( outputDirectory, allProblems ) ;
        System.err.println(
            "There were problems. See " + outputDirectory + "/" + PROBLEMS_FILENAME ) ;
      }

    } catch( Exception e ) {
      LOG.error( "Fatal", e ) ;
      throw e ;
    }
  }

  protected DocumentGeneratorParameters createParameters(
      final String[] arguments,
      final File baseDirectory
  ) throws ArgumentException {
    return new DocumentGeneratorParameters(
        baseDirectory,
        arguments
    );
  }

  protected void processDocumentRequests(
      final DocumentGeneratorConfiguration configuration,
      final File outputDirectory,
      final DocumentProducer documentProducer,
      final List< Problem > allProblems 
  ) throws Exception {
    for( final DocumentRequest documentRequest : configuration.getDocumentRequests() ) {
      Iterables.addAll(
          allProblems,
          processDocumentRequest(
              documentRequest,
              outputDirectory,
              documentProducer
          )
      ) ;
    }
  }

  protected String getSpecificCommandLineParametersDescriptor() {
    return " [OPTIONS] document1 [document2...]";
  }


  private static Iterable< Problem > processDocumentRequest(
      DocumentRequest documentRequest,
      File targetDirectory,
      DocumentProducer documentProducer
  ) throws Exception {
    final File outputFile = createOutputFile(
        targetDirectory,
        documentRequest
    ) ;
    LOG.info( "Generating document file '%s'...", outputFile.getAbsolutePath() ) ;
    final FileOutputStream outputStream = new FileOutputStream( outputFile ) ;
    final Iterable< Problem > problems = documentProducer.produce( documentRequest, outputStream ) ;
    outputStream.flush() ;
    outputStream.close() ;
    return problems ;
  }


}

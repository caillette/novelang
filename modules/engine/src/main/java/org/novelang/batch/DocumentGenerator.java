/*
 * Copyright (C) 2010 Laurent Caillette
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

package org.novelang.batch;

import java.io.File;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.lang.SystemUtils;
import org.novelang.common.Problem;
import org.novelang.configuration.ConfigurationTools;
import org.novelang.configuration.DocumentGeneratorConfiguration;
import org.novelang.configuration.parse.ArgumentException;
import org.novelang.configuration.parse.DocumentGeneratorParameters;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.produce.DocumentProducer;
import org.novelang.produce.DocumentRequest;
import org.novelang.produce.StreamDirector;

/**
 * The main class for running document generation in command-line mode.
 *
 * The {@code Logger} instance is NOT held in statically-initialized final field as it would
 * trigger premature initialization, we want a call to {@link org.novelang.outfit.LogbackConfigurationTools} to happen first.
 *
 * @author Laurent Caillette
 */
public class DocumentGenerator extends AbstractDocumentGenerator< DocumentGeneratorParameters > {

  private static final Logger LOGGER = LoggerFactory.getLogger( DocumentGenerator.class );

  public static final String COMMAND_NAME = "generate";

  @Override
  public void main(
      final DocumentGeneratorParameters parameters
  ) throws Exception {

    final DocumentGeneratorConfiguration configuration =
        ConfigurationTools.createDocumentGeneratorConfiguration( parameters ) ;
    final File outputDirectory = configuration.getOutputDirectory();
    resetTargetDirectory( outputDirectory ) ;
    final DocumentProducer documentProducer =
        new DocumentProducer( configuration.getProducerConfiguration() ) ;
    final List< Problem > allProblems = Lists.newArrayList() ;

    processDocumentRequests(
        configuration,
        outputDirectory,
        documentProducer,
        allProblems
    ) ;

    if( ! allProblems.isEmpty() ) {
      reportProblems( outputDirectory, allProblems ) ;
      //noinspection UseOfSystemOutOrSystemErr
      System.err.println(
          "There were problems. See " + outputDirectory + "/" + PROBLEMS_FILENAME ) ;
      throw new GenerationFailedException( allProblems ) ;
    }

  }

  public static DocumentGeneratorParameters createParameters(
      final String[] arguments,
      final File baseDirectory
  ) throws ArgumentException {
    return new DocumentGeneratorParameters(
        baseDirectory,
        arguments
    ) ;
  }

  public static DocumentGeneratorParameters createParameters(
      final String[] arguments
  ) throws ArgumentException {
    return createParameters( arguments, new File( SystemUtils.USER_DIR ) ) ;
  }

  public static void processDocumentRequests(
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

  public static String getSpecificCommandLineParametersDescriptor() {
    return " [OPTIONS] document1 [document2...]";
  }


  private static Iterable< Problem > processDocumentRequest(
      final DocumentRequest documentRequest,
      final File targetDirectory,
      final DocumentProducer documentProducer
  ) throws Exception {
    final Iterable< Problem > problems = documentProducer.produce(
        documentRequest,
        documentProducer.createRenderable( documentRequest ),
        StreamDirector.forDirectory( documentRequest, targetDirectory )
    ) ;
    return problems ;
  }


}

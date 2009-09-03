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
import java.util.List;

import com.google.common.collect.Lists;
import novelang.common.Problem;
import novelang.configuration.ConfigurationTools;
import novelang.configuration.SplitByChapterConfiguration;
import novelang.configuration.parse.ArgumentException;
import novelang.configuration.parse.SplitByChapterParameters;
import novelang.produce.DocumentProducer;
import novelang.system.Log;
import novelang.system.LogFactory;

/**
 * @author Laurent Caillette
 */
public class SplitByChapter extends AbstractDocumentGenerator< SplitByChapterParameters > {

  private static final Log LOG = LogFactory.getLog( SplitByChapter.class ) ;

  public void main(
      String commandName,
      String[] arguments,
      File baseDirectory
  ) throws Exception {

    final SplitByChapterParameters parameters ;

    parameters = createParametersOrExit( commandName, arguments, baseDirectory ) ;

    try {
      LOG.info(
          "Starting %s with arguments %s",
          getClass().getSimpleName(),
          asString( arguments )
      ) ;

      final SplitByChapterConfiguration configuration =
          ConfigurationTools.createDocumentGeneratorConfiguration( parameters ); ;
      final File outputDirectory = configuration.getOutputDirectory();
      resetTargetDirectory( outputDirectory ) ;
      final DocumentProducer documentProducer =
          new DocumentProducer( configuration.getProducerConfiguration() ) ;
      final List< Problem > allProblems = Lists.newArrayList() ;

      processDocumentRequest( configuration, outputDirectory, documentProducer, allProblems ) ;

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

  private void processDocumentRequest(
      final SplitByChapterConfiguration configuration,
      final File outputDirectory,
      final DocumentProducer documentProducer,
      final List< Problem > allProblems
  ) {
    throw new UnsupportedOperationException( "processDocumentRequest" ) ;
  }

  protected SplitByChapterParameters createParameters(
      final String[] arguments,
      final File baseDirectory
  ) throws ArgumentException {
    return new SplitByChapterParameters(
        baseDirectory,
        arguments
    );
  }

  protected String getSpecificCommandLineParametersDescriptor() {
    return " [OPTIONS] document-to-split.nlp";
  }

}

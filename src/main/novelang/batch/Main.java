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
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import novelang.configuration.ConfigurationTools;
import novelang.configuration.BatchConfiguration;
import novelang.configuration.parse.ArgumentException;
import novelang.configuration.parse.BatchParameters;
import novelang.common.Problem;
import novelang.produce.DocumentProducer;
import novelang.produce.DocumentRequest;
import novelang.rendering.HtmlProblemPrinter;
import novelang.system.StartupTools;
import novelang.system.EnvironmentTools;

/**
 * The main class for running in command-line mode.
 *
 * The {@code Logger} instance is NOT held in statically-initialized final field as it would
 * trigger premature initialization, we want a call to {@link StartupTools} to happen first.
 *
 * @author Laurent Caillette
 */
public class Main {

  private static final String PROBLEMS_FILENAME = "problems.html";

  public static void main( String[] arguments ) throws Exception {
    StartupTools.fixLogDirectory( arguments ) ;
    EnvironmentTools.logSystemProperties() ;
    final File baseDirectory = new File( SystemUtils.USER_DIR ) ;
    main( baseDirectory, arguments ) ;
  }

  public static void main( File baseDirectory, String[] arguments ) throws Exception {
    final Logger LOGGER = LoggerFactory.getLogger( Main.class ) ;

    try {
      final BatchParameters parameters = new BatchParameters(
          baseDirectory,
          arguments
      ) ;

      LOGGER.debug( "Starting {} with arguments {}",
          ClassUtils.getShortClassName( Main.class ), asString( arguments ) ) ;

      final BatchConfiguration configuration =
          ConfigurationTools.createBatchConfiguration( parameters ); ;
      final File outputDirectory = configuration.getOutputDirectory();
      resetTargetDirectory( LOGGER, outputDirectory ) ;
      final DocumentProducer documentProducer =
          new DocumentProducer( configuration.getProducerConfiguration() ) ;
      final List< Problem > allProblems = Lists.newArrayList() ;

      for( final DocumentRequest documentRequest : configuration.getDocumentRequests() ) {
        Iterables.addAll(
            allProblems,
            processDocumentRequest(
                LOGGER,
                documentRequest,
                outputDirectory,
                documentProducer
            )
        ) ;
      }
      if( ! allProblems.isEmpty() ) {
        reportProblems( outputDirectory, allProblems ) ;
        System.err.println(
            "There were problems. See " + outputDirectory + "/" + PROBLEMS_FILENAME ) ;
      }

    } catch( ArgumentException e ) {
      if( e.isHelpRequested() ) {
        printHelpOnConsole( e ) ;
        System.exit( -1 ) ;
      } else {
        LOGGER.error( "Parameters exception, printing help and exiting.", e ) ;
        printHelpOnConsole( e ) ;
        System.exit( -2 ) ;
      }
    } catch( Exception e ) {
      LOGGER.error( "Fatal", e ) ;
      throw e ;
    }
  }

  private static void printHelpOnConsole( ArgumentException e ) {
    e.getHelpPrinter().print(
        System.out,
        ClassUtils.getShortClassName( Main.class ) + " [OPTIONS] document1 [document2...]", 
        80
    ) ;
  }

  // TODO does ToStringBuilder save from doing this?
  private static String asString( String[] args ) {
    final StringBuffer buffer = new StringBuffer( "[" ) ;
    boolean first = true ;
    for( int i = 0 ; i < args.length ; i++ ) {
      final String arg = args[ i ] ;
      if( first ) {
        first = false ;
      } else {
        buffer.append( "," ) ;
      }
      buffer.append( arg ) ;
    }
    buffer.append( "]" ) ;
    return buffer.toString() ;
  }

  private static void reportProblems(
      File targetDirectory,
      Iterable< Problem > allProblems
  ) throws IOException {
    final File problemFile = new File( targetDirectory, PROBLEMS_FILENAME ) ;
    final OutputStream outputStream = new FileOutputStream( problemFile ) ;
    // TODO: use some text-oriented thing to avoid meaningless links inside generated file.
    final HtmlProblemPrinter problemPrinter = new HtmlProblemPrinter() ;
    problemPrinter.printProblems( outputStream, allProblems, "<not available>" ) ;
    outputStream.flush() ;
    outputStream.close() ;
  }

  private static Iterable< Problem > processDocumentRequest(
      Logger logger,
      DocumentRequest documentRequest,
      File targetDirectory,
      DocumentProducer documentProducer
  ) throws IOException {
    final File outputFile = createOutputFile(
        logger,
        targetDirectory,
        documentRequest
    ) ;
    logger.info( "Generating document file '{}'...", outputFile.getAbsolutePath() ) ;
    final FileOutputStream outputStream = new FileOutputStream( outputFile ) ;
    final Iterable< Problem > problems = documentProducer.produce( documentRequest, outputStream ) ;
    outputStream.flush() ;
    outputStream.close() ;
    return problems ;
  }

  private static void resetTargetDirectory(
      Logger logger,
      File targetDirectory
  ) throws IOException {
    if( targetDirectory.exists() ) {
      logger.info( "Deleting '{}'...", targetDirectory.getAbsolutePath() ) ;
    }
    FileUtils.deleteDirectory( targetDirectory ) ;
    FileUtils.forceMkdir( targetDirectory ) ;
    logger.info( "Created '{}'...", targetDirectory.getAbsolutePath() ) ;
  }

  public static File createOutputFile(
      Logger logger,
      File targetDir,
      DocumentRequest documentRequest
  )
      throws IOException
  {
    final String relativeFileName = documentRequest.getDocumentSourceName() +
        "." + documentRequest.getRenditionMimeType().getFileExtension() ;
    final File outputFile =  new File( targetDir, relativeFileName ) ;
    logger.debug( "Resolved output file name '{}'", outputFile.getAbsolutePath() ) ;
    FileUtils.forceMkdir( outputFile.getParentFile() );
    return outputFile ;
  }



}

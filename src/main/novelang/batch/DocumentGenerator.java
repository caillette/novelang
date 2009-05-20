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
import novelang.system.LogFactory;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import novelang.common.Problem;
import novelang.configuration.BatchConfiguration;
import novelang.configuration.ConfigurationTools;
import novelang.configuration.parse.ArgumentException;
import novelang.configuration.parse.BatchParameters;
import novelang.produce.DocumentProducer;
import novelang.produce.DocumentRequest;
import novelang.rendering.HtmlProblemPrinter;
import novelang.system.StartupTools;
import novelang.system.Log;

/**
 * The main class for running in command-line mode.
 *
 * The {@code Logger} instance is NOT held in statically-initialized final field as it would
 * trigger premature initialization, we want a call to {@link StartupTools} to happen first.
 *
 * @author Laurent Caillette
 */
public class DocumentGenerator {


  private static final Log LOG = LogFactory.getLog( DocumentGenerator.class ) ;

  private static final String PROBLEMS_FILENAME = "problems.html" ;

  public static void main( String commandName, String[] arguments ) throws Exception {
    final File baseDirectory = new File( SystemUtils.USER_DIR ) ;
    main( commandName, arguments, baseDirectory ) ;
  }

  public static void main(
      String commandName,
      String[] arguments,
      File baseDirectory
  ) throws Exception {
    final BatchParameters parameters ;

    try {
      parameters = new BatchParameters(
          baseDirectory,
          arguments
      ) ;
    } catch( ArgumentException e ) {
      if( e.isHelpRequested() ) {
        printHelpOnConsole( commandName, e ) ;
        System.exit( -1 ) ;
        throw new Error( "Never executes but makes compiler happy" ) ;
      } else {
        LOG.error( "Parameters exception, printing help and exiting.", e ) ;
        printHelpOnConsole( commandName, e ) ;
        System.exit( -2 ) ;
        throw new Error( "Never executes but makes compiler happy" ) ;
      }
    }

    try {
      LOG.debug( "Starting %s with arguments %s",
          ClassUtils.getShortClassName( DocumentGenerator.class ),
          asString( arguments )
      ) ;

      final BatchConfiguration configuration =
          ConfigurationTools.createBatchConfiguration( parameters ); ;
      final File outputDirectory = configuration.getOutputDirectory();
      resetTargetDirectory( LOG, outputDirectory ) ;
      final DocumentProducer documentProducer =
          new DocumentProducer( configuration.getProducerConfiguration() ) ;
      final List< Problem > allProblems = Lists.newArrayList() ;

      for( final DocumentRequest documentRequest : configuration.getDocumentRequests() ) {
        Iterables.addAll(
            allProblems,
            processDocumentRequest(
                LOG,
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

    } catch( Exception e ) {
      LOG.error( "Fatal", e ) ;
      throw e ;
    }
  }

  private static void printHelpOnConsole( String commandName, ArgumentException e ) {
    if( null != e.getMessage() ) {
      System.out.println( e.getMessage() ) ;
    }
    e.getHelpPrinter().print(
        System.out,
        commandName + " [OPTIONS] document1 [document2...]",
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
      Log log,
      DocumentRequest documentRequest,
      File targetDirectory,
      DocumentProducer documentProducer
  ) throws Exception {
    final File outputFile = createOutputFile(
        log,
        targetDirectory,
        documentRequest
    ) ;
    log.info( "Generating document file '%s'...", outputFile.getAbsolutePath() ) ;
    final FileOutputStream outputStream = new FileOutputStream( outputFile ) ;
    final Iterable< Problem > problems = documentProducer.produce( documentRequest, outputStream ) ;
    outputStream.flush() ;
    outputStream.close() ;
    return problems ;
  }

  private static void resetTargetDirectory(
      Log log,
      File targetDirectory
  ) throws IOException {
    if( targetDirectory.exists() ) {
      log.info( "Deleting '%s'...", targetDirectory.getAbsolutePath() ) ;
    }
    FileUtils.deleteDirectory( targetDirectory ) ;
    FileUtils.forceMkdir( targetDirectory ) ;
    log.info( "Created '%s'...", targetDirectory.getAbsolutePath() ) ;
  }

  public static File createOutputFile(
      Log log,
      File targetDir,
      DocumentRequest documentRequest
  )
      throws IOException
  {
    final String relativeFileName = documentRequest.getDocumentSourceName() +
        "." + documentRequest.getRenditionMimeType().getFileExtension() ;
    final File outputFile =  new File( targetDir, relativeFileName ) ;
    log.debug( "Resolved output file name '%s'", outputFile.getAbsolutePath() ) ;
    FileUtils.forceMkdir( outputFile.getParentFile() );
    return outputFile ;
  }



}

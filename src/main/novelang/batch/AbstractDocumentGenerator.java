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
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.io.FileUtils;

import novelang.configuration.parse.ArgumentException;
import novelang.configuration.parse.GenericParameters;
import novelang.system.Log;
import novelang.system.LogFactory;
import novelang.common.Problem;
import novelang.rendering.HtmlProblemPrinter;
import novelang.produce.DocumentRequest;

/**
 * Base class for batch commands.
 *
 * @author Laurent Caillette
 */
public abstract class AbstractDocumentGenerator< P extends GenericParameters > {

  private static final Log LOG = LogFactory.getLog( AbstractDocumentGenerator.class ) ;
  protected static final String PROBLEMS_FILENAME = "problems.html" ;

  protected abstract P createParameters(
      final String[] arguments,
      final File baseDirectory
  ) throws ArgumentException ;

  protected P createParametersOrExit(
      final String commandName,
      final boolean mayTerminateJvm, 
      final String[] arguments,
      final File baseDirectory
  ) throws CannotStartException {
    final P parameters ;
    try {
      parameters = createParameters( arguments, baseDirectory ) ;
    } catch( ArgumentException e ) {
      if( e.isHelpRequested() ) {
        printHelpOnConsole( commandName, e ) ;
        if( mayTerminateJvm ) {
          System.exit( -1 ) ;
        }
        throw new CannotStartException( e ) ;
      } else {
        LOG.error( "Parameters exception, printing help and exiting.", e ) ;
        printHelpOnConsole( commandName, e ) ;
        if( mayTerminateJvm ) {
          System.exit( -2 ) ;
        }
        throw new CannotStartException( e ) ;
      }
    }
    return parameters;
  }

  public void main( String commandName, String[] arguments ) throws Exception {
    final File baseDirectory = new File( SystemUtils.USER_DIR ) ;
    main( commandName, true, arguments, baseDirectory ) ;
  }

  public abstract void main(
      String commandName,
      boolean mayTerminateJvm,
      String[] arguments,
      File baseDirectory
  ) throws Exception ;


  private void printHelpOnConsole( String commandName, ArgumentException e ) {
    if( null != e.getMessage() ) {
      System.out.println( e.getMessage() ) ;
    }
    e.getHelpPrinter().print(
        System.out,
        commandName + getSpecificCommandLineParametersDescriptor(),
        80
    ) ;
  }

  protected abstract String getSpecificCommandLineParametersDescriptor() ;

  protected static void reportProblems(
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

  // TODO does ToStringBuilder save from doing this?
  protected static String asString( String[] args ) {
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

  protected static void resetTargetDirectory(
      File targetDirectory
  ) throws IOException {
    if( targetDirectory.exists() ) {
      LOG.info( "Deleting '%s'...", targetDirectory.getAbsolutePath() ) ;
    }
    FileUtils.deleteDirectory( targetDirectory ) ;
    FileUtils.forceMkdir( targetDirectory ) ;
    LOG.info( "Created '%s'...", targetDirectory.getAbsolutePath() ) ;
  }



  protected static File createOutputFile(
      File targetDir,
      DocumentRequest documentRequest
  )
      throws IOException
  {
    final String relativeFileName = documentRequest.getDocumentSourceName() +
        "." + documentRequest.getRenditionMimeType().getFileExtension() ;
    final File outputFile =  new File( targetDir, relativeFileName ) ;
    LOG.debug( "Resolved output file name '%s'", outputFile.getAbsolutePath() ) ;
    FileUtils.forceMkdir( outputFile.getParentFile() );
    return outputFile ;
  }
}

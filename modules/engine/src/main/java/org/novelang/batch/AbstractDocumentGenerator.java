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
package org.novelang.batch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;

import org.novelang.common.Problem;
import org.novelang.configuration.parse.GenericParameters;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.produce.DocumentRequest;
import org.novelang.rendering.HtmlProblemPrinter;

/**
 * Base class for batch commands.
 *
 * @author Laurent Caillette
 */
public abstract class AbstractDocumentGenerator< PARAMETERS extends GenericParameters > {

  private static final Logger LOGGER = LoggerFactory.getLogger( AbstractDocumentGenerator.class );

  protected static final String PROBLEMS_FILENAME = "problems.html" ;


  public abstract void main( PARAMETERS parameters )
      throws Exception ;


  protected static void reportProblems(
      final File targetDirectory,
      final Iterable< Problem > allProblems
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
  protected static String asString( final String[] args ) {
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

  /**
   * Delete all previously-existing documents in target directory.
   */
  public static void resetTargetDirectory(
      final File targetDirectory
  ) throws IOException {
    if( targetDirectory.exists() ) {
      LOGGER.info( "Deleting '", targetDirectory.getAbsolutePath(), "'..." ) ;
    }
    FileUtils.deleteDirectory( targetDirectory ) ;
    FileUtils.forceMkdir( targetDirectory ) ;
    LOGGER.info( "Created '", targetDirectory.getAbsolutePath(), "'..." ) ;
  }



  protected static File createOutputFile(
      final File targetDir,
      final DocumentRequest documentRequest
  )
      throws IOException
  {
    final String relativeFileName = documentRequest.getDocumentSourceName() +
        "." + documentRequest.getRenditionMimeType().getFileExtension() ;
    final File outputFile =  new File( targetDir, relativeFileName ) ;
    LOGGER.debug( "Resolved output file name '", outputFile.getAbsolutePath(), "'" );
    FileUtils.forceMkdir( outputFile.getParentFile() );
    return outputFile ;
  }
}

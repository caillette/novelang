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

package novelang.daemon;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.mortbay.jetty.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.common.FileTools;
import novelang.common.StructureKind;
import novelang.configuration.ContentConfiguration;

/**
 * Displays directory content.
 * <p>
 * Security concerns:
 * <ul>
 *   <li>All displayed paths are relative.
 *   <li>All generated links are relative.
 *   <li>If target directory contains two dots ("..") then access is not authorized.
 * </ul>
 * Because Web browsers calculate absolute links from relative links and user-typed location,
 * there must be a trailing solidus ("/") at the end of request target. If there isn't,
 * redirection occurs to correct location.
 *
 * @author Laurent Caillette
 */
public class DirectoryScanHandler extends GenericHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger( DirectoryScanHandler.class ) ;

  private final File contentRoot ;
  private static final String ACCESS_DENIED_MESSAGE =
      "Target may contain reference to parent directory, denying access.";

  public DirectoryScanHandler( ContentConfiguration contentConfiguration ) {
    this.contentRoot = contentConfiguration.getContentRoot() ;
  }

  protected void doHandle(
      String target,
      HttpServletRequest request,
      HttpServletResponse response,
      int dispatch
  )
      throws IOException, ServletException
  {
    LOGGER.debug( "Attempting to handle {}", request.getRequestURI() ) ;

    if( target.contains( ".." ) ) {
      LOGGER.warn( ACCESS_DENIED_MESSAGE ) ;
      response.setStatus( HttpServletResponse.SC_UNAUTHORIZED ) ;
      response.setContentType( "html" ) ;

      final PrintWriter writer = new PrintWriter( response.getOutputStream() ) ;
      writer.println( "<html>" ) ;
      writer.println( "<body>" ) ;
      writer.println( ACCESS_DENIED_MESSAGE ) ;
      writer.println( "</body>" ) ;
      writer.println( "</html>" ) ;
      writer.flush() ;

      return ;

    }

    final File scanned = new File( contentRoot, target ) ;
    if( scanned.exists() && scanned.isDirectory() ) {
      LOGGER.debug( "Listing files from '{}'", scanned.getAbsolutePath() ) ;

      if( target.endsWith( "/") ) {
        // Relative links computed on the client side will be correct.
        final List< File > files = FileTools.scan(
            scanned,
            StructureKind.getAllFileExtensions(),
            FileTools.ABSOLUTEPATH_COMPARATOR
        ) ;

        response.setStatus( HttpServletResponse.SC_OK ) ;
        response.setContentType( "html" ) ;

        generateHtml( response.getOutputStream(), scanned, files ) ;

        ( ( Request ) request ).setHandled( true ) ;
        LOGGER.debug( "Handled request {}", request.getRequestURI() ) ;
      } else {
        // Need to redirect to a URL ending with "/" for correct relative links.
        final String redirectionTarget = target + "/" ;
        response.sendRedirect( redirectionTarget ) ;
        response.setStatus( HttpServletResponse.SC_FOUND ) ;
        response.setContentType( "html" ) ;
        LOGGER.debug( "Redirected to '{}'", redirectionTarget ) ;

      }

    }

  }

  private void generateHtml(
      OutputStream outputStream,
      File scannedDirectory,
      Iterable< File > sortedFiles
  ) {
    final PrintWriter writer = new PrintWriter( outputStream ) ;
    writer.println( "<html>" ) ;
    writer.println( "<body>" ) ;
    writer.println( "<tt>" ) ;

    LOGGER.debug( "Relativizing files from '{}'", scannedDirectory.getAbsolutePath() ) ;

    for( File file : sortedFiles ) {
      final String fileNameRelativeToContentRoot = FileTools.relativizePath( contentRoot, file ) ;
      final String documentNameRelativeToContentRoot =
          htmlizeExtension( fileNameRelativeToContentRoot ) ;
      final String fileNameRelativeToScannedDir =
          FileTools.relativizePath( scannedDirectory, file ) ;
      final String documentNameRelativeToScannedDir =
          htmlizeExtension( fileNameRelativeToScannedDir ) ;

      writer.println(
          "<a href=\"" + documentNameRelativeToScannedDir + "\">" +
          documentNameRelativeToContentRoot + "</a>" +
          "<br/>"
      ) ;
    }

    writer.println( "</tt>" ) ;
    writer.println( "</body>" ) ;
    writer.println( "</html>" ) ;
    writer.flush() ;
  }

  private static String htmlizeExtension( String relativeFileName ) {
    return FilenameUtils.removeExtension( relativeFileName ) +
    ".html";
  }


}
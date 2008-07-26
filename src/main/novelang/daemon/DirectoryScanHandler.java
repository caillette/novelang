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
 * Holds resources which don't require rendering.
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

      final List< File > files = FileTools.scan(
          scanned,
          StructureKind.getAllFileExtensions(),
          FileTools.ABSOLUTEPATH_COMPARATOR
      ) ;

      response.setStatus( HttpServletResponse.SC_OK ) ;
      response.setContentType( "html" ) ;

      generateHtml( response.getOutputStream(), files ) ;

      ( ( Request ) request ).setHandled( true ) ;
      LOGGER.debug( "Handled request {}", request.getRequestURI() ) ;

    }

  }

  private void generateHtml(
      OutputStream outputStream,
      Iterable< File > sortedFiles
  ) {
    final PrintWriter writer = new PrintWriter( outputStream ) ;
    writer.println( "<html>" ) ;
    writer.println( "<body>" ) ;
    writer.println( "<tt>" ) ;

    LOGGER.debug( "Relativizing files from '{}'", contentRoot.getAbsolutePath() ) ;

    for( File file : sortedFiles ) {
      final String relativeFileName = FileTools.relativizePath( contentRoot, file ) ;
      final String relativeDocumentName =
          FilenameUtils.removeExtension( relativeFileName ) +
          ".html"
      ;
      writer.println(
          "<a href=\"" + relativeDocumentName + "\">" +
          relativeDocumentName + "</a>" +
          "<br/>"
      ) ;
    }

    writer.println( "</tt>" ) ;
    writer.println( "</body>" ) ;
    writer.println( "</html>" ) ;
    writer.flush() ;
  }


}
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

package org.novelang.daemon;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jetty.server.Request;
import org.novelang.common.FileTools;
import org.novelang.common.StructureKind;
import org.novelang.configuration.ContentConfiguration;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.rendering.RenditionMimeType;

/**
 * Displays directory content.
 * <p>
 * Security concerns:
 * <ul>
 *   <li>All displayed paths are relative.
 *   <li>All generated links are relative.
 *   <li>If target directory contains two dots ("{@code ..}") then access is not authorized.
 *     This should just happen in case of a deliberate attack since Web browsers
 *     (Safari 3.1.2, Camino 1.6.1) resolve ("{@code ..}") on their side. 
 * </ul>
 * Because Web browsers calculate absolute links from relative links and user-typed location,
 * there must be a trailing solidus ("{@code /}") at the end of request target. If there isn't,
 * redirection occurs to correct location.
 * <p>
 * Known problem with Safari: Content-type in response not taken in account.
 * Yet Safari deduces MIME type from file extension (we hit this when generating error reports).
 * In case of a target ending by "{@code /}" Safari believes it's a file to download.
 * So for Safari we handle a fake "{@value #MIME_HINT}" resource which doesn't conflict with other
 * resources. Redirection also occurs to this fake resource is Safari browser is detected. 
 *
 * @author Laurent Caillette
 */
public class DirectoryScanHandler extends GenericHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger( DirectoryScanHandler.class ) ;

  private final File contentRoot ;
  private static final String ACCESS_DENIED_MESSAGE =
      "Target may contain reference to parent directory, denying access ." ;

  private static final String HTML_CONTENT_TYPE = RenditionMimeType.HTML.getFileExtension() ;
  
  /**
   * Fake resource name indicating that directory listing is requested for browsers
   * which know MIME type only from resource extension, not Content-Type.
   */
  public static final String MIME_HINT = "-.html" ;

  public DirectoryScanHandler( final ContentConfiguration contentConfiguration ) {
    this.contentRoot = contentConfiguration.getContentRoot() ;
  }

  @Override
  protected void doHandle(
      final String target,
      final HttpServletRequest request,
      final HttpServletResponse response
  )
      throws IOException, ServletException
  {
    LOGGER.debug( "Attempting to handle request for user agent ",
        request.getHeader( "User-Agent" ) ) ;

    if( target.contains( ".." ) ) {

      sendUnauthorizedResponse( response ) ;
      ( ( Request ) request ).setHandled( true ) ;
      LOGGER.debug( "Concluded by unauthorized message for original request '",
          request.getRequestURI(),
          "'"
      ) ;

    } else {

      final boolean needsMimeHint = doesBrowserNeedMimeHint( request ) ;
      final boolean mimeHintPresent = target.endsWith( "/" + MIME_HINT ) ;

      final String targetWithoutMimeHint ;

      if( mimeHintPresent ) {
        targetWithoutMimeHint = target.substring( 0, target.length() - MIME_HINT.length() ) ;
      } else {
        targetWithoutMimeHint = target ;
      }

      final String normalizedTarget ;
      final boolean needsRedirection ;

      if( targetWithoutMimeHint.endsWith( "/" ) ) {
        normalizedTarget = target + ( needsMimeHint ? MIME_HINT : "" ) ;
        needsRedirection = needsMimeHint & ! mimeHintPresent ;
      } else {
        normalizedTarget = target + "/" + ( needsMimeHint ? MIME_HINT : "" ) ;
        needsRedirection = true ;
      }

      final File scanned = new File( contentRoot, targetWithoutMimeHint ) ;
      final boolean directoryExists = scanned.exists() && scanned.isDirectory() ;

      if( directoryExists ) {
        if( needsRedirection ) {
          redirectTo( response, normalizedTarget ) ;
          LOGGER.debug( "Concluded by redirection for original request '",
              request.getRequestURI(), "'."
          ) ;
        } else {
          listFilesAndDirectories( response, scanned ) ;
          LOGGER.debug( "Concluded by directory listing for original request '",
              request.getRequestURI(), "'." ) ;
        }
        ( ( Request ) request ).setHandled( true ) ;
      }

    }
  }

  private static void redirectTo( 
      final HttpServletResponse response, 
      final String redirectionTarget 
  )
      throws IOException
  {
    response.sendRedirect( redirectionTarget ) ;
    response.setStatus( HttpServletResponse.SC_FOUND ) ;
    response.setContentType( HTML_CONTENT_TYPE ) ;
    LOGGER.debug( "Redirected to '", redirectionTarget, "'." ) ;
  }

  /**
   * Currently returns true if Apple's Safari browser is detected.
   */
  private static boolean doesBrowserNeedMimeHint( final HttpServletRequest request ) {
    final String userAgent = request.getHeader( "User-Agent" ) ;
    if( null == userAgent ) {
      LOGGER.warn( "Got no User-Agent in ", request ) ;
      return false ;
    } else {
      return userAgent.contains( "Safari" ) ;
//      return userAgent.contains( "Mozilla" ) ;
    }
  }

  private static void sendUnauthorizedResponse( 
      final HttpServletResponse response 
  ) throws IOException {
    LOGGER.warn( ACCESS_DENIED_MESSAGE ) ;
    response.setStatus( HttpServletResponse.SC_UNAUTHORIZED ) ;
    response.setContentType( HTML_CONTENT_TYPE ) ;

    final PrintWriter writer = new PrintWriter( response.getOutputStream() ) ;
    writer.println( "<html>" ) ;
    writer.println( "<body>" ) ;
    writer.println( ACCESS_DENIED_MESSAGE ) ;
    writer.println( "</body>" ) ;
    writer.println( "</html>" ) ;
    writer.flush() ;
  }

  private void listFilesAndDirectories(
    final HttpServletResponse response,
    final File scanned
  ) throws IOException {

    final List< File > filesAndDirectories = Lists.newArrayList() ;
    filesAndDirectories.addAll(
        FileTools.scanFiles( scanned, StructureKind.getAllFileExtensions(), true ) ) ;
    filesAndDirectories.addAll( 
        FileTools.scanDirectories( scanned ) ) ;

    final List< File > files = Ordering.from( FileTools.ABSOLUTEPATH_COMPARATOR ).sortedCopy(
        filesAndDirectories
    ) ; 

    response.setStatus( HttpServletResponse.SC_OK ) ;
    generateHtml( response.getOutputStream(), scanned, files ) ;
    // Must be set last or Safari gets confused and cannot render the document as a Web page.
    response.setContentType( HTML_CONTENT_TYPE ) ;
  }


  private void generateHtml(
      final OutputStream outputStream,
      final File scannedDirectory,
      final Iterable< File > sortedFiles
  ) {
    final PrintWriter writer = new PrintWriter( outputStream ) ;
    writer.println( "<html>" ) ;
    writer.println( "<body>" ) ;
    writer.println( "<tt>" ) ;

    LOGGER.debug( "Relativizing files from '", scannedDirectory.getAbsolutePath(), "'" ) ;

    if( FileTools.isParentOf( contentRoot, scannedDirectory ) ) {
      writer.println( "<a href=\"..\">..</a><br/>" ) ;
    }

    for( final File file : sortedFiles ) {
      final String documentNameRelativeToContentRoot = createLinkablePath( contentRoot, file ) ;
      final String documentNameRelativeToScannedDir = createLinkablePath( scannedDirectory, file ) ;

      writer.println(
          ( file.isDirectory() ? "" : "&nbsp;&nbsp;" ) +
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

  private static String createLinkablePath( final File scannedDirectory, final File file ) {
    final String fileNameRelativeToScannedDir =
        FileTools.urlifyPath( FileTools.relativizePath( scannedDirectory, file ) ) ;
    return file.isDirectory() ?
        fileNameRelativeToScannedDir :
        htmlizeExtension( fileNameRelativeToScannedDir )
    ;
  }

  private static String htmlizeExtension( final String relativeFileName ) {
    return FilenameUtils.removeExtension( relativeFileName ) +
    ".html";
  }


}
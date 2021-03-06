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
package org.novelang.daemon;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.rendering.RenditionMimeType;

/**
 *
 * @author Laurent Caillette
 */
public class LocalhostOnlyHandler extends GenericHandler{

  private static final Logger LOGGER = LoggerFactory.getLogger( LocalhostOnlyHandler.class ) ;

  private static final String HTML_CONTENT_TYPE = RenditionMimeType.HTML.getFileExtension() ;

  public LocalhostOnlyHandler() { }

  @Override
  protected void doHandle(
      final String target,
      final HttpServletRequest request,
      final HttpServletResponse response
  ) throws IOException, ServletException {
    final String remoteAddress = request.getRemoteHost() ;
    if( ! isLocalhost( remoteAddress ) ) {
      LOGGER.info( "Unauthorized connection: ", remoteAddress ) ;
      response.setStatus( HttpServletResponse.SC_UNAUTHORIZED ) ;

      final PrintWriter writer = new PrintWriter( response.getOutputStream() ) ;
      writer.println( "<html>" ) ;
      writer.println( "<body>" ) ;
      writer.println( "<p>" ) ;
      writer.println( "Not authorized to connect: " + remoteAddress ) ;
      writer.println( "</p>" ) ;
      writer.println( "<p>" ) ;
      writer.println( "This server only allows HTTP requests from localhost" ) ;
      writer.println( "</p>" ) ;
      writer.println( "</body>" ) ;
      writer.println( "</html>" ) ;
      writer.flush() ;

      response.setContentType( HTML_CONTENT_TYPE ) ;
      ( ( Request ) request ).setHandled( true ) ;

    }


  }

  private static boolean isLocalhost( final String address ) {
    try {
      return InetAddress.getByName( address ).isLoopbackAddress() ;
    } catch ( UnknownHostException e ) {
      LOGGER.warn( "Could not resolve: '", address, "'" ) ;
      return false ;
    }
  }
}
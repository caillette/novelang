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

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Request;
import novelang.system.LogFactory;
import novelang.system.Log;
import novelang.rendering.RenditionMimeType;

/**
 *
 * @author Laurent Caillette
 */
public class LocalhostOnlyHandler extends GenericHandler{

  private static final Log LOG = LogFactory.getLog( LocalhostOnlyHandler.class ) ;

  private static final String HTML_CONTENT_TYPE = RenditionMimeType.HTML.getFileExtension() ;

  public LocalhostOnlyHandler() { }

  protected void doHandle(
      String target,
      HttpServletRequest request,
      HttpServletResponse response,
      int dispatch
  ) throws IOException, ServletException {
    final String remoteAddress = request.getRemoteHost() ;
    if( ! isLocalhost( remoteAddress ) ) {
      LOG.info( "Unauthorized connection: %s", remoteAddress ) ;
      response.setStatus( HttpServletResponse.SC_UNAUTHORIZED ) ;

      final PrintWriter writer = new PrintWriter( response.getOutputStream() ) ;
      writer.println( "<html>" ) ;
      writer.println( "<body>" ) ;
      writer.println( "Not authorized to connect: " + remoteAddress ) ;
      writer.println( "</body>" ) ;
      writer.println( "</html>" ) ;
      writer.flush() ;

      response.setContentType( HTML_CONTENT_TYPE ) ;
      ( ( Request ) request ).setHandled( true ) ;

    }


  }

  private boolean isLocalhost( String remoteAddress ) {
    return remoteAddress.startsWith( "127.0.0" ) ;
  }
}
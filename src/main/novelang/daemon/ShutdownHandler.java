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
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mortbay.jetty.Request;
import novelang.configuration.FontDescriptor;
import novelang.configuration.ServerConfiguration;
import novelang.common.Renderable;
import novelang.part.Part;
import novelang.produce.DocumentProducer;
import novelang.produce.RequestTools;
import novelang.produce.DocumentRequest;
import novelang.rendering.RenditionMimeType;
import novelang.loader.ResourceName;
import novelang.parser.SupportedCharacters;
import novelang.parser.Escape;
import com.google.common.collect.Sets;

/**
 *
 * @author Laurent Caillette
 */
public class ShutdownHandler extends GenericHandler{

  private static final Logger LOGGER = LoggerFactory.getLogger( ShutdownHandler.class ) ;
  private static final String SHUTDOWN_TARGET = "/~shutdown.html";

  private static final String HTML_CONTENT_TYPE = RenditionMimeType.HTML.getFileExtension() ;

  public ShutdownHandler() { }

  protected void doHandle(
      String target,
      HttpServletRequest request,
      HttpServletResponse response,
      int dispatch
  ) throws IOException, ServletException {
    if( SHUTDOWN_TARGET.equals( target ) ) {
      LOGGER.info( "Shutdown requested!" ) ;
      response.setStatus( HttpServletResponse.SC_UNAUTHORIZED ) ;

      final PrintWriter writer = new PrintWriter( response.getOutputStream() ) ;
      writer.println( "<html>" ) ;
      writer.println( "<body>" ) ;
      writer.println( "Shutting down!" ) ;
      writer.println( "</body>" ) ;
      writer.println( "</html>" ) ;
      writer.flush() ;

      response.setContentType( HTML_CONTENT_TYPE ) ;
      ( ( Request ) request ).setHandled( true ) ;

      System.exit( 0 ) ;
    }


  }
}
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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;

/**
 * Base class for serving requests, with catch-all error handling.
 * 
 * @author Laurent Caillette
 */
public abstract class GenericHandler extends AbstractHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger( GenericHandler.class );

  @Override
  public final void handle(
      final String target,
      final Request jettyRequest,
      final HttpServletRequest request,
      final HttpServletResponse response
  ) throws IOException {

    if( ( ( Request ) request ).isHandled() ) {
      // Jetty seems to pass the request to every handler, even if a previous one
      // did set the request as handled.
      return ;
    }

    try {
      doHandle( target, request, response ) ;
    } catch( Exception e ) {
      response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR ) ;
      response.getOutputStream().print( "<html>" );
      response.getOutputStream().print( "<body>" );
      response.getOutputStream().print( "<pre>" );

      final PrintWriter exceptionWriter = new PrintWriter( response.getOutputStream() ) ;
      e.printStackTrace( exceptionWriter );
      exceptionWriter.flush() ;

      response.getOutputStream().print( "</pre>" );
      response.getOutputStream().print( "</body>" );
      response.getOutputStream().print( "</html>" );

      LOGGER.warn( e, "Exception occured" );

    }

  }

  protected abstract void doHandle(
      String target,
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException, ServletException ;


}

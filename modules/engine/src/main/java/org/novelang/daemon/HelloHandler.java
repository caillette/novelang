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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Just some "Hello world" stuff to get sure Jetty's up.
 *
 * @author Laurent Caillette
 */
public class HelloHandler extends AbstractHandler {

  @Override
  public void handle(
      final String target,
      final Request jettyRequest,
      final HttpServletRequest request,
      final HttpServletResponse response
  )
      throws IOException, ServletException {
    response.setContentType( "text/html" ) ;
    response.setStatus( HttpServletResponse.SC_OK ) ;
    response.getWriter().println( "<h1>Hello</h1>" ) ;
    ( ( Request ) request ).setHandled( true ) ;
  }
}

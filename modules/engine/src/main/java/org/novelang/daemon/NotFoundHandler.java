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
 * @author Laurent Caillette
 */
public class NotFoundHandler extends AbstractHandler {

  @Override
  public void handle(
      final String target,
      final Request jettyRequest,
      final HttpServletRequest request,
      final HttpServletResponse response
  )
      throws IOException, ServletException
  {
    response.setContentType( "text/html" ) ;
    response.setStatus( HttpServletResponse.SC_NOT_FOUND ) ;
    response.getWriter().println(
        "<html><body><h1>Not found: " + request.getPathInfo() + "</h1></html></body>" ) ;
    ( ( Request ) request ).setHandled( true ) ;
  }

}
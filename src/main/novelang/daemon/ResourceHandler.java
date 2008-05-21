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
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.configuration.ServerConfiguration;
import novelang.loader.ResourceLoader;
import novelang.loader.ResourceNotFoundException;
import novelang.produce.RequestTools;
import novelang.produce.PolymorphicRequest;

/**
 * @author Laurent Caillette
 */
public class ResourceHandler extends AbstractHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger( ResourceHandler.class ) ;

  private final ResourceLoader resourceLoader ;

  public ResourceHandler( ServerConfiguration serverConfiguration ) {
    this( serverConfiguration.getRenderingConfiguration().getResourceLoader() ) ;
  }

  protected ResourceHandler( ResourceLoader resourceLoader ) {
    this.resourceLoader = resourceLoader ;
  }

  public void handle(
      String target,
      HttpServletRequest request,
      HttpServletResponse response,
      int dispatch
  )
      throws IOException, ServletException
  {
    final PolymorphicRequest documentRequest =
        RequestTools.createPolymorphicRequest( request.getPathInfo() ) ;

    if( null != documentRequest ) {

      try {
        final InputStream inputStream = resourceLoader.getInputStream(
            documentRequest.getDocumentSourceName() + "." +
            documentRequest.getResourceExtension()
        ) ;

        IOUtils.copy( inputStream, response.getOutputStream() ) ;

        response.setStatus( HttpServletResponse.SC_OK ) ;
        response.setContentType( documentRequest.getResourceExtension() ) ;
        ( ( Request ) request ).setHandled( true ) ;


        LOGGER.debug( "Handled request {}", request.getRequestURI() ) ;
        
      } catch( ResourceNotFoundException e ) {
        // Do nothing, we just don't handle that request.
      }

    }
  }

}

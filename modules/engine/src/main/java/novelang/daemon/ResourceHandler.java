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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import novelang.configuration.ProducerConfiguration;
import novelang.loader.ResourceLoader;
import novelang.loader.ResourceLoaderTools;
import novelang.loader.ResourceName;
import novelang.loader.ResourceNotFoundException;
import novelang.loader.UrlResourceLoader;
import novelang.logger.Logger;
import novelang.logger.LoggerFactory;
import novelang.produce.PolymorphicRequest;
import novelang.produce.RequestTools;
import org.apache.commons.io.IOUtils;
import org.mortbay.jetty.Request;

/**
 * Holds resources which don't require rendering.
 *
 * @author Laurent Caillette
 */
public class ResourceHandler extends GenericHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger( ResourceHandler.class ) ;

  private final ResourceLoader resourceLoader ;

  public ResourceHandler( final ProducerConfiguration serverConfiguration ) {
    this(
        ResourceLoaderTools.compose(
            serverConfiguration.getRenderingConfiguration().getResourceLoader(),
            new UrlResourceLoader( createUrlQuiet(
                serverConfiguration.getContentConfiguration().getContentRoot() ) )
        )
    ) ;
  }

  /**
   * Dirty hack.
   */
  private static URL createUrlQuiet( final File file ) {
    try {
      return file.toURL() ;
    } catch( MalformedURLException e ) {
      throw new RuntimeException( e ) ;
    }
  }

  protected ResourceHandler( final ResourceLoader resourceLoader ) {
    this.resourceLoader = resourceLoader ;
    LOGGER.debug( "Using resourceLoader ", resourceLoader ) ;
  }

  protected void doHandle(
      final String target,
      final HttpServletRequest request,
      final HttpServletResponse response,
      final int dispatch
  )
      throws IOException, ServletException
  {
    LOGGER.debug( "Attempting to handle ", request.getRequestURI() ) ;

    final PolymorphicRequest documentRequest = 
        RequestTools.createPolymorphicRequest( request.getPathInfo() ) ;

    if( null != documentRequest ) {

      try {
        final String resourceName = removeLeadingSolidus(
            documentRequest.getDocumentSourceName() + "." +
            documentRequest.getResourceExtension()
        ) ;
        final InputStream inputStream = resourceLoader.getInputStream(
            new ResourceName( resourceName ) ) ;

        try {
          IOUtils.copy( inputStream, response.getOutputStream() ) ; // TODO close stream.
        } finally {
          inputStream.close() ;
        }

        response.setStatus( HttpServletResponse.SC_OK ) ;

        final String contentType =
            ResourceMimeTypes.getMimeType( documentRequest.getResourceExtension() ) ;
        if( null != contentType ) {
          response.setContentType( contentType ) ;
        }

        ( ( Request ) request ).setHandled( true ) ;
        LOGGER.debug(
            "Handled request '",
            request.getRequestURI(),
            "' with content-type '",
            contentType,
            "'."
        ) ;
        
      } catch( ResourceNotFoundException e ) {
        LOGGER.debug( "Could not serve ", request.getRequestURI() ) ;
        // Then do nothing, we just don't handle that request.
      }

    }
  }

  private static String removeLeadingSolidus( final String s ) {
    if( s.startsWith( "/" ) ) {
      return s.substring( 1 ) ;
    } else {
      return s ;
    }
  }
}

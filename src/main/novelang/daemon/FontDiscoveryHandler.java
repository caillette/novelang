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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.configuration.ProducerConfiguration;
import novelang.configuration.RenderingConfiguration;
import novelang.loader.ResourceName;
import novelang.parser.Encoding;
import novelang.rendering.RenditionMimeType;
import novelang.rendering.font.FontDiscoveryStreamer;

/**
 * Generates a PDF document showing available fonts.
 *
 * @author Laurent Caillette
 */
public class FontDiscoveryHandler extends GenericHandler{

  private static final Logger LOGGER = LoggerFactory.getLogger( FontDiscoveryHandler.class ) ;

  private final RenderingConfiguration renderingConfiguration ;
  public static final String DOCUMENT_NAME = "/~fonts.pdf" ;
  public static final ResourceName STYLESHEET = new ResourceName( "font-list.xsl" ) ;

  public FontDiscoveryHandler( ProducerConfiguration producerConfiguration ) {
    renderingConfiguration = producerConfiguration.getRenderingConfiguration() ;
  }

  protected void doHandle( 
      String target,
      HttpServletRequest request,
      HttpServletResponse response,
      int dispatch
  ) throws IOException, ServletException {
    if( DOCUMENT_NAME.equals( target ) ) {
      LOGGER.info( "Font listing requested" ) ;

      final FontDiscoveryStreamer  fontDiscoveryStreamer =
          new FontDiscoveryStreamer( renderingConfiguration, STYLESHEET ) ;

      response.setStatus( HttpServletResponse.SC_OK ) ;
      try {
        fontDiscoveryStreamer.generate( response.getOutputStream(), Encoding.SOURCE ) ;
      } catch( Exception e ) {
        throw new RuntimeException( e );
      }

      response.setContentType( RenditionMimeType.PDF.getMimeName() ) ;
//      response.setContentType( RenditionMimeType.XML.getMimeName() ) ;
        ( ( Request ) request ).setHandled( true ) ;
        LOGGER.debug( "Handled request {}", request.getRequestURI() ) ;
    }
  }




}

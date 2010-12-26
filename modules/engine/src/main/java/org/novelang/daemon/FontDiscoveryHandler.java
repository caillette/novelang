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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerConfigurationException;
import org.mortbay.jetty.Request;
import org.novelang.configuration.ProducerConfiguration;
import org.novelang.configuration.RenderingConfiguration;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.DefaultCharset;
import org.novelang.outfit.loader.ResourceName;
import org.novelang.outfit.xml.TransformerCompositeException;
import org.novelang.rendering.RenditionMimeType;
import org.novelang.rendering.font.FontDiscoveryStreamer;
import org.xml.sax.SAXException;

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

  public FontDiscoveryHandler( final ProducerConfiguration producerConfiguration ) {
    renderingConfiguration = producerConfiguration.getRenderingConfiguration() ;
  }

  @Override
  protected void doHandle(
      final String target,
      final HttpServletRequest request,
      final HttpServletResponse response,
      final int dispatch
  ) throws IOException, ServletException {
    if( DOCUMENT_NAME.equals( target ) ) {
      LOGGER.info( "Font listing requested" ) ;

      final FontDiscoveryStreamer  fontDiscoveryStreamer;
      try {
        fontDiscoveryStreamer = new FontDiscoveryStreamer( renderingConfiguration, STYLESHEET );
      } catch( TransformerConfigurationException e ) {
        throw new ServletException( e ) ;
      } catch( SAXException e ) {
        throw new ServletException( e );
      } catch( TransformerCompositeException e ) {
        throw new ServletException( e );
      }

      response.setStatus( HttpServletResponse.SC_OK ) ;
      try {
        fontDiscoveryStreamer.generate( response.getOutputStream(), DefaultCharset.SOURCE ) ;
      } catch( Exception e ) {
        throw new RuntimeException( e );
      }

      response.setContentType( RenditionMimeType.PDF.getMimeName() ) ;
//      response.setContentType( RenditionMimeType.XML.getMimeName() ) ;
        ( ( Request ) request ).setHandled( true ) ;
        LOGGER.debug( "Handled request ", request.getRequestURI() ) ;
    }
  }




}

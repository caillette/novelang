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
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerConfigurationException;

import org.eclipse.jetty.server.Request;
import org.xml.sax.SAXException;

import org.novelang.configuration.ProducerConfiguration;
import org.novelang.configuration.RenderingConfiguration;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.DefaultCharset;
import org.novelang.outfit.TemporaryFileTools;
import org.novelang.outfit.loader.ResourceName;
import org.novelang.outfit.xml.TransformerCompositeException;
import org.novelang.rendering.RenditionMimeType;
import org.novelang.rendering.buffer.CisternOutputStream;
import org.novelang.rendering.font.FontDiscoveryStreamer;

/**
 * Generates a PDF document showing available fonts.
 *
 * @author Laurent Caillette
 */
public class FontDiscoveryHandler extends GenericHandler{

  private static final Logger LOGGER = LoggerFactory.getLogger( FontDiscoveryHandler.class ) ;

  /**
   * Only because
   * {@link org.novelang.rendering.Renderer#render(org.novelang.common.Renderable, java.io.OutputStream, org.novelang.common.metadata.Page, java.io.File)}
   * requires a reference to content directory for other cases.
   */
  private final URL contentRoot ;

  private final RenderingConfiguration renderingConfiguration ;
  public static final String DOCUMENT_NAME = "/~fonts.pdf" ;
  public static final ResourceName STYLESHEET = new ResourceName( "font-list.xsl" ) ;

  /**
   * Buffer size for {@link org.novelang.rendering.buffer.CisternOutputStream}.
   */
  private static final int BUFFER_SIZE_BYTES = 1024 * 1024 ;


  public FontDiscoveryHandler( final ProducerConfiguration producerConfiguration ) {
    renderingConfiguration = producerConfiguration.getRenderingConfiguration() ;
    try {
      contentRoot = producerConfiguration.getContentConfiguration()
          .getContentRoot().toURI().toURL() ;
    } catch( MalformedURLException e ) {
      throw new RuntimeException( e ) ;
    }
  }

  @Override
  protected void doHandle(
      final String target,
      final HttpServletRequest request,
      final HttpServletResponse response
  ) throws IOException, ServletException {
    if( DOCUMENT_NAME.equals( target ) ) {
      LOGGER.info( "Font listing requested" ) ;

      final FontDiscoveryStreamer  fontDiscoveryStreamer;
      try {
        fontDiscoveryStreamer =
            new FontDiscoveryStreamer( renderingConfiguration, STYLESHEET, contentRoot );
      } catch( TransformerConfigurationException e ) {
        throw new ServletException( e ) ;
      } catch( SAXException e ) {
        throw new ServletException( e );
      } catch( TransformerCompositeException e ) {
        throw new ServletException( e );
      }

      response.setStatus( HttpServletResponse.SC_OK ) ;

      final CisternOutputStream deferredOutputStream = new CisternOutputStream(
            TemporaryFileTools.TEMPORARY_FILE_SERVICE.createFileSupplier( "page", ".pdf" ),
            BUFFER_SIZE_BYTES
      ) ;

      try {
        fontDiscoveryStreamer.generate( deferredOutputStream, DefaultCharset.SOURCE ) ;
        deferredOutputStream.copy( response.getOutputStream() ) ;
      } catch( Exception e ) {
        throw new RuntimeException( e );
      } finally {
        if( deferredOutputStream != null && deferredOutputStream.isOpen() ) {
          deferredOutputStream.close() ;
        }
      }

      response.setContentType( RenditionMimeType.PDF.getMimeName() ) ;
//      response.setContentType( RenditionMimeType.XML.getMimeName() ) ;
        ( ( Request ) request ).setHandled( true ) ;
        LOGGER.debug( "Handled request ", request.getRequestURI() ) ;
    }
  }




}

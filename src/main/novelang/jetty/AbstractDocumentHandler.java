/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.jetty;

import java.nio.charset.Charset;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.jetty.Request;
import novelang.renderer.Renderer;
import novelang.renderer.RenditionMimeType;
import novelang.renderer.PlainTextRenderer;
import novelang.renderer.XmlRenderer;
import novelang.renderer.PdfRenderer;
import novelang.model.renderable.Renderable;

/**
 * @author Laurent Caillette
 */
public abstract class AbstractDocumentHandler extends AbstractHandler {

  protected void serve(
      HttpServletRequest request,
      HttpServletResponse response,
      Renderable rendered,
      String target
  ) throws IOException {
    if( target.endsWith( ".txt" ) ) {
      serve( request, response, new PlainTextRenderer(), rendered ) ;
    } else if( target.endsWith( ".xml" ) ) {
      serve( request, response, new XmlRenderer(), rendered ) ;
    } else if( target.endsWith( ".pdf" ) ) {
      serve( request, response, new PdfRenderer(), rendered ) ;
    }
  }

  private void serve(
      HttpServletRequest request,
      HttpServletResponse response,
      Renderer renderer,
      Renderable rendered
  ) throws IOException {

    response.setStatus( HttpServletResponse.SC_OK ) ;

    final RenditionMimeType mimeType = renderer.render(
        rendered,
        response.getOutputStream()
    ) ;
    response.setContentType( mimeType.getMimeName() ) ;
    ( ( Request ) request ).setHandled( true ) ;
  }
}

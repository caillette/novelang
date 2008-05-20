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

package novelang.jetty;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Objects;
import novelang.configuration.RenderingConfiguration;
import novelang.configuration.ServerConfiguration;
import novelang.model.common.FileLookupHelper;
import novelang.model.common.Problem;
import novelang.model.common.StructureKind;
import novelang.model.implementation.Part;
import novelang.model.renderable.Renderable;
import novelang.rendering.DocumentRequest;
import novelang.rendering.GenericRenderer;
import novelang.rendering.HtmlProblemPrinter;
import novelang.rendering.HtmlWriter;
import novelang.rendering.NlpWriter;
import novelang.rendering.PdfWriter;
import novelang.rendering.PlainTextWriter;
import novelang.rendering.Renderer;
import novelang.rendering.RenditionMimeType;
import novelang.rendering.XmlWriter;
import novelang.produce.PolymorphicRequest;
import novelang.produce.RequestTools;
import novelang.produce.DocumentProducer;

/**
 * Serves rendered content.
 *
 * By now, it re-creates a whole document when an error report is requested.
 * This is because it is not possible to change the type of a requested document without
 * an HTTP redirect. 
 * <p>
 * Solutions:
 * <ol>
 * <li> Cache the Problems in the session.
 * <li> Cache all the Parts and Books and whatever.
 * </ol>
 *
 * Solution 1 sounds better as it keeps error display away from complex caching stuff.
 *
 * @author Laurent Caillette
 */
public class DocumentHandler extends AbstractHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger( DocumentHandler.class ) ;

  private final DocumentProducer documentProducer ;


  public DocumentHandler( ServerConfiguration serverConfiguration ) {
    documentProducer = new DocumentProducer( serverConfiguration ) ;
  }


  public void handle(
      String target,
      HttpServletRequest request,
      HttpServletResponse response,
      int dispatch
  ) throws IOException, ServletException {

    final PolymorphicRequest documentRequest =
        RequestTools.createPolymorphicRequest( request.getPathInfo() ) ;

    if( null == documentRequest ) {
      return ;

    } else {
      final ServletOutputStream outputStream = response.getOutputStream();

      if( documentRequest.isRendered() ) {

        final Renderable rendered = documentProducer.createRenderable( documentRequest ) ;

        if( documentRequest.getDisplayProblems() ) {

          final HtmlProblemPrinter problemPrinter = new HtmlProblemPrinter() ;
          final String originalTarget = documentRequest.getOriginalTarget();
          problemPrinter.printProblems(
              outputStream,
              rendered.getProblems(),
              originalTarget
          ) ;
          setAsHandled( request ) ;
          // TODO redirect to document page if renderable has no problem.
          LOGGER.debug( "Served error request '{}'", originalTarget ) ;

        } else if( rendered.hasProblem() ) {

          final HtmlProblemPrinter problemPrinter = new HtmlProblemPrinter() ;
          final String redirectionTarget =
              documentRequest.getOriginalTarget() + RequestTools.ERRORPAGE_SUFFIX;
          response.sendRedirect( redirectionTarget ) ;
          response.setStatus( HttpServletResponse.SC_FOUND ) ;
          problemPrinter.printProblems(
              outputStream, rendered.getProblems(), request.getQueryString() ) ;
          response.setContentType( problemPrinter.getMimeType().getMimeName() ) ;
          setAsHandled( request ) ;
          LOGGER.debug( "Redirected to '{}'", redirectionTarget ) ;

        } else {

          response.setStatus( HttpServletResponse.SC_OK ) ;

          documentProducer.produce( documentRequest, rendered, outputStream ) ;
          response.setContentType( documentRequest.getRenditionMimeType().getMimeName() ) ;
          setAsHandled( request ) ;
          LOGGER.debug( "Handled request {}", request.getRequestURI() ) ;

        }
      } // Don't handle the non-rendered case, this is left to other handlers.

    }
  }

  private void setAsHandled( HttpServletRequest request ) {
    ( ( Request ) request ).setHandled( true ) ;
  }

}

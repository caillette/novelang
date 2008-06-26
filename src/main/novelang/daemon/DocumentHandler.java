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
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.configuration.ServerConfiguration;
import novelang.common.Renderable;
import novelang.common.Problem;
import novelang.rendering.HtmlProblemPrinter;
import novelang.produce.PolymorphicRequest;
import novelang.produce.RequestTools;
import novelang.produce.DocumentProducer;
import com.google.common.collect.Lists;

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

        final Renderable rendered;
        try {
          rendered = documentProducer.createRenderable( documentRequest );
        } catch( IOException e ) {
          renderProblems(
              Lists.newArrayList( Problem.createProblem( e ) ),
              documentRequest.getOriginalTarget(),
              outputStream
          ) ;
          throw e ;
        }

        if( documentRequest.getDisplayProblems() ) {

          if( rendered.hasProblem() ) {
            renderProblemsAsRequested( documentRequest, rendered, outputStream ) ;
          } else {
            redirectToOriginalTarget( documentRequest, response ) ;
          }

        } else if( rendered.hasProblem() ) {
          redirectToProblemPage( documentRequest, response ) ;
        } else {

          response.setStatus( HttpServletResponse.SC_OK ) ;
          documentProducer.produce( documentRequest, rendered, outputStream ) ;
          response.setContentType( documentRequest.getRenditionMimeType().getMimeName() ) ;
          LOGGER.debug( "Handled request {}", request.getRequestURI() ) ;

        }
      }

      ( ( Request ) request ).setHandled( true ) ;
    }
  }

  private void redirectToProblemPage(
      PolymorphicRequest documentRequest,
      HttpServletResponse response
  ) throws IOException {
    final String redirectionTarget =
        documentRequest.getOriginalTarget() + RequestTools.ERRORPAGE_SUFFIX ;
    response.sendRedirect( redirectionTarget ) ;
    response.setStatus( HttpServletResponse.SC_FOUND ) ;
    LOGGER.debug( "Redirected to '{}'", redirectionTarget ) ;
  }

  private void redirectToOriginalTarget(
      PolymorphicRequest documentRequest,
      HttpServletResponse response
  ) throws IOException {
    final String redirectionTarget = documentRequest.getOriginalTarget() ;
    response.sendRedirect( redirectionTarget ) ;
    response.setStatus( HttpServletResponse.SC_FOUND ) ;
    response.setContentType( documentRequest.getRenditionMimeType().getMimeName() ) ;
    LOGGER.debug( "Redirected to '{}'", redirectionTarget ) ;
  }

  private void renderProblemsAsRequested(
      PolymorphicRequest documentRequest,
      Renderable rendered,
      ServletOutputStream outputStream
  ) throws IOException {
    renderProblems( rendered.getProblems(), documentRequest.getOriginalTarget(), outputStream ) ;
  }

  private void renderProblems(
      Iterable< Problem > problems,
      String originalTarget,
      OutputStream outputStream
  ) throws IOException {
    final HtmlProblemPrinter problemPrinter = new HtmlProblemPrinter() ;
    problemPrinter.printProblems(
        outputStream,
        problems,
        originalTarget
    ) ;
    LOGGER.debug( "Served error request '{}'", originalTarget ) ;

  }

}

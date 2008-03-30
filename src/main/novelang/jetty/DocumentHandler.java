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

import java.io.IOException;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;

import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.jetty.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.rendering.Renderer;
import novelang.rendering.DocumentRequest;
import novelang.rendering.ProblemPrinter;
import novelang.rendering.RenditionMimeType;
import novelang.rendering.GenericRenderer;
import novelang.rendering.PlainTextWriter;
import novelang.rendering.PdfWriter;
import novelang.rendering.XmlWriter;
import novelang.rendering.XslWriter;
import novelang.rendering.NlpWriter;
import novelang.model.renderable.Renderable;
import novelang.model.common.StructureKind;
import novelang.model.common.Problem;
import novelang.model.common.FileLookupHelper;
import novelang.model.implementation.Part;
import novelang.model.implementation.Book;
import com.google.common.base.Objects;

/**
 * This method does all the dispatching of servlet requests.
 *
 * By now, it re-creates a whole document when an error report is requested.
 * This is because it is not possible to change the type of a requested document without
 * an HTTP redirect. 
 *
 * Solutions:
 * 1) Cache the Problems in the session.
 * 2) Cache all the Parts and Books and whatever.
 *
 * Solution 1 sounds better as it keeps error display away from complex caching stuff.
 *
 * @author Laurent Caillette
 */
public class DocumentHandler extends AbstractHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger( DocumentHandler.class ) ;

  private final File basedir ;


  public DocumentHandler( File basedir ) {
    this.basedir = Objects.nonNull( basedir ) ;
    if( ! basedir.exists() ) {
      throw new IllegalArgumentException( "Doesn't exist: '" + basedir.getAbsolutePath() + "'" ) ;
    }
  }


  public void handle(
      String target,
      HttpServletRequest request,
      HttpServletResponse response,
      int dispatch
  ) throws IOException, ServletException {

    final DocumentRequest documentRequest = HttpDocumentRequest.create( request ) ;

    if( null == documentRequest ) {
      return ;
    }

    final ServletOutputStream outputStream = response.getOutputStream();
    final Renderable rendered = createRenderable( documentRequest ) ;
    final Iterable<Problem> problems = rendered.getProblems();

    if( documentRequest.getDisplayProblems() ) {
      final ProblemPrinter problemPrinter = new ProblemPrinter() ;
      final String originalTarget = documentRequest.getOriginalTarget();
      problemPrinter.printProblems(
          outputStream,
          problems,
          originalTarget
      ) ;
      setAsHandled( request ) ;
      // TODO redirect to document page if renderable has no problem.
      LOGGER.debug( "Served error request '{}'", originalTarget ) ;

    } else if( rendered.hasProblem() ) {
      final ProblemPrinter problemPrinter = new ProblemPrinter() ;
      final String redirectionTarget =
          documentRequest.getOriginalTarget() + HttpDocumentRequest.ERRORPAGE_SUFFIX;
      response.sendRedirect( redirectionTarget ) ;
      response.setStatus( HttpServletResponse.SC_FOUND ) ;
      problemPrinter.printProblems(
          outputStream, problems, request.getQueryString() ) ;
      response.setContentType( problemPrinter.getMimeType().getMimeName() ) ;
      setAsHandled( request ) ;
      LOGGER.debug( "Redirected to '{}'", redirectionTarget ) ;

    } else {
      final RenditionMimeType mimeType = documentRequest.getDocumentMimeType() ;
      switch( mimeType ) {
        case PDF :
          serve( request, response, new GenericRenderer( new PdfWriter() ), rendered ) ;
          break;
        case TXT :
          serve( request, response, new GenericRenderer( new PlainTextWriter() ), rendered ) ;
          break;
        case XML :
          serve( request, response, new GenericRenderer( new XmlWriter() ), rendered ) ;
          break ;
        case HTML :
          serve(
              request,
              response,
              new GenericRenderer( new XslWriter( "html.xsl", RenditionMimeType.HTML ) ),
              rendered
          ) ;
          break ;
        case NLP :
          serve( request, response, new GenericRenderer( new NlpWriter() ), rendered ) ;
          break ;
        default :
          final IllegalArgumentException illegalArgumentException =
              new IllegalArgumentException( "Unsupported: " + mimeType );
          LOGGER.warn( "Internal error", illegalArgumentException ) ;
          throw illegalArgumentException;
      }

    }
  }

  private Renderable createRenderable( DocumentRequest documentRequest )
      throws IOException
  {
    final StructureKind structureKind = documentRequest.getStructureKind() ;
    switch( structureKind ) {
      case BOOK :
        final File bookFile = FileLookupHelper.load(
            basedir,
            documentRequest.getDocumentSourceName(),
            StructureKind.BOOK.getFileExtensions()
        ) ;
        final Book book = new Book( bookFile.getPath(), bookFile );
        book.load() ;
        return book;
      case PART :
        final File partFile = FileLookupHelper.load(
            basedir,
            documentRequest.getDocumentSourceName(),
            StructureKind.PART.getFileExtensions()
        ) ;
        return new Part( partFile ) ;
      default :
        throw new IllegalArgumentException( "Unsupported: " + structureKind ) ;
    }
  }

  private void serve(
      HttpServletRequest request,
      HttpServletResponse response,
      Renderer renderer,
      Renderable rendered
  ) throws IOException {

    response.setStatus( HttpServletResponse.SC_OK ) ;

    renderer.render(
        rendered,
        response.getOutputStream()
    ) ;
    response.setContentType( renderer.getMimeType().getMimeName() ) ;
    setAsHandled( request ) ;

    LOGGER.debug( "Handled request {}", request.getRequestURI() ) ;
  }

  private void setAsHandled( HttpServletRequest request ) {
    ( ( Request ) request ).setHandled( true ) ;
  }

}

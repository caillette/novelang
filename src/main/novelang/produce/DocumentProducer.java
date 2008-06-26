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
package novelang.produce;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.configuration.RenderingConfiguration;
import novelang.configuration.ServerConfiguration;
import novelang.common.FileLookupHelper;
import novelang.common.Problem;
import novelang.common.StructureKind;
import novelang.common.Renderable;
import novelang.part.Part;
import novelang.book.Book;
import novelang.book.function.FunctionRegistry;
import novelang.rendering.GenericRenderer;
import novelang.rendering.HtmlWriter;
import novelang.rendering.NlpWriter;
import novelang.rendering.PdfWriter;
import novelang.rendering.PlainTextWriter;
import novelang.rendering.RenditionMimeType;
import novelang.rendering.XmlWriter;

/**
 * Produces a document into passed-in {@link DocumentRequest}s.
 *
 * @author Laurent Caillette
 */
public class DocumentProducer {

  private static final Logger LOGGER = LoggerFactory.getLogger( DocumentProducer.class ) ;

  private final File basedir ;
  private final RenderingConfiguration renderingConfiguration ;


  protected DocumentProducer( File basedir, RenderingConfiguration renderingConfiguration ) {
    this.basedir = basedir;
    this.renderingConfiguration = renderingConfiguration;
  }

  public DocumentProducer( ServerConfiguration configuration ) {
    this(
        configuration.getContentConfiguration().getContentRoot(),
        configuration.getRenderingConfiguration()
    ) ;
  }

  public Iterable< Problem > produce(
      final AbstractRequest request,
      final OutputStream outputStream
  ) throws IOException {
    return produce( request, createRenderable( request ), outputStream ) ;
  }

  public Iterable< Problem > produce(
      final AbstractRequest request,
      final Renderable rendered,
      final OutputStream outputStream
  ) throws IOException {

    final RenditionMimeType mimeType = request.getRenditionMimeType() ;

    // Java-flavored curryfication, wow!
    class Serve {
      public void with( GenericRenderer renderer ) { serve( outputStream, renderer, rendered ) ; }
    }
    final Serve serve = new Serve() ;

    LOGGER.debug( "Attempting to produce '{}'", request.getOriginalTarget() ) ;

    switch( mimeType ) {

      case PDF :
        serve.with( new GenericRenderer( new PdfWriter( renderingConfiguration ) ) ) ;
        break ;

      case TXT :
        serve.with( new GenericRenderer( new PlainTextWriter() ) ) ;
        break ;

      case XML :
        serve.with( new GenericRenderer( new XmlWriter() ) ) ;
        break ;

      case HTML :
        serve.with( new GenericRenderer( new HtmlWriter( renderingConfiguration ) ) ) ;
        break ;

      case NLP :
        serve.with( new GenericRenderer( new NlpWriter( renderingConfiguration ) ) ) ;
        break ;

      default :
        throw new IllegalArgumentException( "Unsupported: " + mimeType ) ;
    }

    LOGGER.debug( "Done with '{}'", request.getOriginalTarget() ) ;


    return rendered.getProblems() ;
    
  }

  private void serve(
      OutputStream outputStream,
      GenericRenderer renderer,
      Renderable rendered
  ) {
    renderer.render( rendered, outputStream ) ;
  }

  public Renderable createRenderable( AbstractRequest documentRequest )
      throws IOException
  {

    try {
      final File bookFile = FileLookupHelper.load(
          basedir,
          documentRequest.getDocumentSourceName(),
          StructureKind.BOOK.getFileExtensions()
      ) ;
      return new Book( FunctionRegistry.getStandardRegistry(), bookFile ) ;
    } catch( FileNotFoundException e ) {
      final File partFile = FileLookupHelper.load(
          basedir,
          documentRequest.getDocumentSourceName(),
          StructureKind.PART.getFileExtensions()
      ) ;
      return new Part( partFile ) ;
    }

  }


}

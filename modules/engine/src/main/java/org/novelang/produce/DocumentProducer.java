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
package org.novelang.produce;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;

import com.google.common.base.Preconditions;
import org.novelang.common.FileTools;
import org.novelang.common.Problem;
import org.novelang.common.Renderable;
import org.novelang.common.StructureKind;
import org.novelang.configuration.ProducerConfiguration;
import org.novelang.configuration.RenderingConfiguration;
import org.novelang.loader.ResourceName;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.novella.Novella;
import org.novelang.opus.Opus;
import org.novelang.outfit.ArrayTools;
import org.novelang.rendering.GenericRenderer;
import org.novelang.rendering.HtmlWriter;
import org.novelang.rendering.NovellaWriter;
import org.novelang.rendering.PdfWriter;
import org.novelang.rendering.PlainTextWriter;
import org.novelang.rendering.RenditionMimeType;
import org.novelang.rendering.XmlWriter;
import org.novelang.rendering.XslWriter;

/**
 * Produces a document into passed-in {@link DocumentRequest}s.
 *
 * @author Laurent Caillette
 */
public class DocumentProducer {

  private static final Logger LOGGER = LoggerFactory.getLogger( DocumentProducer.class ) ;

  private final File basedir ;
  private final RenderingConfiguration renderingConfiguration ;
  private final Charset defaultSourceCharset ;
  private final ExecutorService executorService ;


  public DocumentProducer( final ProducerConfiguration configuration ) {
    this.basedir = Preconditions.checkNotNull(
        configuration.getContentConfiguration().getContentRoot() ) ;
    this.renderingConfiguration = Preconditions.checkNotNull(
        configuration.getRenderingConfiguration() ) ;
    this.defaultSourceCharset = Preconditions.checkNotNull(
        configuration.getContentConfiguration().getSourceCharset() ) ;
    this.executorService = configuration.getExecutorService() ;
  }

  public Iterable< Problem > produce(
      final AbstractRequest request,
      final OutputStream outputStream
  ) throws Exception {
    return produce( request, createRenderable( request ), outputStream ) ;
  }

  public Iterable< Problem > produce(
      final AbstractRequest request,
      final Renderable rendered,
      final OutputStream outputStream
  ) throws Exception {

    final RenditionMimeType mimeType = request.getRenditionMimeType() ;

    // Java-flavored curryfication, wow!
    class Serve {
      public void with( final GenericRenderer renderer ) throws Exception {
        serve( outputStream, renderer, rendered ) ;
      }
    }
    final Serve serve = new Serve() ;
    LOGGER.debug(
        "Attempting to produce '",
        request.getOriginalTarget(),
        "' ",
        request.toString(),
        ""
    ) ;


    final ResourceName stylesheet = ArrayTools.firstNotNull(
        request.getAlternateStylesheet(),
        rendered.getCustomStylesheetMap().get( mimeType )
    ) ;

    final Charset charset = rendered.getRenderingCharset() ;

    switch( mimeType ) {

      case PDF :
        serve.with( new GenericRenderer( new PdfWriter( renderingConfiguration, stylesheet ) ) ) ;
        break ;

      case TXT :
        serve.with( new GenericRenderer( new PlainTextWriter( charset ) ) ) ;
        break ;

      case XML :
        serve.with( new GenericRenderer( new XmlWriter(), true ) ) ;
        break ;

      case HTML :
        serve.with( new GenericRenderer(
            new HtmlWriter( renderingConfiguration, stylesheet, charset ), true ) ) ;
        break ;

      case NOVELLA:
        serve.with( new GenericRenderer(
            new NovellaWriter( renderingConfiguration, stylesheet, charset ) ) ) ;
        break ;

      case FO :
        final ResourceName foStylesheet =
            stylesheet == null ? PdfWriter.DEFAULT_FO_STYLESHEET : stylesheet ;
        serve.with( new GenericRenderer( new XslWriter( renderingConfiguration, foStylesheet ) ) ) ;
        break ;

      default :
        throw new IllegalArgumentException( "Unsupported: " + mimeType ) ;
    }

    LOGGER.debug( "Done with '", request.getOriginalTarget(), "'." ) ;


    return rendered.getProblems() ;
    
  }

  private static void serve(
      final OutputStream outputStream,
      final GenericRenderer renderer,
      final Renderable rendered
  ) throws Exception {
    renderer.render( rendered, outputStream ) ;
  }

  public Renderable createRenderable( final AbstractRequest documentRequest ) throws IOException {

    final Charset suggestedRenderingCharset = renderingConfiguration.getDefaultCharset() ;
    LOGGER.debug( "About to create renderable with document source name '",
        documentRequest.getDocumentSourceName(),
        "'..."
    ) ;

    try {
      final File bookFile = FileTools.load(
          basedir,
          documentRequest.getDocumentSourceName(),
          StructureKind.OPUS.getFileExtensions()
      ) ;
      return new Opus(
          basedir,
          bookFile,
          executorService,
          defaultSourceCharset,
          suggestedRenderingCharset,
          documentRequest.getTags()
      ) ;
    } catch( FileNotFoundException e ) {
      final File partFile = FileTools.load(
          basedir,
          documentRequest.getDocumentSourceName(),
          StructureKind.NOVELLA.getFileExtensions()
      ) ;
      return new Novella(
          partFile, 
          defaultSourceCharset, 
          suggestedRenderingCharset
      ).relocateResourcePaths( basedir ).makeStandalone( documentRequest.getTags() ) ;
    }

  }


}

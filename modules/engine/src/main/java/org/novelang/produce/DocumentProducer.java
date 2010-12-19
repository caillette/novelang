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
import org.novelang.common.metadata.Page;
import org.novelang.common.metadata.PageIdentifier;
import org.novelang.configuration.ProducerConfiguration;
import org.novelang.configuration.RenderingConfiguration;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.novella.Novella;
import org.novelang.opus.Opus;
import org.novelang.outfit.ArrayTools;
import org.novelang.outfit.loader.ResourceName;
import org.novelang.rendering.FragmentWriter;
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
      final DocumentRequest request,
      final Renderable rendered,
      final StreamDirector streamDirector
  ) throws Exception {

    final RenditionMimeType mimeType = request.getRenditionMimeType() ;

    // Java-flavored curryfication, wow!
    class Serve {
      public void with( final GenericRenderer renderer ) throws Exception {
        serve( streamDirector, renderer, request.getPageIdentifier(), rendered ) ;
      }
    }
    final Serve serve = new Serve() ;
    LOGGER.debug( "Attempting to produce ", request );


    final ResourceName stylesheet = ArrayTools.firstNotNull(
        request.getAlternateStylesheet(),
        rendered.getCustomStylesheetMap().get( mimeType )
    ) ;

    final Charset charset = rendered.getRenderingCharset() ;
    final FragmentWriter fragmentWriter ;
    boolean renderLocation = false ;

    switch( mimeType ) {

      case PDF :
        fragmentWriter = new PdfWriter( renderingConfiguration, stylesheet ) ;
        break ;

      case TXT :
        fragmentWriter = new PlainTextWriter( charset ) ;
        break ;

      case XML :
        fragmentWriter = new XmlWriter() ;
        renderLocation = true ;
        break ;

      case HTML :
        fragmentWriter = new HtmlWriter( renderingConfiguration, stylesheet, charset ) ;
        renderLocation = true ;
        break ;

      case NOVELLA:
        fragmentWriter = new NovellaWriter( renderingConfiguration, stylesheet, charset ) ;
        break ;

      case FO :
        final ResourceName foStylesheet =
            stylesheet == null ? PdfWriter.DEFAULT_FO_STYLESHEET : stylesheet ;
        fragmentWriter = new XslWriter( renderingConfiguration, foStylesheet ) ;
        break ;

      default :
        throw new IllegalArgumentException( "Unsupported: " + mimeType ) ;
    }

    serve.with( new GenericRenderer( fragmentWriter, renderLocation ) ) ;

    LOGGER.debug( "Done with '", request.getOriginalTarget(), "'." ) ;

    return rendered.getProblems() ;
    
  }

  private static void serve(
      final StreamDirector streamDirector,
      final GenericRenderer renderer,
      final PageIdentifier pageIdentifier,
      final Renderable renderable
  ) throws Exception {
    streamDirector.feedStreams(
        renderable,
        renderer,
        pageIdentifier,
        new StreamDirector.StreamFeeder() {
          @Override
          public void feed(
              final Renderable someRenderable,
              final OutputStream outputStream,
              final Page page
          ) throws Exception {
            renderer.render( someRenderable, outputStream, page ) ;
          }
        }

    ) ;
  }

  public Renderable createRenderable( final DocumentRequest documentRequest ) throws IOException {

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

      LOGGER.info( "Attempting to load file '", bookFile, "' with charset ",
          suggestedRenderingCharset.name() ) ;
      
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

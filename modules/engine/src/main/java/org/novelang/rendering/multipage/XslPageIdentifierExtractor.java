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
package org.novelang.rendering.multipage;

import java.io.OutputStream;
import java.nio.charset.Charset;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.TransformerHandler;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.output.NullOutputStream;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import static com.google.common.base.Preconditions.checkNotNull;

import org.novelang.common.SyntacticTree;
import org.novelang.common.metadata.DocumentMetadata;
import org.novelang.common.metadata.PageIdentifier;
import org.novelang.outfit.xml.SaxRecorder;
import org.novelang.outfit.xml.TransformerErrorListener;
import org.novelang.outfit.xml.XslTransformerFactory;
import org.novelang.rendering.GenericRenderer;
import org.novelang.rendering.RenditionMimeType;
import org.novelang.rendering.XmlWriter;

/**
 * Computes the pages of a document tree, using
 * previously-{@link XslMultipageStylesheetCapture captured}
 * stylesheet.
 *
 * @author Laurent Caillette
 */
public class XslPageIdentifierExtractor implements PagesExtractor {

  private final SaxRecorder.Player stylesheetPlayer;
  private final EntityResolver entityResolver ;
  private final URIResolver uriResolver ;

  public XslPageIdentifierExtractor(
      final EntityResolver entityResolver,
      final URIResolver uriResolver,
      final SaxRecorder.Player stylesheetPlayer
  ) {
    this.entityResolver = checkNotNull( entityResolver ) ;
    this.uriResolver = checkNotNull( uriResolver ) ;
    this.stylesheetPlayer = stylesheetPlayer ;
  }


  @Override
  public ImmutableMap<PageIdentifier, String > extractPages(
      final SyntacticTree documentTree
  )
      throws Exception
  {

    if( stylesheetPlayer == null ) {
      return PagesExtractor.EMPTY_MAP ;
    }

    final TransformerErrorListener transformerErrorListener = new TransformerErrorListener() ;
    
    final XslTransformerFactory xslTransformerFactory = new XslTransformerFactory.FromPlayer(
        stylesheetPlayer,
        entityResolver,
        uriResolver,
        transformerErrorListener
    ) ;
    
    transformerErrorListener.flush() ;

    final TransformerHandler transformerHandler = xslTransformerFactory.newTransformerHandler() ;
    transformerHandler.getTransformer().setErrorListener( transformerErrorListener ) ;

    final XmlMultipageReader multipageReader = new XmlMultipageReader() ;
    transformerHandler.setResult( new SAXResult( multipageReader ) );

    final XmlWriter xmlWriter = new XmlWriter( RenditionMimeType.XML ) {
      @Override
      protected ContentHandler createContentHandler(
          final OutputStream outputStream,
          final DocumentMetadata documentMetadata,
          final Charset charset
      ) throws Exception {
        return transformerHandler ;
      }
    } ;

    final GenericRenderer renderer = new GenericRenderer( xmlWriter ) ;

    renderer.renderTree( documentTree, new NullOutputStream(), null, null, null  ) ;

    transformerErrorListener.flush() ;

    return multipageReader.getPageIdentifiers() ;
  }
}

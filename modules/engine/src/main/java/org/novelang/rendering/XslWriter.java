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

package org.novelang.rendering;

import java.io.OutputStream;
import java.nio.charset.Charset;

import com.google.common.collect.ImmutableList;
import javax.xml.transform.Transformer;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;
import org.novelang.common.metadata.DocumentMetadata;
import org.novelang.configuration.RenderingConfiguration;
import org.novelang.outfit.loader.ResourceLoader;
import org.novelang.outfit.loader.ResourceName;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.xml.EntityEscapeSelector;
import org.novelang.outfit.xml.LocalEntityResolver;
import org.novelang.outfit.xml.LocalUriResolver;
import org.novelang.outfit.xml.SaxMulticaster;
import org.novelang.outfit.xml.XmlNamespaces;
import org.novelang.outfit.xml.XslTransformerFactory;
import org.novelang.parser.NodeKindTools;
import org.novelang.rendering.xslt.validate.SaxConnectorForVerifier;
import org.novelang.outfit.DefaultCharset;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Writes XML basing on an XSLT stylesheet.
 *
 * @author Laurent Caillette
 */
public class XslWriter extends XmlWriter {

  private static final Logger LOGGER = LoggerFactory.getLogger( XslWriter.class ) ;

  protected final EntityResolver entityResolver;
  protected final URIResolver uriResolver;
  protected static final RenditionMimeType DEFAULT_RENDITION_MIME_TYPE = RenditionMimeType.XML ;

  protected final ResourceName xslFileName ;
  protected final ResourceLoader resourceLoader ;
  protected final EntityEscapeSelector entityEscapeSelector ;
  private static final ResourceName IDENTITY_XSL_FILE_NAME = new ResourceName( "identity.xsl" ) ;

  public XslWriter( final RenderingConfiguration configuration, final ResourceName xslFileName ) {
    this( configuration, xslFileName, DefaultCharset.RENDERING, DEFAULT_RENDITION_MIME_TYPE ) ;
  }

  public XslWriter(
      final String namespaceUri,
      final String nameQualifier,
      final RenderingConfiguration configuration,
      final ResourceName xslFileName
  ) {
    this(
        namespaceUri,
        nameQualifier,
        configuration,
        xslFileName,
        DEFAULT_RENDITION_MIME_TYPE
    ) ;
  }

  public XslWriter(
      final String namespaceUri,
      final String nameQualifier,
      final RenderingConfiguration configuration,
      final ResourceName xslFileName,
      final RenditionMimeType mimeType
  ) {
    this(
        namespaceUri,
        nameQualifier,
        configuration,
        xslFileName,
        DefaultCharset.RENDERING,
        mimeType,
        NO_ENTITY_ESCAPE
    ) ;
  }

  public XslWriter(
      final RenderingConfiguration configuration,
      final ResourceName xslFileName,
      final Charset charset,
      final RenditionMimeType mimeType
  ) {
    this( configuration, xslFileName, charset, mimeType, NO_ENTITY_ESCAPE ) ;
  }

  public XslWriter(
      final RenderingConfiguration configuration,
      final ResourceName xslFileName,
      final Charset charset,
      final RenditionMimeType mimeType,
      final EntityEscapeSelector entityEscapeSelector
  ) {
    this(
        XmlNamespaces.TREE_NAMESPACE_URI,
        XmlNamespaces.TREE_NAME_QUALIFIER,
        configuration,
        xslFileName,
        charset,
        mimeType,
        entityEscapeSelector
    ) ;
  }

  public XslWriter(
      final String namespaceUri,
      final String nameQualifier,
      final RenderingConfiguration configuration,
      final ResourceName xslFileName,
      final Charset charset,
      final RenditionMimeType mimeType,
      final EntityEscapeSelector entityEscapeSelector
  ) {
    super( namespaceUri, nameQualifier, charset, mimeType ) ;
    this.entityEscapeSelector = checkNotNull( entityEscapeSelector ) ;
    this.resourceLoader = checkNotNull( configuration.getResourceLoader() ) ;

    final ResourceName safeXslFileName;
    if( null == xslFileName ) {
      safeXslFileName = IDENTITY_XSL_FILE_NAME;
    } else {
      safeXslFileName = xslFileName ;
    }
    this.xslFileName = safeXslFileName ;
    entityResolver = new LocalEntityResolver( resourceLoader, entityEscapeSelector ) ;
    uriResolver = new LocalUriResolver( resourceLoader, entityResolver ) {
      @Override
      protected ImmutableList< ContentHandler > createAdditionalContentHandlers() {
        return XslWriter.createAdditionalContentHandlers() ;
      }

    } ;
    LOGGER.debug( "Created ", getClass().getName(), " with stylesheet ", safeXslFileName ) ;
  }


  @Override
  protected ContentHandler createContentHandler(
      final OutputStream outputStream,
      final DocumentMetadata documentMetadata,
      final Charset charset
  )
      throws Exception
  {
    LOGGER.debug( "Creating ContentHandler with charset ", charset.name() );

    final TransformerHandler transformerHandler = new XslTransformerFactory.FromResource(
        resourceLoader,
        xslFileName,
        entityResolver,
        uriResolver,
        createAdditionalContentHandlers()
    ).newTransformerHandler() ;
    
    configure( transformerHandler.getTransformer(), documentMetadata ) ;

    final ContentHandler sinkContentHandler =
        createSinkContentHandler( outputStream, documentMetadata, charset ) ;
    transformerHandler.setResult( new SAXResult( sinkContentHandler ) ) ;

    return transformerHandler ;

  }

  private static void configure(
      final Transformer transformer,
      final DocumentMetadata documentMetadata
  ) {
    transformer.setParameter(
        "timestamp",
        documentMetadata.getCreationTimestamp()
    ) ;
    transformer.setParameter(
        "charset",
        documentMetadata.getCharset().name()
    ) ;
  }

  /**
   * Hook for letting subclasses post-process XSL output.
   */
  protected ContentHandler createSinkContentHandler(
      final OutputStream outputStream,
      final DocumentMetadata documentMetadata,
      final Charset charset
  ) throws Exception
  {
    return super.createContentHandler( outputStream, documentMetadata, charset ) ;
  }

  private static final EntityEscapeSelector NO_ENTITY_ESCAPE = new EntityEscapeSelector() {
    @Override
    public boolean shouldEscape( final String publicId, final String systemId ) {
      return false ;
    }
  } ;

  private static SaxMulticaster connectXpathVerifier( final TemplatesHandler templatesHandler ) {
    return new SaxMulticaster( templatesHandler, createAdditionalContentHandlers() ) ;
  }

  private static ImmutableList< ContentHandler > createAdditionalContentHandlers() {
    return ImmutableList.< ContentHandler >of( new SaxConnectorForVerifier(
        XmlNamespaces.TREE_NAMESPACE_URI, NodeKindTools.getRenderingNames() ) ) ;
  }




}

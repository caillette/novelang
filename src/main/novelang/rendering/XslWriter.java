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

package novelang.rendering;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import novelang.configuration.RenderingConfiguration;
import novelang.loader.ResourceLoader;
import novelang.model.common.TreeMetadata;

/**
 * @author Laurent Caillette
 */
public class XslWriter extends XmlWriter {

  protected static final Logger LOGGER = LoggerFactory.getLogger( XslWriter.class ) ;

  protected final EntityResolver entityResolver;
  protected static final RenditionMimeType DEFAULT_RENDITION_MIME_TYPE = RenditionMimeType.XML ;

  protected final String xslFileName ;
  protected final ResourceLoader resourceLoader;




  public XslWriter( RenderingConfiguration configuration, String xslFileName ) {
    this( configuration, xslFileName, DEFAULT_RENDITION_MIME_TYPE ) ;
  }

  public XslWriter(
      RenderingConfiguration configuration,
      String xslFileName,
      RenditionMimeType mimeType
  ) {
    super( mimeType ) ;
    this.resourceLoader = configuration.getResourceLoader() ;
    this.xslFileName = xslFileName ;
    entityResolver = new LocalEntityResolver() ;

  }

  protected final ContentHandler createContentHandler(
      OutputStream outputStream,
      TreeMetadata treeMetadata,
      Charset encoding
  )
      throws Exception
  {
    final SAXTransformerFactory saxTransformerFactory =
        ( SAXTransformerFactory ) TransformerFactory.newInstance() ;

    final TemplatesHandler templatesHandler = saxTransformerFactory.newTemplatesHandler() ;

    final XMLReader reader = XMLReaderFactory.createXMLReader() ;
    reader.setContentHandler( templatesHandler ) ;
    reader.setEntityResolver( entityResolver ) ;
    reader.parse( new InputSource( resourceLoader.getInputStream( xslFileName ) ) ) ;

    final Templates templates = templatesHandler.getTemplates() ;
    final TransformerHandler transformerHandler =
        saxTransformerFactory.newTransformerHandler( templates ) ;
    configure( transformerHandler.getTransformer(), treeMetadata ) ;

    final ContentHandler sinkContentHandler =
        createSinkContentHandler( outputStream, treeMetadata, encoding ) ;
    transformerHandler.setResult( new SAXResult( sinkContentHandler ) ) ;

    return transformerHandler ;

  }

  private void configure( Transformer transformer, TreeMetadata treeMetadata ) {
    transformer.setParameter(
        "timestamp",
        treeMetadata.getCreationTimestampAsString()
    ) ;
    transformer.setParameter(
        "wordcount",
        treeMetadata.getWordCount()
    ) ;
    transformer.setParameter(
        "encoding",
        treeMetadata.getEncoding().name()
    ) ;
  }

  protected ContentHandler createSinkContentHandler(
      OutputStream outputStream,
      TreeMetadata treeMetadata,
      Charset encoding
  ) throws Exception
  {
    return super.createContentHandler( outputStream, treeMetadata, encoding ) ;
  }

  /**
   * Fetches local files in the same directory as the stylesheet.
   * This is because the {@code systemId} as read by the stylesheet loader is prefixed
   * with current directory (bug?).
   */
  protected class LocalEntityResolver implements EntityResolver {

    public InputSource resolveEntity(
        String publicId,
        String systemId
    ) throws SAXException, IOException {
      systemId = systemId.substring( systemId.lastIndexOf( "/" ) + 1 ) ;
      LOGGER.debug(
          "Resolving entity publicId='{}' systemId='{}'", publicId, systemId ) ;
      final InputSource inputSource = new InputSource( resourceLoader.getInputStream( systemId ) ) ;
      return inputSource ;
    }
  }
}

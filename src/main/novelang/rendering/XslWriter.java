/*
 * Copyright (C) 2006 Laurent Caillette
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
package novelang.rendering;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import novelang.model.common.TreeMetadata;

/**
 * @author Laurent Caillette
 */
public class XslWriter extends XmlWriter {

  protected static final Logger LOGGER = LoggerFactory.getLogger( PdfWriter.class ) ;
  protected static final String NOVELANG_STYLES_DIR = "novelang.styles.dir" ;
  protected static final EntityResolver ENTITY_RESOLVER ;
  protected static final URL STYLES_DIR ;
  protected static final RenditionMimeType DEFAULT_RENDITION_MIME_TYPE = RenditionMimeType.XML ;
  protected final URL stylesheet ;
  private static final String BUNDLED_STYLES_DIR = "/style/";


  static {
    final String stylesDirName = System.getProperty( NOVELANG_STYLES_DIR ) ;
    if( StringUtils.isBlank( stylesDirName ) ) {
      LOGGER.debug( "No directory set for styles" ) ;
      STYLES_DIR = null ;
    } else {
      final File dir = new File( stylesDirName ) ;
      if( dir.exists() ) {
        try {
          STYLES_DIR = dir.getCanonicalFile().toURL() ;
        } catch( IOException e ) {
          throw new RuntimeException( e );
        }
        LOGGER.info( "Styles directory set to '{}'", STYLES_DIR.toExternalForm() ) ;
      } else {
        STYLES_DIR = null ;
        LOGGER.warn( "Styles directory '{}' does not exist", stylesDirName ) ;
      }
    }
    ENTITY_RESOLVER = new LocalEntityResolver() ;
  }


  public XslWriter( String xslFileName ) {
    this( xslFileName, DEFAULT_RENDITION_MIME_TYPE ) ;
  }

  public XslWriter( String xslFileName, RenditionMimeType mimeType ) {
    super( mimeType ) ;
    if( null == STYLES_DIR ) {
      this.stylesheet = getClass().getResource( BUNDLED_STYLES_DIR + xslFileName ) ;
    } else {
      try {
        this.stylesheet = new URL( STYLES_DIR, xslFileName ) ;
      } catch( MalformedURLException e ) {
        throw new RuntimeException( e ) ;
      }
    }
    LOGGER.info( "Using stylesheet '{}'", stylesheet.toExternalForm() ) ;
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
    reader.setEntityResolver( ENTITY_RESOLVER ) ;
    reader.parse( new InputSource( stylesheet.openStream() ) ) ;

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
  protected static class LocalEntityResolver implements EntityResolver {

    public InputSource resolveEntity(
        String publicId,
        String systemId
    ) throws SAXException, IOException {
      systemId = systemId.substring( systemId.lastIndexOf( "/" ) + 1 ) ;
      LOGGER.debug(
          "Attempting to resolve entity publicId='{}' systemId='{}'", publicId, systemId ) ;
      final URL entityUrl = XslWriter.class.getResource( BUNDLED_STYLES_DIR + systemId ) ;
      final InputSource inputSource = new InputSource( entityUrl.openStream() ) ;
      LOGGER.debug( "Resolved entity '{}'", entityUrl.toExternalForm() ) ;
      return inputSource ;
    }
  }
}

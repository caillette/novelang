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
  protected static final File STYLES_DIR ;
  protected static final RenditionMimeType DEFAULT_RENDITION_MIME_TYPE = RenditionMimeType.XML ;
  protected final File stylesheet ; // TODO use URL like for LocalEntityResolver


  static {
    final String stylesDirName = System.getProperty( NOVELANG_STYLES_DIR ) ;
    if( StringUtils.isBlank( stylesDirName ) ) {
      LOGGER.debug( "No directory set for styles" ) ;
      throw new RuntimeException( // TODO load resource.
          "No default stylesheet supported yet, set -D" + NOVELANG_STYLES_DIR + " instead" ) ;
    } else {
      final File dir = new File( stylesDirName ) ;
      if( dir.exists() ) {
        try {
          STYLES_DIR = dir.getCanonicalFile() ;
        } catch( IOException e ) {
          throw new RuntimeException( e );
        }
        LOGGER.info( "Styles directory set to '{}'", STYLES_DIR.getAbsolutePath() ) ;
      } else {
        STYLES_DIR = null ;
        LOGGER.warn( "Styles directory '{}' does not exist", STYLES_DIR.getAbsolutePath() ) ;
      }
    }
    try {
      ENTITY_RESOLVER = new LocalEntityResolver( STYLES_DIR.toURL().toExternalForm() ) ;
    } catch( MalformedURLException e ) {
      throw new RuntimeException( e );
    }
  }


  protected XslWriter( File stylesheet, RenditionMimeType mimeType ) {
    super( mimeType ) ;
    this.stylesheet = stylesheet ;
    LOGGER.info( "Using stylesheet '{}'", stylesheet.getAbsolutePath() ) ;
  }

  public XslWriter( String xslFileName ) {
    this( new File( STYLES_DIR, xslFileName ), DEFAULT_RENDITION_MIME_TYPE ) ;
  }

  public XslWriter( String xslFileName, RenditionMimeType mimeType ) {
    this( new File( STYLES_DIR, xslFileName ), mimeType ) ;
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
    reader.parse( new InputSource( new FileReader( stylesheet ) ) ) ;

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

    private final String resourcePrefix ;

    public LocalEntityResolver( String resourcePrefix ) {
      this.resourcePrefix = resourcePrefix ;
    }

    public InputSource resolveEntity(
        String publicId,
        String systemId
    ) throws SAXException, IOException {
      systemId = systemId.substring( systemId.lastIndexOf( "/" ) + 1 ) ;
      LOGGER.debug(
          "Attempting to resolve entity publicId='{} systemId='{}'", publicId, systemId ) ;
      final URL entityUrl = new URL( resourcePrefix + systemId ) ;
      final InputSource inputSource = new InputSource( entityUrl.openStream() ) ;
      LOGGER.debug( "Resolved entity '{}'", entityUrl.toExternalForm() ) ;
      return inputSource ;
    }
  }
}

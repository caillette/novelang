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
package novelang.rendering;

import java.io.File;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.net.URL;
import java.net.MalformedURLException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Templates;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.apps.FOPException;
import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.XMLReaderFactory;
import com.google.common.base.Objects;

/**
 * @author Laurent Caillette
 */
public class PdfWriter extends XmlWriter {

  private static final Logger LOGGER = LoggerFactory.getLogger( PdfWriter.class ) ;
  private static final String NOVELANG_STYLES_DIR = "novelang.styles.dir" ;
  private static final String DEFAULT_FO_STYLESHEET = /*"identity.xsl"*/ "fo.xsl" ;
  private static final EntityResolver ENTITY_RESOLVER ;
  private static final File STYLES_DIR ;

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

  private final File stylesheet ; // TODO use URL like for LocalEntityResolver

  public PdfWriter() {
    stylesheet = new File( STYLES_DIR, DEFAULT_FO_STYLESHEET ) ;
    LOGGER.info( "Loaded stylesheet from '{}'", stylesheet.getAbsolutePath() ) ;
  }

  public RenditionMimeType getMimeType() {
    return forcedMimeType ;
  }

// ================
// Debug properties
// ================

  private RenditionMimeType forcedMimeType = RenditionMimeType.PDF ;

  public void setForcedMimeType( RenditionMimeType forcedMimeType ) {
    this.forcedMimeType = Objects.nonNull( forcedMimeType ) ;
  }

  private boolean applyFop = true ;

  public void setApplyFop( boolean applyFop ) {
    this.applyFop = applyFop;
  }

// ==========
// Generation
// ==========

  /**
   * Creates a {@code ContentHandler} piped to a stylesheet producing Formatting Objects (FO)
   * to the PDF generator (Apache FOP).
   * When {@link #applyFop} is set to true, formatted XML is produced instead.
   */
  protected ContentHandler createContentHandler( OutputStream outputStream, Charset encoding )
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

    final ContentHandler sinkContentHandler ;

    if( applyFop ) {
      sinkContentHandler = createFopContentHandler( outputStream ) ;
    } else {
      sinkContentHandler = super.createContentHandler( outputStream, encoding ) ;
    }

    transformerHandler.setResult( new SAXResult( sinkContentHandler ) ) ;

    return transformerHandler ;

  }

  private final ContentHandler createFopContentHandler( OutputStream outputStream )
      throws FOPException
  {
    final FopFactory fopFactory = FopFactory.newInstance() ;
    final FOUserAgent foUserAgent = fopFactory.newFOUserAgent() ;

    final Fop fop ;
    try {
      fop = fopFactory.newFop( MimeConstants.MIME_PDF, foUserAgent, outputStream ) ;
    } catch( FOPException e ) {
      throw new RuntimeException( e );
    }

    return fop.getDefaultHandler() ;

  }

  /**
   * Fetches local files in the same directory as the stylesheet.
   * This is because the {@code systemId} as read by the stylesheet loader is prefixed
   * with current directory (bug?).
   */
  private static class LocalEntityResolver implements EntityResolver {

    private final String resourcePrefix ;

    public LocalEntityResolver( String resourcePrefix ) {
      this.resourcePrefix = resourcePrefix ;
    }

    public InputSource resolveEntity(
        String publicId,
        String systemId
    ) throws SAXException, IOException {
      systemId = systemId.substring( systemId.lastIndexOf( "/" ) + 1 ) ;      
      LOGGER.debug( "Attempting to resolve entity publicId='{} systemId='{}'", publicId, systemId ) ;
      final URL entityUrl = new URL( resourcePrefix + systemId ) ;
      final InputSource inputSource = new InputSource( entityUrl.openStream() ) ;
      LOGGER.debug( "Resolved entity '{}'", entityUrl.toExternalForm() ) ;
      return inputSource ;
    }
  }


}

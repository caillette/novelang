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
package novelang.renderer;

import java.io.File;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileReader;
import java.nio.charset.Charset;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Templates;
import javax.xml.transform.sax.SAXSource;
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
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Laurent Caillette
 */
public class PdfRenderer extends XmlRenderer {

  private static final String NOVELANG_STYLES_DIR = "novelang.styles.dir" ;
  private static final String DEFAULT_FO_STYLESHEET = "identity.xsl" ;

  private static final Logger LOGGER = LoggerFactory.getLogger( PdfRenderer.class ) ;

  private static final File stylesDir ;
  static {
    final String stylesDirName = System.getProperty( NOVELANG_STYLES_DIR ) ;
    if( StringUtils.isBlank( stylesDirName ) ) {
      stylesDir = null ;
      LOGGER.debug( "No directory set for styles" ) ;
    } else {
      final File dir = new File( stylesDirName ) ;
      if( dir.exists() ) {
        stylesDir = dir ;
        LOGGER.info( "Styles directory set to '{}'", stylesDir.getAbsolutePath() ) ;
      } else {
        stylesDir = null ;
        LOGGER.warn( "Styles directory '{}' does not exist", stylesDir.getAbsolutePath() ) ;
      }
    }
  }

  private final File stylesheet ;

  public PdfRenderer() {
    if( null == stylesDir ) {
      throw new RuntimeException(
          "No default stylesheet supported yet, set -D" + NOVELANG_STYLES_DIR + " instead") ;
    }
    stylesheet = new File( stylesDir, DEFAULT_FO_STYLESHEET ) ;
  }


  public String getMimeType() {
//    return "application/pdf" ;
    return "text/plain" ;
  }

  protected ContentHandler createContentHandler( OutputStream outputStream, Charset encoding )
      throws Exception
  {

    final SAXTransformerFactory saxTransformerFactory =
        ( SAXTransformerFactory ) TransformerFactory.newInstance() ;

    final TemplatesHandler templatesHandler = saxTransformerFactory.newTemplatesHandler() ;

    final XMLReader reader = XMLReaderFactory.createXMLReader() ;
    reader.setContentHandler( templatesHandler ) ;
    reader.parse( new InputSource( new FileReader( stylesheet ) ) ) ;

    final Templates templates = templatesHandler.getTemplates() ;
    final TransformerHandler transformerHandler =
        saxTransformerFactory.newTransformerHandler( templates ) ;

    final ContentHandler sinkContentHandler ;

    // Useful to see the transformation result.
    sinkContentHandler = super.createContentHandler( outputStream, encoding ) ;
//    sinkContentHandler = createFopContentHandler( outputStream ) ;

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
}

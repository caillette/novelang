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
import javax.xml.transform.Transformer;
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
import novelang.model.common.TreeMetadata;
import novelang.configuration.RenderingConfiguration;

/**
 * @author Laurent Caillette
 */
public class PdfWriter extends XslWriter {

  protected static final String DEFAULT_FO_STYLESHEET =  "pdf.xsl" ; 

  public PdfWriter( RenderingConfiguration configuration ) {
    super( configuration, DEFAULT_FO_STYLESHEET ) ;
  }


// ==========
// Generation
// ==========

  protected final ContentHandler createSinkContentHandler(
      OutputStream outputStream,
      TreeMetadata treeMetadata,
      Charset encoding
  )
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

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
package org.novelang.daemon;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.SystemUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;
import static org.junit.Assert.assertTrue;

import org.novelang.common.filefixture.Resource;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.DefaultCharset;
import org.novelang.rendering.RenditionMimeType;

/**
 * Collection of methods and small classes for tests.
 *
 * @author Laurent Caillette
 */
/*package*/ class HttpDaemonFixture {

  private static final Logger LOGGER = LoggerFactory.getLogger( HttpDaemonFixture.class ) ;
  
  public static final int TIMEOUT = 5000 ;

  public static final String PDF = "." + RenditionMimeType.PDF.getFileExtension() ;

  public  static final String HTML = "." + RenditionMimeType.HTML.getFileExtension() ;

  public static final String CAMINO_USER_AGENT =
      "Mozilla/5.0 (Macintosh; U; Intel Mac OS X; en; rv:1.8.1.14) " +
      "Gecko/20080512 Camino/1.6.1 (like Firefox/2.0.0.14)"
  ;

  public static final String SAFARI_USER_AGENT =
      "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_4_11; en) " +
      "AppleWebKit/525.18 (KHTML, like Gecko) " +
      "Version/3.1.2 Safari/525.22"
  ;
  public static final String DEFAULT_USER_AGENT = CAMINO_USER_AGENT ;

  public static final Charset DEFAULT_PLATFORM_CHARSET = Charset.forName( SystemUtils.FILE_ENCODING ) ;


  private HttpDaemonFixture() { }

  public static String extractPdfText( final byte[] pdfBytes ) throws IOException {
    final PDDocument pdfDocument = PDDocument.load( new ByteArrayInputStream( pdfBytes ) ) ;
    try {
      return new PDFTextStripper().getText( pdfDocument ) ;
    } finally {
      pdfDocument.close() ;
    }
  }

  private static final Pattern STRIP_COMMENTS_PATTERN = Pattern.compile( "%.*\\n" ) ;

  /**
   * Removes comments in novella/opus format.
   */
  public static String shaveComments( final String s ) {
    final Matcher matcher = STRIP_COMMENTS_PATTERN.matcher( s ) ;
    final StringBuffer buffer = new StringBuffer() ;
    while( matcher.find() ) {
      matcher.appendReplacement( buffer, "" ) ;
    }
    matcher.appendTail( buffer ) ;
    return buffer.toString() ;
  }

  protected static void checkDirectoryListing(
      final ResponseSnapshot responseSnapshot ,
      final Resource resource
  ) {
    final String fullPath = resource.getFullPath().substring( 1 ) ; // Remove leading solidus.
    final String filePath = fullPath + resource.getBaseName() + ".html" ;

    LOGGER.debug( "fullpath='", fullPath, "'" ) ;
    LOGGER.debug( "filepath='", filePath, "'" ) ;
    LOGGER.debug( "Checking response body: \n", responseSnapshot.getContent() ) ;

    final String expectedFullPath = "<a href=\"" + fullPath + "\">" + fullPath + "</a>" ;
    LOGGER.debug( "Expected fullPath='", expectedFullPath, "'" ) ;

    assertTrue( responseSnapshot.getContent().contains( expectedFullPath ) ) ;
    assertTrue( responseSnapshot.getContent()
        .contains( "<a href=\"" + filePath + "\">" + filePath + "</a>" ) ) ;
  }

  /**
   * We need to read several values from an {@link org.apache.http.HttpResponse} so it would be convenient
   * to use it as return type for {@link HttpDaemonSupport#followRedirection(String, String)}
   * but it's impossible to read the streamable content more than once.
   * We turn this by keeping a snapshot of everything needed.
   */
  protected static class ResponseSnapshot {

    private final String content ;
    private final List< Header > locationsRedirectedTo ;

    public ResponseSnapshot(
        final HttpResponse httpResponse,
        final List< Header > locationsRedirectedTo
    ) throws IOException {
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream() ;
      httpResponse.getEntity().writeTo( outputStream ) ;
      content = new String( outputStream.toByteArray(), DefaultCharset.RENDERING.name() ) ;
      this.locationsRedirectedTo = locationsRedirectedTo ;
    }

    public String getContent() {
      return content ;
    }

    public List< Header > getLocationsRedirectedTo() {
      return locationsRedirectedTo ;
    }
  }

}

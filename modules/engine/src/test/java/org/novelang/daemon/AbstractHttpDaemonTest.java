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
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.junit.Rule;
import org.novelang.ResourceTools;
import org.novelang.ResourcesForTests;
import org.novelang.common.filefixture.Directory;
import org.novelang.common.filefixture.Resource;
import org.novelang.common.filefixture.ResourceInstaller;
import org.novelang.configuration.ConfigurationTools;
import org.novelang.configuration.parse.DaemonParameters;
import org.novelang.configuration.parse.GenericParametersConstants;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.DefaultCharset;
import org.novelang.outfit.TcpPortBooker;
import org.novelang.outfit.loader.CompositeResourceLoader;
import org.novelang.produce.DocumentRequest;
import org.novelang.rendering.RenditionMimeType;
import org.novelang.testing.junit.MethodSupport;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Base class for tests with {@link HttpDaemon}.
 * TODO make this become one {@link org.junit.rules.MethodRule}.
 *
 * @author Laurent Caillette
 */
public abstract class AbstractHttpDaemonTest {

  static {
    ResourcesForTests.initialize() ;
  }

  private static final Logger LOGGER = LoggerFactory.getLogger( HttpDaemonTest.class ) ;

  protected static final Charset ISO_8859_1 = Charset.forName( "ISO_8859_1" );
  private static final int TIMEOUT = 5000 ;
  private static final Pattern STRIP_COMMENTS_PATTERN = Pattern.compile( "%.*\\n" ) ;
  protected static final String PDF = "." + RenditionMimeType.PDF.getFileExtension() ;
  protected static final String HTML = "." + RenditionMimeType.HTML.getFileExtension() ;

  @SuppressWarnings( { "InstanceVariableMayNotBeInitialized" } )
  private HttpDaemon httpDaemon ;

  @Rule
  public final MethodSupport methodSupport ;

  protected AbstractHttpDaemonTest() {
    final Object methodSupportLock = getMethodSupportLock() ;
    methodSupport = methodSupportLock == null ? new DaemonMethodSupport()
        : new DaemonMethodSupport( methodSupportLock ) ;
    resourceInstaller = new ResourceInstaller( methodSupport ) ;
  }

  private class DaemonMethodSupport extends MethodSupport {

    private DaemonMethodSupport() {
    }

    private DaemonMethodSupport( final Object executionLock ) {
      super( executionLock ) ;
    }

    @Override
    protected void afterStatementEvaluation() throws Exception {
      httpDaemon.stop() ;
    }
  }


  protected Object getMethodSupportLock() {
    return null ;
  }

  protected final ResourceInstaller resourceInstaller;
  
  protected final int daemonPort = TcpPortBooker.THIS.find() ;

  private static final String CAMINO_USER_AGENT =
      "Mozilla/5.0 (Macintosh; U; Intel Mac OS X; en; rv:1.8.1.14) " +
      "Gecko/20080512 Camino/1.6.1 (like Firefox/2.0.0.14)"
  ;

  protected static final String SAFARI_USER_AGENT =
      "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_4_11; en) " +
      "AppleWebKit/525.18 (KHTML, like Gecko) " +
      "Version/3.1.2 Safari/525.22"
  ;

  private static final String DEFAULT_USER_AGENT = CAMINO_USER_AGENT ;

  protected static String shaveComments( final String s ) {
    final Matcher matcher = STRIP_COMMENTS_PATTERN.matcher( s ) ;
    final StringBuffer buffer = new StringBuffer() ;
    while( matcher.find() ) {
      matcher.appendReplacement( buffer, "" ) ;
    }
    matcher.appendTail( buffer ) ;
    return buffer.toString() ;
  }

  protected URL buildUrl( final DocumentRequest documentRequest )
      throws MalformedURLException
  {
    return new URL( "http://localhost:" + daemonPort + documentRequest.getOriginalTarget() ) ;
  }

  protected static String readAsString( final URL url ) throws IOException {
    final StringWriter stringWriter = new StringWriter() ;
    IOUtils.copy( url.openStream(), stringWriter ) ;
    return stringWriter.toString() ;
  }

  protected static byte[] readAsBytes( final URL url ) throws IOException {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream() ;
    IOUtils.copy( url.openStream(), outputStream ) ;
    return outputStream.toByteArray() ;
  }

  protected void save( final String name, final String document ) throws IOException {
    final File file = new File( resourceInstaller.getTargetDirectory(), name ) ;
    FileUtils.writeStringToFile( file, document ) ;
    LOGGER.info( "Wrote file '", file.getAbsolutePath(), "'" ) ;
  }

  protected void save( final String name, final byte[] document ) throws IOException {
    save( new File( resourceInstaller.getTargetDirectory(), name ), document ) ;
  }

  private static void save( final File file, final byte[] document ) throws IOException {
    FileUtils.writeByteArrayToFile( file, document ) ;
    LOGGER.info( "Wrote file '", file.getAbsolutePath(), "'" ) ;
  }

  protected static void save( final File file, final URL documentUrl ) throws IOException {
    FileUtils.writeByteArrayToFile( file, readAsBytes( documentUrl ) ) ;
    LOGGER.info( "Wrote file '", file.getAbsolutePath(), "'" ) ;
  }

  protected final void setup() throws Exception {
    daemonSetup( ISO_8859_1 ) ;
  }

  protected final String setup( final Resource resource ) throws Exception {
    resourceInstaller.copy( resource ) ;
    daemonSetup( DefaultCharset.RENDERING ) ;
    final String novellaSource = resource.getAsString( DefaultCharset.SOURCE ) ;
    return novellaSource ;
  }

  protected final void setup(
      final File styleDirectory,
      final Charset renderingCharset
  ) throws Exception {
    daemonSetup( styleDirectory, renderingCharset ) ;
  }

  protected final String alternateSetup(
      final Resource resource,
      final Charset renderingCharset
  ) throws Exception {
    resourceInstaller.copy( resource ) ;
    daemonSetup( renderingCharset ) ;
    final String novellaSource = resource.getAsString( DefaultCharset.SOURCE ) ;
    return novellaSource ;
  }

  private void daemonSetup( final File styleDirectory, final Charset renderingCharset )
      throws Exception
  {
    httpDaemon = new HttpDaemon( ResourceTools.createDaemonConfiguration(
        daemonPort,
        resourceInstaller.getTargetDirectory(),
        CompositeResourceLoader.create( ConfigurationTools.BUNDLED_STYLE_DIR, styleDirectory )
    ) ) ;
    httpDaemon.start() ;
  }

  private void daemonSetup( final Charset renderingCharset )
      throws Exception
  {
    httpDaemon = new HttpDaemon( ResourceTools.createDaemonConfiguration(
        daemonPort,
        resourceInstaller.getTargetDirectory(),
        renderingCharset
    ) ) ;
    httpDaemon.start() ;
  }

  protected final void daemonSetupWithFonts( final Directory fontDirectory )
      throws Exception
  {
    final File directoryAsFile = resourceInstaller.copy( fontDirectory ) ;


    final DaemonParameters daemonParameters = new DaemonParameters(
        resourceInstaller.getTargetDirectory(),
        GenericParametersConstants.OPTIONPREFIX + DaemonParameters.OPTIONNAME_HTTPDAEMON_PORT,
        "" + daemonPort,
        GenericParametersConstants.OPTIONPREFIX + GenericParametersConstants.OPTIONNAME_FONT_DIRECTORIES,
        directoryAsFile.getAbsolutePath()
    ) ;

    httpDaemon = new HttpDaemon(
        ConfigurationTools.createDaemonConfiguration( daemonParameters ) ) ;

    httpDaemon.start() ;
  }

  protected final ResponseSnapshot followRedirection( final String originalUrlAsString )
      throws IOException
  {
    return followRedirection( originalUrlAsString, DEFAULT_USER_AGENT ) ;
  }

  /**
   * Follows redirection using {@link org.apache.http.client.HttpClient}'s default, and returns response body.
   */
  protected final ResponseSnapshot followRedirection(
      final String originalUrlAsString,
      final String userAgent
  ) throws IOException {

    final List< Header > locationsRedirectedTo = Lists.newArrayList() ;

    final AbstractHttpClient httpClient = new DefaultHttpClient() ;

    httpClient.setRedirectHandler( new RecordingRedirectHandler( locationsRedirectedTo ) ) ;
    final HttpParams parameters = new BasicHttpParams() ;
    parameters.setIntParameter( CoreConnectionPNames.SO_TIMEOUT, TIMEOUT ) ;
    final HttpGet httpGet = new HttpGet( originalUrlAsString ) ;
    httpGet.setHeader( "User-Agent", userAgent ) ;
    httpGet.setParams( parameters ) ;
    final HttpResponse httpResponse = httpClient.execute( httpGet ) ;

    final ResponseSnapshot responseSnapshot =
        new ResponseSnapshot( httpResponse, locationsRedirectedTo ) ;
    save( "generated.html", responseSnapshot.getContent() ) ;

    return responseSnapshot ;
  }

  protected static void checkDirectoryListing(
      final ResponseSnapshot responseSnapshot ,
      final Resource resource
  ) throws IOException {
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

  protected final void renderAndCheckStatusCode(
      final Resource resource,
      final String savedFileName
  )
      throws IOException
  {
    final HttpGet httpGet = new HttpGet(
        "http://localhost:" + daemonPort + "/" + resource.getBaseName() + PDF ) ;
    final HttpResponse httpResponse = new DefaultHttpClient().execute( httpGet ) ;

    final ByteArrayOutputStream responseContent = new ByteArrayOutputStream() ;
    IOUtils.copy( httpResponse.getEntity().getContent(), responseContent ) ;
    save( savedFileName, responseContent.toByteArray() ) ;
    final int statusCode = httpResponse.getStatusLine().getStatusCode();
    assertEquals( ( long ) HttpStatus.SC_OK, ( long ) statusCode ) ;
  }

  protected static String extractPdfText( final byte[] pdfBytes ) throws IOException {
    final PDDocument pdfDocument = PDDocument.load( new ByteArrayInputStream( pdfBytes ) ) ;
    try {
      return new PDFTextStripper().getText( pdfDocument ) ;
    } finally {
      pdfDocument.close() ;
    }
  }

  /**
   * We need to read several values from an {@link org.apache.http.HttpResponse} so it would be convenient
   * to use it as return type for {@link org.novelang.daemon.HttpDaemonTest#followRedirection(String, String)}
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

  private static class RecordingRedirectHandler extends DefaultRedirectHandler {

    private final List< Header > locations ;

    public RecordingRedirectHandler( final List< Header > locations ) {
      this.locations = locations ;
    }

    @Override
    public URI getLocationURI( final HttpResponse response, final HttpContext context )
        throws ProtocolException
    {
      locations.addAll( Arrays.asList( response.getHeaders( "Location" ) ) ) ;
      return super.getLocationURI( response, context );
    }
  }

}

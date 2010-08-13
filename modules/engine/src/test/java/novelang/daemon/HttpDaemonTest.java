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
package novelang.daemon;

import com.google.common.collect.Lists;
import novelang.ResourceTools;
import novelang.ResourcesForTests;
import novelang.common.LanguageTools;
import novelang.common.filefixture.JUnitAwareResourceInstaller;
import novelang.common.filefixture.Resource;
import novelang.produce.RequestTools;
import novelang.rendering.RenditionMimeType;
import novelang.system.DefaultCharset;
import novelang.system.Log;
import novelang.system.LogFactory;
import org.apache.commons.httpclient.HttpConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.NameAwareTestClassRunner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * End-to-end tests with {@link HttpDaemon} and the download of some generated documents.
 *
 * @author Laurent Caillette
 */
@SuppressWarnings( { "HardcodedFileSeparator" } )
@RunWith( value = NameAwareTestClassRunner.class )
public class HttpDaemonTest {

  @Test
  public void novellaOk() throws Exception {

    final Resource resource = ResourcesForTests.Served.GOOD_PART;
    final String novellaSource = alternateSetup( resource, ISO_8859_1 ) ;
    final String generated = readAsString( new URL(
        "http://localhost:" + HTTP_DAEMON_PORT + "/" +
        resource.getName()
    ) ) ;
    final String shaved = shaveComments( generated ) ;
    save( "generated.novella", generated ) ;
    final String normalizedNovellaSource = LanguageTools.unixifyLineBreaks( novellaSource ) ;
    final String normalizedShaved = LanguageTools.unixifyLineBreaks( shaved ) ;
    assertEquals( normalizedNovellaSource, normalizedShaved ) ;

  }

  @Test
  public void pdfOk() throws Exception {
    final Resource resource = ResourcesForTests.Served.GOOD_PART;
    alternateSetup( resource, ISO_8859_1 ) ;
    final byte[] generated = readAsBytes( new URL(
        "http://localhost:" + HTTP_DAEMON_PORT + "/" + resource.getBaseName() + PDF ) ) ;
    save( "generated.pdf", generated ) ;
    assertTrue( generated.length > 100 ) ;
  }

  @Test
  public void correctMimeTypeForPdf() throws Exception {
    final Resource resource = ResourcesForTests.Served.GOOD_PART;
    setup( resource ) ;
    final HttpGet httpGet = new HttpGet(
        "http://localhost:" + HTTP_DAEMON_PORT + "/" + resource.getBaseName() + PDF ) ;
    final HttpResponse httpResponse = new DefaultHttpClient().execute( httpGet ) ;
    final Header[] headers = httpResponse.getHeaders( "Content-type" ) ;
    assertTrue( "Got:" + Arrays.asList( headers ), headers.length > 0 ) ;
    assertEquals( "Got:" + Arrays.asList( headers ), "application/pdf", headers[ 0 ].getValue() ) ;
  }

  @Test
  public void greekCharactersOk() throws Exception {
    final Resource novellaGreek = ResourcesForTests.Parts.NOVELLA_GREEK ;
    setup( novellaGreek ) ;
    renderAndCheckStatusCode( novellaGreek, "greek.pdf" );
  }

  @Test
  public void polishCharactersOk() throws Exception {
    final Resource novellaPolish = ResourcesForTests.Parts.NOVELLA_POLISH ;
    setup( novellaPolish ) ;
    renderAndCheckStatusCode( novellaPolish, "polish.pdf" );
  }

  @Test
  public void fontListingMakesNoSmoke() throws Exception {
    setup() ;
    final byte[] generated = readAsBytes(
        new URL( "http://localhost:" + HTTP_DAEMON_PORT + FontDiscoveryHandler.DOCUMENT_NAME ) ) ;
    save( "generated.pdf", generated ) ;
    assertTrue( generated.length > 100 ) ;
  }

  @Test
  public void htmlNoSmoke() throws Exception {
    final Resource resource = ResourcesForTests.Served.GOOD_PART;
    setup( resource ) ;
    final byte[] generated = readAsBytes( new URL(
        "http://localhost:" + HTTP_DAEMON_PORT + "/" + resource.getBaseName() + HTML ) ) ;
    assertTrue( generated.length > 100 ) ;
  }

  @Test
  public void htmlBrokenCausesRedirection() throws Exception {
    final Resource resource = ResourcesForTests.Served.BROKEN_NOVELLA;
    setup( resource ) ;

    final String brokentDocumentName = resource.getBaseName() + HTML ;
    final String brokenDocumentUrl =
        "http://localhost:" + HTTP_DAEMON_PORT + "/" + brokentDocumentName ;
    final ResponseSnapshot responseSnapshot = followRedirection(
        brokenDocumentUrl ) ;

    assertTrue( responseSnapshot.getContent().contains( "Requested:" ) ) ;

    assertTrue(
        "Expected link to requested page",
        responseSnapshot.getContent().contains( brokentDocumentName )
    ) ;

    assertEquals( 1L, ( long ) responseSnapshot.getLocationsRedirectedTo().size() ) ;
    assertEquals(
        brokenDocumentUrl + RequestTools.ERRORPAGE_SUFFIX,
        responseSnapshot.getLocationsRedirectedTo().get( 0 ).getValue()
    ) ;
  }

  @Test
  public void errorPageForUnbrokenHtmlNotBrokenCausesRedirection() throws Exception {
    final Resource resource = ResourcesForTests.Served.GOOD_PART;
    setup( resource ) ;

    final ResponseSnapshot responseSnapshot = followRedirection(
        "http://localhost:" + HTTP_DAEMON_PORT + "/" + resource.getBaseName() + HTML +
        RequestTools.ERRORPAGE_SUFFIX
    ) ;

    assertFalse( responseSnapshot.getContent().contains( "Requested:" ) ) ;

  }

  @Test
  public void listDirectoryContentNoTrailingSolidus() throws Exception {
    final Resource resource = ResourcesForTests.Served.GOOD_PART;
    resourceInstaller.copyWithPath( resource ) ;
    setup() ;
    final ResponseSnapshot responseSnapshot =
        followRedirection( "http://localhost:" + HTTP_DAEMON_PORT ) ;
    checkDirectoryListing( responseSnapshot, resource ) ;
  }

  @Test
  public void listDirectoryContentWithTrailingSolidus() throws Exception {
      final Resource resource = ResourcesForTests.Served.GOOD_PART;
      resourceInstaller.copyWithPath( resource ) ;
      setup() ;
    final String urlAsString = "http://localhost:" + HTTP_DAEMON_PORT + "/";
    final ResponseSnapshot responseSnapshot = followRedirection( urlAsString ) ;
      checkDirectoryListing( responseSnapshot, resource ) ;
  }

  @Test
  public void listDirectoryContentWithSafari() throws Exception {
    final Resource resource = ResourcesForTests.Served.GOOD_PART;
    resourceInstaller.copyWithPath( resource ) ;
    setup() ;

    final String urlAsString = "http://localhost:" + HTTP_DAEMON_PORT + "/";
    final ResponseSnapshot responseSnapshot = followRedirection(
        urlAsString,
        SAFARI_USER_AGENT
    ) ;

    assertEquals( 1L, ( long ) responseSnapshot.getLocationsRedirectedTo().size() ) ;
    assertEquals( 
        urlAsString + DirectoryScanHandler.MIME_HINT,
        responseSnapshot.getLocationsRedirectedTo().get( 0 ).getValue()
    ) ;

    checkDirectoryListing( responseSnapshot, resource ) ;

  }

  @Test
  public void testAlternateStylesheetInQueryParameter() throws Exception {
    final Resource resource = ResourcesForTests.Served.GOOD_BOOK ;
    resourceInstaller.copy( resource ) ;
    resourceInstaller.copy( ResourcesForTests.Served.GOOD_PART ) ;
    final File stylesheetFile = resourceInstaller.copyScoped(
        ResourcesForTests.Served.dir, ResourcesForTests.Served.Style.VOID_XSL ) ;
    setup( stylesheetFile.getParentFile(), DefaultCharset.RENDERING ) ;

    final byte[] generated = readAsBytes( new URL(
        "http://localhost:" + HTTP_DAEMON_PORT + "/" + resource.getBaseName() + HTML +
                "?stylesheet=" + ResourcesForTests.Served.Style.VOID_XSL.getName()
    ) ) ;

    save( "generated.html", generated ) ;
    assertTrue( new String( generated ).contains( "this is the void stylesheet" ) ) ;

  }

  @Test
  public void testAlternateStylesheetInBook() throws Exception {
      final Resource resource = ResourcesForTests.Served.BOOK_ALTERNATE_XSL ;
      resourceInstaller.copy( resource ) ;
      resourceInstaller.copy( ResourcesForTests.Served.GOOD_PART ) ;
      final File stylesheetFile = resourceInstaller.copyScoped(
          ResourcesForTests.Served.dir, ResourcesForTests.Served.Style.VOID_XSL ) ;
      setup( stylesheetFile.getParentFile(), DefaultCharset.RENDERING ) ;

      final byte[] generated = readAsBytes( new URL(
          "http://localhost:" + HTTP_DAEMON_PORT + "/" + resource.getBaseName() + HTML ) ) ;

      save( "generated.html", generated ) ;
      assertTrue( new String( generated ).contains( "this is the void stylesheet" ) ) ;

  }

  
// =======
// Fixture
// =======

  private static final Log LOG = LogFactory.getLog( HttpDaemonTest.class ) ;

  static {
    ResourcesForTests.initialize() ;
  }

  private static final Charset ISO_8859_1 = Charset.forName( "ISO_8859_1" );


  private static final int TIMEOUT = 5000 ;



  private static final Pattern STRIP_COMMENTS_PATTERN = Pattern.compile( "%.*\\n" ) ;

  private static String shaveComments( final String s ) {
    final Matcher matcher = STRIP_COMMENTS_PATTERN.matcher( s ) ;
    final StringBuffer buffer = new StringBuffer() ;
    while( matcher.find() ) {
      matcher.appendReplacement( buffer, "" ) ;
    }
    matcher.appendTail( buffer ) ;
    return buffer.toString() ;
  }

  private static String readAsString( final URL url ) throws IOException {
    final StringWriter stringWriter = new StringWriter() ;
    IOUtils.copy( url.openStream(), stringWriter ) ;
    return stringWriter.toString() ;
  }

  private static byte[] readAsBytes( final URL url ) throws IOException {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream() ;
    IOUtils.copy( url.openStream(), outputStream ) ;
    return outputStream.toByteArray() ;
  }

  private void save( final String name, final String document ) throws IOException {
    final File file = new File( resourceInstaller.getTargetDirectory(), name ) ;
    FileUtils.writeStringToFile( file, document ) ;
    LOG.info( "Wrote file '%s'", file.getAbsolutePath() ) ;
  }

  private void save( final String name, final byte[] document ) throws IOException {
    final File file = new File( resourceInstaller.getTargetDirectory(), name ) ;
    FileUtils.writeByteArrayToFile(
        new File( resourceInstaller.getTargetDirectory(), name ), document ) ;
    LOG.info( "Wrote file '%s'", file.getAbsolutePath() ) ;
  }

  private static final int HTTP_DAEMON_PORT = 8081 ;



  private static final String PDF = "." + RenditionMimeType.PDF.getFileExtension() ;
  private static final String HTML = "." + RenditionMimeType.HTML.getFileExtension() ;



  @SuppressWarnings( { "InstanceVariableMayNotBeInitialized" } )
  private HttpDaemon httpDaemon ;

  private final JUnitAwareResourceInstaller resourceInstaller = new JUnitAwareResourceInstaller() ;



  private void setup() throws Exception {
    daemonSetup( ISO_8859_1 ) ;
  }


  private String setup( final Resource resource ) throws Exception {
    resourceInstaller.copy( resource ) ;
    daemonSetup( DefaultCharset.RENDERING ) ;
    final String novellaSource = resource.getAsString( DefaultCharset.SOURCE ) ;
    return novellaSource ;
  }

  private void setup(
      final File styleDirectory,
      final Charset renderingCharset
  ) throws Exception {
    daemonSetup( styleDirectory, renderingCharset ) ;
  }

  private String alternateSetup(
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
        HTTP_DAEMON_PORT,
        resourceInstaller.getTargetDirectory(),
        styleDirectory,
        renderingCharset
    ) ) ;
    httpDaemon.start() ;
  }

  private void daemonSetup( final Charset renderingCharset )
      throws Exception
  {
    httpDaemon = new HttpDaemon( ResourceTools.createDaemonConfiguration(
        HTTP_DAEMON_PORT,
        resourceInstaller.getTargetDirectory(),
        renderingCharset
    ) ) ;
    httpDaemon.start() ;
  }


  @After
  public void tearDown() throws Exception {
    httpDaemon.stop() ;
  }


  private static final String CAMINO_USER_AGENT =
      "Mozilla/5.0 (Macintosh; U; Intel Mac OS X; en; rv:1.8.1.14) " +
      "Gecko/20080512 Camino/1.6.1 (like Firefox/2.0.0.14)"
  ;
  
  private static final String SAFARI_USER_AGENT =
      "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_4_11; en) " +
      "AppleWebKit/525.18 (KHTML, like Gecko) " +
      "Version/3.1.2 Safari/525.22"
  ;

  private static final String DEFAULT_USER_AGENT = CAMINO_USER_AGENT ;

  private ResponseSnapshot followRedirection( final String originalUrlAsString )
      throws IOException
  {
    return followRedirection( originalUrlAsString, DEFAULT_USER_AGENT ) ;
  }

  /**
   * Follows redirection using {@link HttpClient}'s default, and returns response body.
   */
  private ResponseSnapshot followRedirection(
      final String originalUrlAsString,
      final String userAgent
  ) throws IOException {

    final List< Header > locationsRedirectedTo = Lists.newArrayList() ;

    final AbstractHttpClient httpClient = new DefaultHttpClient() ;

    httpClient.setRedirectHandler( new RecordingRedirectHandler( locationsRedirectedTo ) ) ;
    final HttpParams parameters = new BasicHttpParams() ;
    parameters.setIntParameter( CoreConnectionPNames.SO_TIMEOUT, TIMEOUT ) ;
    final HttpGet httpGet = new HttpGet( originalUrlAsString ) ;
    httpGet.setHeader( "User-Agent", userAgent ); ;
    httpGet.setParams( parameters ) ;
    final HttpResponse httpResponse = httpClient.execute( httpGet ) ;

    final ResponseSnapshot responseSnapshot =
        new ResponseSnapshot( httpResponse, locationsRedirectedTo ) ;
    save( "generated.html", responseSnapshot.getContent() ) ;

    return responseSnapshot ;
  }

  private static void checkDirectoryListing(
      final ResponseSnapshot responseSnapshot ,
      final Resource resource
  ) throws IOException {
    final String fullPath = resource.getFullPath().substring( 1 ) ; // Remove leading solidus.
    final String filePath = fullPath + resource.getBaseName() + ".html" ;

    LOG.debug( "fullpath='%s'", fullPath ) ;
    LOG.debug( "filepath='%s'", filePath ) ;
    LOG.debug( "Checking response body: \n%s", responseSnapshot.getContent() ) ;

    final String expectedFullPath = "<a href=\"" + fullPath + "\">" + fullPath + "</a>" ;
    LOG.debug( "Expected fullPath='%s'", expectedFullPath ) ;

    assertTrue( responseSnapshot.getContent().contains( expectedFullPath ) ) ;
    assertTrue( responseSnapshot.getContent()
        .contains( "<a href=\"" + filePath + "\">" + filePath + "</a>" ) ) ;
  }


  /**
   * We need to read several values from an {@link HttpResponse} so it would be convenient
   * to use it as return type for {@link HttpDaemonTest#followRedirection(String, String)}
   * but it's impossible to read the streamable content more than once.
   * We turn this by keeping a snapshot of everything needed.
   */
  private static class ResponseSnapshot {

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


  private void renderAndCheckStatusCode( final Resource resource, final String savedFileName )
      throws IOException
  {
    final HttpGet httpGet = new HttpGet(
        "http://localhost:" + HTTP_DAEMON_PORT + "/" + resource.getBaseName() + PDF ) ;
    final HttpResponse httpResponse = new DefaultHttpClient().execute( httpGet ) ;

    final ByteArrayOutputStream responseContent = new ByteArrayOutputStream() ;
    IOUtils.copy( httpResponse.getEntity().getContent(), responseContent ) ;
    save( savedFileName, responseContent.toByteArray() ) ;
    final int statusCode = httpResponse.getStatusLine().getStatusCode();
    assertEquals( ( long ) HttpStatus.SC_OK, ( long ) statusCode ) ;
  }


}

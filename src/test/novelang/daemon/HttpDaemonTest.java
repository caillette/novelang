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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import novelang.TestResourceTree;
import novelang.TestResourceTools;
import novelang.common.LanguageTools;
import novelang.common.filefixture.JUnitAwareResourceInstaller;
import novelang.common.filefixture.Resource;
import novelang.produce.RequestTools;
import novelang.rendering.RenditionMimeType;
import novelang.system.DefaultCharset;
import novelang.system.Log;
import novelang.system.LogFactory;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.NameAwareTestClassRunner;

/**
 * End-to-end tests with {@link HttpDaemon} and the download of some generated documents.
 *
 * @author Laurent Caillette
 */
@RunWith( value = NameAwareTestClassRunner.class )
public class HttpDaemonTest {

  @Test
  public void nlpOk() throws Exception {

    final Resource resource = TestResourceTree.Served.GOOD_PART;
    final String nlpSource = alternateSetup( resource, ISO_8859_1 ) ;
    final String generated = readAsString( new URL(
        "http://localhost:" + HTTP_DAEMON_PORT + "/" +
        resource.getName()
    ) ) ;
    final String shaved = shaveComments( generated ) ;
    save( "generated.nlp", generated ) ;
    final String normalizedNlpSource = LanguageTools.unixifyLineBreaks( nlpSource ) ;
    final String normalizedShaved = LanguageTools.unixifyLineBreaks( shaved ) ;
    Assert.assertEquals( normalizedNlpSource, normalizedShaved ) ;

  }

  @Test
  public void pdfOk() throws Exception {
    final Resource resource = TestResourceTree.Served.GOOD_PART;
    alternateSetup( resource, ISO_8859_1 ) ;
    final byte[] generated = readAsBytes( new URL(
        "http://localhost:" + HTTP_DAEMON_PORT + "/" + resource.getBaseName() + PDF ) ) ;
    save( "generated.pdf", generated ) ;
    assertTrue( generated.length > 100 ) ;
  }

  @Test
  public void correctMimeTypeForPdf() throws Exception {
    final Resource resource = TestResourceTree.Served.GOOD_PART;
    setup( resource ) ;
    final GetMethod getMethod = new GetMethod(
        "http://localhost:" + HTTP_DAEMON_PORT + "/" + resource.getBaseName() + PDF ) ;
    new HttpClient().executeMethod( getMethod ) ;
    final Header[] headers = getMethod.getResponseHeaders( "Content-type" ) ;
    LOG.debug( "Got headers: %s", headers ) ;
    Assert.assertEquals( "application/pdf", headers[ 0 ].getValue() ) ;
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
    final Resource resource = TestResourceTree.Served.GOOD_PART;
    setup( resource ) ;
    final byte[] generated = readAsBytes( new URL(
        "http://localhost:" + HTTP_DAEMON_PORT + "/" + resource.getBaseName() + HTML ) ) ;
    assertTrue( generated.length > 100 ) ;
  }

  @Test
  public void htmlBrokenCausesRedirection() throws Exception {
    final Resource resource = TestResourceTree.Served.BROKEN_PART;
    setup( resource ) ;

    final String brokentDocumentName = resource.getBaseName() + HTML ;
    final HttpMethod method = followRedirection(
        "http://localhost:" + HTTP_DAEMON_PORT + "/" + brokentDocumentName ) ;
    final String responseBody = method.getResponseBodyAsString() ;

    assertTrue( responseBody.contains( "Requested:" ) ) ;

    assertTrue(
        "Expected link to requested page",
        responseBody.contains( brokentDocumentName )
    ) ;

    assertTrue(
        "Expected path '" + brokentDocumentName + RequestTools.ERRORPAGE_SUFFIX + "'",
        method.getPath().contains( brokentDocumentName + RequestTools.ERRORPAGE_SUFFIX )
    ) ;
  }

  @Test
  public void errorPageForUnbrokenHtmlNotBrokenCausesRedirection() throws Exception {
    final Resource resource = TestResourceTree.Served.GOOD_PART;
    setup( resource ) ;

    final HttpMethod method = followRedirection(
        "http://localhost:" + HTTP_DAEMON_PORT + "/" + resource.getBaseName() + HTML +
        RequestTools.ERRORPAGE_SUFFIX
    ) ;

    final String responseBody = method.getResponseBodyAsString() ;
    assertFalse( responseBody.contains( "Requested:" ) ) ;

  }

  @Test
  public void listDirectoryContentNoTrailingSolidus() throws Exception {
    final Resource resource = TestResourceTree.Served.GOOD_PART;
    resourceInstaller.copyWithPath( resource ) ;
    setup() ;
    final HttpMethod method = followRedirection( "http://localhost:" + HTTP_DAEMON_PORT ) ;
    checkDirectoryListing( method, resource ) ;
  }

  @Test
  public void listDirectoryContentWithTrailingSolidus() throws Exception {
      final Resource resource = TestResourceTree.Served.GOOD_PART;
      resourceInstaller.copyWithPath( resource ) ;
      setup() ;
      final HttpMethod method = followRedirection( "http://localhost:" + HTTP_DAEMON_PORT + "/" ) ;
      checkDirectoryListing( method, resource ) ;
  }

  @Test
  public void listDirectoryContentWithSafari() throws Exception {
    final Resource resource = TestResourceTree.Served.GOOD_PART;
    resourceInstaller.copyWithPath( resource ) ;
    setup() ;

    final HttpMethod method = followRedirection(
        "http://localhost:" + HTTP_DAEMON_PORT + "/",
        SAFARI_USER_AGENT
    ) ;

    Assert.assertTrue( method.getPath().endsWith( "/" + DirectoryScanHandler.MIME_HINT ) ) ;

    checkDirectoryListing( method, resource ) ;

  }

  @Test
  public void testAlternateStylesheetInQueryParameter() throws Exception {
    final Resource resource = TestResourceTree.Served.GOOD_BOOK ;
    resourceInstaller.copy( resource ) ;
    resourceInstaller.copy( TestResourceTree.Served.GOOD_PART ) ;
    final File stylesheetFile = resourceInstaller.copyScoped(
        TestResourceTree.Served.dir, TestResourceTree.Served.Style.VOID_XSL ) ;
    setup( stylesheetFile.getParentFile(), DefaultCharset.RENDERING ) ;

    final byte[] generated = readAsBytes( new URL(
        "http://localhost:" + HTTP_DAEMON_PORT + "/" + resource.getBaseName() + HTML +
                "?stylesheet=" + TestResourceTree.Served.Style.VOID_XSL.getName()
    ) ) ;

    save( "generated.html", generated ) ;
    assertTrue( new String( generated ).contains( "this is the void stylesheet" ) ) ;

  }

  @Test
  public void testAlternateStylesheetInBook() throws Exception {
      final Resource resource = TestResourceTree.Served.BOOK_ALTERNATE_XSL ;
      resourceInstaller.copy( resource ) ;
      resourceInstaller.copy( TestResourceTree.Served.GOOD_PART ) ;
      final File stylesheetFile = resourceInstaller.copyScoped(
          TestResourceTree.Served.dir, TestResourceTree.Served.Style.VOID_XSL ) ;
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
    TestResourceTree.initialize() ;  
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



  private HttpDaemon httpDaemon ;

  private final JUnitAwareResourceInstaller resourceInstaller = new JUnitAwareResourceInstaller() ;



  private void setup() throws Exception {
    daemonSetup( ISO_8859_1 ) ;
  }


  private String setup( final Resource resource ) throws Exception {
    resourceInstaller.copy( resource ) ;
    daemonSetup( DefaultCharset.RENDERING ) ;
    final String nlpSource = resource.getAsString( DefaultCharset.SOURCE ) ;
    return nlpSource ;
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
    final String nlpSource = resource.getAsString( DefaultCharset.SOURCE ) ;
    return nlpSource ;
  }


  private void daemonSetup( final File styleDirectory, final Charset renderingCharset )
      throws Exception
  {
    httpDaemon = new HttpDaemon( TestResourceTools.createDaemonConfiguration(
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
    httpDaemon = new HttpDaemon( TestResourceTools.createDaemonConfiguration(
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

  private HttpMethod followRedirection( final String originalUrlAsString ) throws IOException {
    return followRedirection( originalUrlAsString, DEFAULT_USER_AGENT ) ;
  }

  private HttpMethod followRedirection(
      final String originalUrlAsString,
      final String userAgent
  ) throws IOException {
    final HttpClient httpClient = new HttpClient() ;
    httpClient.getHttpConnectionManager().getParams().setConnectionTimeout( TIMEOUT ) ;
    final HttpMethod method = new GetMethod( originalUrlAsString ) ;
    method.setRequestHeader( "User-Agent", userAgent ) ;
    method.setFollowRedirects( true ) ;
    httpClient.executeMethod( method ) ;

    final String responseBody = method.getResponseBodyAsString() ;
    save( "generated.html", responseBody ) ;

    return method;
  }

  private void checkDirectoryListing(
      final HttpMethod method,
      final Resource resource
  ) throws IOException {
    final String responseBody = method.getResponseBodyAsString() ;
    final String fullPath = resource.getFullPath().substring( 1 ) ; // Remove leading solidus.
    final String filePath = fullPath + resource.getBaseName() + ".html" ;

    LOG.debug( "fullpath='%s'", fullPath ) ;
    LOG.debug( "filepath='%s'", filePath ) ;
    LOG.debug( "Checking response body: \n%s", responseBody ) ;

    final String expectedFullPath = "<a href=\"" + fullPath + "\">" + fullPath + "</a>" ;
    LOG.debug( "Expected fullPath='%s'", expectedFullPath ) ;

    assertTrue( responseBody.contains( expectedFullPath ) ) ;
    assertTrue( responseBody.contains( "<a href=\"" + filePath + "\">" + filePath + "</a>" ) ) ;
  }


}

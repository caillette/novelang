/*
 * Copyright (C) 2011 Laurent Caillette
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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Rule;
import org.junit.Test;
import static com.google.common.base.Charsets.ISO_8859_1;
import static com.google.common.base.Charsets.UTF_8;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.novelang.daemon.HttpDaemonFixture.PDF;
import static org.novelang.daemon.HttpDaemonFixture.shaveComments;
import static org.novelang.outfit.TextTools.unixifyLineBreaks;
import static org.novelang.rendering.multipage.MultipageFixture.TargetPage.*;

import org.novelang.ResourcesForTests;
import org.novelang.common.filefixture.Resource;
import org.novelang.outfit.DefaultCharset;
import org.novelang.produce.GenericRequest;
import org.novelang.produce.MalformedRequestException;
import org.novelang.rendering.multipage.MultipageFixture;

/**
 * Tests for {@link HttpDaemon} based on {@link org.novelang.daemon.HttpDaemonSupport}.
 *
 * @author Laurent Caillette
 */
public class HttpDaemonTest {


  @Test
  public void redirectAfterHittingRenderingProblem() throws Exception {
    final Resource resource = ResourcesForTests.Served.GOOD_PART ;
    support.resourceInstaller.copy( resource ) ;
    final Resource stylesheetResource = ResourcesForTests.XslFormatting.XSL_BROKEN_RENDERING ;
    final File stylesheetFile = support.resourceInstaller.copyScoped(
        ResourcesForTests.XslFormatting.dir, stylesheetResource ) ;
    support.setup( stylesheetFile.getParentFile(), DefaultCharset.RENDERING ) ;


    final HttpDaemonFixture.ResponseSnapshot responseSnapshot = support.followRedirection(
        support.createUrl( "/" + resource.getBaseName() + HttpDaemonFixture.PDF +
            "?stylesheet=" + stylesheetResource.getName() ).toExternalForm()
    ) ;

    // We can't cast some rubbish to ReadableDateTime.
    assertThat( responseSnapshot.getContent() ).contains( "org.joda.time.ReadableDateTime" ) ;

    // That's the matgic for a PDF document.
    assertThat( responseSnapshot.getContent() ).doesNotContain( "%PDF-" ) ;

  }



  @Test
  public void multipage() throws Exception {

    final MultipageFixture multipageFixture = new MultipageFixture(
        support.resourceInstaller,
        ResourcesForTests.Multipage.MULTIPAGE_XSL,
        ResourcesForTests.MainResources.Style.DEFAULT_NOVELLA_XSL
    ) ;

    support.setup(
        multipageFixture.getStylesheetFile().getParentFile(),
        DefaultCharset.RENDERING
    ) ;

    verify( multipageFixture, ZERO ) ;
    verify( multipageFixture, MAIN ) ;
    verify( multipageFixture, ONE ) ;

  }


  @Test
  public void testAlternateStylesheetInBook() throws Exception {
      final Resource resource = ResourcesForTests.Served.BOOK_ALTERNATE_XSL ;
      support.resourceInstaller.copy( resource ) ;
      support.resourceInstaller.copy( ResourcesForTests.Served.GOOD_PART ) ;
      final File stylesheetFile = support.resourceInstaller.copyScoped(
          ResourcesForTests.Served.dir, ResourcesForTests.Served.Style.VOID_XSL ) ;
      support.setup( stylesheetFile.getParentFile(), DefaultCharset.RENDERING ) ;

      final byte[] generated = support.readAsBytes(
          "/" + resource.getBaseName() + HttpDaemonFixture.HTML ) ;

      assertTrue( new String( generated ).contains( "this is the void stylesheet" ) ) ;

  }


  @Test
  public void indicateErrorLocationForBrokentStylesheet() throws Exception {
    final Resource resource = ResourcesForTests.Served.GOOD_PART ;
    support.resourceInstaller.copy( resource ) ;
    final Resource stylesheetResource = ResourcesForTests.Served.Style.ERRONEOUS_XSL ;
    final File stylesheetFile = support.resourceInstaller.copyScoped(
        ResourcesForTests.Served.dir, stylesheetResource ) ;
    support.setup( stylesheetFile.getParentFile(), DefaultCharset.RENDERING ) ;


    final HttpDaemonFixture.ResponseSnapshot responseSnapshot = support.followRedirection(
        support.createUrl( "/" + resource.getBaseName() + HttpDaemonFixture.HTML +
            "?stylesheet=" + stylesheetResource.getName() ).toExternalForm()
    ) ;

    assertThat( responseSnapshot.getContent() ).contains(
        "line=25; column=38 - "
        + "xsl:this-is-not-supposed-to-work is not allowed in this position in the stylesheet"
    ) ;

  }


  @Test
  public void testAlternateStylesheetInQueryParameter() throws Exception {
    final Resource resource = ResourcesForTests.Served.GOOD_BOOK ;
    support.resourceInstaller.copy( resource ) ;
    support.resourceInstaller.copy( ResourcesForTests.Served.GOOD_PART ) ;
    final File stylesheetFile = support.resourceInstaller.copyScoped(
        ResourcesForTests.Served.dir, ResourcesForTests.Served.Style.VOID_XSL ) ;
    support.setup( stylesheetFile.getParentFile(), DefaultCharset.RENDERING ) ;

    final byte[] generated = support.readAsBytes(
        "/" + resource.getBaseName() + HttpDaemonFixture.HTML +
                "?stylesheet=" + ResourcesForTests.Served.Style.VOID_XSL.getName()
    ) ;

    assertTrue( new String( generated ).contains( "this is the void stylesheet" ) ) ;

  }


  @Test
  public void listDirectoryContentWithSafari() throws Exception {
    final Resource resource = ResourcesForTests.Served.GOOD_PART;
    support.resourceInstaller.copyWithPath( resource ) ;
    support.setup() ;

    final String urlAsString = support.createUrl( "/" ).toExternalForm() ;
    final HttpDaemonFixture.ResponseSnapshot responseSnapshot = support.followRedirection(
        urlAsString,
        HttpDaemonFixture.SAFARI_USER_AGENT
    ) ;

    assertEquals( 1L, ( long ) responseSnapshot.getLocationsRedirectedTo().size() ) ;
    assertEquals(
        urlAsString + DirectoryScanHandler.MIME_HINT,
        responseSnapshot.getLocationsRedirectedTo().get( 0 ).getValue()
    ) ;

    HttpDaemonFixture.checkDirectoryListing( responseSnapshot, resource ) ;

  }

  @Test
  public void listDirectoryContentWithTrailingSolidus() throws Exception {
      final Resource resource = ResourcesForTests.Served.GOOD_PART;
      support.resourceInstaller.copyWithPath( resource ) ;
      support.setup() ;
    final HttpDaemonFixture.ResponseSnapshot responseSnapshot = support.followRedirection(
        support.createUrl( "/" ).toExternalForm() ) ;
      HttpDaemonFixture.checkDirectoryListing( responseSnapshot, resource ) ;
  }

  @Test
  public void listDirectoryContentNoTrailingSolidus() throws Exception {
    final Resource resource = ResourcesForTests.Served.GOOD_PART;
    support.resourceInstaller.copyWithPath( resource ) ;
    support.setup() ;
    final HttpDaemonFixture.ResponseSnapshot responseSnapshot =
        support.followRedirection( support.createUrl( "" ).toExternalForm() ) ;
    HttpDaemonFixture.checkDirectoryListing( responseSnapshot, resource ) ;
  }


  @Test
  public void errorPageForUnbrokenHtmlNotBrokenCausesRedirection() throws Exception {
    final Resource resource = ResourcesForTests.Served.GOOD_PART;
    support.setup( resource ) ;

    final HttpDaemonFixture.ResponseSnapshot responseSnapshot = support.followRedirection(
        support.createUrl(
            "/" + resource.getBaseName() + HttpDaemonFixture.HTML +
            GenericRequest.ERRORPAGE_SUFFIX 
        ).toExternalForm()
    ) ;

    assertFalse( responseSnapshot.getContent().contains( "Requested:" ) ) ;

  }



  @Test
  public void htmlBrokenCausesRedirection() throws Exception {
    final Resource resource = ResourcesForTests.Served.BROKEN_NOVELLA;
    support.setup( resource ) ;

    final String brokenDocumentRequest = "/" + resource.getBaseName() + HttpDaemonFixture.HTML ;
    final String brokenDocumentUrl =
        support.createUrl( brokenDocumentRequest ).toExternalForm() ;

    final HttpDaemonFixture.ResponseSnapshot responseSnapshot = support.followRedirection(
        brokenDocumentUrl ) ;

    assertTrue( responseSnapshot.getContent().contains( "Requested:" ) ) ;

    assertTrue(
        "Expected link to requested page",
        responseSnapshot.getContent().contains( brokenDocumentRequest )
    ) ;

    assertEquals( 1L, ( long ) responseSnapshot.getLocationsRedirectedTo().size() ) ;
    assertEquals(
        brokenDocumentUrl + GenericRequest.ERRORPAGE_SUFFIX,
        responseSnapshot.getLocationsRedirectedTo().get( 0 ).getValue()
    ) ;
  }


  /**
   * Tests if a Novella renders as its own source!
   * In order to force character escape we use non-default encoding.
   */
  @Test
  public void novellaOk() throws Exception {
    final Resource resource = ResourcesForTests.Served.GOOD_PART;
    final String novellaSource = support.alternateSetup( resource, UTF_8, ISO_8859_1 ) ;
    final String generated = support.readAsString(
        resource,
        // Weird, but forces correct escaping. Plays with partial compatiblity with ISO_8859_1.
        HttpDaemonFixture.DEFAULT_PLATFORM_CHARSET
//        MAC_ROMAN
    ) ;
    final String normalizedNovellaSource = unixifyLineBreaks( novellaSource ) ;
    final String normalizedShaved = unixifyLineBreaks( shaveComments( generated ) ) ;
    assertThat( normalizedShaved ).isEqualTo( normalizedNovellaSource ) ;
  }


  @Test
  public void pdfOk() throws Exception {
    final Resource resource = ResourcesForTests.Served.GOOD_PART;
    support.alternateSetup( resource, UTF_8, ISO_8859_1 ) ;
    final byte[] generated = support.readAsBytes( "/" + resource.getBaseName() + PDF ) ;
    final String pdfText = HttpDaemonFixture.extractPdfText( generated ) ;
    assertThat( pdfText ).contains( GOOD_PART_EXTRACT ) ;
  }


  @Test
  public void correctMimeTypeForPdf() throws Exception {
    final Resource resource = ResourcesForTests.Served.GOOD_PART;
    support.setup( resource ) ;
    final HttpGet httpGet = support.createHttpGet(
        "/" + resource.getBaseName() + HttpDaemonFixture.PDF ) ;
    final HttpResponse httpResponse = new DefaultHttpClient().execute( httpGet ) ;
    final Header[] headers = httpResponse.getHeaders( "Content-type" ) ;
    assertThat( headers ).isNotEmpty() ;
    assertThat( headers[ 0 ].getValue() ).isEqualTo( "application/pdf" ) ;
  }

  @Test
  public void greekCharactersOk() throws Exception {
    final Resource resource = ResourcesForTests.Parts.NOVELLA_GREEK;
    renderPdfAndCheckStatusCode( resource );
  }

  @Test
  public void polishCharactersOk() throws Exception {
    final Resource resource = ResourcesForTests.Parts.NOVELLA_POLISH ;
    renderPdfAndCheckStatusCode( resource ) ;
  }

  @Test
  public void romanianCharactersOk() throws Exception {
    final Resource resource = ResourcesForTests.Parts.NOVELLA_ROMANIAN ;
    renderPdfAndCheckStatusCode( resource ) ;
  }

  @Test
//  @Ignore( "Moved to BetterHttpDaemonTest" )
  public void htmlNoSmoke() throws Exception {
    final Resource resource = ResourcesForTests.Served.GOOD_PART;
    support.setup( resource ) ;
    final String generated = support.readAsString(
        "/" + resource.getBaseName() + HttpDaemonFixture.HTML ) ;
    assertThat( generated ).contains( GOOD_PART_EXTRACT ) ;
  }



// =======
// Fixture
// =======

  @Rule
  public final HttpDaemonSupport support = new HttpDaemonSupport() ;

  private static final Charset MAC_ROMAN = Charset.forName( "MacRoman" ) ;

  private void renderPdfAndCheckStatusCode( final Resource resource ) throws Exception {
    final Resource novellaGreek = resource ;
    support.setup( novellaGreek ) ;
    support.renderAndCheckStatusCode( "/" + resource.getBaseName() + PDF ) ;
  }


  private void verify(
      final MultipageFixture multipageFixture,
      final MultipageFixture.TargetPage targetPage
  ) throws IOException, MalformedRequestException {
    MultipageFixture.verify( targetPage,
        support.readAsBytes( multipageFixture.requestFor( targetPage ).getOriginalTarget() ) ) ;
  }


  private static final String GOOD_PART_EXTRACT = "Used in HttpDaemonTest. Edit with care.";

}

/*
 * Copyright (C) 2010 Laurent Caillette
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
import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;
import org.novelang.ResourcesForTests;
import org.novelang.common.filefixture.Resource;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.DefaultCharset;
import org.novelang.rendering.multipage.MultipageFixture;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * End-to-end tests with {@link HttpDaemon} and the download of some generated documents.
 *
 * @author Laurent Caillette
 */
@SuppressWarnings( { "HardcodedFileSeparator" } )
public class HttpDaemonTest extends AbstractTestHttpDaemon {



  @Test
  @Ignore( "Moved to BetterHttpDaemonTest" )
  public void listDirectoryContentNoTrailingSolidus() throws Exception {
    final Resource resource = ResourcesForTests.Served.GOOD_PART;
    resourceInstaller.copyWithPath( resource ) ;
    setup() ;
    final HttpDaemonFixture.ResponseSnapshot responseSnapshot =
        followRedirection( "http://localhost:" + daemonPort ) ;
    HttpDaemonFixture.checkDirectoryListing( responseSnapshot, resource ) ;
  }

  @Test
  public void listDirectoryContentWithTrailingSolidus() throws Exception {
      final Resource resource = ResourcesForTests.Served.GOOD_PART;
      resourceInstaller.copyWithPath( resource ) ;
      setup() ;
    final String urlAsString = "http://localhost:" + daemonPort + "/";
    final HttpDaemonFixture.ResponseSnapshot responseSnapshot = followRedirection( urlAsString ) ;
      HttpDaemonFixture.checkDirectoryListing( responseSnapshot, resource ) ;
  }

  @Test
  public void listDirectoryContentWithSafari() throws Exception {
    final Resource resource = ResourcesForTests.Served.GOOD_PART;
    resourceInstaller.copyWithPath( resource ) ;
    setup() ;

    final String urlAsString = "http://localhost:" + daemonPort + "/";
    final HttpDaemonFixture.ResponseSnapshot responseSnapshot = followRedirection(
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
  public void testAlternateStylesheetInQueryParameter() throws Exception {
    final Resource resource = ResourcesForTests.Served.GOOD_BOOK ;
    resourceInstaller.copy( resource ) ;
    resourceInstaller.copy( ResourcesForTests.Served.GOOD_PART ) ;
    final File stylesheetFile = resourceInstaller.copyScoped(
        ResourcesForTests.Served.dir, ResourcesForTests.Served.Style.VOID_XSL ) ;
    setup( stylesheetFile.getParentFile(), DefaultCharset.RENDERING ) ;

    final byte[] generated = readAsBytes( new URL(
        "http://localhost:" + daemonPort + "/" + resource.getBaseName() + HttpDaemonFixture.HTML +
                "?stylesheet=" + ResourcesForTests.Served.Style.VOID_XSL.getName()
    ) ) ;

    save( "generated.html", generated ) ;
    assertTrue( new String( generated ).contains( "this is the void stylesheet" ) ) ;

  }

  @Test
  public void indicateErrorLocationForBrokentStylesheet() throws Exception {
    final Resource resource = ResourcesForTests.Served.GOOD_PART ;
    resourceInstaller.copy( resource ) ;
    final Resource stylesheetResource = ResourcesForTests.Served.Style.ERRONEOUS_XSL ;
    final File stylesheetFile = resourceInstaller.copyScoped(
        ResourcesForTests.Served.dir, stylesheetResource ) ;
    setup( stylesheetFile.getParentFile(), DefaultCharset.RENDERING ) ;


    final HttpDaemonFixture.ResponseSnapshot responseSnapshot = followRedirection(
        "http://localhost:" + daemonPort + "/" + resource.getBaseName() + HttpDaemonFixture.HTML +
            "?stylesheet=" + stylesheetResource.getName()
    ) ;

    save( "generated.html", responseSnapshot.getContent() ) ;
    assertThat( responseSnapshot.getContent() ).contains(
        "line=25; column=38 - "
        + "xsl:this-is-not-supposed-to-work is not allowed in this position in the stylesheet"
    ) ;

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
          "http://localhost:" + daemonPort + "/" + resource.getBaseName() + HttpDaemonFixture.HTML ) ) ;

      save( "generated.html", generated ) ;
      assertTrue( new String( generated ).contains( "this is the void stylesheet" ) ) ;

  }



  @Test
  public void multipage() throws Exception {

    final MultipageFixture multipageFixture = new MultipageFixture(
        resourceInstaller,
        ResourcesForTests.Multipage.MULTIPAGE_XSL,
        ResourcesForTests.MainResources.Style.DEFAULT_NOVELLA_XSL
    ) ;

    setup( multipageFixture.getStylesheetFile().getParentFile(), DefaultCharset.RENDERING ) ;

    save( multipageFixture.getAncillaryDocument0File(),
        buildUrl( multipageFixture.requestForAncillaryDocument0() ) ) ;
    save( multipageFixture.getMainDocumentFile(), buildUrl( multipageFixture.requestForMain() ) ) ;
    save( multipageFixture.getAncillaryDocument1File(),
        buildUrl( multipageFixture.requestForAncillaryDocument1() ) ) ;

    multipageFixture.verifyGeneratedFiles() ;
  }



// =======
// Fixture
// =======

  private static final Logger LOGGER = LoggerFactory.getLogger( HttpDaemonTest.class );

}

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

import java.nio.charset.Charset;
import java.util.Arrays;

import com.google.common.base.Charsets;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Rule;
import org.junit.Test;
import org.novelang.ResourcesForTests;
import org.novelang.common.filefixture.Resource;

import static com.google.common.base.Charsets.ISO_8859_1;
import static com.google.common.base.Charsets.UTF_8;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.novelang.daemon.HttpDaemonFixture.PDF;
import static org.novelang.daemon.HttpDaemonFixture.shaveComments;
import static org.novelang.outfit.TextTools.unixifyLineBreaks;

/**
 * Tests for {@link HttpDaemon} based on {@link org.novelang.daemon.HttpDaemonSupport}.
 *
 * @author Laurent Caillette
 */
public class BetterHttpDaemonTest {

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
//        HttpDaemonFixture.DEFAULT_PLATFORM_CHARSET
        MAC_ROMAN
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
    assertThat( pdfText ).contains( "Used in HttpDaemonTest. Edit with care." ) ;
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

// =======
// Fixture
// =======

  @Rule
  public final HttpDaemonSupport support = new HttpDaemonSupport() ;

  private static final Charset MAC_ROMAN = Charset.forName( "MacRoman" ) ;

}

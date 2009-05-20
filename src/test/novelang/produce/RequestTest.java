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

package novelang.produce;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import static org.junit.Assert.*;
import org.junit.Test;
import novelang.rendering.RenditionMimeType;
import novelang.system.LogFactory;
import novelang.system.Log;
import com.google.common.collect.ImmutableSet;
import com.google.common.base.Joiner;

/**
 * @author Laurent Caillette
 */
public class RequestTest {

  @Test
  public void documentRequest() {
    final DocumentRequest request =
        RequestTools.createDocumentRequest( PDF_REQUEST_PATH ); ;
    assertEquals( PDF_REQUEST_PATH, request.getOriginalTarget() ) ;
    assertEquals( RenditionMimeType.PDF, request.getRenditionMimeType() ) ;
    assertTrue( request.isRendered() ) ;
    assertEquals( "pdf", request.getResourceExtension() ) ;
    assertEquals( REQUEST_BODY, request.getDocumentSourceName() ) ;
    assertTrue( "Got: " + request.getTags(), request.getTags().isEmpty() ) ;

    assertFalse( StringUtils.isBlank( request.toString() ) ) ;
    assertNull( request.getAlternateStylesheet() ) ;
  }

  @Test
  public void documentRequestWithStylesheet() {
    final DocumentRequest request =
        RequestTools.createDocumentRequest( PDF_REQUEST_PATH_WITHSTYLESHEET ) ;
    assertEquals( PDF_REQUEST_PATH, request.getOriginalTarget() ) ;
    assertEquals( RenditionMimeType.PDF, request.getRenditionMimeType() ) ;
    assertTrue( request.isRendered() ) ;
    assertEquals( "pdf", request.getResourceExtension() ) ;
    assertEquals( REQUEST_BODY, request.getDocumentSourceName() ) ;
    assertEquals( STYLESHEET_RESOURCENAME, request.getAlternateStylesheet().getName() ) ;

    assertFalse( StringUtils.isBlank( request.toString() ) ) ;
  }

  @Test
  public void polymorphicRequestForError() {
    final PolymorphicRequest request =
        RequestTools.createPolymorphicRequest( REQUEST_PATH_BROKEN ) ;
    assertTrue( request.getDisplayProblems() ) ;
    assertEquals( PDF_REQUEST_PATH, request.getOriginalTarget() ) ;
    assertEquals( RenditionMimeType.PDF, request.getRenditionMimeType() ) ;
    assertEquals( REQUEST_BODY, request.getDocumentSourceName() ) ;

    assertFalse( StringUtils.isBlank( request.toString() ) ) ;
  }

  @Test
  public void polymorphicRequestForRawResource() {
    final PolymorphicRequest request =
        RequestTools.createPolymorphicRequest( CSS_REQUEST_PATH ) ;
    assertFalse( request.getDisplayProblems() ) ;
    assertEquals( CSS_REQUEST_PATH, request.getOriginalTarget() ) ;
    assertNull( request.getRenditionMimeType() ) ;
    assertEquals( REQUEST_BODY, request.getDocumentSourceName() ) ;

    assertFalse( StringUtils.isBlank( request.toString() ) ) ;
  }

  @Test
  public void polymorphicRequestWitTags() {
    final PolymorphicRequest request =
        RequestTools.createPolymorphicRequest( PDF_REQUEST_PATH_WITHSTYLESHEET_AND_TAGS ) ;
    assertFalse( request.getDisplayProblems() ) ;
    assertEquals( PDF_REQUEST_PATH, request.getOriginalTarget() ) ;
    assertEquals( RenditionMimeType.PDF, request.getRenditionMimeType() ) ;
    assertEquals( REQUEST_BODY, request.getDocumentSourceName() ) ;
    assertEquals( TAGSET, request.getTags() ) ;

    assertFalse( StringUtils.isBlank( request.toString() ) ) ;
  }



// =======
// Fixture
// =======

  private static final String REQUEST_BODY = "/path/to/file" ;

  private static final String PDF_REQUEST_PATH = REQUEST_BODY + ".pdf" ;
  private static final String CSS_REQUEST_PATH = REQUEST_BODY + ".css" ;

  private static final String STYLESHEET_RESOURCENAME = "dir/sheet.xsl" ;

  private static final String PDF_REQUEST_PATH_WITHSTYLESHEET =
      PDF_REQUEST_PATH +
      "?" + RequestTools.ALTERNATE_STYLESHEET_PARAMETER_NAME +
      "=" + STYLESHEET_RESOURCENAME
  ;

  private static Set< String > TAGSET = ImmutableSet.of( "tag-1", "Tag2" ) ;

  private static final String PDF_REQUEST_PATH_WITHSTYLESHEET_AND_TAGS =
      PDF_REQUEST_PATH +
      "?" +
      RequestTools.TAGSET_PARAMETER_NAME + "=" +
          Joiner.on( RequestTools.LIST_SEPARATOR ).join( TAGSET ) +
      "&" +
      RequestTools.ALTERNATE_STYLESHEET_PARAMETER_NAME + "=" + STYLESHEET_RESOURCENAME
  ;

  private static final String REQUEST_PATH_BROKEN =
      PDF_REQUEST_PATH + RequestTools.ERRORPAGE_SUFFIX ;

  private static final Log LOG = LogFactory.getLog( RequestTest.class ) ;
  static {
    LOG.info( PDF_REQUEST_PATH ) ;
    LOG.info( PDF_REQUEST_PATH_WITHSTYLESHEET ) ;
    LOG.info( PDF_REQUEST_PATH_WITHSTYLESHEET_AND_TAGS ) ;
    LOG.info( REQUEST_PATH_BROKEN ) ;
  }



}

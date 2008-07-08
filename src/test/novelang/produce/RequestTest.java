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

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import org.apache.commons.lang.StringUtils;
import novelang.rendering.RenditionMimeType;

/**
 * @author Laurent Caillette
 */
public class RequestTest {
  private static final String REQUEST_BODY = "/path/to/file" ;

  private static final String PDF_REQUEST_PATH = REQUEST_BODY + ".pdf" ;
  private static final String CSS_REQUEST_PATH = REQUEST_BODY + ".css" ;

  private static final String STYLESHEET_RESOURCENAME = "dir/sheet.xsl" ;

  private static final String PDF_REQUEST_PATH_WITHSTYLESHEET =
      PDF_REQUEST_PATH +
      "?" + RequestTools.ALTERNATE_STYLESHEET_PARAMETER_NAME +
      "=" + STYLESHEET_RESOURCENAME
  ;

  private static final String REQUEST_PATH_BROKEN =
      PDF_REQUEST_PATH + RequestTools.ERRORPAGE_SUFFIX ;

  @Test
  public void documentRequest() {
    final DocumentRequest request =
        RequestTools.createDocumentRequest( PDF_REQUEST_PATH ); ;
    assertEquals( PDF_REQUEST_PATH, request.getOriginalTarget() ) ;
    assertEquals( RenditionMimeType.PDF, request.getRenditionMimeType() ) ;
    assertTrue( request.isRendered() ) ;
    assertEquals( "pdf", request.getResourceExtension() ) ;
    assertEquals( REQUEST_BODY, request.getDocumentSourceName() ) ;

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
}

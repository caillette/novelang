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
import org.apache.commons.lang.StringUtils;
import novelang.rendering.RenditionMimeType;

/**
 * @author Laurent Caillette
 */
public class RequestTest {
  private static final String REQUEST_BODY = "/path/to/file" ;

  private static final String PDF_REQUEST_PATH = REQUEST_BODY + ".pdf" ;
  private static final String CSS_REQUEST_PATH = REQUEST_BODY + ".css" ;

  private static final String REQUEST_PATH_BROKEN =
      PDF_REQUEST_PATH + RequestTools.ERRORPAGE_SUFFIX ;

  @Test
  public void documentRequest() {
    final DocumentRequest request =
        RequestTools.createDocumentRequest( PDF_REQUEST_PATH ); ;
    Assert.assertEquals( PDF_REQUEST_PATH, request.getOriginalTarget() ) ;
    Assert.assertEquals( RenditionMimeType.PDF, request.getRenditionMimeType() ) ;
    Assert.assertTrue( request.isRendered() ) ;
    Assert.assertEquals( "pdf", request.getResourceExtension() ) ;
    Assert.assertEquals( REQUEST_BODY, request.getDocumentSourceName() ) ;

    Assert.assertFalse( StringUtils.isBlank( request.toString() ) ) ;
  }

  @Test
  public void polymorphicRequestForError() {
    final PolymorphicRequest request =
        RequestTools.createPolymorphicRequest( REQUEST_PATH_BROKEN ) ;
    Assert.assertTrue( request.getDisplayProblems() ) ;
    Assert.assertEquals( PDF_REQUEST_PATH, request.getOriginalTarget() ) ;
    Assert.assertEquals( RenditionMimeType.PDF, request.getRenditionMimeType() ) ;
    Assert.assertEquals( REQUEST_BODY, request.getDocumentSourceName() ) ;

    Assert.assertFalse( StringUtils.isBlank( request.toString() ) ) ;
  }

  @Test
  public void polymorphicRequestForRawResource() {
    final PolymorphicRequest request =
        RequestTools.createPolymorphicRequest( CSS_REQUEST_PATH ) ;
    Assert.assertFalse( request.getDisplayProblems() ) ;
    Assert.assertEquals( CSS_REQUEST_PATH, request.getOriginalTarget() ) ;
    Assert.assertNull( request.getRenditionMimeType() ) ;
    Assert.assertEquals( REQUEST_BODY, request.getDocumentSourceName() ) ;

    Assert.assertFalse( StringUtils.isBlank( request.toString() ) ) ;
  }
}

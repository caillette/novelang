/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.jetty;

import org.junit.Assert;
import org.junit.Test;
import novelang.model.common.StructureKind;
import novelang.rendering.DocumentRequest;
import novelang.rendering.RenditionMimeType;

/**
 * @author Laurent Caillette
 */
public class HttpDocumentRequestTest {
  private static final String REQUEST_SOURCEFILE = "/path/to/file" ;

  private static final String REQUEST_PATH = "/part" + REQUEST_SOURCEFILE + ".pdf" ;
  private static final String REQUEST_URL = REQUEST_PATH ;

  private static final String REQUEST_PATH_BROKEN =
      REQUEST_PATH + HttpDocumentRequest.ERRORPAGE_SUFFIX ;

  @Test
  public void testInterpretDocumentRequest() {
    final DocumentRequest documentRequest =
        HttpDocumentRequest.create( REQUEST_PATH ) ;
    Assert.assertFalse( documentRequest.getDisplayProblems() ) ;
    Assert.assertEquals( REQUEST_URL, documentRequest.getOriginalTarget() ) ;
    Assert.assertEquals( RenditionMimeType.PDF, documentRequest.getRenditionMimeType() ) ;
    Assert.assertTrue( documentRequest.isRendered() ) ;
    Assert.assertEquals( "pdf", documentRequest.getResourceExtension() ) ;
    Assert.assertEquals( REQUEST_SOURCEFILE, documentRequest.getDocumentSourceName() ) ;
    Assert.assertEquals( StructureKind.PART, documentRequest.getStructureKind() ) ;
  }

  @Test
  public void testInterpretProblemDisplayRequest() {
    final DocumentRequest documentRequest =
        HttpDocumentRequest.create( REQUEST_PATH_BROKEN ) ;
    Assert.assertTrue( documentRequest.getDisplayProblems() ) ;
    Assert.assertEquals( REQUEST_URL, documentRequest.getOriginalTarget() ) ;
    Assert.assertEquals( RenditionMimeType.PDF, documentRequest.getRenditionMimeType() ) ;
    Assert.assertEquals( REQUEST_SOURCEFILE, documentRequest.getDocumentSourceName() ) ;
    Assert.assertEquals( StructureKind.PART, documentRequest.getStructureKind() ) ;
  }
}

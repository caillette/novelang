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

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import novelang.designator.Tag;
import novelang.designator.TagTestTools;
import novelang.logger.Logger;
import novelang.logger.LoggerFactory;
import novelang.rendering.RenditionMimeType;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Laurent Caillette
 */
public class RequestTest {

  @Test
  public void documentRequest() {
    final DocumentRequest request = createDocumentRequest( PDF_REQUEST_PATH ) ;
    assertEquals( PDF_REQUEST_PATH, request.getOriginalTarget() ) ;
    assertEquals( RenditionMimeType.PDF, request.getRenditionMimeType() ) ;
    assertTrue( request.isRendered() ) ;
    assertEquals( "pdf", request.getResourceExtension() ) ;
    assertEquals( SIMPLE_REQUEST_BODY, request.getDocumentSourceName() ) ;
    assertTrue( "Got: " + request.getTags(), request.getTags().isEmpty() ) ;

    assertFalse( StringUtils.isBlank( request.toString() ) ) ;
    assertNull( request.getAlternateStylesheet() ) ;
  }

  @Test
  public void documentRequestWithStylesheet() {
    final DocumentRequest request = createDocumentRequest( PDF_REQUEST_PATH_WITHSTYLESHEET ) ;
    assertEquals( PDF_REQUEST_PATH, request.getOriginalTarget() ) ;
    assertEquals( RenditionMimeType.PDF, request.getRenditionMimeType() ) ;
    assertTrue( request.isRendered() ) ;
    assertEquals( "pdf", request.getResourceExtension() ) ;
    assertEquals( SIMPLE_REQUEST_BODY, request.getDocumentSourceName() ) ;
    assertNotNull( request.getAlternateStylesheet() ) ;
    assertEquals( STYLESHEET_RESOURCENAME, request.getAlternateStylesheet().getName() ) ;

    assertFalse( StringUtils.isBlank( request.toString() ) ) ;
  }

  @Test
  public void documentRequestWithDots() {
    final DocumentRequest request = createDocumentRequest( DOTTEDHTML_REQUEST_PATH ) ;
    assertEquals( DOTTEDHTML_REQUEST_PATH, request.getOriginalTarget() ) ;
    assertEquals( RenditionMimeType.HTML, request.getRenditionMimeType() ) ;
    assertTrue( request.isRendered() ) ;
    assertEquals( DOTTED_REQUEST_BODY, request.getDocumentSourceName() ) ;

    assertTrue( "Got: " + request.getTags(), request.getTags().isEmpty() ) ;

    assertFalse( StringUtils.isBlank( request.toString() ) ) ;
    assertNull( request.getAlternateStylesheet() ) ;
    
  }



  @Test
  public void polymorphicRequestForError() {
    final PolymorphicRequest request = createPolymorphicRequest( REQUEST_PATH_BROKEN ) ;

    assertTrue( request.getDisplayProblems() ) ;
    assertEquals( PDF_REQUEST_PATH, request.getOriginalTarget() ) ;
    assertEquals( RenditionMimeType.PDF, request.getRenditionMimeType() ) ;
    assertEquals( SIMPLE_REQUEST_BODY, request.getDocumentSourceName() ) ;

    assertFalse( StringUtils.isBlank( request.toString() ) ) ;
  }

  @Test
  public void polymorphicRequestForRawResource() {
    final PolymorphicRequest request = createPolymorphicRequest( CSS_REQUEST_PATH ) ;
    assertFalse( request.getDisplayProblems() ) ;
    assertEquals( CSS_REQUEST_PATH, request.getOriginalTarget() ) ;
    assertNull( request.getRenditionMimeType() ) ;
    assertEquals( SIMPLE_REQUEST_BODY, request.getDocumentSourceName() ) ;

    assertFalse( StringUtils.isBlank( request.toString() ) ) ;
  }

  @Test
  public void polymorphicRequestWithTags() {
    final PolymorphicRequest request =
        createPolymorphicRequest( PDF_REQUEST_PATH_WITHSTYLESHEET_AND_TAGS ) ;
    assertFalse( request.getDisplayProblems() ) ;
    assertEquals( PDF_REQUEST_PATH, request.getOriginalTarget() ) ;
    assertEquals( RenditionMimeType.PDF, request.getRenditionMimeType() ) ;
    assertEquals( SIMPLE_REQUEST_BODY, request.getDocumentSourceName() ) ;
    assertEquals( TAGSET, request.getTags() ) ;

    assertFalse( StringUtils.isBlank( request.toString() ) ) ;
  }

  @Test
  public void polymorphicRequestWithSuspiciousTagDefinition() {
    final PolymorphicRequest request =
        createPolymorphicRequest( PDF_REQUEST_PATH_WITH_ILL_FORMED_TAGS ) ;
    assertFalse( request.getDisplayProblems() ) ;
    assertEquals( PDF_REQUEST_PATH, request.getOriginalTarget() ) ;
    assertEquals( RenditionMimeType.PDF, request.getRenditionMimeType() ) ;
    assertEquals( SIMPLE_REQUEST_BODY, request.getDocumentSourceName() ) ;
    assertEquals( TAGSET, request.getTags() ) ;

    assertFalse( StringUtils.isBlank( request.toString() ) ) ;
  }



// =======
// Fixture
// =======

  private static final String SIMPLE_REQUEST_BODY = "/path/to/file" ;
  private static final String DOTTED_REQUEST_BODY = "/path/to/0.1.2" ;

  private static final String PDF_REQUEST_PATH = SIMPLE_REQUEST_BODY + ".pdf" ;
  private static final String CSS_REQUEST_PATH = SIMPLE_REQUEST_BODY + ".css" ;
  private static final String DOTTEDHTML_REQUEST_PATH = DOTTED_REQUEST_BODY + ".html" ;

  private static final String STYLESHEET_RESOURCENAME = "dir/sheet.xsl" ;

  private static final String PDF_REQUEST_PATH_WITHSTYLESHEET =
      PDF_REQUEST_PATH +
      "?" + RequestTools.ALTERNATE_STYLESHEET_PARAMETER_NAME +
      "=" + STYLESHEET_RESOURCENAME
  ;

  private static final Set< Tag > TAGSET = 
      ImmutableSet.of( new Tag( "tag-1" ), new Tag( "Tag2" ) ) ;

  private static final String PDF_REQUEST_PATH_WITHSTYLESHEET_AND_TAGS ;
  static {
    final Set< String > tagsAsString= Sets.newHashSet() ;
    for( final Tag tag : TAGSET ) {
      tagsAsString.add( TagTestTools.getTagAsString( tag ) ) ;
    }

    PDF_REQUEST_PATH_WITHSTYLESHEET_AND_TAGS =
        PDF_REQUEST_PATH +
        "?" +
        RequestTools.TAGSET_PARAMETER_NAME + "=" +
            Joiner.on( RequestTools.LIST_SEPARATOR ).join( tagsAsString ) +
        "&" +
        RequestTools.ALTERNATE_STYLESHEET_PARAMETER_NAME + "=" + STYLESHEET_RESOURCENAME
    ;
  }

  private static final Set< String > TAGS_AS_STRINGSET = Sets.newHashSet();
  static {
    for( final Tag tag : TAGSET ) {
      TAGS_AS_STRINGSET.add( TagTestTools.getTagAsString( tag ) ) ;
    }
  }


  private static final String PDF_REQUEST_PATH_WITH_ILL_FORMED_TAGS ;
  static {
    PDF_REQUEST_PATH_WITH_ILL_FORMED_TAGS =
        PDF_REQUEST_PATH +
        "?" +
        RequestTools.TAGSET_PARAMETER_NAME + "=;" +
            Joiner.on( RequestTools.LIST_SEPARATOR ).join(TAGS_AS_STRINGSET)
    ;
  }

  private static final String REQUEST_PATH_BROKEN =
      PDF_REQUEST_PATH + RequestTools.ERRORPAGE_SUFFIX ;

  private static final Logger LOGGER = LoggerFactory.getLogger( RequestTest.class ) ;

  private static DocumentRequest createDocumentRequest( final String requestString ) {
    LOGGER.info( "Using ", requestString ) ;
    return RequestTools.createDocumentRequest( requestString ) ;
  }

  private static PolymorphicRequest createPolymorphicRequest( final String requestString ) {
    LOGGER.info( "Using ", requestString ) ;
    return RequestTools.createPolymorphicRequest( requestString ) ;
  }



}

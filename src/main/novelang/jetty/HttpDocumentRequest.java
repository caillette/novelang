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

package novelang.jetty;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import novelang.model.common.StructureKind;
import novelang.rendering.DocumentRequest;
import novelang.rendering.RawResource;
import novelang.rendering.RenditionMimeType;

/**
 * @author Laurent Caillette
 */
public class HttpDocumentRequest implements DocumentRequest {

  private static final Logger LOGGER = LoggerFactory.getLogger( HttpDocumentRequest.class ) ;

  public static final String ERRORPAGE_SUFFIX = "/error.html";
  private static final String ERRORPAGE_SUFFIX_REGEX = "/error\\.html";

  private HttpDocumentRequest() { }

  @Override
  public String toString() {
    return
        ClassUtils.getShortClassName( getClass() ) + "[" +
        "isErrorRequest" + "=" + getDisplayProblems() +
        ";documentMimeType" + "=" +
            ( renditionMimeType == null ? "<null>" : renditionMimeType.getMimeName() ) +
        ";structureKind" + "=" + getStructureKind() +
        ";documentSourceName" + "=" + getDocumentSourceName() +
        ";originalTarget" + "=" + getOriginalTarget() +
        "]"
    ;
  }


// ================
// documentMimeType
// ================

  private RenditionMimeType renditionMimeType = RenditionMimeType.PDF ;

  public RenditionMimeType getRenditionMimeType() {
    if( null == renditionMimeType ) {
      throw new IllegalStateException( "No renditionMimeType defined " ) ;
    }
    return renditionMimeType;
  }

  private void setRenditionMimeType( RenditionMimeType renditionMimeType ) {
    this.renditionMimeType = renditionMimeType;
  }

  public boolean isRendered() {
    return null != renditionMimeType ;
  }

// ============
// errorRequest
// ============

  private boolean displayProblems = false ;

  public boolean getDisplayProblems() {
    return displayProblems;
  }

  private void setDisplayProblems( boolean displayProblems ) {
    this.displayProblems = displayProblems;
  }


// =============
// structureKind
// =============

  private StructureKind structureKind;

  public StructureKind getStructureKind() {
    return structureKind;
  }

  private void setStructureKind( StructureKind structureKind ) {
    this.structureKind = structureKind;
  }


// =================
// ResourceExtension
// =================

  private String resourceExtension ;

  public String getResourceExtension() {
    return resourceExtension ;
  }

  private void setResourceExtension( String resourceExtension ) {
    this.resourceExtension = resourceExtension;
  }


// ==================
// documentSourceName
// ==================

  private String documentSourceName ;

  public String getDocumentSourceName() {
    return documentSourceName;
  }

  private void setDocumentSourceName( String documentSourceName ) {
    this.documentSourceName = documentSourceName;
  }

// ==============
// originalTarget
// ==============

  private String originalTarget ;

  public String getOriginalTarget() {
    return originalTarget;
  }

  private void setOriginalTarget( String originalTarget ) {
    this.originalTarget = originalTarget;
  }

// =====================
// targetNoStructureKind
// =====================

  public String getTargetNoStructureKind() {
    throw new UnsupportedOperationException( "getTargetNoStructureKind" ) ;
  }

// =======
// Factory
// =======

  /**
   * Parsing request path. Dynamically crafted according to enums.
   */
  private static Pattern PATTERN ;
  static {
    final StringBuffer buffer = new StringBuffer() ;

    // First the structure.
    buffer.append( "(/(" ) ;
    for( final StructureKind structureKind : StructureKind.values() ) {
      if( structureKind.ordinal() > 0 ) {
        buffer.append( "|" ) ;
      }
      buffer.append( structureKind.getPathToken() ) ;
    }
    buffer.append( ")" ) ;

    // Now the path without extension. No dots for security reasons (forbid '..').
    buffer.append( "((?:\\/(?:\\w|-|_)+)+)" ) ;

    // The extension defining the MIME type.
    buffer.append( "(?:\\.(" ) ;

    final List< String > allExtensions = Lists.newArrayList() ;
    Iterables.addAll( allExtensions, RenditionMimeType.getFileExtensions() ) ;
    Iterables.addAll( allExtensions, RawResource.getFileExtensions() ) ;

    boolean first = true ;
    for( final String fileExtension : allExtensions ) {
      if( first ) {
        first = false ;
      } else {
        buffer.append( "|" ) ;
      }
      buffer.append( fileExtension ) ;
    }
    buffer.append( ")))" ) ;

    // Maybe an error report.
    buffer.append( "(" + ERRORPAGE_SUFFIX_REGEX + ")?" ) ;

    PATTERN = Pattern.compile( buffer.toString() ) ;
    LOGGER.debug( "Crafted regex {}.", PATTERN.pattern() ) ;
  }


//  public static DocumentRequest create( HttpServletRequest httpRequest )
//      throws IllegalArgumentException
//  {
//    final String requestPath = httpRequest.getPathInfo() ;
//    return createDocumentRequest( requestPath ) ;
//  }

  public static DocumentRequest create( String requestPath )
      throws IllegalArgumentException
  {

    final Matcher matcher = PATTERN.matcher( requestPath ) ;
    if( matcher.find() && matcher.groupCount() >= 3 ) {

      final HttpDocumentRequest request = new HttpDocumentRequest() ;

      final String rawStructure = matcher.group( 2 ) ;
      request.setStructureKind( StructureKind.valueOf( rawStructure.toUpperCase() ) ) ;

      final String rawDocumentSourceName = matcher.group( 3 ) ;
      request.setDocumentSourceName( rawDocumentSourceName ) ;

      final String rawDocumentMimeType = matcher.group( 4 ) ;
      request.setResourceExtension( rawDocumentMimeType ) ;
      try {
        request.setRenditionMimeType(
          RenditionMimeType.valueOf( rawDocumentMimeType.toUpperCase() ) ) ;
      } catch( IllegalArgumentException e ) {
        request.setRenditionMimeType( null ) ;
      }

      request.setDisplayProblems( ERRORPAGE_SUFFIX.equals( matcher.group( 5 ) ) ) ;

      request.setOriginalTarget( matcher.group( 1 ) ) ;

      LOGGER.debug( "Parsed: {}", request ) ;

      return request ;

    } else {
      return null ;
    }

  }

}

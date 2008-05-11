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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import novelang.model.common.StructureKind;
import novelang.rendering.RawResource;
import novelang.rendering.RenditionMimeType;

/**
 * Contains everything needed to build a specific requested document.
 * <p>
 * TODO add more classes: AbstractRequest (base), ProblemDocumentRequest, ResourceRequest.
 * <p>
 * TODO remove the copy-paste from novelang.jetty.DocumentRequest.
 * <p>@
 * TODO Use some factory method to create the regex parser, with two parameters:
 * 1) allow error request 2) allow raw resource request.
 *
 * @author Laurent Caillette
 */
public class DocumentRequest {

  private static final Logger LOGGER = LoggerFactory.getLogger( DocumentRequest.class ) ;

  @Override
  public String toString() {
    return
        ClassUtils.getShortClassName( getClass() ) + "[" +
        ";structureKind" + "=" + getStructureKind() +
        ";documentSourceName" + "=" + getDocumentSourceName() +
        ";originalTarget" + "=" + getOriginalTarget() +
        "]"
    ;
  }


// ==============
// originalTarget
// ==============

  private String originalTarget ;

  /**
   * Returns the initial request, as part of HTTP query or as command-line argument,
   * that suffices to construct this object.
   * For a HTTP query like {@code http://localhost:8080/part/samples/showcase/showcase.html}
   * it returns the value {@code /part/samples/showcase/showcase.html}
   *
   * @return a non-null, non-empty String.
   */
  public String getOriginalTarget() {
    return originalTarget;
  }

  private void setOriginalTarget( String originalTarget ) {
    this.originalTarget = originalTarget;
  }


// ================
// documentMimeType
// ================

  private RenditionMimeType renditionMimeType = RenditionMimeType.PDF ;

  /**
   * Returns the {@code RenditionMimeType}.
   * @return a non-null object.
   * @throws IllegalArgumentException if no {@code RenditionMimeType} was defined.
   */
  public RenditionMimeType getRenditionMimeType() {
    if( null == renditionMimeType ) {
      throw new IllegalStateException( "No renditionMimeType defined " ) ;
    }
    return renditionMimeType;
  }

  private void setRenditionMimeType( RenditionMimeType renditionMimeType ) {
    this.renditionMimeType = renditionMimeType;
  }


// =============
// structureKind
// =============

  private StructureKind structureKind;

  /**
   * Wether it's a {@link StructureKind#PART} or {@link StructureKind#BOOK}.
   * @return a non-null object.
   */
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

  /**
   * Returns the extension for this resource (without the dot).
   * @return a non-null, non-empty String.
   */
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

  /**
   * Returns the document as requested, without extension.
   * For a HTTP query like {@code http://localhost:8080/part/samples/showcase/showcase.html}
   * it returns the value {@code /samples/showcase/showcase}
   * @return  a non-null, non-empty String.
   */
  public String getDocumentSourceName() {
    return documentSourceName;
  }

  private void setDocumentSourceName( String documentSourceName ) {
    this.documentSourceName = documentSourceName;
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

    PATTERN = Pattern.compile( buffer.toString() ) ;
    LOGGER.debug( "Crafted regex {}.", PATTERN.pattern() ) ;
  }



  public static DocumentRequest create( String requestPath )
      throws IllegalArgumentException
  {

    final Matcher matcher = PATTERN.matcher( requestPath ) ;
    if( matcher.find() && matcher.groupCount() >= 3 ) {

      final DocumentRequest request = new DocumentRequest() ;

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

      request.setOriginalTarget( matcher.group( 1 ) ) ;

      LOGGER.debug( "Parsed: {}", request ) ;

      return request ;

    } else {
      return null ;
    }

  }


}


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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import novelang.rendering.RawResource;
import novelang.rendering.RenditionMimeType;
import novelang.loader.ResourceName;

/**
 * The base class for requests and a do-everything factory.
 *
 * @see RequestTools for getting instances.
 *
 * @author Laurent Caillette
 */
/*package*/ abstract class AbstractRequest {

  private static final Logger LOGGER = LoggerFactory.getLogger( AbstractRequest.class ) ;

  private static final String ERRORPAGE_SUFFIX_REGEX = "/error\\.html";

// ================
// documentMimeType
// ================

  private RenditionMimeType renditionMimeType = RenditionMimeType.PDF ;

  public RenditionMimeType getRenditionMimeType() {
    return renditionMimeType;
  }

  private void setRenditionMimeType( RenditionMimeType renditionMimeType ) {
    this.renditionMimeType = renditionMimeType;
  }

  public boolean isRendered() {
    return null != renditionMimeType ;
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


// ===================
// alternateStylesheet
// ===================

  private ResourceName alternateStylesheet = null ;

  public ResourceName getAlternateStylesheet() {
    return alternateStylesheet ;
  }

  private void setAlternateStylesheet( ResourceName resourceName ) {
    this.alternateStylesheet = resourceName ;
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


// =======
// Factory
// =======

  private static Pattern createPattern( boolean polymorphic ) {
    final StringBuffer buffer = new StringBuffer() ;

    buffer.append( "(" ) ;

    // The path without extension. No dots for security reasons (forbid '..').
    buffer.append( "((?:\\/(?:\\w|-|_)+)+)" ) ;

    // The extension defining the MIME type.
    buffer.append( "(?:\\.(" ) ;

    final List< String > allExtensions = Lists.newArrayList() ;
    Iterables.addAll( allExtensions, RenditionMimeType.getFileExtensions() ) ;
    if( polymorphic ) {
      Iterables.addAll( allExtensions, RawResource.getFileExtensions() ) ;
    }

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

    if( polymorphic ) {
      buffer.append( "(" + ERRORPAGE_SUFFIX_REGEX + ")?" ) ;
    }

    buffer.append( "(?:\\?(?:" );
    buffer.append( RequestTools.ALTERNATE_STYLESHEET_PARAMETER_NAME ) ;
    buffer.append( "\\=)" ) ;
    buffer.append( "(" ) ; // Start of capturing group.
      buffer.append( ResourceName.PATTERN.pattern() ) ;
    buffer.append( ")" ) ; // End of capturing group.
    buffer.append( ")?" ) ;

    return Pattern.compile( buffer.toString() ) ;
  }
  
  private static Pattern DOCUMENT_ONLY_PATTERN = createPattern( false ) ;
  private static Pattern POLYMORPHIC_PATTERN = createPattern( true ) ;

  static {
    LOGGER.debug( "Crafted regex for Document only: {}", DOCUMENT_ONLY_PATTERN.pattern() ) ;
    LOGGER.debug( "Crafted regex for Polymorphic requests: {}", POLYMORPHIC_PATTERN.pattern() ) ;
  }



  public static DocumentRequest createDocumentRequest( String requestPath ) {
    return create( requestPath, new DocumentRequest() ) ;
  }

  public static DocumentRequest forgeDocumentRequest(
      String documentName,
      RenditionMimeType renditionMimeType,
      ResourceName stylesheet
  ) {
    final AbstractRequest documentRequest = new DocumentRequest() ;
    documentRequest.setDocumentSourceName( documentName ) ;
    documentRequest.setRenditionMimeType( renditionMimeType ) ;
    documentRequest.setAlternateStylesheet( stylesheet ) ;
    return ( DocumentRequest ) documentRequest ;
  }

  public static PolymorphicRequest createPolymorphicRequest( String requestPath ) {
    return create( requestPath, new PolymorphicRequest() ) ;
  }

  private static < T extends AbstractRequest > T create(
      String requestPath,
      T request
  )
      throws IllegalArgumentException
  {
    final Pattern pattern = request instanceof PolymorphicRequest ?
        POLYMORPHIC_PATTERN :
        DOCUMENT_ONLY_PATTERN
    ;

    final Matcher matcher = pattern.matcher( requestPath ) ;
    if( matcher.find() && matcher.groupCount() >= 2 ) {

      final String rawDocumentSourceName = matcher.group( 2 ) ;
      request.setDocumentSourceName( rawDocumentSourceName ) ;

      final String rawDocumentMimeType = matcher.group( 3 ) ;
      request.setResourceExtension( rawDocumentMimeType ) ;
      try {
        request.setRenditionMimeType(
          RenditionMimeType.valueOf( rawDocumentMimeType.toUpperCase() ) ) ;
      } catch( IllegalArgumentException e ) {
        request.setRenditionMimeType( null ) ;
      }

      if( request instanceof PolymorphicRequest ) {
        ( ( PolymorphicRequest ) request ).setDisplayProblems(
            RequestTools.ERRORPAGE_SUFFIX.equals( matcher.group( 4 ) ) ) ;
      }

      request.setOriginalTarget( matcher.group( 1 ) ) ;

      final String alternateStylesheet = matcher.group( 5 ) ;
      if( ! StringUtils.isBlank( alternateStylesheet ) ) {
        request.setAlternateStylesheet( new ResourceName( alternateStylesheet ) ) ;
      }

      LOGGER.debug( "Parsed: {}", request ) ;

      return request ;

    } else {
      return null ;
    }

  }

}
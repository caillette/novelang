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
package org.novelang.produce;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;
import org.novelang.common.metadata.PageIdentifier;
import org.novelang.designator.Tag;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.loader.ResourceName;
import org.novelang.rendering.RawResource;
import org.novelang.rendering.RenderingTools;
import org.novelang.rendering.RenditionMimeType;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Unique implementation encapsulating the request for a rendered document or a resource.
 *
 * TODO: use ANTLR for parsing.
 *
 * @author Laurent Caillette
 */
public final class GenericRequest implements DocumentRequest, ResourceRequest {

  private static final Logger LOGGER = LoggerFactory.getLogger( GenericRequest.class ) ;

  public static final String ERRORPAGE_SUFFIX = "/error.html";
  public static final String TAGSET_PARAMETER_NAME= "tags" ;
  public static final ImmutableSet< String > SUPPORTED_PARAMETER_NAMES =
      ImmutableSet.of( ALTERNATE_STYLESHEET_PARAMETER_NAME, TAGSET_PARAMETER_NAME ) ;

  /**
   * <a href="http://www.ietf.org/rfc/rfc2396.txt" >RFC</a> p. 26-27.
   */
  public static final String LIST_SEPARATOR = ";" ;


// =======
// For all
// =======

  private final String originalTarget ;

  @Override
  public String getOriginalTarget() {
    return originalTarget ;
  }

  private final String documentSourceName ;

  @Override
  public String getDocumentSourceName() {
    return documentSourceName;
  }

  private final boolean rendered ;

  @Override
  public boolean isRendered() {
    return rendered ;
  }


// ========
// Rendered
// ========

  private final RenditionMimeType renditionMimeType ;

  @Override
  public RenditionMimeType getRenditionMimeType() {
    return renditionMimeType ;
  }

  private final ResourceName alternateStylesheet ;

  @Override
  public ResourceName getAlternateStylesheet() {
    return alternateStylesheet ;
  }

  private final ImmutableSet< Tag > tags ;

  @Override
  public ImmutableSet< Tag > getTags() {
    return tags ;
  }

  private final boolean displayProblems ;

  @Override
  public boolean getDisplayProblems() {
    return displayProblems ;
  }

  private final PageIdentifier pageIdentifier ;

  @Override
  public PageIdentifier getPageIdentifier() {
    return pageIdentifier ;
  }
// ============
// Non-rendered
// ============

  private final String resourceExtension ;

  /**
   * Always null if {@link #isRendered()} is true.
   * Never null nor blank if {@link #isRendered()} is false.
   *
   * @return a {@code String} that may be null, but never blank.
   */
  @Override
  public String getResourceExtension() {
    return resourceExtension ;
  }


// ============
// Constructors
// ============

  private GenericRequest(
      final String originalTarget,
      final String documentSourceName,
      final boolean displayProblems,
      final RenditionMimeType renditionMimeType,
      final PageIdentifier pageIdentifier,
      final ResourceName alternateStylesheet,
      final ImmutableSet<Tag> tags
  ) {
    this.pageIdentifier = pageIdentifier;
    checkHasCharacters( originalTarget ) ;
    checkHasCharacters( documentSourceName ) ;
    this.documentSourceName = documentSourceName ;
    this.rendered = true ;
    this.renditionMimeType = checkNotNull( renditionMimeType ) ;
    this.alternateStylesheet = alternateStylesheet ;
    this.tags = checkNotNull( tags ) ;
    this.displayProblems = displayProblems ;

    this.resourceExtension = null ;
    this.originalTarget = rebuildOriginalTarget() ;
  }

  private String rebuildOriginalTarget() {
    final ImmutableList.Builder< String > parametersBuilder = ImmutableList.builder() ;
    if( alternateStylesheet != null ) {
      parametersBuilder.add(
          ALTERNATE_STYLESHEET_PARAMETER_NAME + "=" + alternateStylesheet.getName() ) ;
    }
    if( ! getTags().isEmpty() ) {
      final Iterable< String > tagNames = Iterables.transform( getTags(), Tag.EXTRACT_TAG_NAME ) ;

      parametersBuilder.add( TAGSET_PARAMETER_NAME + "=" +
          Joiner.on( LIST_SEPARATOR ).join( tagNames ) ) ;
    }
    final ImmutableList< String > parameters = parametersBuilder.build() ;
    return documentSourceName
        + ( pageIdentifier == null ? "" : PAGEIDENTIFIER_PREFIX + pageIdentifier.getName() )
        + "." + renditionMimeType.getFileExtension()
        + ( parameters.isEmpty() ? "" : "?" + Joiner.on( "&" ).join( parameters ) ) ;
  }

  private GenericRequest(
      final String originalTarget,
      final String documentSourceName,
      final String resourceExtension
  ) {
    checkHasCharacters( originalTarget ) ;
    this.originalTarget = documentSourceName + "." + resourceExtension ;
    checkHasCharacters( documentSourceName ) ;
    this.documentSourceName = documentSourceName ;
    this.rendered = false ;
    checkHasCharacters( resourceExtension ) ;
    this.resourceExtension = resourceExtension ;

    renditionMimeType = null ;
    pageIdentifier = null ;
    alternateStylesheet = null ;
    tags = null ;
    displayProblems = false ;
  }

  private static void checkHasCharacters( final String string ) {
    checkArgument( ! StringUtils.isBlank( string ) ) ;
  }

  
// =======
// Parsing
// =======

  private static final String TAG_PATTERN = "[a-zA-Z0-9][a-zA-Z0-9\\-_]*"  ;

  /**
   * Allow non-word characters only in the middle of word characters and if they are not
   * consecutive.
   */
  private static final String PATH_SEGMENT_PATTERN = "[A-Za-z0-9]+(?:(?:-|_|\\.)[A-Za-z0-9]+)*" ;


  private static Pattern createPattern() {
    final StringBuilder buffer = new StringBuilder() ;

    buffer.append( "(" ) ;

    // The path without extension. No double dots for security reasons (forbid '..').
    buffer.append( "((?:\\/" + PATH_SEGMENT_PATTERN + ")+)" ) ;

    // Page identifier.
    buffer.append( "(?:--(" ) ;
    buffer.append( PageIdentifier.PATTERN.pattern() ) ;
    buffer.append( "))?" );

    // The extension defining the MIME type.
    buffer.append( "(?:\\.(" ) ;

    final ImmutableList< String > allExtensions = ImmutableList.< String >builder()
        .addAll( RenditionMimeType.getFileExtensions() )
        .addAll( RawResource.getFileExtensions() )
        .build()
    ;

    buffer.append( Joiner.on( "|" ).join( allExtensions ) ) ;
    buffer.append( "))" ) ;
    buffer.append( ")" ) ;

    // This duplicates the 'tag' rule in ANTLR grammar. Shame.
    final String parameter = "([a-zA-Z0-9\\-\\=_&\\./" + LIST_SEPARATOR + "]+)" ;

    buffer.append( "(?:\\?" ) ;
    buffer.append( parameter ) ;
    buffer.append( ")?" ) ;

    return Pattern.compile( buffer.toString() ) ;
  }

  private static final Pattern DOCUMENT_PATTERN = createPattern() ;
  static {
    LOGGER.debug( "Crafted regex: ", DOCUMENT_PATTERN.pattern() ) ;
  }

  private static String extractExtension( final String path ) throws MalformedRequestException {
    final Matcher matcher = DOCUMENT_PATTERN.matcher( path ) ;
    if( matcher.find() && matcher.groupCount() >= 4 ) {
      return matcher.group( 4 ) ;
    } else {
      throw new MalformedRequestException( "Doesn't contain an extension: '" + path + "'" ) ;
    }
  }

  private static ImmutableMap< String, String > getQueryMap( final String query )
      throws MalformedRequestException
  {
    if( StringUtils.isBlank( query ) ) {
      return ImmutableMap.of() ;
    } else {
      final Iterable< String > params = Splitter.on( '&' ).split( query ) ;
      final ImmutableMap.Builder< String, String > map = ImmutableMap.builder() ;
      for( final String param : params ) {
        final ImmutableList< String > strings =
            ImmutableList.copyOf( Splitter.on( '=' ).split( param ) ) ;
        final String name = strings.get( 0 ) ;
        if( strings.size() > 2 ) {
          throw new MalformedRequestException( "Multiple '=' for parameter " + name ) ;
        }
        final String value ;
        if( strings.size() > 1 ) {
          value = strings.get( 1 ) ;
        } else {
          value = null ;
        }
        if( map.build().keySet().contains( name ) ) {
          throw new MalformedRequestException( "Duplicate value for parameter " + name ) ;
        }
        map.put( name, value ) ;
      }
      return map.build() ;
    }
  }


  private static ResourceName extractResourceName(
      final ImmutableMap< String, String > parameterMap
  ) {
    final String parameterValue = parameterMap.get(
        ALTERNATE_STYLESHEET_PARAMETER_NAME ) ;
    if( parameterValue == null ) {
      return null ;
    } else {
      return new ResourceName( parameterValue ) ;
    }
  }

  private static final Pattern TAGS_PATTERN =
      Pattern.compile( TAG_PATTERN + "(?:" + LIST_SEPARATOR + TAG_PATTERN + ")*" ) ;
  private static final Pattern TAGS_SEPARATOR_PATTERN = Pattern.compile( LIST_SEPARATOR ) ;

  private static ImmutableSet< Tag > parseTags( final String value ) throws MalformedRequestException {
    if( TAGS_PATTERN.matcher( value ).matches() ) {
      return RenderingTools.toTagSet( ImmutableSet.copyOf( TAGS_SEPARATOR_PATTERN.split( value ) ) ) ;
    } else {
      throw new MalformedRequestException( "Bad tags: '" + value + "'" ) ;
    }
  }


  private static ImmutableSet< Tag > extractTags(
      final ImmutableMap< String, String > parameterMap
 ) throws MalformedRequestException {
    final String parameterValue = parameterMap.get( TAGSET_PARAMETER_NAME ) ;
    if( parameterValue == null ) {
      return ImmutableSet.of() ;
    } else {
      return parseTags( parameterValue ) ;
    }
  }

  private static void verifyAllParameterNames( final Set< String > parameterNames )
      throws MalformedRequestException
  {
    for( final String parameterName : parameterNames ) {
      if( ! SUPPORTED_PARAMETER_NAMES.contains( parameterName ) ) {
        throw new MalformedRequestException(
            "Unsupported query parameter: '" + parameterName + "'" ) ;
      }
    }
  }


  public static AnyRequest parse( final String originalTarget ) throws MalformedRequestException {
    final Matcher matcher = DOCUMENT_PATTERN.matcher( originalTarget ) ;
    if( matcher.find() && matcher.groupCount() >= 4 ) {

      // Document source name plus extension, minus page identifier.
      final String fullTarget = matcher.group( 2 ) + "." + matcher.group( 4 ) ;

      final boolean showProblems = fullTarget.endsWith( ERRORPAGE_SUFFIX ) ;

      final String targetMinusError ;
      if( showProblems ) {
        targetMinusError = fullTarget.substring(
            0, fullTarget.length() - ERRORPAGE_SUFFIX.length() ) ;
      } else {
        targetMinusError = fullTarget ;
      }

      final String rawDocumentMimeType = extractExtension( targetMinusError ) ;
      final String rawDocumentSourceName = targetMinusError.substring(
            0, targetMinusError.length() - rawDocumentMimeType.length() - 1 ) ;

      final String maybePageIdentifier = matcher.group( 3 );
      final PageIdentifier pageIdentifier =
          maybePageIdentifier == null ? null : new PageIdentifier( maybePageIdentifier ) ;

      final RenditionMimeType renditionMimeType = RenditionMimeType.maybeValueOf(
          rawDocumentMimeType == null ? null : rawDocumentMimeType.toUpperCase() ) ;

      final ImmutableMap< String, String > parameterMap = matcher.groupCount() >= 5 ?
          getQueryMap( matcher.group( 5 ) ) : ImmutableMap.< String, String >of() ;
      verifyAllParameterNames( parameterMap.keySet() ) ;

      final ResourceName alternateStylesheet = extractResourceName( parameterMap ) ;

      final ImmutableSet< Tag > tagset = extractTags( parameterMap ) ;

      final AnyRequest request ;
      if( renditionMimeType == null ) {
        request = new GenericRequest( originalTarget, rawDocumentSourceName, rawDocumentMimeType ) ;
      } else {
        request = new GenericRequest(
            originalTarget,
            rawDocumentSourceName,
            showProblems,
            renditionMimeType,
            pageIdentifier,
            alternateStylesheet,
            tagset
        ) ;
      }
      LOGGER.debug( "Parsed: ", request ) ;

      return request ;

    } else {
      throw new MalformedRequestException( "Could not parse: '" + originalTarget + "'." ) ;
    }

  }


// ================
// java.lang.Object
// ================


  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder( getClass().getSimpleName() + "[" ) ;
    if( isRendered() && getDisplayProblems() ) {
      stringBuilder.append( "displayProblems=true; " ) ;
    }
    stringBuilder.append( getOriginalTarget() ) ;
    stringBuilder.append( "]" ) ;
    return stringBuilder.toString() ;
  }

  @Override
  public boolean equals( final Object other ) {
    if( this == other ) {
      return true ;
    }
    if( other == null || getClass() != other.getClass() ) {
      return false ;
    }

    final GenericRequest that = ( GenericRequest ) other ;

    if( displayProblems != that.displayProblems ) {
      return false ;
    }
    if( rendered != that.rendered ) {
      return false ;
    }
    if( alternateStylesheet != null ?
        ! alternateStylesheet.equals( that.alternateStylesheet )
        : that.alternateStylesheet != null
    ) {
      return false ;
    }
    if( !documentSourceName.equals( that.documentSourceName ) ) {
      return false ;
    }
    if( !originalTarget.equals( that.originalTarget ) ) {
      return false ;
    }
    if( renditionMimeType != that.renditionMimeType ) {
      return false ;
    }
    if( resourceExtension != null
        ? !resourceExtension.equals( that.resourceExtension )
        : that.resourceExtension != null
    ) {
      return false ;
    }
    if( tags != null ? !tags.equals( that.tags ) : that.tags != null ) {
      return false ;
    }

    return true ;
  }

  @Override
  public int hashCode() {
    int result = originalTarget.hashCode() ;
    result = 31 * result + documentSourceName.hashCode();
    result = 31 * result + ( pageIdentifier != null ? pageIdentifier.hashCode() : 0 ) ;
    result = 31 * result + ( rendered ? 1 : 0 ) ;
    result = 31 * result + ( renditionMimeType != null ? renditionMimeType.hashCode() : 0 ) ;
    result = 31 * result + ( alternateStylesheet != null ? alternateStylesheet.hashCode() : 0 ) ;
    result = 31 * result + ( tags != null ? tags.hashCode() : 0 ) ;
    result = 31 * result + ( displayProblems ? 1 : 0 ) ;
    result = 31 * result + ( resourceExtension != null ? resourceExtension.hashCode() : 0 ) ;
    return result ;
  }
}

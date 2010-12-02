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
package org.novelang.request;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang.StringUtils;
import org.novelang.designator.Tag;
import org.novelang.outfit.loader.ResourceName;
import org.novelang.rendering.RenditionMimeType;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Laurent Caillette
 */
public final class GenericRequest implements DocumentRequest2, ResourceRequest2 {

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
  public ImmutableSet<Tag> getTags() {
    return tags ;
  }

  private final boolean displayProblems ;

  @Override
  public boolean getDisplayProblems() {
    return displayProblems ;
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
      final ResourceName alternateStylesheet,
      final ImmutableSet< Tag > tags
  ) {
    checkHasCharacters( originalTarget ) ;
    this.originalTarget = originalTarget ;
    checkHasCharacters( documentSourceName ) ;
    this.documentSourceName = documentSourceName ;
    this.rendered = true ;
    this.renditionMimeType = checkNotNull( renditionMimeType ) ;
    this.alternateStylesheet = alternateStylesheet ;
    this.tags = checkNotNull( tags ) ;
    this.displayProblems = displayProblems ;

    this.resourceExtension = null ;
  }

  private GenericRequest(
      final String originalTarget,
      final String documentSourceName,
      final boolean rendered,
      final String resourceExtension
  ) {
    checkHasCharacters( originalTarget ) ;
    this.originalTarget = originalTarget ;
    checkHasCharacters( documentSourceName ) ;
    this.documentSourceName = documentSourceName ;
    this.rendered = rendered ;
    checkHasCharacters( resourceExtension ) ;
    this.resourceExtension = resourceExtension ;

    renditionMimeType = null ;
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

  public static AnyRequest parse( final String originalTarget ) {
    throw new UnsupportedOperationException( "TODO" ) ;
  }


// ================
// java.lang.Object
// ================


  @Override
  public String toString() {
    final String generic = getClass().getSimpleName() + "[" +
        "originalTarget" + "=" + getOriginalTarget() +
        "; documentSourceName" + "=" + getDocumentSourceName()
    ;

    final String specific ;
    if( isRendered() ) {
      specific =
          "; displayProblems" + "=" + getDisplayProblems() +
          "; documentMimeType" + "=" +
              ( getRenditionMimeType() == null ? "<null>" : getRenditionMimeType().getMimeName() ) +
          "; stylesheet" + "=" + getAlternateStylesheet() +
          "; tags" + "=" + getTags() +
          "]"
      ;

    } else {
     specific = "; resourceExtension=" + getResourceExtension() ;
    }

    return generic + specific + "]" ;
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
    result = 31 * result + ( rendered ? 1 : 0 ) ;
    result = 31 * result + ( renditionMimeType != null ? renditionMimeType.hashCode() : 0 ) ;
    result = 31 * result + ( alternateStylesheet != null ? alternateStylesheet.hashCode() : 0 ) ;
    result = 31 * result + ( tags != null ? tags.hashCode() : 0 ) ;
    result = 31 * result + ( displayProblems ? 1 : 0 ) ;
    result = 31 * result + ( resourceExtension != null ? resourceExtension.hashCode() : 0 ) ;
    return result ;
  }
}

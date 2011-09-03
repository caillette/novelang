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
package org.novelang.outfit.xml;

import javax.xml.transform.SourceLocator;

import org.xml.sax.Locator;

/**
 * Immutable implementation of {@link SourceLocator} and various utilities for {@link Locator}.
 *
 * @author Laurent Caillette
 */
public final class ImmutableSourceLocator implements SourceLocator {
  
  private final String publicId ;
  private final String systemId ;
  private final int lineNumber ;
  private final int columnNumber ;

  public ImmutableSourceLocator(
      final int lineNumber,
      final int columnNumber
  ) {
    this( null, null, lineNumber, columnNumber ) ;
  }

  public ImmutableSourceLocator(
      final String publicId,
      final String systemId,
      final int lineNumber,
      final int columnNumber
  ) {
    this.publicId = publicId ;
    this.systemId = systemId ;
    this.lineNumber = lineNumber ;
    this.columnNumber = columnNumber ;
  }

  public static final ImmutableSourceLocator NULL =
      new ImmutableSourceLocator( null, null, -1, -1 ) ;

  public static ImmutableSourceLocator create( final Locator locator ) {
    if( locator == null ) {
      return NULL ;
    } else {
      return new ImmutableSourceLocator(
          locator.getPublicId(),
          locator.getSystemId(),
          locator.getLineNumber(),
          locator.getColumnNumber()
      ) ;
    }
  }

  public static ImmutableSourceLocator create( final SourceLocator locator ) {
    if( locator == null ) {
      return NULL ;
    } else {
      return new ImmutableSourceLocator(
          locator.getPublicId(),
          locator.getSystemId(),
          locator.getLineNumber(),
          locator.getColumnNumber()
      ) ;
    }
  }

  @Override
  public String getPublicId() {
    return publicId ;
  }

  @Override
  public String getSystemId() {
    return systemId ;
  }

  @Override
  public int getLineNumber() {
    return lineNumber ;
  }

  @Override
  public int getColumnNumber() {
    return columnNumber ;
  }

  @Override
  public boolean equals( final Object other ) {
    if( this == other ) {
      return true ;
    }
    if( other == null || getClass() != other.getClass() ) {
      return false ;
    }

    final ImmutableSourceLocator that = ( ImmutableSourceLocator ) other ;

    if( columnNumber != that.columnNumber ) {
      return false ;
    }
    if( lineNumber != that.lineNumber ) {
      return false ;
    }
    if( publicId != null ? !publicId.equals( that.publicId ) : that.publicId != null ) {
      return false ;
    }
    if( systemId != null ? !systemId.equals( that.systemId ) : that.systemId != null ) {
      return false ;
    }

    return true ;
  }

  @Override
  public int hashCode() {
    int result = publicId != null ? publicId.hashCode() : 0 ;
    result = 31 * result + ( systemId != null ? systemId.hashCode() : 0 ) ;
    result = 31 * result + lineNumber ;
    result = 31 * result + columnNumber ;
    return result ;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" + asSingleLineString( this ) + "}" ;
  }

  public static String asSingleLineString( final SourceLocator sourceLocator ) {
  return ( sourceLocator.getPublicId() == null ? "" : "publicId=" + sourceLocator.getPublicId() + "; " )
      + ( sourceLocator.getSystemId() == null ? "" : "systemId=" + sourceLocator.getSystemId() + "; " )
      + "line=" + sourceLocator.getLineNumber() + "; "
      + "column=" + sourceLocator.getColumnNumber();
}
}

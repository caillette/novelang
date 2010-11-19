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

package org.novelang.common;

/**
 * Represents the exact position of a character inside document sources.
 *
 * @author Laurent Caillette
 */
public class Location implements Comparable< Location > {

  private final String fileName ;
  private final int line ;
  private final int column ;


  public Location( final String fileName ) {
    this( fileName, -1, -1 ) ;
  }

  public Location( final String fileName, final int line, final int column ) {
    this.fileName = fileName ;
    this.line = line ;
    this.column = column ;
  }


  public String getFileName() {
    return fileName;
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }

  public boolean isPositionDefined() {
    return -1 < line && -1 < column ;
  }

  public String toHumanReadableForm() {
    return
        ( isPositionDefined() ? "(" + line + ":" + column + ") " : "(?) " ) +
        fileName 
    ;

  }

// ==============
// Usual suspects
// ==============

  @Override
  public String toString() {
    return
        toHumanReadableForm() 
//        ( isPositionDefined() ? "-:-" : line + ":" + column ) +
//        ":'" + fileName + "'"
    ;
  }

  @Override
  public boolean equals( final Object other ) {
    if( this == other ) {
      return true ;
    }
    if( other == null || getClass() != other.getClass() ) {
      return false ;
    }

    final Location location = ( Location ) other ;

    if( column != location.column ) {
      return false ;
    }
    if( line != location.line ) {
      return false ;
    }
    if( fileName != null ? !fileName.equals( location.fileName ) : location.fileName != null ) {
      return false ;
    }

    return true ;
  }

  @Override
  public int hashCode() {
    int result = fileName != null ? fileName.hashCode() : 0 ;
    result = 31 * result + line ;
    result = 31 * result + column ;
    return result ;
  }

  @Override
  public int compareTo( final Location other ) {
    if( other == null ) {
      return 1 ;
    }
    final int stringDifference = this.fileName.compareTo( other.getFileName() ) ;
    if( stringDifference == 0 ) {
      final int lineDifference = this.line - other.line ;
      if( lineDifference == 0 ) {
        final int columnDifference = this.column - other.column ;
        return columnDifference ;
      } else {
        return lineDifference ;
      }
    } else {
      return stringDifference ;
    }    
  }
}

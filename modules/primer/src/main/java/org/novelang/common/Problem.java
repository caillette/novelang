/*
 * Copyright (C) 2011 Laurent Caillette
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.RecognitionException;
import com.google.common.base.Preconditions;
import org.antlr.runtime.tree.RewriteCardinalityException;

//import novelang.parser.antlr.AntlrErrorInterpreter;

/**
 * Represents something bad that happened during document generation.
 * 
 * @author Laurent Caillette
 */
public class Problem implements Comparable< Problem > {

  final Location location ;
  final String message ;

  private Problem( final Location location, final String message ) {
    this.location = Preconditions.checkNotNull( location ) ;
    this.message = Preconditions.checkNotNull( message ) ;
  }

  public Location getLocation() {
    return location;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return getLocation() + " " + getMessage() ;
  }

  /**
   * Kinda dependency towards ANTLR formatting but who cares?.
   */
  private static final Pattern MESSAGE_WITH_LOCATION =
      Pattern.compile( "line (\\d+):(\\d+) (.+)" ) ;

  public static Problem createProblem( final String message ) {
    return createProblem( new Location( "<unknown file>", -1, -1 ), message ) ;
  }

  public static Problem createProblem( final Location location, final String message ) {
    return new Problem( location, message ) ;
  }

  public static Problem createProblem( final Exception exception ) {
    return new Problem( new Location( "<unknown file>", -1, -1 ), exception.getMessage() ) ;
  }

  public static Problem createProblem(
      final LocationFactory locationFactory,
      final Exception exception
  ) {
    int line = -1 ;
    int column = -1 ;
    final String bareMessage = exception.getMessage() ;
    if( exception instanceof RecognitionException ) {
      final RecognitionException recognitionException = ( RecognitionException ) exception ;
      line = recognitionException.line ;
      column = recognitionException.charPositionInLine ;
    }
    final Location location = locationFactory.createLocation( line, column ) ;
    return new Problem( location, bareMessage ) ;
  }

  public static Problem createProblem(
      final LocationFactory locationFactory,
      final RecognitionException exception,
      final String[] tokenNames
  ) {
    final Location location = locationFactory.createLocation(
        exception.line, exception.charPositionInLine ) ;
    // TODO reactivate this once solved dependency mess introduced (unveiled) by Maven.
//    final String message = AntlrErrorInterpreter.getErrorMessage( exception, tokenNames ) ;
//    return new Problem( location, message ) ;
    return createProblem( location, "TODO: interpret error" + exception.getMessage() ) ; // Bad.
  }
  
  
  public static Problem createProblem(
      final LocationFactory locationFactory,
      final RecognitionException exception
  ) {
    final Location location = locationFactory.createLocation(
        exception.line, exception.charPositionInLine ) ;
    final String message = exception.getMessage() == null ? "?" : exception.getMessage() ;
    return new Problem( location, message ) ;  }

  /**
   * @deprecated, use standard exception-aware
   *     {@link #createProblem(LocationFactory, Exception) method}. The parser should trap those
   *     exceptions. 
   */
  public static Problem createProblem(
      final LocationFactory locationFactory,
      final String message
  ) {
    final Matcher matcher = MESSAGE_WITH_LOCATION.matcher( message ) ;
    int line = -1 ;
    int column = -1 ;
    final String bareMessage ;
    if( matcher.matches() && 3 == matcher.groupCount() ) {
      line = Integer.parseInt( matcher.group( 1 ) ) ;
      column = Integer.parseInt( matcher.group( 2 ) ) ;
      bareMessage = matcher.group( 3 ) ;
    } else {
      bareMessage = message ;
    }
    final Location location = locationFactory.createLocation( line, column ) ;
    return new Problem( location, bareMessage ) ;
  }

  public static Problem createProblem( final String message, final Location location ) {
    return new Problem( location, message ) ;
  }

  public static Problem createProblem(
      final String message,
      final LocationFactory locationFactory,
      final int line,
      final int column
  ) {
    return new Problem( locationFactory.createLocation( line, column ), message ) ;
  }


// ==============
// Usual suspects
// ==============


  @Override
  public boolean equals( final Object o ) {
    if( this == o ) return true;
    if( o == null || getClass() != o.getClass() ) return false;

    final Problem problem = ( Problem ) o;

    if( location != null ? !location.equals( problem.location ) : problem.location != null )
      return false;
    if( message != null ? !message.equals( problem.message ) : problem.message != null )
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = location != null ? location.hashCode() : 0;
    result = 31 * result + ( message != null ? message.hashCode() : 0 );
    return result;
  }

  @Override
  public int compareTo( final Problem other ) {
    if( this.equals( other ) ) {
      return 0 ;
    }
    if( other == null ) {
      return 1 ;
    }
    final int locationDifference = getLocation().compareTo( other.getLocation() ) ;
    if( locationDifference == 0 ) {
      return message.compareTo( other.getMessage() ) ;
    } else {
      return locationDifference ;
    }
  }
}

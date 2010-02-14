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

package novelang.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.MissingTokenException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Token;
import com.google.common.base.Preconditions;

import novelang.parser.antlr.error.AntlrErrorInterpreter;
import novelang.parser.unicode.UnicodeNames;

/**
 * Represents something bad that happened during document generation.
 * 
 * @author Laurent Caillette
 */
public class Problem {

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
    return new Problem( new Location( "<unknown file>", -1, -1 ), message ) ;
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
    final String message = AntlrErrorInterpreter.getErrorMessage( exception, tokenNames ) ;
    return new Problem( location, message ) ;  
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
}

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
import com.google.common.base.Objects;

/**
 * @author Laurent Caillette
 */
public class Problem {

  final Location location ;
  final String message ;

  private Problem( Location location, String message ) {
    this.location = Objects.nonNull( location ) ;
    this.message = Objects.nonNull( message ) ;
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

  public static Problem createProblem( String message ) {
    return new Problem( new Location( "<unknown file>", -1, -1 ), message ) ;
  }

  public static Problem createProblem( Exception exception ) {
    return new Problem( new Location( "<unknown file>", -1, -1 ), exception.getMessage() ) ;
  }

  public static Problem createProblem(
      LocationFactory locationFactory,
      Exception exception
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
      LocationFactory locationFactory,
      String message
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

  public static Problem createProblem( String message, Location location ) {
    return new Problem( location, message ) ;
  }
}

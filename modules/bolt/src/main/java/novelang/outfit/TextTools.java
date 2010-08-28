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
package novelang.outfit;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Utility class for language constructs.
 *
 * @author Laurent Caillette
 */
public class TextTools {

  private TextTools() {
    throw new Error() ;
  }

  /**
   * Converts a character to the two-character hexadecimal representation of its most
   * significative one-byte representation.
   *
   * @param character
   * @return a two-byte string.
   * @author Jon A. Cruz http://www.thescripts.com/forum/thread15875.html
   */
  public static String to8byteHex( final char character ) {
    return Integer.toHexString( 0x100 | ( 0x0ff & ( int ) character ) ).substring( 1 ) ;
  }


  public static final String LINE_FEED = "" + ( ( char ) 10 ) ;
  public static final String CARRIAGE_RETURN = "" + ( ( char ) 13 ) ;

  private static final String UNIX_LINE_BREAK = LINE_FEED ;

  private static final Pattern NON_UNIX_LINE_BREAK_PATTERN =
      Pattern.compile( CARRIAGE_RETURN + LINE_FEED + "?" ) ;

  /**
   * Replaces CR (Carriage Return, u000D) and CR + LF sequences by a single LF (Line Feed, u000A),
   * in the Unix style.
   *
   * <a href="http://en.wikipedia.org/wiki/Newline">See Wikipedia article on New Line</a>.
   *
   * @param text a non-null String.
   * @return a non-null String with normalized line breaks.
   */
  public static String unixifyLineBreaks( final String text ) {
      final StringBuffer buffer = new StringBuffer() ; // Matcher doesn't like StringBuilder.
      final Matcher matcher = NON_UNIX_LINE_BREAK_PATTERN.matcher( text ) ;
      while( matcher.find() ) {
        matcher.appendReplacement( buffer, /*"#"*/UNIX_LINE_BREAK ) ;
      }
      matcher.appendTail( buffer ) ;
      return buffer.toString() ;
  }

  public static String to16ByteHex( final int aCharacterAsInt ) {
    return String.format( "%04X", aCharacterAsInt );
  }
}

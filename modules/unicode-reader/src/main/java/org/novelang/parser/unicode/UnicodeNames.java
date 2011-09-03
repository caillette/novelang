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

package org.novelang.parser.unicode;


import org.novelang.build.CodeGenerationTools;
import org.novelang.outfit.TextTools;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;

/**
 * Reads Unicode character names from a binary file
 * {@link org.novelang.build.CodeGenerationTools#UNICODE_NAMES_BINARY}.
 * This saves a few hundreds of milliseconds at runtime for each name request, which is of
 * poor interest. The cool thing is the speedup at debug time.
 *
 * @author Laurent Caillette
 */
public class UnicodeNames {

  private static final Logger LOGGER = LoggerFactory.getLogger( UnicodeNames.class ) ;


  private UnicodeNames() { }

  /**
   * Returns the pure Unicode name, like "{@code LATIN_SMALL_LETTER_A}".
   *
   * @param character some character.
   * @return a possibly null String.
   */
  public static String getPureName( final char character ) {
    final Exception exception ;
    final String characterAsString = ( int ) character +
        " (" + toHexadecimalString( character ) + ")" ;
    try {
      final String pureName = new UnicodeNamesBinaryReader(
          UnicodeNames.class.getResource( CodeGenerationTools.UNICODE_NAMES_BINARY ) ).getName( character ) ;
      if( pureName == null ) {
        LOGGER.warn( "No name found for character ", characterAsString ) ;
      } else {
        LOGGER.debug( "Found name for character ", characterAsString, " '", pureName, "'" ) ;
      }
      return pureName ;
    } catch( Exception e ) {
      exception = e ;
    }
    LOGGER.error( exception, "No name found for character ", characterAsString ) ;
    return null ;
  }


  /**
   * Returns Unicode name with hexadecimal value.
   * @param character
   * @return
   */
  public static String getDecoratedName( final char character ) {
    final String pureName = getPureName( character ) ;
    final String hexadecimalValue = toHexadecimalString( character );
    if( pureName == null ) {
      return "[Unicode unknown: " + hexadecimalValue + "]" ;
    } else {
      return pureName + " [" + hexadecimalValue + "]" ;

    }
  }

  private static String toHexadecimalString( final char character ) {
    return "0x" + TextTools.to16ByteHex( character ).toUpperCase();
  }

}

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

import org.novelang.outfit.TextTools;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.apache.commons.lang.CharUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link UnicodeNames} basing on some well-known values.
 *
 * @author Laurent Caillette
 */
public class UnicodeNamesTest {

  @Test
  public void logSomeBasicCharacterRepresentations() {
    final int aCharacterAsInt = 'a' ;
    final String aCharacterAsHex = TextTools.to16ByteHex( aCharacterAsInt ) ;
    LOGGER.info(
        "\nThe 'a' letter",
        "\nAs int: ", aCharacterAsInt,
        "\nAs hex: ", aCharacterAsHex,
        "\nUnicode escaped: ", CharUtils.unicodeEscaped( 'a' )
    ) ;
  }

  @Test
  public void unicode16NameHasSpaces() {
    verify( "LATIN_SMALL_LETTER_A", 'a' ) ;
  }

  @Test
  public void unicode16NameHasNoSpace() {
    verify( "DIAERESIS", '\u00a8' ) ;
  }

  @Test
  public void unicode16NameHasHyphen() {
    verify( "RIGHT-POINTING_DOUBLE_ANGLE_QUOTATION_MARK", '\u00bb' ) ;    
  }

  @Test
  public void unicode16NameIsControl1() {
    verify( "ESCAPE", '\u001b' ) ;
  }

  @Test
  public void unicode16NameIsControl2() {
    verify( "CHARACTER_TABULATION", '\t' ) ;
  }

  @Test
  public void lastKnownValue() {
    verify( "REPLACEMENT_CHARACTER", ( char ) 0xFFFD ) ;
  }

  @Test
  public void smokeTestOnEveryCharacter() {
    final int totalCharacterCount = 256 * 256;
    for( int counter = totalCharacterCount - 256 ; counter < totalCharacterCount ; counter ++ ) {
      final char character = ( char ) counter ;
      UnicodeNames.getPureName( character ) ;
    }
  }

// =======
// Fixture
// =======

  private static final Logger LOGGER = LoggerFactory.getLogger( UnicodeNamesTest.class ) ;

  private static void verify( final String expected, final char character ) {
    assertEquals( expected, UnicodeNames.getPureName( character ) ) ;
  }


}

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
package novelang.build;

import java.util.Set;

import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.ImmutableSet;

/**
 * Tests for {@link novelang.parser.SupportedCharacters}.
 *
 * @author Laurent Caillette
 */
public class SupportedCharactersGeneratorTest {

  private static final Logger LOGGER = 
      LoggerFactory.getLogger( SupportedCharactersGeneratorTest.class ) ;

  @Test
  public void extractSupportedCharacters() {
    final String tokensDeclaration = "'x'  'X'\n'0'  '9' '\u00e0'\n'\\\\'  '|'" ;
    final Set< Character > characters =
        SupportedCharactersGenerator.extractSupportedCharacters( tokensDeclaration ) ;

    LOGGER.debug( "Got: {}", characters ) ;

    assertEquals( 7, characters.size() ) ;

    assertTrue( characters.contains( 'x' ) ) ;
    assertTrue( characters.contains( 'X' ) ) ;
    assertTrue( characters.contains( '0' ) ) ;
    assertTrue( characters.contains( '9' ) ) ;
    assertTrue( characters.contains( new Character( '\u00e0' ) ) ) ;
    assertTrue( characters.contains( '\\' ) ) ;
    assertTrue( characters.contains( '|' ) ) ;

  }

  @Test
  public void extractJustPunctuationSign() {
    final String tokensDeclaration = "'|'" ;
    final Set< Character > characters =
        SupportedCharactersGenerator.extractSupportedCharacters( tokensDeclaration ) ;

    LOGGER.debug( "Got: {}", characters ) ;
    assertEquals( 1, characters.size() ) ;
    assertTrue( characters.contains( '|' ) ) ;

  }

  @Test
  public void extractJustUnicode() {
    final String tokensDeclaration = "'\u00e0'" ;
    final Set< Character > characters =
        SupportedCharactersGenerator.extractSupportedCharacters( tokensDeclaration ) ;

    LOGGER.debug( "Got: {}", characters ) ;
    assertEquals( 1, characters.size() ) ;
    assertTrue( characters.contains( '\u00e0' ) ) ;

  }

  @Test
  public void convertToEscapedUnicode() {
    final Set< Character > characters = ImmutableSet.of( '\u00f6' ) ;
    final Set<SupportedCharactersGenerator.Item> escapedCharacters = 
        SupportedCharactersGenerator.convertToEscapedUnicode( characters ) ;
    assertEquals( 1, escapedCharacters.size() ) ;
    assertEquals( "\\u00f6", escapedCharacters.iterator().next().declaration ) ;
    
  }
}
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
package novelang.parser;

import java.util.Set;

import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests for {@link SupportedCharacters}.
 *
 * @author Laurent Caillette
 */
public class SupportedCharactersTest {

  private static final Logger LOGGER = LoggerFactory.getLogger( SupportedCharactersTest.class ) ;

  @Test
  public void extractSupportedCharacters() {
    final String tokensDeclaration = "'x'  'X'\n'0'  '9' '\u00e0'\n'\\\\'  '|'" ;
    final Set< Character > characters =
        SupportedCharacters.extractSupportedCharacters( tokensDeclaration ) ;

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
        SupportedCharacters.extractSupportedCharacters( tokensDeclaration ) ;

    LOGGER.debug( "Got: {}", characters ) ;
    assertEquals( 1, characters.size() ) ;
    assertTrue( characters.contains( '|' ) ) ;

  }

  @Test
  public void extractJustUnicode() {
    final String tokensDeclaration = "'\u00e0'" ;
    final Set< Character > characters =
        SupportedCharacters.extractSupportedCharacters( tokensDeclaration ) ;

    LOGGER.debug( "Got: {}", characters ) ;
    assertEquals( 1, characters.size() ) ;
    assertTrue( characters.contains( '\u00e0' ) ) ;

  }

  @Test
  public void lexerCharacters() {
    final Set< Character > characters = SupportedCharacters.getSupportedCharacters() ;
    assertNotNull( characters ) ;
    LOGGER.debug( "Got: {}", characters ) ;
    
  }
}

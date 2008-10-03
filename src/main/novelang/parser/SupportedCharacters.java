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

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Attempts to find out all characters supported by the parser.
 * This is done by parsing the ANTLR-generated token list.
 *
 * @author Laurent Caillette
 */
public class SupportedCharacters {

  private static final Logger LOGGER = LoggerFactory.getLogger( SupportedCharacters.class ) ;

  private static Set< Character > SUPPORTED_CHARACTERS ;
  private static Set< Character > NON_WORD_CHARACTERS ;

  private static final String ANTLR_TOKENS = "/novelang/parser/antlr/Novelang__.g";

  private static final Pattern TOKENS_DECLARATIONS =
      Pattern.compile( "'(\\w)|(\\\\\\W)|(\\\\u[a-f[0-9]]{4})|([\\p{Punct}&&[^']])'" ) ;
  static {
    LOGGER.debug( "Crafted regex: " + TOKENS_DECLARATIONS.toString() ) ;
  }

  private static final CharacterExtractor CHARACTER_EXTRACTOR =
      new CharacterExtractor(
          new LitteralConverter(),
          new EscapedCharacterConverter(),
          new UnicodeConverter(),
          new LitteralConverter()
      )
  ;

  private static Set< Character > loadSupportedCharacters() {
    Set< Character > supportedCharacters = null ;
    final InputStream resourceStream =
        SupportedCharacters.class.getResourceAsStream( ANTLR_TOKENS ) ;
    if( null == resourceStream ) {
      LOGGER.error(
          "Could not load resource: '{}', supported characters are unknown.", ANTLR_TOKENS ) ;
    } else {
      try {
        final String tokensDeclaration = IOUtils.toString( resourceStream ) ;
        supportedCharacters = extractSupportedCharacters( tokensDeclaration ) ;
      } catch( IOException e ) {
        LOGGER.error(
            "Could not load resource: '{}', supported characters are unknown.", ANTLR_TOKENS ) ;
      }
    }
    if( null == supportedCharacters ) {
      return null ;
    } else {
      return ImmutableSet.copyOf( Sets.newTreeSet( Lists.sortedCopy( supportedCharacters ) ) ) ;
    }
  }

  protected static Set< Character > extractSupportedCharacters( String tokensDeclaration ) {
    final Matcher matcher = TOKENS_DECLARATIONS.matcher( tokensDeclaration ) ;
    final Set< Character > characters = Sets.newHashSet() ;
    while( matcher.find() ) {
      final Character character = extractCharacter( matcher ) ;
      if( null != character ) {
        characters.add( character ) ;
      }
    }
    return characters ;
  }

  private static Character extractCharacter( Matcher matcher ) {
    return CHARACTER_EXTRACTOR.extract( matcher ) ;
  }

  private interface CharacterConverter {
    Character convert( String declaration ) ;
  }

  private static class CharacterExtractor  {
    private final CharacterConverter[] converters ;

    public CharacterExtractor( CharacterConverter... converters ) {
      this.converters = converters.clone() ;
    }

    public final Character extract( Matcher matcher ) {
      final int effectiveGroupCount = matcher.groupCount() - 1 ; // Don't use group 0
      if( converters.length != matcher.groupCount() ) {
        throw new IllegalArgumentException(
            "Matcher has " + matcher.groupCount() + " groups (including group 0) against "
          + converters.length + " converters"
        ) ;
      }

      for( int converterIndex = 0 ; converterIndex < effectiveGroupCount ; converterIndex++ ) {
        final int groupIndex = converterIndex + 1;
        final String match = matcher.group( groupIndex ) ;
        if( match != null  ) {
//          LOGGER.debug( "Converting '{}' from group {}", match, groupIndex ) ;
          return converters[ converterIndex ].convert( match );
        }
      }

      return null ;
    }


  }

  private static class LitteralConverter implements CharacterConverter {

    public Character convert( String characterDeclaration ) {
      if( 1 != characterDeclaration.length() ) {
        throw new IllegalArgumentException(
            "Should contains one character only, was: '" + characterDeclaration + "'" ) ;
      } ;
      return characterDeclaration.charAt( 0 ) ;
    }
  }

  private static class UnicodeConverter implements CharacterConverter {

    public Character convert( String characterDeclaration ) {
      if( ! characterDeclaration.startsWith( "\\u" ) ) {
        throw new IllegalArgumentException(
            "Should be unicode starting with '\\u', was: '" + characterDeclaration + "'" ) ;
      } ;
      final String hex = "#" +
          characterDeclaration.substring( 2, characterDeclaration.length() ) ;
//      LOGGER.debug( "Decoding {}", hex ) ;
      Integer decoded = Integer.decode( hex ) ;
      return new Character( ( char ) decoded.intValue() ) ;
    }
  }

  private static class EscapedCharacterConverter implements CharacterConverter {

    public Character convert( String characterDeclaration ) {
      if( ! characterDeclaration.startsWith( "\\" ) ) {
        throw new IllegalArgumentException(
            "Should be escaped starting with '\\', was: '" + characterDeclaration + "'" ) ;
      } ;
      if( characterDeclaration.length() != 2 ) {
        throw new IllegalArgumentException(
            "Should start with '\\' then 1 character, was: '" + characterDeclaration + "'" ) ;
      } ;
      return characterDeclaration.charAt( 1 ) ;
    }
  }

  /**
   * Returns supported characters.
   * This is done lazily because otherwise the unit test gets screwed before testing anything.
   */
  public static synchronized Set< Character > getSupportedCharacters() {
    if( null == SUPPORTED_CHARACTERS ) {
      SUPPORTED_CHARACTERS = loadSupportedCharacters() ;
    }
    return SUPPORTED_CHARACTERS ;
  }

  /**
   * Returns supported characters.
   * This is done lazily because otherwise the unit test gets screwed before testing anything.
   */
  public static synchronized Set< Character > getNonWordCharacters() {
    if( null == NON_WORD_CHARACTERS ) {
      NON_WORD_CHARACTERS = removeWordCharacters( getSupportedCharacters() ) ;
    }
    return NON_WORD_CHARACTERS ;
  }

  private static final String WORD_CHARACTERS =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789" ;

  private static Set< Character > removeWordCharacters( Set< Character > supportedCharacters ) {
    if( null == supportedCharacters ) {
      return null ;
    } else {
      final char[] wordCharacters = WORD_CHARACTERS.toCharArray() ;
      final Set updatedCharacterSet = Sets.newTreeSet( supportedCharacters ) ;
      for( int i = 0; i < wordCharacters.length ; i++ ) {
        final Character wordCharacter = wordCharacters[ i ] ;
        updatedCharacterSet.remove( wordCharacter ) ;
      }
      final ImmutableSet< Character > resultingSet = ImmutableSet.copyOf( updatedCharacterSet ) ;
//      for( Character character : resultingSet ) {
//        LOGGER.debug( "Kept character 0x{}", Integer.toHexString( character.charValue() ) ) ;
//      }
      return resultingSet;
    }
  }

}

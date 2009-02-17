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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * Table of escaped symbols, using HTML entity names whenever defined and Unicode names otherwise.
 *
 * @author Laurent Caillette
 */
public class SourceUnescape {

  private static final Logger LOGGER = LoggerFactory.getLogger( SourceUnescape.class ) ;

  private static final BiMap< String, Character > ESCAPED_CHARACTERS;
  private static final Map< String, Character > ESCAPED_CHARACTERS_ALTERNATIVES;
  private static final BiMap< String, Character > ESCAPED_HTML_CHARACTERS ;

  /**
   * Left-pointing double angle quotation mark "&#xab;".
   */
  public static Character ESCAPE_START = '\u00ab' ; // «

  /**
   * Right-pointing double angle quotation mark "&#xbb;".
   */
  public static Character ESCAPE_END = '\u00bb' ; // »


  static {

    final BiMap< String, Character > escapedCharacters = Maps.newHashBiMap() ;
    final Map< String, Character > escapedCharactersAlternatives = Maps.newHashMap() ;
    final BiMap< String, Character > escapedHtmlCharacters = Maps.newHashBiMap() ;

    // Symbols to keep after character escape refactoring.

    escapedCharacters.put( "left-pointing-double-angle-quotation-mark", ESCAPE_START ) ; // «
    escapedCharactersAlternatives.put( "laquo", ESCAPE_START ) ;

    escapedCharacters.put( "right-pointing-double-angle-quotation-mark", ESCAPE_END ) ; // »
    escapedCharactersAlternatives.put( "raquo", ESCAPE_END ) ;

    escapedCharacters.put( "less-than-sign", '<' ) ;
    escapedCharactersAlternatives.put( "lt", '<' ) ;

    escapedCharacters.put( "greater-than-sign", '>' ) ;
    escapedCharactersAlternatives.put( "gt", '>' ) ;

    escapedCharacters.put( "grave-accent", '`' ) ;

    escapedCharacters.put( "percent-sign", '%' ) ;

    escapedCharacters.put( "left-curly-bracket", '{' ) ;
    escapedCharacters.put( "right-curly-bracket", '}' ) ;

    escapedCharacters.put( "latin-small-ligature-oe", '\u0153' ) ;
    escapedCharactersAlternatives.put( "oelig", '\u0153' ) ;

    escapedCharacters.put( "latin-capital-ligature-oe", '\u0152' ) ;
    escapedCharactersAlternatives.put( "OElig", '\u0152' ) ;

    escapedCharacters.put( "euro-sign", '\u8364' ) ;

    escapedCharacters.put( "multiplication-sign", '\u00d7' ) ; // ×
    escapedCharactersAlternatives.put( "times", '\u00d7' ) ;

    escapedHtmlCharacters.put( "oelig", '\u0153' ) ;
    escapedHtmlCharacters.put( "OElig", '\u0152' ) ;
    escapedHtmlCharacters.put( "amp", '&' ) ;
    escapedHtmlCharacters.put( "lt", '<' ) ;
    escapedHtmlCharacters.put( "gt", '>' ) ;

    ESCAPED_CHARACTERS = Maps.unmodifiableBiMap( escapedCharacters ) ;
    ESCAPED_CHARACTERS_ALTERNATIVES = ImmutableMap.copyOf( escapedCharactersAlternatives ) ;
    ESCAPED_HTML_CHARACTERS = Maps.unmodifiableBiMap( escapedHtmlCharacters ) ;
  }

  public static Map< String, Character > getMainCharacterEscapes() {
    return new ImmutableMap.Builder().putAll( ESCAPED_CHARACTERS ).build() ;
  }

  public static String escapeHtml( Character unescaped ) {
    return ESCAPED_HTML_CHARACTERS.inverse().get( unescaped ) ;
  }

  public static Character unescapeCharacter( String escaped )
      throws NoUnescapedCharacterException
  {
    Character unescaped = ESCAPED_CHARACTERS.get( escaped ) ;
    if( null == unescaped ) {
      unescaped = ESCAPED_CHARACTERS_ALTERNATIVES.get( escaped ) ;
      if ( null == unescaped ) {
        final NoUnescapedCharacterException exception = new NoUnescapedCharacterException( escaped ) ;
        LOGGER.warn( "Unsupported symbol", exception ) ;
        throw exception ;
      }
    }
    LOGGER.debug( "Escaped: '{}'", escaped ) ;
    return unescaped ;
  }


  /**
   * Returns escaped symbol.
   * @param unescaped must not be null.
   * @return null if not found.
   */
  private static String escapeCharacter( Character unescaped ) {
    return ESCAPED_CHARACTERS.inverse().get( unescaped ) ;
  }

  public static String escapeText( String text ) {
    final StringBuffer buffer = new StringBuffer() ;
    for( char c : text.toCharArray() ) {
      final String escaped = escapeCharacter( c ) ;
      if( null == escaped ) {
        buffer.append( c ) ;
      } else {
        buffer.append( ESCAPE_START ).append( escaped ).append( ESCAPE_END ) ;
      }
    }
    return buffer.toString();
  }

  private static final Pattern PLAIN_ESCAPE_PATTERN =
      Pattern.compile( "(" + ESCAPE_START + "(\\w+(?:-\\w+)*)" + ESCAPE_END + ")" ) ;
  private static final Pattern HTML_ESCAPE_PATTERN =
      Pattern.compile( "(\\&(\\w+);)" ) ;

  static {
    LOGGER.debug( "Crafted regex {}", PLAIN_ESCAPE_PATTERN.pattern() ) ;
    LOGGER.debug( "Crafted regex {}", HTML_ESCAPE_PATTERN.pattern() ) ;
  }

  public static String unescapeText( String text )
      throws NoUnescapedCharacterException
  {
    final Matcher matcher = PLAIN_ESCAPE_PATTERN.matcher( text ) ;
    final StringBuffer buffer = new StringBuffer() ;
    int keepFrom = 0 ;
    while( matcher.find() ) {
      if( matcher.start() > 0 && keepFrom < text.length() ) {
        final String previous = text.substring( keepFrom, matcher.start() ) ;
        buffer.append( previous ) ;
      }
      final String escapeCode = matcher.group( 2 ) ;
      final Character escapedSymbol = SourceUnescape.unescapeCharacter( escapeCode ) ;
      if( null == escapedSymbol ) {
        throw new NoUnescapedCharacterException( escapeCode ) ;
      }
      buffer.append( escapedSymbol ) ;
      keepFrom = matcher.end() ;
    }
    if( keepFrom < text.length() ) {
      final String tail = text.substring( keepFrom, text.length() ) ;
      buffer.append( tail ) ;
    }

    return buffer.toString() ;
  }



}
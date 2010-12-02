/*
 * Copyright (C) 2010 Laurent Caillette
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

package org.novelang.parser;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.parser.shared.Lexeme;

/**
 * Table of escaped symbols, using HTML entity names whenever defined and Unicode names otherwise.
 *
 * @author Laurent Caillette
 */
public class SourceUnescape {

  private static final Logger LOGGER = LoggerFactory.getLogger( SourceUnescape.class );

  private static final Map< String, Character > UNICODE_ESCAPES;
  private static final Map< String, Character > HTML_ENTITY_NAMES_ESCAPES;

  /**
   * Left-pointing double angle quotation mark "&#xab;".
   * Must be the same as declared in the grammar!
   */
  public static final Character ESCAPE_START = '\u00ab' ; // «

  /**
   * Right-pointing double angle quotation mark "&#xbb;".
   * Must be the same as declared in the grammar!
   */
  public static final Character ESCAPE_END = '\u00bb' ; // »

  private SourceUnescape() { }


  static {

    final Map< String, Character > escapedCharacters = Maps.newHashMap() ;
    final Map< String, Character > escapedCharactersAlternatives = Maps.newHashMap() ;

    for( final Lexeme lexeme : GeneratedLexemes.getLexemes().values() ) {
      final String htmlEntityName = lexeme.getHtmlEntityName() ;
      final Character character = lexeme.getCharacter() ;
      escapedCharacters.put( unicodeUpperNameToEscapeName( lexeme.getUnicodeName() ), character ) ;
      if( null != htmlEntityName ) {
        escapedCharactersAlternatives.put( htmlEntityName, character ) ;
      }
    }

    UNICODE_ESCAPES = ImmutableMap.copyOf( escapedCharacters ) ;
    HTML_ENTITY_NAMES_ESCAPES = ImmutableMap.copyOf( escapedCharactersAlternatives ) ;
  }

  public static Map< String, Character > getMainCharacterEscapes() {
    return new ImmutableMap.Builder< String, Character >().putAll( UNICODE_ESCAPES ).build() ;
  }

  public static Character unescapeCharacter( final String escaped )
      throws NoUnescapedCharacterException
  {
    Character unescaped = UNICODE_ESCAPES.get( escaped ) ;
    if( null == unescaped ) {
      unescaped = HTML_ENTITY_NAMES_ESCAPES.get( escaped ) ;
      if ( null == unescaped ) {
        final NoUnescapedCharacterException exception = new NoUnescapedCharacterException( escaped ) ;
        LOGGER.warn( exception, "Unsupported symbol" ) ;
        throw exception ;
      }
    }
    LOGGER.debug( "Escaped: '", escaped, "'" ) ;
    return unescaped ;
  }




  private static final Pattern PLAIN_ESCAPE_PATTERN =
      Pattern.compile( "(" + ESCAPE_START + "(\\w+(?:-\\w+)*)" + ESCAPE_END + ")" ) ;
  private static final Pattern HTML_ESCAPE_PATTERN =
      Pattern.compile( "(\\&(\\w+);)" ) ;

  static {
    LOGGER.debug( "Crafted regex ", PLAIN_ESCAPE_PATTERN.pattern() ) ;
    LOGGER.debug( "Crafted regex ", HTML_ESCAPE_PATTERN.pattern() ) ;
  }

  public static String unescapeText( final String text )
      throws NoUnescapedCharacterException
  {
    final Matcher matcher = PLAIN_ESCAPE_PATTERN.matcher( text ) ;
    final StringBuilder buffer = new StringBuilder();
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


  public static String unicodeUpperNameToEscapeName( final String upperName ) {
    return upperName.toLowerCase().replace( '_', '-' ) ;
  }
}
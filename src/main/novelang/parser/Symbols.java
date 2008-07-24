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
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.BiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMap;

/**
 * Table of escaped symbols, using HTML entity names whenever defined and Unicode names otherwise.
 *
 * @author Laurent Caillette
 */
public class Symbols {

  private static final Logger LOGGER = LoggerFactory.getLogger( Symbols.class ) ;

  private static final BiMap< String, String > ESCAPED_SYMBOLS ;
  private static final BiMap< String, String > UNESCAPED_SYMBOLS ;
  private static final Set< String > HTML_ESCAPED ;

  static {

    final BiMap< String, String > symbols = Maps.newHashBiMap() ;
    final Set< String > htmlEscaped = Sets.newHashSet() ;

    symbols.put( "apos", "'" ) ;

    symbols.put( "hellip", "\u2026" ) ;
    symbols.put( "percent", "%" ) ;
    symbols.put( "lcub", "{" ) ;
    symbols.put( "rcub", "}" ) ;

    symbols.put( "plus", "+" ) ;
    symbols.put( "equals", "=" ) ;
    symbols.put( "dollar", "$" ) ;
    symbols.put( "numbersign", "#" ) ;
    symbols.put( "colon", ":" ) ;
    symbols.put( "lowline", "_" ) ;
    symbols.put( "euro", "\u20ac" ) ;
    symbols.put( "amp", "&" ) ;
    symbols.put( "solidus", "/" ) ;
    symbols.put( "lt", "<" ) ;
    symbols.put( "gt", ">" ) ;
    symbols.put( "tilde", "~" ) ;
    symbols.put( "rp", ")" ) ;
    symbols.put( "quot", "\"" ) ;
    symbols.put( "fullstop", "." ) ;
    symbols.put( "deg", "\u00b0" ) ;

    symbols.put( "oelig", "\u0153" ) ;
    htmlEscaped.add( "oelig" ) ;

    symbols.put( "OElig", "\u0152" ) ;
    htmlEscaped.add( "OElig" ) ;

    ESCAPED_SYMBOLS = Maps.unmodifiableBiMap( symbols ) ;
    UNESCAPED_SYMBOLS = Maps.unmodifiableBiMap( symbols.inverse() ) ;
    HTML_ESCAPED = Sets.newHashSet( htmlEscaped ) ;
  }

  public static Map< String, String > getDefinitions() {
    return new ImmutableMap.Builder().putAll( ESCAPED_SYMBOLS ).build() ;
  }

  public static boolean isHtmlEscape( String escaped ) {
    return HTML_ESCAPED.contains( escaped ) ;
  }

  public static String unescapeSymbol( String escaped )
      throws UnsupportedEscapedSymbolException
  {
    final String unescaped = ESCAPED_SYMBOLS.get( escaped ) ;
    if( null == unescaped ) {
      final UnsupportedEscapedSymbolException exception =
          new UnsupportedEscapedSymbolException( escaped ) ;
      LOGGER.warn( "Unsupported symbol", exception ) ;
      throw exception ;
    } else {
      return unescaped ;
    }
  }

  public static String ESCAPE_OPEN = "\u00ab" ; // «
  public static String ESCAPE_CLOSE = "\u00bb" ; // »


  /**
   * Returns escaped symbol.
   * @param unescaped must not be null.
   * @return null if not found.
   */
  public static String escapeSymbol( String unescaped ) {
    return UNESCAPED_SYMBOLS.get( unescaped ) ;
  }

  public static String escapeText( String text ) {
    final StringBuffer buffer = new StringBuffer() ;
    for( char c : text.toCharArray() ) {
      final String s = "" + c ; // Let the compiler optimize this!
      final String escaped = escapeSymbol( s ) ;
      if( null == escaped ) {
        buffer.append( c ) ;
      } else {
        buffer.append( ESCAPE_OPEN ).append( escaped ).append( ESCAPE_CLOSE ) ;
      }
    }
    return buffer.toString();
  }

  private static final Pattern ESCAPE_PATTERN =
      Pattern.compile( "(" + ESCAPE_OPEN + "(\\w+)" + ESCAPE_CLOSE + ")" ) ;

  static {
    LOGGER.debug( "Crafted regex {}", ESCAPE_PATTERN.pattern() ) ;
  }

  public static String unescapeText( String text ) throws UnsupportedEscapedSymbolException {
    final Matcher matcher = ESCAPE_PATTERN.matcher( text ) ;
    final StringBuffer buffer = new StringBuffer() ;
    int keepFrom = 0 ;
    while( matcher.find() ) {
      if( matcher.start() > 0 && keepFrom < text.length() ) {
        final String previous = text.substring( keepFrom, matcher.start() ) ;
        buffer.append( previous ) ;
      }
      final String escapeCode = matcher.group( 2 ) ;
      final String escapedSymbol = Symbols.unescapeSymbol( escapeCode ) ;
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

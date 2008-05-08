/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.parser;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMapBuilder;
import com.google.common.collect.BiMap;
import com.google.common.collect.Sets;

/**
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
    symbols.put( "plus", "+" ) ;
    symbols.put( "equals", "=" ) ;
    symbols.put( "dollar", "$" ) ;
    symbols.put( "lowline", "_" ) ;
    symbols.put( "euro", "\u20ac" ) ;
    symbols.put( "amp", "&" ) ;
    symbols.put( "solidus", "/" ) ;
    symbols.put( "lt", "<" ) ;
    symbols.put( "gt", ">" ) ;
    symbols.put( "tilde", "~" ) ;
//    symbols.put( "snip", "[...]" ) ; // TODO remove this and support litteral.
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
    return ImmutableMapBuilder.fromMap( ESCAPED_SYMBOLS ).getMap() ;
  }

  public static boolean isHtmlEscape( String escaped ) {
    return HTML_ESCAPED.contains( escaped ) ;
  }

  public static String unescape( String escaped )
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

  /**
   * Returns escaped symbol.
   * @param unescaped must not be null.
   * @return null if not found.
   */
  public static String escape( String unescaped ) {
    return UNESCAPED_SYMBOLS.get( unescaped ) ;
  }

}
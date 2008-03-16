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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMapBuilder;

/**
 * @author Laurent Caillette
 */
public class SymbolUnescape {

  private static final Logger LOGGER = LoggerFactory.getLogger( SymbolUnescape.class ) ;

  private static final Map< String, String > SYMBOLS = Maps.newHashMap() ;

  static {
    SYMBOLS.put( "percent", "%" ) ;
    SYMBOLS.put( "equals", "=" ) ;
    SYMBOLS.put( "dollar", "$" ) ;
    SYMBOLS.put( "lt", "<" ) ;
    SYMBOLS.put( "gt", ">" ) ;
    SYMBOLS.put( "tilde", "~" ) ;
    SYMBOLS.put( "deg", "\u00b0" ) ;
    SYMBOLS.put( "oelig", "\u0153" ) ;
    SYMBOLS.put( "OElig", "\u0152" ) ;
  }

  public static Map< String, String > getDefinitions() {
    return ImmutableMapBuilder.fromMap( SYMBOLS ).getMap() ;
  }


  public static String unescape( String escaped )
      throws UnsupportedEscapedSymbolException
  {
    final String unescaped = SYMBOLS.get( escaped ) ;
    if( null == unescaped ) {
      final UnsupportedEscapedSymbolException exception =
          new UnsupportedEscapedSymbolException( escaped ) ;
      LOGGER.warn( "Unsupported symbol", exception ) ;
      throw exception ;
    } else {
      return unescaped ;
    }
  }

  public static class SymbolDefinition {
    private final String unescaped ;
    private final String escaped ;


    public SymbolDefinition( String unescaped, String escaped ) {
      this.unescaped = unescaped;
      this.escaped = escaped;
    }


    public String getUnescaped() {
      return unescaped;
    }

    public String getEscaped() {
      return escaped;
    }
  }
}

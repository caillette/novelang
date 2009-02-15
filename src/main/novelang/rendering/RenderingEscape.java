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
package novelang.rendering;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Map;

import org.apache.commons.lang.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import novelang.parser.GeneratedLexemes;
import novelang.parser.Lexeme;

/**
 * Escapes characters for rendering. Based on {@link GeneratedLexemes}.
 * <p>
 * Novelang grammar <em>must</em> define at least HTML named entities for those characters
 * (otherwise HTML rendering will break):
 * <pre>
&lt; &gt; &amp;
 * </pre>
 *
 * @author Laurent Caillette
 */
public class RenderingEscape {

  private static final Logger LOGGER = LoggerFactory.getLogger( RenderingEscape.class ) ;

  private static final Map< Character, String > HTML_CONVENIENCE_ESCAPES;
  private static final Map< Character, String > HTML_MANDATORY_ESCAPES;

  static {
    final Map< Character, String > htmlConvenienceEscapes = Maps.newHashMap() ;
    final Map< Character, String > htmlMandatoryEscapes = Maps.newHashMap() ;
    for( Lexeme lexeme : GeneratedLexemes.getLexemes().values() ) {
      final String htmlEntityName = lexeme.getHtmlEntityName();
      if( htmlEntityName != null ) {
        put( lexeme.getCharacter(), htmlEntityName, htmlConvenienceEscapes ) ;
      }
    }
    put( '<', "lt", htmlConvenienceEscapes, htmlMandatoryEscapes ) ;
    put( '>', "gt", htmlConvenienceEscapes, htmlMandatoryEscapes ) ;
    put( '&', "amp", htmlConvenienceEscapes, htmlMandatoryEscapes ) ;

    HTML_CONVENIENCE_ESCAPES = ImmutableMap.copyOf( htmlConvenienceEscapes ) ;
    HTML_MANDATORY_ESCAPES = ImmutableMap.copyOf( htmlMandatoryEscapes ) ;
    LOGGER.debug( "Created HTML convenience escape table with {} entries.",
        HTML_CONVENIENCE_ESCAPES.size() ) ;
    LOGGER.debug( "Created HTML mandatory escape table with {} entries.",
        HTML_MANDATORY_ESCAPES.size() ) ;
  }

  private static void put( Character character, String string, Map< Character, String >... maps ) {
    for( Map< Character, String > map : maps ) {
      map.put( character, string ) ;
    }
  }

  /**
   * Replaces a given character with HTML named entity if not a part of given charset,
   * or returns the character itself.
   *
   * @param unescaped a non-null object.
   * @param capability non-null object.
   * @return a non-null, non-empty String.
   */
  public static String maybeEscapeHtml( char unescaped, CharsetEncodingCapability capability ) {
    final String mandatoryEscape = HTML_MANDATORY_ESCAPES.get( unescaped ) ;
    if( null == mandatoryEscape ) {
      if( capability.canEncode( unescaped ) ) {
        return "" + unescaped ;
      } else {
        final String convenienceEscape = HTML_CONVENIENCE_ESCAPES.get( unescaped ) ;
        if( null ==  convenienceEscape ) {
            return "&" + CharUtils.unicodeEscaped( unescaped ) + ";" ;
        } else {
          return "&" + convenienceEscape + ";" ;
        }
      }
    } else {
      return "&" + mandatoryEscape + ";" ;
    }
  }

  public static String escapeHtmlText( String text, CharsetEncodingCapability capability ) {
    final StringBuffer buffer = new StringBuffer() ;
    for( char c : text.toCharArray() ) {
      final String escaped = maybeEscapeHtml( c, capability ) ;
      buffer.append( escaped ) ;
    }
    return buffer.toString() ;
  }

  public static CharsetEncodingCapability createCapability( final Charset charset ) {
    final CharsetEncoder encoder = charset.newEncoder() ;
    return new CharsetEncodingCapability() {
      public boolean canEncode( char c ) {
        return encoder.canEncode( c ) ;
      }
    } ;
  }

  public interface CharsetEncodingCapability {
    boolean canEncode( char c ) ;
  }
}

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
package org.novelang.rendering;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.parser.GeneratedLexemes;
import org.novelang.parser.SourceUnescape;
import org.novelang.parser.shared.Lexeme;
import org.apache.commons.lang.CharUtils;

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

// ===============  
// Tables creation
// ===============  
  
  private static final Map< Character, String > UNICODE_NAME_ESCAPES ;
  private static final Map< Character, String > PREFERRED_ESCAPES ;
  private static final Map< Character, String > HTML_MANDATORY_ESCAPES ;

  static {
    final Map< Character, String > unicodeNameEscapes = Maps.newHashMap() ;
    final Map< Character, String > preferredEscapes = Maps.newHashMap() ;
    final Map< Character, String > htmlMandatoryEscapes = Maps.newHashMap() ;
    for( final Lexeme lexeme : GeneratedLexemes.getLexemes().values() ) {
      final Character character = lexeme.getCharacter();
      final String unicodeName = lexeme.getUnicodeName() ;
      put( character, SourceUnescape.unicodeUpperNameToEscapeName( unicodeName ), unicodeNameEscapes ) ;
      final String htmlEntityName = lexeme.getHtmlEntityName();
      if( htmlEntityName != null ) {
        put( character, htmlEntityName, preferredEscapes ) ;
      }
    }
    put( '<', "lt", preferredEscapes, htmlMandatoryEscapes ) ;
    put( '>', "gt", preferredEscapes, htmlMandatoryEscapes ) ;
    put( '&', "amp", preferredEscapes, htmlMandatoryEscapes ) ;
    put( Spaces.NO_BREAK_SPACE, "nbsp", preferredEscapes, htmlMandatoryEscapes ) ;

    UNICODE_NAME_ESCAPES = ImmutableMap.copyOf( unicodeNameEscapes ) ;
    LOGGER.debug(
        "Created Unicode name escape table with ",
        UNICODE_NAME_ESCAPES.size(),
        " entries."
    ) ;
 
    PREFERRED_ESCAPES = ImmutableMap.copyOf( preferredEscapes ) ;
    LOGGER.debug(
        "Created preferred escape table with ",
        PREFERRED_ESCAPES.size(),
        " entries."
    ) ;

    HTML_MANDATORY_ESCAPES = ImmutableMap.copyOf( htmlMandatoryEscapes ) ;
    LOGGER.debug(
        "Created HTML mandatory escape table with ",
        HTML_MANDATORY_ESCAPES.size(),
        " entries."
    ) ;
  }

  private static void put( 
      final Character character, 
      final String string, 
      final Map< Character, String >... maps 
  ) {
    for( final Map< Character, String > map : maps ) {
      map.put( character, string ) ;
    }
  }

  
// ===========  
// HTML escape  
// ===========  
  
  private static String escapeHtmlIfNeeded( 
      final char unescaped, 
      final CharsetEncodingCapability capability 
  ) {
    final String mandatoryEscape = HTML_MANDATORY_ESCAPES.get( unescaped ) ;
    if( null == mandatoryEscape ) {
      if( capability.canEncode( unescaped ) ) {
        return "" + unescaped ;
      } else {
        final String convenienceEscape = PREFERRED_ESCAPES.get( unescaped ) ;
        if( null ==  convenienceEscape ) {
            return wrapWithHtmlEntityDelimiters( CharUtils.unicodeEscaped( unescaped ) ) ;
        } else {
          return wrapWithHtmlEntityDelimiters( convenienceEscape ) ;
        }
      }
    } else {
      return wrapWithHtmlEntityDelimiters( mandatoryEscape ) ;
    }
  }
  
  private static String wrapWithHtmlEntityDelimiters( final String string ) {
    return "&" + string + ";" ;
  }


  /**
   * For each character, replaces a given character with HTML named entity if not a part 
   * of given charset.
   *
   * @param text a non-null object.
   * @param capability non-null object.
   * @return a non-null, non-empty String.
   */
  public static String escapeToHtmlText( 
      final String text, 
      final CharsetEncodingCapability capability 
  ) {
    final StringBuffer buffer = new StringBuffer() ;
    for( final char c : text.toCharArray() ) {
      final String escaped = escapeHtmlIfNeeded( c, capability ) ;
      buffer.append( escaped ) ;
    }
    return buffer.toString() ;
  }


// =============  
// Source escape  
// =============  

  private static String escapeToSourceIfNeeded( 
      final char unescaped, 
      final CharsetEncodingCapability capability 
  ) {
    if( capability.canEncode( unescaped ) ) {
      return "" + unescaped ;
    } else {
      return unconditionalEscapeToSource( unescaped );
    }
  }

  public static String unconditionalEscapeToSource( final char unescaped ) {
    final String preferredEscape = PREFERRED_ESCAPES.get( unescaped ) ;
    if( null ==  preferredEscape ) {
      final String unicodeEscape = UNICODE_NAME_ESCAPES.get( unescaped );
      if( null == unicodeEscape ) {
        throw new IllegalArgumentException(
            "No Unicode name for: " + CharUtils.unicodeEscaped( unescaped ) ) ;
      } else {
        return wrapWithSourceEscapeDelimiters( unicodeEscape ) ;
      }
    } else {
      return wrapWithSourceEscapeDelimiters( preferredEscape ) ;
    }
  }


  private static String wrapWithSourceEscapeDelimiters( final String string ) {
    return 
        SourceUnescape.ESCAPE_START + 
        string + 
        SourceUnescape.ESCAPE_END
    ;
  }

  /**
   * For each character, replaces with source-friendly escape if not a part of given charset.
   * Escape is based on HTML entity name if available, or Unicode name otherwise.
   *
   * @param text a non-null object.
   * @param capability non-null object.
   * @return a non-null, non-empty String.
   */
  public static String escapeToSourceText( 
      final String text, 
      final CharsetEncodingCapability capability 
  ) {
    final StringBuffer buffer = new StringBuffer() ;
    for( final char c : text.toCharArray() ) {
      final String escaped = escapeToSourceIfNeeded( c, capability ) ;
      buffer.append( escaped ) ;
    }
    return buffer.toString() ;
  }
  
  
// ===================  
// Encoding capability  
// ===================  

  /**
   * Avoids to expose a whole {@link CharsetEncoder} while we just want to know 
   * if it can encode a character.
   */
  public static CharsetEncodingCapability createCapability( final Charset charset ) {
    final CharsetEncoder encoder = charset.newEncoder() ;
    return new CharsetEncodingCapability() {
      public boolean canEncode( final char c ) {
        return encoder.canEncode( c ) ;
      }
    } ;
  }

  public interface CharsetEncodingCapability {
    boolean canEncode( char c ) ;
  }
}

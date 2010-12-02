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
package org.novelang.novelist;

import java.util.Locale;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;

/**
 * Defines character frequencies idepending on language.
 *
 * @author Laurent Caillette
 */
public class LetterDistribution extends Distribution< Character > {

  private static final Logger LOGGER = LoggerFactory.getLogger( LetterDistribution.class ) ;

  private LetterDistribution( final Map< Character, Float > frequencies ) {
    super( LetterDistribution.class.getSimpleName(), frequencies ) ;
  }


  private final static Map< Locale, LetterDistribution > DISTRIBUTIONS = Maps.newHashMap() ;

  /**
   * Returns a {@code Map} between a character and its frequency in the given language.
   */
  public synchronized static LetterDistribution getFrequency( final Locale locale ) {
    final Locale supportedLocale ;
    if( locale == SupportedLocales.DEFAULT_LOCALE ) {
      supportedLocale = locale;
    } else {
      LOGGER.warn( "Unsupported: ", locale, ", using default: ", SupportedLocales.DEFAULT_LOCALE );
      supportedLocale = SupportedLocales.DEFAULT_LOCALE;
    }
    if( ! DISTRIBUTIONS.containsKey( supportedLocale ) ) {
      DISTRIBUTIONS.put( supportedLocale, new LetterDistribution( FRENCH_FREQUENCIES ) ) ;
    }
    return DISTRIBUTIONS.get( supportedLocale ) ;
  }


  /**
   * These are frequencies for the French language, as documented by
   * <a href="http://en.wikipedia.org/wiki/Letter_frequency" >Wikipedia</a>.
   * <p>
   * Here is the regex for turning the table into Java code. Use a Web browser with Gecko engine
   * (Firefox, Camino) to copy the table cells of interest.
   * See meaning of "{L}" in the regex in
   * <a href="http://www.unicode.org/reports/tr18">Unicode Regular Expressions</a>,
   * "General Category Property" chapter.
   * <p/>
   * <pre>
   * (\p{L})\t(\d+(?:\.\d+)?)\%?$
   * .put( '$1', $2f )
   * </pre>
   */
  private static final Map< Character, Float > FRENCH_FREQUENCIES =
      new ImmutableMap.Builder< Character, Float >()
      .put( 'a', 7.636f )
      .put( 'b', 0.901f )
      .put( 'c', 3.260f )
      .put( 'd', 3.669f )
      .put( 'e', 14.715f )
      .put( 'f', 1.066f )
      .put( 'g', 0.866f )
      .put( 'h', 0.737f )
      .put( 'i', 7.529f )
      .put( 'j', 0.545f )
      .put( 'k', 0.049f )
      .put( 'l', 5.456f )
      .put( 'm', 2.968f )
      .put( 'n', 7.095f )
      .put( 'o', 5.378f )
      .put( 'p', 3.021f )
      .put( 'q', 1.362f )
      .put( 'r', 6.553f )
      .put( 's', 7.948f )
      .put( 't', 7.244f )
      .put( 'u', 6.311f )
      .put( 'v', 1.628f )
      .put( 'w', 0.114f )
      .put( 'x', 0.387f )
      .put( 'y', 0.308f )
      .put( 'z', 0.136f )
      .put( 'à', 0.486f )
      .put( 'å', 0.0f )
      .put( 'ä', 0.0f )
      .put( 'ą', 0.0f )
      .put( 'œ', 0.018f )
      .put( 'ç', 0.085f )
      .put( 'ĉ', 0.0f )
      .put( 'ć', 0.0f )
      .put( 'è', 0.271f )
      .put( 'é', 1.904f )
      .put( 'ê', 0.225f )
      .put( 'ë', 0.001f ) // Cheated: original is 0.000
      .put( 'ę', 0.0f )
      .put( 'ĝ', 0.0f )
      .put( 'ğ', 0.0f )
      .put( 'ĥ', 0.0f )
      .put( 'î', 0.045f )
      .put( 'ì', 0.0f )
      .put( 'ï', 0.005f )
      .put( 'ı', 0.0f )
      .put( 'ĵ', 0.0f )
      .put( 'ł', 0.0f )
      .put( 'ñ', 0.0f )
      .put( 'ń', 0.0f )
      .put( 'ò', 0.0f )
      .put( 'ö', 0.0f )
      .put( 'ó', 0.0f )
      .put( 'ŝ', 0.0f )
      .put( 'ş', 0.0f )
      .put( 'ś', 0.0f )
      .put( 'ß', 0.0f )
      .put( 'ù', 0.058f )
      .put( 'ŭ', 0.0f )
      .put( 'ü', 0.0f )
      .put( 'ź', 0.0f )
      .put( 'ż', 0.0f )
      .build()
  ;


}

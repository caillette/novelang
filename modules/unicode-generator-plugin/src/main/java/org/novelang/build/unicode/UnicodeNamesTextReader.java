/*
 * Copyright (C) 2011 Laurent Caillette
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

package org.novelang.build.unicode;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;


import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * Reads Unicode character names from a property file {@value #RESOURCE_NAME}.
 * The file may contain duplicate keys.
 *
 * @author Laurent Caillette
 */
/*package*/ class UnicodeNamesTextReader {

  private static final Logger LOGGER = LoggerFactory.getLogger( UnicodeNamesTextReader.class ) ;

  /**
   * <a href="http://www.unicode.org/Public/5.2.0/ucd/UnicodeData.txt" >Unicode 5.2</a>
   */
  private static final String RESOURCE_NAME = "UnicodeData.txt" ;

  private static final String DESCRIPTOR_TEXT = "(?:\\w| |-|/|,|<|>|\\(|\\))*" ;
  private static final String IGNORED_DESCRIPTOR = "(?:" + DESCRIPTOR_TEXT + ";)" ;
  private static final String USEFUL_DESCRIPTOR = "(" + DESCRIPTOR_TEXT + ");" ;

  private static final Pattern PROPERTY_LINE_PATTERN =
      Pattern.compile( "(\\w{4});" +
          USEFUL_DESCRIPTOR +
          IGNORED_DESCRIPTOR + "{8}" +
          USEFUL_DESCRIPTOR +
          IGNORED_DESCRIPTOR + "{3}" +
          "(?:\\w*)"
      ) ;

  static {
    LOGGER.debug( "Crafted regex: " + PROPERTY_LINE_PATTERN.pattern() ) ;
  }


  private static String readProperties() throws IOException {

    final URL resource = UnicodeNamesTextReader.class.getResource( RESOURCE_NAME ) ;
    LOGGER.info( "Reading " + resource.toExternalForm() ) ;
    final InputStream inputStream = resource.openStream() ;
    return IOUtils.toString( inputStream ) ;
  }

  public Map< Character, String > loadNames() throws IOException {
    return extractNames( readProperties() ) ;
  }

  /*package*/ Map< Character, String > extractNames( final String names ) throws IOException {
    final Map< Character, String > characterToNameMap =
        Maps.newHashMapWithExpectedSize( 256 * 256 ) ;
    final Matcher matcher = PROPERTY_LINE_PATTERN.matcher( names ) ;
    int limiter = 0 ;

    while( matcher.find() /*&& limiter < 150*/ ) {
      final String code = matcher.group( 1 ) ;
      if( code.length() == 4 ) {
        final String name ;
        final String casualName = matcher.group( 2 ) ;
        if( "<control>".equals( casualName ) ) {
          final String controlName = matcher.group( 3 ) ;
          name = controlName ;
        } else {
          name = casualName ;
        }

        final int codeAsInt = Integer.parseInt( code, 16 ) ; // Be confident!
        final Character character = ( char ) codeAsInt ;
        final String existing = characterToNameMap.get( character ) ;
        if( existing == null && ! "".equals( name ) ) {
          // Retain first definition, seems that most interesting appear first.
          characterToNameMap.put( character, name ) ;
//          LOG.info( "Added " + ( ( int ) character ) + " as '" + name + "'" +
//                  ( character != limiter ? " OOOPS!" : "" )
//          ) ;
        }
      }
      limiter++ ;
    }
    return ImmutableMap.copyOf( characterToNameMap ) ;
  }


}
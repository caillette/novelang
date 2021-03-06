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
package org.novelang.outfit.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.xml.sax.InputSource;

import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;

/**
 * Tweaks DTD entity on-the-fly for escaping entity that we need to appear as-they-are
 * inside rendered HTML.
 *
 * @author Laurent Caillette
 */
public class DtdTools {

  private static final Logger LOGGER = LoggerFactory.getLogger( DtdTools.class );

  private static final Pattern PATTERN = Pattern.compile(
      "\\<\\!ENTITY\\s+(\\w+)\\s+\\\"(&#(?:\\d|\\w|\\;)+)\\\"\\s*?\\>" ) ;

  static {
    LOGGER.debug( "Crafted regex ", PATTERN.pattern() ) ;
  }
  private static final String REPLACEMENT = "<!ENTITY $1 \"&amp;$1;\" > " ;

  private DtdTools() { }

  public static InputSource escapeEntities( final InputSource unescapedInputSource )
      throws IOException
  {
    final String unescapedDtd ;
    if( null == unescapedInputSource.getCharacterStream() ) {
      if( null == unescapedInputSource.getByteStream() ) {
        throw new IllegalArgumentException( "unescapedInputSource provides no valid stream" ) ;
      } else {
        unescapedDtd = IOUtils.toString( unescapedInputSource.getByteStream() ) ;
      }
    } else {
      unescapedDtd = IOUtils.toString( unescapedInputSource.getCharacterStream() ) ;
    }

    final Matcher matcher = PATTERN.matcher( unescapedDtd ) ;
    
    final String escapedDtd = matcher.replaceAll( REPLACEMENT ) ;
    final InputSource escapedInputSource = new InputSource( unescapedInputSource.getSystemId() );
    escapedInputSource.setEncoding( unescapedInputSource.getEncoding() ) ;
    escapedInputSource.setPublicId( unescapedInputSource.getPublicId() ) ;
    escapedInputSource.setCharacterStream( new StringReader( escapedDtd ) ) ;
    return escapedInputSource;
  }


}

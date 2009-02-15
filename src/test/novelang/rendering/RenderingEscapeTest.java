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

import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.parser.NoUnescapedCharacterException;

/**
 * Tests for {@link RenderingEscape}.
 *
 * @author Laurent Caillette
 */
public class RenderingEscapeTest {

  private static final Logger LOGGER = LoggerFactory.getLogger( RenderingEscapeTest.class ) ;

  @Test
  public void escapeHtml0() throws NoUnescapedCharacterException {
    assertEquals(
        "&amp;x&gt;y&lt;z",
        RenderingEscape.escapeHtmlText( "&x>y<z", CHARSET_ENCODING_CAPABILITY )
    ) ;
  }

  @Test
  public void escapeHtmlWithEncoding0() throws NoUnescapedCharacterException {
    assertEquals(
        "x&oelig;\u00e8",
        RenderingEscape.escapeHtmlText( "x\u0153\u00e8", CHARSET_ENCODING_CAPABILITY )
    ) ;
  }

  @Test
  public void charsetEncodingCapability0() {

    // LATIN_SMALL_LETTER_E_WITH_GRAVE
    assertTrue( CHARSET_ENCODING_CAPABILITY.canEncode( '\u00e8' ) ) ;

    // LATIN_SMALL_LETTER_O_WITH_DOUBLE_ACUTE
    assertFalse( CHARSET_ENCODING_CAPABILITY.canEncode( '\u0151' ) ) ;
  }


// =======
// Fixture
// =======

  private static final Charset ISO_8859_1 = Charset.forName( "ISO-8859-1" ) ;

  private static final RenderingEscape.CharsetEncodingCapability CHARSET_ENCODING_CAPABILITY =
      RenderingEscape.createCapability( ISO_8859_1 ) ;

}

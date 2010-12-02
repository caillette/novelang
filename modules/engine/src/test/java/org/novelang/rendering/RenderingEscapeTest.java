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
package org.novelang.rendering;

import org.novelang.parser.NoUnescapedCharacterException;
import org.novelang.parser.SourceUnescape;
import static org.junit.Assert.*;
import org.junit.Test;

import java.nio.charset.Charset;

/**
 * Tests for {@link RenderingEscape}.
 *
 * @author Laurent Caillette
 */
public class RenderingEscapeTest {

  /**
   * Not defined in {@link #ISO_8859_1} but has HTML entity name.
   */
  protected static final char OE_LIGATURED = '\u0153' ;
  
  /**
   * Defined in {@link #ISO_8859_1} and has HTML entity name.
   */
  private static final String EGRAVE = "\u00e8";
  
  /**
   * Not defined in {@link #ISO_8859_1} nor has HTML entity name.
   */  
  private static final String O_DOUBLEACUTE = "\u0151";

  @Test
  public void escapeToHtmlWithMandatoryEscapes() throws NoUnescapedCharacterException {
    assertEquals(
        "&amp;x&gt;y&lt;z",
        RenderingEscape.escapeToHtmlText( "&x>y<z", CHARSET_ENCODING_CAPABILITY )
    ) ;
  }

  @Test
  public void escapeHtmlWithHtmlEntityName() throws NoUnescapedCharacterException {
    assertEquals(
        "x&oelig;",
        RenderingEscape.escapeToHtmlText( 
            "x" + OE_LIGATURED, 
            CHARSET_ENCODING_CAPABILITY 
        )
    ) ;
  }  
  
  @Test
  public void escapeToHtmlWithNoEscapeNeeded() throws NoUnescapedCharacterException {
    assertEquals(
        "x" + EGRAVE,
        RenderingEscape.escapeToHtmlText( 
            "x" + EGRAVE, 
            CHARSET_ENCODING_CAPABILITY 
        )
    ) ;
  }

  
  
  @Test 
  public void escapeToSourceWithNoEscapeNeeded() {
    assertEquals(
        "x" + EGRAVE,
        RenderingEscape.escapeToSourceText( 
            "x" + EGRAVE,
            CHARSET_ENCODING_CAPABILITY        
        )
    ) ;
  }

  @Test 
  public void escapeToSourceWithHtmlEntityName() {
    assertEquals(
        "x" + SourceUnescape.ESCAPE_START + "oelig" + SourceUnescape.ESCAPE_END ,
        RenderingEscape.escapeToSourceText( 
            "x" + OE_LIGATURED,
            CHARSET_ENCODING_CAPABILITY        
        )
    ) ;
  }

  @Test 
  public void escapeToSourceWithUnicodeName() {
    assertEquals(
        "x" + 
            SourceUnescape.ESCAPE_START + 
            "latin-small-letter-o-with-double-acute" + 
            SourceUnescape.ESCAPE_END
        ,
        RenderingEscape.escapeToSourceText( 
            "x" + O_DOUBLEACUTE,
            CHARSET_ENCODING_CAPABILITY        
        )
    ) ;
  }

  @Test 
  public void escapeToSourceNotMessedWithHtml() {
    assertEquals(
        "&<>",
        RenderingEscape.escapeToSourceText( 
            "&<>",
            CHARSET_ENCODING_CAPABILITY        
        )
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

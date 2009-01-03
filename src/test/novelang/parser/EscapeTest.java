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

import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static novelang.parser.Escape.ESCAPE_START;
import static novelang.parser.Escape.ESCAPE_END;

/**
 * Tests for {@link Escape}.
 *
 * @author Laurent Caillette
 */
public class EscapeTest {
  
  private static final Logger LOGGER = LoggerFactory.getLogger( EscapeTest.class ) ; 

  @Test
  public void escapeNotFoundMessageOk() throws NoUnescapedCharacterException {
    final String escapedCode = "does-not-exist";
    final String escaped = ESCAPE_START + escapedCode + ESCAPE_END ;
    final String text = "Please escape " + escaped + "!" ;
    try {
      Escape.unescapeText( escaped ) ;
      Assert.fail( "Failed to catch exception" ) ;
    } catch ( NoUnescapedCharacterException e ) {
      assertTrue( e.getMessage().contains( escapedCode ) ) ;
    }
  }

  @Test( expected = NoUnescapedCharacterException.class )
  public void escapeNotFound1() throws NoUnescapedCharacterException {
    Escape.unescapeText( ESCAPE_START + "does-not-exist" + ESCAPE_END ) ;
  }

  @Test
  public void escape0() throws NoUnescapedCharacterException {
    assertEquals( ">", Escape.unescapeText( ESCAPE_START + "greater-than-sign" + ESCAPE_END ) ) ;
  }

  @Test
  public void escape1() throws NoUnescapedCharacterException {
    assertEquals(
        "a>",
        Escape.unescapeText( "a" + ESCAPE_START + "greater-than-sign" + ESCAPE_END )
    ) ;
  }

  @Test
  public void escape2() throws NoUnescapedCharacterException {
    assertEquals(
        "a>b",
        Escape.unescapeText( "a" + ESCAPE_START + "greater-than-sign" + ESCAPE_END + "b" )
    ) ;
  }

  @Test
  public void escape3() throws NoUnescapedCharacterException {
    assertEquals(
        "a><",
        Escape.unescapeText(
            "a" + ESCAPE_START + "greater-than-sign" + ESCAPE_END +
            ESCAPE_START + "lower-than-sign" + ESCAPE_END
        )
    ) ;
  }

  @Test
  public void escape5() throws NoUnescapedCharacterException {
    assertEquals(
        "a>b<",
        Escape.unescapeText(
            "a" + ESCAPE_START + "greater-than-sign" + ESCAPE_END + "b" +
            ESCAPE_START + "lower-than-sign" + ESCAPE_END
        )
    ) ;
  }

  @Test
  public void escape6() throws NoUnescapedCharacterException {
    assertEquals(
        "a>b<c",
        Escape.unescapeText(
            "a" + ESCAPE_START + "greater-than-sign" + ESCAPE_END + "b" +
                ESCAPE_START + "lower-than-sign" + ESCAPE_END + "c"
        )
    ) ;
  }

  @Test
  public void escape7() throws NoUnescapedCharacterException {
    assertEquals(
        "abc>d<e",
        Escape.unescapeText(
            "abc" + ESCAPE_START + "gt" + ESCAPE_END + "d" +
                ESCAPE_START + "lt" + ESCAPE_END + "e"
        )
    ) ;
  }

  @Test
  public void escape8() throws NoUnescapedCharacterException {
    assertEquals(
        "abc>d<e",
        Escape.unescapeText(
            "abc" + ESCAPE_START + "greater-than-sign" + ESCAPE_END + "d" +
                ESCAPE_START + "lower-than-sign" + ESCAPE_END + "e"
        )
    ) ;
  }

  @Test
  public void escape9() throws NoUnescapedCharacterException {
    assertEquals(
        "abc>d<ef",
        Escape.unescapeText(
            "abc" + ESCAPE_START + "gt" + ESCAPE_END + "d" +
                ESCAPE_START + "lt" + ESCAPE_END + "ef"
        )
    ) ;
  }

  @Test
  public void escape10() throws NoUnescapedCharacterException {
    assertEquals(
        "abc>d<ef",
        Escape.unescapeText(
            "abc" + ESCAPE_START + "greater-than-sign" + ESCAPE_END + "d" +
                ESCAPE_START + "lower-than-sign" + ESCAPE_END + "ef"
        )
    ) ;
  }

  @Test
  public void escapeHtml0() throws NoUnescapedCharacterException {
    assertEquals(
        "&amp;x&gt;y&lt;z",
        Escape.escapeHtmlText( "&x>y<z" )
    ) ;
  }


}

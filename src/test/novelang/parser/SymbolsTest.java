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

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static novelang.parser.Symbols.ESCAPE_CLOSE;
import static novelang.parser.Symbols.ESCAPE_OPEN;

/**
 * Tests for {@link Symbols}.
 *
 * @author Laurent Caillette
 */
public class SymbolsTest {

  @Test
  public void escape0() throws UnsupportedEscapedSymbolException {
    assertEquals( "&", Symbols.unescapeText( ESCAPE_OPEN + "amp" + ESCAPE_CLOSE ) ) ;
  }

  @Test
  public void escape1() throws UnsupportedEscapedSymbolException {
    assertEquals(
        "a&",
        Symbols.unescapeText( "a" + ESCAPE_OPEN + "amp" + ESCAPE_CLOSE )
    ) ;
  }

  @Test
  public void escape2() throws UnsupportedEscapedSymbolException {
    assertEquals(
        "a&b",
        Symbols.unescapeText( "a" + ESCAPE_OPEN + "amp" + ESCAPE_CLOSE + "b" )
    ) ;
  }

  @Test
  public void escape3() throws UnsupportedEscapedSymbolException {
    assertEquals(
        "a&<",
        Symbols.unescapeText(
            "a" + ESCAPE_OPEN + "amp" + ESCAPE_CLOSE + ESCAPE_OPEN + "lt" + ESCAPE_CLOSE )
    ) ;
  }

  @Test
  public void escape5() throws UnsupportedEscapedSymbolException {
    assertEquals(
        "a&b<",
        Symbols.unescapeText(
            "a" + ESCAPE_OPEN + "amp" + ESCAPE_CLOSE + "b" + ESCAPE_OPEN + "lt" + ESCAPE_CLOSE )
    ) ;
  }

  @Test
  public void escape6() throws UnsupportedEscapedSymbolException {
    assertEquals(
        "a&b<c",
        Symbols.unescapeText(
            "a" + ESCAPE_OPEN + "amp" + ESCAPE_CLOSE + "b" +
            ESCAPE_OPEN + "lt" + ESCAPE_CLOSE + "c"
        )
    ) ;
  }

  @Test
  public void escape7() throws UnsupportedEscapedSymbolException {
    assertEquals(
        "abc&d<e",
        Symbols.unescapeText(
            "abc" + ESCAPE_OPEN + "amp" + ESCAPE_CLOSE + "d" +
                ESCAPE_OPEN + "lt" + ESCAPE_CLOSE + "e"
        )
    ) ;
  }

  @Test
  public void escape8() throws UnsupportedEscapedSymbolException {
    assertEquals(
        "abc&d<ef",
        Symbols.unescapeText(
            "abc" + ESCAPE_OPEN + "amp" + ESCAPE_CLOSE + "d" +
                ESCAPE_OPEN + "lt" + ESCAPE_CLOSE + "ef"
        )
    ) ;
  }


}

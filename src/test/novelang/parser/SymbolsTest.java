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

/**
 * Tests for {@link Symbols}.
 *
 * @author Laurent Caillette
 */
public class SymbolsTest {

  @Test
  public void escape0() throws UnsupportedEscapedSymbolException {
    Assert.assertEquals( "&", Symbols.unescapeText( "&amp;" ) ) ;
  }

  @Test
  public void escape1() throws UnsupportedEscapedSymbolException {
    Assert.assertEquals( "a&", Symbols.unescapeText( "a&amp;" ) ) ;
  }

  @Test
  public void escape2() throws UnsupportedEscapedSymbolException {
    Assert.assertEquals( "a&b", Symbols.unescapeText( "a&amp;b" ) ) ;
  }

  @Test
  public void escape3() throws UnsupportedEscapedSymbolException {
    Assert.assertEquals( "a&<", Symbols.unescapeText( "a&amp;&lt;" ) ) ;
  }

  @Test
  public void escape5() throws UnsupportedEscapedSymbolException {
    Assert.assertEquals( "a&b<", Symbols.unescapeText( "a&amp;b&lt;" ) ) ;
  }

  @Test
  public void escape6() throws UnsupportedEscapedSymbolException {
    Assert.assertEquals( "a&b<c", Symbols.unescapeText( "a&amp;b&lt;c" ) ) ;
  }

  @Test
  public void escape7() throws UnsupportedEscapedSymbolException {
    Assert.assertEquals( "abc&d<e", Symbols.unescapeText( "abc&amp;d&lt;e" ) ) ;
  }

  @Test
  public void escape8() throws UnsupportedEscapedSymbolException {
    Assert.assertEquals( "abc&d<ef", Symbols.unescapeText( "abc&amp;d&lt;ef" ) ) ;
  }


}

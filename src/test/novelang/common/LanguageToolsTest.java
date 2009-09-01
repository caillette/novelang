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
package novelang.common;

import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link LanguageTools}.
 *
 * @author Laurent Caillette
 */
public class LanguageToolsTest {

  @Test
  public void lineBreakNormalizationDoingNothing() {
    final String s = "azer\tyuiop123" ;
    assertEquals( s, LanguageTools.normaliseLineBreaks( s ) ) ;
  }

  @Test
  public void simplestCrLfNormalization() {
    assertEquals("\u0010", LanguageTools.normaliseLineBreaks( "\u0013\u0010" ) ) ; 
  }

  @Test
  public void simplestCrNormalization() {
    assertEquals("\u0010", LanguageTools.normaliseLineBreaks( "\u0013" ) ) ;
  }

  @Test
  public void complexLineBreakNormalization() {
    assertEquals(
        "\u0010a\u0010b\u0010c",
        LanguageTools.normaliseLineBreaks( "\u0013a\u0010b\u0013\u0010c" )
    ) ;
  }


}
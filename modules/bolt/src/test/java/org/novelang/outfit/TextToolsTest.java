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
package org.novelang.outfit;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.novelang.outfit.TextTools.*;

/**
 * Tests for {@link org.novelang.outfit.TextTools}.
 *
 * @author Laurent Caillette
 */
public class TextToolsTest {

  @Test
  public void lineBreakNormalizationDoingNothing() {
    final String s = "azer\tyuiop123" ;
    assertEquals( s, unixifyLineBreaks( s ) ) ;
  }

  @Test
  public void simplestCrLfNormalization() {
    assertEquals( LINE_FEED, unixifyLineBreaks( CARRIAGE_RETURN + LINE_FEED ) ) ;
  }

  @Test
  public void twoCrLfNormalizations() {
    assertEquals(
        LINE_FEED + LINE_FEED,
        unixifyLineBreaks( CARRIAGE_RETURN + LINE_FEED + CARRIAGE_RETURN + LINE_FEED )
    ) ;
  }

  @Test
  public void simplestCrNormalization() {
    assertEquals( LINE_FEED, unixifyLineBreaks( CARRIAGE_RETURN ) ) ;
  }

  @Test
  public void complexLineBreakNormalization() {
    assertEquals(
        LINE_FEED + "a" + LINE_FEED + "b" + LINE_FEED + "c",
        unixifyLineBreaks(
                CARRIAGE_RETURN + "a" + LINE_FEED + "b" + CARRIAGE_RETURN + LINE_FEED + "c" )
    ) ;
  }

  @Test
  public void getSureThatWeDoEscapeRightCharacters() {
      final String stringOfRawChars = "" + ( ( char ) 13 ) + ( ( char ) 10 );
      final String stringOfUnicode = "\u0013\u0010";
      Assert.assertFalse( stringOfUnicode.equals( stringOfRawChars ) ) ; 
  }


// =======
// Fixture
// =======


}
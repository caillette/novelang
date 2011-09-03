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

package org.novelang.rendering;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link Spaces}.
 *
 * @author Laurent Caillette
 */
public class SpacesTest {

  @Test
  public void doNothing() {
    verifyHard( "", "" ); ;
  }

  @Test
  public void trimSpaces() {
    verifyHard( "x", " x  " ) ;
  }

  @Test
  public void replaceByNoBreakSpaces() {
    verifyHard( "x" + Spaces.NO_BREAK_SPACE + "y" + Spaces.NO_BREAK_SPACE + "z", "x  y  z" ) ;
  }


// =======
// Fixture
// =======

  private static void verifyHard( final String expected, final String toBeNormalized ) {
    final String normalized = Spaces.normalizeLiteral( toBeNormalized ) ;
    Assert.assertEquals( expected, normalized ) ;
  }


}
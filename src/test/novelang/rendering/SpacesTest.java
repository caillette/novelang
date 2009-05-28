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

import org.junit.Test;
import org.junit.Assert;
import org.apache.commons.lang.StringEscapeUtils;
import novelang.system.LogFactory;
import novelang.system.Log;

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

  public void replaceByNoBreakSpaces() {
    verifyHard( "x" + Spaces.NO_BREAK_SPACE + "y" + Spaces.NO_BREAK_SPACE, "x  y  z" ) ;
  }


// =======
// Fixture
// =======

  private static final Log LOG = LogFactory.getLog( SpacesTest.class ) ;

  private static void verifyHard( String expected, String toBeNormalized ) {
    LOG.debug( "Expected: '" + StringEscapeUtils.escapeJava( expected ) ) ;
    final String normalized = Spaces.normalizeHardLiteral( toBeNormalized ) ;
    LOG.debug( "Normalized: '" + StringEscapeUtils.escapeJava( normalized ) ) ;
    Assert.assertEquals( expected, normalized ) ;
  }

}
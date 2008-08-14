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
package novelang.rendering.xslt;

import org.junit.Test;
import org.junit.Assert;

/**
 * @author Laurent Caillette
 */
public class XsltFunctionsTest {

  @Test
  public void testConvertToNumber() {
    check( "Vingt-deux", 22, "FR", "capital" ) ;
  }

// =======
// Fixture
// =======

  private static void check( String expected, int number, String locale, String caseType ) {
    Assert.assertEquals(
        expected,
        XsltFunctions.numberAsText(
            new Double( number ),
            locale,
            caseType
        )
    ) ;

  }
}

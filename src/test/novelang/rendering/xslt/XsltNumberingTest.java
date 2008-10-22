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

import java.io.IOException;

import org.junit.Test;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;

/**
 * @author Laurent Caillette
 */
public class XsltNumberingTest {

  private static final Logger LOGGER = LoggerFactory.getLogger( XsltNumberingTest.class ) ;

  @Test
  public void convertToNumber() {
    check( "Vingt-deux", 22, "FR", "capital" ) ;
  }

  @Test
  public void formatDateTime() {
    final ReadableDateTime dateTime = new DateTime( 2008, 10, 18, 20, 37, 0, 0 ) ;
    final String humanReadableDateTime = Numbering.formatDateTime( dateTime, "yyyy-MM-dd kk:mm" ) ;
    final String base36DateTime = Numbering.formatDateTime( dateTime, "BASE36" ) ;
    Assert.assertEquals( "2k917445", base36DateTime );
    Assert.assertEquals( "2008-10-18 20:37", humanReadableDateTime );
  }

  @Test
  public void unformatDateTime() {
    final ReadableDateTime dateTime = new DateTime( 2008, 10, 18, 20, 37, 0, 0 ) ;
    Assert.assertEquals( dateTime, Numbering.unformatDateTime( "2k917445", "BASE36" ) ) ;

    Assert.assertEquals( 
        dateTime,
        Numbering.unformatDateTime( "2008-10-18 20:37", "yyyy-MM-dd kk:mm" )
    ) ;
  }

  @Test
  public void printScrambledDateTime() {
    final String scrambled = "2k917b2k";
    LOGGER.info(
        "Unscrambling {}: {}",
        scrambled,
        Numbering.formatDateTime(
            Numbering.unformatDateTime( scrambled, "BASE36" ),
            "yyyy-MM-dd kk:mm"
        )
    ) ;
  }

// =======
// Fixture
// =======

  private static void check( String expected, int number, String locale, String caseType ) {
    Assert.assertEquals(
        expected,
        Numbering.numberAsText(
            ( double ) number,
            locale,
            caseType
        )
    ) ;

  }
}

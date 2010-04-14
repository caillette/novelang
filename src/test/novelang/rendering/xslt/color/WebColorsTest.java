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
package novelang.rendering.xslt.color;

import java.io.IOException;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import novelang.system.Log;
import novelang.system.LogFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link WebColors}.
 *
 * @author Laurent Caillette
 */
public class WebColorsTest {


  @Test 
  public void readSimpleXhtml() throws XMLStreamException, IOException {

    final String xhtml =
        "<html>                                                    \n" +
        "<head>                                                    \n" +
        "  <title>Whatever</title>                                 \n" +
        "</head>                                                   \n" +
        "<body>                                                    \n" +
        "<dl>                                                      \n" +
        "  <dt><strong>deepskyblue</strong><em>darkblue</em></dt>  \n" +
        "  <dt><strong>darkorange</strong><em>maroon</em></dt>     \n" +
        "  <dt><strong>darkslateblue</strong><em>beige</em></dt>   \n" +
        "  </dl>                                                   \n" +
        "</body>                                                   \n" +
        "</html>"
    ;

    final WebColors colorsReader = new WebColors( xhtml ) ;
    LOG.info( "Got those colors: " + colorsReader.getColorPairs() ) ;

    final Iterator< ColorPair > colorPairs = colorsReader.getColorCycler().iterator() ;

    assertTrue( colorPairs.hasNext() ) ;
    verify( "deepskyblue", "darkblue", colorPairs.next() ) ;
    verify( "darkorange", "maroon", colorPairs.next() ) ;
    verify( "darkslateblue", "beige", colorPairs.next() ) ;
    verify( "deepskyblue", "darkblue", colorPairs.next() ) ; // Cycling.

  }


// =======
// Fixture
// =======

  private static final Log LOG = LogFactory.getLog( WebColorsTest.class ) ;

  private static void verify(
      final String expectedBackgroundColorName,
      final String expectedForegroundColorName,
      final ColorPair colorPair
  ) {
    assertEquals( expectedBackgroundColorName, colorPair.getBackground() ) ;
    assertEquals( expectedForegroundColorName, colorPair.getForeground() ) ;
  }

}

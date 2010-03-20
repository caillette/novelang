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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import javax.xml.stream.XMLStreamException;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests for {@link WebColorsXhtmlReader}.
 *
 * @author Laurent Caillette
 */
public class WebColorsXhtmlReaderTest {

  @Test
  public void missingResourceCausesEmptyCycler() {
    final Iterable< ColorPair > cycler =
        new WebColorsXhtmlReader( getClass().getResource( "doesnotexist" ) )
        .getColorCycler()
    ;
    assertFalse( cycler.iterator().hasNext() ) ;
  }


  @Test @Ignore
  public void readSimpleXhtml() throws XMLStreamException, IOException {

    final String xhtml =
        "<html>                                                  \n" +
        "<body>                                                  \n" +
        "<head>                                                  \n" +
        "  <title>Whatever</title>                               \n" +
        "</head>                                                 \n" +
        "<dt><strong>deepskyblue</strong><em>darkblue</em></dt>  \n" +
        "<dt><strong>darkorange</strong><em>maroon</em></dt>     \n" +
        "<dt><strong>darkslateblue</strong><em>beige</em></dt>   \n" +
        "</body>                                                 \n" +
        "</html>"
    ;
    final InputStream inputStream = new ByteArrayInputStream( xhtml.getBytes( CHARSET ) ) ;
    final List< ColorPair > colorPairs = WebColorsXhtmlReader.readColorPairs( inputStream ) ;

    assertEquals( "deepskyblue", colorPairs.get( 0 ).getBackground() ) ;
    assertEquals( "darkblue", colorPairs.get( 0 ).getForeground() ) ;

    assertEquals( "darkorange", colorPairs.get( 1 ).getBackground() ) ;
    assertEquals( "maroon", colorPairs.get( 1 ).getForeground() ) ;

    assertEquals( "darkslateblue", colorPairs.get( 2 ).getBackground() ) ;
    assertEquals( "beige", colorPairs.get( 2 ).getForeground() ) ;

  }


// =======
// Fixture
// =======

  private static final Charset CHARSET = Charset.forName( "UTF-8" ) ;

}

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
package org.novelang.rendering.multipage;

import java.io.IOException;
import java.io.StringReader;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.novelang.common.metadata.PageIdentifier;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Tests for {@link org.novelang.rendering.multipage.XmlMultipageReader}.
 *
 * @author Laurent Caillette
 */
public class XmlMultipageReaderTest {

  @Test 
  public void readAndVerify() throws IOException, SAXException {
    parse( xmlMultipageReader, XML ) ;

    assertThat( xmlMultipageReader.getPageIdentifiers() ).isEqualTo( ImmutableMap.of(
        new PageIdentifier( "One" ), "value-1",
        new PageIdentifier( "Two" ), "value-2"
    ) ) ;
  }

// =======
// Fixture
// =======

  private final XmlMultipageReader xmlMultipageReader = new XmlMultipageReader() ;

  private static void parse(
      final XmlMultipageReader xmlMultipageReader,
      final String xml
  ) throws SAXException, IOException {
    final XMLReader reader = XMLReaderFactory.createXMLReader() ;
    reader.setContentHandler( xmlMultipageReader ) ;
    reader.parse( new InputSource( new StringReader( xml ) ) ) ;
  }

  private static final String XML =
      "<n:pages\n" +
      "    xmlns:n=\"http://novelang.org/book-xml/1.0\"\n" +
      ">\n" +
      "  <n:page>\n" +
      "    <n:page-identifier>One</n:page-identifier>\n" +
      "    <n:page-path>value-1</n:page-path>\n" +
      "  </n:page>\n" +
      "  <n:page>\n" +
      "    <n:page-identifier>Two</n:page-identifier>\n" +
      "    <n:page-path>value-2</n:page-path>\n" +
      "  </n:page>\n" +
      "</n:pages>"
  ;

}

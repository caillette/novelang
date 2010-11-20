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
package org.novelang.configuration.fop;

import java.io.IOException;
import java.io.StringReader;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import org.novelang.configuration.fop.FopFactoryConfiguration.Renderer ;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Laurent Caillette
 */
public class TestFopFactoryConfigurationReader {

  @Test
  public void justReadXml() throws SAXException, IOException {
    parse( fopConfigurationReader, XML ) ;
  }

  @Test
  public void verifyConfiguration() throws SAXException, IOException {
    parse( fopConfigurationReader, XML ) ;
    final FopFactoryConfiguration configuration = getUniqueConfiguration() ;

    assertThat( configuration.getTargetResolution() ).isEqualTo( 72 ) ;

    final ImmutableSet< Renderer > renderers = configuration.getRenderers() ;
    assertThat( renderers ).hasSize( 1 ) ;

    final Renderer renderer = renderers.asList().get( 0 ) ;
    assertThat( renderer.getMime() ).isEqualTo( "application/pdf" ) ;

    assertThat( renderer.getOutputProfile() ).isNotNull() ;
    assertThat( renderer.getOutputProfile().getName() )
        .isEqualTo( "profiles/EuropeISOCoatedFOGRA27.icc" ) ;

    final ImmutableList< Renderer.FontsDirectory > fontsDirectories =
        renderer.getFontsDirectories() ;
    assertThat( fontsDirectories ).hasSize( 2 ) ;

    final Renderer.FontsDirectory fontsDirectory0 = fontsDirectories.asList().get( 0 ) ;
    assertThat( fontsDirectory0.getRecursive() ).isFalse() ;
    assertThat( fontsDirectory0.getPath() ).isEqualTo( "my/fonts" ) ;

    final Renderer.FontsDirectory fontsDirectory1 = fontsDirectories.asList().get( 1 ) ;
    assertThat( fontsDirectory1.getRecursive() ).isTrue() ;
    assertThat( fontsDirectory1.getPath() ).isEqualTo( "more/fonts" ) ;

  }



// =======
// Fixture
// =======

  private final FopFactoryConfigurationReader fopConfigurationReader =
      new FopFactoryConfigurationReader() ;

  private FopFactoryConfiguration getUniqueConfiguration() {
    final ImmutableList< FopFactoryConfiguration > configurations =
        fopConfigurationReader.getConfigurations() ;
    assertThat( configurations ).hasSize( 1 ) ;
    return configurations.get( 0 ) ;
  }

  private static void parse(
      final FopFactoryConfigurationReader fopConfigurationReader,
      final String xml
  ) throws SAXException, IOException {
    final XMLReader reader = XMLReaderFactory.createXMLReader() ;
    reader.setContentHandler( fopConfigurationReader ) ;
    reader.parse( new InputSource( new StringReader( xml ) ) ) ;
  }

  private static final String XML = "<xsl:stylesheet\n" +
      "    version=\"1.0\"\n" +
      "    xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n" +
      "    xmlns:nmeta=\"http://novelang.org/meta-xsl/1.0\"\n" +
      ">\n" +
      "  <nmeta:fop version=\"1.0\" >\n" +
      "    <target-resolution> 72</target-resolution>\n" +
      "    <renderers>\n" +
      "      <renderer mime=\"application/pdf\" >\n" +
      "        <fonts-directory>my/fonts</fonts-directory>\n" +
      "        <fonts-directory recursive=\"true\" >more/fonts</fonts-directory>\n" +
      "        <output-profile>profiles/EuropeISOCoatedFOGRA27.icc</output-profile>\n" +
      "        <filterList>\n" +
      "          <value>null</value>\n" +
      "        </filterList>\n" +
      "        <filterList type=\"image\" >\n" +
      "          <value>flate</value>\n" +
      "          <value>ascii-85</value>\n" +
      "        </filterList>\n" +
      "      </renderer>\n" +
      "    </renderers>\n" +
      "  </nmeta:fop>\n" +
      "\n" +
      "</xsl:stylesheet>"
  ;

}

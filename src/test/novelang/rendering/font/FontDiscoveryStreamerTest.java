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
package novelang.rendering.font;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.fop.apps.FopFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import novelang.configuration.FopFontStatus;
import novelang.configuration.RenderingConfiguration;
import novelang.configuration.ConfigurationTools;
import novelang.daemon.FontDiscoveryHandler;
import novelang.rendering.font.SyntheticFontMapTest;
import novelang.loader.ClasspathResourceLoader;
import novelang.loader.ResourceLoader;
import novelang.loader.ResourceName;
import novelang.parser.Encoding;
import novelang.rendering.XslWriter;

/**
 * Tests for {@link FontDiscoveryStreamer}.
 *
 * @author Laurent Caillette
 */
public class FontDiscoveryStreamerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger( FontDiscoveryStreamerTest.class ) ;

  @Test
  public void noSmoke() throws Exception {

    final FontDiscoveryStreamer streamer = new FontDiscoveryStreamer(
        HOLLOW_RENDERING_CONFIGURATION,
        new ResourceName( "identity.xsl" )
    ) {
      protected XslWriter createXslWriter(
          RenderingConfiguration renderingConfiguration,
          ResourceName resourceName
      ) {
        return new XslWriter(
            FontDiscoveryStreamer.NAMESPACE.getURI(),
            FontDiscoveryStreamer.NAMESPACE.getPrefix(),
            renderingConfiguration,
            resourceName
        )  ;
      }
    } ;

    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream() ;
    streamer.generate( byteArrayOutputStream, ENCODING ) ;
    final String result = new String( byteArrayOutputStream.toByteArray(), ENCODING.name() ) ;

    LOGGER.debug( "Generated: \n{}", result ) ;

  }


// =======
// Fixture
// =======

  private static final ClasspathResourceLoader RESOURCE_LOADER =
      new ClasspathResourceLoader( ConfigurationTools.BUNDLED_STYLE_DIR ) ;
  
  private static final Charset ENCODING = Encoding.DEFAULT;

  private static final RenderingConfiguration HOLLOW_RENDERING_CONFIGURATION =
      new RenderingConfiguration() {
        public ResourceLoader getResourceLoader() {
          return RESOURCE_LOADER;
        }
        public FopFactory getFopFactory() { // Yet unused.
          return null ;
        }
        public FopFontStatus getCurrentFopFontStatus() {
          return SyntheticFontMapTest.FONT_STATUS ;
        }
      }
  ;

}
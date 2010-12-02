/*
 * Copyright (C) 2010 Laurent Caillette
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
package org.novelang.rendering.font;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

import org.novelang.configuration.ConfigurationTools;
import org.novelang.configuration.FopFontStatus;
import org.novelang.configuration.RenderingConfiguration;
import org.novelang.outfit.loader.ClasspathResourceLoader;
import org.novelang.outfit.loader.ResourceLoader;
import org.novelang.outfit.loader.ResourceName;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.rendering.XslWriter;
import org.novelang.outfit.DefaultCharset;
import org.apache.fop.apps.FopFactory;
import org.junit.Test;

/**
 * Tests for {@link FontDiscoveryStreamer}.
 *
 * @author Laurent Caillette
 */
public class FontDiscoveryStreamerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger( FontDiscoveryStreamerTest.class );

  @Test
  public void noSmoke() throws Exception {

    final FontDiscoveryStreamer streamer = new FontDiscoveryStreamer(
        HOLLOW_RENDERING_CONFIGURATION,
        new ResourceName( "identity.xsl" )
    ) {
      @Override
      protected XslWriter createXslWriter(
          final RenderingConfiguration renderingConfiguration,
          final ResourceName resourceName
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
    streamer.generate( byteArrayOutputStream, RENDERING_CHARSET ) ;
    final String result = new String( byteArrayOutputStream.toByteArray(), RENDERING_CHARSET.name() ) ;

    LOGGER.debug( "Generated: \n", result ) ;

  }


// =======
// Fixture
// =======

  private static final ClasspathResourceLoader RESOURCE_LOADER =
      new ClasspathResourceLoader( ConfigurationTools.BUNDLED_STYLE_DIR ) ;
  
  private static final Charset RENDERING_CHARSET = DefaultCharset.RENDERING;

  private static final RenderingConfiguration HOLLOW_RENDERING_CONFIGURATION =
      new RenderingConfiguration() {
        @Override
        public ResourceLoader getResourceLoader() {
          return RESOURCE_LOADER;
        }
        @Override
        public FopFactory getFopFactory() { // Yet unused.
          return null ;
        }
        @Override
        public FopFontStatus getCurrentFopFontStatus() {
          return SyntheticFontMapTest.FONT_STATUS ;
        }

        @Override
        public Charset getDefaultCharset() {
          return DefaultCharset.RENDERING ;
        }
      }
  ;

}

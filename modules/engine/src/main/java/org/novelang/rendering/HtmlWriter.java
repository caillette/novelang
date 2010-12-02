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

package org.novelang.rendering;

import java.io.OutputStream;
import java.nio.charset.Charset;

import org.novelang.outfit.xml.EntityEscapeSelector;
import org.xml.sax.ContentHandler;
import org.novelang.common.Nodepath;
import org.novelang.common.metadata.DocumentMetadata;
import org.novelang.configuration.RenderingConfiguration;
import org.novelang.outfit.loader.ResourceName;

/**
 * @author Laurent Caillette
 */
public class HtmlWriter extends XslWriter {

  protected static final ResourceName DEFAULT_HTML_STYLESHEET =  new ResourceName( "html.xsl" ) ;

  private final RenderingEscape.CharsetEncodingCapability charsetEncodingCapability ;

  public HtmlWriter(
      final RenderingConfiguration configuration,
      final ResourceName stylesheet,
      final Charset charset
  ) {
    super(
        configuration,
        null == stylesheet ? DEFAULT_HTML_STYLESHEET : stylesheet,
        charset,
        RenditionMimeType.HTML,
        ESCAPE_ISO_ENTITIES
    ) ;
    charsetEncodingCapability = RenderingEscape.createCapability( charset ) ;
  }

  @Override
  public void writeLiteral( final Nodepath kinship, final String word ) throws Exception {
      super.write( kinship, RenderingEscape.escapeToHtmlText( word, charsetEncodingCapability ) ) ;
  }

  @Override
  public void write( final Nodepath kinship, final String word ) throws Exception {
    super.write( kinship, RenderingEscape.escapeToHtmlText( word, charsetEncodingCapability ) ) ;
  }


  @Override
  protected ContentHandler createSinkContentHandler(
      final OutputStream outputStream,
      final DocumentMetadata documentMetadata,
      final Charset charset
  ) throws Exception {
    return new HtmlSink( outputStream, charset ) ;
  }

  private static final EntityEscapeSelector ESCAPE_ISO_ENTITIES = new EntityEscapeSelector() {
    @Override
    public boolean shouldEscape( final String publicId, final String systemId ) {
      return publicId.startsWith( "ISO 8879:1986//ENTITIES" ) ;
    }
  };
}
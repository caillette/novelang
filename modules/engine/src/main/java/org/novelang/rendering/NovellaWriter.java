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

package org.novelang.rendering;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.xml.transform.TransformerConfigurationException;
import org.novelang.common.Nodepath;
import org.novelang.common.metadata.DocumentMetadata;
import org.novelang.configuration.RenderingConfiguration;
import org.novelang.outfit.loader.ResourceName;
import org.novelang.outfit.xml.TransformerCompositeException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * A {@link FragmentWriter} which rewrites Novelang source.
 * 
 * @author Laurent Caillette
 */
public class NovellaWriter extends XslWriter {

  protected static final ResourceName DEFAULT_NOVELLA_STYLESHEET = new ResourceName( "novella.xsl" ) ;
  
  private final RenderingEscape.CharsetEncodingCapability charsetEncodingCapability ;

  

  public NovellaWriter(
      final RenderingConfiguration configuration,
      final ResourceName stylesheet,
      final Charset charset
  ) throws IOException, TransformerConfigurationException, SAXException, TransformerCompositeException
  {
    super(
        configuration,
        null == stylesheet ? DEFAULT_NOVELLA_STYLESHEET : stylesheet,
        charset,
        RenditionMimeType.NOVELLA
    ) ;
    this.charsetEncodingCapability = RenderingEscape.createCapability( charset ) ;
  }

  @Override
  protected final ContentHandler createSinkContentHandler(
      final OutputStream outputStream,
      final DocumentMetadata documentMetadata,
      final Charset charset
  )
      throws Exception
  {

    // dom4j's XML writer does some clever entity-escaping not good for us.
    return new TextSink( outputStream );
  }

  @Override
  public void write( final Nodepath kinship, final String word ) throws Exception {
    final String escaped = RenderingEscape.escapeToSourceText( word, charsetEncodingCapability ) ;
    super.write( kinship, escaped ) ;
  }

}

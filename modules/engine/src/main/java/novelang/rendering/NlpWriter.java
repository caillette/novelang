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

package novelang.rendering;

import java.io.OutputStream;
import java.nio.charset.Charset;

import org.xml.sax.ContentHandler;
import novelang.common.metadata.DocumentMetadata;
import novelang.common.Nodepath;
import novelang.configuration.RenderingConfiguration;
import novelang.loader.ResourceName;

/**
 * A {@link FragmentWriter} which rewrites Novelang source.
 * 
 * @author Laurent Caillette
 */
public class NlpWriter extends XslWriter {

  protected static final ResourceName DEFAULT_NLP_STYLESHEET = new ResourceName( "nlp.xsl" ) ;
  
  private final RenderingEscape.CharsetEncodingCapability charsetEncodingCapability ;

  

  public NlpWriter(
      final RenderingConfiguration configuration,
      final ResourceName stylesheet,
      final Charset charset
  ) {
    super(
        configuration,
        null == stylesheet ? DEFAULT_NLP_STYLESHEET : stylesheet,
        charset,
        RenditionMimeType.NLP 
    ) ;
    this.charsetEncodingCapability = RenderingEscape.createCapability( charset ) ;
  }

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

  public void write( final Nodepath kinship, final String word ) throws Exception {
    final String escaped = RenderingEscape.escapeToSourceText( word, charsetEncodingCapability ) ;
    super.write( kinship, escaped ) ;
  }

}
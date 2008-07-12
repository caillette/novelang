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
import novelang.configuration.RenderingConfiguration;
import novelang.common.Nodepath;
import novelang.common.metadata.TreeMetadata;
import novelang.parser.Symbols;
import novelang.loader.ResourceName;

/**
 * @author Laurent Caillette
 */
public class HtmlWriter extends XslWriter {

  protected static final ResourceName DEFAULT_HTML_STYLESHEET =  new ResourceName( "html.xsl" ) ;

  public HtmlWriter( RenderingConfiguration configuration, ResourceName stylesheet ) {
    super(
        configuration,
        null == stylesheet ? DEFAULT_HTML_STYLESHEET : stylesheet,
        RenditionMimeType.HTML,
        ESCAPE_ISO_ENTITIES
    ) ;
  }

  public void write( Nodepath kinship, String word ) throws Exception {
    final StringBuffer reconstructed = new StringBuffer() ;
    for( char c : word.toCharArray() ) {
      final String s = "" + c ; // Let the compiler optimize this!
      final String escaped = Symbols.escapeSymbol( s ) ;
      if( ( null != escaped ) && ! "&".equals( s ) && Symbols.isHtmlEscape( escaped ) ) {
        reconstructed.append( "&" ).append( escaped ).append( ";" ) ;
      } else {
        reconstructed.append( s ) ;
      }
    }
//    if( hasEscape ) {
//      super.writeLitteral( kinship, reconstructed.toString() ) ;
//    } else {
      super.write( kinship, reconstructed.toString() ) ;
//    }
  }


  protected ContentHandler createSinkContentHandler(
      OutputStream outputStream,
      TreeMetadata treeMetadata,
      Charset encoding
  ) throws Exception {
    return new HtmlSink( outputStream ) ; 
  }

  private static final EntityEscapeSelector ESCAPE_ISO_ENTITIES = new EntityEscapeSelector() {
    public boolean shouldEscape( String publicId, String systemId ) {
      return publicId.startsWith( "ISO 8879:1986" ) ;
//          "ISO 8879:1986//ENTITIES Numeric and Special Graphic//EN//XML"
//          "ISO 8879:1986//ENTITIES Publishing//EN//XML"
//          "ISO 8879:1986//ENTITIES Added Latin 1//EN//XML"
    }
  };
}
/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.rendering;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import novelang.model.common.NodePath;
import novelang.model.common.TreeMetadata;
import novelang.parser.Symbols;
import novelang.loader.ResourceLoader;
import novelang.configuration.RenderingConfiguration;

/**
 * @author Laurent Caillette
 */
public class EscapingWriter extends XslWriter {

  public EscapingWriter(
      RenderingConfiguration configuration,
      String xslFileName,
      RenditionMimeType mimeType
  ) {
    super( configuration, xslFileName, mimeType );
  }

  public void write( NodePath kinship, String word ) throws Exception {
    final StringBuffer reconstructed = new StringBuffer() ;
    for( char c : word.toCharArray() ) {
      final String s = "" + c ; // Let the compiler optimize this!
      final String escaped = Symbols.escape( s ) ;
      if( null == escaped ) {
        reconstructed.append( c ) ;
      } else {
        reconstructed.append( "&" ).append( escaped ).append( ";" ) ;
      }
    }
    super.write( kinship, reconstructed.toString() ) ;
  }



}
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

import novelang.configuration.RenderingConfiguration;
import novelang.model.common.Nodepath;
import novelang.parser.Symbols;

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

  public void write( Nodepath kinship, String word ) throws Exception {
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
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
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import novelang.common.Nodepath;
import novelang.common.metadata.DocumentMetadata;
import novelang.common.metadata.MetadataHelper;

/**
 * @author Laurent Caillette
 */
public class PlainTextWriter implements FragmentWriter {

  private final Charset charset ;

  public PlainTextWriter( Charset charset ) {
    this.charset = charset;
  }

  private PrintWriter writer ;

  public RenditionMimeType getMimeType() {
    return RenditionMimeType.TXT ;
  }

  public void startWriting(
      OutputStream outputStream,
      DocumentMetadata documentMetadata
  ) throws Exception {
    final OutputStreamWriter outputStreamWriter = new OutputStreamWriter( outputStream, charset ) ;        
    writer = new PrintWriter( outputStreamWriter ) ;
    writer.append( "Timestamp: " );
    writer.append(
        MetadataHelper.TIMESTAMP_FORMATTER.print( documentMetadata.getCreationTimestamp() ) ) ;
    writer.println() ;
  }

  public void finishWriting() throws Exception {
    writer.flush() ;
  }

  public void start( Nodepath kinship, boolean wholeDocument ) throws Exception {
    final String indentPlus = spaces( kinship.getDepth() + 1 ) ;
    writer.append( kinship.getCurrent().name() ).append( " { \n" ).append( indentPlus ) ;
  }

  public void end( Nodepath path ) throws Exception {
    final String indent = spaces( path.getDepth() ) ;
    final String indentMinus = spaces( path.getDepth() - 1 ) ;
    writer.append( "\n" ).append( indent ).append( "}\n" ).append( indentMinus ) ;
  }

  public void write( Nodepath path, String word ) throws Exception {
    writer.append( word ) ;
  }

  public void writeLiteral( Nodepath kinship, String word ) throws Exception {
    write( kinship, word ) ;
  }

  static final String spaces( int size ) {
    final StringBuffer buffer = new StringBuffer() ;
    for( int i = 0 ; i < size ; i++ ) {
      buffer.append( "  " ) ;
    }
    return buffer.toString() ;
  }
}

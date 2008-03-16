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

/**
 * @author Laurent Caillette
 */
public class PlainTextWriter implements FragmentWriter {

  private PrintWriter writer ;

  public RenditionMimeType getMimeType() {
    return RenditionMimeType.TXT ;
  }

  public void startWriting( OutputStream outputStream, Charset encoding ) throws Exception {
    writer = new PrintWriter( outputStream ) ;
  }

  public void finishWriting() throws Exception {
    writer.flush() ;
  }

  public void start( Path kinship, boolean wholeDocument ) throws Exception {
    final String indentPlus = spaces( kinship.getDepth() ) ;
    writer.append( kinship.getCurrent().name() ).append( " { \n" ).append( indentPlus ) ;
  }

  public void end( Path path, boolean wholeDocument ) throws Exception {
    final String indentMinus = spaces( path.getDepth() ) ;
    writer.append( "\n" ).append( indentMinus ).append( "}\n" ).append( indentMinus ) ;
  }

  public void just( String word ) throws Exception {
    writer.append( word ) ;
  }


  static final String spaces( int size ) {
    final StringBuffer buffer = new StringBuffer() ;
    for( int i = 0 ; i < size ; i++ ) {
      buffer.append( "  " ) ;
    }
    return buffer.toString() ;
  }
}

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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.novelang.common.Nodepath;
import org.novelang.common.metadata.DocumentMetadata;
import org.novelang.parser.NodeKind;

/**
 * @author Laurent Caillette
 */
public class PlainTextWriter implements FragmentWriter {

  private final Charset charset ;
  private final Map< NodeKind, DelimiterPair > delimiters ;

  public PlainTextWriter( final Charset charset ) {
    this( charset, VOID_DELIMITERS) ;
  }

  public PlainTextWriter(
      final Charset charset,
      final Map< NodeKind, DelimiterPair > delimiterPairs
  ) {
    this.charset = charset ;
    this.delimiters = ImmutableMap.copyOf( delimiterPairs ) ;
  }

  private PrintWriter writer ;

  @Override
  public RenditionMimeType getMimeType() {
    return RenditionMimeType.TXT ;
  }

  @Override
  public void startWriting(
      final OutputStream outputStream,
      final DocumentMetadata documentMetadata
  ) throws Exception {
    final OutputStreamWriter outputStreamWriter = new OutputStreamWriter( outputStream, charset ) ;        
    writer = new PrintWriter( outputStreamWriter ) ;
  }

  @Override
  public void finishWriting() throws Exception {
    writer.flush() ;
  }

  @Override
  public void start( final Nodepath kinship, final boolean wholeDocument ) throws Exception {
    final DelimiterPair delimiterPair = getDelimiterPair( kinship ) ;
    if( delimiterPair != null ) {
      write( kinship, delimiterPair.left ) ;
    }
  }

  @Override
  public void end( final Nodepath kinship ) throws Exception {
    final DelimiterPair delimiterPair = getDelimiterPair( kinship ) ;
    if( delimiterPair != null ) {
      write( kinship, delimiterPair.right ) ;
    }
  }

  @Override
  public void write( final Nodepath path, final String word ) throws Exception {
    writer.append( word ) ;
  }

  @Override
  public void writeLiteral( final Nodepath kinship, final String word ) throws Exception {
    write( kinship, word ) ;
  }

  private DelimiterPair getDelimiterPair( final Nodepath kinship ) {
    return delimiters.get( kinship.getCurrent() ) ;
  }

  public static class DelimiterPair
  {

    public final String left ;
    public final String right ;

    private DelimiterPair( final String left, final String right ) {
      this.left = left ;
      this.right = right ;
    }
  }

  public static DelimiterPair pair( final String left, final String right ) {
    return new DelimiterPair( left, right ) ;
  }

  private static final Map< NodeKind, DelimiterPair > VOID_DELIMITERS = ImmutableMap.of() ;
}

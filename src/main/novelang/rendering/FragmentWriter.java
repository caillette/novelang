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
import java.nio.charset.Charset;

import com.google.common.base.Objects;
import novelang.model.common.NodeKind;

/**
 * @author Laurent Caillette
 */
public interface FragmentWriter {

  void startWriting( OutputStream outputStream, Charset encoding ) throws Exception ;
  void finishWriting() throws Exception ;

  void start( Path kinship, boolean wholeDocument ) throws Exception ;
  void end( Path kinship, boolean wholeDocument ) throws Exception ;
  void just( String word ) throws Exception ;

  RenditionMimeType getMimeType() ;


  /**
   * Represents the stack of Node Kinds preceding current tree.
   */
  class Path {

    final int depth ;
    final NodeKind current ;

    public Path() {
      depth = 0 ;
      current = null ;
    }

    public Path( NodeKind current ) {
      depth = 1 ;
      this.current = Objects.nonNull( current ) ;
    }

    public Path( Path previous, NodeKind current ) {
      depth = previous.getDepth() + 1 ;
      this.current = Objects.nonNull( current ) ;
    }    

    public int getDepth() {
      return depth ;
    }

    public NodeKind getCurrent() {
      return current;
    }

  }
}

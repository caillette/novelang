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

import java.io.OutputStream;

import org.novelang.common.Nodepath;
import org.novelang.common.metadata.DocumentMetadata;

/**
 * @author Laurent Caillette
 */
public interface FragmentWriter {

  void startWriting(
      OutputStream outputStream,
      DocumentMetadata documentMetadata
  ) throws Exception ;
  
  void finishWriting() throws Exception ;

  void start( Nodepath kinship, boolean wholeDocument ) throws Exception ;
  void end( Nodepath kinship ) throws Exception ;
  void write( Nodepath kinship, String word ) throws Exception ;

  /**
   * Same as {@link #write(org.novelang.common.Nodepath , String)} but without escaping.
   */
  void writeLiteral( Nodepath kinship, String word ) throws Exception ;

  RenditionMimeType getMimeType() ;


}

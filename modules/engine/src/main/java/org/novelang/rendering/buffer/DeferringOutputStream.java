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
package org.novelang.rendering.buffer;

import java.io.IOException;
import java.io.OutputStream;

import org.novelang.outfit.TemporaryFileService;

/**
 * An {@code OutputStream } that keeps all bytes in memory, or in a temporary files if going a
 * given amount.
 * <p>
 * This class is not thread-safe.
 *
 * @author Laurent Caillette
 */
public class DeferringOutputStream extends OutputStream {

  private boolean released = false ;
  private final int maximumHeapMemorySizeInBytes ;
  private final TemporaryFileService.FileSupplier fileSupplier ;

  /**
   * Constructor.
   *
   * @param fileSupplier a non-null object.
   * @param maximumHeapMemorySizeInBytes maximum amount of bytes to keep in heap memory.
   */
  public DeferringOutputStream(
      final TemporaryFileService.FileSupplier fileSupplier,
      final int maximumHeapMemorySizeInBytes
  ) {
    throw new UnsupportedOperationException( "TODO" ) ;
  }


  /**
   * Copies buffer's content to the given {@code OutputStream}.
   *
   * @param target a non-null object.
   * @throws IllegalStateException if already released.
   */
  public void copy( final OutputStream target ) throws IOException {
    throw new UnsupportedOperationException( "TODO" ) ;
  }

  /**
   * Releases underlying resources.
   *
   * @throws IllegalStateException if already released.
   */
  public void release() throws IOException {
    throw new UnsupportedOperationException( "TODO" ) ;
  }

// ============
// OutputStream
// ============

  @Override
  public void write( final int someByte ) throws IOException {
    throw new UnsupportedOperationException( "TODO" );
  }

  @Override
  public void write( final byte[] bytes, final int offest, final int length ) throws IOException {
    throw new UnsupportedOperationException( "TODO" ) ;
  }
}

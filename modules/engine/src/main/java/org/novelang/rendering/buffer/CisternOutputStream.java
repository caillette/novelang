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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.google.common.base.Preconditions;
import org.apache.commons.io.IOUtils;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.TemporaryFileService;

/**
 * An {@code OutputStream } that keeps all bytes in memory, or in a temporary files if going a
 * given amount.
 * <p>
 * This class is not thread-safe.
 *
 * @author Laurent Caillette
 */
public class CisternOutputStream extends OutputStream {

  private static final Logger LOGGER = LoggerFactory.getLogger( CisternOutputStream.class ) ;

  private byte[] bytes ;
  private int byteCount ;
  private final TemporaryFileService.FileSupplier fileSupplier ;
  private File file = null ;
  private FileOutputStream fileOutputStream = null;

  /**
   * Constructor.
   *
   * @param fileSupplier a non-null object.
   * @param maximumHeapMemorySizeInBytes maximum amount of bytes to keep in heap memory.
   */
  public CisternOutputStream(
      final TemporaryFileService.FileSupplier fileSupplier,
      final int maximumHeapMemorySizeInBytes
  ) {
    this.fileSupplier = Preconditions.checkNotNull( fileSupplier ) ;
    bytes = new byte[ maximumHeapMemorySizeInBytes ] ;
    byteCount = 0 ;
  }


  /**
   * Copies buffer's content to the given {@code OutputStream}.
   *
   * @param target a non-null object.
   * @throws IllegalStateException if already released.
   */
  public void copy( final OutputStream target ) throws IOException {
    checkState() ;
    if( file == null ) {
      target.write( bytes, 0, byteCount ) ;
    } else {
      fileOutputStream.flush() ;
      final FileInputStream fileInputStream = new FileInputStream( file ) ;
      try {
        // No need to buffer, says the documentation.
        IOUtils.copy( fileInputStream, target ) ;
      } finally {
        fileInputStream.close() ;
      }
    }
  }

  private void checkState() {
    if( bytes == null && file == null ) {
      throw new IllegalStateException( "Already released" ) ;
    }
  }

  /**
   * Returns true if adding given byte count overflows the heap memory buffer.
   */
  private boolean overflows( final int additionalByteCount ) {
    return byteCount + additionalByteCount > bytes.length ;
  }

  @SuppressWarnings( { "IOResourceOpenedButNotSafelyClosed" } )
  private void switchToFile() throws IOException {
    Preconditions.checkState( file == null ) ;
    Preconditions.checkState( fileOutputStream == null ) ;
    file = fileSupplier.get() ;
    fileOutputStream = new FileOutputStream( file ) ;
    fileOutputStream.write( bytes, 0, byteCount ) ;
    LOGGER.debug( "Wrote ", byteCount, " bytes into '", file.getAbsolutePath(), "'." ) ;
    bytes = null ;
  }


// ============
// OutputStream
// ============

  @Override
  public void write( final int someByte ) throws IOException {
    checkState() ;
    if( file == null ) {
      if( overflows( 1 ) ) {
        switchToFile() ;
      } else {
        bytes[ byteCount ++ ] = ( byte ) someByte ;
        return ;
      }
    }
    fileOutputStream.write( someByte ) ;
  }

  @Override
  public void write( final byte[] bytes, final int offset, final int length ) throws IOException {
    checkState() ;
    if( file == null ) {
      if( overflows( length ) ) {
        switchToFile() ;
      } else {
        for( int i = 0 ; i < length ; i ++ ) {
         this.bytes[ byteCount ++ ] = bytes[ offset + i ] ;
        }
        return ;
      }
    }
    fileOutputStream.write( bytes, offset, length ) ;
  }

  @Override
  public void flush() throws IOException {
    checkState() ;
    if( fileOutputStream != null ) {
      fileOutputStream.flush() ;
    }
  }

  public boolean isOpen() {
    return bytes != null || file != null ;
  }

  /**
   * Releases underlying resources.
   *
   * @throws IllegalStateException if already released.
   */
  @Override
  public void close() throws IOException {
    checkState() ;
    bytes = null ;
    byteCount = -1 ;
    if( fileOutputStream != null ) {
      fileOutputStream.close() ;
      fileOutputStream = null ;
    }
    if( file != null ) {
      file.delete() ;
      file = null ;
    }
  }

  
}

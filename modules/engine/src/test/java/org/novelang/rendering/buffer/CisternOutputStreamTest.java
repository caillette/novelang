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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import static org.fest.assertions.Assertions.assertThat;

import org.novelang.outfit.TemporaryFileService;
import org.novelang.testing.junit.MethodSupport;

/**
 * Tests for {@link CisternOutputStream}.
 *
 * @author Laurent Caillette
 */
public class CisternOutputStreamTest {

  @Test
  public void overflowHeapMemorySize2() throws IOException {
    final CisternOutputStream deferringOutputStream = createDeferredOutputStream( 1 );
    assertThat( fileSupplier.file ).doesNotExist() ;
    deferringOutputStream.write( 1 ) ;
    deferringOutputStream.write( 2 ) ;
    assertThat( fileSupplier.file ).exists() ;

    deferringOutputStream.flush() ;
    final ByteArrayOutputStream capture = new ByteArrayOutputStream() ;
    deferringOutputStream.copy( capture ) ;
    assertThat( capture.toByteArray() ).contains( new byte[]{ 1 , 2 } ) ;

    deferringOutputStream.close() ;
    assertThat( fileSupplier.file ).doesNotExist() ;
  }

  @Test
  public void overflowHeapMemorySize4() throws IOException {
    final CisternOutputStream deferringOutputStream = createDeferredOutputStream( 1 );
    assertThat( fileSupplier.file ).doesNotExist() ;
    deferringOutputStream.write( new byte[] { 1, 2, 3, 4 }, 0, 4 ) ;
    assertThat( fileSupplier.file ).exists() ;

    deferringOutputStream.flush() ;
    final ByteArrayOutputStream capture = new ByteArrayOutputStream() ;
    deferringOutputStream.copy( capture ) ;
    assertThat( capture.toByteArray() ).contains( new byte[]{ 1, 2, 3, 4 } ) ;

    deferringOutputStream.close() ;
    assertThat( fileSupplier.file ).doesNotExist() ;
  }

  @Test( expected = IllegalStateException.class )
  public void noAccessPastRelease() throws IOException {
    final CisternOutputStream deferringOutputStream = createDeferredOutputStream( 1 ) ;
    deferringOutputStream.write( 1 ) ;
    deferringOutputStream.close() ;
    deferringOutputStream.write( 1 ) ;
  }


// =======
// Fixture
// =======

  private CisternOutputStream createDeferredOutputStream( final int size ) {
    return new CisternOutputStream( fileSupplier, size ) ;
  }

  @Rule
  public final MethodSupport methodSupport = new MethodSupport() {
    @Override
    protected void beforeStatementEvaluation() throws Exception {
      fileSupplier = new OneShotFileSupplier() ;
    }
  } ;

  private class OneShotFileSupplier implements TemporaryFileService.FileSupplier {

    final File file = new File( methodSupport.getDirectory(), "temporary-file" ) ;

    @Override
    public File get() throws IOException {
      return file ;
    }
  }

  private OneShotFileSupplier fileSupplier = null ;
}

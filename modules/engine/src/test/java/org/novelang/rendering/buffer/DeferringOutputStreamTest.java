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

import org.fest.assertions.Assertions;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.novelang.outfit.TemporaryFileService;
import org.novelang.testing.junit.MethodSupport;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Tests for {@link org.novelang.rendering.buffer.DeferringOutputStream}.
 *
 * @author Laurent Caillette
 */
@Ignore( "Missing implementation")
public class DeferringOutputStreamTest {

  @Test
  public void overflowHeapMemorySize() throws IOException {
    final DeferringOutputStream deferringOutputStream = new DeferringOutputStream( 
        fileSupplier, 1 ) ;
    assertThat( fileSupplier.file ).doesNotExist() ;
    deferringOutputStream.write( 1 ) ;
    deferringOutputStream.write( 2 ) ;
    assertThat( fileSupplier.file ).exists() ;

    deferringOutputStream.flush() ;
    final ByteArrayOutputStream capture = new ByteArrayOutputStream() ;
    deferringOutputStream.copy( capture ) ;
    assertThat( capture.toByteArray() ).contains( new byte[]{ 1 , 2 } ) ;

    deferringOutputStream.release() ;
    assertThat( fileSupplier.file ).doesNotExist() ;
  }


// =======
// Fixture
// =======

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

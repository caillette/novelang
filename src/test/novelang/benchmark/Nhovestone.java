/*
 * Copyright (C) 2010 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package novelang.benchmark;

import novelang.novelist.Novelist;
import novelang.system.Log;
import novelang.system.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Laurent Caillette
 */
public class Nhovestone {

  private static final Log LOG = LogFactory.getLog( Nhovestone.class );

  private final URL documentRequestUrl ;
  private final File documentSourceDirectory ;

  public Nhovestone( final URL documentRequestUrl, final File documentSourceDirectory ) {
    this.documentRequestUrl = documentRequestUrl;
    this.documentSourceDirectory = documentSourceDirectory;
  }

  public void warmup( final int passCount ) throws IOException {
    final Novelist novelist = new Novelist(
        new File( documentSourceDirectory, getClass().getSimpleName() ).getAbsolutePath(),
        new Novelist.LevelGeneratorSupplierWithDefaults(),
        1
    ) ;
    novelist.write( 1 ) ;
    LOG.info( "Warming up, " + passCount + " iterations..." ) ;
    for( int pass = 1 ; pass <= passCount ; pass ++ ) {
      requestWholeDocument() ;
//      if( pass > 0 && pass % 10 == 0 ) {
        LOG.debug( "  Performed pass " + pass + ".");
//      }
    }
    LOG.info( "Warmup complete." ) ;
  }

  private static final int BUFFER_SIZE = 1024 * 1024 ;
  private byte[] buffer = new byte[ BUFFER_SIZE ] ;


  public long requestWholeDocument() throws IOException {
    final InputStream inputStream = documentRequestUrl.openStream() ;
    try {
      final long startTime = System.currentTimeMillis() ;
      for( int read = inputStream.read( buffer ) ; read > -1 ; ) { }
      final long endTime = System.currentTimeMillis() ;
      return endTime - startTime ;
    } finally {
      inputStream.close() ;
    }
  }

  public static void main( final String[] args ) throws IOException {
    new Nhovestone(
        new URL( "http://localhost:8080/scratch/novelist/Nhovestone-0001.html" ),
        new File( "scratch/novelist" ) 
    ).warmup( 1000 ) ;

    System.exit( 0 ) ;
  }

}

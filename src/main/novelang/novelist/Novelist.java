/*
 * Copyright (C) 2010 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package novelang.novelist;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Preconditions;
import novelang.system.Log;
import novelang.system.LogFactory;

/**
 * @author Laurent Caillette
 */
public class Novelist {

  private static final Log LOG = LogFactory.getLog( Novelist.class );

  private final File targetFile ;
  private final Generator< ? extends TextElement > generator ;

  public Novelist( final File targetFile, final Generator< ? extends TextElement > generator ) {
    this.targetFile = Preconditions.checkNotNull( targetFile ).getAbsoluteFile() ;
    this.generator = Preconditions.checkNotNull( generator ) ;    
  }

  public void write( final int iterationCount, final boolean append ) throws IOException {
    final OutputStream outputStream ;
    if( append ) {
      throw new UnsupportedOperationException( "Appending not supported yet" ) ;
    } else {
      if( targetFile.getParentFile().mkdirs() ) {
        LOG.info( "Created directory '" + targetFile.getParentFile() + "'" ) ;
      }
      if( targetFile.exists() ) {
        if( ! targetFile.delete() ) {
          throw new IOException( "Could not delete file: '" + targetFile.getAbsolutePath() + "'" ) ;
        }
        if( ! targetFile.createNewFile() ) {
          throw new IOException( "Could not create file: '" + targetFile.getAbsolutePath() + "'" ) ;
        }
      }
    }

    LOG.info( "Writing for " + iterationCount + " iterations..." ) ;

    outputStream = new FileOutputStream( targetFile, append ) ;
    try {
      for( int iteration = 1 ; iteration <= iterationCount ; iteration ++ ) {
        final String text = generator.generate().getLiteral() ;
        IOUtils.write( text, outputStream ) ;
      }
    } finally {
      outputStream.close() ;
    }

    LOG.info( "Writing done." ) ;
  }

  private static final int DEFAULT_ITERATION_COUNT = 20 ;

  public static void main( final String[] args ) throws IOException {
    if( args.length < 1 ) {
      System.out.println( Novelist.class.getName() + "<target-file> [iteration-count]" ) ;
      System.exit( 1 ) ;
    }

    final int iterationCount ;
    if( args.length < 2 ) {
      iterationCount = DEFAULT_ITERATION_COUNT ;
    } else {
      iterationCount = Integer.parseInt( args[ 1 ] ) ;
    }

    new Novelist(
        new File( args[ 0 ] ),
        new SimpleLevelGenerator( GenerationDefaults.FOR_LEVELS )
    ).write( iterationCount, false ) ;
  }
}

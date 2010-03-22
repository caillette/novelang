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
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import novelang.system.Log;
import novelang.system.LogFactory;

/**
 * @author Laurent Caillette
 */
public class Novelist {

  private static final Log LOG = LogFactory.getLog( Novelist.class );

  private final List< GhostWriter > ghostWriters ;

  public Novelist(
      final String fileNamePrototype,
      final GeneratorSupplier< Level > generatorSupplier,
      final int ghostWriterCount
  ) throws IOException {
    final List< GhostWriter > ghostWriterList = Lists.newArrayList() ;
    for( int i = 1 ; i <= ghostWriterCount ; i ++ ) {
      final File file = createFreshFile( fileNamePrototype, i ) ;
      ghostWriterList.add( new GhostWriter( file, generatorSupplier.get( i ) ) ) ;
    }
    ghostWriters = ImmutableList.copyOf( ghostWriterList ) ;
  }

  private static File createFreshFile( final String prototype, final int counter ) throws IOException {
    final File targetFile = new File( prototype + "-" + counter + ".nlp" ) ;
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
    return targetFile ;
  }


  public void write( final int iterationCount ) throws IOException {
    for( final GhostWriter ghostWriter : ghostWriters ) {
      ghostWriter.write( iterationCount ) ;
    }
    LOG.info( "Writing done." ) ;
  }

  private static class GhostWriter {
    private final String name ;
    private final File file ;
    private final Generator< ? extends TextElement > generator ;
    private static final Log WRITER_LOG = LogFactory.getLog( GhostWriter.class ) ;

    private GhostWriter(
        final File file,
        final Generator< ? extends TextElement > generator
    ) {
      this.name = FilenameUtils.getBaseName( file.getName() ) ;
      this.file = file ;
      this.generator = generator ;
    }

    public void write( final int iterationCount ) throws IOException {
      final OutputStream outputStream = new FileOutputStream( file, true ) ;
      try {
        for( int i = 0 ; i < iterationCount ; i ++ ) {
          final String text = generator.generate().getLiteral() ;
          IOUtils.write( text, outputStream ) ;
          WRITER_LOG.debug( "{" + name + "} wrote " + text.length() + " bytes." ) ;
        }
      } finally {
        outputStream.close() ;
      }
    }

  }

  private static final int DEFAULT_GHOSTWRITER_COUNT = 5 ;
  private static final int DEFAULT_ITERATION_COUNT = 3 ;

  public static void main( final String[] args ) throws IOException {
    if( args.length < 1 ) {
      System.out.println( Novelist.class.getName() +
          "<target-file-noprefix> [ [ghostwriter-count] [iteration-count] ]" ) ;
      System.exit( 1 ) ;
    }

    final int ghostWriterCount ;
    if( args.length < 2 ) {
      ghostWriterCount = DEFAULT_GHOSTWRITER_COUNT ;
    } else {
      ghostWriterCount = Integer.parseInt( args[ 1 ] ) ;
    }

    final int iterationCount ;
    if( args.length < 3 ) {
      iterationCount = DEFAULT_ITERATION_COUNT ;
    } else {
      iterationCount = Integer.parseInt( args[ 2 ] ) ;
    }

    final Supplier< Generator< ? extends TextElement > > generatorSupplier =
        new Supplier< Generator< ? extends TextElement > >() {
          public Generator< ? extends TextElement > get() {
            return new SimpleLevelGenerator( GenerationDefaults.FOR_LEVELS ) ;
          }
        }
    ;

    new Novelist(
        args[ 0 ],
        new LevelGeneratorSupplierWithDefaults(), 
        ghostWriterCount
    ).write( iterationCount ) ;
  }

  public interface GeneratorSupplier< T extends TextElement > {
    Generator< ? extends T > get( final int number ) ;
  }

  public static class LevelGeneratorSupplierWithDefaults implements GeneratorSupplier< Level > {
    public Generator< ? extends Level > get( final int number ) {
      final SimpleLevelGenerator.Configuration configuration =
          GenerationDefaults.FOR_LEVELS.withLevelCounterStart( number ) ;
      return new SimpleLevelGenerator( configuration ) ;
    }
  }
}

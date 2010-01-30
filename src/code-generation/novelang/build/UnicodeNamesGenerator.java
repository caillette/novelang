package novelang.build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.SystemUtils;

import novelang.system.Log;
import novelang.system.LogFactory;

/**
 * Generates a file containing every Unicode name.
 * <ul>
 *   <li>First 65536 * 4 bytes: the offsets of the names. 
 *   <li>8-bit characters for names, zero-terminated.
 * </ul>
 *
 * @author Laurent Caillette
 */
public class UnicodeNamesGenerator {

  private static final Log LOG = LogFactory.getLog( UnicodeNamesGenerator.class ) ;
  private final File targetFile ;
  private static final int CHARACTER_COUNT_FOR_LOGGING = 10000;

  public UnicodeNamesGenerator(
      final String packageName,
      final String namesFile,
      final File targetDirectory
  ) throws IOException {
    this.targetFile =
        JavaGenerator.resolveTargetFile( targetDirectory, packageName, namesFile ) ;
    if( targetFile.getParentFile().mkdirs() ) {
      LOG.info( "Created '" + targetDirectory.getAbsolutePath() + "'" ) ;
    }
  }

  public void generate() throws IOException {
    LOG.info( "About to generate into '" + targetFile.getAbsolutePath() + "'..." ) ;
    if( targetFile.exists() ) {
      if( targetFile.delete() ) {
        LOG.info( "Deleted '" + targetFile.getAbsolutePath() + "'" ) ;
      }
    }
    if( ! targetFile.createNewFile() ) {
      throw new IOException( "Could not create '" + targetFile.getAbsolutePath() + "'" ) ;
    }
    LOG.info( "Loading names..." ) ;
    final Map< Character, String > characters = new UnicodeNamesTextReader().loadNames() ;
    final OutputStream outputStream = new FileOutputStream( targetFile ) ;
    LOG.info( "Generating indexed file..." ) ;
    generate( new BufferedOutputStream( outputStream, 640 * 1024 ), characters ) ;
    outputStream.close() ;
  }

  /**
   *  Generates the offset table and the names.
   * 
   * @param outputStream not flushed.
   * @param characterNames a Map with characters having contiguous codes that start by 0.
   */
  public static void generate(
      final OutputStream outputStream,
      final Map< Character, String > characterNames
  ) throws IOException {
    final Set< Character > characters = characterNames.keySet() ;
    final List< Character > characterList = Lists.newArrayList( characters ) ;
    final Map< Integer, Integer > offsetsFromFirstName = Maps.newHashMap() ;
    Collections.sort( characterList, CHARACTER_COMPARATOR /* Needed? */ ) ;
    Preconditions.checkArgument( characterList.size() <= 256 * 256 ) ;

    // Find the offset of the name of each character.
    int writePositionFromFirstName = 0 ;
    int contiguousCharacterIndex = 0 ; // Need an int here because a short is signed.
    final int maximumCharacterIndex = characterList.get( characterList.size() - 1 ) ;

    for( ; contiguousCharacterIndex <= maximumCharacterIndex ; contiguousCharacterIndex ++ ) {
      final Character character = ( char ) contiguousCharacterIndex ;
      if( characterNames.containsKey( character ) ) {
        offsetsFromFirstName.put( contiguousCharacterIndex, writePositionFromFirstName ) ;
        writePositionFromFirstName +=
            characterNames.get( character ).getBytes( CHARSET ).length + // Real length.
            1 // Terminal zero.
        ;
      } else {
        offsetsFromFirstName.put( contiguousCharacterIndex, null ) ;
      }
      if( contiguousCharacterIndex % CHARACTER_COUNT_FOR_LOGGING == 0 ) {
        LOG.debug( "Calculated offset for character " + contiguousCharacterIndex ) ;
      }
    }

    // Write offsets
    final int offsetTableSize = contiguousCharacterIndex * 4 ;
    for( final Map.Entry< Integer, Integer > entry : offsetsFromFirstName.entrySet() ) {
      final byte[] bytes ;
      final Integer value = entry.getValue() ;
      if( value == null ) {
        bytes = ZERO_OFFSET ;
      } else {
        bytes = asBytes( offsetTableSize + value ) ;
      }
      outputStream.write( bytes ) ;
      if( entry.getKey() % CHARACTER_COUNT_FOR_LOGGING == 0 ) {
        LOG.debug( "Wrote offset entry for character " + entry.getKey() ) ;
      }
    }

    // Write names.
    for( int characterIndex = 0 ; characterIndex <= maximumCharacterIndex ; characterIndex ++ ) {
      final String characterName = characterNames.get( ( char ) characterIndex ) ;
      if( characterName != null ) {
        final byte[] bytes = characterName.getBytes( CHARSET ) ;
        outputStream.write( bytes ) ;
        outputStream.write( TERMINAL_ZERO ) ;
      }
      if( characterIndex % CHARACTER_COUNT_FOR_LOGGING == 0 ) {
        LOG.debug( "Wrote name entry for character " + characterIndex ) ;
      }
    }
    outputStream.flush() ;
    LOG.debug( "Generation complete." ) ;

  }

  private static final Charset CHARSET = Charset.forName( "UTF-8" ) ;
  private static final byte[] TERMINAL_ZERO = new byte[] { 0 } ;
  private static final byte[] ZERO_OFFSET = new byte[] { 0, 0, 0, 0 } ;

  private static final Comparator< Character > CHARACTER_COMPARATOR =
      new Comparator< Character >() {
        public int compare( final Character c1, final Character c2 ) {
          return ( ( int ) c1.charValue() ) - ( ( int ) c2.charValue() ) ;
        }
      }
  ;


  /*package*/ static byte[] asBytes( final int i ) {
    final byte[] bytes = new byte[ 4 ] ;
    bytes[ 0 ] = ( byte ) ( i >>> 24 ) ; 
    bytes[ 1 ] = ( byte ) ( i >>> 16 ) ;
    bytes[ 2 ] = ( byte ) ( i >>> 8 ) ;
    bytes[ 3 ] = ( byte ) ( i & 0x000000FF ) ;
    return bytes ;
  }


// ==============================
// Main, for interactive testing.
// ==============================


  public static void main( final String[] args ) throws IOException {
    final File projectDirectory =
        SystemUtils.USER_DIR.endsWith( "idea" )?
        new File( SystemUtils.USER_DIR ).getParentFile() :
        new File( SystemUtils.USER_DIR )
    ;
    final File targetDirectory = new File( projectDirectory, "src/main" ) ;

    new File( SystemUtils.USER_DIR );
    new UnicodeNamesGenerator( "novelang.parser.unicode", "names.bin", targetDirectory ).
        generate() ;

  }
}

package org.novelang.build.unicode;

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
import org.novelang.build.CodeGenerationTools;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates a file containing every Unicode name.
 * <ul>
 *   <li>First 4 bytes: n, the number of character in the offset table.
 *   <li>n * 4 bytes: the offsets of the names (from the start of the file).
 *       Offsets are 32-bit, unsigned ints.
 *   <li>8-bit characters for names, zero-terminated.
 * </ul>
 *
 * @author Laurent Caillette
 */
public class UnicodeNamesGenerator {

  private static final Logger LOGGER = LoggerFactory.getLogger( UnicodeNamesGenerator.class ) ;

  private final File targetFile ;
  private static final int UNSIGNED_MAX_16BIT = 256 * 256;

  public UnicodeNamesGenerator(
      final String packageName,
      final String namesFile,
      final File targetDirectory
  ) throws IOException {
    this.targetFile =
        CodeGenerationTools.resolveTargetFile( targetDirectory, packageName, namesFile ) ;
    if( targetFile.getParentFile().mkdirs() ) {
      LOGGER.info( "Created '" + targetDirectory.getAbsolutePath() + "'" ) ;
    }
  }

  public void generate() throws IOException {
    LOGGER.info( "About to generate into '" + targetFile.getAbsolutePath() + "'..." ) ;
    if( targetFile.exists() ) {
      if( targetFile.delete() ) {
        LOGGER.info( "Deleted '" + targetFile.getAbsolutePath() + "'" ) ;
      }
    }
    if( ! targetFile.createNewFile() ) {
      throw new IOException( "Could not create '" + targetFile.getAbsolutePath() + "'" ) ;
    }
    LOGGER.info( "Loading names..." ) ;
    final Map< Character, String > characters = new UnicodeNamesTextReader().loadNames() ;
    final OutputStream outputStream = new FileOutputStream( targetFile ) ;
    LOGGER.info( "Generating indexed file..." ) ;
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

    Collections.sort( characterList, CHARACTER_COMPARATOR /* Needed? */ ) ;
    final int lastCharacterIndex = characterList.get( characterList.size() - 1 ) ;

    generate( outputStream, characterNames, lastCharacterIndex + 1 ) ;
  }


  /**
   *  Generates the offset table and the names.
   *
   * @param outputStream not flushed.
   * @param characterNames a Map with characters having contiguous codes that start by 0.
   */
  public static void generate(
      final OutputStream outputStream,
      final Map< Character, String > characterNames,
      final int totalCharacterCount
  ) throws IOException {
    Preconditions.checkArgument( totalCharacterCount <= UNSIGNED_MAX_16BIT ) ;
    final Map< Integer, Integer > offsetsFromFirstName =
        Maps.newHashMapWithExpectedSize( totalCharacterCount ) ;
    final Map< Character, byte[] > characterNamesAsBytes =
        calculateCharacterNamesAsBytes( characterNames ) ;

    // Find the offset of the name of each character.
    int writePositionFromFirstName = 0 ;
    int characterCount = 0 ;

    for( int characterIndex = 0 ; characterIndex < totalCharacterCount ; characterIndex ++ ) {
      final Character character = ( char ) characterIndex ;
      if( characterNames.containsKey( character ) ) {
        offsetsFromFirstName.put( characterIndex, writePositionFromFirstName ) ;
        writePositionFromFirstName +=
            characterNamesAsBytes.get( character ).length + // Real length.
            1 // Terminal zero.
        ;
        characterCount ++ ;
      } else {
        offsetsFromFirstName.put( characterIndex, null ) ;
      }
    }
    LOGGER.debug( "Found " + characterCount + " characters." ) ;

    // Write character count.
    outputStream.write( asBytes( totalCharacterCount ) ) ;

    // Write offsets.
    final int offsetTableSize = totalCharacterCount * 4 ;
    for( int characterIndex = 0 ; characterIndex < totalCharacterCount ; characterIndex ++ ) {
      final byte[] bytes ;
      final Integer value = offsetsFromFirstName.get( characterIndex ) ;
      if( value == null ) {
        bytes = ZERO_OFFSET ;
      } else {
        bytes = asBytes( 4 + offsetTableSize + value ) ;
      }
      outputStream.write( bytes ) ;
    }

    // Write names.
    for( int characterIndex = 0 ; characterIndex < totalCharacterCount ; characterIndex ++ ) {
      final byte[] nameBytes = characterNamesAsBytes.get( ( char ) characterIndex ) ;
      if( nameBytes != null ) {
        outputStream.write( nameBytes ) ;
        outputStream.write( TERMINAL_ZERO ) ;
      }
    }
    outputStream.flush() ;
    LOGGER.debug( "Generation complete." ) ;

  }


  /**
   * Getting bytes only once speeds generation up a lot.
   */
  private static Map< Character, byte[] > calculateCharacterNamesAsBytes(
      final Map<Character, String> characterNames
  ) {
    final Map< Character, byte[] > map = Maps.newHashMapWithExpectedSize( characterNames.size() ) ;
    for( final Map.Entry< Character, String > entry : characterNames.entrySet() ) {
      map.put( entry.getKey(), entry.getValue().replace( ' ', '_' ).getBytes( CHARSET ) ) ;
    }
    return map ;
  }

  private static final Charset CHARSET = Charset.forName( "UTF-8" ) ;
  private static final byte[] TERMINAL_ZERO = { 0 } ;
  private static final byte[] ZERO_OFFSET = { 0, 0, 0, 0 } ;

  private static final Comparator< Character > CHARACTER_COMPARATOR =
      new Comparator< Character >() {
        @Override
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


// ==============================================
// Main, supports no arg for interactive testing.
// ==============================================


  public static void main( final String[] args ) throws IOException {
    final File targetDirectory ;
    if( args.length == 0 ) {
      final File projectDirectory = SystemUtils.USER_DIR.endsWith( "idea" ) ?
          new File( SystemUtils.USER_DIR ).getParentFile() :
          new File( SystemUtils.USER_DIR )
      ;
      targetDirectory = new File( projectDirectory, "idea/generated/antlr" ) ;
    } else if( args.length == 1 ) {
      targetDirectory = new File( args[ 0 ] ) ;
    } else {
      throw new IllegalArgumentException(
          "Usage: " + ClassUtils.getShortClassName( UnicodeNamesGenerator.class ) +
          "[target-directory]"
      ) ;
    }
    new UnicodeNamesGenerator( "org.novelang.parser.unicode", "names.bin", targetDirectory ).
        generate() ;

  }
}

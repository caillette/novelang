package novelang.build;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import novelang.system.Log;
import novelang.system.LogFactory;

/**
 * Tests for {@link novelang.build.UnicodeNamesGenerator}.
 *
 * @author Laurent Caillette
 */
public class UnicodeNamesGeneratorTest {

  @Test
  public void convertIntegerToBytes() {

    for( final Map.Entry< Integer, byte[] > entry : INTEGER_TO_BYTES.entrySet() ) {
      final byte[] expectedBytes = entry.getValue();
      final String message =
          "Int: " + String.format( "%6d", entry.getKey() ) +
          " bytes: [ " + asHex( expectedBytes ) + " ]"
      ;

      final byte[] actualBytes = UnicodeNamesGenerator.asBytes( entry.getKey() ) ;
      assertEquals( message, expectedBytes.length, actualBytes.length ) ;

      for( int i = 0 ; i < expectedBytes.length ; i ++ ) {
        assertEquals(
            message,
            expectedBytes[ i ],
            actualBytes[ i ]
        ) ;
      }
      LOG.info( message ) ;

    }

  }

  @Test
  public void twoContiguousValues() throws IOException {

    final Map< Character, String > map = new ImmutableMap.Builder< Character, String >()
        .put( '\u0000', "Zero" ) // 90 101 114 11 0 (total length 5)
        .put( '\u0001', "One" )  // 79 110 101    0 (total length 4)
        .build()
    ;
    final ByteArrayOutputStream bytes = new ByteArrayOutputStream() ;
    UnicodeNamesGenerator.generate( bytes, map, 2 ) ;
    final byte[] actualBytes = bytes.toByteArray() ;

    final byte[] expectedBytes = new byte[] {
        0, 0, 0,  2,          // Size of the offset table.
        0, 0, 0, 12,          // Offset to "Zero".
        0, 0, 0, 17,          // Offset to "One".
        90, 101, 114, 111, 0, // Z e r o \0
        79, 110, 101, 0       // O n e \0
    } ;
    final String message =
        "Expected: \n[ " + asHex( expectedBytes ) + " ] " +
        "but got \n[ " + asHex( actualBytes ) + " ]\n" ;
    assertEquals( message, expectedBytes.length, actualBytes.length ) ;
    for( int i = 0 ; i < expectedBytes.length ; i ++ ) {
      assertEquals(
          message,
          expectedBytes[ i ],
          actualBytes[ i ]
      ) ;
    }    

  }

  @Test
  public void twoValuesWithAHoleInTheMiddle() throws IOException {

    final Map< Character, String > map = new ImmutableMap.Builder< Character, String >()
        .put( '\u0000', "Zero" ) // 90 101 114 111 0 (total length 5)
        // Discontinuity, no \u0001 value here.
        .put( '\u0002', "Two" )  // 84 119 111     0 (total length 3)
        .build()
    ;
    final ByteArrayOutputStream bytes = new ByteArrayOutputStream() ;
    UnicodeNamesGenerator.generate( bytes, map, 4 ) ;
    final byte[] actualBytes = bytes.toByteArray() ;

    final byte[] expectedBytes = new byte[] {
        0, 0, 0,  4,          // 3 characters.
        0, 0, 0, 20,          // Offset to "Zero".
        0, 0, 0,  0,          // Offset for some undefined character.
        0, 0, 0, 25,          // Offset to "One".
        0, 0, 0,  0,          // Offset for some undefined character.
        90, 101, 114, 111, 0, // Z e r o \0
        84, 119, 111, 0       // T w o \0
    } ;
    final String message =
        "Expected: \n[ " + asHex( expectedBytes ) + " ] " +
        "but got \n[ " + asHex( actualBytes ) + " ]\n" ;
    assertEquals( message, expectedBytes.length, actualBytes.length ) ;
    for( int i = 0 ; i < expectedBytes.length ; i ++ ) {
      assertEquals(
          message,
          expectedBytes[ i ],
          actualBytes[ i ]
      ) ;
    }

  }



// =======
// Fixture
// =======

  private static final Log LOG = LogFactory.getLog( UnicodeNamesGeneratorTest.class ) ;

  private static final BiMap< Integer, byte[] > INTEGER_TO_BYTES =
      new ImmutableBiMap.Builder< Integer, byte[] >()
      .put(     0, new byte[] { 0, 0, 0,    0 } )
      .put(     1, new byte[] { 0, 0, 0,    1 } )
      .put(    10, new byte[] { 0, 0, 0,   10 } )
      .put(   255, new byte[] { 0, 0, 0,   -1 } )
      .put( 65535, new byte[] { 0, 0, -1,  -1 } )
      .build()
  ;

  private static String asHex( final byte[] bytes ) {
    final StringBuilder builder = new StringBuilder() ;
    for( final byte b : bytes ) {

      final ByteBuffer bb = ByteBuffer.allocate(4);
      bb.put( new byte[] { 0, 0, 0, b } ) ;
      bb.rewind() ;
      final int unsignedByteVal = bb.getInt() ;
      builder.append( " " ) ;
      final String hexString = Integer.toHexString( unsignedByteVal ) ;
      final String formattedHexString = hexString.length() > 1 ? hexString : "0" + hexString ;
      builder.append( formattedHexString ) ;
    }
    return builder.length() > 0 ? builder.toString().substring( 1 ) : "" ;
  }

}

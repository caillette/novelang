package org.novelang.parser.unicode;

import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Tets for {@link UnicodeNamesBinaryReader}.
 * TODO: refactor and share reference arrays with {@link org.novelang.build.UnicodeNamesGeneratorTest}.
 *
 * @author Laurent Caillette
 */
public class UnicodeNamesBinaryReaderTest {

  @Test
  public void readingInContiguousTable() throws IOException, CharacterOutOfBoundsException {
    verify( BYTES_ZERO_ONE, '\u0000', "Zero" ) ;
    verify( BYTES_ZERO_ONE, '\u0001', "One" ) ;
  }

  @Test
  public void readingCharacterInATableWithNulls() throws IOException, CharacterOutOfBoundsException {
    verify( BYTES_ZERO_BLANK_TWO, '\u0000', "Zero" ) ;
    verify( BYTES_ZERO_BLANK_TWO, '\u0001', null ) ;
    verify( BYTES_ZERO_BLANK_TWO, '\u0002', "Two" ) ;
  }

  @Test
  public void readingOf65thCharacter() throws IOException, CharacterOutOfBoundsException {
    verify( BYTES_SIXTEENBLANKS_SIXTEEN, '\u0040', "Sixty-four" ) ;
  }

  @Test( expected = CharacterOutOfBoundsException.class )
  public void dontSeekForCharacterPastOffsetTable() throws IOException, CharacterOutOfBoundsException {
    verify( BYTES_ZERO_ONE, '\u0002', "" ) ;
  }

  @Test
  public void longFromByteArray() {
    final byte[] bytes = { 0, 4, 6, -128 } ;
    assertEquals( 263808, UnicodeNamesBinaryReader.asLong( bytes ) ) ;
  }


  @Test
  public void unsignedBytesToLong() {
    final byte negativeByte = -128 ;
    final long unsignedByte = ( negativeByte ) & 0x000000FF ; 
    assertEquals( 128, unsignedByte ) ;
  }


// =======
// Fixture
// =======

  private static void verify(
      final byte[] binary,
      final char character,
      final String expectedName
  ) throws IOException, CharacterOutOfBoundsException {
    final InputStream inputStream = new ByteArrayInputStream( binary ) ;
    final String actualName = UnicodeNamesBinaryReader.readName( inputStream, character ) ;
    assertEquals( expectedName, actualName ) ;
  }

  private static final byte[] BYTES_ZERO_ONE = {
      0, 0, 0,  2,          // 2 characters at all.
      0, 0, 0, 12,
      0, 0, 0, 17,
      90, 101, 114, 111, 0, // Z e r o \0
      79, 110, 101, 0       // O n e \0
  } ;

  private static final byte[] BYTES_ZERO_BLANK_TWO = {
      0, 0, 0,  4,          // 4 characters at all.
      0, 0, 0, 20,
      0, 0, 0,  0,
      0, 0, 0, 25,
      0, 0, 0,  0,
      90, 101, 114, 111, 0, // Z e r o \0
      84, 119, 111, 0       // T w o \0
  } ;

  private static final byte[] BYTES_SIXTEENBLANKS_SIXTEEN = {
      0, 0, 0, 65,  // 65 characters at all.
      0, 0, 0, 0,  // #0
      0, 0, 0, 0,  // #1
      0, 0, 0, 0,  // #2
      0, 0, 0, 0,  // #3
      0, 0, 0, 0,  // #4
      0, 0, 0, 0,  // #5
      0, 0, 0, 0,  // #6
      0, 0, 0, 0,  // #7
      0, 0, 0, 0,  // #8
      0, 0, 0, 0,  // #9
      0, 0, 0, 0,  // #10
      0, 0, 0, 0,  // #11
      0, 0, 0, 0,  // #12
      0, 0, 0, 0,  // #13
      0, 0, 0, 0,  // #14
      0, 0, 0, 0,  // #15
      0, 0, 0, 0,  // #16
      0, 0, 0, 0,  // #17
      0, 0, 0, 0,  // #18
      0, 0, 0, 0,  // #19
      0, 0, 0, 0,  // #20
      0, 0, 0, 0,  // #21
      0, 0, 0, 0,  // #22
      0, 0, 0, 0,  // #23
      0, 0, 0, 0,  // #24
      0, 0, 0, 0,  // #25
      0, 0, 0, 0,  // #26
      0, 0, 0, 0,  // #27
      0, 0, 0, 0,  // #28
      0, 0, 0, 0,  // #29
      0, 0, 0, 0,  // #30
      0, 0, 0, 0,  // #31
      0, 0, 0, 0,  // #32
      0, 0, 0, 0,  // #33
      0, 0, 0, 0,  // #34
      0, 0, 0, 0,  // #35
      0, 0, 0, 0,  // #36
      0, 0, 0, 0,  // #37
      0, 0, 0, 0,  // #38
      0, 0, 0, 0,  // #39
      0, 0, 0, 0,  // #40
      0, 0, 0, 0,  // #41
      0, 0, 0, 0,  // #42
      0, 0, 0, 0,  // #43
      0, 0, 0, 0,  // #44
      0, 0, 0, 0,  // #45
      0, 0, 0, 0,  // #46
      0, 0, 0, 0,  // #47
      0, 0, 0, 0,  // #48
      0, 0, 0, 0,  // #49
      0, 0, 0, 0,  // #50
      0, 0, 0, 0,  // #51
      0, 0, 0, 0,  // #52
      0, 0, 0, 0,  // #53
      0, 0, 0, 0,  // #54
      0, 0, 0, 0,  // #55
      0, 0, 0, 0,  // #56
      0, 0, 0, 0,  // #57
      0, 0, 0, 0,  // #58
      0, 0, 0, 0,  // #59
      0, 0, 0, 0,  // #60
      0, 0, 0, 0,  // #61
      0, 0, 0, 0,  // #62
      0, 0, 0, 0,  // #63
      0, 0, 1, 8,  // #64
      83, 105, 120, 116, 121, 45, 102, 111, 117, 114, 0 // S i x t y - f o u r \0
  } ;


}
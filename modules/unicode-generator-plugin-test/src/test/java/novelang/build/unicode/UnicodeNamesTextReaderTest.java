package novelang.build.unicode;

import java.io.IOException;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import static org.junit.Assert.assertEquals;

import novelang.build.unicode.UnicodeNamesTextReader;
import org.junit.Test;

/**
 * Tests for {@link UnicodeNamesTextReader}.
 *
 * @author Laurent Caillette
 */
public class UnicodeNamesTextReaderTest {

  @Test
  public void first16Characters() throws IOException {
    final Map< Character, String > characterNames =
        new UnicodeNamesTextReader().extractNames( FIRST_16_AS_STRING ) ;
    verify( FIRST_16_AS_MAP, characterNames ) ;

  }

// =======
// Fixture
// =======

  private static void verify(
      final Map< Character, String > expected,
      final Map< Character, String > actual
  ) {
    assertEquals( expected.size(), actual.size() ) ;
    for( final Map.Entry< Character, String > entry : expected.entrySet() ) {
      final String expectedValue = entry.getValue();
      final String actualValue = actual.get( entry.getKey() );
      assertEquals( expectedValue, actualValue ) ;
    }
  }

  private static final String FIRST_16_AS_STRING =
      "0000;<control>;Cc;0;BN;;;;;N;NULL;;;;\n" +
      "0001;<control>;Cc;0;BN;;;;;N;START OF HEADING;;;;\n" +
      "0002;<control>;Cc;0;BN;;;;;N;START OF TEXT;;;;\n" +
      "0003;<control>;Cc;0;BN;;;;;N;END OF TEXT;;;;\n" +
      "0004;<control>;Cc;0;BN;;;;;N;END OF TRANSMISSION;;;;\n" +
      "0005;<control>;Cc;0;BN;;;;;N;ENQUIRY;;;;\n" +
      "0006;<control>;Cc;0;BN;;;;;N;ACKNOWLEDGE;;;;\n" +
      "0007;<control>;Cc;0;BN;;;;;N;BELL;;;;\n" +
      "0008;<control>;Cc;0;BN;;;;;N;BACKSPACE;;;;\n" +
      "0009;<control>;Cc;0;S;;;;;N;CHARACTER TABULATION;;;;\n" +
      "000A;<control>;Cc;0;B;;;;;N;LINE FEED (LF);;;;\n" +
      "000B;<control>;Cc;0;S;;;;;N;LINE TABULATION;;;;\n" +
      "000C;<control>;Cc;0;WS;;;;;N;FORM FEED (FF);;;;\n" +
      "000D;<control>;Cc;0;B;;;;;N;CARRIAGE RETURN (CR);;;;\n" +
      "000E;<control>;Cc;0;BN;;;;;N;SHIFT OUT;;;;\n" +
      "000F;<control>;Cc;0;BN;;;;;N;SHIFT IN;;;;\n" +

      "0010;<control>;Cc;0;BN;;;;;N;DATA LINK ESCAPE;;;;\n" +
      "0011;<control>;Cc;0;BN;;;;;N;DEVICE CONTROL ONE;;;;\n" +
      "0012;<control>;Cc;0;BN;;;;;N;DEVICE CONTROL TWO;;;;\n" +
      "0013;<control>;Cc;0;BN;;;;;N;DEVICE CONTROL THREE;;;;\n" +
      "0014;<control>;Cc;0;BN;;;;;N;DEVICE CONTROL FOUR;;;;\n" +
      "0015;<control>;Cc;0;BN;;;;;N;NEGATIVE ACKNOWLEDGE;;;;\n" +
      "0016;<control>;Cc;0;BN;;;;;N;SYNCHRONOUS IDLE;;;;\n" +
      "0017;<control>;Cc;0;BN;;;;;N;END OF TRANSMISSION BLOCK;;;;\n" +
      "0018;<control>;Cc;0;BN;;;;;N;CANCEL;;;;\n" +
      "0019;<control>;Cc;0;BN;;;;;N;END OF MEDIUM;;;;\n" +
      "001A;<control>;Cc;0;BN;;;;;N;SUBSTITUTE;;;;\n" +
      "001B;<control>;Cc;0;BN;;;;;N;ESCAPE;;;;\n" +
      "001C;<control>;Cc;0;B;;;;;N;INFORMATION SEPARATOR FOUR;;;;\n" +
      "001D;<control>;Cc;0;B;;;;;N;INFORMATION SEPARATOR THREE;;;;\n" +
      "001E;<control>;Cc;0;B;;;;;N;INFORMATION SEPARATOR TWO;;;;\n" +
      "001F;<control>;Cc;0;S;;;;;N;INFORMATION SEPARATOR ONE;;;;\n" +
      "0020;SPACE;Zs;0;WS;;;;;N;;;;;\n" +
      "0021;EXCLAMATION MARK;Po;0;ON;;;;;N;;;;;\n" +
      "0022;QUOTATION MARK;Po;0;ON;;;;;N;;;;;\n" +
      "0023;NUMBER SIGN;Po;0;ET;;;;;N;;;;;\n" +
      "0024;DOLLAR SIGN;Sc;0;ET;;;;;N;;;;;\n" +
      "0025;PERCENT SIGN;Po;0;ET;;;;;N;;;;;\n" +
      "0026;AMPERSAND;Po;0;ON;;;;;N;;;;;\n" +
      "0027;APOSTROPHE;Po;0;ON;;;;;N;APOSTROPHE-QUOTE;;;;\n" +
      "0028;LEFT PARENTHESIS;Ps;0;ON;;;;;Y;OPENING PARENTHESIS;;;;\n" +
      "0029;RIGHT PARENTHESIS;Pe;0;ON;;;;;Y;CLOSING PARENTHESIS;;;;\n" +
      "002A;ASTERISK;Po;0;ON;;;;;N;;;;;\n" +
      "002B;PLUS SIGN;Sm;0;ES;;;;;N;;;;;\n" +
      "002C;COMMA;Po;0;CS;;;;;N;;;;;\n" +
      "002D;HYPHEN-MINUS;Pd;0;ES;;;;;N;;;;;\n" +
      "002E;FULL STOP;Po;0;CS;;;;;N;PERIOD;;;;\n" +
      "002F;SOLIDUS;Po;0;CS;;;;;N;SLASH;;;;\n" +
      "0030;DIGIT ZERO;Nd;0;EN;;0;0;0;N;;;;;\n" +
      "0031;DIGIT ONE;Nd;0;EN;;1;1;1;N;;;;;\n" +
      "0032;DIGIT TWO;Nd;0;EN;;2;2;2;N;;;;;\n" +
      "0033;DIGIT THREE;Nd;0;EN;;3;3;3;N;;;;;\n" +
      "0034;DIGIT FOUR;Nd;0;EN;;4;4;4;N;;;;;\n" +
      "0035;DIGIT FIVE;Nd;0;EN;;5;5;5;N;;;;;\n" +
      "0036;DIGIT SIX;Nd;0;EN;;6;6;6;N;;;;;\n" +
      "0037;DIGIT SEVEN;Nd;0;EN;;7;7;7;N;;;;;\n" +
      "0038;DIGIT EIGHT;Nd;0;EN;;8;8;8;N;;;;;\n" +
      "0039;DIGIT NINE;Nd;0;EN;;9;9;9;N;;;;;\n" +
      "003A;COLON;Po;0;CS;;;;;N;;;;;\n" +
      "003B;SEMICOLON;Po;0;ON;;;;;N;;;;;\n" +
      "003C;LESS-THAN SIGN;Sm;0;ON;;;;;Y;;;;;\n" +
      "003D;EQUALS SIGN;Sm;0;ON;;;;;N;;;;;\n" +
      "003E;GREATER-THAN SIGN;Sm;0;ON;;;;;Y;;;;;\n" +
      "003F;QUESTION MARK;Po;0;ON;;;;;N;;;;;\n" +
      "0040;COMMERCIAL AT;Po;0;ON;;;;;N;;;;;\n" +
      "0041;LATIN CAPITAL LETTER A;Lu;0;L;;;;;N;;;;0061;\n" +
      "0042;LATIN CAPITAL LETTER B;Lu;0;L;;;;;N;;;;0062;\n" +
      "0043;LATIN CAPITAL LETTER C;Lu;0;L;;;;;N;;;;0063;\n" +
      "0044;LATIN CAPITAL LETTER D;Lu;0;L;;;;;N;;;;0064;\n" +
      "0045;LATIN CAPITAL LETTER E;Lu;0;L;;;;;N;;;;0065;\n" +
      "0046;LATIN CAPITAL LETTER F;Lu;0;L;;;;;N;;;;0066;\n" +
      "0047;LATIN CAPITAL LETTER G;Lu;0;L;;;;;N;;;;0067;"
  ;

  private static final Map< Character, String > FIRST_16_AS_MAP =
      new ImmutableMap.Builder< Character, String >()
      .put( ( char ) 0x0, "NULL" )
      .put( ( char ) 0x1, "START OF HEADING" )
      .put( ( char ) 0x2, "START OF TEXT" )
      .put( ( char ) 0x3, "END OF TEXT" )
      .put( ( char ) 0x4, "END OF TRANSMISSION" )
      .put( ( char ) 0x5, "ENQUIRY" )
      .put( ( char ) 0x6, "ACKNOWLEDGE" )
      .put( ( char ) 0x7, "BELL" )
      .put( ( char ) 0x8, "BACKSPACE" )
      .put( ( char ) 0x9, "CHARACTER TABULATION" )
      .put( ( char ) 0x0a, "LINE FEED (LF)" )
      .put( ( char ) 0x0b, "LINE TABULATION" )
      .put( ( char ) 0x0c, "FORM FEED (FF)" )
      .put( ( char ) 0x0d, "CARRIAGE RETURN (CR)" )
      .put( ( char ) 0x0e, "SHIFT OUT" )
      .put( ( char ) 0x0f, "SHIFT IN" )

      .put( ( char ) 0x10, "DATA LINK ESCAPE" )
      .put( ( char ) 0x11, "DEVICE CONTROL ONE" )
      .put( ( char ) 0x12, "DEVICE CONTROL TWO" )
      .put( ( char ) 0x13, "DEVICE CONTROL THREE" )
      .put( ( char ) 0x14, "DEVICE CONTROL FOUR" )
      .put( ( char ) 0x15, "NEGATIVE ACKNOWLEDGE" )
      .put( ( char ) 0x16, "SYNCHRONOUS IDLE" )
      .put( ( char ) 0x17, "END OF TRANSMISSION BLOCK" )
      .put( ( char ) 0x18, "CANCEL" )
      .put( ( char ) 0x19, "END OF MEDIUM" )
      .put( ( char ) 0x1A, "SUBSTITUTE" )
      .put( ( char ) 0x1B, "ESCAPE" )
      .put( ( char ) 0x1C, "INFORMATION SEPARATOR FOUR" )
      .put( ( char ) 0x1D, "INFORMATION SEPARATOR THREE" )
      .put( ( char ) 0x1E, "INFORMATION SEPARATOR TWO" )
      .put( ( char ) 0x1F, "INFORMATION SEPARATOR ONE" )
      .put( ( char ) 0x20, "SPACE" )
      .put( ( char ) 0x21, "EXCLAMATION MARK" )
      .put( ( char ) 0x22, "QUOTATION MARK" )
      .put( ( char ) 0x23, "NUMBER SIGN" )
      .put( ( char ) 0x24, "DOLLAR SIGN" )
      .put( ( char ) 0x25, "PERCENT SIGN" )
      .put( ( char ) 0x26, "AMPERSAND" )
      .put( ( char ) 0x27, "APOSTROPHE" )
      .put( ( char ) 0x28, "LEFT PARENTHESIS" )
      .put( ( char ) 0x29, "RIGHT PARENTHESIS" )
      .put( ( char ) 0x2A, "ASTERISK" )
      .put( ( char ) 0x2B, "PLUS SIGN" )
      .put( ( char ) 0x2C, "COMMA" )
      .put( ( char ) 0x2D, "HYPHEN-MINUS" )
      .put( ( char ) 0x2E, "FULL STOP" )
      .put( ( char ) 0x2F, "SOLIDUS" )
      .put( ( char ) 0x30, "DIGIT ZERO" )
      .put( ( char ) 0x31, "DIGIT ONE" )
      .put( ( char ) 0x32, "DIGIT TWO" )
      .put( ( char ) 0x33, "DIGIT THREE" )
      .put( ( char ) 0x34, "DIGIT FOUR" )
      .put( ( char ) 0x35, "DIGIT FIVE" )
      .put( ( char ) 0x36, "DIGIT SIX" )
      .put( ( char ) 0x37, "DIGIT SEVEN" )
      .put( ( char ) 0x38, "DIGIT EIGHT" )
      .put( ( char ) 0x39, "DIGIT NINE" )
      .put( ( char ) 0x3A, "COLON" )
      .put( ( char ) 0x3B, "SEMICOLON" )
      .put( ( char ) 0x3C, "LESS-THAN SIGN" )
      .put( ( char ) 0x3D, "EQUALS SIGN" )
      .put( ( char ) 0x3E, "GREATER-THAN SIGN" )
      .put( ( char ) 0x3F, "QUESTION MARK" )
      .put( ( char ) 0x40, "COMMERCIAL AT" )
      .put( ( char ) 0x41, "LATIN CAPITAL LETTER A" )
      .put( ( char ) 0x42, "LATIN CAPITAL LETTER B" )
      .put( ( char ) 0x43, "LATIN CAPITAL LETTER C" )
      .put( ( char ) 0x44, "LATIN CAPITAL LETTER D" )
      .put( ( char ) 0x45, "LATIN CAPITAL LETTER E" )
      .put( ( char ) 0x46, "LATIN CAPITAL LETTER F" )
      .put( ( char ) 0x47, "LATIN CAPITAL LETTER G" )
      .build()
  ;
}

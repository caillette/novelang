package novelang.parser.unicode;

import novelang.common.LanguageTools;
import novelang.system.Log;
import novelang.system.LogFactory;
import org.apache.commons.lang.CharUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link UnicodeNames} basing on some well-known values.
 *
 * @author Laurent Caillette
 */
public class UnicodeNamesTest {

  @Test
  public void logSomeBasicCharacterRepresentations() {
    final int aCharacterAsInt = 'a' ;
    final String aCharacterAsHex = LanguageTools.to16ByteHex( aCharacterAsInt ) ;
    LOG.info(
        "\nThe 'a' letter" +
        "\nAs int: " + aCharacterAsInt +
        "\nAs hex: " + aCharacterAsHex +
        "\nUnicode escaped: " + CharUtils.unicodeEscaped( 'a' ) 
    ) ;
  }

  @Test
  public void unicode16NameHasSpaces() {
    verify( "LATIN_SMALL_LETTER_A", 'a' ) ;
  }

  @Test
  public void unicode16NameHasNoSpace() {
    verify( "DIAERESIS", '\u00a8' ) ;
  }

  @Test
  public void unicode16NameHasHyphen() {
    verify( "RIGHT-POINTING_DOUBLE_ANGLE_QUOTATION_MARK", '\u00bb' ) ;    
  }

  @Test
  public void unicode16NameIsControl1() {
    verify( "ESCAPE", '\u001b' ) ;
  }

  @Test
  public void unicode16NameIsControl2() {
    verify( "CHARACTER_TABULATION", '\t' ) ;
  }

  @Test
  public void lastKnownValue() {
    verify( "REPLACEMENT_CHARACTER", ( char ) 0xFFFD ) ;
  }

  @Test
  public void smokeTestOnEveryCharacter() {
    final int totalCharacterCount = 256 * 256;
    for( int counter = totalCharacterCount - 256 ; counter < totalCharacterCount ; counter ++ ) {
      final char character = ( char ) counter ;
      UnicodeNames.getPureName( character ) ;
    }
  }

// =======
// Fixture
// =======

  private static final Log LOG = LogFactory.getLog( UnicodeNamesTest.class ) ;

  private static void verify( final String expected, final char character ) {
    assertEquals( expected, UnicodeNames.getPureName( character ) ) ;
  }


}

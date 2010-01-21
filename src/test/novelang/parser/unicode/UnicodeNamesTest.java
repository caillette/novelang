package novelang.parser.unicode;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.apache.commons.lang.CharUtils;

import novelang.system.LogFactory;
import novelang.system.Log;

/**
 * Tests for {@link UnicodeNames} basing on some well-known values.
 *
 * @author Laurent Caillette
 */
public class UnicodeNamesTest {

  @Test
  public void logSomeBasicCharacterRepresentations() {
    final int aCharacterAsInt = 'a' ;
    final String aCharacterAsHex = String.format( "%04X", aCharacterAsInt ) ;
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
  public void verifyAllUnicode16Mapped() {
    // Forget about sign.
    for( short counter = Short.MIN_VALUE ; counter < Short.MAX_VALUE ; counter ++ ) {
      final char character = ( char ) counter ;
      assertNotNull( UnicodeNames.getUnicodeName( character ) ) ;
    }
  }

// =======
// Fixture
// =======

  private static final Log LOG = LogFactory.getLog( UnicodeNamesTest.class ) ;

  private static void verify( final String expected, final char character ) {
    assertEquals( expected, UnicodeNames.getUnicodeName( character ) ) ;
  }





}

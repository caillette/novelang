package novelang.parser.unicode;


import novelang.build.CodeGenerationTools;
import novelang.outfit.TextTools;
import novelang.logger.Logger;
import novelang.logger.LoggerFactory;

/**
 * Reads Unicode character names from a binary file
 * {@link novelang.build.CodeGenerationTools#UNICODE_NAMES_BINARY}.
 * This saves a few hundreds of milliseconds at runtime for each name request, which is of
 * poor interest. The cool thing is the speedup at debug time.
 *
 * @author Laurent Caillette
 */
public class UnicodeNames {

  private static final Logger LOGGER = LoggerFactory.getLogger( UnicodeNames.class ) ;


  private UnicodeNames() { }

  /**
   * Returns the pure Unicode name, like "{@code LATIN_SMALL_LETTER_A}".
   *
   * @param character some character.
   * @return a possibly null String.
   */
  public static String getPureName( final char character ) {
    final Exception exception ;
    final String characterAsString = ( int ) character +
        " (" + toHexadecimalString( character ) + ")" ;
    try {
      final String pureName = new UnicodeNamesBinaryReader(
          UnicodeNames.class.getResource( CodeGenerationTools.UNICODE_NAMES_BINARY ) ).getName( character ) ;
      if( pureName == null ) {
        LOGGER.warn( "No name found for character ", characterAsString ) ;
      } else {
        LOGGER.debug( "Found name for character ", characterAsString, " '", pureName, "'" ) ;
      }
      return pureName ;
    } catch( Exception e ) {
      exception = e ;
    }
    LOGGER.error( exception, "No name found for character ", characterAsString ) ;
    return null ;
  }


  /**
   * Returns Unicode name with hexadecimal value.
   * @param character
   * @return
   */
  public static String getDecoratedName( final char character ) {
    final String pureName = getPureName( character ) ;
    final String hexadecimalValue = toHexadecimalString( character );
    if( pureName == null ) {
      return "[Unicode unknown: " + hexadecimalValue + "]" ;
    } else {
      return pureName + " [" + hexadecimalValue + "]" ;

    }
  }

  private static String toHexadecimalString( final char character ) {
    return "0x" + TextTools.to16ByteHex( character ).toUpperCase();
  }

}

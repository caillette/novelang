package novelang.parser.unicode;

import java.util.Properties;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.InputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.net.URL;


import org.apache.commons.io.IOUtils;

import novelang.system.Log;
import novelang.system.LogFactory;
import novelang.common.LanguageTools;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * Reads Unicode character names from a binary file {@value #RESOURCE_NAME}.
 * This saves a few hundreds of milliseconds at runtime for each name request, which is of
 * poor interest. The cool thing is the speedup at debug time.
 *
 * @author Laurent Caillette
 */
public class UnicodeNames {

  private static final Log LOG = LogFactory.getLog( UnicodeNames.class ) ;
  private static final String RESOURCE_NAME = "names.bin" ;

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
          UnicodeNames.class.getResource( RESOURCE_NAME ) ).getName( character ) ;
      if( pureName == null ) {
        LOG.warn( "No name found for character " + characterAsString ) ;
      } else {
        LOG.debug( "Found name for character " + characterAsString + " '" + pureName + "'" ) ;
      }
      return pureName ;
    } catch( Exception e ) {
      exception = e ;
    }
    LOG.error( "No name found for character " + characterAsString , exception ) ;
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

  private static String toHexadecimalString( char character ) {
    return "0x" + LanguageTools.to16ByteHex( character ).toUpperCase();
  }

}

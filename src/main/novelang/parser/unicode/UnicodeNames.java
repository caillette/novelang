package novelang.parser.unicode;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.text.DecimalFormat;


import novelang.system.Log;
import novelang.system.LogFactory;

/**
 * Reads Unicode character names from a property file.
 *
 * @author Laurent Caillette
 */
public class UnicodeNames {

  private static final Log LOG = LogFactory.getLog( UnicodeNames.class ) ;
  private static final String RESOURCE_NAME = "unicode-names.properties" ;
  private static final String UNDEFINED = "Undefined:" ;

  private static final Properties PROPERTIES ;

  private UnicodeNames() { }

  static {
    PROPERTIES = new Properties() ;
    try {
      final InputStream inputStream = UnicodeNames.class.getResource( RESOURCE_NAME ).openStream() ;
      PROPERTIES.load( inputStream ) ;
    } catch( IOException e ) {
      LOG.error( "Could not load " + RESOURCE_NAME, e ) ;
    }
  }

  public static String getUnicodeName( final int code ) {
    final String formattedCode = new DecimalFormat( "000X" ).format( code ) ;
    return PROPERTIES.getProperty( formattedCode, UNDEFINED + formattedCode ) ;
  }


  public static void main( final String[] args ) {
    for( final String code : PROPERTIES.stringPropertyNames() ) {
      System.out.println( code + "=" + PROPERTIES.getProperty( code ) ) ;
    }
  }

}

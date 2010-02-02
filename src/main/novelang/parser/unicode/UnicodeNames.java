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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * Reads Unicode character names from a property file {@value #RESOURCE_NAME}.
 * The file may contain duplicate keys.
 *
 * @author Laurent Caillette
 */
public class UnicodeNames {

  private static final Log LOG = LogFactory.getLog( UnicodeNames.class ) ;
  private static final String RESOURCE_NAME = "names.bin" ;


  public static String getUnicodeName( final char character ) {
    try {
      final String pureName = new UnicodeNamesBinaryReader(
          UnicodeNames.class.getResource( RESOURCE_NAME ) ).getName( character ) ;
      return pureName.replace( ' ', '_' ) ;
    } catch( Exception e ) {
      LOG.error( "Could not load name for character " + ( int ) character , e ) ;
      return null ;
    }
  }



}

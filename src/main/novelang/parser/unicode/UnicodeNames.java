package novelang.parser.unicode;

import java.util.Properties;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.InputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;


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
  private static final String RESOURCE_NAME = "UnicodeData.txt" ;

  private static final String DESCRIPTOR_TEXT = "(?:\\w| |-|<|>)*" ;
  private static final String IGNORED_DESCRIPTOR = "(?:" + DESCRIPTOR_TEXT + ";)" ;
  private static final String USEFUL_DESCRIPTOR = "(" + DESCRIPTOR_TEXT + ");" ;

  private static final Pattern PROPERTY_LINE_PATTERN =
//      Pattern.compile( "(\\w+);((?:\\w| |-)+);(?:(?:\\w| |-|<|>)*;){7}(?:(?:\\w| |-|<|>)*;)(?:(?:\\w| |-|<|>)*;){4}" ) ;
      Pattern.compile( "(\\w+);" +
          USEFUL_DESCRIPTOR +
          IGNORED_DESCRIPTOR + "{8}" +
          USEFUL_DESCRIPTOR +
          IGNORED_DESCRIPTOR + "{3}"
      ) ;

  static {
    LOG.debug( "Crafted regex: " + PROPERTY_LINE_PATTERN.pattern() ) ;
  }

  private static final Map< Character, String > CODE_TO_NAMES = loadNames( readProperties() ) ;

  private UnicodeNames() { }


  private static String readProperties() {
    try {
      final InputStream inputStream = UnicodeNames.class.getResource( RESOURCE_NAME ).openStream() ;
      return IOUtils.toString( inputStream ) ;
    } catch( IOException e ) {
      LOG.error( "Could not read " + RESOURCE_NAME, e ) ;
      return "" ;
    }
  }

  private static Map< Character, String > loadNames( final String propertiesAsString )
  {
    final Map< Character, String > characterToNameMap = Maps.newHashMap() ;
    final Matcher matcher = PROPERTY_LINE_PATTERN.matcher( propertiesAsString ) ;
    while( matcher.find() ) {
      final String code = matcher.group( 1 ) ;
      if( code.length() == 4 ) {
        final String name ;
        final String casualName = matcher.group( 2 ) ;
        if( "<control>".equals( casualName ) ) {
          final String controlName = matcher.group( 3 ) ;
          name = controlName ;
        } else {
          name = casualName ;
        }       

        final int codeAsInt = Integer.parseInt( code, 16 ) ; // Be confident!
        final Character character = ( char ) codeAsInt ;
        final String existing = characterToNameMap.get( character ) ;
        if( existing == null ) {
          // Retain first definition, seems that most interesting appear first.
          characterToNameMap.put( character, name ) ;
        }
      }
    }
    return ImmutableMap.copyOf( characterToNameMap ) ;
  }

  public static String getUnicodeName( final char character ) {
    final String unicodeName = CODE_TO_NAMES.get( character ) ;
    if( unicodeName == null ) {
      return "(Unicode unknown) " + String.format( "%04X", ( int ) character ) ;
    } else {
      return unicodeName.replace( ' ', '_' ) ;
    }
  }



}

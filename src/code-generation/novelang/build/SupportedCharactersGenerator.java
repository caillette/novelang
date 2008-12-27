/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package novelang.build;

import java.util.List;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;

import org.antlr.stringtemplate.StringTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharUtils;
import com.google.common.collect.*;
import com.google.common.base.Function;
import com.google.common.base.Nullable;


/**
 * Generates a Java enum from tokens (in a {@code tokens} clause) declared in and ANTLR grammar.
 *
 * @author Laurent Caillette
 */
public class SupportedCharactersGenerator extends JavaGenerator {

  private static final Logger LOGGER = LoggerFactory.getLogger( SupportedCharactersGenerator.class ) ;

  public SupportedCharactersGenerator(
      File grammarFile,
      String packageName,
      String className,
      File targetDirectory
  ) throws IOException
  {
    super( grammarFile, packageName, className, targetDirectory ) ;
  }

  protected String generateCode() {
    return generateJavaEnumeration(
        convertToEscapedUnicode( 
            extractSupportedCharacters( getGrammar() ) 
        ) 
    ) ;
  } 

  protected String generateJavaEnumeration( Set< String > characters ) {
    final StringTemplate javaEnum = createStringTemplate( "setOfCharacters" ) ;
    javaEnum.setAttribute( "characters", characters ) ;
    return javaEnum.toString() ;

  }
  
  
  private static final Pattern TOKENS_DECLARATIONS =
      Pattern.compile( "'(.)'|'(\\\\.)'|'(\\\\u[a-f[0-9]]{4})'" ) ;
  static {
    LOGGER.debug( "Crafted regex: " + TOKENS_DECLARATIONS.toString() ) ;
  }


// ======  
// Escape
// ======  
  
  private static final Function<Character,String> CHAR_TO_UNICODE = 
      new Function< Character, String >() {
        public String apply( Character character ) {
          if( Character.)
          return CharUtils.unicodeEscaped( character ) ;
        }
      }
  ;
  
  public static Set< String > convertToEscapedUnicode( Set< Character > characters ) {
    // We cannot apply a function directly to a Set (because results may imply duplicates).
    return ImmutableSet.copyOf( 
        Lists.transform( Lists.newArrayList( characters ), 
        CHAR_TO_UNICODE ) 
    ) ;
  }

// ==========  
// Extraction
// ==========
  
  /**
   * Declaration order matters: it corresponds to the group index in the regex.
   */
  private static final CharacterExtractor CHARACTER_EXTRACTOR =
      new CharacterExtractor(
          new LitteralConverter(),
          new EscapedCharacterConverter(),
          new UnicodeConverter()
      )
  ;

  protected static Set< Character > extractSupportedCharacters( String tokensDeclaration ) {
    final Matcher matcher = TOKENS_DECLARATIONS.matcher( tokensDeclaration ) ;
    final Set< Character > characters = Sets.newHashSet() ;
    while( matcher.find() ) {
      final Character character = extractCharacter( matcher ) ;
      if( null != character ) {
        characters.add( character ) ;
      }
    }
    return characters ;
  }

  private static Character extractCharacter( Matcher matcher ) {
    return CHARACTER_EXTRACTOR.extract( matcher ) ;
  }

  private interface CharacterConverter {
    Character convert( String declaration ) ;
  }

  private static class CharacterExtractor  {
    private final CharacterConverter[] converters ;

    public CharacterExtractor( CharacterConverter... converters ) {
      this.converters = converters.clone() ;
    }

    public final Character extract( Matcher matcher ) {
      final int effectiveGroupCount = matcher.groupCount() ;
      if( converters.length != matcher.groupCount() ) {
        throw new IllegalArgumentException(
            "Matcher has " + matcher.groupCount() + " groups (including group 0) against "
          + converters.length + " converters"
        ) ;
      }

      for( int converterIndex = 0 ; converterIndex < effectiveGroupCount ; converterIndex++ ) {
        final int groupIndex = converterIndex + 1;
        final String match = matcher.group( groupIndex ) ;
        if( match != null  ) {
          return converters[ converterIndex ].convert( match ) ;
        }
      }

      return null ;
    }


  }

  private static class LitteralConverter implements CharacterConverter {

    public Character convert( String characterDeclaration ) {
      if( 1 != characterDeclaration.length() ) {
        throw new IllegalArgumentException(
            "Should contains one character only, was: '" + characterDeclaration + "'" ) ;
      }
      return characterDeclaration.charAt( 0 ) ;
    }
  }

  private static class UnicodeConverter implements CharacterConverter {

    public Character convert( String characterDeclaration ) {
      if( ! characterDeclaration.startsWith( "\\u" ) ) {
        throw new IllegalArgumentException(
            "Should be unicode starting with '\\u', was: '" + characterDeclaration + "'" ) ;
      }
      final String hex = "#" +
          characterDeclaration.substring( 2, characterDeclaration.length() ) ;
//      LOGGER.debug( "Decoding {}", hex ) ;
      final Integer decoded = Integer.decode( hex ) ;
      return ( char ) decoded.intValue() ;
    }
  }

  private static class EscapedCharacterConverter implements CharacterConverter {

    public Character convert( String characterDeclaration ) {
      if( ! characterDeclaration.startsWith( "\\" ) ) {
        throw new IllegalArgumentException(
            "Should be escaped starting with '\\', was: '" + characterDeclaration + "'" ) ;
      }
      if( characterDeclaration.length() != 2 ) {
        throw new IllegalArgumentException(
            "Should start with '\\' then 1 character, was: '" + characterDeclaration + "'" ) ;
      }
      return characterDeclaration.charAt( 1 ) ;
    }
  }



}
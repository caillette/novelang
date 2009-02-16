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

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import novelang.parser.shared.Lexeme;

/**
 * @author Laurent Caillette
*/
/*static*/ class LexemeDeclarationExtractor {

  private static final Logger LOGGER = LoggerFactory.getLogger( LexemeDeclarationExtractor.class ) ;

  /**
   * Groups:
   * <ol>
   *   <li>Token name</li>
   *   <li>Token litteral as single character.</li>
   *   <li>Token litteral as escaped character.</li>
   *   <li>Token litteral as unicode (hex).</li>
   *   <li>Named HTML entity as comment (optional).</li>
   * </ol>
   */
  private static final Pattern TOKENS_DECLARATIONS = Pattern.compile(
      "([A-Z0-9_]+)\\ *\\:\\ *(?:'(.)'|'(\\\\.)'|'(\\\\u[a-f[0-9]]{4})')\\ *;(?:\\ *//\\ *\\&([A-Za-z0-9]+);)?+"
  ) ;

  static {
    LOGGER.debug( "Crafted regex: " + TOKENS_DECLARATIONS.toString() ) ;
  }
  
  private final CharacterConverter[] converters ;

  public LexemeDeclarationExtractor( CharacterConverter... converters ) {
    this.converters = converters.clone() ;
  }

  public static Set< Lexeme > extractLexemeDeclarations( String grammar ) {
    final Matcher matcher = TOKENS_DECLARATIONS.matcher( grammar ) ;
    final Set< Lexeme > declarations = Sets.newHashSet() ;
    while( matcher.find() ) {
      final Lexeme declaration = extractLexemeDeclaration( matcher ) ;
      if( null != declaration ) {
        declarations.add( declaration ) ;
      }
    }
    return declarations ;

  }

  private final Lexeme extract( Matcher matcher ) {
    final int expectedGroupCount = converters.length + 2 ;
    Preconditions.checkArgument(
        matcher.groupCount() == expectedGroupCount,
        "Matcher has %i groups (including lexeme name and comment) against %i converters",
        matcher.groupCount(),
        converters.length
    ) ;

    final String tokenName = matcher.group( 1 ) ;
    final String htmlEntityName = matcher.group( matcher.groupCount() ) ;

    for( int converterIndex = 0 ; converterIndex < converters.length ; converterIndex++ ) {
      final int groupIndex = converterIndex + 2 ;
      final String match = matcher.group( groupIndex ) ;
      if( match != null  ) {
        final Character character = converters[ converterIndex ].convert( match ) ;
        return new Lexeme( tokenName, character, htmlEntityName ) ;
      }
    }

    return null ;
  }

  private static Lexeme extractLexemeDeclaration( Matcher matcher ) {
    return DECLARATION_EXTRACTOR.extract( matcher ) ;
  }

  /**
   * Declaration order matters: it corresponds to the group index in the regex.
   */
  private static final LexemeDeclarationExtractor DECLARATION_EXTRACTOR =
      new LexemeDeclarationExtractor(
          new LexemeDeclarationExtractor.LitteralCharacterConverter(),
          new LexemeDeclarationExtractor.EscapedCharacterConverter(),
          new LexemeDeclarationExtractor.UnicodeCharacterConverter()
      )
  ;


// ====================
// Character converters
// ====================

  interface CharacterConverter {
    Character convert( String declaration ) ;
  }


  public static class LitteralCharacterConverter implements CharacterConverter {

    public Character convert( String characterDeclaration ) {
      if( 1 != characterDeclaration.length() ) {
        throw new IllegalArgumentException(
            "Should contains one character only, was: '" + characterDeclaration + "'" ) ;
      }
      return characterDeclaration.charAt( 0 ) ;
    }
  }

  public static class UnicodeCharacterConverter implements CharacterConverter {

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

  public  static class EscapedCharacterConverter implements CharacterConverter {

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

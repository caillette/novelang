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
package org.novelang.build.antlr;

import java.util.Set;

import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.parser.shared.Lexeme;
import org.apache.commons.lang.CharUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link LexemeGenerator}.
 *
 * @author Laurent Caillette
 */
@SuppressWarnings( { "HardcodedFileSeparator" } )
public class LexemeDeclarationExtractorTest {

  @Test
  public void extractSupportedCharacters() {
    final String grammar =
        createAntlrDeclaration( SMALL_X ) +
        createAntlrDeclaration( BIG_X ) +
        createAntlrDeclaration( ZERO ) +
        createAntlrDeclaration( AGRAVE ) +
        createAntlrDeclaration( RSOLIDUS ) +
        createAntlrDeclaration( VBAR )
    ;

    LOGGER.info( "Created grammar: \n", grammar );

    final Set< Lexeme > declarations =
        LexemeDeclarationExtractor.extractLexemeDeclarations( grammar ) ;

    LOGGER.debug( "Got: ", declarations ) ;

    assertEquals( 6L, ( long ) declarations.size() ) ;

    assertTrue( declarations.contains( SMALL_X ) ) ;
    assertTrue( declarations.contains( BIG_X ) ) ;
    assertTrue( declarations.contains( ZERO ) ) ;
    assertTrue( declarations.contains( AGRAVE ) ) ;
    assertTrue( declarations.contains( RSOLIDUS ) ) ;
    assertTrue( declarations.contains( VBAR ) ) ;

  }

  @Test
  public void extractJustPunctuationSign() {
    final String grammar = createAntlrDeclaration( VBAR ) ;
    final Set<Lexeme> declarations =
        LexemeDeclarationExtractor.extractLexemeDeclarations( grammar ) ;

    LOGGER.debug( "Got: ", declarations ) ;
    assertEquals( 1L, ( long ) declarations.size() ) ;
    assertTrue( declarations.contains( VBAR ) ) ;

  }

  @Test
  public void extractJustUnicodeLowerCase() {
    final String grammar = createAntlrDeclaration( AGRAVE ) ;
    final Set<Lexeme> lexemeDeclarations =
        LexemeDeclarationExtractor.extractLexemeDeclarations( grammar ) ;

    LOGGER.debug( "Got: ", lexemeDeclarations ) ;
    assertEquals( 1L, ( long ) lexemeDeclarations.size() ) ;
    assertTrue( lexemeDeclarations.contains( AGRAVE ) ) ;

  }

  @Test
  public void extractJustUnicodeUpperCase() {
    final String grammar = createAntlrDeclaration( ICUTE, SymbolRepresentation.UNICODE_UPPER ) ;
    final Set< Lexeme > lexemeDeclarations =
        LexemeDeclarationExtractor.extractLexemeDeclarations( grammar ) ;

    LOGGER.debug( "Got: ", lexemeDeclarations ) ;
    assertEquals( 1L, ( long ) lexemeDeclarations.size() ) ;
    assertTrue( lexemeDeclarations.contains( ICUTE ) ) ;

  }

/*
  @Test
  public void convertToEscapedUnicode() {
    final Set< Character > characters = ImmutableSet.of( '\u00f6' ) ;
    final Set< LexemeGenerator.Item > escapedCharacters =
        LexemeGenerator.convertToEscapedUnicode( characters ) ;
    assertEquals( 1, escapedCharacters.size() ) ;
    assertEquals( "\\u00f6", escapedCharacters.iterator().next().declaration ) ;
    
  }
*/

// =======
// Fixture
// =======

  private static final Logger LOGGER = 
      LoggerFactory.getLogger( LexemeDeclarationExtractorTest.class );

  private static final Lexeme SMALL_X = new Lexeme( "SMALL_X", 'x', null, null ) ;
  private static final Lexeme BIG_X = new Lexeme( "BIG_X", 'X', null, "X" ) ;
  private static final Lexeme ZERO = new Lexeme( "ZERO", '0', null, "000" ) ;
  private static final Lexeme AGRAVE = new Lexeme( "AGRAVE", '\u00e0', "agrave", "a" ) ;
  private static final Lexeme ICUTE = new Lexeme( "ICUTE", '\u00ED', "icute", "i" ) ;
  private static final Lexeme RSOLIDUS = new Lexeme( "RSOLIDUS", '\\', null, null ) ;
  private static final Lexeme VBAR = new Lexeme( "VBAR", '|', null, null ) ;

  private static String createAntlrDeclaration( final Lexeme declaration ) {
    return createAntlrDeclaration( declaration, SymbolRepresentation.LITERAL ) ;
  }

  private static String createAntlrDeclaration(
      final Lexeme declaration, 
      final SymbolRepresentation symbolRepresentation
  ) {
    final StringBuilder declarationBuilder = new StringBuilder() ;
    declarationBuilder.append( declaration.getUnicodeName() ) ;
    declarationBuilder.append( " : '" ) ;

    final String unicodeEscaped = CharUtils.unicodeEscaped( declaration.getCharacter() ) ;
    switch( symbolRepresentation ) {
      case LITERAL :
        declarationBuilder.append( declaration.getCharacter() ) ;
        break ;
      case UNICODE_LOWER :
        declarationBuilder.append( unicodeEscaped ) ;
        break ;
      case UNICODE_UPPER :
        declarationBuilder.append( "\\u" ) ;
        declarationBuilder.append( unicodeEscaped.substring( 2 ).toUpperCase() ) ;
        break ;
      default :
        throw new IllegalArgumentException( "Unsupported: " + symbolRepresentation ) ;
    }
    declarationBuilder.append( "' ; " ) ;
    if( declaration.hasHtmlEntityName() || declaration.hasDiacriticlessRepresentation() ) {
      declarationBuilder.append( "// " ) ;
      if( declaration.hasHtmlEntityName() ) {
        declarationBuilder.append( "&" ) ;
        declarationBuilder.append( declaration.getHtmlEntityName() ) ;
        declarationBuilder.append( "; " ) ;
      }
      if( declaration.hasDiacriticlessRepresentation() ) {
        declarationBuilder.append( "\"" ) ;
        declarationBuilder.append( declaration.getAscii62() ) ;
        declarationBuilder.append( "\"" ) ;
      }
    }
    declarationBuilder.append( "\n" ) ;
    return declarationBuilder.toString() ;
  }
  
  private enum SymbolRepresentation {
    LITERAL, UNICODE_LOWER, UNICODE_UPPER
  }

}
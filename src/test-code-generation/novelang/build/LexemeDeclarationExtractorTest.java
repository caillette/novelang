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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import novelang.system.LogFactory;
import novelang.system.Log;
import novelang.parser.shared.Lexeme;

/**
 * Tests for {@link LexemeGenerator}.
 *
 * @author Laurent Caillette
 */
public class LexemeDeclarationExtractorTest {

  @Test
  public void extractSupportedCharacters() {
    final String grammar =
        createAntlrDeclaration( SMALL_X, "x" ) +
        createAntlrDeclaration( BIG_X, "X" ) +
        createAntlrDeclaration( ZERO, "0" ) +
        createAntlrDeclaration( AGRAVE, "\u00e0" ) +
        createAntlrDeclaration( RSOLIDUS, "\\\\" ) +
        createAntlrDeclaration( VBAR, "|" )
    ;

    LOG.info( "Created grammar: \n%s", grammar );

    final Set< Lexeme > declarations =
        LexemeDeclarationExtractor.extractLexemeDeclarations( grammar ) ;

    LOG.debug( "Got: %s", declarations ) ;

    assertEquals( 6, declarations.size() ) ;

    assertTrue( declarations.contains( SMALL_X ) ) ;
    assertTrue( declarations.contains( BIG_X ) ) ;
    assertTrue( declarations.contains( ZERO ) ) ;
    assertTrue( declarations.contains( AGRAVE ) ) ;
    assertTrue( declarations.contains( RSOLIDUS ) ) ;
    assertTrue( declarations.contains( VBAR ) ) ;

  }

  @Test
  public void extractJustPunctuationSign() {
    final String grammar = createAntlrDeclaration( VBAR, "|" ) ;
    final Set<Lexeme> declarations =
        LexemeDeclarationExtractor.extractLexemeDeclarations( grammar ) ;

    LOG.debug( "Got: %s", declarations ) ;
    assertEquals( 1, declarations.size() ) ;
    assertTrue( declarations.contains( VBAR ) ) ;

  }

  @Test
  public void extractJustUnicode() {
    final String grammar = createAntlrDeclaration( AGRAVE, "\u00e0" ) ;
    final Set<Lexeme> lexemeDeclarations =
        LexemeDeclarationExtractor.extractLexemeDeclarations( grammar ) ;

    LOG.debug( "Got: %s", lexemeDeclarations ) ;
    assertEquals( 1, lexemeDeclarations.size() ) ;
    assertTrue( lexemeDeclarations.contains( AGRAVE ) ) ;

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

  private static final Log LOG = LogFactory.getLog( LexemeDeclarationExtractorTest.class ) ;

  private static final Lexeme SMALL_X = new Lexeme( "SMALL_X", 'x', null ) ;
  private static final Lexeme BIG_X = new Lexeme( "BIG_X", 'X', null ) ;
  private static final Lexeme ZERO = new Lexeme( "ZERO", '0', null ) ;
  private static final Lexeme AGRAVE = new Lexeme( "AGRAVE", '\u00e0', "agrave" ) ;
  private static final Lexeme RSOLIDUS = new Lexeme( "RSOLIDUS", '\\', null ) ;
  private static final Lexeme VBAR = new Lexeme( "VBAR", '|', null ) ;

  private String createAntlrDeclaration( Lexeme declaration, String symbol ) {
    return
        declaration.getUnicodeName() + " : " + "'" + symbol + "' ; " +
        ( declaration.hasHtmlEntityName() ? "// &" + declaration.getHtmlEntityName() + ";" : "" ) +
        "\n"
    ;
  }


}
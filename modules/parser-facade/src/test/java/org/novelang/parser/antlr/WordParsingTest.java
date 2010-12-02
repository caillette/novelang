/*
 * Copyright (C) 2010 Laurent Caillette
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
package org.novelang.parser.antlr;

import java.util.Map;

import org.junit.Test;
import org.antlr.runtime.RecognitionException;
import static org.novelang.parser.antlr.TreeFixture.tree;
import static org.novelang.parser.NodeKind.WORD_;
import static org.novelang.parser.NodeKind.WORD_AFTER_CIRCUMFLEX_ACCENT;
import org.novelang.parser.SourceUnescape;

/**
 * Tests for work parsing.
 *
 * @author Laurent Caillette
 */
public class WordParsingTest {


  @Test
  public void wordCausedABug1() throws RecognitionException {
    PARSERMETHOD_WORD.checkTreeAfterSeparatorRemoval( "myIdentifier", tree( WORD_, "myIdentifier" ) ) ;
  }

  @Test
  /**
   * This one because {@code 'fi'} was recognized as the start of {@code 'file'}
   * and the parser generated this error:
   * {@code line 1:10 mismatched character 'e' expecting 'l'}.
   */
  public void wordCausedABug2() throws RecognitionException {
    PARSERMETHOD_WORD.checkTreeAfterSeparatorRemoval( "fi", tree( WORD_, "fi" ) ) ;
  }

  @Test
  public void wordIsSingleLetter() throws RecognitionException {
    PARSERMETHOD_WORD.checkTreeAfterSeparatorRemoval( "w", tree( WORD_, "w" ) ) ;
  }

  @Test
  public void wordIsTwoLetters() throws RecognitionException {
    PARSERMETHOD_WORD.checkTreeAfterSeparatorRemoval( "Www", tree( WORD_, "Www" ) ) ;
  }

  @Test
  public void wordIsThreeDigits() throws RecognitionException {
    PARSERMETHOD_WORD.checkTreeAfterSeparatorRemoval( "123", tree( WORD_, "123" ) ) ;
  }

  @Test
  public void wordIsDigitsWithHyphenMinusInTheMiddle() throws RecognitionException {
    PARSERMETHOD_WORD.checkTreeAfterSeparatorRemoval( "123-456", tree( WORD_, "123-456" ) ) ;
  }

  @Test
  public void wordFailsWithLeadingApostrophe() throws RecognitionException {
    PARSERMETHOD_WORD.checkFails( "'w" ) ;
  }

  @Test
  public void wordFailsWithTrailingHyphenMinus() throws RecognitionException {
    PARSERMETHOD_WORD.checkFails( "'w-" ) ;
  }

  @Test
  public void wordWithSuperscript() throws RecognitionException {
    PARSERMETHOD_WORD.checkTreeAfterSeparatorRemoval(
        "w^e",
        tree( WORD_, tree( "w" ), tree( WORD_AFTER_CIRCUMFLEX_ACCENT, "e" ) )
    ) ;
  }

  @Test
  public void wordIsEveryEscapedCharacter() throws RecognitionException {
    final Map< String, Character > map = SourceUnescape.getMainCharacterEscapes() ;
    for( final String key : map.keySet() ) {
      final String escaped = SourceUnescape.ESCAPE_START + key + SourceUnescape.ESCAPE_END ;
      final Character unescaped = map.get( key ) ;
      PARSERMETHOD_WORD.checkTreeAfterSeparatorRemoval( escaped, tree( WORD_, "" + unescaped ) ) ;
    }
  }


// =======
// Fixture
// =======

  private static final ParserMethod PARSERMETHOD_WORD =
      new ParserMethod( "word" ) ;


}

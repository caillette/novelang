/*
 * Copyright (C) 2011 Laurent Caillette
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
package org.novelang.rendering;

import java.util.regex.Pattern;

import org.fest.reflect.core.Reflection;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.novelang.parser.NodeKind.*;
import static org.novelang.parser.antlr.TreeFixture.tree;

import org.novelang.common.SyntacticTree;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.novella.Novella;
import org.novelang.novella.NovellaFixture;

/**
 * Tests for 
 * {@link RenderingTools#toImplicitIdentifier(org.novelang.common.SyntacticTree)}.
 *
 * @author Laurent Caillette
 */
public class RenderingToolsForIdentifierTest {

  @Test
  public void renderJustAWord() throws Exception {
    final SyntacticTree tree = tree(
        LEVEL_TITLE,
        tree( WORD_, "word" )
    ) ;
    verify( "word", tree );
  }

  @Test
  public void useAscii62() throws Exception {
    final SyntacticTree tree = tree(
        LEVEL_TITLE,
        tree( WORD_, "a\u00e8\u0153" ) // "aéœ"
    ) ;
    verify( "aeoe", tree );
  }

  @Test
  public void renderWordsAndPunctuationSigns() throws Exception {
    final SyntacticTree tree = tree(
        LEVEL_TITLE,
        tree( WORD_, "wx" ),
        tree( PUNCTUATION_SIGN, tree( SIGN_COMMA, tree( "," ) ) ),
        tree( WORD_, "yz" ),
        tree( PUNCTUATION_SIGN, tree( SIGN_FULLSTOP, tree( "." ) ) )

    ) ;
    verify( "wx_yz", tree );
  }

  @Test
  public void renderWordsAndPunctuationSignsWithFirstWordHavingCap() throws Exception {
    final SyntacticTree tree = tree(
        LEVEL_TITLE,
        tree( WORD_, "Wx" ),
        tree( PUNCTUATION_SIGN, tree( SIGN_COMMA, tree( "," ) ) ),
        tree( WORD_, "yz" ),
        tree( PUNCTUATION_SIGN, tree( SIGN_FULLSTOP, tree( "." ) ) )

    ) ;
    verify( "Wx_yz", tree );
  }

  @Test
  public void renderWordThenBlockInsideGraveAccents() throws Exception {
    final SyntacticTree tree = tree(
        LEVEL_TITLE,
        tree( WORD_, "v" ),
        tree( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS, tree( "0.1.2" ) )

    ) ;
    verify( "v0-1-2", tree );
  }


  @Test
  public void twoAdjacentWords() throws Exception {
    final SyntacticTree tree = tree(
        LEVEL_TITLE,
        tree( WORD_, "wx" ),
        tree( WORD_, "yz" )

    ) ;
    verify( "wxYz", tree );
  }

  @Test
  public void hyphenInTheMiddleOfAWord() throws Exception {
    final SyntacticTree tree = tree(
        LEVEL_TITLE,
        tree( WORD_, "xy-z" )

    ) ;
    verify( "xy-z", tree );
  }


  @Test
  public void twoAdjacentWordsFirstWithCap() throws Exception {
    final SyntacticTree tree = tree(
        LEVEL_TITLE,
        tree( WORD_, "Wx" ),
        tree( WORD_, "yz" )

    ) ;
    verify( "WxYz", tree );
  }


  @Test
  public void twoAdjacentWordsSecondWithCap() throws Exception {
    final SyntacticTree tree = tree(
        LEVEL_TITLE,
        tree( WORD_, "wx" ),
        tree( WORD_, "Yz" )

    ) ;
    verify( "wxYz", tree );
  }


  @Test
  public void punctuationSignThenBlock() throws Exception {
    final SyntacticTree tree = tree(
        LEVEL_TITLE,
        tree( WORD_, "wx" ),
        tree( 
            PUNCTUATION_SIGN, 
            tree( SIGN_COMMA, "," ) 
        ),
        tree( 
            BLOCK_INSIDE_PARENTHESIS, 
            tree( WORD_, "Yz" )        
        )

    ) ;
    verify( "wx_Yz", tree );
  }


  @Test
  public void parenthesis() throws Exception {
    final SyntacticTree tree = tree(
        LEVEL_TITLE,
        tree( WORD_, "uv" ),
        tree(
            BLOCK_INSIDE_PARENTHESIS,
            tree( WORD_, "wx" ),
            tree( WORD_, "Yz" )
        )
    ) ;
    verify( "uv_wxYz", tree );
  }

  @Test
  public void regexForWordReplacement() {
    final Pattern pattern = Reflection.staticField( "WORD_BUT_FIRST" ).
        ofType( Pattern.class ).in( RenderingTools.class ).get() ;
    assertNotNull( pattern ) ;
    assertEquals( "uv§_yz", "uv wx_yz".replaceAll( pattern.pattern(), "§" ) ) ;

  }
  
  @Test
  public void demo() throws Exception {
    logRendered( "This is some text. Be cool." ) ;
    logRendered( "This is a title... (So what?)" ) ;
    logRendered( "Version `0.1.2.3`" ) ;
    logRendered( "Some ``@#!garbage)<--.§``here!" ) ;
    logRendered( "Some `@#!garbage)<--.§` here!" ) ;
    logRendered( "Some `@#!garbage)<--.§` " ) ;
  }



// =======
// Fixture
// =======
  
  private static final Logger LOGGER = LoggerFactory.getLogger( 
      RenderingToolsForIdentifierTest.class ) ;

  private static void verify( final String expected, final SyntacticTree tree ) throws Exception {
    final String rendered = RenderingTools.toImplicitIdentifier( tree ) ;
    assertEquals( expected, rendered ) ;
  }
  
  private void logRendered( final String text ) throws Exception {
    final Novella novella = NovellaFixture.create( text ) ;
    final String identifier = RenderingTools.toImplicitIdentifier( novella.getDocumentTree() ) ;
    LOGGER.info( "\n    " + text + "\n -> " + identifier ) ;
  }
  
}
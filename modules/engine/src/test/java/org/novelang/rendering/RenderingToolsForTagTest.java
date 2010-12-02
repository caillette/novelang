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
package org.novelang.rendering;

import java.util.Set;

import org.novelang.common.SyntacticTree;
import org.novelang.designator.Tag;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.novella.Novella;
import org.novelang.novella.NovellaFixture;
import org.junit.Test;

import static org.novelang.parser.NodeKind.*;
import static org.novelang.parser.antlr.TreeFixture.tree;
import static org.junit.Assert.assertEquals;

/**
 * Tests for 
 * {@link RenderingTools#toImplicitIdentifier(org.novelang.common.SyntacticTree)}.
 *
 * @author Laurent Caillette
 */
public class RenderingToolsForTagTest {

  @Test
  public void singleWord() throws Exception {
    final SyntacticTree tree = tree(
        LEVEL_TITLE,
        tree( WORD_, "word" )
    ) ;
    verify( tree, "word" ) ;
  }

  @Test
  public void useAscii62() throws Exception {
    final SyntacticTree tree = tree(
        LEVEL_TITLE,
        tree( WORD_, "a\u00e8\u0153" ) // "aéœ"
    ) ;
    verify( tree, "aeoe" );
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
    verify( tree, "wx", "yz" ) ;
  }

  @Test
  public void renderWordsAndPunctuationSignsWithFirstWordHavingCap() throws Exception {
    final SyntacticTree tree = tree(
        LEVEL_TITLE,
        tree( WORD_, "uv" ),
        tree( PUNCTUATION_SIGN, tree( SIGN_COMMA, tree( "," ) ) ),
        tree( WORD_, "wx" ),
        tree( PUNCTUATION_SIGN, tree( SIGN_COMMA, tree( "," ) ) ),
        tree( WORD_, "yz" ),
        tree( PUNCTUATION_SIGN, tree( SIGN_FULLSTOP, tree( "." ) ) )

    ) ;
    verify( tree, "uv", "wx", "yz" );
  }

  @Test
  public void renderWordThenBlockInsideGraveAccents() throws Exception {
    final SyntacticTree tree = tree(
        LEVEL_TITLE,
        tree( WORD_, "v" ),
        tree( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS, tree( "0.1.2" ) )

    ) ;
    verify( tree, "v0-1-2" ) ;
  }


  @Test
  public void hyphenInTheMiddleOfAWord() throws Exception {
    final SyntacticTree tree = tree(
        LEVEL_TITLE,
        tree( WORD_, "xy-z" )

    ) ;
    verify( tree, "xy-z" ) ;
  }



  @Test
  public void punctuationSignThenBlock() throws Exception {
    final SyntacticTree tree = tree(
        LEVEL_TITLE,
        tree( WORD_, "uv" ),
        tree( 
            PUNCTUATION_SIGN, 
            tree( SIGN_COMMA, "," ) 
        ),
        tree( 
            BLOCK_INSIDE_PARENTHESIS, 
            tree( WORD_, "wx" ),        
            tree( WORD_, "yz" )        
        )

    ) ;
    verify( tree, "uv", "wxYz" ) ;
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
    verify( tree, "uv", "wxYz" );
  }

  
  @Test
  public void demo() throws Exception {
    logRendered( "This is some text. Be cool." ) ;
    logRendered( "This is a title... (so what?)" ) ;
    logRendered( "version `0.1.2.3`" ) ;
    logRendered( "Some ``@#!garbage)<--.§``here!" ) ;
    logRendered( "More ``@#!garbage)<--.§``here!" ) ;
  }



// =======
// Fixture
// =======
  
  private static final Logger LOGGER = LoggerFactory.getLogger( RenderingToolsForTagTest.class );

  private static void verify( 
      final SyntacticTree tree, 
      final String... expected 
  ) throws Exception {
    final Set< Tag > rendered = RenderingTools.toImplicitTagSet( tree ) ;
    assertEquals( Tag.toTagSet( expected ), rendered ) ;
  }
  
  private void logRendered( final String text ) throws Exception {
    final Novella novella = NovellaFixture.create( text ) ;
    final Set< Tag > tags = RenderingTools.toImplicitTagSet( novella.getDocumentTree() ) ;
    LOGGER.info( "\n    " + text + "\n -> " + tags ) ;
  }
  
}
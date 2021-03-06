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
package org.novelang.treemangling;

import org.junit.Ignore;
import org.junit.Test;
import static org.novelang.parser.NodeKind.*;
import static org.novelang.parser.antlr.TreeFixture.tree;

import org.novelang.common.SyntacticTree;
import org.novelang.common.tree.Treepath;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.parser.antlr.TreeFixture;

/**
 * Tests for {@link LevelMangler}.
 * 
 * @author Laurent Caillette
 */
public class UrlManglerTest {

  @Test
  public void doNothingWhenNothingToDo() {
    final SyntacticTree tree = tree(
        NOVELLA,
        tree( PARAGRAPH_REGULAR ),
        tree( PARAGRAPH_REGULAR )
    );
    verifyFixNamedUrls(
        tree,
        tree
    ) ;
  }

  @Test
  public void namedUrlAtStartOfAPart() {
    verifyFixNamedUrls( 
        tree(
            NOVELLA,
            tree( 
                PARAGRAPH_REGULAR,
                tree(
                    _URL,
                    tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
                    tree( URL_LITERAL, "http://foo.com" )
                )                            
            )            
        ),
        tree(
            NOVELLA,
            tree( WHITESPACE_, "  " ),
            tree( 
                PARAGRAPH_REGULAR,
                tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
                tree( LINE_BREAK_ ),
                tree( URL_LITERAL, "http://foo.com" )
                            
            )
        )        
    ) ;
  }

  @Test
  public void detectNoNamingWanted() {
    verifyFixNamedUrls(
        tree(
            PARAGRAPH_REGULAR,
            tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
            tree( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS, " " ),
            tree( _URL, tree( URL_LITERAL, "http://foo.com" ) )
        ),
        tree(
            PARAGRAPH_REGULAR,
            tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
            tree( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS, " " ),
            tree( LINE_BREAK_ ),
            tree( URL_LITERAL, "http://foo.com" )
        )
    ) ;
  }

  @Test
  public void namedUrlWithSquareBrackets() {
    verifyFixNamedUrls(
        tree(
            NOVELLA,
            tree(
                PARAGRAPH_REGULAR,
                tree(
                    _URL,
                    tree( BLOCK_INSIDE_SQUARE_BRACKETS, tree( WORD_, "name" ) ),
                    tree( URL_LITERAL, "http://foo.com" )
                )
            )
        ),
        tree(
            NOVELLA,
            tree(
                PARAGRAPH_REGULAR,
                tree( BLOCK_INSIDE_SQUARE_BRACKETS, tree( WORD_, "name" ) ),
                tree( LINE_BREAK_ ),
                tree( URL_LITERAL, "http://foo.com" )

            )
        )
    ) ;
  }

  /**
   * This test reproduces a bug. It stresses detection of paragraph exit.
   */
  @Test
  public void fixNamedUrlAtStartOfParagraphAfterLevelIntroducer() {
    verifyFixNamedUrls( 
        tree(
            NOVELLA,
            tree( 
                PARAGRAPH_REGULAR,
                tree( WORD_, "p" )
            ),
            tree( 
                LEVEL_INTRODUCER_,
                tree( LEVEL_INTRODUCER_INDENT_, "==" ),
                tree( LEVEL_TITLE, tree( WORD_, "t" ) )            
            ),
            tree( 
                PARAGRAPH_REGULAR,
                tree(
                    _URL,
                    tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
                    tree( URL_LITERAL, "http://foo.com" )
                )                            
            )            
        ),
        tree(
            NOVELLA,
            tree( 
                PARAGRAPH_REGULAR,
                tree( WORD_, "p" )
            ),
            tree( LINE_BREAK_ ),
            tree( LINE_BREAK_ ),
            tree( 
                LEVEL_INTRODUCER_,
                tree( LEVEL_INTRODUCER_INDENT_, "==" ),
                tree( LEVEL_TITLE, tree( WORD_, "t" ) )            
            ),
            tree( LINE_BREAK_ ),
            tree( LINE_BREAK_ ),
            tree( WHITESPACE_, "  " ),
            tree( 
                PARAGRAPH_REGULAR, 
                tree( 
                    BLOCK_INSIDE_DOUBLE_QUOTES,
                    tree( WORD_, "name" )                
                ),
                tree( WHITESPACE_, "  " ),
                tree( LINE_BREAK_ ),
                tree( URL_LITERAL, "http://foo.com")
            )
        )    
    ) ;
  }

  @Test
  public void fixUnnamedUrlAtStartOfAPart() {
    verifyFixNamedUrls( 
        tree(
            NOVELLA,
            tree( 
                PARAGRAPH_REGULAR,
                tree(
                    _URL,
                    tree( URL_LITERAL, "http://foo.com" )
                )                            
            )            
        ),
        tree(
            NOVELLA,
            tree( WHITESPACE_, "  " ),
            tree( 
                PARAGRAPH_REGULAR,
                tree( URL_LITERAL, "http://foo.com" )
                            
            )
        )
        
    ) ;
  }

  @Test
  @Ignore( "Awaiting fix")
  public void namedUrlInsideSquareBrackets() {
    verifyFixNamedUrls(
        tree(
            NOVELLA,
            tree(
                PARAGRAPH_REGULAR,
                tree(
                    BLOCK_INSIDE_SQUARE_BRACKETS,
                    tree(
                        _URL,
                        tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
                        tree( URL_LITERAL, "http://foo.com" )
                    )
                )
            )
        ),
        tree(
            NOVELLA,
            tree(
                PARAGRAPH_REGULAR,
                tree(
                    BLOCK_INSIDE_SQUARE_BRACKETS,
                    tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
                    tree( LINE_BREAK_ ),
                    tree( URL_LITERAL, "http://foo.com" )
                )
            )
        )
    ) ;
  }

  @Test
  public void namedUrlInsideParenthesis() {
    verifyFixNamedUrls(
        tree(
            NOVELLA,
            tree(
                PARAGRAPH_REGULAR,
                tree(
                    BLOCK_INSIDE_PARENTHESIS,
                    tree(
                        _URL,
                        tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
                        tree( URL_LITERAL, "http://foo.com" )
                    )
                )
            )
        ),
        tree(
            NOVELLA,
            tree(
                PARAGRAPH_REGULAR,
                tree(
                    BLOCK_INSIDE_PARENTHESIS,
                    tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
                    tree( LINE_BREAK_ ),
                    tree( URL_LITERAL, "http://foo.com" )
                )
            )
        )
    ) ;
  }

  @Test
  public void fixNamedUrlInsideAParagraphWithLineBreakBeforeQuotes() {
    verifyFixNamedUrls( 
        tree( 
            PARAGRAPH_REGULAR,
            tree( WORD_, "w" ),
            tree(
                _URL,
                tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
                tree( URL_LITERAL, "http://foo.com" )
            )                            
        ),            
        tree( 
            PARAGRAPH_REGULAR,
            tree( WORD_, "w"),
            tree( LINE_BREAK_ ),
            tree( WHITESPACE_, "  " ),
            tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
            tree( LINE_BREAK_ ),
            tree( URL_LITERAL, "http://foo.com" )
                        
        )        
    ) ;

  }

  @Test
  public void fixNamedUrlInsideAParagraphNoLineBreakBeforeQuotes() {
    verifyFixNamedUrls(
        tree(
            PARAGRAPH_REGULAR,
            tree( WORD_, "w" ),
            tree(
                _URL,
                tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
                tree( URL_LITERAL, "http://foo.com" )
            )
        ),
        tree(
            PARAGRAPH_REGULAR,
            tree( WORD_, "w"),
            tree( WHITESPACE_, " " ),
            tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
            tree( LINE_BREAK_ ),
            tree( URL_LITERAL, "http://foo.com" )

        )
    ) ;

  }

  @Test
  public void namedUrlAtTheStartOfARegularParagraph() {
    verifyFixNamedUrls(
        tree(
            PARAGRAPH_REGULAR,
            tree(
                _URL,
                tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
                tree( URL_LITERAL, "http://foo.com" )
            )
        ),
        tree(
            PARAGRAPH_REGULAR,
            tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
            tree( LINE_BREAK_ ),
            tree( URL_LITERAL, "http://foo.com" )

        )
    ) ;

  }

  @Test
  public void namedUrlAtTheStartOfParagraphAsListItemWithTripleHyphen() {
    verifyFixNamedUrls(
        tree(
            PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_,
            tree(
                _URL,
                tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
                tree( URL_LITERAL, "http://foo.com" )
            )
        ),
        tree(
            PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_,
            tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
            tree( LINE_BREAK_ ),
            tree( URL_LITERAL, "http://foo.com" )

        )
    ) ;

  }

  @Test
  public void namedUrlAtTheStartOfParagraphAsListItemWithDoubleHyphenAnNumberSign() {
    verifyFixNamedUrls(
        tree(
            PARAGRAPH_AS_LIST_ITEM_WITH_DOUBLE_HYPHEN_AND_NUMBER_SIGN,
            tree(
                _URL,
                tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
                tree( URL_LITERAL, "http://foo.com" )
            )
        ),
        tree(
            PARAGRAPH_AS_LIST_ITEM_WITH_DOUBLE_HYPHEN_AND_NUMBER_SIGN,
            tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
            tree( LINE_BREAK_ ),
            tree( URL_LITERAL, "http://foo.com" )

        )
    ) ;

  }

  @Test
  public void fixUrlWithoutNameAtStartOfAParagraph() {
    verifyFixNamedUrls( 
        tree(
            PARAGRAPH_REGULAR,
            tree(
                _URL,
                tree( URL_LITERAL, "http://foo.com" )
            )            
        ),
        tree( 
            PARAGRAPH_REGULAR,
            tree( URL_LITERAL, "http://foo.com" )
                        
        )        
    ) ;
  }

  @Test
  public void fixUrlWithoutNameInsideAParagraph() {
    verifyFixNamedUrls( 
        tree(
            PARAGRAPH_REGULAR,
            tree( WORD_, "w" ),
            tree(
                _URL,
                tree( URL_LITERAL, "http://foo.com" )
            )            
        ),
        tree( 
            PARAGRAPH_REGULAR,
            tree( WORD_, "w" ),
            tree( LINE_BREAK_ ),
            tree( URL_LITERAL, "http://foo.com" )
                        
        )        
    ) ;
  }

  @Test
  public void dontGetFooledByPreviousParagraphsInsideAngledBracketPairs() {
    verifyFixNamedUrls(
        tree(
            NOVELLA,
            tree(
                PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS,
                tree( PARAGRAPH_REGULAR, tree( WORD_, "w" ) )
            ),
            tree(
                PARAGRAPH_REGULAR,
                tree(
                    _URL,
                    tree( BLOCK_INSIDE_SQUARE_BRACKETS, tree( WORD_, "name" ) ),
                    tree( URL_LITERAL, "http://foo.com" )
                )
            )
        ),
        tree(
            NOVELLA,
            tree(
                PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS,
                tree( PARAGRAPH_REGULAR, tree( WORD_, "w" ) )
            ),
            tree(
                PARAGRAPH_REGULAR,
                tree( BLOCK_INSIDE_SQUARE_BRACKETS, tree( WORD_, "name" ) ),
                tree( LINE_BREAK_ ),
                tree( URL_LITERAL, "http://foo.com" )

            )
        )
    ) ;
  }


  /**
   * Was a bug, breaking an ArrayIndexOutOfBoundsException.
   */
  @Test
  public void detectNotSameParagraph() {
    verifyFixNamedUrls(
        tree(
            NOVELLA,
            tree(
                PARAGRAPH_REGULAR,
                tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "q" ) )

            ),
            tree(
                PARAGRAPH_REGULAR,
                tree(
                    _URL,
                    tree( URL_LITERAL, "http://foo.net" )
                )
            )
        ),
        tree(
            NOVELLA,
            tree(
                PARAGRAPH_REGULAR,
                tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "q" ) )
            ),
            tree(
                PARAGRAPH_REGULAR,
                tree( LINE_BREAK_ ),
                tree( LINE_BREAK_ ),
                tree( URL_LITERAL, "http://foo.net" )
            )
        )
    ) ;
  }



// =======
// Fixture
// =======

  private static final Logger LOGGER = LoggerFactory.getLogger( UrlManglerTest.class ) ;

  private static void verifyFixNamedUrls(
      final SyntacticTree expectedTree,
      final SyntacticTree rawTree
  ) {
    LOGGER.info( "Expected tree: ", TreeFixture.asString( expectedTree ) ) ;
    final Treepath< SyntacticTree > expectedTreepath = Treepath.create( expectedTree ) ;

    final Treepath< SyntacticTree > rehierarchized = fixNamedUrls( rawTree ) ;

    TreeFixture.assertEqualsNoSeparators(
        expectedTreepath.getTreeAtEnd(),
        rehierarchized.getTreeAtEnd()
    ) ;

  }


  private static Treepath< SyntacticTree > fixNamedUrls( final SyntacticTree rawTree ) {
    LOGGER.info( "Raw tree: ", TreeFixture.asString( rawTree ) ) ;
    final Treepath< SyntacticTree > mangledTreepath =
        UrlMangler.fixNamedUrls( Treepath.create( rawTree ) ) ;
    SyntacticTree mangledTree = mangledTreepath.getTreeAtEnd() ;
    LOGGER.info( "Mangled tree: ", TreeFixture.asString( mangledTree ) ) ;
    mangledTree = SeparatorsMangler.removeSeparators( mangledTree ) ;
    LOGGER.info( "  No separators: ", TreeFixture.asString( mangledTree ) ) ;

    return mangledTreepath ;

  }



}
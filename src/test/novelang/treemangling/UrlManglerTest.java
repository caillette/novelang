/*
 * Copyright (C) 2009 Laurent Caillette
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
package novelang.treemangling;

import org.junit.Test;
import novelang.system.LogFactory;
import novelang.system.Log;
import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import static novelang.parser.NodeKind.*;
import novelang.parser.antlr.TreeFixture;
import static novelang.parser.antlr.TreeFixture.tree;

/**
 * Tests for {@link LevelMangler}.
 * 
 * @author Laurent Caillette
 */
public class UrlManglerTest {

  private static final Log LOG = LogFactory.getLog( UrlManglerTest.class ) ;

  @Test
  public void doNothingWhenNothingToDo() {
    final SyntacticTree tree = tree(
        PART,
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
            PART,
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
            PART,
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
            PART,
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
            PART,
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
            PART,
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
            PART,
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
            PART,
            tree( 
                PARAGRAPH_REGULAR,
                tree(
                    _URL,
                    tree( URL_LITERAL, "http://foo.com" )
                )                            
            )            
        ),
        tree( 
            PART,
            tree( WHITESPACE_, "  " ),
            tree( 
                PARAGRAPH_REGULAR,
                tree( URL_LITERAL, "http://foo.com" )
                            
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
            PART,
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
            PART,
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
            PART,
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
            PART,
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

  private static void verifyFixNamedUrls(
      final SyntacticTree expectedTree,
      final SyntacticTree rawTree
  ) {
    LOG.info( "Expected tree: %s", TreeFixture.asString( expectedTree ) ) ;
    final Treepath< SyntacticTree > expectedTreepath = Treepath.create( expectedTree ) ;

    final Treepath< SyntacticTree > rehierarchized = fixNamedUrls( rawTree ) ;

    TreeFixture.assertEqualsNoSeparators(
        expectedTreepath.getTreeAtEnd(),
        rehierarchized.getTreeAtEnd()
    ); ;

  }


  private static Treepath< SyntacticTree > fixNamedUrls( final SyntacticTree rawTree ) {
    LOG.info( "Raw tree: %s", TreeFixture.asString( rawTree ) ) ;
    final Treepath< SyntacticTree > mangledTreepath =
        UrlMangler.fixNamedUrls( Treepath.create( rawTree ) ) ;
    SyntacticTree mangledTree = mangledTreepath.getTreeAtEnd() ;
    LOG.info( "Mangled tree: %s", TreeFixture.asString( mangledTree ) ) ;
    mangledTree = SeparatorsMangler.removeSeparators( mangledTree ) ;
    LOG.info( "  No separators: %s", TreeFixture.asString( mangledTree ) ) ;

    return mangledTreepath ;

  }



}
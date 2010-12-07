package org.novelang.treemangling;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.novelang.common.SyntacticTree;
import org.novelang.common.tree.Treepath;
import org.novelang.designator.Tag;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.parser.antlr.TreeFixture;

import static org.novelang.parser.NodeKind.*;
import static org.novelang.parser.antlr.TreeFixture.tree;

/**
 * Tests for {@link TagMangler}.
 *
 * @author Laurent Caillette
 */
public class TagManglerTest {


  @Test
  public void doNothingWhenNothingToDo() {
    final SyntacticTree tree = tree(
        NOVELLA,
        tree( PARAGRAPH_REGULAR ),
        tree( PARAGRAPH_REGULAR )
    );
    verifyTagMangling(tree, tree) ;
  }


  @Test
  public void oneExplicitTagOnTitle() {

    verifyTagMangling(
        tree(
            NOVELLA,
            tree(
                _LEVEL,
                tree( _EXPLICIT_TAG, "tag-1" ),
                tree( LEVEL_TITLE, tree( WORD_, "title" ) ),
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "w" )
                )
            )
        ),
        tree(
            NOVELLA,
            tree(
                _LEVEL,
                tree( TAG, "tag-1" ),
                tree( LEVEL_TITLE, tree( WORD_, "title" ) ),
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "w" )
                )
            )
        )
    ) ;
  }
  @Test
  public void dontGoIntoRawLines() {

    verifyTagMangling(
        tree(
            NOVELLA,
            tree( RAW_LINES, "xxx\nyyy\nzzz" )
        ),
        tree(
            NOVELLA,
            tree( RAW_LINES, "xxx\nyyy\nzzz" )
        )
    ) ;
  }


  @Test
  public void twoNestedLevelsWithImplicitTags() {

    verifyTagMangling(
        tree(
            NOVELLA,
            tree(
                _LEVEL,
                tree( _IMPLICIT_TAG, "title-1" ),
                tree( LEVEL_TITLE, tree( WORD_, "title-1" ) ),
                tree(
                    _LEVEL,
                    tree( _IMPLICIT_TAG, "title-2" ),
                    tree( LEVEL_TITLE, tree( WORD_, "title-2" ) ),
                    tree(
                        PARAGRAPH_REGULAR,
                        tree( WORD_, "w" )
                    )
                )
            )
        ),
        tree(
            NOVELLA,
            tree(
                _LEVEL,
                tree( LEVEL_TITLE, tree( WORD_, "title-1" ) ),
                tree(
                    _LEVEL,
                    tree( LEVEL_TITLE, tree( WORD_, "title-2" ) ),
                    tree(
                        PARAGRAPH_REGULAR,
                        tree( WORD_, "w" )
                    )
                )
            )
        )
    ) ;
  }


  @Test
  public void oneExplicitTagOnParagraph() {

    verifyTagMangling(
        tree(
            NOVELLA,
            tree(
                _LEVEL,
                tree(
                    PARAGRAPH_REGULAR,
                    tree( _EXPLICIT_TAG, "tag-1" ),
                    tree( WORD_, "w" )
                )
            )
        ),
        tree(
            NOVELLA,
            tree(
                _LEVEL,
                tree(
                    PARAGRAPH_REGULAR,
                    tree( TAG, "tag-1" ),
                    tree( WORD_, "w" )
                )
            )
        )
    ) ;
  }
@Test
  public void oneExplicitTagOnLinesOfLiteral() {

    verifyTagMangling(
        tree(
            NOVELLA,
            tree(
                _LEVEL,
                tree(
                    LINES_OF_LITERAL,
                    tree( _EXPLICIT_TAG, "tag-1" ),
                    tree( RAW_LINES, "w" )
                )
            )
        ),
        tree(
            NOVELLA,
            tree(
                _LEVEL,
                tree(
                    LINES_OF_LITERAL,
                    tree( TAG, "tag-1" ),
                    tree( RAW_LINES, "w" )
                )
            )
        )
    ) ;
  }


  @Test
  public void oneImplicitTag() {

    verifyTagMangling(
        tree(
            NOVELLA,
            tree(
                _LEVEL,
                tree( _IMPLICIT_TAG, "tag-1" ),
                tree( LEVEL_TITLE, tree( WORD_, "tag-1" ) ),
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "w" )
                )
            )
        ),
        tree(
            NOVELLA,
            tree(
                _LEVEL,
                tree( LEVEL_TITLE, tree( WORD_, "tag-1" ) ),
                tree( PARAGRAPH_REGULAR, tree(WORD_, "w" ) )
            )
        )
    ) ;
  }


  @Test
  public void twoImplicitTags() {

    verifyTagMangling(
        tree(
            NOVELLA,
            tree(
                _LEVEL,
                tree( _IMPLICIT_TAG, "tag-1" ),
                tree( _IMPLICIT_TAG, "tag-2" ),
                tree( 
                    LEVEL_TITLE, 
                    tree( WORD_, "tag-1" ), 
                    tree( PUNCTUATION_SIGN, tree( SIGN_COLON, "," ) ), 
                    tree( WORD_, "tag-2" ) 
                ),
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "w" )
                )
            )
        ),
        tree(
            NOVELLA,
            tree(
                _LEVEL,
                tree( 
                    LEVEL_TITLE, 
                    tree( WORD_, "tag-1" ), 
                    tree( PUNCTUATION_SIGN, tree( SIGN_COLON, "," ) ), 
                    tree( WORD_, "tag-2" ) 
                ),
                tree( PARAGRAPH_REGULAR, tree(WORD_, "w" ) )
            )
        )
    ) ;
  }
  
  @Test
  public void promote() {

    verifyPromotion(
        tree(
            NOVELLA,
            tree(
                _LEVEL,
                tree(
                    PARAGRAPH_REGULAR,
                    tree( _PROMOTED_TAG, "tag-1" ),
                    tree( WORD_, "w" )
                )
            )
        ),
        tree(
            NOVELLA,
            tree(
                _LEVEL,
                tree(
                    PARAGRAPH_REGULAR,
                    tree( _IMPLICIT_TAG, "tag-1" ),
                    tree( WORD_, "w" )
                )
            )
        ),
        ImmutableSet.of( new Tag( "tag-1" ) )
    ) ;    
  }



// =======
// Fixture
// =======

  private static final Logger LOGGER = LoggerFactory.getLogger( TagManglerTest.class );

  private static void verifyTagMangling(
      final SyntacticTree expectedTree,
      final SyntacticTree actualTree
  ) {
    LOGGER.info( "Expected tree: ", TreeFixture.asString( expectedTree ) ) ;
    final Treepath< SyntacticTree > expectedTreepath = Treepath.create( expectedTree ) ;

    final Treepath< SyntacticTree > rehierarchized =
        TagMangler.enhance( Treepath.create( actualTree ) ) ;

    TreeFixture.assertEqualsWithSeparators(
        expectedTreepath,
        rehierarchized
    ) ;

  }

  private static void verifyPromotion(
      final SyntacticTree expectedTree,
      final SyntacticTree actualTree,
      final Set< Tag > explicitTags
  ) {
    LOGGER.info( "Expected tree: ", TreeFixture.asString( expectedTree ) ) ;
    final Treepath< SyntacticTree > expectedTreepath = Treepath.create( expectedTree ) ;

    final Treepath< SyntacticTree > rehierarchized =
        TagMangler.promote( Treepath.create( actualTree ), explicitTags ) ;

    TreeFixture.assertEqualsWithSeparators(
        expectedTreepath,
        rehierarchized
    ) ;

  }

}

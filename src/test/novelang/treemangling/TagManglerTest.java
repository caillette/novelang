package novelang.treemangling;

import novelang.system.Log;
import novelang.system.LogFactory;
import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import static novelang.parser.antlr.TreeFixture.tree;
import novelang.parser.antlr.TreeFixture;
import static novelang.parser.NodeKind.*;
import static novelang.parser.NodeKind.WORD_;
import novelang.designator.Tag;
import org.junit.Test;

import java.util.Set;

/**
 * Tests for {@link TagMangler}.
 *
 * @author Laurent Caillette
 */
public class TagManglerTest {


  @Test
  public void doNothingWhenNothingToDo() {
    final SyntacticTree tree = tree(
        PART,
        tree( PARAGRAPH_REGULAR ),
        tree( PARAGRAPH_REGULAR )
    );
    verifyTagMangling(tree, tree) ;
  }


  @Test
  public void oneExplicitTagOnTitle() {

    verifyTagMangling(
        tree(
            PART,
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
            PART,
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
  public void dontGoIntoLinesOfLiteral() {

    verifyTagMangling(
        tree(
            PART,
            tree( LINES_OF_LITERAL, "xxx\nyyy\nzzz" )
        ),
        tree(
            PART,
            tree( LINES_OF_LITERAL, "xxx\nyyy\nzzz" )
        )
    ) ;
  }


  @Test
  public void twoNestedLevelsWithImplicitTags() {

    verifyTagMangling(
        tree(
            PART,
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
            PART,
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
            PART,
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
            PART,
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
  public void oneImplicitTag() {

    verifyTagMangling(
        tree(
            PART,
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
            PART,
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
            PART,
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
            PART,
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



// =======
// Fixture
// =======

  private static final Log LOG = LogFactory.getLog( TagManglerTest.class ) ;

  private static void verifyTagMangling(
      final SyntacticTree expectedTree,
      final SyntacticTree actualTree
  ) {
    LOG.info( "Expected tree: %s", TreeFixture.asString( expectedTree ) ) ;
    final Treepath< SyntacticTree > expectedTreepath = Treepath.create( expectedTree ) ;

    final Treepath< SyntacticTree > rehierarchized =
        TagMangler.enhance( Treepath.create( actualTree ) ) ;

    TreeFixture.assertEqualsWithSeparators(
        expectedTreepath,
        rehierarchized
    ) ;

  }

}

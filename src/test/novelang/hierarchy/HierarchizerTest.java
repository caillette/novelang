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
package novelang.hierarchy;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.parser.NodeKind;
import static novelang.parser.NodeKind.*;
import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import novelang.parser.antlr.TreeFixture;
import static novelang.parser.antlr.TreeFixture.tree;

/**
 * @author Laurent Caillette
 */
public class HierarchizerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger( HierarchizerTest.class ) ;

  @Test
  public void justMove() {
    verifyRehierarchizeFromLeftToRight(
        tree(
            PART,
            tree(
                DELIMITER_THREE_EQUAL_SIGNS_,
                tree( NodeKind.PARAGRAPH_REGULAR )
            )
        ),
        tree(
            PART,
            tree( DELIMITER_THREE_EQUAL_SIGNS_ ),
            tree( NodeKind.PARAGRAPH_REGULAR )
        ),
        DELIMITER_THREE_EQUAL_SIGNS_
    ) ;
  }

  @Test
  public void ignoreFirst() {
    verifyRehierarchizeFromLeftToRight(
        tree(
            PART,
            tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS ),
            tree(
                DELIMITER_THREE_EQUAL_SIGNS_,
                tree( NodeKind.PARAGRAPH_REGULAR )
            )
        ),
        tree(
            PART,
            tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS ),
            tree( DELIMITER_THREE_EQUAL_SIGNS_ ),
            tree( NodeKind.PARAGRAPH_REGULAR )
        ),
        DELIMITER_THREE_EQUAL_SIGNS_
    ) ;
  }

  @Test
  public void ignoreChapter() {
    verifyRehierarchizeFromLeftToRight(
        tree(
            PART,
            tree( DELIMITER_THREE_EQUAL_SIGNS_ ),
            tree(
                DELIMITER_TWO_EQUAL_SIGNS_,
                tree( NodeKind.PARAGRAPH_REGULAR )
            )
        ),
        tree(
            PART,
            tree( DELIMITER_THREE_EQUAL_SIGNS_ ),
            tree( DELIMITER_TWO_EQUAL_SIGNS_ ),
            tree( NodeKind.PARAGRAPH_REGULAR )
        ),
        DELIMITER_TWO_EQUAL_SIGNS_,
        DELIMITER_THREE_EQUAL_SIGNS_
    ) ;
  }

  @Test
  public void ignoreAndAttachAtUpperLevel() {
    final SyntacticTree expected = tree(
        PART,
        tree( NodeKind.PARAGRAPH_REGULAR ),
        tree( DELIMITER_THREE_EQUAL_SIGNS_ ),
        tree(
            DELIMITER_TWO_EQUAL_SIGNS_,
            tree( NodeKind.PARAGRAPH_REGULAR )
        ),
        tree( DELIMITER_THREE_EQUAL_SIGNS_ ),
        tree(
            DELIMITER_TWO_EQUAL_SIGNS_,
            tree( IDENTIFIER ),
            tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
        ),
        tree( DELIMITER_TWO_EQUAL_SIGNS_ )
    ) ;
    final SyntacticTree toBeRehierarchized = tree(
        PART,
        tree( NodeKind.PARAGRAPH_REGULAR ),
        tree( DELIMITER_THREE_EQUAL_SIGNS_ ),
        tree( DELIMITER_TWO_EQUAL_SIGNS_ ),
        tree( NodeKind.PARAGRAPH_REGULAR ),
        tree( DELIMITER_THREE_EQUAL_SIGNS_ ),
        tree( DELIMITER_TWO_EQUAL_SIGNS_ ),
        tree( IDENTIFIER ),
        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS ),
        tree( DELIMITER_TWO_EQUAL_SIGNS_ )
    );
    verifyRehierarchizeFromLeftToRight(
        expected,
        toBeRehierarchized,
        DELIMITER_TWO_EQUAL_SIGNS_,
        DELIMITER_THREE_EQUAL_SIGNS_
    ) ;
  }

  @Test
  public void wasABug() {
    final SyntacticTree expected = tree(
        PART,
        tree( DELIMITER_TWO_EQUAL_SIGNS_, tree( IDENTIFIER ) ),
        tree( DELIMITER_TWO_EQUAL_SIGNS_, tree( "don't touch me") )
    );
    final SyntacticTree toBeRehierarchized = tree(
        PART,
        tree( DELIMITER_TWO_EQUAL_SIGNS_ ),
        tree( IDENTIFIER ),
        tree( DELIMITER_TWO_EQUAL_SIGNS_, tree( "don't touch me") )
    );
    verifyRehierarchizeFromLeftToRight(
        expected,
        toBeRehierarchized,
        DELIMITER_TWO_EQUAL_SIGNS_,
        DELIMITER_THREE_EQUAL_SIGNS_
    ) ;
  }


  @Test
  public void aggregateList() {
    final SyntacticTree expected = tree(
        PART,
        tree( NodeKind.PARAGRAPH_REGULAR ),
        tree(
            _LIST_WITH_TRIPLE_HYPHEN,
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ )
        ),
        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
    ) ;

    final SyntacticTree toBeRehierarchized = tree(
        PART,
        tree( NodeKind.PARAGRAPH_REGULAR ),
        tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
        tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
    ) ;
    verifyRehierarchizeList( expected, toBeRehierarchized ) ;
  }

  @Test
  public void aggregateListInsideChapter() {
    final SyntacticTree expected = tree(
        PART,
        tree( DELIMITER_THREE_EQUAL_SIGNS_,
          tree( NodeKind.PARAGRAPH_REGULAR ),
          tree(
              _LIST_WITH_TRIPLE_HYPHEN,
              tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
              tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ )
          ),
          tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
        ),
        tree( DELIMITER_THREE_EQUAL_SIGNS_, tree( LINES_OF_LITERAL, "" ) )
    ) ;

    final SyntacticTree toBeRehierarchized = tree(
        PART,
        tree(
            DELIMITER_THREE_EQUAL_SIGNS_,
            tree( NodeKind.PARAGRAPH_REGULAR ),
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
            tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
        ),
        tree(
            DELIMITER_THREE_EQUAL_SIGNS_,
            tree( LINES_OF_LITERAL, "" )
        )
    ) ;
    verifyRehierarchizeList( expected, toBeRehierarchized ) ;
  }


  @Test
  public void aggregateSeveralLists() {
    final SyntacticTree expected = tree(
        PART,
        tree( NodeKind.PARAGRAPH_REGULAR ),
        tree(
            _LIST_WITH_TRIPLE_HYPHEN,
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ )
        ),
        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS ),
        tree(
            _LIST_WITH_TRIPLE_HYPHEN,
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
            tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ )
        ),
        tree( LINES_OF_LITERAL, "" )

    ) ;
    final SyntacticTree toBeRehierarchized = tree(
        PART,
        tree( NodeKind.PARAGRAPH_REGULAR ),
        tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
        tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS ),
        tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
        tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
        tree( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ),
        tree( LINES_OF_LITERAL, "" )
    ) ;
    verifyRehierarchizeList( expected, toBeRehierarchized ) ;
  }


// =======
// Fixture
// =======

  private static void verifyRehierarchizeFromLeftToRight(
      SyntacticTree expectedTree,
      SyntacticTree flatTree,
      NodeKind accumulatorKind,
      NodeKind... ignored
  ) {
    LOGGER.info( "Flat tree: " + TreeFixture.asString( flatTree ) ) ;
    LOGGER.info( "Expected tree: " + TreeFixture.asString( expectedTree ) ) ;
    final Treepath< SyntacticTree > expectedTreepath = Treepath.create( expectedTree ) ;
    final Treepath< SyntacticTree > flatTreepath = Treepath.create( flatTree ) ;

    final Treepath rehierarchized = Hierarchizer.rehierarchizeFromLeftToRight(
        flatTreepath,
        accumulatorKind,
        new Filter.ExclusionFilter( ignored )
    ) ;

    TreeFixture.assertEquals(
        expectedTreepath,
        rehierarchized
    ) ;


  }

  private static void verifyRehierarchizeList(
      SyntacticTree expectedTree,
      SyntacticTree flatTree
  ) {
    LOGGER.info( "Flat tree: " + TreeFixture.asString( flatTree ) ) ;
    LOGGER.info( "Expected tree: " + TreeFixture.asString( expectedTree ) ) ;
    final Treepath< SyntacticTree > expectedTreepath = Treepath.create( expectedTree ) ;
    final Treepath< SyntacticTree > flatTreepath = Treepath.create( flatTree ) ;

    final Treepath rehierarchized = Hierarchizer.rehierarchizeLists( flatTreepath ) ;

    TreeFixture.assertEquals(
        expectedTreepath,
        rehierarchized
    ) ;


  }

}

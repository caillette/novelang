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
import novelang.common.NodeKind;
import static novelang.common.NodeKind.*;
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
            tree( CHAPTER, tree( PARAGRAPH_PLAIN ) )
        ),
        tree(
            PART,
            tree( CHAPTER ),
            tree( PARAGRAPH_PLAIN )
        ),
        CHAPTER
    ) ;
  }

  @Test
  public void ignoreFirst() {
    verifyRehierarchizeFromLeftToRight(
        tree(
            PART,
            tree( BLOCKQUOTE ),
            tree( CHAPTER, tree( PARAGRAPH_PLAIN ) )
        ),
        tree(
            PART,
            tree( BLOCKQUOTE ),
            tree( CHAPTER ),
            tree( PARAGRAPH_PLAIN )
        ),
        CHAPTER
    ) ;
  }

  @Test
  public void ignoreChapter() {
    verifyRehierarchizeFromLeftToRight(
        tree(
            PART,
            tree( CHAPTER ),
            tree( SECTION, tree( PARAGRAPH_PLAIN ) )
        ),
        tree(
            PART,
            tree( CHAPTER ),
            tree( SECTION ),
            tree( PARAGRAPH_PLAIN )
        ),
        SECTION,
        CHAPTER
    ) ;
  }

  @Test
  public void ignoreAndAttachAtUpperLevel() {
    final SyntacticTree expected = tree(
        PART,
        tree( PARAGRAPH_PLAIN ),
        tree( CHAPTER ),
        tree( SECTION, tree( PARAGRAPH_PLAIN ) ),
        tree( CHAPTER ),
        tree( SECTION, tree( IDENTIFIER ), tree( BLOCKQUOTE ) ),
        tree( SECTION )
    );
    final SyntacticTree toBeRehierarchized = tree(
        PART,
        tree( PARAGRAPH_PLAIN ),
        tree( CHAPTER ),
        tree( SECTION ),
        tree( PARAGRAPH_PLAIN ),
        tree( CHAPTER ),
        tree( SECTION ),
        tree( IDENTIFIER ),
        tree( BLOCKQUOTE ),
        tree( SECTION )
    );
    verifyRehierarchizeFromLeftToRight(
        expected,
        toBeRehierarchized,
        SECTION,
        CHAPTER
    ) ;
  }

  @Test
  public void wasABug() {
    final SyntacticTree expected = tree(
        PART,
        tree( SECTION, tree( IDENTIFIER ) ),
        tree( SECTION, tree( "don't touch me") )
    );
    final SyntacticTree toBeRehierarchized = tree(
        PART,
        tree( SECTION ),
        tree( IDENTIFIER ),
        tree( SECTION, tree( "don't touch me") )
    );
    verifyRehierarchizeFromLeftToRight(
        expected,
        toBeRehierarchized,
        SECTION,
        CHAPTER
    ) ;
  }


  @Test
  public void aggregateSpeech() {
    final SyntacticTree expected = tree(
        PART,
        tree( PARAGRAPH_PLAIN ),
        tree(
            _SPEECH_SEQUENCE,
            tree( PARAGRAPH_SPEECH ),
            tree( PARAGRAPH_SPEECH_CONTINUED )
        ),
        tree( BLOCKQUOTE )
    ) ;

    final SyntacticTree toBeRehierarchized = tree(
        PART,
        tree( PARAGRAPH_PLAIN ),
        tree( PARAGRAPH_SPEECH ),
        tree( PARAGRAPH_SPEECH_CONTINUED ),
        tree( BLOCKQUOTE )
    ) ;
    verifyRehierarchizeSpeech( expected, toBeRehierarchized ) ;
  }

  @Test
  public void aggregateSpeechInsideChapter() {
    final SyntacticTree expected = tree(
        PART,
        tree( CHAPTER,
          tree( PARAGRAPH_PLAIN ),
          tree(
              _SPEECH_SEQUENCE,
              tree( PARAGRAPH_SPEECH ),
              tree( PARAGRAPH_SPEECH_CONTINUED )
          ),
          tree( BLOCKQUOTE )
        ),
        tree( CHAPTER, tree( LITERAL, "" ) )
    ) ;

    final SyntacticTree toBeRehierarchized = tree(
        PART,
        tree(
            CHAPTER,
            tree( PARAGRAPH_PLAIN ),
            tree( PARAGRAPH_SPEECH ),
            tree( PARAGRAPH_SPEECH_CONTINUED ),
            tree( BLOCKQUOTE )
        ),
        tree(
            CHAPTER,
            tree( LITERAL, "" )
        )
    ) ;
    verifyRehierarchizeSpeech( expected, toBeRehierarchized ) ;
  }


  @Test
  public void aggregateSeveralSpeeches() {
    final SyntacticTree expected = tree(
        PART,
        tree( PARAGRAPH_PLAIN ),
        tree(
            _SPEECH_SEQUENCE,
            tree( PARAGRAPH_SPEECH ),
            tree( PARAGRAPH_SPEECH_CONTINUED )
        ),
        tree( BLOCKQUOTE ),
        tree(
            _SPEECH_SEQUENCE,
            tree( PARAGRAPH_SPEECH ),
            tree( PARAGRAPH_SPEECH_ESCAPED ),
            tree( PARAGRAPH_SPEECH_CONTINUED )
        ),
        tree( LITERAL, "" )

    ) ;
    final SyntacticTree toBeRehierarchized = tree(
        PART,
        tree( PARAGRAPH_PLAIN ),
        tree( PARAGRAPH_SPEECH ),
        tree( PARAGRAPH_SPEECH_CONTINUED ),
        tree( BLOCKQUOTE ),
        tree( PARAGRAPH_SPEECH ),
        tree( PARAGRAPH_SPEECH_ESCAPED ),
        tree( PARAGRAPH_SPEECH_CONTINUED ),
        tree( LITERAL, "" )
    ) ;
    verifyRehierarchizeSpeech( expected, toBeRehierarchized ) ;
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
        new Hierarchizer.ExclusionFilter( ignored )
    ) ;

    TreeFixture.assertEquals(
        expectedTreepath,
        rehierarchized
    ) ;


  }

  private static void verifyRehierarchizeSpeech(
      SyntacticTree expectedTree,
      SyntacticTree flatTree
  ) {
    LOGGER.info( "Flat tree: " + TreeFixture.asString( flatTree ) ) ;
    LOGGER.info( "Expected tree: " + TreeFixture.asString( expectedTree ) ) ;
    final Treepath< SyntacticTree > expectedTreepath = Treepath.create( expectedTree ) ;
    final Treepath< SyntacticTree > flatTreepath = Treepath.create( flatTree ) ;

    final Treepath rehierarchized = Hierarchizer.rehierarchizeSpeeches( flatTreepath ) ;

    TreeFixture.assertEquals(
        expectedTreepath,
        rehierarchized
    ) ;


  }

}

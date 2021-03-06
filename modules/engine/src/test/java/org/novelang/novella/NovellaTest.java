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

package org.novelang.novella;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import org.antlr.runtime.RecognitionException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import static org.fest.assertions.Assertions.assertThat;
import static org.novelang.parser.NodeKind.*;
import static org.novelang.parser.antlr.TreeFixture.tree;

import org.novelang.ResourcesForTests;
import org.novelang.common.Location;
import org.novelang.common.Problem;
import org.novelang.common.SyntacticTree;
import org.novelang.common.filefixture.ResourceInstaller;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.parser.NodeKind;
import org.novelang.parser.SourceUnescape;
import org.novelang.parser.antlr.TreeFixture;
import org.novelang.testing.junit.MethodSupport;

/**
 * @author Laurent Caillette
 */
public class NovellaTest {


  @Test
  public void loadPartOk() throws IOException {
    final Novella novella = new Novella( resourceInstaller.copy(
            ResourcesForTests.Parts.NOVELLA_JUST_SECTIONS ) ) ;
    final SyntacticTree partTree = novella.getDocumentTree();
    Assert.assertNotNull( partTree ) ;
    final SyntacticTree expected = tree(
        NOVELLA,
        tree( 
            _LEVEL,
            tree( _IMPLICIT_TAG, "Section1novella" ),
            tree( LEVEL_TITLE, tree( WORD_, "Section1novella" ) ),
            tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "p00" ), tree( WORD_, "w001" ) )
        ),
        tree( 
            _LEVEL,
            tree( _IMPLICIT_TAG, "section1W11" ),
            tree( LEVEL_TITLE, tree( WORD_, "section1" ), tree( WORD_, "w11" ) ),
            tree(
                PARAGRAPH_REGULAR,
                tree( WORD_, "p10" ),
                tree( WORD_, "w101" ),
                tree( WORD_, "w102" )
            )
        )
    ) ;

    TreeFixture.assertEqualsNoSeparators( expected, partTree ) ;
    Assert.assertFalse( novella.getProblems().iterator().hasNext() ) ;
  }

  @Test
  public void partWithMissingImagesHasProblem() throws IOException {
    final File partFile = resourceInstaller.copy( ResourcesForTests.Parts.NOVELLA_MISSING_IMAGES ) ;
    final Novella novella = new Novella( partFile ) ;
    novella.relocateResourcePaths( partFile.getParentFile() ) ;
    Assert.assertTrue( novella.hasProblem() ) ;
    final List< Problem > problems = Lists.newArrayList( novella.getProblems() ) ;
    LOGGER.debug( "Got problems: ", problems ) ;
    Assert.assertEquals( 2, problems.size() ) ;

  }

  @Test
  public void badCharacterCorrectlyShownInProblem() throws IOException {
    final Novella novella = new Novella( "b\u00A4d" ) ;
    Assert.assertTrue( novella.hasProblem() ) ;
    final List< Problem > problems = Lists.newArrayList( novella.getProblems() ) ;
    LOGGER.debug( "Got problems: ", problems ) ;
    Assert.assertEquals(
        "No viable alternative at input '\u00A4' CURRENCY_SIGN [0x00A4]",
        novella.getProblems().iterator().next().getMessage()
    ) ;

  }

  @Test
  public void loadPartWithMetadata() throws IOException {
    final Novella novella = new Novella( resourceInstaller.copy(
        ResourcesForTests.Parts.NOVELLA_JUST_SECTIONS ) ).makeStandalone() ;
    final SyntacticTree partTree = novella.getDocumentTree();
    Assert.assertNotNull( partTree ) ;
    final SyntacticTree expected = tree( NOVELLA,
        tree( _META,
            tree( _WORD_COUNT, "8" )
        ),        
        tree( 
            _LEVEL,
            tree( _IMPLICIT_IDENTIFIER, "Section1novella" ),
            tree( _IMPLICIT_TAG, "Section1novella" ),
            tree( LEVEL_TITLE, tree( WORD_, "Section1novella" ) ),
            tree( PARAGRAPH_REGULAR, tree( WORD_, "p00" ), tree( WORD_, "w001" ) )
        ),
        tree( 
            _LEVEL,
            tree( _IMPLICIT_IDENTIFIER, "section1W11" ),
            tree( _IMPLICIT_TAG, "section1W11" ),
            tree( LEVEL_TITLE, tree( WORD_, "section1" ), tree( WORD_, "w11" ) ),
            tree(
                PARAGRAPH_REGULAR,
                tree( WORD_, "p10" ),
                tree( WORD_, "w101" ),
                tree( WORD_, "w102" )
            )
        )
    ) ;

    TreeFixture.assertEqualsNoSeparators( expected, partTree ) ;
    Assert.assertFalse( novella.getProblems().iterator().hasNext() ) ;
  }

  /**
   * Checks that a single Novella file gets rehierarchized.
   * @throws IOException
   */
  @Test
  public void loadSimpleStructure() throws IOException {
    final Novella novella = new Novella( resourceInstaller.copy(
        ResourcesForTests.Parts.NOVELLA_SIMPLE_STRUCTURE ) ) ;
    final SyntacticTree partTree = novella.getDocumentTree();
    Assert.assertNotNull( partTree ) ;
    final SyntacticTree expected = tree(
        NOVELLA,
        tree( 
            _LEVEL,
            tree( _IMPLICIT_TAG, "Chapter-0" ),
            tree( LEVEL_TITLE, tree( WORD_, "Chapter-0" ) ),
            tree(
                _LEVEL,
                tree( _IMPLICIT_TAG, "Section-0-0" ),
                tree( LEVEL_TITLE, tree( WORD_, "Section-0-0" ) ),
                tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "Paragraph-0-0-0" ) ),
                tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "Paragraph-0-0-1" ) )
            ),
            tree( 
                _LEVEL,
                tree( _IMPLICIT_TAG, "Section-0-1" ),
                tree( LEVEL_TITLE, tree( WORD_, "Section-0-1" ) ),
                tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "Paragraph-0-1-0" ) )
                
            )
        ),
        tree( 
            _LEVEL,
            tree( _IMPLICIT_TAG, "Chapter-1" ),
            tree( LEVEL_TITLE, tree( WORD_, "Chapter-1" ) ),
            tree( 
                _LEVEL,
                tree( _IMPLICIT_TAG, "Section-1-0" ),
                tree( LEVEL_TITLE, tree( WORD_, "Section-1-0" ) ),
                tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "Paragraph-1-0-0" ) ),
                tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "Paragraph-1-0-1" ) )           
            ),
            tree( 
                _LEVEL,
                tree( _IMPLICIT_TAG, "Section-1-1" ),
                tree( LEVEL_TITLE, tree( WORD_, "Section-1-1" ) ),
                tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "Paragraph-1-1-0" ) )
            )
        )
    ) ;

    TreeFixture.assertEqualsNoSeparators( expected, partTree ) ;
    Assert.assertFalse( novella.getProblems().iterator().hasNext() ) ;

  }
  

  @Test
  public void partWithParsingErrorDoesNotAttemptToCountWords() {
    final Novella novella = NovellaFixture.createStandaloneNovella( "````" ) ;
    Assert.assertTrue( novella.hasProblem() ) ;
  }

  @Test
  public void problemWithBadEscapeCodeHasLocation() {
    final Novella novella = new Novella(
        "\n" +
        "..." + SourceUnescape.ESCAPE_START + "unknown-escape-code" + SourceUnescape.ESCAPE_END
    ) ;
    Assert.assertTrue( novella.hasProblem() ) ;
    final Iterator<Problem> problems = novella.getProblems().iterator();
    final Problem problem = problems.next() ;
    Assert.assertFalse( problems.hasNext() ) ;
    Assert.assertEquals( 2, problem.getLocation().getLine() ) ;
    Assert.assertEquals( 4, problem.getLocation().getColumn() ) ;
  }


  @Test( timeout = TEST_TIMEOUT_MILLISECONDS )
  public void problemWithSeparatorsAndEmbeddedLists() {
    final Novella novella = new Novella( "- y `z`" ) ;
    final SyntacticTree expected = tree(
        NOVELLA,
        tree(
            PARAGRAPH_REGULAR,
            tree(
                _EMBEDDED_LIST_WITH_HYPHEN,
                tree(
                    _EMBEDDED_LIST_ITEM,
                    tree( WORD_, "y" ),
                    tree( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS, "z" )
                )
            )
        )
    ) ;
    final SyntacticTree partTree = novella.getDocumentTree() ;
    TreeFixture.assertEqualsWithSeparators( expected, partTree ) ;
  }


  @Test( timeout = TEST_TIMEOUT_MILLISECONDS )
  public void problemWithSeparatorsAndTitle() {
    final Novella novella = new Novella( "== y `z`" ) ;
    final SyntacticTree expected = tree(
        NOVELLA,
        tree(
            _LEVEL,
            tree( _IMPLICIT_TAG, "yZ" ),
            tree(
                LEVEL_TITLE,
                tree( WORD_, "y" ),
                tree( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS, "z" )
            )
        )
    ) ;
    final SyntacticTree partTree = novella.getDocumentTree() ;
    TreeFixture.assertEqualsWithSeparators( expected, partTree ) ;
  }
  
  @Test( timeout = TEST_TIMEOUT_MILLISECONDS )
  public void dontCalculateImpossibleIdentifier() {
    final Novella novella = new Novella( "== ..." ) ;
    final SyntacticTree expected = tree(
        NOVELLA,
        tree( _META,
            tree( _WORD_COUNT, "0" )
        ),
        tree(
            _LEVEL,
            tree(
                LEVEL_TITLE,
                tree( PUNCTUATION_SIGN, tree( SIGN_ELLIPSIS, "..." ) )
            )
        )
    ) ;
    final SyntacticTree partTree = novella.makeStandalone().getDocumentTree() ;
    TreeFixture.assertEqualsWithSeparators( expected, partTree ) ;
  }

  @Test
  public void dontLoseLocationDuringLevelMangling() throws RecognitionException {
    
    final Novella novella = new Novella(
        "\n" +
        "\n" +
        "== Lz\u00E9ro" + "\n" + // '\u00E9' == 'é' Makes tag appear different, debugging easier.
        "\n" +
        "p0"
    ) ;
    final SyntacticTree expected = tree(
        NOVELLA,
        new Location( "<String>", 1, 0 ),
        tree(
            _LEVEL, 
            new Location( "<String>", 3, 0 ),
            tree( _IMPLICIT_TAG, "Lzero" ),
            tree(
                LEVEL_TITLE,
                new Location( "<String>" ),
                tree(
                    WORD_,
                    new Location( "<String>" ),
                    "Lz\u00E9ro"
                )
            ),
            tree(
                PARAGRAPH_REGULAR,
                new Location( "<String>", 5, 0 ),
                tree(
                    WORD_,
                    new Location( "<String>" ),
                    "p0" 
                ) 
            )
        )
    ) ;
    final SyntacticTree partTree = novella.getDocumentTree() ;
    TreeFixture.assertEquals( expected, partTree, true ) ;

  }

  

  @Test
  public void loadPartUtf8WithBom() throws IOException {
    final Novella novella = new Novella( resourceInstaller.copy(
            ResourcesForTests.Parts.NOVELLA_UTF8_BOM ) ) ;
    Assert.assertFalse( novella.getProblems().iterator().hasNext() ) ;
  }


  @Test
  public void rewriteEarlyExitCountedAsAProblem() throws IOException {
    final Novella novella = new Novella( "\"\"" ) ;
    assertThat( novella.getProblems() ).hasSize( 1 ) ;
    final Problem problem = novella.getProblems().iterator().next() ;
    assertThat( problem.getLocation().isPositionDefined() ).isTrue() ;
  }


// =======
// Fixture
// =======

  private static final Logger LOGGER = LoggerFactory.getLogger( NovellaTest.class ) ;

  private static final int TEST_TIMEOUT_MILLISECONDS = 10 * 60 * 1000 ;

  static {
      ResourcesForTests.initialize() ;
  }

  @Rule
  public final MethodSupport methodSupport = new MethodSupport() ;

  private final ResourceInstaller resourceInstaller = new ResourceInstaller( methodSupport ) ;



  private static final SyntacticTree $FULLSTOP$ = tree( PUNCTUATION_SIGN, tree( SIGN_FULLSTOP, "." ) );
  private static final SyntacticTree $COMMA$ = tree( PUNCTUATION_SIGN, tree( SIGN_COMMA, "," ) );

}

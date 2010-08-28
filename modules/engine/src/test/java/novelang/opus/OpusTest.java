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
package novelang.opus;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import novelang.ResourcesForTests;
import novelang.common.Problem;
import novelang.common.SyntacticTree;
import novelang.common.filefixture.JUnitAwareResourceInstaller;
import novelang.common.filefixture.Resource;
import novelang.logger.Logger;
import novelang.logger.LoggerFactory;
import novelang.parser.antlr.TreeFixture;
import org.apache.commons.lang.SystemUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.NameAwareTestClassRunner;

import static novelang.ResourcesForTests.initialize;
import static novelang.parser.NodeKind.*;
import static novelang.parser.antlr.TreeFixture.tree;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link Opus} and also built-in functions.
 * 
 * @author Laurent Caillette
 */
@SuppressWarnings( { "HardcodedFileSeparator" } )
@RunWith( value = NameAwareTestClassRunner.class )
public class OpusTest {

  /**
   * Test that some parsing error produces a Problem.
   */
  @Test
  public void badCommandGeneratesProblem() {
    final File oneWordFile = resourceInstaller.copy( ResourcesForTests.Parts.NOVELLA_ONE_WORD ) ;

    final Opus opus = OpusTestTools.createBook(
        SystemUtils.getUserDir(),
        "insert file:" + oneWordFile.getAbsolutePath() + " $recurse" // old syntax
    ) ;
    assertTrue( opus.hasProblem() ) ;

  }

  /**
   * Test {@link novelang.opus.function.builtin.InsertCommand}.
   */
  @Test
  public void justInsert() {
    final File oneWordFile = resourceInstaller.copy( ResourcesForTests.Parts.NOVELLA_ONE_WORD ) ;

    final String absoluteFilePath = oneWordFile.getAbsolutePath().replace( '\\', '/' ) ;
    final Opus opus = OpusTestTools.createBook(
        SystemUtils.getUserDir(),
        "insert file:" + absoluteFilePath
    ) ;
    final SyntacticTree bookTree = opus.getDocumentTree() ;
    TreeFixture.assertEqualsNoSeparators(
        tree( OPUS,
            tree( _META, tree( _WORD_COUNT, "1" ) ),
            tree(
                PARAGRAPH_REGULAR,
                tree( WORD_, "oneword" )
            )
        ),
        bookTree
    ) ;
    assertFalse( opus.hasProblem() ) ;

  }

  /**
   * Test {@link novelang.opus.function.builtin.InsertCommand}.
   */
  @Test
  public void insertWithRecursiveFileScan() throws IOException {
    resourceInstaller.copy( ResourcesForTests.Scanned.dir ) ;
    final File scannedBookNoStyle =
        resourceInstaller.createFileObject( ResourcesForTests.Scanned.BOOK ) ;

    final Opus opus = OpusTestTools.createBook( scannedBookNoStyle ) ;
    LOGGER.debug( "Opus's document tree: ", opus.getDocumentTree().toStringTree() ) ;

    final SyntacticTree bookTree = opus.getDocumentTree() ;
    TreeFixture.assertEqualsNoSeparators(
        tree( OPUS,
            tree( _META, tree( _WORD_COUNT, "6" ) ),
            tree(
                _LEVEL,
                tree( LEVEL_TITLE, tree( WORD_, "file1" ) ),
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "content-of-file1" )
                )
            ),
            tree(
                _LEVEL,
                tree( LEVEL_TITLE, tree( WORD_, "file2" ) ),
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "content-of-file2" )
                )
            ),
            tree(
                _LEVEL,
                tree( LEVEL_TITLE, tree( WORD_, "file3" ) ),
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "content-of-file3" )
                )
            )
        ),
        bookTree
    ) ;
    assertFalse( opus.hasProblem() ) ;


  }

  /**
   * Test {@link novelang.opus.function.builtin.InsertCommand}.
   */
  @Test
  public void insertWithFlatFileScan() throws IOException {
    resourceInstaller.copy( ResourcesForTests.Scanned.dir ) ;
    final File scannedBookNoStyleNoRecurse = resourceInstaller.createFileObject(
        ResourcesForTests.Scanned.BOOK_NORECURSE ) ;

    final Opus opus = OpusTestTools.createBook( scannedBookNoStyleNoRecurse ) ;
    LOGGER.debug( "Opus's document tree: ", opus.getDocumentTree().toStringTree() ) ;

    final SyntacticTree bookTree = opus.getDocumentTree() ;
    TreeFixture.assertEqualsNoSeparators(
        tree(
            OPUS,
            tree( _META, tree( _WORD_COUNT, "2" ) ),
            tree(
                PARAGRAPH_REGULAR,
                tree( WORD_, "content-of-file1" )
            ),
            tree(
                PARAGRAPH_REGULAR,
                tree( WORD_, "content-of-file2" )
            )
        ),
        bookTree
    ) ;
    assertFalse( opus.hasProblem() ) ;


  }


  /**
   * Test {@link novelang.opus.function.builtin.InsertCommand}.
   */
  @Test
  public void insertWithFileScanAndStyle() throws IOException {
    resourceInstaller.copy( ResourcesForTests.Scanned.dir ) ;
    final File scannedBookWithStyle = resourceInstaller.createFileObject(
        ResourcesForTests.Scanned.BOOK_WITHSTYLE ) ;

    final Opus opus = OpusTestTools.createBook( scannedBookWithStyle );
    LOGGER.debug( "Opus's document tree: ", opus.getDocumentTree().toStringTree() ) ;

    final SyntacticTree bookTree = opus.getDocumentTree() ;
    TreeFixture.assertEqualsNoSeparators(
        tree( OPUS,
            tree( _META, tree( _WORD_COUNT, "6" ) ),
            tree(
                _LEVEL,
                tree( _STYLE, tree( CUSTOM_STYLE ) ),
                tree( LEVEL_TITLE, tree( WORD_, "file1" ) ),
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "content-of-file1" )
                )
            ),
            tree(
                _LEVEL,
                tree( _STYLE, tree( CUSTOM_STYLE ) ),
                tree( LEVEL_TITLE, tree( WORD_, "file2" ) ),
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "content-of-file2" )
                )
            ),
            tree(
                _LEVEL,
                tree( _STYLE, tree( CUSTOM_STYLE ) ),
                tree( LEVEL_TITLE, tree( WORD_, "file3" ) ),
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "content-of-file3" )
                )
            )
        ),
        bookTree
    ) ;
    assertFalse( opus.hasProblem() ) ;
  }



  /**
   * Test {@link novelang.opus.function.builtin.InsertCommand}.
   */
  @Test
  public void insertWithBadPart() throws IOException {
    resourceInstaller.copy( ResourcesForTests.Served.BROKEN_NOVELLA ) ;
    final File scannedBookWithBadPart =
        resourceInstaller.copy( ResourcesForTests.Served.BROKEN_BOOK_BAD_SCANNED_NOVELLA ) ;

    final Opus opus = OpusTestTools.createBook( scannedBookWithBadPart ) ;
    LOGGER.debug( "Opus's document tree: ", opus.getDocumentTree().toStringTree() ) ;

    assertTrue( opus.hasProblem() ) ;

  }


  /**
   * Test {@link novelang.opus.function.builtin.InsertCommand} and empty Novella detection
   * in {@link novelang.common.AbstractSourceReader}.
   */
  @Test
  public void insertEmptyPart() throws IOException {
    final Resource emptyPartResource = ResourcesForTests.BookWithEmptyPart.EMPTY_NOVELLA;
    resourceInstaller.copy( emptyPartResource ) ;
    final File bookFile = resourceInstaller.copy( ResourcesForTests.BookWithEmptyPart.BOOK ) ;

    final Opus opus = OpusTestTools.createBook( bookFile ) ;

    final Iterator< Problem > problems = opus.getProblems().iterator() ;
    assertTrue( problems.hasNext() ) ;
    final Problem problem = problems.next() ;
    assertTrue( problem.getMessage().contains( "Novella is empty" ) ) ;
    assertTrue( problem.getLocation().getFileName().contains( emptyPartResource.getBaseName() ) ) ;
    assertFalse( problems.hasNext() ) ;

  }


  /**
   * Test {@link novelang.opus.function.builtin.InsertCommand}.
   */
  @Test
  public void detectMissingImage() throws IOException {
    resourceInstaller.copy( ResourcesForTests.MissingImages.MISSING_IMAGE_PART ) ;
    final File scannedBookWithBadImage =
        resourceInstaller.copy( ResourcesForTests.MissingImages.MISSING_IMAGE_BOOK ) ;

    final Opus opus = OpusTestTools.createBook( scannedBookWithBadImage ) ;
    LOGGER.debug( "Opus's document tree: ", opus.getDocumentTree().toStringTree() ) ;

    assertTrue( opus.hasProblem() ) ;

  }


  /**
   * Test {@link novelang.opus.function.builtin.InsertCommand}.
   */
  @Test
  public void insertWithExplicitIdentifiers() throws IOException {
    resourceInstaller.copyWithPath( ResourcesForTests.Identifiers.BOOK_1 ) ;
    resourceInstaller.copyWithPath( ResourcesForTests.Identifiers.NOVELLA_1 ) ;
    final File bookWithIdentifier =
        resourceInstaller.createFileObject( ResourcesForTests.Identifiers.BOOK_1 ) ;

    final Opus opus = OpusTestTools.createBook( bookWithIdentifier ) ;
    LOGGER.debug( "Opus's document tree: ", opus.getDocumentTree().toStringTree() ) ;

    final SyntacticTree bookTree = opus.getDocumentTree() ;
    TreeFixture.assertEqualsNoSeparators(
        tree( OPUS,
            tree( _META, tree( _WORD_COUNT, "2" ) ),
            tree(
                _LEVEL,
                tree( _EXPLICIT_IDENTIFIER, tree( "\\\\IdentifierOne\\IdentifierTwo" ) ),
                tree( _IMPLICIT_TAG, "LevelTwo" ),
                tree( LEVEL_TITLE, tree( WORD_, "LevelTwo" ) ),
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "Paragraph" )
                )
            )
        ),
        bookTree
    ) ;
    assertFalse( opus.hasProblem() ) ;
  }


  /**
   * Test {@link novelang.opus.function.builtin.InsertCommand}.
   */
  @Test
  public void insertWithPromotedTags() throws IOException {
    final File bookWithTags =
        resourceInstaller.copyWithPath( ResourcesForTests.TaggedPart.PROMOTED_TAGS_BOOK ) ;
    resourceInstaller.copyWithPath( ResourcesForTests.TaggedPart.PROMOTED_TAGS_PART_1 ) ;

    final Opus opus = OpusTestTools.createBook( bookWithTags ) ;
    LOGGER.debug( "Opus's document tree: ", opus.getDocumentTree().toStringTree() ) ;
    assertFalse( opus.hasProblem() ) ;

    final SyntacticTree bookTree = opus.getDocumentTree() ;
    TreeFixture.assertEqualsNoSeparators(
        tree( OPUS,
            tree( 
                _META, 
                tree( _WORD_COUNT, "7" ),
                tree(
                    _TAGS,
                    tree( _EXPLICIT_TAG, "Bar" ),
                    tree( _EXPLICIT_TAG, "Foo" ) 
                )
            ),
            tree(
                _LEVEL,
                tree( _IMPLICIT_IDENTIFIER, tree( "\\\\FooAndBar" ) ),
                tree( _EXPLICIT_TAG, "Bar" ),
                tree( _EXPLICIT_TAG, "Foo" ),
                tree( LEVEL_TITLE, tree( WORD_, "Foo" ), tree( WORD_, "and" ), tree( WORD_, "Bar" ) ),
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "y" ),
                    tree( APOSTROPHE_WORDMATE, "'" ),
                    tree( _PRESERVED_WHITESPACE ),
                    tree( WORD_, "z" ),
                    tree( PUNCTUATION_SIGN, tree( SIGN_FULLSTOP, "." ) )
                )
            ),
            tree(
                _LEVEL,
                tree( _IMPLICIT_IDENTIFIER, tree( "\\\\Foo_Bar" ) ),
                tree( _PROMOTED_TAG, "Bar" ),
                tree( _PROMOTED_TAG, "Foo" ),
                tree(
                    LEVEL_TITLE,
                    tree( WORD_, "Foo" ),
                    tree( PUNCTUATION_SIGN, tree( SIGN_COMMA, "," ) ),
                    tree( WORD_, "Bar" )
                )
            )
        ),
        bookTree
    ) ;
  }



  /**
   * Test {@link novelang.opus.function.builtin.InsertCommand}.
   */
  @Test
  public void insertWithImplicitIdentifiers() throws IOException {
    final File bookWithIdentifier =
        resourceInstaller.copyWithPath( ResourcesForTests.Identifiers.BOOK_2 ) ;
    resourceInstaller.copyWithPath( ResourcesForTests.Identifiers.NOVELLA_2 ) ;

    final Opus opus = OpusTestTools.createBook( bookWithIdentifier ) ;
    LOGGER.debug( "Opus's document tree: ", opus.getDocumentTree().toStringTree() ) ;

    final SyntacticTree bookTree = opus.getDocumentTree() ;
    TreeFixture.assertEqualsNoSeparators(
        tree( OPUS,
            tree( _META, tree( _WORD_COUNT, "6" ) ),
            tree(
                _LEVEL,
                tree( _IMPLICIT_TAG, "L0-1" ),
                tree( LEVEL_TITLE, tree( WORD_, "L0-1" ) ),
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "p0-1" )
                )
            ),
            tree(
                _LEVEL,
                tree( _IMPLICIT_IDENTIFIER, tree( "\\\\L1" ) ),
                tree( _IMPLICIT_TAG, "L1" ),
                tree( LEVEL_TITLE, tree( WORD_, "L1" ) ),
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "p1" )
                )
            ),
            tree(
                _LEVEL,
                tree( _IMPLICIT_TAG, "L0-1" ),
                tree( LEVEL_TITLE, tree( WORD_, "L0-1" ) ),
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "p0-1" )
                )
            )
        ),
        bookTree
    ) ;
    assertFalse( opus.hasProblem() ) ;
  }


  /**
   * Test {@link novelang.opus.function.builtin.InsertCommand}.
   */
  @Test
  public void insertWithCollidingIdentifiers() throws IOException {
    final File bookWithIdentifier =
        resourceInstaller.copy( ResourcesForTests.Identifiers.BOOK_4 ) ;
    resourceInstaller.copy( ResourcesForTests.Identifiers.Subdirectory4.dir ) ;

    final Opus opus = OpusTestTools.createBook( bookWithIdentifier ) ;
    LOGGER.debug( "Opus's document tree: ", opus.getDocumentTree().toStringTree() ) ;

    final SyntacticTree bookTree = opus.getDocumentTree() ;
    TreeFixture.assertEqualsNoSeparators(
        tree( OPUS,
            tree( _META, tree( _WORD_COUNT, "4" ) ),
            tree(
                _LEVEL,
                tree( _COLLIDING_EXPLICIT_IDENTIFIER, "\\\\Collider" ),
                tree( _IMPLICIT_TAG, "L0" ),
                tree( LEVEL_TITLE, tree( WORD_, "L0" ) ),
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "p0" )
                )
            ),
            tree(
                _LEVEL,
                tree( _COLLIDING_EXPLICIT_IDENTIFIER, "\\\\Collider" ),
                tree( _IMPLICIT_TAG, "L1" ),
                tree( LEVEL_TITLE, tree( WORD_, "L1" ) ),
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "p1" )
                )
            )
        ),
        bookTree
    ) ;
    assertFalse( opus.hasProblem() ) ;
  }


  /**
   * Test {@link novelang.opus.function.builtin.InsertCommand}.
   */
  @Test
  public void insertWithRecurseShouldKeepImplicitIdentifiers() throws IOException {
    final File bookWithIdentifier =
        resourceInstaller.copyWithPath( ResourcesForTests.Identifiers.BOOK_3_RECURSE ) ;
    resourceInstaller.copyWithPath( ResourcesForTests.Identifiers.Subdirectory3.PART_3 ) ;

    verifyBook3( bookWithIdentifier );
  }

  /**
   * Test {@link novelang.opus.function.builtin.InsertCommand}.
   */
  @Test
  public void insertShouldKeepImplicitIdentifiers() throws IOException {
    final File bookWithIdentifier =
        resourceInstaller.copyWithPath( ResourcesForTests.Identifiers.BOOK_3_STRAIGHT ) ;
    resourceInstaller.copyWithPath( ResourcesForTests.Identifiers.Subdirectory3.PART_3 ) ;

    verifyBook3( bookWithIdentifier );
  }

// =======
// Fixture
// =======

  static {
    initialize() ;
  }

  private static final Logger LOGGER = LoggerFactory.getLogger( OpusTest.class );

  private final JUnitAwareResourceInstaller resourceInstaller = new JUnitAwareResourceInstaller() ;

  public static final String CUSTOM_STYLE = "mystyle" ;


  private static void verifyBook3( final File bookWithIdentifier ) throws IOException {
    final Opus opus = OpusTestTools.createBook( bookWithIdentifier ) ;
    LOGGER.debug( "Opus's document tree: ", opus.getDocumentTree().toStringTree() ) ;

    final SyntacticTree bookTree = opus.getDocumentTree() ;
    TreeFixture.assertEqualsNoSeparators(
        tree( OPUS,
            tree( _META, tree( _WORD_COUNT, "8" ) ),
            tree(
                _LEVEL,
                tree( _IMPLICIT_IDENTIFIER, tree( "\\\\L0" ) ),
                tree( _IMPLICIT_TAG, "L0" ),
                tree( LEVEL_TITLE, tree( WORD_, "L0" ) ),
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "p0" )
                ),
                tree(     
                    _LEVEL,
                    tree( _IMPLICIT_IDENTIFIER, tree( "\\\\L0-0" ) ),
                    tree( _IMPLICIT_TAG, "L0-0" ),
                    tree( LEVEL_TITLE, tree( WORD_, "L0-0" ) ),
                    tree(
                        PARAGRAPH_REGULAR,
                        tree( WORD_, "p0-0" )
                    )
                ),
                tree(     
                    _LEVEL,
                    tree( _IMPLICIT_IDENTIFIER, tree( "\\\\L0-1" ) ),
                    tree( _IMPLICIT_TAG, "L0-1" ),
                    tree( LEVEL_TITLE, tree( WORD_, "L0-1" ) ),
                    tree(
                        PARAGRAPH_REGULAR,
                        tree( WORD_, "p0-1" )
                    )
                )
                
            ),
            tree(
                _LEVEL,
                tree( _IMPLICIT_IDENTIFIER, tree( "\\\\L1" ) ),
                tree( _IMPLICIT_TAG, "L1" ),
                tree( LEVEL_TITLE, tree( WORD_, "L1" ) ),
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "p1" )
                )
            )
        ),
        bookTree
    ) ;
    assertFalse( opus.hasProblem() ) ;
  }




}

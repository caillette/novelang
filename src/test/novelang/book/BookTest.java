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
package novelang.book;

import novelang.TestResourceTree;
import static novelang.TestResourceTree.initialize;
import novelang.common.SyntacticTree;
import novelang.common.filefixture.JUnitAwareResourceInstaller;
import static novelang.parser.NodeKind.*;
import novelang.parser.antlr.TreeFixture;
import static novelang.parser.antlr.TreeFixture.tree;
import novelang.system.Log;
import novelang.system.LogFactory;

import org.apache.commons.lang.SystemUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.NameAwareTestClassRunner;

import java.io.File;
import java.io.IOException;

/**
 * Test for {@link Book} and also built-in functions.
 * 
 * @author Laurent Caillette
 */
@RunWith( value = NameAwareTestClassRunner.class )
public class BookTest {

  /**
   * Test that some parsing error produces a Problem.
   */
  @Test
  public void badCommandGeneratesProblem() {
    final File oneWordFile = resourceInstaller.copy( TestResourceTree.Parts.PART_ONE_WORD ) ;

    final Book book = BookTestTools.createBook(
        SystemUtils.getUserDir(),
        "insert file:" + oneWordFile.getAbsolutePath() + " $recurse" // old syntax
    ) ;
    Assert.assertTrue( book.hasProblem() ) ;

  }

  /**
   * Test {@link novelang.book.function.builtin.InsertCommand}.
   */
  @Test
  public void justInsert() {
    final File oneWordFile = resourceInstaller.copy( TestResourceTree.Parts.PART_ONE_WORD ) ;

    final String absoluteFilePath = oneWordFile.getAbsolutePath().replace( '\\', '/' ) ;
    final Book book = BookTestTools.createBook(
        SystemUtils.getUserDir(),
        "insert file:" + absoluteFilePath
    ) ;
    final SyntacticTree bookTree = book.getDocumentTree() ;
    TreeFixture.assertEqualsNoSeparators(
        tree( BOOK,
            tree( _META, tree( _WORD_COUNT, "1" ) ),
            tree(
                PARAGRAPH_REGULAR,
                tree( WORD_, "oneword" )
            )
        ),
        bookTree
    ) ;
    Assert.assertFalse( book.hasProblem() ) ;

  }

  /**
   * Test {@link novelang.book.function.builtin.InsertCommand}.
   */
  @Test
  public void insertWithRecursiveFileScan() throws IOException {
    resourceInstaller.copy( TestResourceTree.Scanned.dir ) ;
    final File scannedBookNoStyle = resourceInstaller.createFileObject( TestResourceTree.Scanned.BOOK ) ;

    final Book book = BookTestTools.createBook( scannedBookNoStyle ) ;
    LOG.debug( "Book's document tree: %s", book.getDocumentTree().toStringTree() ) ;

    final SyntacticTree bookTree = book.getDocumentTree() ;
    TreeFixture.assertEqualsNoSeparators(
        tree( BOOK,
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
    Assert.assertFalse( book.hasProblem() ) ;


  }

  /**
   * Test {@link novelang.book.function.builtin.InsertCommand}.
   */
  @Test
  public void insertWithFlatFileScan() throws IOException {
    resourceInstaller.copy( TestResourceTree.Scanned.dir ) ;
    final File scannedBookNoStyleNoRecurse = resourceInstaller.createFileObject(
        TestResourceTree.Scanned.BOOK_NORECURSE ) ;

    final Book book = BookTestTools.createBook( scannedBookNoStyleNoRecurse ) ;
    LOG.debug( "Book's document tree: %s", book.getDocumentTree().toStringTree() ) ;

    final SyntacticTree bookTree = book.getDocumentTree() ;
    TreeFixture.assertEqualsNoSeparators(
        tree( 
            BOOK,
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
    Assert.assertFalse( book.hasProblem() ) ;


  }


  /**
   * Test {@link novelang.book.function.builtin.InsertCommand}.
   */
  @Test
  public void insertWithFileScanAndStyle() throws IOException {
    resourceInstaller.copy( TestResourceTree.Scanned.dir ) ;
    final File scannedBookWithStyle = resourceInstaller.createFileObject(
        TestResourceTree.Scanned.BOOK_WITHSTYLE ) ;

    final Book book = BookTestTools.createBook( scannedBookWithStyle );
    LOG.debug( "Book's document tree: %s", book.getDocumentTree().toStringTree() ) ;

    final SyntacticTree bookTree = book.getDocumentTree() ;
    TreeFixture.assertEqualsNoSeparators(
        tree( BOOK,
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
    Assert.assertFalse( book.hasProblem() ) ;
  }



  /**
   * Test {@link novelang.book.function.builtin.InsertCommand}.
   */
  @Test
  public void insertWithBadPart() throws IOException {
    resourceInstaller.copy( TestResourceTree.Served.BROKEN_PART ) ;
    final File scannedBookWithBadPart =
        resourceInstaller.copy( TestResourceTree.Served.BROKEN_BOOK_BAD_SCANNED_PART ) ;

    final Book book = BookTestTools.createBook( scannedBookWithBadPart ) ;
    LOG.debug( "Book's document tree: %s", book.getDocumentTree().toStringTree() ) ;

    Assert.assertTrue( book.hasProblem() ) ;

  }


  /**
   * Test {@link novelang.book.function.builtin.InsertCommand}.
   */
  @Test
  public void insertWithExplicitIdentifiers() throws IOException {
    resourceInstaller.copyWithPath( TestResourceTree.Identifiers.BOOK_1 ) ;
    resourceInstaller.copyWithPath( TestResourceTree.Identifiers.PART_1 ) ;
    final File bookWithIdentifier =
        resourceInstaller.createFileObject( TestResourceTree.Identifiers.BOOK_1 ) ;

    final Book book = BookTestTools.createBook( bookWithIdentifier ) ;
    LOG.debug( "Book's document tree: %s", book.getDocumentTree().toStringTree() ) ;

    final SyntacticTree bookTree = book.getDocumentTree() ;
    TreeFixture.assertEqualsNoSeparators(
        tree( BOOK,
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
    Assert.assertFalse( book.hasProblem() ) ;
  }



  /**
   * Test {@link novelang.book.function.builtin.InsertCommand}.
   */
  @Test
  public void insertWithImplicitIdentifiers() throws IOException {
    resourceInstaller.copyWithPath( TestResourceTree.Identifiers.BOOK_2 ) ;
    resourceInstaller.copyWithPath( TestResourceTree.Identifiers.PART_2 ) ;
    final File bookWithIdentifier =
        resourceInstaller.createFileObject( TestResourceTree.Identifiers.BOOK_2 ) ;

    final Book book = BookTestTools.createBook( bookWithIdentifier ) ;
    LOG.debug( "Book's document tree: %s", book.getDocumentTree().toStringTree() ) ;

    final SyntacticTree bookTree = book.getDocumentTree() ;
    TreeFixture.assertEqualsNoSeparators(
        tree( BOOK,
            tree( _META, tree( _WORD_COUNT, "6" ) ),
            tree(
                _LEVEL,
                tree( _IMPLICIT_IDENTIFIER, tree( "\\\\L0\\L0-1" ) ),
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
                tree( _IMPLICIT_IDENTIFIER, tree( "\\\\L0\\L0-1" ) ),
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
    Assert.assertFalse( book.hasProblem() ) ;
  }


// =======
// Fixture
// =======

  static {
    initialize() ;
  }

  private static final Log LOG = LogFactory.getLog( BookTest.class ) ;
  private final JUnitAwareResourceInstaller resourceInstaller = new JUnitAwareResourceInstaller() ;

  public static final String CUSTOM_STYLE = "mystyle" ;

}

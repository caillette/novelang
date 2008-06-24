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
package novelang.model.book;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.ScratchDirectoryFixture;
import novelang.TestResourceTools;
import novelang.TestResources;
import novelang.model.common.NodeKind;
import static novelang.model.common.NodeKind.*;
import novelang.model.common.SyntacticTree;
import novelang.model.function.FunctionRegistry;
import novelang.parser.antlr.TreeFixture;
import static novelang.parser.antlr.TreeFixture.tree;

/**
 * Test for {@link Book} and also built-in functions.
 * 
 * @author Laurent Caillette
 */
public class BookTest {

  private static final Logger LOGGER = LoggerFactory.getLogger( BookTest.class ) ;

  /**
   * Tests the {@link novelang.model.function.builtin.SectionFunction}.
   */
  @Test
  public void justCreateSection() {
    final Book book = new Book(
        FunctionRegistry.getStandardRegistry(),
        SystemUtils.getUserDir(), 
        "section \n" + " My Section"
    ) ;
    final SyntacticTree bookTree = book.getDocumentTree() ;
    TreeFixture.assertEquals(
        tree( BOOK,
            tree(
                SECTION,
                tree( TITLE, tree( WORD, "My" ), tree( WORD, "Section" ) )
            )
        ),
        bookTree
    ) ;
    Assert.assertFalse( book.hasProblem() ) ;
  }

  /**
   * Test {@link novelang.model.function.builtin.InsertFunction}.
   */
  @Test
  public void justInsert() {

    final Book book = new Book(
        FunctionRegistry.getStandardRegistry(),
        SystemUtils.getUserDir(),
        "insert file:" + oneWordFile.getAbsolutePath()
    ) ;
    final SyntacticTree bookTree = book.getDocumentTree() ;
    TreeFixture.assertEquals(
        tree( BOOK,
            tree(
                PARAGRAPH_PLAIN,
                tree( WORD, "oneword" )
            )
        ),
        bookTree
    ) ;
    Assert.assertFalse( book.hasProblem() ) ;

  }

  /**
   * Test {@link novelang.model.function.builtin.InsertFunction}.
   */
  @Test
  public void insertWithFileScan() throws IOException {
    final Book book = new Book(
        FunctionRegistry.getStandardRegistry(),
        scannedBook
    ) ;
    LOGGER.debug( "Book's document tree:" + book.getDocumentTree().toStringTree() ) ;

    final SyntacticTree bookTree = book.getDocumentTree() ;
    TreeFixture.assertEquals(
        tree( BOOK,
            tree(
                NodeKind.CHAPTER ,
                tree( NodeKind.TITLE, tree( WORD, "file1" ) ),
                tree(
                    PARAGRAPH_PLAIN,
                    tree( WORD, "content-of-file1" )
                )
            ),
            tree(
                NodeKind.CHAPTER ,
                tree( NodeKind.TITLE, tree( WORD, "file2" ) ),
                tree(
                    PARAGRAPH_PLAIN,
                    tree( WORD, "content-of-file2" )
                )
            ),
            tree(
                NodeKind.CHAPTER ,
                tree( NodeKind.TITLE, tree( WORD, "file3" ) ),
                tree(
                    PARAGRAPH_PLAIN,
                    tree( WORD, "content-of-file3" )
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

  private static final String ONE_WORD_FILENAME = TestResources.ONE_WORD ;
  private File oneWordFile ;

  public static final String SCANNED_BOOK_FILENAME = TestResources.SCANNED_DIR ;
  private File scannedBook ;

  @Before
  public void setUp() throws Exception {

    final String testName = ClassUtils.getShortClassName( getClass() ) ;
    final ScratchDirectoryFixture scratchDirectoryFixture =
        new ScratchDirectoryFixture( testName ) ;
    final File contentDirectory = scratchDirectoryFixture.getTestScratchDirectory() ;

    oneWordFile = TestResourceTools.copyResourceToFile(
        getClass(),
        ONE_WORD_FILENAME,
        contentDirectory
    ) ;

    TestResourceTools.copyResourceToFile(
        getClass(), TestResources.SCANNED_FILE1, contentDirectory ) ;
    TestResourceTools.copyResourceToFile(
        getClass(), TestResources.SCANNED_FILE2, contentDirectory ) ;
    TestResourceTools.copyResourceToFile(
        getClass(), TestResources.SCANNED_FILE3, contentDirectory ) ;
    scannedBook = TestResourceTools.copyResourceToFile(
        getClass(), TestResources.SCANNED_BOOK, contentDirectory ) ;
  }

}

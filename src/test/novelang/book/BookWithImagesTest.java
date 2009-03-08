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

import novelang.ScratchDirectoryFixture;
import novelang.TestResourceTree;
import static novelang.TestResourceTree.initialize;
import static novelang.TestResourceTree.Images;
import novelang.book.function.FunctionRegistry;
import novelang.common.SyntacticTree;
import novelang.common.filefixture.Filer;
import static novelang.parser.NodeKind.*;
import novelang.parser.antlr.TreeFixture;
import static novelang.parser.antlr.TreeFixture.tree;
import novelang.system.DefaultCharset;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.NameAwareTestClassRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Tests for {@link Book} with embedded images.
 *
 * @author Laurent Caillette
 */
@RunWith( value = NameAwareTestClassRunner.class )
public class BookWithImagesTest {

  /**
   * Test {@link novelang.book.function.builtin.InsertFunction}.
   */
  @Test
  public void imagesInPartsWithExplicitNames() throws IOException {
    final Book book = new Book(
        FunctionRegistry.getStandardRegistry(),
        testDirectory,
        bookWithImagesExplicit,
        DefaultCharset.SOURCE,
        DefaultCharset.RENDERING
    ) ;
    LOGGER.debug( "Book's document tree:" + book.getDocumentTree().toStringTree() ) ;

    final SyntacticTree bookTree = book.getDocumentTree() ;
    TreeFixture.assertEquals(
        EXPECTED_BOOK_TREE,
        bookTree
    ) ;
    Assert.assertFalse( book.hasProblem() ) ;
  }

  /**
   * Test {@link novelang.book.function.builtin.InsertFunction}.
   */
  @Test
  public void imagesInPartsWithRecurse() throws IOException {
    final Book book = new Book(
        FunctionRegistry.getStandardRegistry(),
        testDirectory,
        bookWithImagesRecurse,
        DefaultCharset.SOURCE,
        DefaultCharset.RENDERING
    ) ;
    LOGGER.debug( "Book's document tree:" + book.getDocumentTree().toStringTree() ) ;

    final SyntacticTree bookTree = book.getDocumentTree() ;
    TreeFixture.assertEquals(
        EXPECTED_BOOK_TREE,
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

  
  private File testDirectory ;
  private File bookWithImagesExplicit;
  private File bookWithImagesRecurse ;

  @Before
  public void before() throws IOException {
    final String testName = NameAwareTestClassRunner.getTestName();
    testDirectory = new ScratchDirectoryFixture( testName ).getTestScratchDirectory() ;

    final Filer filer = new Filer( testDirectory ) ;
    filer.copyContent( Images.dir ) ;
        
    bookWithImagesExplicit = filer.createFileObject( Images.dir, Images.BOOK_EXPLICIT ) ;
    bookWithImagesRecurse = filer.createFileObject( Images.dir, Images.BOOK_RECURSIVE ) ;
    
    LOGGER.info( "bookWithImagesExplicit: {}", bookWithImagesExplicit );
    LOGGER.info( "bookWithImagesRecurse: {}", bookWithImagesRecurse );
  }

  private static final Logger LOGGER = LoggerFactory.getLogger( BookWithImagesTest.class ) ;

  private static final SyntacticTree EXPECTED_BOOK_TREE = tree(
      BOOK,
      tree( _META, tree( _WORD_COUNT, "0" ) ),
      tree(
          VECTOR_IMAGE,
          tree( RESOURCE_LOCATION, "/child/grandchild/Yellow-128x64.svg" ),
          tree( _IMAGE_WIDTH, "128mm" ),
          tree( _IMAGE_HEIGHT, "64mm" )
      ),
      tree(
          RASTER_IMAGE,
          tree( RESOURCE_LOCATION, "/Green-128x64.jpg" ),
          tree( _IMAGE_WIDTH, "128px" ),
          tree( _IMAGE_HEIGHT, "64px" )
      ),
      tree(
          RASTER_IMAGE,
          tree( RESOURCE_LOCATION, "/child/Blue-128x64.gif" ),
          tree( _IMAGE_WIDTH, "128px" ),
          tree( _IMAGE_HEIGHT, "64px" )
      ),
      tree(
          RASTER_IMAGE,
          tree( RESOURCE_LOCATION, "/Red-128x64.png" ),
          tree( _IMAGE_WIDTH, "128px" ),
          tree( _IMAGE_HEIGHT, "64px" )
      )
  ) ;


}

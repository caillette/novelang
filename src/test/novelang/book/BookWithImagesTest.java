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
import static novelang.TestResourceTree.Images;
import static novelang.TestResourceTree.initialize;
import novelang.book.function.FunctionRegistry;
import novelang.common.SyntacticTree;
import novelang.common.filefixture.Filer;
import static novelang.common.filefixture.ResourceSchema.relativizeResourcePath;
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

  private static final String VECTOR_IMAGE_WIDTH = Images.VECTOR_IMAGE_WIDTH ;
  private static final String VECTOR_IMAGE_HEIGHT = Images.VECTOR_IMAGE_HEIGHT ;
  private static final String RASTER_IMAGE_WIDTH = Images.RASTER_IMAGE_WIDTH ;
  private static final String RASTER_IMAGE_HEIGHT = Images.RASTER_IMAGE_HEIGHT ;

  private static final String RESOURCE_PATH_YELLOW = 
      relativizeResourcePath( Images.dir, Images.Child.Grandchild.YELLOW_SVG ) ;
  private static final String RESOURCE_PATH_GREEN = 
      relativizeResourcePath( Images.dir, Images.GREEN_JPG ) ;
  private static final String RESOURCE_PATH_BLUE = 
      relativizeResourcePath( Images.dir, Images.Child.BLUE_GIF ) ;
  private static final String RESOURCE_PATH_RED = 
      relativizeResourcePath( Images.dir, Images.RED_PNG ) ;
  
  private static final SyntacticTree EXPECTED_BOOK_TREE = tree(
      BOOK,
      tree( _META, tree( _WORD_COUNT, "0" ) ),
      tree(
          VECTOR_IMAGE,
          tree( RESOURCE_LOCATION, RESOURCE_PATH_YELLOW ),
          tree( _IMAGE_WIDTH, VECTOR_IMAGE_WIDTH ),
          tree( _IMAGE_HEIGHT, VECTOR_IMAGE_HEIGHT )
      ),
      tree(
          RASTER_IMAGE,
          tree( RESOURCE_LOCATION, RESOURCE_PATH_GREEN ),
          tree( _IMAGE_WIDTH, RASTER_IMAGE_WIDTH ),
          tree( _IMAGE_HEIGHT, RASTER_IMAGE_HEIGHT )
      ),
      tree(
          RASTER_IMAGE,
          tree( RESOURCE_LOCATION, RESOURCE_PATH_BLUE ),
          tree( _IMAGE_WIDTH, RASTER_IMAGE_WIDTH ),
          tree( _IMAGE_HEIGHT, RASTER_IMAGE_HEIGHT )
      ),
      tree(
          RASTER_IMAGE,
          tree( RESOURCE_LOCATION, RESOURCE_PATH_RED ),
          tree( _IMAGE_WIDTH, RASTER_IMAGE_WIDTH ),
          tree( _IMAGE_HEIGHT, RASTER_IMAGE_HEIGHT )
      )
  ) ;


}

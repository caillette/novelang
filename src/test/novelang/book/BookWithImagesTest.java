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

import java.io.IOException;
import java.io.File;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runners.NameAwareTestClassRunner;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import novelang.book.function.FunctionRegistry;
import novelang.common.SyntacticTree;
import novelang.parser.antlr.TreeFixture;
import static novelang.parser.antlr.TreeFixture.tree;
import static novelang.parser.NodeKind.*;
import novelang.loader.ResourceName;
import novelang.TestResources;
import novelang.TestResourceTools;
import novelang.ScratchDirectoryFixture;
import novelang.system.DefaultCharset;

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

  private static final ResourceName IMAGE_RED = TestResources.IMAGE_RED_128x64_PNG ;
  private static ResourceName IMAGE_GREEN = TestResources.IMAGE_GREEN_128x64_JPG ;
  private static ResourceName IMAGE_BLUE = TestResources.IMAGE_BLUE_128x64_GIF ;
  private static ResourceName IMAGE_YELLOW = TestResources.IMAGE_YELLOW_128x64_SVG ;
  private static ResourceName PART_1 = TestResources.PART_WITH_IMAGE1 ;
  private static ResourceName PART_2 = TestResources.PART_WITH_IMAGE2 ;
  private static ResourceName BOOK_WITH_IMAGES_EXPLICIT = TestResources.BOOK_WITH_IMAGES_EXPLICIT;
  private static ResourceName BOOK_WITH_IMAGES_RECURSE = TestResources.BOOK_WITH_IMAGES_RECURSE ;

  private File testDirectory ;
  private File bookWithImagesExplicit;
  private File bookWithImagesRecurse ;

  @Before
  public void before() throws IOException {
    final String testName = NameAwareTestClassRunner.getTestName();
    LOGGER.info( "Test name doesn't work inside IDEA-7.0.3: {}", testName ) ;
    testDirectory = new ScratchDirectoryFixture( testName ).getTestScratchDirectory() ;

    TestResourceTools.copyResourceToDirectory( getClass(), PART_1, testDirectory ) ;
    TestResourceTools.copyResourceToDirectory( getClass(), PART_2, testDirectory ) ;
    TestResourceTools.copyResourceToDirectory( getClass(), IMAGE_BLUE, testDirectory ) ;
    TestResourceTools.copyResourceToDirectory( getClass(), IMAGE_GREEN, testDirectory ) ;
    TestResourceTools.copyResourceToDirectory( getClass(), IMAGE_RED, testDirectory ) ;
    TestResourceTools.copyResourceToDirectory( getClass(), IMAGE_YELLOW, testDirectory ) ;
    TestResourceTools.copyResourceToDirectory( getClass(), BOOK_WITH_IMAGES_EXPLICIT, testDirectory ) ;
    TestResourceTools.copyResourceToDirectory( getClass(), BOOK_WITH_IMAGES_RECURSE, testDirectory ) ;

    bookWithImagesExplicit = new File( testDirectory, BOOK_WITH_IMAGES_EXPLICIT.getName() ) ;
    bookWithImagesRecurse = new File( testDirectory, BOOK_WITH_IMAGES_RECURSE.getName() ) ;
  }

  private static final Logger LOGGER = LoggerFactory.getLogger( BookWithImagesTest.class ) ;

  private static final SyntacticTree EXPECTED_BOOK_TREE = tree(
      BOOK,
      tree( _META, tree( _WORD_COUNT, "0" ) ),
      tree(
          RASTER_IMAGE,
          tree( RESOURCE_LOCATION, "/images/others/Red-128x64.png" ),
          tree( _IMAGE_WIDTH, "128px" ),
          tree( _IMAGE_HEIGHT, "64px" )
      ),
      tree(
          RASTER_IMAGE,
          tree( RESOURCE_LOCATION, "/images/Green-128x64.jpg" ),
          tree( _IMAGE_WIDTH, "128px" ),
          tree( _IMAGE_HEIGHT, "64px" )
      ),
      tree(
          VECTOR_IMAGE,
          tree( RESOURCE_LOCATION, "/images/others/Yellow-128x64.svg" ),
          tree( _IMAGE_WIDTH, "128mm" ),
          tree( _IMAGE_HEIGHT, "64mm" )
      ),
      tree(
          RASTER_IMAGE,
          tree( RESOURCE_LOCATION, "/images/Blue-128x64.gif" ),
          tree( _IMAGE_WIDTH, "128px" ),
          tree( _IMAGE_HEIGHT, "64px" )
      )
  )
      ;


}

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
package novelang.composium;

import static novelang.TestResourceTree.Images;
import static novelang.TestResourceTree.initialize;

import novelang.TestResourceTools;
import novelang.common.SyntacticTree;
import novelang.common.filefixture.Relativizer;
import novelang.common.filefixture.ResourceSchema;
import novelang.common.filefixture.JUnitAwareResourceInstaller;
import static novelang.parser.NodeKind.*;

import novelang.designator.Tag;
import novelang.parser.antlr.TreeFixture;
import static novelang.parser.antlr.TreeFixture.tree;
import novelang.system.DefaultCharset;
import novelang.system.Log;
import novelang.system.LogFactory;

import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.NameAwareTestClassRunner;

import java.io.File;
import java.io.IOException;

/**
 * Tests for {@link Composium} with embedded images.
 *
 * @author Laurent Caillette
 */
@RunWith( value = NameAwareTestClassRunner.class )
public class ComposiumWithImagesTest {

  /**
   * Test {@link novelang.composium.function.builtin.InsertCommand}.
   */
  @Test
  public void imagesInPartsWithExplicitNames() throws IOException {
    final Composium composium = new Composium(
        resourceInstaller.getTargetDirectory(),
        bookWithImagesExplicit,
        TestResourceTools.getExecutorService(),
        DefaultCharset.SOURCE,
        DefaultCharset.RENDERING,
        ImmutableSet.< Tag >of()
    ) ;
    LOG.debug( "Composium's document tree: %s", composium.getDocumentTree().toStringTree() ) ;

    final SyntacticTree bookTree = composium.getDocumentTree() ;
    TreeFixture.assertEqualsNoSeparators(
        EXPECTED_BOOK_TREE,
        bookTree
    ) ;
    Assert.assertFalse( composium.hasProblem() ) ;
  }

  /**
   * Test {@link novelang.composium.function.builtin.InsertCommand}.
   */
  @Test
  public void imagesInPartsWithRecurse() throws IOException {
    final Composium composium = new Composium(
        resourceInstaller.getTargetDirectory(),
        bookWithImagesRecurse,
        TestResourceTools.getExecutorService(),
        DefaultCharset.SOURCE,
        DefaultCharset.RENDERING,
        ImmutableSet.< Tag >of()
    ) ;
    LOG.debug( "Composium's document tree: %s", composium.getDocumentTree().toStringTree() ) ;

    final SyntacticTree bookTree = composium.getDocumentTree() ;
    TreeFixture.assertEqualsNoSeparators(
        EXPECTED_BOOK_TREE,
        bookTree
    ) ;
    Assert.assertFalse( composium.hasProblem() ) ;
  }


// =======
// Fixture
// =======

  private static final String RESOURCE_PATH_YELLOW ;
  private static final String RESOURCE_PATH_GREEN ;
  private static final String RESOURCE_PATH_BLUE ;
  private static final String RESOURCE_PATH_RED ;

  static {
    initialize() ;
    final Relativizer relativizer = ResourceSchema.relativizer( Images.dir ) ;
    RESOURCE_PATH_YELLOW = relativizer.apply( Images.Child.Grandchild.YELLOW_SVG ) ;
    RESOURCE_PATH_GREEN = relativizer.apply( Images.GREEN_JPG ) ;
    RESOURCE_PATH_BLUE = relativizer.apply( Images.Child.BLUE_GIF ) ;
    RESOURCE_PATH_RED = relativizer.apply( Images.RED_PNG ) ;

  }
  private final JUnitAwareResourceInstaller resourceInstaller = new JUnitAwareResourceInstaller() ;


  private File bookWithImagesExplicit;
  private File bookWithImagesRecurse ;

  @Before
  public void before() throws IOException {

    resourceInstaller.copyContent( Images.dir ) ;
        
    bookWithImagesExplicit = resourceInstaller.createFileObject( Images.dir, Images.BOOK_EXPLICIT ) ;
    bookWithImagesRecurse = resourceInstaller.createFileObject( Images.dir, Images.BOOK_RECURSIVE ) ;
    
    LOG.info( "bookWithImagesExplicit: '%s'", bookWithImagesExplicit );
    LOG.info( "bookWithImagesRecurse: '%s'", bookWithImagesRecurse );
  }

  private static final Log LOG = LogFactory.getLog( ComposiumWithImagesTest.class ) ;

  private static final String VECTOR_IMAGE_WIDTH = Images.VECTOR_IMAGE_WIDTH ;
  private static final String VECTOR_IMAGE_HEIGHT = Images.VECTOR_IMAGE_HEIGHT ;
  private static final String RASTER_IMAGE_WIDTH = Images.RASTER_IMAGE_WIDTH ;
  private static final String RASTER_IMAGE_HEIGHT = Images.RASTER_IMAGE_HEIGHT ;

  private static final SyntacticTree EXPECTED_BOOK_TREE = tree(
      COMPOSIUM,
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
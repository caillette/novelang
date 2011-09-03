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
package org.novelang.opus;

import java.io.File;
import java.io.IOException;

import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.novelang.ResourceTools;
import org.novelang.common.SyntacticTree;
import org.novelang.common.filefixture.Relativizer;
import org.novelang.common.filefixture.ResourceInstaller;
import org.novelang.common.filefixture.ResourceSchema;
import org.novelang.designator.Tag;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.DefaultCharset;
import org.novelang.parser.antlr.TreeFixture;
import org.novelang.testing.junit.MethodSupport;

import static org.novelang.ResourcesForTests.Images;
import static org.novelang.ResourcesForTests.initialize;
import static org.novelang.parser.NodeKind.*;
import static org.novelang.parser.antlr.TreeFixture.tree;

/**
 * Tests for {@link Opus} with embedded images.
 *
 * @author Laurent Caillette
 */
public class OpusWithImagesTest {

  /**
   * Test {@link org.novelang.opus.function.builtin.InsertCommand}.
   */
  @Test
  public void imagesInPartsWithExplicitNames() throws IOException {
    final Opus opus = new Opus(
        resourceInstaller.getTargetDirectory(),
        bookWithImagesExplicit,
        ResourceTools.getExecutorService(),
        DefaultCharset.SOURCE,
        DefaultCharset.RENDERING,
        ImmutableSet.< Tag >of()
    ) ;
    LOGGER.debug( "Opus's document tree: ", opus.getDocumentTree().toStringTree() ) ;

    final SyntacticTree bookTree = opus.getDocumentTree() ;
    TreeFixture.assertEqualsNoSeparators(
        EXPECTED_BOOK_TREE,
        bookTree
    ) ;
    Assert.assertFalse( opus.hasProblem() ) ;
  }

  /**
   * Test {@link org.novelang.opus.function.builtin.InsertCommand}.
   */
  @Test
  public void imagesInPartsWithRecurse() throws IOException {
    final Opus opus = new Opus(
        resourceInstaller.getTargetDirectory(),
        bookWithImagesRecurse,
        ResourceTools.getExecutorService(),
        DefaultCharset.SOURCE,
        DefaultCharset.RENDERING,
        ImmutableSet.< Tag >of()
    ) ;
    LOGGER.debug( "Opus's document tree: ", opus.getDocumentTree().toStringTree() ) ;

    final SyntacticTree bookTree = opus.getDocumentTree() ;
    TreeFixture.assertEqualsNoSeparators(
        EXPECTED_BOOK_TREE,
        bookTree
    ) ;
    Assert.assertFalse( opus.hasProblem() ) ;
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
  @Rule
  public final MethodSupport methodSupport = new MethodSupport() ;

  private final ResourceInstaller resourceInstaller = new ResourceInstaller( methodSupport ) ;


  private File bookWithImagesExplicit;
  private File bookWithImagesRecurse ;

  @Before
  public void before() throws IOException {

    resourceInstaller.copyContent( Images.dir ) ;
        
    bookWithImagesExplicit = resourceInstaller.createFileObject( Images.dir, Images.BOOK_EXPLICIT ) ;
    bookWithImagesRecurse = resourceInstaller.createFileObject( Images.dir, Images.BOOK_RECURSIVE ) ;
    
    LOGGER.info( "bookWithImagesExplicit: '", bookWithImagesExplicit, "'" ) ;
    LOGGER.info( "bookWithImagesRecurse: '", bookWithImagesRecurse, "'" );
  }

  private static final Logger LOGGER = LoggerFactory.getLogger( OpusWithImagesTest.class ) ;

  private static final String VECTOR_IMAGE_WIDTH = Images.VECTOR_IMAGE_WIDTH ;
  private static final String VECTOR_IMAGE_HEIGHT = Images.VECTOR_IMAGE_HEIGHT ;
  private static final String RASTER_IMAGE_WIDTH = Images.RASTER_IMAGE_WIDTH ;
  private static final String RASTER_IMAGE_HEIGHT = Images.RASTER_IMAGE_HEIGHT ;

  private static final SyntacticTree EXPECTED_BOOK_TREE = tree(
      OPUS,
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

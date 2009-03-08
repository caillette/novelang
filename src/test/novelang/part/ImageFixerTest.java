/*
 * Copyright (C) 2009 Laurent Caillette
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
package novelang.part ;

import com.google.common.collect.Lists;
import novelang.ScratchDirectoryFixture;
import static novelang.TestResourceTree.Images;
import static novelang.TestResourceTree.initialize;
import novelang.common.Problem;
import novelang.common.ProblemCollector;
import novelang.common.SyntacticTree;
import novelang.common.filefixture.Filer;
import static novelang.parser.NodeKind.*;
import static novelang.parser.antlr.TreeFixture.assertEquals;
import static novelang.parser.antlr.TreeFixture.tree;
import org.junit.Assert;
import static org.junit.Assert.assertSame;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.NameAwareTestClassRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Tests for {@link ImageFixer}
 * 
 * @author Laurent Caillette
 */
@RunWith( value = NameAwareTestClassRunner.class )
public class ImageFixerTest {

  @Test
  public void noChange() {
    final ImageFixer pathRelocator =
        new ImageFixer( parentDirectory, parentDirectory, null ) ;
    final SyntacticTree tree = tree( 
        PART, 
        tree( PARAGRAPH_REGULAR ) 
    ) ;
    assertEquals(  
      tree, 
        pathRelocator.relocateResources( tree )
    ) ;
  }
  
  @Test
  public void replaceRasterImageInTree() {
    final ListProblemCollector problemCollector = new ListProblemCollector() ;
    final ImageFixer pathRelocator =
        new ImageFixer( parentDirectory, parentDirectory, problemCollector ) ;
    
    final SyntacticTree treeToAbsolutize = tree( 
        PART, 
        tree( 
            PARAGRAPH_REGULAR,
            tree( RASTER_IMAGE, tree( RESOURCE_LOCATION, "./" + RESOURCE_UNDER_PARENT ) )
        
        ) 
    ) ;
    
    final String expectedAbsoluteResourceLocation = "/" + RESOURCE_UNDER_PARENT;
    
    final SyntacticTree expectedTree = tree( 
        PART, 
        tree( 
            PARAGRAPH_REGULAR,
            tree( 
                RASTER_IMAGE, 
                tree( RESOURCE_LOCATION, expectedAbsoluteResourceLocation ),
                tree( _IMAGE_WIDTH, RASTER_IMAGE_WIDTH ),
                tree( _IMAGE_HEIGHT, RASTER_IMAGE_HEIGHT )
            )
        ) 
    ) ;
    final SyntacticTree resultTree = pathRelocator.relocateResources( treeToAbsolutize );
    Assert.assertFalse( "" + problemCollector, problemCollector.hasProblem() ) ;
    assertEquals(
        expectedTree,
        resultTree
    ) ;
  }

  /**
   * In addition to tree content, this test verifies that SVG document is parsed
   * (which includes some entity loading).
   */
  @Test
  public void replaceVectorImageInTree() {
    final ListProblemCollector problemCollector = new ListProblemCollector() ;
    final ImageFixer imageFixer =
        new ImageFixer( grandChildDirectory, grandChildDirectory, problemCollector ) ;

    final SyntacticTree treeToAbsolutize = tree(
        PART,
        tree(
            PARAGRAPH_REGULAR,
            tree( VECTOR_IMAGE, tree( RESOURCE_LOCATION, "./" + RESOURCE_UNDER_GRANDCHILD ) )

        )
    ) ;

    final String expectedAbsoluteResourceLocation = "/" + RESOURCE_UNDER_GRANDCHILD;

    final SyntacticTree expectedTree = tree(
        PART,
        tree(
            PARAGRAPH_REGULAR,
            tree(
                VECTOR_IMAGE,
                tree( RESOURCE_LOCATION, expectedAbsoluteResourceLocation ),
                tree( _IMAGE_WIDTH, VECTOR_IMAGE_WIDTH ),
                tree( _IMAGE_HEIGHT, VECTOR_IMAGE_HEIGHT )
            )
        )
    ) ;

    final SyntacticTree resultTree = imageFixer.relocateResources( treeToAbsolutize );
    
    Assert.assertFalse( "" + problemCollector, problemCollector.hasProblem() ) ;
    assertEquals(
        expectedTree,
        resultTree
    ) ;
  }

  @Test
  public void reportProblem() {
    final ListProblemCollector problemCollector = new ListProblemCollector() ;
    final ImageFixer pathRelocator =
        new ImageFixer( parentDirectory, parentDirectory, problemCollector ) ;
    
    final SyntacticTree treeToAbsolutize = tree( 
        PART, 
        tree( 
            PARAGRAPH_REGULAR,
            tree( RASTER_IMAGE, tree( RESOURCE_LOCATION, "./doesnotexist" ) )
        
        ) 
    ) ;
    pathRelocator.relocateResources( treeToAbsolutize ) ;
    
    assertSame( 1, problemCollector.getProblems().length ) ;
    
  }
  
  @Test( expected = ImageFixerException.class )
  public void detectUnauthorizedAccessToUpperDirectory() throws ImageFixerException {
    justRelocate(
        childDirectory, 
        childDirectory,
        ( "../" + RESOURCE_UNDER_PARENT ) // This one exists above, but should be forbidden!
    ) ;
  }
  
  @Test( expected = ImageFixerException.class )
  public void detectNonExistingResource() throws ImageFixerException {
    justRelocate( 
        parentDirectory,
        parentDirectory,
        "doesnotexist" 
    ) ;
  }
  
  @Test
  public void absoluteFromBaseToBase() throws ImageFixerException {
    check(
        "/" + RESOURCE_UNDER_PARENT,
        parentDirectory,
        parentDirectory,
        "/" + RESOURCE_UNDER_PARENT
    ) ;
  }

  @Test
  public void relativeFromBaseToBase() throws ImageFixerException {
    check(
        "/" + RESOURCE_UNDER_PARENT,
        parentDirectory,
        parentDirectory,
        "./" + RESOURCE_UNDER_PARENT
    ) ;
  }

  @Test
  public void absoluteFromChildToChild() throws ImageFixerException {
    check(
        "/" + CHILD_NAME + "/" + RESOURCE_UNDER_CHILD,
        parentDirectory, 
        childDirectory, 
        "/" + CHILD_NAME + "/" + RESOURCE_UNDER_CHILD 
    ) ;
  }

  @Test
  public void relativeFromChildToChild() throws ImageFixerException {
    check(
        "/" + CHILD_NAME + "/" + RESOURCE_UNDER_CHILD,
        parentDirectory, 
        childDirectory, 
        "./" + RESOURCE_UNDER_CHILD
    ) ;
  }

  @Test
  public void absoluteFromChildToGrandchild() throws ImageFixerException {
    check(
        "/" + CHILD_NAME + "/" + GRANDCHILD_NAME + "/" + RESOURCE_UNDER_GRANDCHILD,
        parentDirectory, 
        childDirectory, 
        "/" + CHILD_NAME + "/" + GRANDCHILD_NAME + "/" + RESOURCE_UNDER_GRANDCHILD 
    ) ;
  }

  @Test
  public void relativeFromChildToGrandchild() throws ImageFixerException {
    check(
        "/" + CHILD_NAME + "/" + GRANDCHILD_NAME + "/" + RESOURCE_UNDER_GRANDCHILD,
        parentDirectory, 
        childDirectory, 
        "./" + GRANDCHILD_NAME + "/" + RESOURCE_UNDER_GRANDCHILD
    ) ;
  }

  @Test
  public void relativeFromChildToParent() throws ImageFixerException {
    check(
        "/" + RESOURCE_UNDER_PARENT,
        parentDirectory, 
        childDirectory, 
        "../" + RESOURCE_UNDER_PARENT
    ) ;
  }

  @Test
  public void absoluteFromChildToParent() throws ImageFixerException {
    check(
        "/" + RESOURCE_UNDER_PARENT,
        parentDirectory, 
        childDirectory, 
        "/" + RESOURCE_UNDER_PARENT
    ) ;
  }



// =======  
// Fixture
// =======
  
  static {
    initialize() ;    
  }

  private static final String RESOURCE_UNDER_PARENT = Images.RED_PNG.getName();
  private static final String RESOURCE_UNDER_CHILD = Images.Child.BLUE_GIF.getName();
  private static final String RESOURCE_UNDER_GRANDCHILD = 
      Images.Child.Grandchild.YELLOW_SVG.getName();
  
  private static final String CHILD_NAME = Images.Child.dir.getName() ;
  private static final String GRANDCHILD_NAME = Images.Child.Grandchild.dir.getName() ;

  private static final String RASTER_IMAGE_WIDTH = Images.RASTER_IMAGE_WIDTH ;
  private static final String RASTER_IMAGE_HEIGHT = Images.RASTER_IMAGE_HEIGHT ;
  private static final String VECTOR_IMAGE_WIDTH = Images.VECTOR_IMAGE_WIDTH ;
  private static final String VECTOR_IMAGE_HEIGHT = Images.VECTOR_IMAGE_HEIGHT ;
  
  private File parentDirectory ;
  private File childDirectory ;
  private File grandChildDirectory ;
  
  @Before
  public void before() throws IOException {
    final String testName = NameAwareTestClassRunner.getTestName();
    parentDirectory = new ScratchDirectoryFixture( testName ).getTestScratchDirectory() ;

    final Filer filer = new Filer( parentDirectory ) ;
    filer.copyContent( Images.dir ) ;
    
    childDirectory = filer.createFileObject( Images.dir, Images.Child.dir );
    grandChildDirectory = filer.createFileObject( Images.dir, Images.Child.Grandchild.dir ) ;
  }

  

  public ImageFixerTest() throws IOException {

  }

  private class ListProblemCollector implements ProblemCollector {
    
    private final List< Problem > problems = Lists.newArrayList() ;

    public void collect( Problem problem ) {
      problems.add( problem ) ;
    }
    
    public Problem[] getProblems() {
      return problems.toArray( new Problem[ problems.size() ] ) ;
    }

    public boolean hasProblem() {
      return ! problems.isEmpty() ;
    }

    @Override
    public String toString() {
      return problems.toString() ;
    }
  }
  
  private void check( 
      final String expectedRelativeResourceName,
      final File baseDirectory,
      final File referrerDirectory,
      final String resourceNameRelativeToReferrer
  ) throws ImageFixerException {
    final ListProblemCollector problemCollector = new ListProblemCollector() ;
    final ImageFixer pathRelocator = new ImageFixer(
        baseDirectory, referrerDirectory, problemCollector ) ;
    final String actualRelativeResourceName = 
        pathRelocator.relocate( resourceNameRelativeToReferrer ) ;
    Assert.assertEquals( expectedRelativeResourceName, actualRelativeResourceName ) ;
    Assert.assertEquals( 0, problemCollector.getProblems().length ) ;    
  }

  private void justRelocate( 
      final File baseDirectory,
      final File referrerDirectory,
      final String resourceNameRelativeToReferrer
  ) throws ImageFixerException {
    final ListProblemCollector problemCollector = new ListProblemCollector() ;
    final ImageFixer pathRelocator = new ImageFixer(
        baseDirectory, referrerDirectory, problemCollector ) ;
    pathRelocator.relocate( resourceNameRelativeToReferrer ) ;
  }
}

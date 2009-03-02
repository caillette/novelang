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
import novelang.common.Problem;
import novelang.common.ProblemCollector;
import novelang.common.SyntacticTree;
import static novelang.parser.NodeKind.*;
import static novelang.parser.antlr.TreeFixture.assertEquals;
import static novelang.parser.antlr.TreeFixture.tree;
import org.apache.commons.io.FileUtils;
import static org.junit.Assert.assertSame;
import org.junit.Test;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Tests for {@link ImageFixer}
 * 
 * @author Laurent Caillette
 */
public class ResourcePathRelocatorTest {

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
  public void replaceInTree() {
    final ImageFixer pathRelocator =
        new ImageFixer( parentDirectory, parentDirectory, null ) ;
    
    final SyntacticTree treeToAbsolutize = tree( 
        PART, 
        tree( 
            PARAGRAPH_REGULAR,
            tree( RASTER_IMAGE, tree( RESOURCE_LOCATION, "./" + RESOURCE_UNDER_PARENT_NAME ) )
        
        ) 
    ) ;
    
    final String expectedAbsoluteResourceLocation = "/" + RESOURCE_UNDER_PARENT_NAME ;
    
    final SyntacticTree expectedTree = tree( 
        PART, 
        tree( 
            PARAGRAPH_REGULAR,
            tree( 
                RASTER_IMAGE, 
                tree( RESOURCE_LOCATION, expectedAbsoluteResourceLocation ) 
            )        
        ) 
    ) ;
    assertEquals(  
        expectedTree, 
        pathRelocator.relocateResources( treeToAbsolutize )
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
  
  @Test( expected = ResourcePathRelocatorException.class )  
  public void detectUnauthorizedAccessToUpperDirectory() throws ResourcePathRelocatorException {
    justRelocate(
        childDirectory, 
        childDirectory,
        ( "../" + RESOURCE_UNDER_PARENT_NAME ) // This one exists above, but should be forbidden! 
    ) ;
  }
  
  @Test( expected = ResourcePathRelocatorException.class )
  public void detectNonExistingResource() throws ResourcePathRelocatorException {
    justRelocate( 
        parentDirectory,
        parentDirectory,
        "doesnotexist" 
    ) ;
  }
  
  @Test
  public void absoluteFromBaseToBase() throws ResourcePathRelocatorException {
    check(
        "/" + RESOURCE_UNDER_PARENT_NAME,
        parentDirectory,
        parentDirectory,
        "/" + RESOURCE_UNDER_PARENT_NAME 
    ) ;
  }

  @Test
  public void relativeFromBaseToBase() throws ResourcePathRelocatorException {
    check(
        "/" + RESOURCE_UNDER_PARENT_NAME,
        parentDirectory,
        parentDirectory,
        "./" + RESOURCE_UNDER_PARENT_NAME 
    ) ;
  }

  @Test
  public void absoluteFromChildToChild() throws ResourcePathRelocatorException {
    check(
        "/" + CHILD_NAME + "/" + RESOURCE_UNDER_CHILD_NAME,
        parentDirectory, 
        childDirectory, 
        "/" + CHILD_NAME + "/" + RESOURCE_UNDER_CHILD_NAME 
    ) ;
  }

  @Test
  public void relativeFromChildToChild() throws ResourcePathRelocatorException {
    check(
        "/" + CHILD_NAME + "/" + RESOURCE_UNDER_CHILD_NAME,
        parentDirectory, 
        childDirectory, 
        "./" + RESOURCE_UNDER_CHILD_NAME 
    ) ;
  }

  @Test
  public void absoluteFromChildToGrandchild() throws ResourcePathRelocatorException {
    check(
        "/" + CHILD_NAME + "/" + GRANDCHILD_NAME + "/" + RESOURCE_UNDER_GRANDCHILD_NAME,
        parentDirectory, 
        childDirectory, 
        "/" + CHILD_NAME + "/" + GRANDCHILD_NAME + "/" + RESOURCE_UNDER_GRANDCHILD_NAME 
    ) ;
  }

  @Test
  public void relativeFromChildToGrandchild() throws ResourcePathRelocatorException {
    check(
        "/" + CHILD_NAME + "/" + GRANDCHILD_NAME + "/" + RESOURCE_UNDER_GRANDCHILD_NAME,
        parentDirectory, 
        childDirectory, 
        "./" + GRANDCHILD_NAME + "/" + RESOURCE_UNDER_GRANDCHILD_NAME 
    ) ;
  }

  @Test
  public void relativeFromChildToParent() throws ResourcePathRelocatorException {
    check(
        "/" + RESOURCE_UNDER_PARENT_NAME,
        parentDirectory, 
        childDirectory, 
        "../" + RESOURCE_UNDER_PARENT_NAME 
    ) ;
  }

  @Test
  public void absoluteFromChildToParent() throws ResourcePathRelocatorException {
    check(
        "/" + RESOURCE_UNDER_PARENT_NAME,
        parentDirectory, 
        childDirectory, 
        "/" + RESOURCE_UNDER_PARENT_NAME 
    ) ;
  }



// =======  
// Fixture
// =======
  
  private static final String CHILD_NAME = "child" ;
  private static final String GRANDCHILD_NAME = "grandchild" ;

  private static final String RESOURCE_UNDER_PARENT_NAME = "resourceInParent" ;
  private static final String RESOURCE_UNDER_CHILD_NAME = "resourceInChild" ;
  private static final String RESOURCE_UNDER_GRANDCHILD_NAME = "resourceInGrandChild" ;
  
  private final File parentDirectory ;
  private final File childDirectory ;
  private final File grandChildDirectory ;

  public ResourcePathRelocatorTest() throws IOException {
    final ScratchDirectoryFixture fixture = new ScratchDirectoryFixture( getClass() ) ;
    parentDirectory = fixture.getTestScratchDirectory() ;
    final File resourceUnderParent = new File( parentDirectory, RESOURCE_UNDER_PARENT_NAME ) ;
    FileUtils.writeStringToFile( resourceUnderParent, RESOURCE_UNDER_PARENT_NAME ) ;
    
    childDirectory = new File( parentDirectory, CHILD_NAME ) ;
    final File resourceUnderChild = new File( childDirectory, RESOURCE_UNDER_CHILD_NAME ) ;
    FileUtils.writeStringToFile( resourceUnderChild, RESOURCE_UNDER_CHILD_NAME ) ;

    grandChildDirectory = new File( childDirectory, GRANDCHILD_NAME ) ;
    final File resourceUnderGrandChild = 
        new File( grandChildDirectory, RESOURCE_UNDER_GRANDCHILD_NAME ) ;
    FileUtils.writeStringToFile( resourceUnderGrandChild, RESOURCE_UNDER_GRANDCHILD_NAME ) ;
  }

  private class ListProblemCollector implements ProblemCollector {
    
    private final List< Problem > problems = Lists.newArrayList() ;

    public void collect( Problem problem ) {
      problems.add( problem ) ;
    }
    
    public Problem[] getProblems() {
      return problems.toArray( new Problem[ problems.size() ] ) ;
    }
  }
  
  private void check( 
      final String expectedRelativeResourceName,
      final File baseDirectory,
      final File referrerDirectory,
      final String resourceNameRelativeToReferrer
  ) throws ResourcePathRelocatorException {
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
  ) throws ResourcePathRelocatorException {
    final ListProblemCollector problemCollector = new ListProblemCollector() ;
    final ImageFixer pathRelocator = new ImageFixer(
        baseDirectory, referrerDirectory, problemCollector ) ;
    pathRelocator.relocate( resourceNameRelativeToReferrer ) ;
  }
}

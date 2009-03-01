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
import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.assertSame;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Tests for {@link novelang.part.ResourceAbsolutizer} 
 * 
 * @author Laurent Caillette
 */
public class ResourceAbsolutizerTest {
  private static final String RESOURCE_UNDER_PARENT_NAME = "resource1" ;
  private static final String RESOURCE_UNDER_CHILD_NAME = "resource2" ;

  @Test
  public void noChange() {
    final ResourceAbsolutizer absolutizer = new ResourceAbsolutizer( parentDirectory, null ) ;
    final SyntacticTree tree = tree( 
        PART, 
        tree( PARAGRAPH_REGULAR ) 
    ) ;
    assertEquals(  
      tree, 
        absolutizer.absolutizeResources( tree )
    ) ;
  }
  
  @Test
  public void replace() {
    final ResourceAbsolutizer absolutizer = new ResourceAbsolutizer( parentDirectory, null ) ;
    final SyntacticTree treeToAbsolutize = tree( 
        PART, 
        tree( 
            PARAGRAPH_REGULAR,
            tree( RASTER_IMAGE, tree( RESOURCE_LOCATION, "./" + RESOURCE_UNDER_PARENT_NAME ) )
        
        ) 
    ) ;
    
    final String expectedAbsoluteResourceLocation = 
        parentDirectory.getAbsolutePath() + "/" + RESOURCE_UNDER_PARENT_NAME ;
    
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
        absolutizer.absolutizeResources( treeToAbsolutize )
    ) ;
  }
  
  @Test
  public void reportProblem() {
    final ListProblemCollector problemCollector = new ListProblemCollector() ;
    final ResourceAbsolutizer absolutizer = 
        new ResourceAbsolutizer( parentDirectory, problemCollector ) ;
    
    final SyntacticTree treeToAbsolutize = tree( 
        PART, 
        tree( 
            PARAGRAPH_REGULAR,
            tree( RASTER_IMAGE, tree( RESOURCE_LOCATION, "./doesnotexist" ) )
        
        ) 
    ) ;
    absolutizer.absolutizeResources( treeToAbsolutize ) ;
    
    assertSame( 1, problemCollector.getProblems().length ) ;
    
  }
  
  @Test( expected = AbsolutizerException.class )  
  public void detectUpperDirectory() throws AbsolutizerException {
    ResourceAbsolutizer.absolutizeFile( childDirectory, "../" + RESOURCE_UNDER_PARENT_NAME ) ;
  }
  
  @Test( expected = AbsolutizerException.class )  
  public void detectNonExistingResource() throws AbsolutizerException {
    ResourceAbsolutizer.absolutizeFile( parentDirectory, "doesnotexist" ) ;
  }
  
  @Test
  public void absolutizeFromCurrentDirectoryOk() throws AbsolutizerException {
    ResourceAbsolutizer.absolutizeFile( parentDirectory, "./" + RESOURCE_UNDER_PARENT_NAME ) ;
  }

  @Test
  public void absolutizeFromDirectoryBelowOk() throws AbsolutizerException {
    ResourceAbsolutizer.absolutizeFile( parentDirectory, "/child/" + RESOURCE_UNDER_CHILD_NAME ) ;
  }



// =======  
// Fixture
// =======
  
  
  private final File parentDirectory ;
  private final File childDirectory ;

  public ResourceAbsolutizerTest() throws IOException {
    final ScratchDirectoryFixture fixture = new ScratchDirectoryFixture( getClass() ) ;
    parentDirectory = fixture.getTestScratchDirectory() ;
    final File resourceUnderParent = new File( parentDirectory, RESOURCE_UNDER_PARENT_NAME ) ;
    FileUtils.writeStringToFile( resourceUnderParent, RESOURCE_UNDER_PARENT_NAME ) ;
    
    childDirectory = new File( parentDirectory, "child" ) ;
    final File resourceUnderChild = new File( childDirectory, RESOURCE_UNDER_CHILD_NAME ) ;
    FileUtils.writeStringToFile( resourceUnderChild, RESOURCE_UNDER_CHILD_NAME ) ;
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
}

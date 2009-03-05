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
package novelang.common.filefixture.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.NameAwareTestClassRunner;
import static junit.framework.Assert.assertEquals;
import novelang.ScratchDirectoryFixture;
import novelang.common.filefixture.Directory;
import novelang.common.filefixture.FileFixture;
import novelang.common.filefixture.Resource;

/**
 * Tests for {@link FileFixture}.
 * @author Laurent Caillette
 */
@RunWith( value = NameAwareTestClassRunner.class )
public class FileFixtureTest {

  @Test
  public void testCreateObjects() {
    FileFixture.register( testDirectory, ResourceTree.class ) ;

    final Directory tree = FileFixture.getAsDirectory( ResourceTree.class ) ;

    final List< Resource > treeResources = tree.getResources() ;
    assertEquals( 0, treeResources.size() ) ;
    final List< Directory > treeDirectories = tree.getSubdirectories() ;
    assertEquals( 2, treeDirectories.size() ) ;
    assertEquals( "d0", treeDirectories.get( 0 ).getName() ) ;
    assertEquals( "d1", treeDirectories.get( 1 ).getName() ) ;

    final Directory d0 = tree.getSubdirectories().get( 0 ) ;
    final List< Resource > d0Resources = d0.getResources() ;
    assertEquals( 2, d0Resources.size() ) ;
    assertEquals( "r.0.0.txt", d0Resources.get( 0 ).getName() ) ;
    assertEquals( "r.0.1.txt", d0Resources.get( 0 ).getName() ) ;
    final List< Directory > d0Directories = d0.getSubdirectories() ;
    assertEquals( 2, d0Directories.size() ) ;

  }

// =======
// Fixture
// =======

  private File testDirectory ;

  @Before
  public void before() throws IOException {
    final String testName = NameAwareTestClassRunner.getTestName() ;
    testDirectory = new ScratchDirectoryFixture( testName ).getTestScratchDirectory() ;
  }


}

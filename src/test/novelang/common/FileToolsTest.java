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
package novelang.common;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.apache.commons.io.FileUtils;
import novelang.ScratchDirectoryFixture;

/**
 * Tests for {@link FileTools}.
 *
 * @author Laurent Caillette
 */
public class FileToolsTest {

  @Test
  public void testRelativizeOk() {
    final String relativized = FileTools.relativizePath( parent, child1 ) ;
    Assert.assertEquals( "child1", relativized ) ;
  }

  @Test (expected = IllegalArgumentException.class )
  public void testRelativizeFails() {
    FileTools.relativizePath( child1, child2 ) ;
  }

  
// =======
// Fixture
// =======

  private File parent ;
  private File child1 ;
  private File child2 ;

  @Before
  public void setUp() throws IOException {
    
    final ScratchDirectoryFixture fixture = new ScratchDirectoryFixture( getClass().getName() ) ;
    final File scratchDirectory = fixture.getTestScratchDirectory() ;

    parent = new File( scratchDirectory, "parent" ) ;
    parent.mkdir() ;
    FileUtils.waitFor( parent, 1000 ) ;

    child1 = new File( parent, "child1" ) ;

    child2 = new File( parent, "child2" ) ;
  }

}

/*
 * Copyright (C) 2010 Laurent Caillette
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
package org.novelang.common;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.novelang.common.filefixture.ResourceInstaller;
import org.novelang.testing.DirectoryFixture;
import org.novelang.testing.junit.MethodSupport;

/**
 * Tests for {@link FileTools}.
 *
 * @author Laurent Caillette
 */
public class FileToolsTest {
  private static final int TIMEOUT_MILLISECONDS = 1000;

  @Test( timeout = TIMEOUT_MILLISECONDS )
  public void testRelativizeOkWithoutTrailingSeparatorOnParent() {
    final String relativized = FileTools.relativizePath( parentNoTrailingSeparator, childFile ) ;
    Assert.assertEquals( "childFile", relativized ) ;
  }

  @Test( timeout = TIMEOUT_MILLISECONDS )
  public void testRelativizeOkWithDirectoryAsChild() {
    final String relativized = FileTools.relativizePath( parentNoTrailingSeparator, childFile ) ;
    Assert.assertEquals( "childFile", relativized ) ;
  }

  @Test( timeout = TIMEOUT_MILLISECONDS )
  public void testRelativizeOkWithTrailingSeparatorOnParent() {
    final String relativized = FileTools.relativizePath( parentWithTrailingSeparator, childFile ) ;
    Assert.assertEquals( "childFile", relativized ) ;
  }

  @Test( timeout = TIMEOUT_MILLISECONDS )
  public void testRelativizeOkWithSubdirectory() {
    final String relativized = FileTools.relativizePath(
        parentWithTrailingSeparator, childDirectory ) ;
    Assert.assertEquals( "childDirectory" + SystemUtils.FILE_SEPARATOR, relativized ) ;
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRelativizeFails() {
    FileTools.relativizePath( childDirectory, childFile ) ;
  }

  /**
   * This doesn't test {@link FileTools} really but it's about verifying JDK behavior.
   */
  @Test
  public void fileInstantiationWithLeadingDot() throws IOException {
    final File childFileDotted = new File( parentNoTrailingSeparator, "./childFile" ) ;
    Assert.assertEquals( 
        childFile.getAbsolutePath(), 
        childFileDotted.getCanonicalFile().getAbsolutePath() 
    ) ;
    
  }

  @Test
  public void testListDirectories() {
    final List< File > directories = FileTools.scanDirectories( parentNoTrailingSeparator ) ;
    Assert.assertEquals( 3, directories.size() ) ;
    Assert.assertEquals( "parent", directories.get( 0 ).getName() ) ;
    Assert.assertEquals( "childDirectory", directories.get( 1 ).getName() ) ;
    Assert.assertEquals( "grandChildDirectory", directories.get( 2 ).getName() ) ;
  }

  @Test
  public void urlifyPath() {
    Assert.assertEquals( "foo/bar/baz/", FileTools.urlifyPath( "foo/bar\\baz\\" ) ) ;
  }
  
// =======
// Fixture
// =======

  private File parentNoTrailingSeparator;
  private File parentWithTrailingSeparator;
  private File childDirectory;
  private File childFile;

  @Rule
  public final MethodSupport methodSupport = new MethodSupport() {
    @Override
    protected void beforeStatementEvaluation() throws Exception {
      parentNoTrailingSeparator = createDirectory( getDirectory(), "parent" ) ;
      parentWithTrailingSeparator = createDirectory( getDirectory(), "parent/" ) ;
      childDirectory = createDirectory( parentNoTrailingSeparator, "childDirectory" ) ;
      createDirectory( parentNoTrailingSeparator, "grandChildDirectory" );
      childFile = new File( parentNoTrailingSeparator, "childFile" ) ;


    }
  };



  private static File createDirectory( final File parent, final String name ) {
    final File directory = new File( parent, name ) ;
    directory.mkdirs() ;
    FileUtils.waitFor( directory, TIMEOUT_MILLISECONDS ) ;
    return directory ;
  }

}

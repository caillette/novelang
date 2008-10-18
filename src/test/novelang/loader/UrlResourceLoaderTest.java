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

package novelang.loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import novelang.ScratchDirectoryFixture;
import novelang.TestResourceTools;
import novelang.TestResources;

/**
 * @author Laurent Caillette
 */
public class UrlResourceLoaderTest {

  @Test
  public void absoluteOk() throws IOException {
    final UrlResourceLoader loader = new UrlResourceLoader( loaderDirectory.toURI().toURL() ) ;
    final InputStream inputStream = loader.getInputStream( RESOURCE_NAME ) ;
    final String resource = IOUtils.toString( inputStream ) ;
    Assert.assertFalse( StringUtils.isBlank( resource ) ) ;
  }

  @Test
  public void relativizedOk() throws IOException {
    final UrlResourceLoader loader = new UrlResourceLoader( loaderDirectory.toURI().toURL() ) ;
    final InputStream inputStream = loader.getInputStream( RESOURCE_NAME ) ;
    final String resource = IOUtils.toString( inputStream ) ;
    Assert.assertFalse( StringUtils.isBlank( resource ) ) ;
  }


  @Test( expected = ResourceNotFoundException.class )
  public void urlResourceLoaderNotFound() throws IOException {
    new UrlResourceLoader( loaderDirectory.toURI().toURL() ).getInputStream(
        new ResourceName( "doesnot.exist" ) ) ;
  }


// =======
// Fixture
// =======

  private static final ResourceName RESOURCE_NAME = TestResources.SHOWCASE;
  private File loaderDirectory;

  @Before
  public void setUp() throws IOException {
    final String testName = ClassUtils.getShortClassName( getClass() );
    final ScratchDirectoryFixture scratchDirectoryFixture =
        new ScratchDirectoryFixture( testName ) ;
    loaderDirectory = scratchDirectoryFixture.getTestScratchDirectory();
    TestResourceTools.copyResourceToDirectory(
        getClass(),
        RESOURCE_NAME,
        loaderDirectory
    ) ;

  }
}
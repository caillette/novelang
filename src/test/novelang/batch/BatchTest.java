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
package novelang.batch;

import java.io.File;
import java.io.IOException;
import java.security.Permission;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import novelang.ScratchDirectoryFixture;
import novelang.TestResourceTools;
import novelang.TestResources;
import novelang.configuration.ConfigurationTools;
import novelang.rendering.RenditionMimeType;

/**
 * Tests for {@link Main}.
 *
 * @author Laurent Caillette
 */
public class BatchTest {

  @Test( expected = CannotExitVirtualMachineWhileTestingException.class )
  public void exitWithIncorrectParameters() throws Exception {
    Main.main( new String[ 0 ] ) ;
  }

  @Test
  public void generateOneDocumentOk() throws Exception {
    Main.main( contentDirectory, new String[] { "/" + RENDERED_DOCUMENT_NAME } ) ;

    final File renderedDocument = new File( outputDirectory, RENDERED_DOCUMENT_NAME ) ;
    Assert.assertTrue( renderedDocument.exists() ) ;
  }

// =======
// Fixture
// =======

  private File contentDirectory ;
  private File outputDirectory ;
  private static final String GOOD_NLP_RESOURCE_NAME = TestResources.SERVED_PARTSOURCE_GOOD ;
  private static final String RENDERED_DOCUMENT_NAME =
      TestResources.SERVED_GOOD_RADIX + "." + RenditionMimeType.HTML.getFileExtension() ;
  private SecurityManager savedSecurityManager ;


  @Before
  public void setUp() throws IOException {
    final ScratchDirectoryFixture scratchDirectoryFixture =
        new ScratchDirectoryFixture( getClass() ) ;
    contentDirectory = scratchDirectoryFixture.getTestScratchDirectory() ;

    TestResourceTools.copyResourceToDirectoryFlat(
        getClass(),
        GOOD_NLP_RESOURCE_NAME,
        contentDirectory
    ) ;

    outputDirectory = new File(
        contentDirectory, ConfigurationTools.DEFAULT_OUTPUT_DIRECTORY_NAME ) ;

    savedSecurityManager = System.getSecurityManager() ;
    System.setSecurityManager( new NoExitSecurityManager() ) ;
  }

  @After
  public void tearDown() {
    System.setSecurityManager( savedSecurityManager ) ;
  }

  private static class NoExitSecurityManager extends SecurityManager {
    public void checkExit( int status ) {
      throw new CannotExitVirtualMachineWhileTestingException() ;
    }

    public void checkPermission( Permission perm ) { }
  }

  private static class CannotExitVirtualMachineWhileTestingException extends RuntimeException { }
}
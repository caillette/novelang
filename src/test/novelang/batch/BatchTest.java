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
import org.junit.runner.RunWith;
import org.junit.runners.NameAwareTestClassRunner;
import novelang.DirectoryFixture;
import novelang.TestResourceTools;
import novelang.TestResources;
import novelang.TestResourceTree;
import novelang.system.LogFactory;
import novelang.system.Log;
import novelang.common.filefixture.ResourceInstaller;
import novelang.common.filefixture.JUnitAwareResourceInstaller;
import novelang.common.filefixture.Resource;
import novelang.configuration.ConfigurationTools;
import novelang.configuration.parse.GenericParameters;
import novelang.rendering.RenditionMimeType;

/**
 * Tests for {@link DocumentGenerator}.
 *
 * @author Laurent Caillette
 */
@RunWith( value = NameAwareTestClassRunner.class )
public class BatchTest {

  @Test( expected = CannotExitVirtualMachineWhileTestingException.class )
  public void exitWithIncorrectParameters() throws Exception {
    new DocumentGenerator().main( "tesing", new String[ 0 ] ) ;
  }

  @Test( expected = CannotExitVirtualMachineWhileTestingException.class )
  public void exitBecauseHelpRequeted() throws Exception {
    new DocumentGenerator().main(
        "tesing", 
        new String[] { GenericParameters.OPTIONPREFIX + GenericParameters.HELP_OPTION_NAME }
    ) ;
  }

  @Test
  public void generateOneDocumentOk() throws Exception {
    final JUnitAwareResourceInstaller resourceInstaller = new JUnitAwareResourceInstaller() ;
    final Resource resource = TestResourceTree.Served.GOOD_PART;
    resourceInstaller.copy( resource ) ;
    final String renderedDocumentName = resource.getBaseName() + "." + HTML_EXTENSION;

    new DocumentGenerator().main(
        "testing",
        true,
        new String[] { "/" + renderedDocumentName },
        resourceInstaller.getTargetDirectory()
    ) ;

    final File renderedDocument = new File(
        new File(
            resourceInstaller.getTargetDirectory(),
            ConfigurationTools.DEFAULT_OUTPUT_DIRECTORY_NAME
        ), 
        renderedDocumentName
    ) ;
    LOG.debug( "Rendered document = '%s'", renderedDocument.getAbsolutePath() ) ;
    Assert.assertTrue( renderedDocument.exists() ) ;
  }

// =======
// Fixture
// =======

  private static final Log LOG = LogFactory.getLog( BatchTest.class ) ;

  static {
    TestResourceTree.initialize() ;
  }

  private static final String HTML_EXTENSION = RenditionMimeType.HTML.getFileExtension() ;


  private SecurityManager savedSecurityManager ;


  @Before
  public void setUp() throws IOException {
    savedSecurityManager = System.getSecurityManager() ;
    System.setSecurityManager( new NoExitSecurityManager() ) ;
  }

  @After
  public void tearDown() {
    System.setSecurityManager( savedSecurityManager ) ;
  }

  private static class NoExitSecurityManager extends SecurityManager {
    public void checkExit( final int status ) {
      throw new CannotExitVirtualMachineWhileTestingException() ;
    }

    public void checkPermission( final Permission perm ) { }
  }

  private static class CannotExitVirtualMachineWhileTestingException extends RuntimeException { }
}

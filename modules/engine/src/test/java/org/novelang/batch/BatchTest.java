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
package org.novelang.batch;

import java.io.File;
import java.io.IOException;
import java.security.Permission;

import org.fest.assertions.Assertions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.novelang.ResourcesForTests;
import org.novelang.common.filefixture.JUnitAwareResourceInstaller;
import org.novelang.common.filefixture.Resource;
import org.novelang.configuration.ConfigurationTools;
import org.novelang.configuration.parse.GenericParameters;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.produce.DocumentRequest;
import org.novelang.rendering.RenditionMimeType;
import org.novelang.testing.junit.NameAwareTestClassRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.novelang.produce.DocumentRequest.PAGEIDENTIFIER_PREFIX;

/**
 * Tests for {@link DocumentGenerator}.
 *
 * @author Laurent Caillette
 */
@RunWith( value = NameAwareTestClassRunner.class )
public class BatchTest {

  @Test( expected = CannotExitVirtualMachineWhileTestingException.class )
  public void exitWithIncorrectParameters() throws Exception {
    new DocumentGenerator().main( "testing", COMMAND_LINE_ARGUMENT_EMPTY ) ;
  }

  @Test( expected = CannotExitVirtualMachineWhileTestingException.class )
  public void exitBecauseHelpRequeted() throws Exception {
    new DocumentGenerator().main(
        "testing",
        new String[] { GenericParameters.OPTIONPREFIX + GenericParameters.HELP_OPTION_NAME }
    ) ;
  }

  @Test
  public void generateOneDocumentOk() throws Exception {
    final JUnitAwareResourceInstaller resourceInstaller = new JUnitAwareResourceInstaller() ;
    final Resource resource = ResourcesForTests.Served.GOOD_PART;
    resourceInstaller.copy( resource ) ;
    final String renderedDocumentName = resource.getBaseName() + "." + MIME_FILE_EXTENSION;

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
    LOGGER.debug( "Rendered document = '", renderedDocument.getAbsolutePath(), "'" ) ;
    Assert.assertTrue( renderedDocument.exists() ) ;
  }

  @Test
//  @Ignore( "Unfinished implementation" )
  public void generateMultipageDocumentOk() throws Exception {
    final JUnitAwareResourceInstaller resourceInstaller = new JUnitAwareResourceInstaller() ;
    final Resource novellaResource = ResourcesForTests.Multipage.MULTIPAGE_NOVELLA;
    resourceInstaller.copy( novellaResource ) ;
    final Resource opusResource = ResourcesForTests.Multipage.MULTIPAGE_OPUS;
    resourceInstaller.copy( opusResource ) ;
    final Resource stylesheetResource = ResourcesForTests.Multipage.MULTIPAGE_XSL;
    final File stylesheetFile = resourceInstaller.copy( stylesheetResource ) ;
    final String renderedDocumentName =
          opusResource.getBaseName() + "." + MIME_FILE_EXTENSION
        + "?" + DocumentRequest.ALTERNATE_STYLESHEET_PARAMETER_NAME
        + "=" + stylesheetResource.getName() 
    ;

    new DocumentGenerator().main(
        "testing",
        false,
        new String[] {
            "/" + renderedDocumentName,
            GenericParameters.OPTIONPREFIX + GenericParameters.OPTIONNAME_STYLE_DIRECTORIES,
            stylesheetFile.getParentFile().getCanonicalPath() 
        },
        resourceInstaller.getTargetDirectory()
    ) ;

    final File outputDirectory = new File(
        resourceInstaller.getTargetDirectory(),
        ConfigurationTools.DEFAULT_OUTPUT_DIRECTORY_NAME
    ) ;
    final File mainDocument = createFileObject( outputDirectory, opusResource, null ) ;
    final File ancillaryDocument0 = createFileObject( outputDirectory, opusResource, "Level-0" ) ;
    final File ancillaryDocument1 = createFileObject( outputDirectory, opusResource, "Level-1" ) ;

    assertThat( mainDocument ).exists() ;
    assertThat( ancillaryDocument0 ).exists() ;
    assertThat( ancillaryDocument1 ).exists() ;
  }


// =======
// Fixture
// =======

  private static final Logger LOGGER = LoggerFactory.getLogger( BatchTest.class );


  static {
    ResourcesForTests.initialize() ;
  }

  private static final String MIME_FILE_EXTENSION = RenditionMimeType.NOVELLA.getFileExtension() ;
  private static final String[] COMMAND_LINE_ARGUMENT_EMPTY = new String[ 0 ];


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

  private static File createFileObject(
      final File outputDirectory,
      final Resource opusResource,
      final String pageIdentifierAsString
  ) {
    return new File(
        outputDirectory,
        opusResource.getBaseName()
            + ( pageIdentifierAsString == null ? ""
                : PAGEIDENTIFIER_PREFIX + pageIdentifierAsString )
            + "." + MIME_FILE_EXTENSION
    ) ;
  }


  private static class NoExitSecurityManager extends SecurityManager {
    @Override
    public void checkExit( final int status ) {
      throw new CannotExitVirtualMachineWhileTestingException() ;
    }

    @Override
    public void checkPermission( final Permission perm ) { }
  }

  private static class CannotExitVirtualMachineWhileTestingException extends RuntimeException { }
}

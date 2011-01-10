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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.novelang.ResourcesForTests;
import org.novelang.common.filefixture.JUnitAwareResourceInstaller;
import org.novelang.common.filefixture.Resource;
import org.novelang.configuration.ConfigurationTools;
import org.novelang.configuration.parse.DocumentGeneratorParameters;
import org.novelang.configuration.parse.GenericParametersConstants;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.DefaultCharset;
import org.novelang.rendering.RenditionMimeType;
import org.novelang.rendering.multipage.MultipageFixture;
import org.novelang.testing.NoSystemExit;
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

  @Test
  public void generateOneDocumentOk() throws Exception {
    final JUnitAwareResourceInstaller resourceInstaller = new JUnitAwareResourceInstaller() ;
    final Resource resource = ResourcesForTests.Served.GOOD_PART;
    resourceInstaller.copy( resource ) ;
    final String renderedDocumentName = resource.getBaseName() + "." + MIME_FILE_EXTENSION ;

    final DocumentGeneratorParameters generatorParameters = DocumentGenerator.createParameters( 
        new String[]{ "/" + renderedDocumentName }, resourceInstaller.getTargetDirectory() ) ;
    new DocumentGenerator().main( generatorParameters ) ;

    final File renderedDocument = new File(
        new File(
            resourceInstaller.getTargetDirectory(),
            ConfigurationTools.DEFAULT_OUTPUT_DIRECTORY_NAME
        ), 
        renderedDocumentName
    ) ;
    LOGGER.debug( "Rendered document = '", renderedDocument.getAbsolutePath(), "'" ) ;
    assertThat( renderedDocument.exists() ).isTrue() ;
  }

  @Test
  public void generateMultipageDocumentOk() throws Exception {
    runMultipageRendering( ResourcesForTests.Multipage.MULTIPAGE_XSL ) ;
  }

  @Test
  public void generateMultipageDocumentWithImport() throws Exception {
    final Resource stylesheetResource = ResourcesForTests.Multipage.MULTIPAGE_IMPORTED_XSL;
    runMultipageRendering( stylesheetResource, ResourcesForTests.Multipage.MULTIPAGE_XSL ) ;
  }



// =======
// Fixture
// =======

  private static final Logger LOGGER = LoggerFactory.getLogger( BatchTest.class );

  private static final File USER_DIR = new File( SystemUtils.USER_DIR ) ;

  private final NoSystemExit noSystemExit = new NoSystemExit() ;

  static {
    ResourcesForTests.initialize() ;
  }

  private static final String MIME_FILE_EXTENSION = RenditionMimeType.NOVELLA.getFileExtension() ;
  private static final String[] COMMAND_LINE_ARGUMENT_EMPTY = new String[ 0 ];


  private static void runMultipageRendering(
      final Resource stylesheetResource,
      final Resource... otherResources
  ) throws Exception {
    final MultipageFixture multipageFixture =
        new MultipageFixture( stylesheetResource, otherResources ) ;

    final DocumentGeneratorParameters parameters = DocumentGenerator.createParameters(
        new String[] {
            multipageFixture.requestForMain().getOriginalTarget(),
            GenericParametersConstants.OPTIONPREFIX + GenericParametersConstants.OPTIONNAME_STYLE_DIRECTORIES,
            multipageFixture.getStylesheetFile().getParentFile().getCanonicalPath()
        },
        multipageFixture.getBaseDirectory()
    ) ;

    new DocumentGenerator().main( parameters ) ;

    multipageFixture.verifyGeneratedFiles() ;
  }

  
  @After
  public void tearDown() {
    noSystemExit.uninstall() ;
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

  private static void verify( final File ancillaryDocumentFile, final String mustContain )
      throws IOException
  {
    assertThat( ancillaryDocumentFile ).exists() ;
    final String fileContent = FileUtils.readFileToString(
        ancillaryDocumentFile, DefaultCharset.RENDERING.name() ) ;
    assertThat( fileContent ).contains( mustContain ) ;
  }
}

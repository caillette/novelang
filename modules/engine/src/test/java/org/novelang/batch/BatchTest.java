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
import org.junit.Rule;
import org.junit.Test;
import org.novelang.ResourcesForTests;
import org.novelang.common.filefixture.Resource;
import org.novelang.common.filefixture.ResourceInstaller;
import org.novelang.configuration.ConfigurationTools;
import org.novelang.configuration.parse.DocumentGeneratorParameters;
import org.novelang.configuration.parse.GenericParametersConstants;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.produce.DocumentRequest;
import org.novelang.produce.GenericRequest;
import org.novelang.produce.MalformedRequestException;
import org.novelang.rendering.RenditionMimeType;
import org.novelang.rendering.multipage.MultipageFixture;
import org.novelang.testing.junit.MethodSupport;

import static org.fest.assertions.Assertions.assertThat;
import static org.novelang.rendering.multipage.MultipageFixture.TargetPage.MAIN;
import static org.novelang.rendering.multipage.MultipageFixture.TargetPage.ONE;
import static org.novelang.rendering.multipage.MultipageFixture.TargetPage.ZERO;

/**
 * Tests for {@link DocumentGenerator}.
 *
 * @author Laurent Caillette
 */
public class BatchTest {

  @Test
  public void generateOneDocumentOk() throws Exception {
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


  static {
    ResourcesForTests.initialize() ;
  }

  @Rule
  public final MethodSupport methodSupport = new MethodSupport() ;

  private final ResourceInstaller resourceInstaller = new ResourceInstaller( methodSupport ) ;


  private static final String MIME_FILE_EXTENSION = RenditionMimeType.NOVELLA.getFileExtension() ;



  private void runMultipageRendering(
      final Resource stylesheetResource,
      final Resource... otherResources
  ) throws Exception {
    final MultipageFixture multipageFixture =
        new MultipageFixture( resourceInstaller, stylesheetResource, otherResources ) ;

    final DocumentGeneratorParameters parameters = DocumentGenerator.createParameters(
        new String[] {
            multipageFixture.requestFor( MAIN ).getOriginalTarget(),
            GenericParametersConstants.OPTIONPREFIX
                + GenericParametersConstants.OPTIONNAME_STYLE_DIRECTORIES,
            multipageFixture.getStylesheetFile().getParentFile().getCanonicalPath()
        },
        methodSupport.getDirectory()
    ) ;

    new DocumentGenerator().main( parameters ) ;

    final File outputDirectory =
        new File( methodSupport.getDirectory(), ConfigurationTools.DEFAULT_OUTPUT_DIRECTORY_NAME ) ;
    verify( outputDirectory, MAIN, multipageFixture ) ;
    verify( outputDirectory, ONE, multipageFixture ) ;
    verify( outputDirectory, ZERO, multipageFixture ) ;

  }

  private static void verify(
      final File outputDirectory,
      final MultipageFixture.TargetPage targetPage,
      final MultipageFixture multipageFixture
  ) throws MalformedRequestException, IOException {
    final DocumentRequest documentRequest = multipageFixture.requestFor( targetPage ) ;
    final String fileName = GenericRequest.getDocumentNameWithPageIdentifier( documentRequest )
        + "." + MultipageFixture.MIME_FILE_EXTENSION ;
    final File file = new File( outputDirectory,  fileName ) ;
    LOGGER.debug( "Verifying as ", targetPage, ": '", file.getAbsolutePath(), "'..." ) ;
    MultipageFixture.verify( targetPage, FileUtils.readFileToByteArray( file ) ) ;
  }


}

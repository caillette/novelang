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
package org.novelang.rendering.multipage;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.novelang.ResourcesForTests;
import org.novelang.common.filefixture.Resource;
import org.novelang.common.filefixture.ResourceInstaller;
import org.novelang.configuration.ConfigurationTools;
import org.novelang.outfit.DefaultCharset;
import org.novelang.produce.DocumentRequest;
import org.novelang.produce.GenericRequest;
import org.novelang.produce.MalformedRequestException;
import org.novelang.rendering.RenditionMimeType;

import static org.fest.assertions.Assertions.assertThat;
import static org.novelang.produce.DocumentRequest.PAGEIDENTIFIER_PREFIX;

/**
 * @author Laurent Caillette
 */
public class MultipageFixture {

  private final ResourceInstaller resourceInstaller ;
  private final Resource stylesheetResource ;
  private final File stylesheetFile ;
  protected final Resource opusResource ;

  private final File mainDocument ;
  private final File ancillaryDocument0 ;
  private final File ancillaryDocument1 ;
  private final File outputDirectory;

  public MultipageFixture(
      final ResourceInstaller resourceInstaller,
      final Resource stylesheetResource,
      final Resource... otherResources
  ) throws Exception {
    this.resourceInstaller = resourceInstaller ;
    this.stylesheetResource = stylesheetResource ;
    for( final Resource otherResource : otherResources ) {
      resourceInstaller.copy( otherResource ) ;
    }
    final Resource novellaResource = ResourcesForTests.Multipage.MULTIPAGE_NOVELLA;
    resourceInstaller.copy( novellaResource ) ;
    this.opusResource = ResourcesForTests.Multipage.MULTIPAGE_OPUS;
    resourceInstaller.copy( opusResource ) ;
    stylesheetFile = resourceInstaller.copy( stylesheetResource ) ;

    outputDirectory = new File(
        resourceInstaller.getTargetDirectory(),
        ConfigurationTools.DEFAULT_OUTPUT_DIRECTORY_NAME
    ) ;
    mainDocument = createFileObject( outputDirectory, opusResource, null ) ;
    ancillaryDocument0 = createFileObject( outputDirectory, opusResource, "Level-0" ) ;
    ancillaryDocument1 = createFileObject( outputDirectory, opusResource, "Level-1" ) ;

  }

  public DocumentRequest requestForMain() throws MalformedRequestException {
    return ( DocumentRequest ) GenericRequest.parse(
        "/"
      + opusResource.getBaseName() + "." + MIME_FILE_EXTENSION
      + "?" + DocumentRequest.ALTERNATE_STYLESHEET_PARAMETER_NAME
      + "=" + this.stylesheetResource.getName()
    ) ;
  }

  public DocumentRequest requestForAncillaryDocument0() throws MalformedRequestException {
    return requestForAncillaryDocument( 0 ) ;
  }

  public DocumentRequest requestForAncillaryDocument1() throws MalformedRequestException {
    return requestForAncillaryDocument( 1 ) ;
  }

  private DocumentRequest requestForAncillaryDocument( final int documentIndex )
      throws MalformedRequestException
  {
    return ( DocumentRequest ) GenericRequest.parse(
        "/"
      + opusResource.getBaseName()
      + PAGEIDENTIFIER_PREFIX + "Level-" + documentIndex
      + "." + MIME_FILE_EXTENSION
      + "?" + DocumentRequest.ALTERNATE_STYLESHEET_PARAMETER_NAME
      + "=" + this.stylesheetResource.getName()
    ) ;
  }

  public File getStylesheetFile() {
    return stylesheetFile ;
  }

  public File getOutputDirectory() {
    return outputDirectory ;
  }

  public File getBaseDirectory() {
    return resourceInstaller.getTargetDirectory() ;
  }

  public File getMainDocumentFile() {
    return mainDocument ;
  }

  public File getAncillaryDocument0File() {
    return ancillaryDocument0 ;
  }

  public File getAncillaryDocument1File() {
    return ancillaryDocument1 ;
  }

  public void verifyGeneratedFiles() throws IOException {
    assertThat( mainDocument ).exists() ;
    verify( ancillaryDocument0, "Level-0/opus/level[1]" ) ;
    verify( ancillaryDocument1, "Level-1/opus/level[2]" ) ;
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

  private static final String MIME_FILE_EXTENSION = RenditionMimeType.NOVELLA.getFileExtension() ;

}

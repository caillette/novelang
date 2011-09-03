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

import static org.fest.assertions.Assertions.assertThat;
import static org.novelang.produce.DocumentRequest.PAGEIDENTIFIER_PREFIX;

import org.novelang.ResourcesForTests;
import org.novelang.common.filefixture.Resource;
import org.novelang.common.filefixture.ResourceInstaller;
import org.novelang.outfit.DefaultCharset;
import org.novelang.produce.DocumentRequest;
import org.novelang.produce.GenericRequest;
import org.novelang.produce.MalformedRequestException;
import org.novelang.rendering.RenditionMimeType;

/**
 * Lots of things to test multipage.
 * Defines one Opus which has 2 Levels, that give 1 {@link TargetPage#MAIN} page and two
 * ancillary pages. 
 *
 * @author Laurent Caillette
 */
public class MultipageFixture {

  private final Resource stylesheetResource ;
  private final File stylesheetFile ;
  protected final Resource opusResource ;

  public static final String MIME_FILE_EXTENSION = RenditionMimeType.NOVELLA.getFileExtension() ;

  public MultipageFixture(
      final ResourceInstaller resourceInstaller,
      final Resource stylesheetResource,
      final Resource... otherResources
  ) throws Exception {
    this.stylesheetResource = stylesheetResource ;
    for( final Resource otherResource : otherResources ) {
      resourceInstaller.copy( otherResource ) ;
    }
    final Resource novellaResource = ResourcesForTests.Multipage.MULTIPAGE_NOVELLA;
    resourceInstaller.copy( novellaResource ) ;
    this.opusResource = ResourcesForTests.Multipage.MULTIPAGE_OPUS;
    resourceInstaller.copy( opusResource ) ;
    stylesheetFile = resourceInstaller.copy( stylesheetResource ) ;

    
  }

  public DocumentRequest requestFor( final TargetPage targetPage ) throws MalformedRequestException {
    int index = 1 ;
    switch( targetPage ) {
      case MAIN :
        return ( DocumentRequest ) GenericRequest.parse(
            "/"
          + opusResource.getBaseName() + "." + MIME_FILE_EXTENSION
          + "?" + DocumentRequest.ALTERNATE_STYLESHEET_PARAMETER_NAME
          + "=" + this.stylesheetResource.getName()
        ) ;

      case ZERO :
        index = 0 ; // Stupid but saves one more instance method.
      case ONE:
        return ( DocumentRequest ) GenericRequest.parse(
            "/"
          + opusResource.getBaseName()
          + PAGEIDENTIFIER_PREFIX + "Level-" + index
          + "." + MIME_FILE_EXTENSION
          + "?" + DocumentRequest.ALTERNATE_STYLESHEET_PARAMETER_NAME
          + "=" + this.stylesheetResource.getName()
        ) ;
      default :
        throw new IllegalArgumentException( "Unsupported: " + targetPage ) ;
    }
  }


  public File getStylesheetFile() {
    return stylesheetFile ;
  }


  public static void verify( final TargetPage targetPage, final byte[] bytes ) {
    switch( targetPage ) {
      case MAIN :
        assertThat( bytes ).isNotEmpty() ;
        break;
      case ZERO:
        verify( "Level-0/opus/level[1]", bytes ) ;
        break;
      case ONE:
        verify( "Level-1/opus/level[2]", bytes ) ;
        break;
    }
  }


  private static void verify( final String mustContain, final byte[] bytes ) {
    assertThat( new String( bytes, DefaultCharset.RENDERING ) ).contains( mustContain ) ;
  }



  public enum TargetPage {
    MAIN, ZERO, ONE
  }

}

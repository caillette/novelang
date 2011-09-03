/*
 * Copyright (C) 2011 Laurent Caillette
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

package org.novelang.configuration;

import java.io.File;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import org.novelang.ResourcesForTests;
import org.novelang.common.filefixture.ResourceInstaller;
import org.novelang.configuration.parse.ArgumentException;
import org.novelang.configuration.parse.DaemonParameters;
import org.novelang.configuration.parse.GenericParametersConstants;
import org.novelang.outfit.loader.ResourceLoader;
import org.novelang.outfit.loader.ResourceName;
import org.novelang.testing.junit.MethodSupport;

/**
 * Test for {@link ConfigurationTools} dedicated to style directories, which comes with some
 * subtle resource loading mechanism.
 * 
 * @author Laurent Caillette
 */
public class StyleDirectoriesTest {
  
  @Test
  public void findDefaultStyleDirectory() throws ArgumentException {
    resourceInstaller.copyWithPath( ResourcesForTests.Served.Style.VOID_XSL ) ;
    final File baseDirectory = resourceInstaller.createFileObject( ResourcesForTests.Served.dir ) ;
    final DaemonParameters parameters = new DaemonParameters( baseDirectory ) ;
    final ResourceLoader resourceLoader = ConfigurationTools.createResourceLoader( parameters ) ;
    Assert.assertNotNull( resourceLoader.getInputStream( 
        ResourcesForTests.Served.Style.VOID_XSL.getResourceName() ) ) ;
  }

  @Test
  public void findResourceFromClassloader() throws ArgumentException {
    final DaemonParameters parameters = 
        new DaemonParameters( resourceInstaller.getTargetDirectory() ) ;
    final ResourceLoader resourceLoader = ConfigurationTools.createResourceLoader( parameters ) ;
    Assert.assertNotNull( resourceLoader.getInputStream( new ResourceName(
        ResourcesForTests.MainResources.Style.DEFAULT_PDF_XSL.getName() ) ) ) ;
  }

  @Test
  public void findResourceAmongMultipleDeclaredDirectories() throws ArgumentException {
    // Any two different directories containing resources would make it.
    final File resource1 = resourceInstaller.copyWithPath( 
        ResourcesForTests.FontStructure.Alternate.MONO_BOLD_OBLIQUE ) ;
    final File resource2 = resourceInstaller.copyWithPath( 
        ResourcesForTests.FontStructure.Fonts.MONO ) ;
    
    final DaemonParameters parameters = new DaemonParameters( 
        resourceInstaller.getTargetDirectory(),
        GenericParametersConstants.OPTIONPREFIX + GenericParametersConstants.OPTIONNAME_STYLE_DIRECTORIES,
        resource1.getParentFile().getAbsolutePath(), 
        resource2.getParentFile().getAbsolutePath() 
    ) ;
    final ResourceLoader resourceLoader = ConfigurationTools.createResourceLoader( parameters ) ;
    Assert.assertNotNull( resourceLoader.getInputStream( new ResourceName(
        resource2.getName() ) ) ) ;
  }

// =======  
// Fixture
// =======  
  
  static {
    ResourcesForTests.initialize() ;
  }
  
  @Rule
  public final MethodSupport methodSupport = new MethodSupport() ;

  private final ResourceInstaller resourceInstaller = new ResourceInstaller( methodSupport ) ;

  
}

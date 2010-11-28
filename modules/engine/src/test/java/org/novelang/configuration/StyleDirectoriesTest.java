package org.novelang.configuration;

import org.novelang.ResourcesForTests;
import org.novelang.testing.junit.NameAwareTestClassRunner;
import org.novelang.outfit.loader.ResourceLoader;
import org.novelang.outfit.loader.ResourceName;
import org.novelang.configuration.parse.DaemonParameters;
import org.novelang.configuration.parse.ArgumentException;
import org.novelang.configuration.parse.GenericParameters;
import org.novelang.common.filefixture.JUnitAwareResourceInstaller;

import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;

import java.io.File;

/**
 * Test for {@link ConfigurationTools} dedicated to style directories, which comes with some
 * subtle resource loading mechanism.
 * 
 * @author Laurent Caillette
 */
@RunWith( NameAwareTestClassRunner.class )
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
        GenericParameters.OPTIONPREFIX + GenericParameters.OPTIONNAME_STYLE_DIRECTORIES,
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
  
  private final JUnitAwareResourceInstaller resourceInstaller = new JUnitAwareResourceInstaller() ; 
  
  
}

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
package novelang.configuration;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.SystemUtils;
import org.apache.fop.apps.FOPException;
import org.junit.Assert;
import org.junit.Test;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import novelang.ScratchDirectoryFixture;
import novelang.TestResourceTree;
import static novelang.TestResourceTree.FontStructure;
import novelang.common.filefixture.Filer;
import novelang.common.filefixture.Relativizer;
import novelang.common.filefixture.ResourceSchema;
import novelang.configuration.parse.ArgumentException;
import novelang.configuration.parse.DocumentGeneratorParameters;
import novelang.configuration.parse.DaemonParameters;
import static novelang.configuration.parse.DaemonParameters.OPTIONNAME_HTTPDAEMON_PORT;
import static novelang.configuration.parse.DaemonParameters.OPTIONNAME_HTTPDAEMON_SERVEREMOTES;
import novelang.configuration.parse.GenericParameters;
import static novelang.configuration.parse.GenericParameters.OPTIONPREFIX;
import static novelang.configuration.parse.GenericParameters.OPTIONNAME_CONTENT_ROOT;
import novelang.produce.DocumentRequest;
import novelang.system.DefaultCharset;

/**
 * Tests for {@link ConfigurationTools}.
 *
 * TODO add tests for:
 *   {@link GenericParameters#getLogDirectory()}
 *   {@link GenericParameters#getStyleDirectory()}
 *   {@link GenericParameters#getHyphenationDirectory()} 
 *
 * @author Laurent Caillette
 */
public class ConfigurationToolsTest {

// ===================
// DaemonConfiguration
// ===================

  @Test
  public void createDaemonConfigurationWithCustomPort()
      throws ArgumentException, FOPException
  {
    final DaemonConfiguration configuration = ConfigurationTools
        .createDaemonConfiguration( createDaemonParameters(
            OPTIONPREFIX + OPTIONNAME_HTTPDAEMON_PORT,
            "8888"
        )
    ) ;
    Assert.assertEquals( 8888, configuration.getPort() ) ;
  }

  @Test
  public void createDaemonConfigurationServingEveryHost()
      throws ArgumentException, FOPException
  {
    final DaemonConfiguration configuration = ConfigurationTools
        .createDaemonConfiguration( createDaemonParameters(
            OPTIONPREFIX + OPTIONNAME_HTTPDAEMON_SERVEREMOTES
        )
    ) ;
    Assert.assertEquals( true, configuration.getServeRemotes() ) ;
  }

  @Test
  public void createDaemonConfigurationFromDefaults()
      throws ArgumentException, FOPException
  {
    final DaemonConfiguration configuration =
        ConfigurationTools.createDaemonConfiguration( createDaemonParameters() ) ;
    Assert.assertEquals( ConfigurationTools.DEFAULT_HTTP_DAEMON_PORT, configuration.getPort() ) ;
    Assert.assertEquals(
        DefaultCharset.RENDERING,
        configuration.getProducerConfiguration().getRenderingConfiguration().getDefaultCharset()
    ) ;
    Assert.assertFalse( configuration.getServeRemotes() ) ;
  }


// ==================
// BatchConfiguration
// ==================

  @Test( expected = ArgumentException.class )
  public void createBatchConfigurationWithNoDocumentRequest()
      throws ArgumentException, FOPException
  {
    ConfigurationTools.createDocumentGeneratorConfiguration( createBatchParameters() ) ;

  }

  public void createBatchConfiguration() throws ArgumentException, FOPException {
    final DocumentGeneratorConfiguration configuration = ConfigurationTools.createDocumentGeneratorConfiguration(
        createBatchParameters( "1.html", "2.html" ) ) ;

    Assert.assertEquals( new File( SystemUtils.USER_DIR ), configuration.getOutputDirectory() ) ;

    final Iterable< DocumentRequest > documentRequests = configuration.getDocumentRequests() ;
    final Iterator<DocumentRequest> iterator = documentRequests.iterator() ;
    Assert.assertTrue( iterator.hasNext() ) ;
    Assert.assertEquals( "1.html", iterator.next().getDocumentSourceName() ) ;
    Assert.assertEquals( "2.html", iterator.next().getDocumentSourceName() ) ;
    Assert.assertFalse( iterator.hasNext() ) ;

    Assert.assertNotNull( configuration.getProducerConfiguration() ) ;

  }

// ======================
// RenderingConfiguration
// ======================

  @Test
  public void createRenderingConfigurationFromDefaultsWithNoDefaultFontsDirectory()
      throws ArgumentException, FOPException, MalformedURLException {
    // 'fonts' directory has no 'fonts' subdirectory!
    final RenderingConfiguration renderingConfiguration = ConfigurationTools
        .createRenderingConfiguration( createDaemonParameters( defaultFontsDirectory ) ) ;

    Assert.assertNotNull( renderingConfiguration.getResourceLoader() ) ;
    Assert.assertNotNull( renderingConfiguration.getFopFactory() ) ;
    checkAllFontsAreGood( renderingConfiguration.getCurrentFopFontStatus() ) ;
  }

  @Test
  public void createRenderingConfigurationFromDefaultsWithDefaultFontsDirectory()
      throws ArgumentException, FOPException, MalformedURLException
  {
    // Sure that parent of 'fonts' subdirectory has a 'fonts' subdirectory!
    final RenderingConfiguration renderingConfiguration = ConfigurationTools
        .createRenderingConfiguration(
            createDaemonParameters( defaultFontsDirectory.getParentFile() ) ) ;

    Assert.assertNotNull( renderingConfiguration.getResourceLoader() ) ;
    Assert.assertNotNull( renderingConfiguration.getFopFactory() ) ;
    checkAllFontsAreGood(
        renderingConfiguration.getCurrentFopFontStatus(),
        fontFileNameDefault1,
        fontFileNameDefault2
    ) ;

  }

  @Test
  public void createRenderingConfigurationFromCustomFontsDirectory()
      throws ArgumentException, FOPException, MalformedURLException
  {
    final DaemonParameters parameters = createDaemonParameters(
        fontStructureDirectory,
        GenericParameters.OPTIONPREFIX + GenericParameters.OPTIONNAME_FONT_DIRECTORIES,
        fontDirNameAlternate
    ) ;
    final RenderingConfiguration renderingConfiguration = ConfigurationTools
        .createRenderingConfiguration( parameters ) ;

    Assert.assertNotNull( renderingConfiguration.getResourceLoader() ) ;
    Assert.assertNotNull( renderingConfiguration.getFopFactory() ) ;
    checkAllFontsAreGood(
        renderingConfiguration.getCurrentFopFontStatus(),
        fontFileNameAlternate
    ) ;

  }

  @Test
  public void createRenderingConfigurationWithRenderingCharset() 
      throws ArgumentException, FOPException
  {
    final DaemonParameters parameters = createDaemonParameters(
        GenericParameters.OPTIONPREFIX + GenericParameters.OPTIONNAME_DEFAULT_RENDERING_CHARSET,
        ISO_8859_2.name()
    ) ;
    final RenderingConfiguration renderingConfiguration = ConfigurationTools
        .createRenderingConfiguration( parameters ) ;
    Assert.assertNotNull( renderingConfiguration.getDefaultCharset() ) ;
    Assert.assertEquals( ISO_8859_2, renderingConfiguration.getDefaultCharset() ) ;
  }


// ====================
// ContentConfiguration
// ====================

  @Test
  public void createContentConfiguration() throws ArgumentException {
    final ContentConfiguration contentConfiguration =
        ConfigurationTools.createContentConfiguration( createDaemonParameters() ) ;
    Assert.assertEquals( scratchDirectory, contentConfiguration.getContentRoot() ) ; 
  }

  @Test
  public void createContentConfigurationWithContentRoot() throws ArgumentException {
    final ContentConfiguration contentConfiguration =
        ConfigurationTools.createContentConfiguration( createDaemonParameters(
            OPTIONPREFIX + OPTIONNAME_CONTENT_ROOT,
            someEmptyContentDirectory.getName()             
        ) 
    ) ;
    Assert.assertEquals( someEmptyContentDirectory, contentConfiguration.getContentRoot() ) ; 
  }

  @Test
  public void createContentConfigurationWithSourceCharset()
      throws ArgumentException, FOPException
  {
    final DaemonParameters parameters = createDaemonParameters(
        GenericParameters.OPTIONPREFIX + GenericParameters.OPTIONNAME_DEFAULT_SOURCE_CHARSET,
        MAC_ROMAN.name()
    ) ;
    final ContentConfiguration contentConfiguration = ConfigurationTools
        .createContentConfiguration( parameters ) ;
    Assert.assertNotNull( contentConfiguration.getSourceCharset() ) ;
    Assert.assertEquals( MAC_ROMAN, contentConfiguration.getSourceCharset() ) ;
  }


// =====================
// ProducerConfiguration
// =====================

  @Test
  public void createProducerConfiguration() throws ArgumentException, FOPException {
    final ProducerConfiguration producerConfiguration =
        ConfigurationTools.createProducerConfiguration( createDaemonParameters() ) ;
    Assert.assertNotNull( producerConfiguration ) ;
    Assert.assertNotNull( producerConfiguration.getContentConfiguration() ) ;
    Assert.assertNotNull( producerConfiguration.getRenderingConfiguration() ) ;
  }


// =======
// Fixture
// =======

  private void checkAllFontsAreGood( FopFontStatus fontStatus, String... relativeFontNames )
      throws MalformedURLException
  {
    final Iterable< String > embedFilesIterable = Iterables.transform(
        fontStatus.getFontInfos(),
        FopTools.EXTRACT_EMBEDFONTINFO_FUNCTION
    ) ;
    final Set< String > embedFilesSet = Sets.newHashSet( embedFilesIterable ) ;
    Assert.assertEquals( relativeFontNames.length, embedFilesSet.size() ) ;
    for( String relativeFontName : relativeFontNames ) {
      final String fontFileUrl = createFontFileUrl( relativeFontName );
      Assert.assertTrue( fontFileUrl, embedFilesSet.contains( fontFileUrl ) ) ;
    }

  }

  private String createFontFileUrl( String fontFileName ) throws MalformedURLException {
    return scratchDirectory.getAbsoluteFile().toURI().toURL().toExternalForm()
        + fontFileName.substring( 1 );
  }

  private static final Charset ISO_8859_2 = Charset.forName( "ISO-8859-2" );
  private static final Charset MAC_ROMAN = Charset.forName( "MacRoman" );

  private final File scratchDirectory ;
  private final File someEmptyContentDirectory ;
  private final File fontStructureDirectory ;
  private final File defaultFontsDirectory ;
  private final String fontFileNameDefault1 ;
  private final String fontFileNameDefault2 ;
  private final String fontFileNameAlternate ;
  private final String fontDirNameAlternate ;

  static {
    TestResourceTree.initialize() ;
  }

  /**
   * Tested methods don't modify files so we can have the same scratch directory name for all.
   */
  public ConfigurationToolsTest() throws IOException {
    scratchDirectory = new ScratchDirectoryFixture(
        ConfigurationToolsTest.class ).getTestScratchDirectory() ;
    someEmptyContentDirectory = new File( scratchDirectory, "some-empty-content-root"  ) ;
    someEmptyContentDirectory.mkdirs() ;
    
    final Filer filer = new Filer( scratchDirectory ) ;
    filer.copyContent( FontStructure.dir ) ;
    defaultFontsDirectory = filer.createFileObject(
        FontStructure.dir,
        FontStructure.Fonts.dir
    ) ;
    fontStructureDirectory = scratchDirectory ;

    final Relativizer relativizer = ResourceSchema.relativizer( FontStructure.dir ) ;
    fontFileNameDefault1 = relativizer.apply( FontStructure.Fonts.MONO ) ;
    fontFileNameDefault2 = relativizer.apply( FontStructure.Fonts.MONO_BOLD ) ;
    fontDirNameAlternate = FontStructure.Alternate.dir.getName() ;
    fontFileNameAlternate = relativizer.apply( FontStructure.Alternate.MONO_BOLD_OBLIQUE ) ;
  }

  private DaemonParameters createDaemonParameters( String... arguments )
      throws ArgumentException
  {
    return createDaemonParameters( scratchDirectory, arguments ) ;
  }

  private DaemonParameters createDaemonParameters( File baseDirectory, String... arguments )
      throws ArgumentException
  {
    return new DaemonParameters( baseDirectory, arguments ) ;
  }

  private DocumentGeneratorParameters createBatchParameters( String... arguments )
      throws ArgumentException
  {
    return createBatchParameters( scratchDirectory, arguments ) ;
  }

  private DocumentGeneratorParameters createBatchParameters( File baseDirectory, String... arguments )
      throws ArgumentException
  {
    return new DocumentGeneratorParameters( baseDirectory, arguments ) ;
  }



}

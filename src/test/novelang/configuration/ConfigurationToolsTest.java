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
import java.util.Iterator;
import java.util.Set;
import java.nio.charset.Charset;

import org.apache.commons.lang.SystemUtils;
import org.apache.fop.apps.FOPException;
import org.junit.Assert;
import org.junit.Test;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import novelang.ScratchDirectoryFixture;
import novelang.TestResourceTools;
import static novelang.TestResourceTools.copyResourceToDirectory;
import novelang.TestResources;
import novelang.system.DefaultCharset;
import novelang.configuration.parse.ArgumentException;
import novelang.configuration.parse.BatchParameters;
import novelang.configuration.parse.DaemonParameters;
import static novelang.configuration.parse.DaemonParameters.OPTIONNAME_HTTPDAEMON_PORT;
import novelang.configuration.parse.GenericParameters;
import static novelang.configuration.parse.GenericParameters.OPTIONPREFIX;
import novelang.produce.DocumentRequest;

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
  }


// ==================
// BatchConfiguration
// ==================

  @Test( expected = ArgumentException.class )
  public void createBatchConfigurationWithNoDocumentRequest()
      throws ArgumentException, FOPException
  {
    ConfigurationTools.createBatchConfiguration( createBatchParameters() ) ;

  }

  public void createBatchConfiguration() throws ArgumentException, FOPException {
    final BatchConfiguration configuration = ConfigurationTools.createBatchConfiguration(
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
        FONT_FILE_DEFAULT_1,
        FONT_FILE_DEFAULT_2
    ) ;

  }

  @Test
  public void createRenderingConfigurationFromCustomFontsDirectory()
      throws ArgumentException, FOPException, MalformedURLException
  {
    final DaemonParameters parameters = createDaemonParameters(
        fontStructureDirectory,
        GenericParameters.OPTIONPREFIX + GenericParameters.OPTIONNAME_FONT_DIRECTORIES,
        ALTERNATE_FONTS_DIR_NAME
    ) ;
    final RenderingConfiguration renderingConfiguration = ConfigurationTools
        .createRenderingConfiguration( parameters ) ;

    Assert.assertNotNull( renderingConfiguration.getResourceLoader() ) ;
    Assert.assertNotNull( renderingConfiguration.getFopFactory() ) ;
    checkAllFontsAreGood(
        renderingConfiguration.getCurrentFopFontStatus(),
        FONT_FILE_ALTERNATE
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
      Assert.assertTrue( embedFilesSet.contains( createFontFileUrl( relativeFontName ) ) ) ;
    }

  }

  private String createFontFileUrl( String fontFileName ) throws MalformedURLException {
    return scratchDirectory.getAbsoluteFile().toURI().toURL().toExternalForm()
        + fontFileName.substring( 1 );
  }

  private static final Charset ISO_8859_2 = Charset.forName( "ISO-8859-2" );
  private static final Charset MAC_ROMAN = Charset.forName( "MacRoman" );

  private static final String FONT_STRUCTURE_DIR = TestResources.FONT_STRUCTURE_DIR ;
  private static final String DEFAULT_FONTS_DIR = TestResources.DEFAULT_FONTS_DIR ;
  private static final String FONT_FILE_DEFAULT_1 = TestResources.FONT_FILE_DEFAULT_1 ;
  private static final String FONT_FILE_DEFAULT_2 = TestResources.FONT_FILE_DEFAULT_2 ;
  private static final String ALTERNATE_FONTS_DIR_NAME = TestResources.ALTERNATE_FONTS_DIR_NAME ;
  private static final String FONT_FILE_ALTERNATE = TestResources.FONT_FILE_ALTERNATE ;
  private static final String FONT_FILE_PARENT_CHILD = TestResources.FONT_FILE_PARENT_CHILD ;
  private static final String FONT_FILE_PARENT_CHILD_BAD =
      TestResources.FONT_FILE_PARENT_CHILD_BAD ;

  private final File scratchDirectory ;
  private final File fontStructureDirectory ;
  private final File defaultFontsDirectory ;

  public ConfigurationToolsTest() throws IOException {
    scratchDirectory = new ScratchDirectoryFixture( ConfigurationToolsTest.class )
        .getTestScratchDirectory() ;

    copyResourceToDirectory( getClass(), FONT_FILE_DEFAULT_1, scratchDirectory ) ;
    copyResourceToDirectory( getClass(), FONT_FILE_DEFAULT_2, scratchDirectory ) ;
    copyResourceToDirectory( getClass(), FONT_FILE_ALTERNATE, scratchDirectory ) ;
    copyResourceToDirectory( getClass(), FONT_FILE_PARENT_CHILD, scratchDirectory ) ;
    copyResourceToDirectory( getClass(), FONT_FILE_PARENT_CHILD_BAD, scratchDirectory ) ;

    defaultFontsDirectory = TestResourceTools.getDirectoryForSure(
        scratchDirectory, DEFAULT_FONTS_DIR ) ;

    fontStructureDirectory = TestResourceTools.getDirectoryForSure(
        scratchDirectory, FONT_STRUCTURE_DIR ) ;

  }

  private final DaemonParameters createDaemonParameters( String... arguments )
      throws ArgumentException
  {
    return createDaemonParameters( scratchDirectory, arguments ) ;
  }

  private final DaemonParameters createDaemonParameters( File baseDirectory, String... arguments )
      throws ArgumentException
  {
    return new DaemonParameters( baseDirectory, arguments ) ;
  }

  private final BatchParameters createBatchParameters( String... arguments )
      throws ArgumentException
  {
    return createBatchParameters( scratchDirectory, arguments ) ;
  }

  private final BatchParameters createBatchParameters( File baseDirectory, String... arguments )
      throws ArgumentException
  {
    return new BatchParameters( baseDirectory, arguments ) ;
  }



}

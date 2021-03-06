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
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.apache.commons.lang.SystemUtils;
import org.apache.fop.apps.FOPException;
import org.fest.reflect.core.Reflection;
import org.fest.reflect.reference.TypeRef;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.novelang.ResourcesForTests.FontStructure;
import static org.novelang.configuration.parse.DaemonParameters.OPTIONNAME_HTTPDAEMON_PORT;
import static org.novelang.configuration.parse.DaemonParameters.OPTIONNAME_HTTPDAEMON_SERVEREMOTES;
import static org.novelang.configuration.parse.GenericParametersConstants.*;

import org.novelang.ResourcesForTests;
import org.novelang.common.FileTools;
import org.novelang.common.filefixture.Relativizer;
import org.novelang.common.filefixture.ResourceInstaller;
import org.novelang.common.filefixture.ResourceSchema;
import org.novelang.configuration.parse.ArgumentException;
import org.novelang.configuration.parse.DaemonParameters;
import org.novelang.configuration.parse.DocumentGeneratorParameters;
import org.novelang.configuration.parse.GenericParameters;
import org.novelang.configuration.parse.GenericParametersConstants;
import org.novelang.outfit.DefaultCharset;
import org.novelang.outfit.loader.AbstractResourceLoader;
import org.novelang.outfit.loader.ClasspathResourceLoader;
import org.novelang.outfit.loader.UrlResourceLoader;
import org.novelang.produce.DocumentRequest;
import org.novelang.testing.junit.MethodSupport;

/**
 * Tests for {@link ConfigurationTools}.
 *
 * TODO add tests for:
 *   {@link GenericParameters#getLogDirectory()}
 *   {@link GenericParameters#getStyleDirectories()}
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
    assertTrue( configuration.getServeRemotes() );
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

    final Iterable<DocumentRequest> documentRequests = configuration.getDocumentRequests() ;
    final Iterator<DocumentRequest> iterator = documentRequests.iterator() ;
    assertTrue( iterator.hasNext() ) ;
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
        .createRenderingConfiguration(
            createDaemonParameters( defaultFontsDirectory ), RenditionKinematic.DAEMON )
    ;

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
            createDaemonParameters( defaultFontsDirectory.getParentFile() ),
            RenditionKinematic.DAEMON
        )
    ;

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
        GenericParametersConstants.OPTIONPREFIX + GenericParametersConstants.OPTIONNAME_FONT_DIRECTORIES,
        fontDirNameAlternate
    ) ;
    final RenderingConfiguration renderingConfiguration = ConfigurationTools
        .createRenderingConfiguration( parameters, RenditionKinematic.DAEMON ) ;

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
        GenericParametersConstants.OPTIONPREFIX + GenericParametersConstants.OPTIONNAME_DEFAULT_RENDERING_CHARSET,
        ISO_8859_2.name()
    ) ;
    final RenderingConfiguration renderingConfiguration = ConfigurationTools
        .createRenderingConfiguration( parameters, RenditionKinematic.DAEMON ) ;
    Assert.assertNotNull( renderingConfiguration.getDefaultCharset() ) ;
    Assert.assertEquals( ISO_8859_2, renderingConfiguration.getDefaultCharset() ) ;
  }

  /**
   * Checks that style directories appear in correct order.
   * Dirty use of reflexion here. TODO: created dedicated files with known content.
   */
  @Test
  public void createWithCorrectResourceLoaderOrder() throws Exception {
    final File parent = methodSupport.getDirectory() ;
    final File directory1 = FileTools.createFreshDirectory( parent, "first" ) ;
    final File directory2 = FileTools.createFreshDirectory( parent, "second" ) ;

    final DaemonParameters parameters = createDaemonParameters(
        OPTIONPREFIX + OPTIONNAME_STYLE_DIRECTORIES,
        directory1.getAbsolutePath(),
        directory2.getAbsolutePath()
    ) ;
    final RenderingConfiguration renderingConfiguration = ConfigurationTools
        .createRenderingConfiguration( parameters, RenditionKinematic.BATCH ) ;

    final ImmutableList< AbstractResourceLoader > resourceLoaders = Reflection.method( "getAll" )
        .withReturnType( new TypeRef< ImmutableList< AbstractResourceLoader > >( ){} )
        .in( renderingConfiguration.getResourceLoader() )
        .invoke()
    ;
    assertThat( resourceLoaders.get( 0 ) ).isInstanceOf( UrlResourceLoader.class ) ;
    assertThat( resourceLoaders.get( 0 ).toString() ).contains( "first" ) ;
    assertThat( resourceLoaders.get( 1 ) ).isInstanceOf( UrlResourceLoader.class ) ;
    assertThat( resourceLoaders.get( 1 ).toString() ).contains( "second" ) ;
    assertThat( resourceLoaders.get( 2 ) ).isInstanceOf( ClasspathResourceLoader.class ) ;


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
        GenericParametersConstants.OPTIONPREFIX + GenericParametersConstants.OPTIONNAME_DEFAULT_SOURCE_CHARSET,
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
        ConfigurationTools.createProducerConfiguration(
            createDaemonParameters(),
            RenditionKinematic.DAEMON
        )
    ;
    Assert.assertNotNull( producerConfiguration ) ;
    Assert.assertNotNull( producerConfiguration.getContentConfiguration() ) ;
    Assert.assertNotNull( producerConfiguration.getRenderingConfiguration() ) ;
  }


// =======
// Fixture
// =======

  private void checkAllFontsAreGood( 
      final FopFontStatus fontStatus, 
      final String... relativeFontNames 
  )
      throws MalformedURLException
  {
    final Iterable< String > embedFilesIterable = Iterables.transform(
        fontStatus.getFontInfos(),
        FopTools.EXTRACT_EMBEDFONTINFO_FUNCTION
    ) ;
    final Set< String > embedFilesSet = Sets.newHashSet( embedFilesIterable ) ;
    Assert.assertEquals( relativeFontNames.length, embedFilesSet.size() ) ;
    for( final String relativeFontName : relativeFontNames ) {
      final String fontFileUrl = createFontFileUrl( relativeFontName );
      assertTrue( fontFileUrl, embedFilesSet.contains( fontFileUrl ) ) ;
    }

  }

  private String createFontFileUrl( final String fontFileName ) throws MalformedURLException {
    return scratchDirectory.getAbsoluteFile().toURI().toURL().toExternalForm()
        + fontFileName.substring( 1 );
  }

  private static final Charset ISO_8859_2 = Charset.forName( "ISO-8859-2" );
  private static final Charset MAC_ROMAN = Charset.forName( "MacRoman" );

  private File scratchDirectory ;
  private File someEmptyContentDirectory ;
  private File fontStructureDirectory ;
  private File defaultFontsDirectory ;
  private String fontFileNameDefault1 ;
  private String fontFileNameDefault2 ;
  private String fontFileNameAlternate ;
  private String fontDirNameAlternate ;

  static {
    ResourcesForTests.initialize() ;
  }
  @Rule
  public final MethodSupport methodSupport = new MethodSupport() {

    @Override
    protected void beforeStatementEvaluation() throws Exception {

      scratchDirectory = getDirectory() ;
      someEmptyContentDirectory = new File( scratchDirectory, "some-empty-content-root"  ) ;
      someEmptyContentDirectory.mkdirs() ;

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
  };

  private final ResourceInstaller filer = new ResourceInstaller( methodSupport ) ;

  /**
   * Tested methods don't modify files so we can have the same scratch directory name for all.
   */
  public ConfigurationToolsTest() throws IOException {
  }

  private DaemonParameters createDaemonParameters( final String... arguments )
      throws ArgumentException
  {
    return createDaemonParameters( scratchDirectory, arguments ) ;
  }

  private DaemonParameters createDaemonParameters( 
      final File baseDirectory, 
      final String... arguments 
  )
      throws ArgumentException
  {
    return new DaemonParameters( baseDirectory, arguments ) ;
  }

  private DocumentGeneratorParameters createBatchParameters( final String... arguments )
      throws ArgumentException
  {
    return createBatchParameters( scratchDirectory, arguments ) ;
  }

  private DocumentGeneratorParameters createBatchParameters( 
      final File baseDirectory, 
      final String... arguments 
  )
      throws ArgumentException
  {
    return new DocumentGeneratorParameters( baseDirectory, arguments ) ;
  }



}

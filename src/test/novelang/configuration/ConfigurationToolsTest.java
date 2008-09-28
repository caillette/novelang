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
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.apache.fop.apps.FOPException;
import org.apache.fop.fonts.EmbedFontInfo;
import novelang.configuration.parse.DaemonParameters;
import novelang.configuration.parse.ArgumentsNotParsedException;
import static novelang.configuration.parse.DaemonParameters.OPTIONNAME_HTTPDAEMON_PORT;
import static novelang.configuration.parse.GenericParameters.OPTIONPREFIX;
import novelang.ScratchDirectoryFixture;
import novelang.TestResourceTools;
import novelang.TestResources;
import static novelang.TestResourceTools.copyResourceToDirectory;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Iterables;

/**
 * Tests for {@link ConfigurationTools2}.
 *
 * @author Laurent Caillette
 */
public class ConfigurationToolsTest {

  @Test
  public void createDaemonConfigurationWithCustomPort() throws ArgumentsNotParsedException {
    final DaemonConfiguration configuration = ConfigurationTools2
        .createDaemonConfiguration( createDaemonParameters(
            OPTIONPREFIX + OPTIONNAME_HTTPDAEMON_PORT,
            "8888"
        )
    ) ;
    Assert.assertEquals( 8888, configuration.getPort() ) ;
  }

  @Test
  public void createDaemonConfigurationFromDefaults() throws ArgumentsNotParsedException {
    final DaemonConfiguration configuration =
        ConfigurationTools2.createDaemonConfiguration( createDaemonParameters() ) ;
    Assert.assertEquals( ConfigurationTools2.DEFAULT_HTTP_DAEMON_PORT, configuration.getPort() ) ;
  }

  @Test
  public void createRenderingConfigurationFromDefaultsWithNoDefaultFontsDirectory()
      throws ArgumentsNotParsedException, FOPException
  {
    // Base directory has no 'fonts' subdirectory!
    final RenderingConfiguration renderingConfiguration = ConfigurationTools2
        .createRenderingConfiguration( createDaemonParameters( defaultFontsDirectory ) ) ;
    Assert.assertNotNull( renderingConfiguration.getResourceLoader() ) ;
    Assert.assertNotNull( renderingConfiguration.getFopFactory() ) ;
    Assert.assertNotNull( renderingConfiguration.getCurrentFopFontStatus() ) ;
  }

  @Test
  public void createRenderingConfigurationFromDefaultsWithDefaultFontsDirectory()
      throws ArgumentsNotParsedException, FOPException, MalformedURLException
  {
    // Sure that parent of 'fonts' subdirectory has a 'fonts' subdirectory!
    final RenderingConfiguration renderingConfiguration = ConfigurationTools2
        .createRenderingConfiguration(
            createDaemonParameters( defaultFontsDirectory.getParentFile() ) ) ;
    Assert.assertNotNull( renderingConfiguration.getResourceLoader() ) ;
    Assert.assertNotNull( renderingConfiguration.getFopFactory() ) ;

    final FopFontStatus fontStatus = renderingConfiguration.getCurrentFopFontStatus() ;
    final Iterable< String > embedFilesIterable = Iterables.transform(
        fontStatus.getFontInfos(), FopTools.EXTRACT_EMBEDFONTINFO_FUNCTION ) ;
    final Set< String > embedFilesSet = Sets.newHashSet( embedFilesIterable ) ;

    Assert.assertEquals( 2, embedFilesSet.size() ) ;
    Assert.assertTrue( embedFilesSet.contains( createFontFileUrl( FONT_FILE_GOOD_1 ) ) ) ;
    Assert.assertTrue( embedFilesSet.contains( createFontFileUrl( FONT_FILE_GOOD_2 ) ) ) ;

  }

  private String createFontFileUrl( String fontFileName ) throws MalformedURLException {
    return scratchDirectory.getAbsoluteFile().toURI().toURL().toExternalForm()
        + fontFileName.substring( 1 );
  }

// =======
// Fixture
// =======

  private static final String FONT_FILE_GOOD_1 = TestResources.FONT_FILE_GOOD_1;
  private static final String FONT_FILE_GOOD_2 = TestResources.FONT_FILE_GOOD_2;
  private static final String FONT_FILE_PARENT_CHILD_3 = TestResources.FONT_FILE_PARENT_CHILD_3;
  private static final String FONT_FILE_PARENT_CHILD_BAD = TestResources.FONT_FILE_PARENT_CHILD_BAD;

  private final File scratchDirectory ;
  private final File defaultFontsDirectory ;

  public ConfigurationToolsTest() throws IOException {
    scratchDirectory = new ScratchDirectoryFixture( ConfigurationToolsTest.class )
        .getTestScratchDirectory() ;


    copyResourceToDirectory( getClass(), FONT_FILE_GOOD_1, scratchDirectory ) ;
    copyResourceToDirectory( getClass(), FONT_FILE_GOOD_2, scratchDirectory ) ;
    copyResourceToDirectory( getClass(), FONT_FILE_PARENT_CHILD_3, scratchDirectory ) ;
    copyResourceToDirectory( getClass(), FONT_FILE_PARENT_CHILD_BAD, scratchDirectory ) ;

    defaultFontsDirectory = new File( scratchDirectory, TestResources.FONTS_DIR ) ;
    Assert.assertTrue( defaultFontsDirectory.exists() ) ;

  }

  private final DaemonParameters createDaemonParameters( String... arguments )
      throws ArgumentsNotParsedException
  {
    return createDaemonParameters( scratchDirectory, arguments ) ;
  }

  private final DaemonParameters createDaemonParameters( File baseDirectory, String... arguments )
      throws ArgumentsNotParsedException
  {
    return new DaemonParameters( baseDirectory, arguments ) ;
  }



}

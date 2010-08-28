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

package novelang.loader;

import java.io.IOException;
import java.io.InputStream;

import novelang.ResourcesForTests;
import novelang.common.filefixture.JUnitAwareResourceInstaller;
import novelang.common.filefixture.Resource;
import novelang.logger.Logger;
import novelang.logger.LoggerFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import novelang.testing.junit.NameAwareTestClassRunner;

/**
 * @author Laurent Caillette
 */
@RunWith( value = NameAwareTestClassRunner.class )
public class UrlResourceLoaderTest {

  @Test
  public void absoluteOk() throws IOException {
    final Resource resource = ResourcesForTests.Parts.NOVELLA_ONE_WORD;
    resourceInstaller.copy( resource ) ;
    final UrlResourceLoader loader = new UrlResourceLoader(
        resourceInstaller.getTargetDirectory().toURI().toURL() ) ;
    final ResourceName resourceName = resource.getResourceName() ;
    LOGGER.debug( "Attempting to get resource '", resourceName, "'" );
    final InputStream inputStream = loader.getInputStream( resourceName ) ;
    final String resourceAsString = IOUtils.toString( inputStream ) ;
    Assert.assertFalse( StringUtils.isBlank( resourceAsString ) ) ;
  }


  @Test( expected = ResourceNotFoundException.class )
  public void urlResourceLoaderNotFound() throws IOException {
    new UrlResourceLoader( resourceInstaller.getTargetDirectory().toURI().toURL() ).getInputStream(
        new ResourceName( "doesnot.exist" ) ) ;
  }


// =======
// Fixture
// =======

  private static final Logger LOGGER = LoggerFactory.getLogger( UrlResourceLoaderTest.class );

  static {
      ResourcesForTests.initialize() ;
  }

  private final JUnitAwareResourceInstaller resourceInstaller = new JUnitAwareResourceInstaller() ;

}
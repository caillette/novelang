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

package org.novelang.outfit.loader;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.novelang.ResourcesForTests;
import org.novelang.common.filefixture.Resource;
import org.novelang.common.filefixture.ResourceInstaller;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.testing.junit.MethodSupport;

/**
 * @author Laurent Caillette
 */
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

  @Rule
  public final MethodSupport methodSupport = new MethodSupport() ;

  private final ResourceInstaller resourceInstaller = new ResourceInstaller( methodSupport ) ;

}
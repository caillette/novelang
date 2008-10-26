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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import novelang.TestResources;

/**
 * @author Laurent Caillette
 */
public class ClasspathResourceLoaderTest {

  @Test
  public void relativizedOk() throws IOException {
    final ClasspathResourceLoader loader = new ClasspathResourceLoader() ;
    final InputStream inputStream = loader.getInputStream( TestResources.ONE_WORD_RESOURCENAME ) ;
    final String resource = IOUtils.toString( inputStream ) ;
    Assert.assertFalse( StringUtils.isBlank( resource ) ) ;
  }

  @Test( expected = ResourceNotFoundException.class )
  public void classResourceLoaderNotFound() throws IOException {
    new ClasspathResourceLoader().getInputStream( new ResourceName( "doesnot.exist" ) ) ;
  }

}

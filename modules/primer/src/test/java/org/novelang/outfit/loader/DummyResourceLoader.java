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
package org.novelang.outfit.loader;

import java.io.InputStream;

import org.apache.commons.io.input.NullInputStream;

/**
 * Useful for testing
 * {@link org.novelang.outfit.loader.ResourceLoader#getInputStream(ResourceName)}'s
 * object identitiy.
 *
* @author Laurent Caillette
*/
public final class DummyResourceLoader extends AbstractResourceLoader {

  public final NullInputStream inputStream = new NullInputStream( 0 ) ;

  @Override
  protected InputStream maybeGetInputStream( final ResourceName resourceName ) {
    return inputStream ;
  }

  @Override
  protected String getMultilineDescription() {
    return getClass().getSimpleName() ;
  }
}

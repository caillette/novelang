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

/**
 * Base class for {@link ResourceLoader} supporting composition.
 * The {@link #maybeGetInputStream(ResourceName)} method doesn't throw an exception
 * on purpose.
 * <p>
 * Subclasses must log from where they're loading the resource they got in the most
 * precise manner (especially when handling multiple paths.
 *
 * @author Laurent Caillette
 */
public abstract class AbstractResourceLoader implements ResourceLoader {

  /**
   * Not sure we want subclasses outside of this package.
   */
  /*package*/ AbstractResourceLoader() { }

  @Override
  public final InputStream getInputStream( final ResourceName resourceName )
      throws ResourceNotFoundException
  {
    final InputStream inputStream ;
    try {
      inputStream = maybeGetInputStream( resourceName ) ;
    } catch( Exception e ) {
      throw new ResourceNotFoundException( resourceName, getMultilineDescription(), e ) ;
    }
    if( inputStream == null ) {
      throw new ResourceNotFoundException( resourceName, getMultilineDescription() ) ;
    }
    return inputStream ;
  }

  protected abstract InputStream maybeGetInputStream( final ResourceName resourceName ) ;

  /**
   * Returns a value which is always the same during the lifetime of the object.
   * The returned {@code String} will be logged in some way. If there are some
   * subpaths on their own line, their indent is 2 characters.
   *
   * @return a non-null, non-empty {@code String}.
   */
  protected abstract String getMultilineDescription() ;

  @Override
  public String toString() {
    return getClass().getSimpleName() ;
  }
}

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

import java.io.InputStream;

/**
 * The most simple contract for loading a resource.
 * An interface is useful here instead of URLs because
 * {@link ClasspathResourceLoader} handles lots of URLs by itself in a opaque way
 * (that should stay opaque anyways).
 * 
 * @author Laurent Caillette
 */
public interface ResourceLoader {

  /**
   * Returns an {@code InputStream} for the given resource name,
   * relative to the {@code ResourceLoader}.
   * @param resourceName a non-null object.
   * @return a non-null object.
   */
  InputStream getInputStream( ResourceName resourceName ) throws ResourceNotFoundException ;

}

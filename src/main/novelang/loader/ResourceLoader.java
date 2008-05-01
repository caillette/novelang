/*
 * Copyright (C) 2006 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.loader;

import java.io.InputStream;
import java.util.MissingResourceException;

/**
 * The most simple contract for loading a resource.
 * An interface is useful here instead of an URL because {@link ClasspathResourceLoader}
 * handles lots of URLs by itself.
 * 
 * @author Laurent Caillette
 */
public interface ResourceLoader {

  /**
   * Returns an {@code InputStream} for the given resource name,
   * relative to the {@code ResourceLoader}.
   * @param resourceName a non-null, non empty String to be interpreted as an URL fragment.
   * @return a non-null object.
   */
  InputStream getInputStream( String resourceName ) throws ResourceNotFoundException ;

}

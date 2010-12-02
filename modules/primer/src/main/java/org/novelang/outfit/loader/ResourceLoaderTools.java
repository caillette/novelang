/*
 * Copyright (C) 2010 Laurent Caillette
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

import com.google.common.base.Preconditions;

/**
 * Utility class.
 *
 * @author Laurent Caillette
 */
public class ResourceLoaderTools {

  private ResourceLoaderTools() {
    throw new Error() ;
  }

  /**
   * Returns a {@code ResourceLoader} attempting to load from first {@code ResourceLoader}, then
   * second if first failed.
   *  
   * @return a non-null object.
   */
  public static ResourceLoader compose( final ResourceLoader first, final ResourceLoader second ) {
    Preconditions.checkNotNull( first ) ;
    Preconditions.checkNotNull( second ) ;

    return new ResourceLoader() {
      @Override
      public InputStream getInputStream( final ResourceName resourceName ) {
        try {
          return first.getInputStream( resourceName ) ;
        } catch( ResourceNotFoundException firstException ) {
          try {
            return second.getInputStream( resourceName ) ;
          } catch( ResourceNotFoundException secondException ) {
            throw new ResourceNotFoundException(
                resourceName,
                ResourceNotFoundException.concatenateSearchPaths( firstException, secondException ), 
                secondException
            ) ;
          }
        }
      }
    } ;
  }

}

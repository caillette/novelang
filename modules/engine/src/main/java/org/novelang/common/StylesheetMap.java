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
package org.novelang.common;

import org.novelang.outfit.loader.ResourceName;
import org.novelang.rendering.RenditionMimeType;

/**
 * Maps a {@link org.novelang.rendering.RenditionMimeType} to a resource name corresponding
 * to a stylesheet.
 *
 * @author Laurent Caillette
 */
public interface StylesheetMap {

  /**
   * Returns a stylesheet resource name if any was defined for given type.
   * 
   * @param renditionMimeType a non-null object.
   * @return a possibly null object.
   */
  ResourceName get( RenditionMimeType renditionMimeType ) ;

  /**
   * Returns null for every call. 
   */
  StylesheetMap EMPTY_MAP = new StylesheetMap() {
    @Override
    public ResourceName get( final RenditionMimeType renditionMimeType ) {
      return null ;
    }
  };

}

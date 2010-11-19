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
package org.novelang.configuration.fop;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.novelang.loader.ResourceName;

/**
 * Mockup of a Husk mapping to FOP configuration as it looks in
 * <a href="http://novelang.blogspot.com/2008/09/opening-access-to-fop-configuration.html">stylesheet metadata blog entry</a>.
 *
 * @author Laurent Caillette
 */
public interface FopFactoryConfiguration {

  int getTargetResolution() ;


  ImmutableSet< Renderer > getRenderers() ;

  interface Renderer {

    String getMime() ;

    ResourceName getOuputProfile() ;

    Fonts getFonts() ;

    ImmutableList< Filters > getFilters() ;


    interface Fonts {

      FontDirectory getDirectory() ;

      interface FontDirectory {

        boolean getRecursive() ;

        ResourceName getPath() ;

      }

    }

    interface Filters {

      String getType() ;

      ImmutableList< String > getValues() ;
      
    }
  }


}

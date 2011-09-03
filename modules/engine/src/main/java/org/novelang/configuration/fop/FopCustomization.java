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
package org.novelang.configuration.fop;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.novelang.outfit.loader.ResourceName;

/**
 * Represents the configuration of a {@link org.apache.fop.apps.FopFactory} for the user-defined
 * subset in XSLT stylsheet metatdata.
 * See: <a href="http://novelang.blogspot.com/2008/09/opening-access-to-fop-configuration.html">stylesheet metadata blog entry</a>.
 *
 * @author Laurent Caillette
 */
public interface FopCustomization {

  int getTargetResolution() ;
  FopCustomization withTargetResolution( int resolutionDpi ) ;

  ImmutableSet< Renderer > getRenderers() ;
  FopCustomization withRenderers( ImmutableSet< Renderer > renderers ) ;

  interface Renderer {

    String getMime() ;
    Renderer withMime( String mime ) ;

    ResourceName getOutputProfile() ;
    Renderer withOutputProfile( ResourceName resourceName ) ;

    /**
     * Instantiators should take care of initializing this member to a non-null value.
     * @return a non-null object by convention.
     */
    ImmutableList< FontsDirectory > getFontsDirectories() ;
    Renderer withFontsDirectories( ImmutableList< FontsDirectory > fontsDirectory ) ;

    interface FontsDirectory {

      boolean getRecursive() ;
      FontsDirectory withRecursive( boolean recursive ) ;

      /**
       * TODO: use safer type, like {@link ResourceName} (alas it currently requires an extension),
       * or simply a {@link java.io.File} object relative to the style directory.
       */
      String getPath() ;
      FontsDirectory withPath( String path ) ;

    }


    /**
     * Instantiators should take care of initializing this member to a non-null value.
     * TODO: implement.
     * @return a non-null object by convention.
     */
    ImmutableList< Filters > getFilters() ;
    Renderer withFilters( ImmutableList< Filters > filters ) ;

    interface Filters {

      String getType() ;
      Filters withType( String type ) ;

      ImmutableList< String > getValues() ;
      Filters withValues( ImmutableList< String > values ) ;
      
    }
  }


}

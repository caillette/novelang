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
package novelang.book;

import java.io.File;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import novelang.common.StylesheetMap;
import novelang.part.Part;
import novelang.rendering.RenditionMimeType;
import novelang.loader.ResourceName;

/**
 * @author Laurent Caillette
 */
public final class Environment {

  private final File baseDirectory ;
  private final Map< RenditionMimeType, ResourceName > mappedStylesheets ;
  private final StylesheetMap stylesheetMap ;

  private Environment( Environment other ) {
    this.baseDirectory = other.baseDirectory ;
    this.mappedStylesheets = Maps.newHashMap( other.mappedStylesheets ) ;
    this.stylesheetMap = new StylesheetMap() {
      public ResourceName get( RenditionMimeType renditionMimeType ) {
        return mappedStylesheets.get( renditionMimeType ) ;
      }
    } ;
  }

  public Environment( File baseDirectory ) {
    this.baseDirectory = Objects.nonNull( baseDirectory ) ;
    this.mappedStylesheets = Maps.newHashMap() ;
    this.stylesheetMap = StylesheetMap.EMPTY_MAP ;
  }

  public File getBaseDirectory() {
    return baseDirectory;
  }

  public Environment map( RenditionMimeType renditionMimeType, String stylesheetPath ) {
    final Environment newEnvironment = new Environment( this ) ;
    newEnvironment.mappedStylesheets.put(
        Objects.nonNull( renditionMimeType ), new ResourceName( stylesheetPath ) ) ;
    return newEnvironment ;
  }

  public StylesheetMap getCustomStylesheets() {
    return stylesheetMap ;
  }

  public Environment addPart( Part part ) {
    throw new UnsupportedOperationException( "addPart" ) ;
  }

  public Iterable< Part > getParts() {
    throw new UnsupportedOperationException( "getPart" ) ;
  }

}

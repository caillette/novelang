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

package novelang.rendering;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * All supported raw resources.
 * That may be a pain to extend for foreign developers but keep it simple for now.
 *
 * @author Laurent Caillette
 */
public enum RawResource {

  js,
  htm,
  css,
  svg,
  gif,
  jpg,
  jpeg,
  png ;

  private static final Function< RawResource, String > FILE_EXTENSION_EXTRACTOR =
      new Function< RawResource, String >() {
        public String apply( final RawResource rawResource ) {
          return rawResource.name() ;
        }
      }
  ;

  public static Iterable< String > getFileExtensions() {
    final List< RawResource > elements = Arrays.asList( values() ) ;
    return Lists.transform( elements, FILE_EXTENSION_EXTRACTOR ) ;
  }


}

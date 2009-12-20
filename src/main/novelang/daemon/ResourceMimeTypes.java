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
package novelang.daemon;

import com.google.common.collect.ImmutableMap;

/**
 * Converts a file extension to a MIME type.
 * This is needed when:
 * <ul>
 *   <li>Safari 4 renders an SVG linked file.
 * </ul>
 *
 * @author Laurent Caillette
 */
public final class ResourceMimeTypes {

  private ResourceMimeTypes() { }

  private static final ImmutableMap< String, String > EXTENSIONS_FOR_MIMETYPES =
      new ImmutableMap.Builder()
      .put( "svg", "image/svg+xml" )
      .put( "js", "text/javascript" )
      .build()
  ;

  /**
   * Returns the MIME type for the given file extension.
   * @param extension a non-null, non-empty String with no leading dot.
   * @return a null object, or a valid MIME type.
   */
  public static String getMimeType( final String extension ) {
    return EXTENSIONS_FOR_MIMETYPES.get( extension ) ;
  }
}

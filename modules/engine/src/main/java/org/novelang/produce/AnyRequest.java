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
package org.novelang.produce;

/**
 * Common behavior for all requests.
 *
 * @author Laurent Caillette
 */
public interface AnyRequest {

  /**
   * Returns the original target part of the request, excluding the part requesting to
   * {@link DocumentRequest#getDisplayProblems()}.
   *
   * @return a non-null, non-empty {@code String}.
   */
  String getOriginalTarget() ;

  /**
   * Returns the document source name, including its path but excluding the
   * {@link DocumentRequest#getRenditionMimeType()} or
   * {@link ResourceRequest#getResourceExtension()}.
   *
   * @return a non-null, non-empty {@code String}.
   */
  String getDocumentSourceName() ;

  /**
   * Returns if the document is the result of rendering, or if it is a resource served as-is.
   * Depending on the value, some other properties make or don't make sense.
   */
  boolean isRendered() ;
}

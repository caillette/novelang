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

import java.io.OutputStream;

import novelang.model.renderable.Renderable;

/**
 * @author Laurent Caillette
 */
public interface Renderer {

  /**
   * Renders the book.
   * @param rendered
   * @param outputStream @return the MIME type of rendered book (useful when it changes to text or HTML for
   *     displaying problems).
   */
  void render(
      Renderable rendered,
      OutputStream outputStream
  ) ;

  RenditionMimeType getMimeType() ;

}

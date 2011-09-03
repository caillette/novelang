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

package org.novelang.rendering;

import java.io.File;
import java.io.OutputStream;

import org.novelang.common.Renderable;
import org.novelang.common.metadata.Page;
import org.novelang.rendering.multipage.PagesExtractor;

/**
 * @author Laurent Caillette
 */
public interface Renderer extends PagesExtractor {

  /**
   * Renders the book.
   *
   * @param rendered cannot be null.
   * @param outputStream cannot be null.
   * @param page may be null, if {@link #extractPages(org.novelang.common.SyntacticTree)}
   *          returned an empty {@code Map}.
   * @param contentDirectory cannot be null. The base directory for resolving embedded resources.
   */
  void render(
      Renderable rendered,
      OutputStream outputStream,
      Page page,
      File contentDirectory
  ) throws Exception ;

  RenditionMimeType getMimeType() ;

}

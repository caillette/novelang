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

import com.google.common.collect.ImmutableSet;
import org.novelang.designator.Tag;
import org.novelang.outfit.loader.ResourceName;
import org.novelang.rendering.RenditionMimeType;
import org.novelang.rendering.multipage.PageIdentifier;

/**
 * The behavior of a document request.
 *
 * @author Laurent Caillette
 */
public interface DocumentRequest extends AnyRequest {

  /**
   * Never null if {@link #isRendered()} is true, always null if {@link #isRendered()} is false.
   */
  RenditionMimeType getRenditionMimeType();

  /**
   * If {@link #isRendered()} is true, maybe non-null if there is an alternate stylesheet.
   * Always null if {@link #isRendered()} is false.
   */
  ResourceName getAlternateStylesheet();

  /**
   * Never null if {@link #isRendered()} is true (but may be empty then).
   * Always null if {@link #isRendered()} is false.
   */
  ImmutableSet< Tag > getTags() ;

  /**
   * If {@link #isRendered()} is true, maybe true or false.
   * Always false if {@link #isRendered()} is false.
   */
  boolean getDisplayProblems();


  /**
   * Maybe null, and always null if {@link #isRendered()} is false.
   */
  PageIdentifier getPageIdentifier() ;

}

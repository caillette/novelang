/*
 * Copyright (C) 2006 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.model.structural;

/**
 * @author Laurent Caillette
 */
public interface StructuralInclusion {

  /**
   * Adds a reference to several paragraphs.
   * @see #addParagraph(int) for meaning of parameters.
   */
  void addParagraphRange( int from, int to ) ;

  /**
   * Adds a reference to one paragraph.
   * @param index 1 means first, 0 means last, less than 0 means reverse count from last.
   */
  void addParagraph( int index ) ;
}

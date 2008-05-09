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

package novelang.model.structural;

import novelang.model.common.Location;
import novelang.model.common.LocationFactory;

/**
 * @author Laurent Caillette
 */
public interface StructuralInclusion extends LocationFactory {

  Location createLocation( int line, int column ) ;

  /**
   * Adds a reference to several paragraphs.
   * @see #addParagraph(novelang.model.common.Location, int) for meaning of parameters.
   */
  void addParagraphRange( Location location, int from, int to ) ;

  /**
   * Adds a reference to one paragraph.
   * @param index 1 means first, 0 means last, less than 0 means reverse count from last.
   */
  void addParagraph( Location location, int index ) ;

  void setCollateWithPrevious( boolean collate ) ;
}

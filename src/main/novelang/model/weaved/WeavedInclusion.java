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

package novelang.model.weaved;

import java.util.Map;

import novelang.model.common.Tree;

/**
 * @author Laurent Caillette
 */
public interface WeavedInclusion {

  /**
   * This should be called only once as it has the side-effect of reporting errors.
   * @param total the number of paragraphs on which to apply ranges and indexes.
   */
  Iterable< Integer >  calculateParagraphIndexes( int total ) ;

  Iterable< Tree > buildTrees( Map< String, Tree > identifiers ) ;

}

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

package org.novelang.common.tree;

import com.google.common.base.Predicate;

/**
 * Thrown when the filtering of a {@link RobustPath} gives nothing. 
 * 
 * @author Laurent Caillette
 */
public class FilterException extends RuntimeException {

  public FilterException( final Treepath treepath, final Predicate predicate ) {
    super( "Predicate " + predicate + " applied on " + treepath + " gives 0 child" ) ;
  }
}

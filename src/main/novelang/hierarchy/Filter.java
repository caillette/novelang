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
package novelang.hierarchy;

import java.util.Set;

import novelang.parser.NodeKind;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Used internally by {@link Hierarchizer}.
 *
 * @author Laurent Caillette
*/
/*package*/ interface Filter {

  boolean isMoveable( NodeKind nodeKind ) ;

  /**
   * Used internally by {@link novelang.hierarchy.Hierarchizer}.
   *
   * @author Laurent Caillette
   */
  class YesFilter implements Filter {
    public boolean isMoveable( NodeKind nodeKind ) {
      return true ;
    }
  }

  /**
   * Used internally by {@link novelang.hierarchy.Hierarchizer}.
   *
   * @author Laurent Caillette
   */
  class ExclusionFilter implements Filter {

    private final Set< NodeKind > excluded ;

    public ExclusionFilter( NodeKind... excluded ) {
      this.excluded = ImmutableSet.copyOf( Sets.newHashSet( excluded ) ) ;
    }

    public boolean isMoveable( NodeKind nodeKind ) {
      return ! excluded.contains( nodeKind ) ;
    }
  }
}

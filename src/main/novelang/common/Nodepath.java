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

package novelang.common;

import org.apache.commons.lang.ClassUtils;
import com.google.common.base.Preconditions;
import novelang.parser.NodeKind;

/**
  * Represents the stack of Node Kinds preceding current tree.
 */
public class Nodepath {

  final int depth ;
  final NodeKind current ;
  final Nodepath ancestor ;

  public Nodepath() {
    depth = 0 ;
    current = null ;
    ancestor = null ;
  }

  public Nodepath( final NodeKind current ) {
    depth = 1 ;
    this.current = Preconditions.checkNotNull( current ) ;
    ancestor = null ;
  }

  public Nodepath( final Nodepath ancestor, final NodeKind current ) {
    depth = ancestor.getDepth() + 1 ;
    this.current = Preconditions.checkNotNull( current ) ;
    this.ancestor = ancestor ;
  }

  public int getDepth() {
    return depth ;
  }

  public Nodepath getAncestor() {
    return ancestor ;
  }

  public NodeKind getCurrent() {
    return current;
  }

  private String getPathAsString() {
    return
        current.name() +
        ( null == ancestor ? "" : "->" + ancestor.getPathAsString() ) ;
  }

  @Override
  public String toString() {
    return ClassUtils.getShortClassName( getClass() ) + ":" + getPathAsString() ;
  }
}

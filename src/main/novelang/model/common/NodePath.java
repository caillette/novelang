/*
 * Copyright (C) 2008 Laurent Caillette
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
package novelang.model.common;

import org.apache.commons.lang.ClassUtils;
import novelang.model.common.NodeKind;
import com.google.common.base.Objects;

/**
   * Represents the stack of Node Kinds preceding current tree.
 */
public class NodePath {

  final int depth ;
  final NodeKind current ;
  final NodePath ancestor ;

  public NodePath() {
    depth = 0 ;
    current = null ;
    ancestor = null ;
  }

  public NodePath( NodeKind current ) {
    depth = 1 ;
    this.current = Objects.nonNull( current ) ;
    ancestor = null ;
  }

  public NodePath( NodePath ancestor, NodeKind current ) {
    depth = ancestor.getDepth() + 1 ;
    this.current = Objects.nonNull( current ) ;
    this.ancestor = ancestor ;
  }

  public int getDepth() {
    return depth ;
  }

  public NodeKind getCurrent() {
    return current;
  }

  private String getPathAsString() {
    return null == ancestor ? "" : ancestor.getPathAsString() + "->" + current.name() ;
  }

  @Override
  public String toString() {
    return ClassUtils.getShortClassName( getClass() ) + ":" + getPathAsString() ;
  }
}

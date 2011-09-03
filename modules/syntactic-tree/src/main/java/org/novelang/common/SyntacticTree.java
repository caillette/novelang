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

package org.novelang.common;

import java.util.Set;

import org.novelang.common.tree.StorageTypeProvider;
import org.novelang.common.tree.Tree;
import org.novelang.parser.NodeKind;

/**
 * Narrows the contract of an Abstract Syntax Tree.
 *
 * @author Laurent Caillette
 */
public interface SyntacticTree 
    extends
    Tree< SyntacticTree >,
    StorageTypeProvider< SyntacticTree >
{

  String getText() ;

  Location getLocation() ;

  /**
   * May return {@code null}.
   */
  NodeKind getNodeKind() ;

  String toStringTree() ;

  boolean isOneOf( NodeKind... nodeKind ) ;
  
  boolean isOneOf( Set< NodeKind > nodeKinds ) ;

  Iterable< ? extends SyntacticTree> getChildren() ;
}

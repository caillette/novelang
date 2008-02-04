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

/**
 * Narrows the contract over an Abstract Syntax Tree.
 * This class doesn't mean to provide a true separation from ANTLR's logic because
 * if we use another parser (that's not planned) the AST may look quite different.
 *
 * @author Laurent Caillette
 */
public interface Tree {

  /**
   * This can't be called {@code getChild()} because it would clash with
   * {@link org.antlr.runtime.tree.Tree#getChild(int)} which returns a
   * {@link org.antlr.runtime.tree.Tree}.
   */
  Tree getChildAt( int i ) ;

  int getChildCount() ;

  Iterable< Tree > getChildren() ;

  String getText() ;

  String toStringTree() ;

  String toString() ;

  Location getLocation() ;

  boolean isOneOf( NodeKind... nodeKind ) ;

}

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
package org.novelang.treemangling;

import org.novelang.common.SimpleTree;
import org.novelang.common.SyntacticTree;
import org.novelang.common.tree.Treepath;
import org.novelang.common.tree.TreepathTools;
import org.novelang.parser.NodeKind;
import static org.novelang.parser.NodeKind.*;
import org.novelang.parser.NodeKindTools;

/**
 * All contiguous List stuff becomes wrapped inside a
 *     {@link org.novelang.parser.NodeKind#_LIST_WITH_TRIPLE_HYPHEN} node.
 *
 *
 * @author Laurent Caillette
 */
public class ListMangler {



  /**
   * Rehierarchize paragraphs which are list items.
   */
  public static Treepath< SyntacticTree > rehierarchizeLists(
      final Treepath< SyntacticTree > parent
  ) {
    if( parent.getTreeAtEnd().getChildCount() > 0 ) {
      Treepath< SyntacticTree > child = Treepath.create( parent, 0 ) ;
      boolean insideList = false ;
      while( true ) {
        final NodeKind nodeKind = getKind( child ) ;
        switch( nodeKind ) {
          case PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_:
            if( insideList ) {
              child = TreepathTools.becomeLastChildOfPreviousSibling( child ).getPrevious() ;
              break ;
            } else {
              final SyntacticTree list =
                  new SimpleTree( _LIST_WITH_TRIPLE_HYPHEN, child.getTreeAtEnd() ) ;
              child = TreepathTools.replaceTreepathEnd( child, list ) ;
              insideList = true ;
            }
            break ;
          case _LEVEL :
          case LEVEL_INTRODUCER_:
            child = rehierarchizeLists( child ) ;
          default :
            insideList = false ;
            break ;
        }
        if( TreepathTools.hasNextSibling( child ) ) {
          child = TreepathTools.getNextSibling( child ) ;
        } else {
          break ;
        }
      }
      return child.getPrevious() ;
    } else {
      return parent ;
    }
  }




  private static NodeKind getKind( final Treepath< SyntacticTree > treepath ) {
    return NodeKindTools.ofRoot( treepath.getTreeAtEnd() ) ;
  }



}
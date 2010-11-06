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

/**
 * All contiguous List stuff becomes wrapped inside a
 *     {@link org.novelang.parser.NodeKind#_LIST_WITH_TRIPLE_HYPHEN} node.
 *
 *
 * @author Laurent Caillette
 */
public class ListMangler {

  private ListMangler() {
  }


  /**
   * Rehierarchize paragraphs which are list items.
   */
  public static Treepath< SyntacticTree > rehierarchizeLists(
      final Treepath< SyntacticTree > parent
  ) {
    if( parent.getTreeAtEnd().getChildCount() > 0 ) {
      Treepath< SyntacticTree > child = Treepath.create( parent, 0 ) ;
      NodeKind insideList = null ;
      while( true ) {
        final NodeKind nodeKind = getKind( child ) ;
        if( nodeKind != null ) {
          switch( nodeKind ) { // TODO: factorize.
            case PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_:
              if( insideList == _LIST_WITH_TRIPLE_HYPHEN ) {
                child = TreepathTools.becomeLastChildOfPreviousSibling( child ).getPrevious() ;
                break ;
              } else {
                final SyntacticTree list =
                    new SimpleTree( _LIST_WITH_TRIPLE_HYPHEN, child.getTreeAtEnd() ) ;
                child = TreepathTools.replaceTreepathEnd( child, list ) ;
                insideList = _LIST_WITH_TRIPLE_HYPHEN ;
              }
              break ;
            case PARAGRAPH_AS_LIST_ITEM_WITH_DOUBLE_HYPHEN_AND_NUMBER_SIGN:
              if( insideList == _LIST_WITH_DOUBLE_HYPHEN_AND_NUMBER_SIGN ) {
                child = TreepathTools.becomeLastChildOfPreviousSibling( child ).getPrevious() ;
                break ;
              } else {
                final SyntacticTree list =
                    new SimpleTree( _LIST_WITH_DOUBLE_HYPHEN_AND_NUMBER_SIGN, child.getTreeAtEnd() ) ;
                child = TreepathTools.replaceTreepathEnd( child, list ) ;
                insideList = _LIST_WITH_DOUBLE_HYPHEN_AND_NUMBER_SIGN ;
              }
              break ;
            case _LEVEL :
            case LEVEL_INTRODUCER_:
            case PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS :
              child = rehierarchizeLists( child ) ;
            default :
              insideList = null ;
              break ;
          }
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

    final SyntacticTree tree = treepath.getTreeAtEnd() ;
    final NodeKind nodeKind = tree.getNodeKind() ;
    return nodeKind ;
  }



}
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

import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.parser.NodeKind;
import novelang.parser.NodeKindTools;

/**
 * Retains nodes which have at least one of given tags, or a child with at least one of the
 * given tags.
 * 
 * @author Laurent Caillette
 */
public class TagFilter {

  public static Treepath< SyntacticTree > filter(
      Treepath< SyntacticTree > treepath,
      Set< String > tags
  ) {
    if( tags.isEmpty() ) {
      return treepath ;
    } else {
      return doFilter( treepath, tags ) ;
    }
  }

  private static Treepath< SyntacticTree > doFilter(
      Treepath< SyntacticTree > treepath,
      Set< String > tags
  ) {
    final SyntacticTree syntacticTree = treepath.getTreeAtEnd() ;
    final NodeKind nodeKind = NodeKindTools.ofRoot( syntacticTree ) ;
    switch( nodeKind.getTagBehavior() ) {

      case TERMINAL :
        if( hasTag( treepath.getTreeAtEnd(), tags ) ) {
          return treepath ;
        } else {
          return null ;
        }

      case SCOPE :
        if( hasTag( treepath.getTreeAtEnd(), tags ) ) {
          return treepath ;
        } // else treat as default.

      default :
        int childIndex = 0 ;
        while( childIndex < treepath.getTreeAtEnd().getChildCount() ) {
          final Treepath< SyntacticTree > childTreepath =
              Treepath.create( treepath, childIndex ) ;
          final Treepath< SyntacticTree > newChildTreepath = doFilter( childTreepath, tags ) ;
          if( null == newChildTreepath ) {
            treepath = TreepathTools.removeEnd( childTreepath ) ;
          } else {
            childIndex ++ ;
          }
        }
        return treepath ;

    }

  }

  private static boolean hasTag(
      SyntacticTree tree,
      Set< String > tags
  ) {
    for( SyntacticTree child : tree.getChildren() ) {
      if( child.isOneOf( NodeKind.TAG ) ) {
        if( tags.contains( child.getChildAt( 0 ).getText() ) ) {
          return true ;
        }
      }
    }
    return false ;
  }

}

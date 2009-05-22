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
import java.util.List;
import java.util.ArrayList;

import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.parser.NodeKind;
import novelang.parser.NodeKindTools;

import com.google.common.collect.Lists;

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
    final SyntacticTree tree = treepath.getTreeAtEnd();
    final NodeKind nodeKind = NodeKindTools.ofRoot( tree ) ;
    switch( nodeKind.getTagBehavior() ) {

      case TERMINAL :
        if( hasTag( tree, tags ) ) {
          return treepath ;
        } else {
          return null ;
        }

      case SCOPE :
        if( hasTag( tree, tags ) ) {
          return treepath ;
        } // else do the following:
      
      case TRAVERSABLE :
        final List< SyntacticTree > newChildList = 
            new ArrayList< SyntacticTree >( tree.getChildCount() ) ;
        
        // Build a new list of children for which filtering doesn't return null. 
        for( int childIndex = 0 ; childIndex < tree.getChildCount() ; childIndex++ ) {
          final Treepath< SyntacticTree > childTreepath = Treepath.create( treepath, childIndex ) ;
          final Treepath< SyntacticTree > newChildTreepath = doFilter( childTreepath, tags ) ;
          if( null != newChildTreepath ) {
            childIndex ++ ;
            newChildList.add( newChildTreepath.getTreeAtEnd() ) ;
          }
        }
        if( tree.getChildCount() > newChildList.size() ) {
          final SyntacticTree[] newChildArray = 
              newChildList.toArray( new SyntacticTree[newChildList.size()] ) ;
          treepath = TreepathTools.replaceTreepathEnd( treepath, tree.adopt( newChildArray ) ) ;
        }
        return treepath ;
        
      default :
        return null ;

    }

  }

  private static boolean hasTag(
      SyntacticTree tree,
      Set< String > tags
  ) {
    for( SyntacticTree child : tree.getChildren() ) {
      if( child.isOneOf( NodeKind._TAGS ) ) {
        for ( SyntacticTree tagChild : child.getChildren() ) {
          if( tags.contains( tagChild.getChildAt( 0 ).getText() ) ) {
            return true ;
          }
        }
      }
    }
    return false ;
  }

}

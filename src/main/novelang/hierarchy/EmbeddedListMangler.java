/*
 * Copyright (C) 2009 Laurent Caillette
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
package novelang.hierarchy ;

import com.google.common.base.Preconditions;
import novelang.common.SimpleTree;
import novelang.common.SyntacticTree;
import novelang.common.tree.TreeTools;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import static novelang.parser.NodeKind.*;

/**
 * Rehiererachizes embedded lists and wraps them into 
 * {@link novelang.parser.NodeKind#_EMBEDDED_LIST_WITH_HYPHEN} elements.
 *  
 * @author Laurent Caillette
 */
public class EmbeddedListMangler {
  
  /**
   * Rehierarchize embedded list items.
   */
  public static Treepath< SyntacticTree > rehierarchizeEmbeddedLists(
      final Treepath< SyntacticTree > treepathToRehierarchize
  ) {
    Treepath< SyntacticTree > currentTreepath ;
    boolean first = true ;
    {
      if( treepathToRehierarchize.getTreeAtEnd().getChildCount() > 0 ) {
        currentTreepath = Treepath.create( treepathToRehierarchize, 0 ) ;
      } else {
        return treepathToRehierarchize ;
      }
    }

    while( true ) {
      // We scan children of treepathToRehierarchize.
      // If there is one EMBEDDED_LIST_ITEM_WITH_HYPHEN_ then we do special stuff on it.
      if( currentTreepath.getTreeAtEnd().isOneOf( EMBEDDED_LIST_ITEM_WITH_HYPHEN_ ) ) {
        final int roof ;
        if( first ) {
          first = false ;
          roof = getItemDepth( currentTreepath ) ;
        } else {
          roof = 0 ;
        }
        currentTreepath = TreepathTools.addChildAt(  
            currentTreepath,
            new SimpleTree( _EMBEDDED_LIST_WITH_HYPHEN.name() ),
            currentTreepath.getIndexInPrevious()  
        ) ;
        currentTreepath = TreepathTools.getNextSibling( currentTreepath ) ;
        currentTreepath = rehierarchizeThisItem( currentTreepath, roof ) ;
      }
      if( TreepathTools.hasNextSibling( currentTreepath ) ) {
        currentTreepath = TreepathTools.getNextSibling( currentTreepath ) ;
      } else {
        return currentTreepath.getPrevious() ;
      }
    }


  }

  /**
   * This works almost like 
   * {@link novelang.hierarchy.Hierarchizer#rehierarchizeThisLevel(novelang.common.tree.Treepath, int)} 
   */
  private static Treepath< SyntacticTree > rehierarchizeThisItem(
      Treepath< SyntacticTree > item,
      int roof
  ) {
    final int depth = getItemDepth( item ) ;
    SyntacticTree itemTree = new SimpleTree( _EMBEDDED_LIST_ITEM.name() ) ;

    while( true ) {

      if( TreepathTools.hasNextSibling( item ) ) {
        final Treepath< SyntacticTree > next = TreepathTools.getNextSibling( item ) ;
        final SyntacticTree nextTree = next.getTreeAtEnd() ;
        
        if( nextTree.isOneOf( WHITESPACE_, LINE_BREAK_ ) ) {
          item = next ;
        } else if( nextTree.isOneOf( EMBEDDED_LIST_ITEM_WITH_HYPHEN_ )) {

          final int newDepth = getItemDepth( next ) ;
          if( newDepth < roof ) {
            throw new IllegalArgumentException(
                "Incorrect depth [" + newDepth + "] " +
                "for level declaration " + nextTree.getLocation()
            ) ;
          }

          if( newDepth > depth ) {    // An item of bigger depth is processed then added.
            // We get a treepath to new subitem, with collapsed subcontent.
            final Treepath< SyntacticTree > plainLevel = rehierarchizeThisItem( next, 0 ) ;
            itemTree = TreeTools.addLast( itemTree, plainLevel.getTreeAtEnd() ) ;
            
          } else  {                   // Same depth or less means we're done with this one.
            return substitute( item, itemTree ) ;
          }

        } else {
          return item ;
        }

      } else {
        return substitute( item, itemTree ) ;
      }
    }
  }
  
  /**
   * Replace the {@link novelang.parser.NodeKind#EMBEDDED_LIST_ITEM_WITH_HYPHEN_} at the end 
   * of the treepath by a {@link novelang.parser.NodeKind#_EMBEDDED_LIST_ITEM}.
   * TODO this looks like nonsense for now.
   */
  private static Treepath< SyntacticTree > substitute(
      Treepath< SyntacticTree > embeddedListItem,
      SyntacticTree levelTree
  ) {
    for( SyntacticTree child : embeddedListItem.getTreeAtEnd().getChildren() ) {
      if( ! child.isOneOf( EMBEDDED_LIST_ITEM_WITH_HYPHEN_ ) ) {
        levelTree = TreeTools.addFirst( levelTree, child ) ;
      }
    }
    return TreepathTools.replaceTreepathEnd( embeddedListItem, levelTree ) ;
  }


  

  /**
   * Returns the depth of a list item given its indentation.
   * It is based on the length of immediate left sibling, or length of immediate left sibling of
   * one ancestor.
   * Handling indentation this way avoids to mess the grammar up, tweaking the 
   * {@link novelang.parser.NodeKind#EMBEDDED_LIST_ITEM_WITH_HYPHEN_} trees with whitespace 
   * injection. This wouldn't work, when the whitespace is before the
   * {@link novelang.parser.NodeKind#PARAGRAPH_REGULAR}.
   * 
   * @param treepath a treepath of {@link novelang.parser.NodeKind#EMBEDDED_LIST_ITEM_WITH_HYPHEN_} 
   * kind.
   * @return a number equal to or greater than 0
   */
  private static int getItemDepth( Treepath< SyntacticTree > treepath ) {
    Preconditions.checkArgument( 
        treepath.getTreeAtEnd().isOneOf( EMBEDDED_LIST_ITEM_WITH_HYPHEN_ ) ) ;
    return getIndentSize( treepath ) ;
  }

  /**
   * Returns the length of immediate left sibling, or length of immediate left sibling of
   * one ancestor.
   */
  private static int getIndentSize( Treepath< SyntacticTree > treepath ) {
    final Treepath< SyntacticTree > previous = treepath.getPrevious();
    if( null == previous ) {
      return 0 ;
    }
    final int indexInPrevious = treepath.getIndexInPrevious() ;
    if( indexInPrevious > 0 ) {
      final SyntacticTree leftSiblingInHierarchy = 
          previous.getTreeAtEnd().getChildAt( indexInPrevious - 1 ) ;
      if( leftSiblingInHierarchy.isOneOf( WHITESPACE_ ) ) {
        return leftSiblingInHierarchy.getText().length() ;
      } else {
        return 0 ;
      }
    } else {
      return getIndentSize( previous ) ;
    }
  }

}

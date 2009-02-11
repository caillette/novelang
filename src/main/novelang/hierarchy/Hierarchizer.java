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

import novelang.parser.NodeKind;
import novelang.parser.NodeKindTools;
import static novelang.parser.NodeKind.*;
import novelang.common.SimpleTree;
import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.common.tree.TreeTools;
import com.google.common.base.Preconditions;

/**
 * Transforms the tree representing a Part for handling various features
 * that are too complicated to handle inside the parser.
 *
 * <ol>
 * <li>{@link NodeKind#LEVEL_INTRODUCER_} become {@link novelang.parser.NodeKind#_LEVEL}.
 * <li>All contiguous List stuff becomes wrapped inside a
 *     {@link NodeKind#_LIST_WITH_TRIPLE_HYPHEN} node.
 * </ol>
 *
 *
 * @author Laurent Caillette
 */
public class Hierarchizer {

  public static Treepath< SyntacticTree > rehierarchizeAll(
      Treepath< SyntacticTree > treepath
  ) {
    treepath = Hierarchizer.rehierarchizeLists( treepath ) ;
    treepath = Hierarchizer.rehierarchizeLevels( treepath ) ;
    return treepath ;    
  }


  /**
   * Rehierarchize paragraphs which are list items.
   */
  public static Treepath< SyntacticTree > rehierarchizeLists(
      Treepath< SyntacticTree > parent
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
                  new SimpleTree( _LIST_WITH_TRIPLE_HYPHEN.name(), child.getTreeAtEnd() ) ;
              child = TreepathTools.replaceTreepathEnd( child, list ) ;
              insideList = true ;
            }
            break ;
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


  public static Treepath< SyntacticTree > rehierarchizeLevels(
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
      // If there is one LEVEL_INTRODUCER_ then we do special stuff on it.
      if( currentTreepath.getTreeAtEnd().isOneOf( LEVEL_INTRODUCER_ ) ) {
        final int roof ;
        if( first ) {
          first = false ;
          roof = getLevelIntroducerDepth( currentTreepath.getTreeAtEnd() ) ;
        } else {
          roof = 0 ;
        }
        currentTreepath = rehierarchizeThisLevel( currentTreepath, roof ) ;
      }
      if( TreepathTools.hasNextSibling( currentTreepath ) ) {
        currentTreepath = TreepathTools.getNextSibling( currentTreepath ) ;
      } else {
        return currentTreepath.getPrevious() ;
      }
    }


  }

  /**
   * Given a {@code Treepath} to a {@link NodeKind#LEVEL_INTRODUCER_}, returns
   * another {@code Treepath} to a a {@link NodeKind#_LEVEL} node where
   * the Level Introducer and all following nodes are collapsed into.
   * <p>
   * Here is how it works.
   * The {@code levelIntroducer} "eats" following siblings that should be children of the
   * level it represents. They get collapsed in the {@link NodeKind#_LEVEL} node.
   * Because {@code Treepath} is an immutable structure, it's important to have only one
   * instance at a time to perform changes on.
   * Because the {@code Treepath} doesn't allow to keep references on trees, we use index
   * when needed.
   *
   * @param levelIntroducer a non-null {@code Treepath} with a minimum depth of 2.
   * @param roof the minimum depth, use 0 to ignore.
   * @return a non-null object.
   */
  private static Treepath< SyntacticTree > rehierarchizeThisLevel(
      Treepath< SyntacticTree > levelIntroducer,
      int roof
  ) {
    final int depth = getLevelIntroducerDepth( levelIntroducer.getTreeAtEnd() ) ;
    final int introducerIndex = levelIntroducer.getIndexInPrevious() ;
    SyntacticTree levelTree = new SimpleTree( _LEVEL.name() ) ;

    while( true ) {

      if( TreepathTools.hasNextSibling( levelIntroducer ) ) {
        // Jump to sibling  at the start of the loop because on first iteration,
        // levelIntroducerTreepath refers to the introducer itself.
        final Treepath< SyntacticTree > next = TreepathTools.getNextSibling( levelIntroducer ) ;
        final SyntacticTree nextTree = next.getTreeAtEnd() ;

        if( LEVEL_INTRODUCER_ == NodeKindTools.ofRoot( nextTree ) ) {

          final int newDepth = getLevelIntroducerDepth( nextTree ) ;
          if( newDepth < roof ) {
            throw new IllegalArgumentException(
                "Incorrect depth [" + newDepth + "] " +
                "for level declaration " + nextTree.getLocation()
            ) ;
          }

          if( newDepth > depth ) {    // An introducer of bigger depth is processed then added.
            // We get a treepath to new sublevel, with collapsed subcontent.
            final Treepath< SyntacticTree > plainLevel = rehierarchizeThisLevel( next, 0 ) ;
            // Jump backward to our introducer, deleting the sublevel to avoid duplicates.
            levelIntroducer = TreepathTools.getSiblingAt( plainLevel, introducerIndex ) ;
            levelIntroducer = TreepathTools.removeNextSibling( levelIntroducer ) ;
            // Anyway the sublevel wasn't lost!
            levelTree = TreeTools.addLast( levelTree, plainLevel.getTreeAtEnd() ) ;
            
          } else  {                   // Same depth or less means we're done with this one.
            return substitute( levelIntroducer, levelTree ) ;
          }

        } else {
          // Just eat the next sibling, moving it to current level.
          levelIntroducer = TreepathTools.removeNextSibling( levelIntroducer ) ;
          levelTree = TreeTools.addLast( levelTree, nextTree ) ;
        }

      } else {
        return substitute( levelIntroducer, levelTree ) ;
      }
    }
  }

  /**
   * Replace the {@link NodeKind#LEVEL_INTRODUCER_} at the end of the treepath by a
   * {@link NodeKind#_LEVEL}, while retaining all of introducer's children except
   * {@link NodeKind#LEVEL_INTRODUCER_INDENT_}.
   */
  private static Treepath< SyntacticTree > substitute(
      Treepath< SyntacticTree > levelIntroducer,
      SyntacticTree levelTree
  ) {
    for( SyntacticTree child : levelIntroducer.getTreeAtEnd().getChildren() ) {
      if( ! child.isOneOf( LEVEL_INTRODUCER_INDENT_ ) ) {
        levelTree = TreeTools.addFirst( levelTree, child ) ;
      }
    }
    return TreepathTools.replaceTreepathEnd( levelIntroducer, levelTree ) ;
  }


  private static NodeKind getKind( Treepath< SyntacticTree > treepath ) {
    return NodeKindTools.ofRoot( treepath.getTreeAtEnd() ) ;
  }


  /**
   * Returns the depth of a level given its indentation.
   *  
   * @param tree a tree of {@link NodeKind#LEVEL_INTRODUCER_} kind.
   * @return a number equal to or greater than 1
   */
  private static int getLevelIntroducerDepth( SyntacticTree tree ) {
    Preconditions.checkArgument( tree.isOneOf( LEVEL_INTRODUCER_ ) ) ;
    Preconditions.checkArgument( tree.getChildCount() > 0 ) ;
    final SyntacticTree indentTree = tree.getChildAt( 0 ) ;
    Preconditions.checkArgument( indentTree.isOneOf( LEVEL_INTRODUCER_INDENT_ ) ) ;
    Preconditions.checkArgument( indentTree.getChildCount() == 1 ) ;
    final String indent = indentTree.getChildAt( 0 ).getText() ;
    Preconditions.checkArgument( indent.startsWith( "=" ) ) ;
    Preconditions.checkArgument( indent.length() > 1 ) ;
    return indent.length() - 1 ;
  }

}

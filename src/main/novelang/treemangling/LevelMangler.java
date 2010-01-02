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
package novelang.treemangling;

import com.google.common.base.Preconditions;
import novelang.common.SimpleTree;
import novelang.common.SyntacticTree;
import novelang.common.tree.TreeTools;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.parser.NodeKind;
import static novelang.parser.NodeKind.*;
import novelang.parser.NodeKindTools;

/**
 * {@link NodeKind#LEVEL_INTRODUCER_} become {@link novelang.parser.NodeKind#_LEVEL}.
 *
 *
 * @author Laurent Caillette
 */
public class LevelMangler {

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
      final int roof
  ) {
    final int depth = getLevelIntroducerDepth( levelIntroducer.getTreeAtEnd() ) ;
    final int introducerIndex = levelIntroducer.getIndexInPrevious() ;
    final SyntacticTree levelIntroducerTree = levelIntroducer.getTreeAtEnd() ;
    SyntacticTree levelTree = new SimpleTree( _LEVEL.name(), levelIntroducerTree.getLocation() ) ;

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
      final Treepath< SyntacticTree > levelIntroducer,
      SyntacticTree levelTree
  ) {
    for( final SyntacticTree child : levelIntroducer.getTreeAtEnd().getChildren() ) {
      if( ! child.isOneOf( LEVEL_INTRODUCER_INDENT_ ) ) {
        levelTree = TreeTools.addFirst( levelTree, child ) ;
      }
    }
    final Treepath< SyntacticTree > treepathWithoutIntroducer = 
        TreepathTools.replaceTreepathEnd( levelIntroducer, levelTree ) ;
    return treepathWithoutIntroducer ;
  }


  /**
   * Returns the depth of a level given its indentation.
   *  
   * @param tree a tree of {@link NodeKind#LEVEL_INTRODUCER_} kind.
   * @return a number equal to or greater than 1
   */
  private static int getLevelIntroducerDepth( final SyntacticTree tree ) {
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

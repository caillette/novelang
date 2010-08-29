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

import org.novelang.common.SyntacticTree;
import org.novelang.common.SimpleTree;
import org.novelang.common.tree.Treepath;
import org.novelang.common.tree.TreepathTools;
import org.novelang.parser.NodeKind;

import static org.novelang.parser.NodeKind.*;
import com.google.common.collect.ImmutableSet;

/**
 * Adds or removes various kinds of separators under various conditions.
 *
 * @author Laurent Caillette
 */
public final class SeparatorsMangler {

  private static final ImmutableSet< NodeKind > BLOCKS_OF_LITERAL = ImmutableSet.of(
      BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
      BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS
  ) ;

// ==================  
// Whitespace removal
// ==================  
  
  /**
   * Removes {@link org.novelang.parser.NodeKind#WHITESPACE_} and
   * {@link org.novelang.parser.NodeKind#LINE_BREAK_} tokens in order to ease comparison.
   */
  public static SyntacticTree removeSeparators( final SyntacticTree tree ) {
    return removeSeparators( Treepath.create( tree ) ).getTreeAtEnd() ;
  }
  
  public static Treepath< SyntacticTree > removeSeparators( Treepath< SyntacticTree > treepath ) {
    int index = 0 ;
    while( index < treepath.getTreeAtEnd().getChildCount() ) {
      final SyntacticTree child = treepath.getTreeAtEnd().getChildAt( index ) ;
      final Treepath< SyntacticTree > childTreepath = Treepath.create( treepath, index ) ;
      if( child.isOneOf( NodeKind.WHITESPACE_, NodeKind.LINE_BREAK_ ) ) {
        treepath = TreepathTools.removeEnd( childTreepath ) ;
      } else {
        treepath = removeSeparators( childTreepath ).getPrevious() ;
        index++ ;
      }
    }
    return treepath ;
  }



// ==============================  
// Mandatory whitespace insertion
// ==============================  

  private static final SimpleTree MANDATORY_WHITESPACE_TREE = 
      new SimpleTree( _PRESERVED_WHITESPACE ) ;
  
  /**
   * Inserts a {@link NodeKind#_PRESERVED_WHITESPACE} before a whitespace-preceded apostrophe.
   */
  public static Treepath< SyntacticTree > insertMandatoryWhitespaceNearApostrophe(
      Treepath< SyntacticTree > treepath
  ) {
    final SyntacticTree tree = treepath.getTreeAtEnd() ;
    if( tree.isOneOf( APOSTROPHE_WORDMATE ) ) {
      treepath = insertMandatoryWhitespaceIfNeeded( treepath, SiblingTraverser.FORWARD ) ;
      treepath = insertMandatoryWhitespaceIfNeeded( treepath, SiblingTraverser.BACKWARD ) ;
    } else if( ! tree.isOneOf( TreeManglingConstants.NON_TRAVERSABLE_NODEKINDS ) ){
      int childIndex = 0 ;
      while( true ) {
        if( childIndex < treepath.getTreeAtEnd().getChildCount() ) {
          treepath = insertMandatoryWhitespaceNearApostrophe(
              Treepath.create( treepath, childIndex ) ).getPrevious() ;
          childIndex++ ;
        } else {
          break ;
        }
      }
    }
    return treepath ;
  }

  private static Treepath< SyntacticTree > insertMandatoryWhitespaceIfNeeded(
      final Treepath< SyntacticTree > treepath,
      final SiblingTraverser walker
  ) {
    Treepath< SyntacticTree > preceding = treepath ;
    boolean foundWhitespace = false ;
    while( true ) {
      preceding = walker.apply( preceding ) ;
      if( null == preceding ) {
        return treepath ;
      } else {
        if( preceding.getTreeAtEnd().isOneOf( WHITESPACE_ ) ) {
          foundWhitespace = true ;
        } else if( foundWhitespace ) {
          if( preceding.getTreeAtEnd().isOneOf( WORD_ ) ) {
            return TreepathTools.addChildAt(
                treepath.getPrevious(),
                MANDATORY_WHITESPACE_TREE,
                treepath.getIndexInPrevious() + walker.getOffset()
            ) ;
          } else {
            return treepath ;
          }
        } else {
          return treepath ;
        }
      }
    }
  }



}

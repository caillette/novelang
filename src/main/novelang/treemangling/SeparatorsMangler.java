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

import novelang.common.SyntacticTree;
import novelang.common.SimpleTree;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.parser.NodeKind;
import novelang.parser.NodeKindTools;
import static novelang.parser.NodeKind.*;

/**
 * @author Laurent Caillette
 */
public final class SeparatorsMangler {

// ==================  
// Whitespace removal
// ==================  
  
  /**
   * Removes {@link novelang.parser.NodeKind#WHITESPACE_} and {@link novelang.parser.NodeKind#LINE_BREAK_}
   * tokens in order to ease comparison.
   */
  public static SyntacticTree removeSeparators( SyntacticTree tree ) {
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

  
// =========================
// Zero-width space addition  
// =========================
  
  private static final SimpleTree ZERO_WIDTH_SPACE_TREE = new SimpleTree( _ZERO_WIDTH_SPACE );

  /**
   * Inserts a {@link NodeKind#_ZERO_WIDTH_SPACE} between two consecutive blocks of literal.
   */
  public static Treepath< SyntacticTree > addZeroWidthSpaceBetweenBlocksOfLiteral(
      Treepath< SyntacticTree > treepath
  ) {
    final SyntacticTree tree = treepath.getTreeAtEnd() ;
    if( tree.isOneOf( 
        BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS, 
        BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS 
    ) ) {
      treepath = insertIfNextIsExactSibling( treepath, ZERO_WIDTH_SPACE_TREE ) ;
    } else if( ! tree.isOneOf( TreeManglingConstants.NON_TRAVERSABLE_NODEKINDS ) ){
      int childIndex = 0 ;
      while( true ) {
        if( childIndex < treepath.getTreeAtEnd().getChildCount() ) {
          treepath = addZeroWidthSpaceBetweenBlocksOfLiteral( 
              Treepath.create( treepath, childIndex ) ).getPrevious() ;
          childIndex++ ;
        } else {
          break ;
        }        
      }      
    }
    return treepath ;
  }
  
  private static Treepath< SyntacticTree > insertIfNextIsExactSibling( 
      Treepath< SyntacticTree > treepath,
      SyntacticTree insert
  ) {
    final NodeKind siblingNodeKind = NodeKindTools.ofRoot( treepath.getTreeAtEnd() ) ;
    if( TreepathTools.hasNextSibling( treepath ) ) {
      final Treepath< SyntacticTree > nextSibling = TreepathTools.getNextSibling( treepath ) ;
      if( NodeKindTools.ofRoot( nextSibling.getTreeAtEnd() ) == siblingNodeKind ) {
        final Treepath< SyntacticTree > afterInsert = TreepathTools.addChildAt( 
            treepath.getPrevious(), insert, nextSibling.getIndexInPrevious() ) ;
        return afterInsert ;
//        return TreepathTools.getNextSibling( afterInsert ) ;
      }
    } 
    return treepath ;
  }


}

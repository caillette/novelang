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
import com.google.common.base.Function;

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
      treepath = insertMandatoryWhitespaceIfNeeded( treepath, FORWARD_WALKER ) ;
      treepath = insertMandatoryWhitespaceIfNeeded( treepath, BACKWARD_WALKER ) ;
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
      HorizontalWalker walker
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

  private interface HorizontalWalker
      extends Function< Treepath< SyntacticTree >, Treepath< SyntacticTree > >
  {
      int getOffset() ;
  } ;

  private static final HorizontalWalker FORWARD_WALKER = new HorizontalWalker() {
    public Treepath< SyntacticTree > apply( Treepath< SyntacticTree > treepath ) {
      if( TreepathTools.hasNextSibling( treepath ) ) {
        return TreepathTools.getNextSibling( treepath ) ;
      } else {
        return null ;
      }
    }

    public int getOffset() {
      return 1 ;
    }
  } ;

  private static final HorizontalWalker BACKWARD_WALKER = new HorizontalWalker() {
    public Treepath< SyntacticTree > apply( Treepath< SyntacticTree > treepath ) {
      if( TreepathTools.hasPreviousSibling( treepath ) ) {
        return TreepathTools.getPreviousSibling( treepath ) ;
      } else {
        return null ;
      }
    }

    public int getOffset() {
      return 0 ;
    }
  } ;


// ==========================
// Zero-width space insertion
// ==========================

  private static final SimpleTree ZERO_WIDTH_SPACE_TREE = new SimpleTree( _ZERO_WIDTH_SPACE );

  /**
   * Inserts a {@link NodeKind#_ZERO_WIDTH_SPACE} between two consecutive blocks of literal.
   */
  public static Treepath< SyntacticTree > insertZeroWidthSpaceBetweenBlocksOfLiteral(
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
          treepath = insertZeroWidthSpaceBetweenBlocksOfLiteral(
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
      }
    } 
    return treepath ;
  }


}

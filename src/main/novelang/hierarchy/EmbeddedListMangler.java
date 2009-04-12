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
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import static novelang.parser.NodeKind.*;

/**
 * Rehiererachizes embedded lists, with {@link novelang.parser.NodeKind#_EMBEDDED_LIST_WITH_HYPHEN} 
 * elements wrapping {@link novelang.parser.NodeKind#_EMBEDDED_LIST_ITEM}.
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
    Treepath< SyntacticTree > current = treepathToRehierarchize ;
    while( true ) {
      Treepath< SyntacticTree > next ;
      if( isRawItem( current ) ) {
        
        current = insertPlaceholder( current ) ;
        // Now currents refers to a new _PLACEHOLDER_ child right before first raw item.

        // The gobbler is a treepath because it may be used as a stack.
        final Treepath< SyntacticTree > gobbler = createGobbler() ;
        
        final int indentation = getIndentSize( current ) ; // Hitting placeholder, but no problem.
        
        final GobbleResult result = gobbleThisIndentOrGreater( gobbler, current, indentation ) ;
        // So we have gobbled every items in sequence, just leaving the placeholder.
        
        current = result.gobbled ;
        current = TreepathTools.replaceTreepathEnd( current, result.gobbler.getTreeAtStart() ) ;
        next = TreepathTools.getNextUpInPreorder( current ) ;
      } else {
        next = TreepathTools.getNextInPreorder( current ) ;
      }
      if( null == next ) {
        return current.getStart() ;
      } else {
        current = next ;
      }
    }

  }

  private static Treepath< SyntacticTree > createGobbler() {
    return Treepath.create( ( SyntacticTree ) new SimpleTree( _EMBEDDED_LIST_WITH_HYPHEN ) );
  }

  private static boolean isRawItem( Treepath< SyntacticTree > current ) {
    return current.getTreeAtEnd().isOneOf( EMBEDDED_LIST_ITEM_WITH_HYPHEN_ );
  }

  private static Treepath< SyntacticTree > insertPlaceholder( Treepath< SyntacticTree > current ) {
    return TreepathTools.addChildAt( 
        current.getPrevious(), 
        new SimpleTree( _PLACEHOLDER_ ), 
        current.getIndexInPrevious() 
    ) ;
  }


  /**
   * Gobbles all consecutive {@link novelang.parser.NodeKind#EMBEDDED_LIST_ITEM_WITH_HYPHEN_} nodes
   * of the same indent or with a greater indent.
   *
   * @param gobbler a tree which solely holds the new embedded list structure.
   * @param gobbleStart where the gobbling starts in the document. Nodes of interest are removed.
   * @param firstIndent indent found by the caller, must be 0 or more.
   * @return a non-null {@code GobbleResult} with a {@code gobbler} value never null, but with
   *     a {@code gobbled} value that can be null when nodes of interest have all been gobbled.
   */
  private static GobbleResult gobbleThisIndentOrGreater(
      Treepath< SyntacticTree > gobbler,
      Treepath< SyntacticTree > gobbleStart,
      int firstIndent
  ) {
    Preconditions.checkArgument( gobbleStart.getTreeAtEnd().isOneOf( _PLACEHOLDER_ ) ) ;
    Preconditions.checkArgument( firstIndent >= 0  ) ;
    
    do {
      final Gobbling gobbling = gobble( gobbleStart, firstIndent ) ;

      if( gobbling.success ) {
        if( firstIndent == gobbling.indentation ) {        // Gobble at same indentation
          gobbler = TreepathTools.addChildLast( gobbler, gobbling.gobbledTree ).getPrevious() ;
          gobbleStart = gobbling.treepathMinusGobbled ;
        } else if( firstIndent < gobbling.indentation ) {  // Gobble at greater indentation
          gobbler = TreepathTools.addChildLast(
              gobbler,
              new SimpleTree( _EMBEDDED_LIST_WITH_HYPHEN )
          ) ;
          final GobbleResult result = gobbleThisIndentOrGreater(
              gobbler,
              gobbleStart,
              gobbling.indentation
          ) ;
          if( result.mayContinue ) {
            final Gobbling gobblingLookahead = gobble( result.gobbled, -1 ) ;
            if( gobblingLookahead.indentation > firstIndent ) {
              // If the indentation is greater than current after gobbling all nodes of same
              // indent or greater, this means there is some "inbetween" indent.
              throw new IllegalArgumentException( "Inconsistent indentation" ) ;
            }
            gobbler = result.gobbler.getPrevious() ;
            gobbleStart = result.gobbled ;
          } else {
            return new GobbleResult( result.gobbler.getPrevious(), result.gobbled, false ) ;
          }

        } else {                                           // Let caller handle smaller indentation
          return new GobbleResult( gobbler, gobbleStart, true ) ;
        }

      } else {
        return new GobbleResult( gobbler, gobbleStart, false ) ;
      }
      
    } while( true ) ;
    
  }

  /**
   * Gobbles one item node if possible.
   * <p>
   * Given parameter {@code gobbleStart} is the path to a node of {@code _PLACEHOLDER} kind.
   * The {@code _PLACEHOLDER} avoids to make the parent become childless when all children
   * have been gobbled (this would discard useful information about where to insert the new
   * rehierarchized list). 
   * <p> 
   * Gobbling removes following siblings of the {@code _PLACEHOLDER}. If one interesting 
   * item (evaluated by {@link #isRawItem(Treepath)}) is found, or if there is no following sibling, 
   * then the method returns. 
   * <p>
   * If, when looking for next siblings of {@code _PLACEHOLDER_} node, a 
   * {@link novelang.parser.NodeKind#WHITESPACE_} is encountered, it sets the value of 
   * {@link Gobbling#indentation}. Otherwise, this value copies {@code knownIndentation} parameter. 
   * <p> 
   * Each call returns a {@code Gobbling} object containing the result of the gobble.
   * <p>
   * {@link Gobbling#success} is set to false if there was no following sibling 
   * of interest (no raw item, all separators skipped).
   * <p>
   * {@link Gobbling#gobbledTree} is the treepath to the {@code _PLACEHOLDER} node, but whith
   * following siblings of interest removed.
   * <p>
   * {@link Gobbling#gobbledTree} is the gobbled item. 
   *  
   * 
   * @param gobbleStart a treepath to the {@code _PLACEHOLDER} node which precedes the sequence
   * of raw items.
   * 
   * @return a {@code Gobbling} object containing 
   *     the result of the gobble. 
   */
  private static Gobbling gobble( Treepath< SyntacticTree > gobbleStart, int indentation ) {
    Preconditions.checkArgument( gobbleStart.getTreeAtEnd().isOneOf( _PLACEHOLDER_ ) ) ;
    Treepath< SyntacticTree > start = gobbleStart ;
    
    do {
      if( TreepathTools.hasNextSibling( start ) ) {
        final Treepath< SyntacticTree > next = TreepathTools.getNextSibling( start ) ;
        if( isRawItem( next ) ) {
          final Treepath< SyntacticTree > minusNext =
              TreepathTools.removeNextSibling( start ) ;
          return new Gobbling( minusNext, makeEmbeddedListItem( next ), indentation ) ;
        } else {
          final SyntacticTree nextTree = next.getTreeAtEnd() ;
          if( nextTree.isOneOf( WHITESPACE_, LINE_BREAK_ ) ) {
            start = TreepathTools.removeNextSibling( start ) ;
            if( nextTree.isOneOf( WHITESPACE_ ) ) {
              indentation = getWhitespaceLength( nextTree ) ;
            } else {
              indentation = 0 ;
            }
            continue ;
          }
        }
      } 
      // If no next sibling at all, or no useful next sibling, then return.
      return new Gobbling( start ) ;      
    } while( true ) ;
  }

  private static SyntacticTree makeEmbeddedListItem( Treepath< SyntacticTree > treepath ) {
    final Iterable< ? extends SyntacticTree > children = treepath.getTreeAtEnd().getChildren() ;
    return new SimpleTree( _EMBEDDED_LIST_ITEM.name(), children ) ;
  }


  /**
   * Holds two return values: the treepath minus the gobbled nodes, and the tree of interest.
   */
  private static class Gobbling {
    private final Treepath< SyntacticTree > treepathMinusGobbled ;
    private final SyntacticTree gobbledTree ;
    private final boolean success ;
    private final int indentation ;

    private Gobbling(
        Treepath< SyntacticTree > treepathMinusGobbled,
        SyntacticTree gobbledTree,
        int indentation
    ) {
      this.treepathMinusGobbled = Preconditions.checkNotNull( treepathMinusGobbled ) ;
      this.gobbledTree = Preconditions.checkNotNull( gobbledTree ) ;
      this.success = true ;
      Preconditions.checkArgument( indentation >= 0 ) ;
      this.indentation = indentation ;
    }

    private Gobbling( Treepath<SyntacticTree> treepathMinusGobbled ) {
      this.treepathMinusGobbled = treepathMinusGobbled ;
      this.gobbledTree = null ;
      this.indentation = Integer.MIN_VALUE ;
      this.success = false ;
    }
  }


  private static class GobbleResult {

    private final Treepath< SyntacticTree > gobbler ;
    private final Treepath< SyntacticTree > gobbled ;
    private final boolean  mayContinue ;

    private GobbleResult(
        Treepath< SyntacticTree > gobbler,
        Treepath< SyntacticTree > gobbled,
        boolean mayContinue
    ) {
      this.gobbler = Preconditions.checkNotNull( gobbler ) ;
      this.gobbled = Preconditions.checkNotNull( gobbled ) ;
      this.mayContinue = mayContinue ;
    }
  }


  /**
   * Returns the depth of a list item given its indentation.
   * It is based on the length of immediate left sibling, or length of immediate left sibling of
   * one ancestor.
   * <p>
   * Handling indentation this way avoids to mess the grammar up.
   * There has been an attempt to inject the whitespace into its following  
   * {@link novelang.parser.NodeKind#EMBEDDED_LIST_ITEM_WITH_HYPHEN_} node.
   * This couldn't work when the whitespace appeared before the
   * {@link novelang.parser.NodeKind#PARAGRAPH_REGULAR} node (when the list item was first in
   * the paragraph, though indented).
   * 
   * @param treepath a treepath of {@link novelang.parser.NodeKind#EMBEDDED_LIST_ITEM_WITH_HYPHEN_} 
   * kind.
   * @return a number equal to or greater than 0
   * @Deprecated
   */
  private static int getItemDepth( Treepath< SyntacticTree > treepath ) {
    Preconditions.checkArgument( isRawItem( treepath ) ) ;
    return getIndentSize( treepath ) ;
  }

  /**
   * Returns the length of immediate left sibling, or length of immediate left sibling of
   * one ancestor.
   * @Deprecated
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
        return getWhitespaceLength( leftSiblingInHierarchy ) ;
      } else {
        return 0 ;
      }
    } else {
      return getIndentSize( previous ) ;
    }
  }
  
  private static int getWhitespaceLength( Treepath< SyntacticTree > treepath ) {
    return getWhitespaceLength( treepath.getTreeAtEnd() ) ;
  }

  private static int getWhitespaceLength( SyntacticTree tree ) {
    Preconditions.checkArgument( tree.isOneOf( WHITESPACE_ ) ) ;
    return tree.getChildAt( 0 ).getText().length() ;
  }

}

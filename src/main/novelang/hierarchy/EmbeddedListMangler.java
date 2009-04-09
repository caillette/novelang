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
//    if( true ) { throw new UnsupportedOperationException( "rehierarchizeEmbeddedLists" ) ; }
    Treepath< SyntacticTree > current = treepathToRehierarchize ;
    while( true ) {
      if( isRawItem( current ) ) {
        
        current = insertPlaceholder( current ) ;
        final Treepath< SyntacticTree > treepathBeforeGobbling = current ;
        // Now currents refers to a new _PLACEHOLDER_ child right before first raw item.
        
        final Treepath< SyntacticTree > gobbler = createGobbler() ;
        // The gobbler is a treepath because it may be used as a stack.
        
        final int indentation = getIndentSize( current ) ; // Hitting placeholder, but no problem.
        
        final GobbleResult result = gobbleThisIndentOrGreater( gobbler, current, indentation ) ;
        // So we have gobbled every items in sequence, just leaving the placeholder.
        
        current = result.gobbled ;
        current = replaceAt(
            current,
            treepathBeforeGobbling, 
            result.gobbler.getTreeAtStart()
        ) ;
      }
      final Treepath< SyntacticTree > next = TreepathTools.getNextInPreorder( current ) ;
      if( null == next ) {
        return current.getStart() ;
      } else {
        current = next ;
      }
    }

  }

  private static Treepath<SyntacticTree> createGobbler() {
    return Treepath.create( ( SyntacticTree ) new SimpleTree( _EMBEDDED_LIST_WITH_HYPHEN ) );
  }

  private static boolean isRawItem( Treepath< SyntacticTree > current ) {
    return current.getTreeAtEnd().isOneOf( EMBEDDED_LIST_ITEM_WITH_HYPHEN_ );
  }

  private static Treepath< SyntacticTree > insertPlaceholder( Treepath<SyntacticTree> current ) {
    return TreepathTools.addChildAt( 
        current, 
        new SimpleTree( _PLACEHOLDER_ ), 
        current.getIndexInPrevious() 
    ) ;
  }

  private static Treepath< SyntacticTree > replaceAt(
      Treepath< SyntacticTree > replacementTarget,
      Treepath< SyntacticTree > treepathTemplate,
      SyntacticTree tree
  ) {
    return TreepathTools.addChildLast( replacementTarget,  tree ) ; 

  }


  /**
   * Gobbles all consecutive {@link novelang.parser.NodeKind#EMBEDDED_LIST_ITEM_WITH_HYPHEN_} nodes
   * of the same indent or with a greater indent.
   *
   * @param gobbler a tree which solely holds the new embedded list structure.
   * @param gobbleStart where the gobbling starts in the document. Nodes of interest are removed.
   * @param indentation expected indentation, must be 0 or more.
   * @return a non-null {@code GobbleResult} with a {@code gobbler} value never null, but with
   *     a {@code gobbled} value that can be null when nodes of interest have all been gobbled.
   */
  private static GobbleResult gobbleThisIndentOrGreater(
      Treepath< SyntacticTree > gobbler,
      Treepath< SyntacticTree > gobbleStart,
      int indentation
  ) {
    Preconditions.checkArgument( gobbleStart.getTreeAtEnd().isOneOf( _PLACEHOLDER_ ) ) ;
    
    do {
      final Gobbling gobbling = gobble( gobbleStart ) ;

      if( null == gobbling ) {
        return new GobbleResult( gobbler, gobbleStart, false ) ;
      } else {
        if( indentation == gobbling.indentation ) {        // Gobble at same indentation
          if( gobbler.getPrevious() == null
           || gobbler.getPrevious().getTreeAtEnd().isOneOf( _EMBEDDED_LIST_WITH_HYPHEN )
           || gobbler.getTreeAtEnd().getChildCount() == 0
          ) {
            gobbler = TreepathTools.addChildLast( gobbler, gobbling.gobbledTree ) ;
          } else {
            gobbler = TreepathTools.addSiblingLast( gobbler, gobbling.gobbledTree ) ;
          }
          gobbleStart = gobbling.treepathMinusGobbled ;
        } else if( indentation < gobbling.indentation ) {  // Gobble at greater indentation
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
            final Gobbling gobblingLookahead = gobble( result.gobbled ) ;
            if( gobblingLookahead != null && gobblingLookahead.indentation > indentation ) {
              // If the indentation is greater than current after gobbling all nodes of same
              // indent or greater, this means there is some "inbetween" indent.
              throw new IllegalArgumentException( "Inconsistent indentation" ) ;
            }
            gobbler = result.gobbler ;
            gobbleStart = result.gobbled ;
          } else {
            return new GobbleResult( result.gobbler.getPrevious(), result.gobbled, false ) ;
          }

        } else {                                           // Let caller handle smaller indentation
          // next indentation is smaller, so we let the caller handle it.
          return new GobbleResult( gobbler, gobbleStart, true ) ;
        }

      }
    } while( true ) ;
    
  }

  /**
   * Gobble one item node if possible.
   * 
   * @param gobbleStart a treepath to the {@code _PLACEHOLDER} node which precedes the sequence
   * of raw items.
   * 
   * @return null if {@code gobbleStart} was null, or a {@code Gobbling} object containing 
   *     the result of the gobble. 
   *     {@code Gobbling#treepathMinusGobbled} still refers to 
   *     the _PLACEHOLDER_ but some separators and one raw item have been removed.
   *     {@code Gobbling#gobbledTree} is the result of the gobbling
   */
  private static Gobbling gobble( Treepath< SyntacticTree > gobbleStart ) {
    if( null == gobbleStart ) {
      return null ;
    }
    Preconditions.checkArgument( gobbleStart.getTreeAtEnd().isOneOf( _PLACEHOLDER_ ) ) ;
    gobbleStart = TreepathTools.getNextSibling( gobbleStart ) ;
    
    do {
      if( isRawItem( gobbleStart ) ) {
        if( TreepathTools.hasNextSibling( gobbleStart ) ) {
          final Treepath< SyntacticTree > nextStart = TreepathTools.getNextSibling( gobbleStart ) ;
          final Treepath< SyntacticTree > nextStartMinusPrevious =
              TreepathTools.removePreviousSibling( nextStart ) ;
          return new Gobbling( nextStartMinusPrevious, makeEmbeddedListItem( gobbleStart ), true ) ;
        } else {
          final Treepath< SyntacticTree > minusPrevious = TreepathTools.removeEnd( gobbleStart ) ;
          return new Gobbling( minusPrevious, makeEmbeddedListItem( gobbleStart ), false ) ;
        }
      } else if( gobbleStart.getTreeAtEnd().isOneOf( WHITESPACE_, LINE_BREAK_ ) ) {
        if( TreepathTools.hasNextSibling( gobbleStart ) ) {
          gobbleStart = TreepathTools.getNextSibling( gobbleStart ) ;
        }
      } else {
        return null ;
      }
    } while( true ) ;
  }

  private static SyntacticTree makeEmbeddedListItem( Treepath< SyntacticTree > treepath ) {
    final Iterable< ? extends SyntacticTree > iterable = treepath.getTreeAtEnd().getChildren() ;
    return new SimpleTree( _EMBEDDED_LIST_ITEM.name(), iterable ) ;
  }


  /**
   * Holds two return values: the treepath minus the gobbled nodes, and the tree of interest.
   */
  private static class Gobbling {
    private final Treepath< SyntacticTree > treepathMinusGobbled ;
    private final SyntacticTree gobbledTree ;
    private final boolean mayGobblingContinue ;
    private final int indentation ;

    private Gobbling(
        Treepath< SyntacticTree > treepathMinusGobbled,
        SyntacticTree gobbledTree,
        boolean mayGobblingContinue
    ) {
      this.treepathMinusGobbled = treepathMinusGobbled ;
      this.gobbledTree = gobbledTree ;
      this.mayGobblingContinue = mayGobblingContinue ;
      this.indentation = treepathMinusGobbled == null ? -1 : getIndentSize( treepathMinusGobbled ) ;
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
    Preconditions.checkArgument( isRawItem( treepath ) ) ;
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

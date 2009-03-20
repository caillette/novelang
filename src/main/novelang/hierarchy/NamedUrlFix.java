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
package novelang.hierarchy;

import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.common.tree.TreepathPreorderIterator;
import novelang.parser.NodeKind;
import static novelang.parser.NodeKind.*;

/**
 * @author Laurent Caillette
 */
public class NamedUrlFix {
  
  public static Treepath< SyntacticTree > fixNamedUrls( Treepath< SyntacticTree > treepath ) {
    State state = State.UNRELATED ;
    int doubleQuotesIndex = -1 ;
    int childIndex = 0 ;
    
    while( childIndex < treepath.getTreeAtEnd().getChildCount() ) {
      final SyntacticTree tree = treepath.getTreeAtEnd().getChildAt( childIndex ) ;
      
      switch ( state ) {
        
        case UNRELATED :
          state = evaluate( tree, State.FRESH_START, PART ) ;
          if( State.UNRELATED == state ) {
            if( tree.getChildCount() > 0 ) {
              treepath = fixNamedUrls( Treepath.create( treepath, childIndex ) ) ;
            }
          }
          break ;
        
        case FRESH_START :
          state = evaluate( tree, State.INDENTATION, WHITESPACE_ ) ;
          doubleQuotesIndex = -1 ;
          break ;
        
        case INDENTATION :
          state = evaluate( tree, State.DOUBLE_QUOTES, BLOCK_INSIDE_DOUBLE_QUOTES ) ;
          if( state == State.DOUBLE_QUOTES ) {
            doubleQuotesIndex = childIndex ;
          }
          break ;
        
        case DOUBLE_QUOTES :          
          state = evaluate( tree, State.TRAILING_SPACE, evaluate( tree, State.LINE_BREAK, NodeKind.LINE_BREAK_ ), WHITESPACE_
          ) ;
          break ;
        
        case TRAILING_SPACE :
          state = evaluate( tree, State.LINE_BREAK, NodeKind.LINE_BREAK_ ) ;
          break ;
        
        case LINE_BREAK :
          if( tree.isOneOf( NodeKind.URL ) ) {
            treepath = replaceByExternalLink( treepath, doubleQuotesIndex ) ;
          } else {
            state = State.UNRELATED ;      
          }
          break ;
        
        default : 
          throw new IllegalStateException( "Unsupported: " + state ) ;
      }
      childIndex++ ;
    }
    return treepath ;
  }

  public static Treepath< SyntacticTree > fixNamedUrlsUsingIterator( 
      Treepath< SyntacticTree > treepath 
  ) {
    State state = State.UNRELATED ;
    Treepath< SyntacticTree > treepathToName = null ;
    final TreepathPreorderIterator< SyntacticTree > iterator = 
        new TreepathPreorderIterator< SyntacticTree >( treepath ) ; 
    
    while( iterator.hasNextTree() ) {
      final SyntacticTree tree = iterator.getNextTree() ;
      
      switch ( state ) {
        
        case UNRELATED :
          state = evaluate( tree, State.FRESH_START, PART ) ;
          if( State.UNRELATED == state ) {
            if( tree.getChildCount() > 0 ) {
              // TODO implement
            }
          }
          break ;
        
        case FRESH_START :
          state = evaluate( tree, State.INDENTATION, WHITESPACE_ ) ;
          treepathToName = null ;
          break ;
        
        case INDENTATION :
          state = evaluate( 
              tree, 
              State.DOUBLE_QUOTES, 
              evaluate( tree, State.INDENTATION, PARAGRAPH_REGULAR ), // Ignoring
              BLOCK_INSIDE_DOUBLE_QUOTES 
          ) ;
          if( state == State.DOUBLE_QUOTES ) {
            treepathToName = iterator.getTreepath() ;
          }
          break ;
        
        case DOUBLE_QUOTES :          
          state = evaluate( 
              tree, State.TRAILING_SPACE, 
              evaluate( tree, State.LINE_BREAK, NodeKind.LINE_BREAK_ ), 
              WHITESPACE_
          ) ;
          break ;
        
        case TRAILING_SPACE :
          state = evaluate( tree, State.LINE_BREAK, NodeKind.LINE_BREAK_ ) ;
          break ;
        
        case LINE_BREAK :
          if( tree.isOneOf( NodeKind.URL ) ) {
//            treepath = TreepathTools.removeEndFrom( treepath, treepathToName ) ;
          } else {
            state = State.UNRELATED ;      
          }
          break ;
        
        default : 
          throw new IllegalStateException( "Unsupported: " + state ) ;
      }
      
    }
    return treepath ;
  }

  /**
   * Replaces the {@link NodeKind#URL} node at the end of the treepath by a 
   * {@link NodeKind#_EXTERNAL_LINK} node.
   *  
   * @param treepathToUrl treepath to the URL node.
   * @param doubleQuotesIndex the index of the name of the URL, which must be a sibling node
   *     of {@link NodeKind#BLOCK_INSIDE_DOUBLE_QUOTES} type. 
   * @return the new treepath.
   */
  private static Treepath< SyntacticTree > replaceByExternalLink( 
      Treepath< SyntacticTree > treepathToUrl, 
      int doubleQuotesIndex
  ) {
    final SyntacticTree nameTree = 
        TreepathTools.getSiblingAt( treepathToUrl, doubleQuotesIndex ).getTreeAtEnd() ;
    throw new UnsupportedOperationException( "replaceByExternalLink" );
  }

  private static State evaluate(
      SyntacticTree tree,
      State positive, 
      NodeKind... nodeKind
  ) {
    return evaluate( tree, positive, State.UNRELATED, nodeKind ) ;
  }
  
  private static State evaluate(
      SyntacticTree tree,
      State positive, 
      State negative, 
      NodeKind... nodeKind
  ) {
    return tree.isOneOf( nodeKind ) ? positive : negative ;
  }

  private enum State {
    UNRELATED,
    FRESH_START, // May represent a line break or the start of some kind of paragraph.
    INDENTATION,
    DOUBLE_QUOTES,
    TRAILING_SPACE,
    LINE_BREAK,
  }
  
}

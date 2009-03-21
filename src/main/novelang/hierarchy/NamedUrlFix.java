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
import novelang.common.SimpleTree;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.parser.NodeKind;
import static novelang.parser.NodeKind.*;

/**
 * @author Laurent Caillette
 */
public class NamedUrlFix {

  public static Treepath< SyntacticTree > fixNamedUrls(
      final Treepath< SyntacticTree > treepath
  ) {
    State state = State.UNRELATED ;
    Treepath< SyntacticTree > treepathToName = null ;

    Treepath< SyntacticTree > current = treepath ;
    Treepath< SyntacticTree > result = current ;

    while( current != null ) {
      final SyntacticTree tree = current.getTreeAtEnd() ;
      
      switch ( state ) {
        
        case UNRELATED :
          state = evaluate( tree, PART, State.FRESH_START ) ;
          break ;
        
        case FRESH_START :
          state = evaluate( tree, WHITESPACE_, State.INDENTATION ) ;
          treepathToName = null ;
          break ;
        
        case INDENTATION :
          state = evaluate( 
              tree,
              BLOCK_INSIDE_DOUBLE_QUOTES,
              State.DOUBLE_QUOTES,
              evaluate( tree, PARAGRAPH_REGULAR, State.INDENTATION ) // Ignoring
          ) ;
          if( state == State.DOUBLE_QUOTES ) {
            treepathToName = current ;
          }
          break ;
        
        case DOUBLE_QUOTES :          
          state = evaluate( 
              tree, WHITESPACE_, State.TRAILING_SPACE,
              evaluate( tree, NodeKind.LINE_BREAK_, State.LINE_BREAK )
          ) ;
          break ;
        
        case TRAILING_SPACE :
          state = evaluate( tree, NodeKind.LINE_BREAK_, State.LINE_BREAK ) ;
          break ;
        
        case LINE_BREAK :
          if( tree.isOneOf( NodeKind.URL ) ) {
            current = TreepathTools.removeSubtree( current, treepathToName ) ;
            current = replaceByExternalLink( current, treepathToName ) ;
          } else {
            state = State.UNRELATED ;      
          }
          break ;
        
        default : 
          throw new IllegalStateException( "Unsupported: " + state ) ;
      }

      result = current ;
      current = TreepathTools.getNextInPreorder( current ) ;
    }
    return result.getStart() ;
  }

  /**
   * Replaces the {@link NodeKind#URL} node at the end of the treepath by a 
   * {@link NodeKind#_EXTERNAL_LINK} node.
   *  
   * @param treepathToUrl treepath to the URL node.
   * @param treepathToUrl treepath to the name node, which must be
   *     of {@link NodeKind#BLOCK_INSIDE_DOUBLE_QUOTES} type. 
   * @return the new treepath.
   */
  private static Treepath< SyntacticTree > replaceByExternalLink( 
      Treepath< SyntacticTree > treepathToUrl, 
      Treepath< SyntacticTree > treepathToName
  ) {
    final SyntacticTree nameTree = new SimpleTree(
        NodeKind._LINK_NAME.name(),
        treepathToName.getTreeAtEnd().getChildren()
    ) ;
    final SyntacticTree externalLinkTree = new SimpleTree(
        NodeKind._EXTERNAL_LINK.name(),
        nameTree,
        treepathToUrl.getTreeAtEnd()
    ) ;
    return TreepathTools.replaceTreepathEnd( treepathToUrl, externalLinkTree ) ;
  }

  private static State evaluate(
      SyntacticTree tree,
      NodeKind nodeKind,
      State positive
  ) {
    return evaluate( tree, nodeKind, positive, State.UNRELATED ) ;
  }
  
  private static State evaluate(
      SyntacticTree tree,
      NodeKind nodeKind,
      State positive,
      State negative
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

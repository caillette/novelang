/*
 * Copyright (C) 2010 Laurent Caillette
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

import com.google.common.collect.ImmutableSet;
import org.novelang.common.SyntacticTree;
import org.novelang.common.SimpleTree;
import org.novelang.common.tree.Traversal;
import org.novelang.common.tree.Treepath;
import org.novelang.common.tree.TreepathTools;
import org.novelang.parser.NodeKind;
import static org.novelang.parser.NodeKind.*;

import java.util.Set;

/**
 * Wraps {@link NodeKind#URL_LITERAL} nodes into {@link NodeKind#_URL} ones, adding
 * preceding {@link NodeKind#BLOCK_INSIDE_DOUBLE_QUOTES}.
 * 
 * <pre>
 *    "external link name"
 * http://url.towards.somewhe.re
 * </pre>
 *  
 * @author Laurent Caillette
 */
public class UrlMangler {

  private UrlMangler() { }

  public static Treepath< SyntacticTree > fixNamedUrls(
      final Treepath< SyntacticTree > treepath
  ) {
    State state = State.OUTSIDE_PARAGRAPH;
    Treepath< SyntacticTree > treepathToName = null ;
    Treepath< SyntacticTree >  paragraph = null ;

    Treepath< SyntacticTree > current = treepath ;
    Treepath< SyntacticTree > result = current ;

    while( current != null ) {
      final SyntacticTree tree = current.getTreeAtEnd() ;
      
      switch ( state ) {
        
        case OUTSIDE_PARAGRAPH:
          state = evaluate(
              tree,
              TreeManglingConstants.PARAGRAPH_NODEKINDS_CONTAINING_URL,
              State.INSIDE_PARAGRAPH,
              State.OUTSIDE_PARAGRAPH
          ) ;
          if( State.INSIDE_PARAGRAPH == state ) {
            paragraph = current ;
          }
          break ;

        case INSIDE_PARAGRAPH :
          state = evaluate( 
              tree,
              TreeManglingConstants.CANDIDATE_URL_NAME_NODEKINDS,
              State.CANDIDATE_URL_NAME,
              evaluate( tree, URL_LITERAL, State.URL )
          ) ;
          if( state == State.CANDIDATE_URL_NAME ) {
            treepathToName = current ;
          }
          break ;

        case CANDIDATE_URL_NAME :
          state = evaluate(
              tree,
              TreeManglingConstants.SEPARATOR_NODEKINDS,       // Loop on this state
              State.CANDIDATE_URL_NAME,  // if separators.
              evaluate( tree, URL_LITERAL, State.URL )
          ) ;              
          if( state != State.CANDIDATE_URL_NAME && state != State.URL ) {
            treepathToName = null ;
          }
          break ;
        
        case URL :
          break ;

        default :
          throw new IllegalStateException( "Unsupported: " + state ) ;
      }

      if( State.URL == state ) {
        if( null != treepathToName ) {
          current = TreepathTools.removeSubtree( current, treepathToName ) ;
        }
        current = replaceByExternalLink( current, treepathToName ) ;
        treepathToName = null ;
        state = State.INSIDE_PARAGRAPH ;
      }

      result = current ;
      if( State.CANDIDATE_URL_NAME == state
       || current.getTreeAtEnd().isOneOf( _URL ) 
       || tree.isOneOf( TreeManglingConstants.SKIPPED_NODEKINDS_FOR_URLMANGLER )
      ) {
        current = Traversal.Preorder.nextUp( current ) ;
      } else {
        current = PREORDER.next( current ) ;
      }

      if( paragraph == null && state == State.INSIDE_PARAGRAPH ) {
        throw new Error( "Code inconsistency" ) ;
      }

      if( current != null
       && paragraph != null
       && ( ( paragraph.getLength() <= current.getLength()
                && ! TreepathTools.hasSameStartingIndicesAs( paragraph, current ) )
         || ( // We test in both ways because paragraphs inside angled brackets
              // have a greater path length due to nesting.
              paragraph.getLength() > current.getLength()
                && ! TreepathTools.hasSameStartingIndicesAs( current, paragraph ) )
          )
      ) {
        state = State.OUTSIDE_PARAGRAPH ;
        paragraph = null ;
        treepathToName = null ;
      }

    }
    return result.getStart() ;
  }

  private static final Traversal.Preorder< SyntacticTree > PREORDER = Traversal.Preorder.create() ;



  /**
   * Replaces the {@link NodeKind#URL_LITERAL} node at the end of the treepath by a 
   * {@link NodeKind#_URL} node.
   *  
   * @param treepathToUrlLiteral treepath to the URL node.
   * @param treepathToUrlLiteral treepath to the name node, which must be
   *     of {@link NodeKind#BLOCK_INSIDE_DOUBLE_QUOTES} type. 
   * @return the new treepath.
   */
  private static Treepath< SyntacticTree > replaceByExternalLink( 
      final Treepath< SyntacticTree > treepathToUrlLiteral,
      final Treepath< SyntacticTree > treepathToName
  ) {
    final SyntacticTree nameTree;
    final SyntacticTree[] children ;
    if( null == treepathToName ) {
      children = new SyntacticTree[] { treepathToUrlLiteral.getTreeAtEnd() } ;

    } else {
      nameTree = treepathToName.getTreeAtEnd() ;
      children = new SyntacticTree[] { nameTree, treepathToUrlLiteral.getTreeAtEnd() } ;
    }

    final SyntacticTree urlTree = new SimpleTree(
        NodeKind._URL,
        children
    ) ;
    return TreepathTools.replaceTreepathEnd( treepathToUrlLiteral, urlTree ) ;
  }

  private static State evaluate(
      final SyntacticTree tree,
      final NodeKind nodeKind,
      final State positive
  ) {
    return evaluate( tree, ImmutableSet.of( nodeKind ), positive, State.INSIDE_PARAGRAPH ) ;
  }

  private static State evaluate(
      final SyntacticTree tree,
      final Set< NodeKind > nodeKinds,
      final State positive,
      final State negative
  ) {
    return tree.isOneOf( nodeKinds ) ? positive : negative ;
  }

  private enum State {
    OUTSIDE_PARAGRAPH,
    INSIDE_PARAGRAPH,
    CANDIDATE_URL_NAME,
    URL
  }
  
}

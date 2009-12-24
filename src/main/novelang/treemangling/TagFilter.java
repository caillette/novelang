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

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import novelang.common.SyntacticTree;
import novelang.common.TagBehavior;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.designator.Tag;
import novelang.parser.NodeKind;
import novelang.parser.NodeKindTools;
import novelang.rendering.RenderingTools;
import novelang.system.LogFactory;
import novelang.system.Log;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

/**
 * Retains nodes which have at least one of given tags, or a child with at least one of the
 * given tags.
 * 
 * TODO: use precalculated {@link NodeKind#_IMPLICIT_TAG} instead of recalculating.
 * 
 * @author Laurent Caillette
 */
public class TagFilter {
  
  private static final Log LOG = LogFactory.getLog( TagFilter.class ) ;

  public static Treepath< SyntacticTree > filter(
      Treepath< SyntacticTree > treepath,
      final Set< Tag > tags
  ) {
    LOG.debug( "Filtering on %s", tags ) ;
    
    if( ! tags.isEmpty() ) {
      final SyntacticTree tree = treepath.getTreeAtEnd() ;
      final SyntacticTree filteredTree = doFilter( tree, tags ).tree ;
      if( tree != filteredTree ) {
        treepath = TreepathTools.replaceTreepathEnd( treepath, filteredTree ) ;
      }
    }

    LOG.debug( "Done filtering on %s", tags ) ;

    return treepath ;
  }
  
  private static Result doFilter( final SyntacticTree tree, final Set< Tag > tags ) {    
    final NodeKind nodeKind = NodeKindTools.ofRoot( tree ) ;
    final TagBehavior behavior = nodeKind.getTagBehavior() ;
    switch( behavior ) {

      case TERMINAL :
        if( hasTag( tree, tags ) ) {
          return new Result( true, tree ) ;
        } else {
          return null ;
        }

      case SCOPE :
        if( hasTag( tree, tags ) ) {
          return new Result( true, tree ) ;
        } // 'else' clause handled below:
      
      case TRAVERSABLE :
        final List< SyntacticTree > newChildList = 
            new ArrayList< SyntacticTree >( tree.getChildCount() ) ;
        
        // Gets true if one child (TERMINAL or SCOPE) has one of the wanted tags.
        boolean hasTaggedChild = false ;
         
        for( final SyntacticTree child : tree.getChildren() ) {
          final TagBehavior childTagBehavior = NodeKindTools.ofRoot( child ).getTagBehavior() ;
          if( childTagBehavior == TagBehavior.NON_TRAVERSABLE ) {
            newChildList.add( child ) ;
          } else {
            final Result result = doFilter( child, tags ) ;
            if( result != null ) {
              if( result.hasTag ) {
                newChildList.add( result.tree ) ;
              }
              hasTaggedChild = hasTaggedChild || result.hasTag ;
            }
          }
        }
        
        if( ( behavior == TagBehavior.SCOPE || behavior == TagBehavior.TERMINAL ) 
         && ! hasTaggedChild ) {
          return null ;
        }
        
        final SyntacticTree[] newChildArray =
            newChildList.toArray( new SyntacticTree[ newChildList.size() ] ) ;
        return new Result( hasTaggedChild, tree.adopt( newChildArray ) ) ;

      default :
        return new Result( false, tree ) ;

    }

  }

  private static boolean hasTag(
      final SyntacticTree tree,
      final Set< Tag > tags
  ) {
    for( final SyntacticTree child : tree.getChildren() ) {
      if( child.isOneOf( NodeKind._EXPLICIT_TAG, NodeKind._IMPLICIT_TAG ) ) {
        if( Tag.contains( tags, child.getChildAt( 0 ).getText() ) ) {
          return true ;
        }
      }
    }
//    if( NodeKind._LEVEL.isRoot( tree ) ) {
//      for( final SyntacticTree child : tree.getChildren() ) {
//        if( child.isOneOf( NodeKind.LEVEL_TITLE ) ) {
//          final Set< Tag > implicitTags = RenderingTools.toImplicitTagSet( child ) ;
//          return ! Sets.intersection( tags, implicitTags ).isEmpty() ;
//        }
//      }
//    }
    return false ;
  }
  
  private static class Result {
    public final boolean hasTag ;
    public final SyntacticTree tree ;

    private Result( final boolean hasTag, final SyntacticTree tree ) {
      this.hasTag = hasTag;
      this.tree = Preconditions.checkNotNull( tree ) ;
    }
  }

}

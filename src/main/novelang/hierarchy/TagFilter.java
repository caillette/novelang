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
package novelang.hierarchy;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import novelang.common.SyntacticTree;
import novelang.common.TagBehavior;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.parser.NodeKind;
import novelang.parser.NodeKindTools;
import novelang.system.LogFactory;
import novelang.system.Log;

import com.google.common.base.Preconditions;

/**
 * Retains nodes which have at least one of given tags, or a child with at least one of the
 * given tags.
 * 
 * @author Laurent Caillette
 */
public class TagFilter {
  
  private static final Log LOG = LogFactory.getLog( TagFilter.class ) ;

  public static Treepath< SyntacticTree > filter(
      Treepath< SyntacticTree > treepath,
      Set< String > tags
  ) {
    LOG.debug( "Filtering on %s", tags ) ;
    
    if( tags.isEmpty() ) {
      return treepath ;
    } else {
      final SyntacticTree tree = treepath.getTreeAtEnd() ;
      final SyntacticTree filteredTree = doFilterWithLogging( tree, tags, "" ).tree ;
      if( tree == filteredTree ) {
        return treepath ;
      } else {
        return TreepathTools.replaceTreepathEnd( treepath, filteredTree ) ;
      }
    }
  }
  
  private static Result doFilterWithLogging( 
      final SyntacticTree tree, 
      final Set< String > tags, 
      final String indent
  ) {
    LOG.debug( "%sdoFilter( %s )", indent, tree.toStringTree() ) ;
    final Result result = doFilter( tree, tags, indent ) ;
    if( null == result ) {
      LOG.debug( "%s-> null", indent ) ;
    } else {
      LOG.debug( "%s-> %b", indent, result.hasTag ) ;
    }
    return result ;
  }
  
  
  private static Result doFilter( 
      final SyntacticTree tree, 
      final Set< String > tags, 
      final String indent
  ) {    
    final NodeKind nodeKind = NodeKindTools.ofRoot( tree ) ;
    final TagBehavior behavior = nodeKind.getTagBehavior();
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
        boolean taggedChildren = false ; 
         
        for( SyntacticTree child : tree.getChildren() ) {
          final NodeKind childNodeKind = NodeKindTools.ofRoot( child ) ;
          final TagBehavior childTagBehavior = childNodeKind.getTagBehavior() ;
          switch( childTagBehavior ) {
            
            case SCOPE :
            case TERMINAL :
            case TRAVERSABLE :
              final Result result = doFilterWithLogging( child, tags, indent + "  " ) ;
              if( result != null ) {
                final SyntacticTree newChild = result.tree ;
                newChildList.add( newChild ) ;
                taggedChildren = taggedChildren || result.hasTag ;
              }
              break ;
            default :
              newChildList.add( child ) ;
          }
        }
        
        if( behavior == TagBehavior.SCOPE || behavior == TagBehavior.TERMINAL ) {
          if( ! taggedChildren ) {
            return null ;
          }
        }
        
        if( newChildList.size() == tree.getChildCount() ) {
          return new Result( taggedChildren, tree ) ; 
        } else {
          final SyntacticTree[] newChildArray = 
              newChildList.toArray( new SyntacticTree[ newChildList.size() ] ) ;
          return new Result( taggedChildren, tree.adopt( newChildArray ) ) ;          
        }
        
      default :
        return new Result( false, tree ) ;

    }

  }

  private static boolean hasTag(
      SyntacticTree tree,
      Set< String > tags
  ) {
    for( SyntacticTree child : tree.getChildren() ) {
      if( child.isOneOf( NodeKind.TAG ) ) {
//        for ( SyntacticTree tagChild : child.getChildren() ) {
          if( tags.contains( child.getChildAt( 0 ).getText() ) ) {
            return true ;
//          }
        }
      }
    }
    return false ;
  }
  
  private static class Result {
    public final boolean hasTag ;
    public final SyntacticTree tree ;

    private Result( boolean hasTag, SyntacticTree tree ) {
      this.hasTag = hasTag;
      this.tree = Preconditions.checkNotNull( tree ) ;
    }
  }

}

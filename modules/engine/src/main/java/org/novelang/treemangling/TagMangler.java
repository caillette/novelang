/*
 * Copyright (C) 2011 Laurent Caillette
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

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import static org.novelang.parser.NodeKind.*;

import org.novelang.common.SimpleTree;
import org.novelang.common.SyntacticTree;
import org.novelang.common.TagBehavior;
import org.novelang.common.tree.Traversal;
import org.novelang.common.tree.Treepath;
import org.novelang.common.tree.TreepathTools;
import org.novelang.designator.Tag;
import org.novelang.parser.NodeKind;
import org.novelang.parser.NodeKindTools;
import org.novelang.rendering.RenderingTools;

/**
 * Deals with {@link NodeKind#TAG}, {@link NodeKind#_EXPLICIT_TAG}, 
 * {@link NodeKind#_IMPLICIT_TAG}, {@link NodeKind#_PROMOTED_TAG}.
 * 
 * @author Laurent Caillette
 */
public class TagMangler {

  private TagMangler() { }

  /**
   * Tranforms {@link NodeKind#TAG} into {@link NodeKind#_EXPLICIT_TAG}
   * and adds {@link NodeKind#_IMPLICIT_TAG}s to
   * {@link NodeKind#_LEVEL} trees with a {@link NodeKind#LEVEL_TITLE}.
   * 
   * @param treepath a non-null object.
   * @return a non-null object with updated/added nodes.
   */
  public static Treepath< SyntacticTree > enhance( final Treepath< SyntacticTree > treepath ) {
    final Treepath< SyntacticTree > enhancedWithExplicitTags = enhanceWithExplicitTags( treepath ) ;
    return enhanceWithImplicitTags( enhancedWithExplicitTags ) ;
  }
  
    
  
  /**
   * Returns the set of values for {@link org.novelang.parser.NodeKind#_EXPLICIT_TAG}s.
   *
   * @return a non-null, possibly empty set.
   */
  public static Set< Tag > findExplicitTags( final SyntacticTree tree ) {
    if( tree.isOneOf( UNTRAVERSABLE_NODES ) ) { 
      return ImmutableSet.of() ;
    }
    if( tree.isOneOf( _EXPLICIT_TAG ) ) {
      return ImmutableSet.of( new Tag( tree.getChildAt( 0 ).getText() ) ) ;
    }
    final Set< Tag > tagset = Sets.newLinkedHashSet() ;
    for( final SyntacticTree child : tree.getChildren() ) {
        tagset.addAll( findExplicitTags( child ) ) ;
    }
    return tagset ;
  }


  /**
   * Could be expressed as a {@link NodeKind} property, maybe.
   */
  private static final ImmutableSet< NodeKind > UNTRAVERSABLE_NODES = ImmutableSet.of( 
      WORD_, 
      WORD_AFTER_CIRCUMFLEX_ACCENT, 
      _STYLE,
      PUNCTUATION_SIGN,
      _IMPLICIT_IDENTIFIER,
      _EXPLICIT_IDENTIFIER
  ) ;
  

// ========
// Explicit
// ========


  @SuppressWarnings( { "AssignmentToForLoopParameter" } )
  public static Treepath< SyntacticTree > enhanceWithExplicitTags(
      Treepath< SyntacticTree > treepath
  ) {
    for( Treepath< SyntacticTree > next = treepath ; next != null ; ) {
      switch( NodeKindTools.ofRoot( next.getTreeAtEnd() ).getTagBehavior() ) {
        case SCOPE :
          next = replaceByExplicitTag( next ) ;
        case TRAVERSABLE :
          next = PREORDER.next( next ) ;
          break;
        case TERMINAL :
          next = replaceByExplicitTag( next ) ;
          next = PREORDER.next( next ) ;
          break ;
        case NON_TRAVERSABLE :
          next = PREORDER.nextUp( next ) ;
          break ;
      }
      if( next != null ) {
        treepath = next ;
      }
    }
    return treepath.getStart() ;
  }

  private static final Traversal.Preorder< SyntacticTree > PREORDER = Traversal.Preorder.create() ;




  private static Treepath< SyntacticTree > replaceByExplicitTag(
      Treepath< SyntacticTree > treepathToTagOwner
  ) {
    final SyntacticTree ownerTree = treepathToTagOwner.getTreeAtEnd() ;
    for( int i = 0 ; i < ownerTree.getChildCount() ; i ++ ) {
      final SyntacticTree child = ownerTree.getChildAt( i ) ;
      if( child.isOneOf( NodeKind.TAG ) ) {
        final SyntacticTree explicitTag = new SimpleTree(
            NodeKind._EXPLICIT_TAG, new SimpleTree( child.getChildAt( 0 ).getText() ) ) ;
        final Treepath< SyntacticTree > treepathToTag = Treepath.create( treepathToTagOwner, i ) ;
        treepathToTagOwner =
            TreepathTools.replaceTreepathEnd( treepathToTag, explicitTag ).
            getPrevious()
        ;
      }
    }
    return treepathToTagOwner ;
  }

// ========
// Implicit
// ========


  @SuppressWarnings( { "AssignmentToForLoopParameter" } )
  public static Treepath< SyntacticTree > enhanceWithImplicitTags(
      Treepath< SyntacticTree > treepath
  ) {
    for( Treepath< SyntacticTree > next = treepath ; next != null ; ) {
      switch( NodeKindTools.ofRoot( treepath.getTreeAtEnd() ) ) {
        case _LEVEL :
          if( ! hasTag( next ) ) {
            final Set< Tag > tagset = findImplicitTags( next ) ;
            if( ! tagset.isEmpty() ) {
              next = addImplicitTags( next, tagset ) ;
            }
          }
        case NOVELLA:
        case OPUS:
          // TODO replace by Traversal.getFirst()
          next = PREORDER.next( next ) ;
          break ;
        default :
          next = PREORDER.nextUp( next ) ;
      }
      if( next != null ) {
        treepath = next ;
      }
    }
    return treepath.getStart() ;
  }

  private static Treepath< SyntacticTree > addImplicitTags(
      Treepath< SyntacticTree > treepathToLevel,
      final Set< Tag > tagset
  ) {
    for( final Tag tag : tagset ) {
      treepathToLevel = TreepathTools.addChildFirst(
          treepathToLevel,
          tag.asSyntacticTree( NodeKind._IMPLICIT_TAG )
      ).getPrevious() ;
    }
    return treepathToLevel ;
  }


  private static boolean hasTag( final Treepath< SyntacticTree > treepathToLevel ) {
    final SyntacticTree ownerTree = treepathToLevel.getTreeAtEnd() ;
    for( int i = 0 ; i < ownerTree.getChildCount() ; i ++ ) {
      final SyntacticTree child = ownerTree.getChildAt( i ) ;
      if( child.isOneOf( NodeKind._EXPLICIT_TAG, NodeKind.TAG ) ) {
        return true ;
      }
    }
    return false ;
  }

  private static Set< Tag > findImplicitTags( final Treepath< SyntacticTree > treepathToLevel ) {
    final SyntacticTree ownerTree = treepathToLevel.getTreeAtEnd() ;
    for( int i = 0 ; i < ownerTree.getChildCount() ; i ++ ) {
      final SyntacticTree child = ownerTree.getChildAt( i ) ;
      if( child.isOneOf( NodeKind.LEVEL_TITLE ) ) {
        return RenderingTools.toImplicitTagSet( child ) ;
      }
    }
    return ImmutableSet.of() ;
  }

// ========  
// Promoted  
// ========

  
  /**
   * Replaces {@link NodeKind#_IMPLICIT_TAG}s appearing in the given tag set 
   * by {@link NodeKind#_PROMOTED_TAG}s. 
   * 
   * @see #promote(Treepath, Set) 
   */
  @SuppressWarnings( { "AssignmentToForLoopParameter" } )
  public static Treepath< SyntacticTree > promote(
      Treepath< SyntacticTree > treepath, 
      final Set< Tag > explicitTags 
  ) {
    if( treepath.getTreeAtStart().getChildCount() == 0 ) {
      return treepath.getStart() ;
    }
    for( Treepath< SyntacticTree > next = treepath ; next != null ; ) {
      final SyntacticTree tree = next.getTreeAtEnd() ;
      final NodeKind nodeKind = NodeKindTools.ofRoot( tree ) ;

      switch( nodeKind ) {
        case NOVELLA:
        case OPUS:
          // TODO replace by Traversal.getFirst()
          next = PREORDER.next( next ) ;
          break ;
        default :
          if ( tree.isOneOf( _IMPLICIT_TAG ) ) {
            final Tag implicitTag = new Tag( tree.getChildAt( 0 ).getText() ) ;
            if ( explicitTags.contains( implicitTag ) ) {
              final SyntacticTree promotedTag = new SimpleTree( _PROMOTED_TAG, tree.getChildren() ) ;
              next = TreepathTools.replaceTreepathEnd( treepath, promotedTag ) ;
            } else {
              next = PREORDER.nextUp( next ) ;
            }
          } else if( nodeKind.getTagBehavior() == TagBehavior.NON_TRAVERSABLE ) { 
            next = PREORDER.nextUp( next ) ;
          } else {
            next = PREORDER.next( next ) ;
          }
      }
      
      if( next != null ) {
        treepath = next ;
      }
    }
    return treepath.getStart() ;
  }

}

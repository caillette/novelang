package novelang.treemangling;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import novelang.common.SyntacticTree;
import novelang.common.SimpleTree;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.parser.NodeKindTools;
import novelang.parser.NodeKind;
import novelang.designator.Tag;
import novelang.rendering.RenderingTools;

/**
 * Tranforms {@link novelang.parser.NodeKind#TAG} into {@link novelang.parser.NodeKind#_EXPLICIT_TAG}
 * and adds {@link novelang.parser.NodeKind#_IMPLICIT_TAG}s to
 * {@link novelang.parser.NodeKind#_LEVEL} trees with a
 * {@link novelang.parser.NodeKind#LEVEL_TITLE}.
 *
 * @author Laurent Caillette
 */
public class TagMangler {

  private TagMangler() { }

  public static Treepath< SyntacticTree > enhance( final Treepath< SyntacticTree > treepath ) {
    return enhanceWithImplicitTags( enhanceWithExplicitTags( treepath ) ) ;
  }

// ========
// Explicit
// ========


  public static Treepath< SyntacticTree > enhanceWithExplicitTags(
      Treepath< SyntacticTree > treepath
  ) {
    for( Treepath< SyntacticTree > next = treepath ; next != null ; ) {
      switch( NodeKindTools.ofRoot( next.getTreeAtEnd() ).getTagBehavior() ) {
        case SCOPE :
          next = replaceByExplicitTag( next ) ;
        case TRAVERSABLE :
          next = TreepathTools.getNextInPreorder( next ) ;
          break;
        case TERMINAL :
          next = replaceByExplicitTag( next ) ;
          next = TreepathTools.getNextInPreorder( next ) ;
          break ;
        case NON_TRAVERSABLE :
          next = TreepathTools.getNextUpInPreorder( next ) ;
          break ;
      }
      if( next != null ) {
        treepath = next ;
      }
    }
    return treepath.getStart() ;
  }


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
        case PART :
        case BOOK :
          next = TreepathTools.getNextInPreorder( next ) ;
          break ;
        default :
          next = TreepathTools.getNextUpInPreorder( next ) ;
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
      ) ;
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

}

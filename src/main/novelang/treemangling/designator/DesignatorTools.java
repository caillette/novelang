package novelang.treemangling.designator;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import novelang.common.SyntacticTree;
import novelang.common.TagBehavior;
import novelang.common.tree.Traversal;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.rendering.RenderingTools;
import novelang.parser.NodeKind;
import novelang.designator.FragmentIdentifier;
import com.google.common.collect.Lists;
import com.google.common.base.Predicate;

/**
 * 
 *
 * @author Laurent Caillette
 */
public class DesignatorTools {

  public static final Predicate< SyntacticTree > IDENTIFIER_TREE_FILTER =
    new Predicate< SyntacticTree >() {
      public boolean apply( final SyntacticTree tree ) {
        return tree.isOneOf( NodeKind._LEVEL, NodeKind.PART, NodeKind.BOOK ) ;
      }
    }
  ;

  public static final Traversal.MirroredPostorder< SyntacticTree > TRAVERSAL =
    Traversal.MirroredPostorder.create( IDENTIFIER_TREE_FILTER )
  ;

  private DesignatorTools() { }

  public static final EnumSet< NodeKind > IDENTIFIER_BEARING_NODEKINDS ;

  static {
    final EnumSet< TagBehavior > BEHAVIORS =
        EnumSet.complementOf( EnumSet.of( TagBehavior.NON_TRAVERSABLE ) ) ;
    final List< NodeKind > nodeKinds = Lists.newArrayList() ;
    for( final NodeKind nodeKind : NodeKind.values() ) {
      if( BEHAVIORS.contains( nodeKind.getTagBehavior() ) ) {
        nodeKinds.add( nodeKind ) ;
      }
    }
    IDENTIFIER_BEARING_NODEKINDS = EnumSet.copyOf( nodeKinds ) ;
  }


  public static String getMarkerText( final SyntacticTree tree ) throws Exception {
    return RenderingTools.toImplicitIdentifier( tree ) ;
  }


  public static SyntacticTree findTitleTree( final SyntacticTree levelTree ) {
    if( levelTree.isOneOf( NodeKind._LEVEL ) ) {
      for( final SyntacticTree child : levelTree.getChildren() ) {
        if( child.isOneOf( NodeKind.LEVEL_TITLE ) ) {
          return child ;
        }
      }
    }
    return null ;
  }


  /**
   * Extracts an identifier (either {@link novelang.parser.NodeKind#ABSOLUTE_IDENTIFIER} or
   * {@link novelang.parser.NodeKind#RELATIVE_IDENTIFIER} if there is one as immediate child.
   */
  public static Treepath< SyntacticTree > findPathToExplicitIdentifier(
      final Treepath< SyntacticTree > parentOfIdentifier
  ) {
    final SyntacticTree parentTree = parentOfIdentifier.getTreeAtEnd() ;
    for( int i = 0 ; i < parentTree.getChildCount() ; i ++ ) {
      final SyntacticTree child = parentTree.getChildAt( i ) ;
      if( child.isOneOf( NodeKind.ABSOLUTE_IDENTIFIER, NodeKind.RELATIVE_IDENTIFIER ) ) {
        return Treepath.create( parentOfIdentifier, i ) ;
      }
    }
    return null ;
  }

  public static void dumpIdentifierMap(
      final StringBuilder stringBuilder,
      final Map< FragmentIdentifier, Treepath< SyntacticTree > > identifierMap,
      final String prefix
  ) {
    for( final Map.Entry< FragmentIdentifier, Treepath< SyntacticTree > >
        fragmentIdentifierTreepathEntry : identifierMap.entrySet()
    ) {
      stringBuilder.append( prefix ) ;
      stringBuilder.append( fragmentIdentifierTreepathEntry.getKey() ) ;
      stringBuilder.append( " -> " ) ;
      stringBuilder.append( fragmentIdentifierTreepathEntry.getValue() ) ;
    }
  }

  public static IdentifierCollisions findCollisions( Treepath< SyntacticTree > treepath ) {
    final Set< String > implicitIdentifiers = Sets.newHashSet() ;
    final Set< String > implicitIdentifierCollisions = Sets.newHashSet() ;
    final Set< String > explicitIdentifiers = Sets.newHashSet() ;
    final Set< String > explicitIdentifierCollisions = Sets.newHashSet() ;
    treepath = TRAVERSAL.first( treepath ) ; // No need for reverse preorder but reusing filter.

    while( true ) {
      detectCollisions(
          treepath,
          implicitIdentifiers,
          implicitIdentifierCollisions,
          NodeKind._IMPLICIT_IDENTIFIER
      ) ;
      detectCollisions(
          treepath,
          explicitIdentifiers,
          explicitIdentifierCollisions,
          NodeKind._EXPLICIT_IDENTIFIER
      ) ;
      final Treepath< SyntacticTree > next = TRAVERSAL.next( treepath ) ;
      if( next == null ) {
        return new IdentifierCollisions() {
          public boolean implicitIdentifierCollides( final SyntacticTree tree ) {
            return implicitIdentifierCollisions.contains( tree.getChildAt( 0 ).getText() ) ;
          }
          public boolean explicitIdentifierCollides( final SyntacticTree tree ) {
            return explicitIdentifierCollisions.contains( tree.getChildAt( 0 ).getText() ) ;
          }
        } ;
      } else {
        treepath = next ;
      }
    }
  }

  private static void detectCollisions(
      final Treepath< SyntacticTree > treepath,
      final Set< String > identifierSet,
      final Set< String > collisionSet,
      final NodeKind nodeKind
  ) {
    final SyntacticTree tree = treepath.getTreeAtEnd();
    if( tree.isOneOf( nodeKind ) ) {
      final String identifierAsString = tree.getChildAt( 0 ).getText() ;
      if( identifierSet.contains( identifierAsString ) ) {
        collisionSet.add( identifierAsString ) ;
      } else {
        identifierSet.add( identifierAsString ) ;
      }
    }
  }


  public static Treepath< SyntacticTree > removeCollidingIdentifiers(
      final IdentifierCollisions identifierCollisions,
      Treepath< SyntacticTree > treepath,
      final NodeKind nodeKind
  ) {
    treepath = TRAVERSAL.first( treepath ) ;
    while( true ) {
      final SyntacticTree parentTree = treepath.getTreeAtEnd() ;
      for( int i = 0 ; i < parentTree.getChildCount() ; i ++ ) {
        final SyntacticTree child = parentTree.getChildAt( i ) ;
        if( child.isOneOf( nodeKind )
         && identifierCollisions.implicitIdentifierCollides( child )
        ) {
          treepath = TreepathTools.removeEnd(  Treepath.create( treepath, i ) ) ;
        }
      }
      final Treepath< SyntacticTree > next = TRAVERSAL.next( treepath ) ;
      if( next == null ) {
        return treepath ;
      } else {
        treepath = next ;
      }
    }
  }

}

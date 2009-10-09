package novelang.treemangling;

import java.util.EnumSet;
import java.util.List;

import novelang.common.SyntacticTree;
import novelang.common.TagBehavior;
import novelang.common.SimpleTree;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.marker.FragmentIdentifier;
import novelang.parser.NodeKind;

import com.google.common.collect.Lists;
import com.google.common.base.Preconditions;

/**
 * @author Laurent Caillette
 */
public class FragmentExtractor {
  
  private FragmentExtractor() { }

  /**
   * Rehierarchize embedded list items.
   */
  public static Treepath< SyntacticTree > extractFragment(
      final Treepath< SyntacticTree > treepath,
      final FragmentIdentifier fragmentIdentifer
  ) {
      return find( fragmentIdentifer, treepath, null ) ;
  }


  /*package*/ static final EnumSet< NodeKind > IDENTIFIER_BEARING_NODEKINDS;
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

  /*package*/ static final NodeKind[] IDENTIFIER_BEARING_NODEKINDS_ARRAY =
      IDENTIFIER_BEARING_NODEKINDS.toArray( new NodeKind[ IDENTIFIER_BEARING_NODEKINDS.size() ] ) ;

  private static Treepath< SyntacticTree > find(
      final FragmentIdentifier identifierLookedFor,
      final Treepath< SyntacticTree > treepath,
      final FragmentIdentifier parentIdentifier
  ) {
    final SyntacticTree tree = treepath.getTreeAtEnd() ;
    if( tree.isOneOf( IDENTIFIER_BEARING_NODEKINDS_ARRAY ) ) {
      final Treepath< SyntacticTree > pathToIdentifier = findPathToIdentifier( treepath ) ;
      final FragmentIdentifier currentIdentifier ;
      if( pathToIdentifier == null ) {
        currentIdentifier = parentIdentifier ;
      } else {
        currentIdentifier = extract( parentIdentifier, pathToIdentifier ) ;
      }
      if( identifierLookedFor.equals( currentIdentifier ) ) {
        return upgradeToCompositeIdentifier( pathToIdentifier, currentIdentifier ).getPrevious() ;
      } else {

        for( int i = 0 ; i < tree.getChildCount() ; i ++ ) {
          final Treepath< SyntacticTree > resultForChild = find(
              identifierLookedFor,
              Treepath.create( treepath, i ),
              currentIdentifier == null ? parentIdentifier : currentIdentifier
          ) ;
          if( resultForChild != null ) {
            return resultForChild ;
          }
        }
      }
    }
    return null ;
  }

// ============
// Boring stuff
// ============

  /**
   * Replaces child node of {@link NodeKind#ABSOLUTE_IDENTIFIER} or
   * {@link NodeKind#RELATIVE_IDENTIFIER} at the end of the treepath by a
   * {@link NodeKind#COMPOSITE_IDENTIFIER} with given identifier.
   */
  private static Treepath< SyntacticTree > upgradeToCompositeIdentifier(
      final Treepath< SyntacticTree > pathToIdentifier,
      final FragmentIdentifier compositeIdentifier
  ) {
    Preconditions.checkArgument( pathToIdentifier.getTreeAtEnd().isOneOf(
        NodeKind.ABSOLUTE_IDENTIFIER, NodeKind.RELATIVE_IDENTIFIER ) ) ;
    final List< SyntacticTree > segmentTrees = Lists.newArrayList() ;
    for( int i = 0 ; i < compositeIdentifier.getSegmentCount() ; i ++ ) {
      segmentTrees.add( new SimpleTree( compositeIdentifier.getSegmentAt( i ) ) ) ;
    }
    final SyntacticTree newIdentifierNode = new SimpleTree(
        NodeKind.COMPOSITE_IDENTIFIER.name(), segmentTrees ) ;
    return TreepathTools.replaceTreepathEnd( pathToIdentifier, newIdentifierNode ) ;
  }


  /**
   * Extracts an identifier if there is one as a direct child.
   */
  /*package*/ static FragmentIdentifier extract(
      final FragmentIdentifier parentIdentifier,
      final Treepath< SyntacticTree > pathToIdentifier
  ) {
    final SyntacticTree identifierTree = pathToIdentifier.getTreeAtEnd() ;
    if( NodeKind.ABSOLUTE_IDENTIFIER.isRoot( identifierTree ) ) {
      return new FragmentIdentifier( extractSegment( identifierTree ) ) ;
    } else if( NodeKind.RELATIVE_IDENTIFIER.isRoot( identifierTree ) ) {
      if( parentIdentifier == null ) {
        throw new IllegalArgumentException( // TODO accumulate errors insted.
            "Missing absolute identifier above relative identifier " + identifierTree ) ;
      } else {
        return new FragmentIdentifier(
            parentIdentifier,
            new FragmentIdentifier( extractSegment( identifierTree ) )
        ) ;
      }
    }
    return null ;
  }

  /**
   * Extracts an identifier if there is one as immediate child.
   */
  /*package*/ static Treepath< SyntacticTree > findPathToIdentifier(
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


  private static String extractSegment( final SyntacticTree identifierTree ) {
    return identifierTree.getChildAt( 0 ).getText() ;
  }


}

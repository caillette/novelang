package novelang.treemangling;

import java.util.EnumSet;
import java.util.Map;
import java.util.List;

import novelang.common.SyntacticTree;
import novelang.common.TagBehavior;
import novelang.common.tree.Treepath;
import novelang.part.FragmentIdentifier;
import novelang.parser.NodeKind;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.base.Preconditions;

import org.antlr.misc.MultiMap;

/**
 * @author Laurent Caillette
 */
public class FragmentExtractor
{
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


  private static final EnumSet< NodeKind > TRAVERSABLE_NODEKINDS ;
  static {
    final EnumSet< TagBehavior > BEHAVIORS =
        EnumSet.complementOf( EnumSet.of( TagBehavior.NON_TRAVERSABLE ) ) ;
    final List< NodeKind > nodeKinds = Lists.newArrayList() ;
    for( final NodeKind nodeKind : NodeKind.values() ) {
      if( BEHAVIORS.contains( nodeKind.getTagBehavior() ) ) {
        nodeKinds.add( nodeKind ) ;
      }
    }
    TRAVERSABLE_NODEKINDS = EnumSet.copyOf( nodeKinds ) ;
  }

  private static final NodeKind[] TRAVERSABLE_NODEKINDS_ARRAY =
      TRAVERSABLE_NODEKINDS.toArray( new NodeKind[ TRAVERSABLE_NODEKINDS.size() ] ) ;

  private static Treepath< SyntacticTree > find(
      final FragmentIdentifier identifierLookedFor,
      final Treepath< SyntacticTree > treepath,
      final FragmentIdentifier parentIdentifier
  ) {
    final SyntacticTree tree = treepath.getTreeAtEnd() ;
    if( tree.isOneOf( TRAVERSABLE_NODEKINDS_ARRAY ) ) {
      final FragmentIdentifier currentIdentifier = extract( parentIdentifier, tree ) ;
      if( identifierLookedFor.equals( currentIdentifier ) ) {
        return treepath ;
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
   * Extracts an identifier if there is one as a direct child.
   */
  private static FragmentIdentifier extract(
      final FragmentIdentifier parentIdentifier,
      final SyntacticTree levelTree
  ) {
    for( final SyntacticTree child : levelTree.getChildren() ) {
      if( NodeKind.ABSOLUTE_IDENTIFIER.isRoot( child ) ) {
        return new FragmentIdentifier( extractSegments( child ) ) ;
      } else if( NodeKind.RELATIVE_IDENTIFIER.isRoot( child ) ) {
        if( parentIdentifier == null ) {
          throw new IllegalArgumentException( // TODO accumulate errors insted.
              "Missing absolute identifier above relative identifier " + child ) ;
        } else {
          final FragmentIdentifier childIdentifier =
              new FragmentIdentifier( extractSegments( child ) ) ;
          return new FragmentIdentifier( parentIdentifier, childIdentifier ) ;
        }
      }
    }
    return null ;
  }

  private static Iterable< String > extractSegments( final SyntacticTree identifierTree ) {
    Preconditions.checkArgument( identifierTree.isOneOf(
        NodeKind.ABSOLUTE_IDENTIFIER, NodeKind.RELATIVE_IDENTIFIER ) ) ;
    final List< String > segments = Lists.newArrayList() ;
    for( final SyntacticTree child : identifierTree.getChildren() ) {
      segments.add( child.getText() ) ;
    }
    return segments ;
  }


}

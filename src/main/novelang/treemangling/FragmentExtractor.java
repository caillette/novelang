package novelang.treemangling;

import java.util.EnumSet;
import java.util.Map;
import java.util.List;

import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import novelang.part.FragmentIdentifier;
import novelang.parser.NodeKind;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
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
      throw new UnsupportedOperationException( "extractFragment" ) ;
  }

  private static final EnumSet< NodeKind > LEVEL_ENABLED_NODEKINDS = EnumSet.of( NodeKind._LEVEL ) ;
  private static final EnumSet< NodeKind > IDENTIFIER_NODEKINDS =
          EnumSet.of( NodeKind.ABSOLUTE_IDENTIFIER, NodeKind.RELATIVE_IDENTIFIER ) ;


  private Multimap< FragmentIdentifier, SyntacticTree > createIdentifierMap(
      final Treepath< SyntacticTree > treepath
  ) {
    final Multimap< FragmentIdentifier, SyntacticTree > wholeMap = createIdentifierMap(
        HashMultimap.< FragmentIdentifier, SyntacticTree >create(), null, treepath ) ;

    throw new UnsupportedOperationException( "createIdentifierMap" ) ;
  }


  private Multimap< FragmentIdentifier, SyntacticTree > createIdentifierMap(
      final Multimap< FragmentIdentifier, SyntacticTree > ancestorMap,
      final FragmentIdentifier identifierForScope,
      final Treepath< SyntacticTree > treepath
  ) {
    final Multimap< FragmentIdentifier, SyntacticTree > localMap = HashMultimap.create() ;
    final SyntacticTree tree = treepath.getTreeAtEnd() ;
    if( NodeKind._LEVEL.isRoot( tree ) ) {

    }

    throw new UnsupportedOperationException( "createIdentifierMap" ) ;
  }

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
//          return new FragmentIdentifier( parentIdentifier, extractSegments( child ) ) ;
        }            
      }
    }

    throw new UnsupportedOperationException( "extract" ) ;
  }

  private static List< String > extractSegments( final SyntacticTree identifierTree ) {
    final List< String > segments = Lists.newArrayList() ;
    for( final SyntacticTree child : identifierTree.getChildren() ) {
      segments.add( child.getText() ) ;
    }
    return segments ;
  }


}

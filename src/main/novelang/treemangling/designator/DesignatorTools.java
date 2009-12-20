package novelang.treemangling.designator;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import novelang.common.SyntacticTree;
import novelang.common.TagBehavior;
import novelang.common.tree.Treepath;
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
        return tree.isOneOf( NodeKind._LEVEL, NodeKind.PART ) ;
      }
    }
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

  public static final NodeKind[] IDENTIFIER_BEARING_NODEKINDS_ARRAY =
      IDENTIFIER_BEARING_NODEKINDS.toArray( new NodeKind[ IDENTIFIER_BEARING_NODEKINDS.size() ] ) ;


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
    for( final FragmentIdentifier fragmentIdentifier : identifierMap.keySet() ) {
      stringBuilder.append( prefix ) ;
      stringBuilder.append( fragmentIdentifier ) ;
      stringBuilder.append( " -> " ) ;
      stringBuilder.append( identifierMap.get( fragmentIdentifier ) ) ;
    }
  }
}

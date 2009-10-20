package novelang.treemangling.designator;

import java.util.List;

import novelang.common.SyntacticTree;
import novelang.common.Problem;
import novelang.common.tree.Treepath;
import novelang.parser.NodeKind;
import novelang.marker.FragmentIdentifier;

/**
 *
 *
 * @author Laurent Caillette
*/
public class SegmentExtractor
{
  private final IdentifierDefinition identifierDefinition ;
  private final String segment ;

  /**
   *
   * @param treepath Path to the node that may carry some (implicit or explicit) identifier.
   * @param problems a list of problems to add to if there is one.
   */
  public SegmentExtractor(
      final Treepath< SyntacticTree > treepath,
      final List< Problem > problems
  ) {

    final Treepath< SyntacticTree > pathToIdentifier =
        DesignatorTools.findPathToExplicitIdentifier( treepath ) ;
    final SyntacticTree tree = pathToIdentifier == null ?
            null :
            pathToIdentifier.getTreeAtEnd()
    ;

    if( NodeKind.ABSOLUTE_IDENTIFIER.isRoot( tree ) ) {
      segment = extractSegment( tree ) ;
      identifierDefinition = IdentifierDefinition.ABSOLUTE ;
    } else if( NodeKind.RELATIVE_IDENTIFIER.isRoot( tree ) ) {
      segment = extractSegment( tree ) ;
      identifierDefinition = IdentifierDefinition.RELATIVE ;
    } else {
      String markerText ;
      final SyntacticTree titleTree = DesignatorTools.findTitleTree( treepath.getTreeAtEnd() ) ;

      if( titleTree == null ) {
        markerText = null ;
      } else {
        try {
          markerText = DesignatorTools.getMarkerText( titleTree ) ;
        } catch( Exception e ) {
          // Exception type is a bit wide but it's due to the signature of getMarkerText
          // which delegates to a Renderer/Writer inside of which so many things can go wrong.
          // Not sure this is a Problem (user input), may be a technical exception at this stage.
          problems.add( Problem.createProblem( e ) ) ;
          markerText = null ;
        }
      }
      if( markerText == null ) {
        segment = null ;
        identifierDefinition = IdentifierDefinition.NONE ;
      } else {
        segment = markerText ;
        identifierDefinition = IdentifierDefinition.IMPLICIT ;
      }
    }
  }

  private static String extractSegment( final SyntacticTree identifierTree ) {
    return identifierTree.getChildAt( 0 ).getText() ;
  }

  public IdentifierDefinition getIdentifierDefinition() {
    return identifierDefinition ;
  }

  public String getSegment() {
    return segment ;
  }

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
}

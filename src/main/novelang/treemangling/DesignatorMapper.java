package novelang.treemangling;

import java.util.Map;
import java.util.List;

import novelang.common.SyntacticTree;
import novelang.common.Problem;
import novelang.common.tree.Treepath;
import novelang.marker.FragmentIdentifier;
import novelang.parser.NodeKind;
import novelang.rendering.RenderingTools;
import novelang.system.DefaultCharset;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;

/**
 * Creates a Map of every valid designator (identifier or, in a near future, tag).
 *
 * @author Laurent Caillette
 */
public class DesignatorMapper
{
    /**
     * Contain only pure identifiers (defined explicitely).
     */
    private final Map< FragmentIdentifier, Treepath > pureIdentifiers = Maps.newHashMap() ;

    /**
     * Contain implicit identifiers, or implicit identifiers mixed with explicit identifiers.
     */
    private final Map< FragmentIdentifier, Treepath > mixedIdentifiers = Maps.newHashMap() ;

    private final List< Problem > problems = Lists.newArrayList() ;

    public DesignatorMapper( final Treepath< SyntacticTree > treepath )
    {
        
    }

    /**
     * Returns the map of pure identifiers (those made out only from
     * {@link NodeKind#ABSOLUTE_IDENTIFIER} and {@link NodeKind#RELATIVE_IDENTIFIER} nodes).
     *
     * @return a non-null map containing no nulls, with {@code Treepath} objects
     *     referencing the same tree as passed to the constructor.
     */
    public Map< FragmentIdentifier, Treepath< SyntacticTree > > getPureIdentifierMap() {
        throw new UnsupportedOperationException( "getPureIdentifierMap" ) ;
    }


    /**
     * Returns the map of derived identifiers (those which are not pure).
     *
     * @return a non-null map containing no nulls, with {@code Treepath} objects
     *     referencing the same tree as passed to the constructor.
     */
    public Map< FragmentIdentifier, Treepath< SyntacticTree > > getDerivedIdentifierMap() {
        throw new UnsupportedOperationException( "getDerivedIdentifierMap" ) ;
    }

    /**
     * Returns problems like duplicate identifier.
     */
    public Iterable< Problem > getProblems() {
        throw new UnsupportedOperationException( "getProblems" ) ;
    }


    private void process(
            final Treepath< SyntacticTree > treepath,
            final FragmentIdentifier parentIdentifier
    ) {
      final SyntacticTree tree = treepath.getTreeAtEnd() ;
      if( tree.isOneOf( FragmentExtractor.IDENTIFIER_BEARING_NODEKINDS ) ) {
          final Treepath< SyntacticTree > pathToIdentifier =
                  FragmentExtractor.findPathToIdentifier( treepath ) ;
          final FragmentIdentifier currentIdentifier ;
          final boolean implicit ;
          if( pathToIdentifier == null ) {
              try {
                  final String markerText = getMarkerText( findTitleTree( tree ) ) ;
                  if( markerText == null ) {
                    implicit = false ;
                  } else {
                    currentIdentifier = new FragmentIdentifier( markerText ) ;
                    implicit = true ;
                  }
              } catch( Exception e ) {
                  problems.add( Problem.createProblem( e ) ) ;
              }
          } else {
            currentIdentifier = FragmentExtractor.extract( parentIdentifier, pathToIdentifier ) ;
            implicit = false ;
          }
      }

    }

    private static String getMarkerText( final SyntacticTree tree ) throws Exception
    {
      final SyntacticTree treeWithTitle = findTitleTree( tree ) ;
      if( treeWithTitle == null ) {
        return null ;
      } else {
        return RenderingTools.markerize( tree, DefaultCharset.RENDERING ) ;
      }
    }


    private static SyntacticTree findTitleTree( final SyntacticTree levelTree ) {
      if( levelTree.isOneOf( NodeKind._LEVEL ) ) {
        for( final SyntacticTree child : levelTree.getChildren() ) {
          if( child.isOneOf( NodeKind.LEVEL_TITLE ) ) {
            return child ;
          }
        }
      }
      return null ;
    }

}

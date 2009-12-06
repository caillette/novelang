package novelang.treemangling.designator;

import novelang.common.Problem;
import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import novelang.common.tree.RobustPath;
import novelang.marker.FragmentIdentifier;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class creates maps from {@link FragmentIdentifier} to {@link Treepath} objects
 * through {@link RobustPath}.
 *
 * @see novelang.treemangling.DesignatorInterpreter
 *
 * @author Laurent Caillette
 */
public class BabyInterpreter implements FragmentMapper< RobustPath< SyntacticTree > > {

  /**
   * Contain only pure identifiers (defined explicitely).
   */
  private final Map< FragmentIdentifier, RobustPath< SyntacticTree > > pureIdentifiers ;

  /**
   * Contain implicit identifiers, or implicit identifiers mixed with explicit identifiers.
   */
  private final Map< FragmentIdentifier, RobustPath< SyntacticTree > > derivedIdentifiers ;


  private final List< Problem > problems ;

  public BabyInterpreter( final Treepath< SyntacticTree > treepath ) {
    final Collector collector = new Collector() ;
    process( collector, treepath, null ) ;
    pureIdentifiers = Collections.unmodifiableMap( collector.pureIdentifiers ) ;
    for( final FragmentIdentifier duplicate : collector.duplicateDerivedIdentifiers ) {
      collector.derivedIdentifiers.remove( duplicate ) ;
    }

    derivedIdentifiers = Collections.unmodifiableMap( collector.derivedIdentifiers ) ;
    problems = Collections.unmodifiableList( collector.problems ) ;

  }

  /**
   * Returns the map of pure identifiers (those made out only from
   * {@link novelang.parser.NodeKind#ABSOLUTE_IDENTIFIER} and
   * {@link novelang.parser.NodeKind#RELATIVE_IDENTIFIER} nodes).
   *
   * @return a non-null, immutable map containing no nulls, with {@code Treepath} objects
   *     referencing the same tree as passed to the constructor.
   */
  public Map< FragmentIdentifier, RobustPath< SyntacticTree > > getPureIdentifierMap() {
    return pureIdentifiers ;
  }


  /**
   * Returns the map of derived identifiers (those which are not pure).
   *
   * @return a non-null, immutable map containing no nulls, with {@code Treepath} objects
   *     referencing the same tree as passed to the constructor.
   */
  public Map< FragmentIdentifier, RobustPath< SyntacticTree > > getDerivedIdentifierMap() {
    return derivedIdentifiers ;
  }


  /**
   * Returns problems like duplicate identifier.
   */
  public Iterable< Problem > getProblems() {
    return problems ;
  }

  public boolean hasProblem() {
    return ! problems.isEmpty() ;
  }


  private void process(
      final Collector collector,
      final Treepath< SyntacticTree > treepath,
      final FragmentIdentifier parentIdentifier
  ) {
    final SyntacticTree tree = treepath.getTreeAtEnd() ;
    if( tree.isOneOf( DesignatorTools.IDENTIFIER_BEARING_NODEKINDS ) ) {
      final FragmentIdentifier explicitIdentifier ;
      final SegmentExtractor segmentExtractor = 
          new SegmentExtractor( treepath, collector.problems ) ;
      final IdentifierDefinition definition = segmentExtractor.getIdentifierDefinition() ;
      final String segment = segmentExtractor.getSegment() ;

      switch( definition ) {

        case NONE :
          explicitIdentifier = null ;
          break ;

        case ABSOLUTE :
          explicitIdentifier = new FragmentIdentifier( segment ) ;
          if( verifyFreshness( collector, tree, explicitIdentifier, collector.pureIdentifiers ) ) {
            collector.pureIdentifiers.put(
                explicitIdentifier,
                RobustPath.create( treepath, DesignatorTools.IDENTIFIER_TREE_FILTER )
            ) ;
          }
          break ;

        case RELATIVE :
          if ( parentIdentifier == null ) {
            addProblem(
                collector,
                tree,
                "Missing absolute parent identifier for relative identifier '" + segment + "'"
            ) ;
            explicitIdentifier = null ;
          } else {
            explicitIdentifier = new FragmentIdentifier( parentIdentifier, segment ) ;
            if( verifyFreshness( 
                collector, tree, explicitIdentifier, collector.pureIdentifiers ) 
            ) {
              collector.pureIdentifiers.put(
                  explicitIdentifier,
                  RobustPath.create( treepath, DesignatorTools.IDENTIFIER_TREE_FILTER )
              ) ;
            }
          }
          break ;

        case IMPLICIT :
          explicitIdentifier = null ;
          final FragmentIdentifier implicitAbsoluteIdentifier = new FragmentIdentifier( segment ) ;
          if( collector.derivedIdentifiers.containsKey( implicitAbsoluteIdentifier ) ) {
            collector.duplicateDerivedIdentifiers.add( implicitAbsoluteIdentifier ) ;
          } else {
            collector.derivedIdentifiers.put( 
                implicitAbsoluteIdentifier,
                RobustPath.create( treepath, DesignatorTools.IDENTIFIER_TREE_FILTER )
            ) ;
          }

          if( parentIdentifier != null ) {
            final FragmentIdentifier implicitRelativeIdentifier =
                new FragmentIdentifier( parentIdentifier, segment ) ;
            if( collector.derivedIdentifiers.containsKey( implicitRelativeIdentifier ) ) {
              collector.duplicateDerivedIdentifiers.add( implicitRelativeIdentifier ) ;
            } else {
              collector.derivedIdentifiers.put( 
                  implicitRelativeIdentifier,
                  RobustPath.create( treepath, DesignatorTools.IDENTIFIER_TREE_FILTER )
              ) ;
            }
          }
          break ;

        default :
          throw new IllegalArgumentException( "Unsupported: " + definition ) ;
      }

      for( int i = 0 ; i < tree.getChildCount() ; i ++ ) {
        process(
            collector,
            Treepath.create( treepath, i ),
            explicitIdentifier
        ) ;
      }

    }
  }

  private boolean verifyFreshness(
      final Collector collector,
      final SyntacticTree tree,
      final FragmentIdentifier fragmentIdentifier,
      final Map< FragmentIdentifier, RobustPath< SyntacticTree > > map
  ) {
    if( map.containsKey( fragmentIdentifier ) ) {
      final String message = "Already defined: '" + fragmentIdentifier + "'" ;
      addProblem( collector, tree, message ) ;
      return false ;
    }
    return true ;
  }

  private void addProblem( 
      final Collector collector, 
      final SyntacticTree tree, 
      final String message 
  ) {
    if( tree.getLocation()  == null ) {
      // Only for tests.
      collector.problems.add( Problem.createProblem( message ) ) ;
    } else {
      collector.problems.add( Problem.createProblem(
          message,
          tree.getLocation() )
      ) ;
    }
  }

  
  private static class Collector {
    
    /**
     * Contain only pure identifiers (defined explicitely).
     */
    public final Map< FragmentIdentifier, RobustPath< SyntacticTree > >
    pureIdentifiers = Maps.newHashMap() ;

    /**
     * Contain implicit identifiers, or implicit identifiers mixed with explicit identifiers.
     */
    public final Map< FragmentIdentifier, RobustPath< SyntacticTree > >
    derivedIdentifiers = Maps.newHashMap() ;


    public Set< FragmentIdentifier > duplicateDerivedIdentifiers = Sets.newHashSet() ;

    public List< Problem > problems = Lists.newArrayList() ;
  }
}
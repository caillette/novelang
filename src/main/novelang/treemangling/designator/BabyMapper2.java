package novelang.treemangling.designator;

import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.Set;
import java.util.Iterator;

import novelang.common.SyntacticTree;
import novelang.common.Problem;
import novelang.common.tree.Treepath;
import novelang.marker.FragmentIdentifier;
import novelang.parser.NodeKind;
import novelang.system.LogFactory;
import novelang.system.Log;
import novelang.treemangling.designator.IdentifierDefinition;
import novelang.treemangling.designator.SegmentExtractor;
import novelang.treemangling.designator.DesignatorTools;
import novelang.treemangling.DesignatorMapper;

import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableSet;

/**
 * Creates a Map of every valid designator (identifier or, in a near future, tag).
 * This class creates maps from {@link novelang.marker.FragmentIdentifier} to {@link novelang.common.tree.Treepath} objects.
 * Its maps are mutable and it doesn't attempt to derive the {@link novelang.common.SyntacticTree} referenced
 * by the {@link novelang.common.tree.Treepath} objects.
 *
 * @see novelang.treemangling.DesignatorMapper
 *
 * @author Laurent Caillette
 */
public class BabyMapper2
{

  /**
   * Contain only pure identifiers (defined explicitely).
   */
  private Map< FragmentIdentifier, int[] > pureIdentifiers = Maps.newHashMap() ;

  /**
   * Contain implicit identifiers, or implicit identifiers mixed with explicit identifiers.
   */
  private Map< FragmentIdentifier, int[] > derivedIdentifiers = Maps.newHashMap() ;


  private Set< FragmentIdentifier > duplicateDerivedIdentifiers = Sets.newHashSet() ;

  private List< Problem > problems = Lists.newArrayList() ;

  public BabyMapper2( final Treepath< SyntacticTree > treepath ) {
    process( treepath, null ) ;
    pureIdentifiers = Collections.unmodifiableMap( pureIdentifiers ) ;
    for( final FragmentIdentifier duplicate : duplicateDerivedIdentifiers ) {
      derivedIdentifiers.remove( duplicate );

      final Iterator<FragmentIdentifier> derivedIdentifiersIterator =
          derivedIdentifiers.keySet().iterator();
    }
    duplicateDerivedIdentifiers = null ;

    derivedIdentifiers = Collections.unmodifiableMap( derivedIdentifiers ) ;
    problems = Collections.unmodifiableList( problems ) ;

  }

  /**
   * Returns the map of pure identifiers (those made out only from
   * {@link novelang.parser.NodeKind#ABSOLUTE_IDENTIFIER} and
   * {@link novelang.parser.NodeKind#RELATIVE_IDENTIFIER} nodes).
   *
   * @return a non-null, immutable map containing no nulls, with {@code Treepath} objects
   *     referencing the same tree as passed to the constructor.
   */
  public Map< FragmentIdentifier, int[] > getPureIdentifierMap() {
    return pureIdentifiers ;
  }


  /**
   * Returns the map of derived identifiers (those which are not pure).
   *
   * @return a non-null, immutable map containing no nulls, with {@code Treepath} objects
   *     referencing the same tree as passed to the constructor.
   */
  public Map< FragmentIdentifier, int[] > getDerivedIdentifierMap() {
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
      final Treepath< SyntacticTree > treepath,
      final FragmentIdentifier parentIdentifier
  ) {
    final SyntacticTree tree = treepath.getTreeAtEnd() ;
    if( tree.isOneOf( DesignatorTools.IDENTIFIER_BEARING_NODEKINDS ) ) {
      final FragmentIdentifier explicitIdentifier ;
      final SegmentExtractor segmentExtractor = new SegmentExtractor( treepath, problems ) ;
      final IdentifierDefinition definition = segmentExtractor.getIdentifierDefinition() ;
      final String segment = segmentExtractor.getSegment() ;

      switch ( definition ) {

        case NONE :
          explicitIdentifier = null ;
          break ;

        case ABSOLUTE :
          explicitIdentifier = new FragmentIdentifier( segment ) ;
          if( verifyFreshness( tree, explicitIdentifier, pureIdentifiers ) ) {
            pureIdentifiers.put( explicitIdentifier, treepath.getIndicesInParent() ) ;
          }
          break ;

        case RELATIVE :
          if ( parentIdentifier == null ) {
            addProblem(
                tree,
                "Missing absolute parent identifier for relative identifier '" + segment + "'"
            ) ;
            explicitIdentifier = null ;
          } else {
            explicitIdentifier = new FragmentIdentifier( parentIdentifier, segment ) ;
            if( verifyFreshness( tree, explicitIdentifier, pureIdentifiers ) ) {
              pureIdentifiers.put( explicitIdentifier, treepath.getIndicesInParent() ) ;
            }
          }
          break ;

        case IMPLICIT :
          explicitIdentifier = null ;
          final FragmentIdentifier implicitAbsoluteIdentifier = new FragmentIdentifier( segment ) ;
          if( derivedIdentifiers.containsKey( implicitAbsoluteIdentifier ) ) {
            duplicateDerivedIdentifiers.add( implicitAbsoluteIdentifier ) ;
          } else {
            derivedIdentifiers.put( implicitAbsoluteIdentifier, treepath.getIndicesInParent() ) ;
          }

          if( parentIdentifier != null ) {
            final FragmentIdentifier implicitRelativeIdentifier =
                new FragmentIdentifier( parentIdentifier, segment ) ;
            if( derivedIdentifiers.containsKey( implicitRelativeIdentifier ) ) {
              duplicateDerivedIdentifiers.add( implicitRelativeIdentifier ) ;
            } else {
              derivedIdentifiers.put( implicitRelativeIdentifier, treepath.getIndicesInParent() ) ;
            }
          }
          break ;

        default :
          throw new IllegalArgumentException( "Unsupported: " + definition ) ;
      }

      for( int i = 0 ; i < tree.getChildCount() ; i ++ ) {
        process(
            Treepath.create( treepath, i ),
            explicitIdentifier
        ) ;
      }

    }
  }

  private boolean verifyFreshness(
      final SyntacticTree tree,
      final FragmentIdentifier fragmentIdentifier,
      final Map< FragmentIdentifier, int[] > map
  ) {
    if( map.containsKey( fragmentIdentifier ) ) {
      final String message = "Already defined: '" + fragmentIdentifier + "'" ;
      addProblem( tree, message ) ;
      return false ;
    }
    return true ;
  }

  private void addProblem( final SyntacticTree tree, final String message ) {
    if( tree.getLocation()  == null ) {
      // Only for tests.
      problems.add( Problem.createProblem( message ) ) ;
    } else {
      problems.add( Problem.createProblem(
          message,
          tree.getLocation() )
      ) ;
    }
  }


}
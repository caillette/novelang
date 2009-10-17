package novelang.treemangling;

import java.util.Map;

import novelang.common.SyntacticTree;
import novelang.common.Problem;
import novelang.common.tree.Treepath;
import novelang.marker.FragmentIdentifier;
import novelang.parser.NodeKind;
import novelang.system.LogFactory;
import novelang.system.Log;
import novelang.treemangling.designator.DesignatorTools;
import novelang.treemangling.designator.BabyMapper;
import novelang.treemangling.designator.BabyMapper2;
import novelang.treemangling.designator.FragmentMapper;

/**
 * Creates a Map of every valid designator (identifier or, in a near future, tag).
 *
 * @author Laurent Caillette
 */
public class DesignatorMapper {
  
  private static final Log LOG = LogFactory.getLog( DesignatorMapper.class ) ;

  /**
   * Contain only pure identifiers (defined explicitely).
   */
  private final Map< FragmentIdentifier, Treepath< SyntacticTree > > pureIdentifiers ;

  /**
   * Contain implicit identifiers, or implicit identifiers mixed with explicit identifiers.
   */
  private final Map< FragmentIdentifier, Treepath< SyntacticTree > > derivedIdentifiers ;


  private final Iterable< Problem > problems  ;

  public DesignatorMapper( final Treepath< SyntacticTree > treepath ) {
    final BabyMapper babyMapper = new BabyMapper( treepath ) ;
    pureIdentifiers = babyMapper.getPureIdentifierMap() ;
    derivedIdentifiers = babyMapper.getDerivedIdentifierMap() ;
    problems = babyMapper.getProblems() ;

    final StringBuilder stringBuilder = new StringBuilder() ;
    stringBuilder.append( "\n  Pure identifiers:" ) ;
    DesignatorTools.dumpIdentifierMap( stringBuilder, pureIdentifiers, "\n    " ) ;
    stringBuilder.append( "\n  Derived identifiers:" ) ;
    DesignatorTools.dumpIdentifierMap( stringBuilder, derivedIdentifiers, "\n    " ) ;
    
    LOG.debug( "Created %s%s", this, stringBuilder ) ;

  }

  /**
   * Returns the map of pure identifiers (those made out only from
   * {@link NodeKind#ABSOLUTE_IDENTIFIER} and {@link NodeKind#RELATIVE_IDENTIFIER} nodes).
   *
   * @return a non-null, immutable map containing no nulls, with {@code Treepath} objects
   *     referencing the same tree as passed to the constructor.
   */
  /*package*/ Map< FragmentIdentifier, Treepath< SyntacticTree > > getPureIdentifierMap() {
    return pureIdentifiers ;
  }


  /**
   * Returns the map of derived identifiers (those which are not pure).
   *
   * @return a non-null, immutable map containing no nulls, with {@code Treepath} objects
   *     referencing the same tree as passed to the constructor.
   */
  /*package*/ Map< FragmentIdentifier, Treepath< SyntacticTree > > getDerivedIdentifierMap() {
    return derivedIdentifiers ;
  }
  
  public Treepath< SyntacticTree > get( final FragmentIdentifier fragmentIdentifier ) {
    Treepath< SyntacticTree > treepath = pureIdentifiers.get( fragmentIdentifier ) ;
    if( treepath == null ) {
      treepath = derivedIdentifiers.get( fragmentIdentifier ) ;
    }
    return treepath ;
  }


  /**
   * Returns a Map referencing {@code Treepath} objects given {@code FragmentIdentifiers},
   * given the root {@code Treepath} and a Map providing tree indices given a
   * {@code FragmentIdentifier}.
   */
  private static Map< FragmentIdentifier, Treepath< SyntacticTree > > remap(
      final Map< FragmentIdentifier, int[] > map,
      final Treepath treepath
  ) {
    throw new UnsupportedOperationException( "remap" ) ;
  }

  /**
   * Returns problems like duplicate identifier.
   */
  public Iterable< Problem > getProblems() {
    return problems ;
  }

  public boolean hasProblem() {
    return problems.iterator().hasNext() ;
  }





}

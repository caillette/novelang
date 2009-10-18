package novelang.treemangling;

import novelang.common.Problem;
import novelang.common.SimpleTree;
import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.marker.FragmentIdentifier;
import novelang.parser.NodeKind;
import novelang.system.Log;
import novelang.system.LogFactory;
import novelang.treemangling.designator.BabyInterpreter;
import novelang.treemangling.designator.DesignatorTools;
import novelang.treemangling.designator.FragmentMapper;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

/**
 * Creates a Mapping from designators ({@link FragmentIdentifier}s or, in the future, tags)
 * and to {@code Treepath}s with nodes containing expanded identifiers (both explicit
 * and implicit, resolved to absolute identifiers).
 * <p>
 * Implementation: due to the immutable nature of the {@code Treepath} this is done 
 * in several passes. 
 * <ol>
 *   <li>
 *     The {@link novelang.treemangling.designator.BabyInterpreter} (used internally) gathers references on the nodes containing 
 *     identifiers, resolving implicit references and finding problems.
 *   </li>
 *   <li>
 *     The {@link #enrich(Treepath, FragmentMapper)} method (used internally) enhances the 
 *     {@code Treepath} with new nodes (
 *     {@link NodeKind#_IMPLICIT_IDENTIFIER} and
 *     {@link NodeKind#_EXPLICIT_IDENTIFIER}
 *     ) containing resolved identifiers. 
 *     Those nodes enhance document readability as they show resolved identifiers.
 *     The same method removes 
 *     {@link NodeKind#ABSOLUTE_IDENTIFIER} {@link NodeKind#RELATIVE_IDENTIFIER} 
 *     nodes which don't improve readabilit.
 *   </li>
 *   <li>
 *     Identifiers from {@link novelang.treemangling.designator.BabyInterpreter} are re-mapped on the new {@code Treepath}. 
 *   </li>
 * </ol> 
 * 
 *
 * @author Laurent Caillette
 */
public class DesignatorInterpreter {
  
  private static final Log LOG = LogFactory.getLog( DesignatorInterpreter.class ) ;

  /**
   * Contains only pure identifiers (defined explicitely).
   */
  private final Map< FragmentIdentifier, Treepath< SyntacticTree > > pureIdentifiers ;

  /**
   * Contains implicit identifiers, or implicit identifiers mixed with explicit identifiers.
   */
  private final Map< FragmentIdentifier, Treepath< SyntacticTree > > derivedIdentifiers ;

  private final Iterable< Problem > problems  ;

  public DesignatorInterpreter( final Treepath< SyntacticTree > treepath ) {
    final BabyInterpreter babyInterpreter = new BabyInterpreter( treepath ) ;
    final Treepath< SyntacticTree > enrichedTreepath = enrich( treepath, babyInterpreter ) ;

    pureIdentifiers = remap( babyInterpreter.getPureIdentifierMap(), enrichedTreepath ) ;
    derivedIdentifiers = remap( babyInterpreter.getDerivedIdentifierMap(), enrichedTreepath ) ;
    problems = babyInterpreter.getProblems() ;

    final StringBuilder stringBuilder = new StringBuilder() ;
    stringBuilder.append( "\n  Pure identifiers:" ) ;
    DesignatorTools.dumpIdentifierMap( stringBuilder, pureIdentifiers, "\n    " ) ;
    stringBuilder.append( "\n  Derived identifiers:" ) ;
    DesignatorTools.dumpIdentifierMap( stringBuilder, derivedIdentifiers, "\n    " ) ;
    
    LOG.debug( "Created %s%s", this, stringBuilder ) ;

  }
  
  
  public Treepath< SyntacticTree > get( final FragmentIdentifier fragmentIdentifier ) {
    Treepath< SyntacticTree > treepath = pureIdentifiers.get( fragmentIdentifier ) ;
    if( treepath == null ) {
      treepath = derivedIdentifiers.get( fragmentIdentifier ) ;
    }
    return treepath ;
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


// ===============  
// Transformations
// ===============
  
  private static Map< FragmentIdentifier, Treepath< SyntacticTree > >
  remap( final Map< FragmentIdentifier, int[] > map, final Treepath< SyntacticTree > treepath ) {
    final Map< FragmentIdentifier, Treepath< SyntacticTree > > result = Maps.newHashMap() ;
    for( final Map.Entry< FragmentIdentifier, int[] > entry : map.entrySet() ) {
      result.put( 
          entry.getKey(), 
          Treepath.create( treepath.getTreeAtStart(), entry.getValue() ) 
      ) ;
    }
    return result ;
  }
  
  
  

  /**
   * Made protected for tests only.
   */
  protected static Treepath< SyntacticTree > enrich(
      Treepath< SyntacticTree > treepath,
      final FragmentMapper< int[] > mapper
  ) {
    final SyntacticTree tree = treepath.getTreeAtEnd() ;
    if( tree.isOneOf( DesignatorTools.IDENTIFIER_BEARING_NODEKINDS ) ) {
      treepath = removeChildren( 
          treepath, 
          NodeKind.ABSOLUTE_IDENTIFIER, 
          NodeKind.RELATIVE_IDENTIFIER 
      ) ;
      final FragmentIdentifier pureIdentifier = 
          findIdentifier( treepath, mapper.getPureIdentifierMap() ) ;
      if( pureIdentifier != null ) {
        treepath = add( treepath, pureIdentifier, NodeKind._EXPLICIT_IDENTIFIER ) ;
      }
      final FragmentIdentifier derivedIdentifier = 
          findIdentifier( treepath, mapper.getDerivedIdentifierMap() ) ;
      if( derivedIdentifier != null ) {
        treepath = add( treepath, derivedIdentifier, NodeKind._IMPLICIT_IDENTIFIER ) ;
      }
                
      for( int i = 0 ; i < tree.getChildCount() ; i ++ ) {
        treepath = enrich( Treepath.create( treepath, i ), mapper ).getPrevious() ;
      }
    }    
    return treepath ;
  }

  public static Treepath< SyntacticTree > removeChildren( 
      final Treepath< SyntacticTree > treepath, 
      final NodeKind... nodeKindsToRemove
  ) {
    final SyntacticTree tree = treepath.getTreeAtEnd() ;
    for( int i = 0 ; i < tree.getChildCount() ; i ++ ) {
      final SyntacticTree child = tree.getChildAt( i ) ;
      if( child.isOneOf( nodeKindsToRemove ) ) {
        return TreepathTools.removeEnd( Treepath.create( treepath, i ) ) ;
      }
    }
    return treepath ;    
  }

  public static Treepath< SyntacticTree > add( 
      final Treepath< SyntacticTree > treepath, 
      final FragmentIdentifier fragmentIdentifier, 
      final NodeKind explicitIdentifier 
  ) {
    final SyntacticTree identifierTree = new SimpleTree( 
        explicitIdentifier, 
        new SimpleTree( fragmentIdentifier.getAbsoluteRepresentation() ) 
    ) ;
    return TreepathTools.addChildFirst( treepath, identifierTree ).getPrevious() ;
  }

  public static FragmentIdentifier findIdentifier( 
      final Treepath< SyntacticTree > treepath, 
      Map< FragmentIdentifier, int[] > map 
  ) {
    final int[] indexesInParent = treepath.getIndicesInParent() ;
    for( final Map.Entry< FragmentIdentifier, int[] > entry : map.entrySet() ) {
      if( Arrays.equals( indexesInParent, entry.getValue() ) ) {
        return entry.getKey() ;
      }
    }
    return null ;
  }
}

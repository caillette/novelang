package novelang.treemangling;

import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import novelang.marker.FragmentIdentifier;
import novelang.parser.NodeKind;
import static novelang.parser.NodeKind.ABSOLUTE_IDENTIFIER;
import novelang.parser.antlr.TreeFixture;
import static novelang.parser.antlr.TreeFixture.tree;
import novelang.system.Log;
import novelang.system.LogFactory;
import novelang.treemangling.DesignatorInterpreter;
import novelang.treemangling.EmbeddedListMangler;

import com.google.common.collect.ImmutableMap;
import novelang.treemangling.designator.FragmentMapper;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

/**
 * Tests for
 * {@link novelang.treemangling.DesignatorInterpreter#enrich(Treepath, novelang.treemangling.designator.FragmentMapper)}
 * which modifies identifier stuff in a {@code Treepath}. 
 *
 * @author Laurent Caillette
 */
public class DesignatorInterpreterEnrichmentTest {

  @Test
  public void enrichNothing() {
    verifyEnrich(
        tree( NodeKind.PART ),
        tree( NodeKind.PART ),
        new FragmentMapperBuilder().build()
    ) ;
  }


  @Test
  public void enrichWithSimpleAbsoluteIdentifier() {
    final SyntacticTree levelTree = tree(
        NodeKind._LEVEL,
        tree( ABSOLUTE_IDENTIFIER, tree( "L0" ) )
    ) ;

    final SyntacticTree partTree = tree( NodeKind.PART, levelTree ) ;

    final Treepath< SyntacticTree > levelTreepath = Treepath.create( partTree, 0 ) ;

    verifyEnrich(
        tree(
            NodeKind.PART,
            tree(
                NodeKind._LEVEL,
//                tree( NodeKind._IMPLICIT_IDENTIFIER, "\\\\L0" ),
                tree( NodeKind._EXPLICIT_IDENTIFIER, "\\\\L0" )
            )                
        ),
        partTree,
        new FragmentMapperBuilder().
            addPure( new FragmentIdentifier( "L0" ), levelTreepath.getIndicesInParent() ).
            build()
    ) ;
  }

  @Test
  public void enrichWithSimpleImplicitIdentifier() {
    
    final SyntacticTree levelTree = tree(
        NodeKind._LEVEL
    ) ;

    final SyntacticTree partTree = tree( NodeKind.PART, levelTree ) ;

    final Treepath< SyntacticTree > levelTreepath = Treepath.create( partTree, 0 ) ;

    final FragmentMapper<int[]> mapper = new FragmentMapperBuilder().
        addDerived( new FragmentIdentifier( "L0" ), levelTreepath.getIndicesInParent() ).
        build()
    ;
    
    verifyEnrich(
        tree(
            NodeKind.PART,
            tree(
                NodeKind._LEVEL,
                tree( NodeKind._IMPLICIT_IDENTIFIER, "\\\\L0" )
            )                
        ),
        partTree,
        mapper
    ) ;
  }


  @Test
  public void beSureOfWhatHappensWithArrayComparison() {
    final int[] array1 = new int[] { 0, 1, 2, 3 } ;
    final int[] array2 = new int[] { 0, 1, 2, 3 } ;
    Assert.assertTrue( Arrays.equals( array1, array2 ) );
  }
  

// =======
// Fixture
// =======

  private static final Log LOG = LogFactory.getLog( EmbeddedListMangler.class ) ;

  private static class FragmentMapperBuilder {
    private final ImmutableMap.Builder< FragmentIdentifier, int[] >
        pureIdentifierMapBuilder =
            new ImmutableMap.Builder< FragmentIdentifier, int[] >() ;
    private final ImmutableMap.Builder< FragmentIdentifier, int[] >
        derivedIdentifierMapBuilder =
              new ImmutableMap.Builder< FragmentIdentifier, int[] >() ;

    public FragmentMapperBuilder addPure(
        final FragmentIdentifier key,
        final int[] value
    ) {
      pureIdentifierMapBuilder.put( key, value ) ;
      return this ;
    }

    public FragmentMapperBuilder addDerived(
        final FragmentIdentifier key,
        final int[] value
    ) {
      derivedIdentifierMapBuilder.put( key, value ) ;
      return this ;
    }

    public FragmentMapper< int[] > build() {
      final Map< FragmentIdentifier, int[] > pure =
              pureIdentifierMapBuilder.build() ;
      final Map< FragmentIdentifier, int[] > derived =
              derivedIdentifierMapBuilder.build() ;
      return new FragmentMapper< int[] >() {
        public Map< FragmentIdentifier, int[] > getPureIdentifierMap() {
          return pure ;
        }

        public Map< FragmentIdentifier, int[] > getDerivedIdentifierMap() {
          return derived ;
        }
      } ;
    }
 
  }

  private static void verifyEnrich(
      final SyntacticTree expectedTree,
      final SyntacticTree originalTree,
      final FragmentMapper< int[] > fragmentMapper
  ) {
    LOG.info( "Flat tree: %s", TreeFixture.asString( originalTree ) ) ;
    LOG.info( "Expected tree: %s", TreeFixture.asString( expectedTree ) ) ;
    final Treepath< SyntacticTree > expectedTreepath = Treepath.create( expectedTree ) ;
    final Treepath< SyntacticTree > originalTreepath = Treepath.create( originalTree ) ;

    final Treepath< SyntacticTree > rehierarchized = DesignatorInterpreterAccessor.enrich(
        DesignatorInterpreter.MIRRORED_POSTORDER.getFirst( originalTreepath ),
        fragmentMapper
    ) ;

      TreeFixture.assertEqualsNoSeparators(
        expectedTreepath.getTreeAtEnd(),
        rehierarchized.getTreeAtEnd()
    ) ;
  }
  
  private abstract static class DesignatorInterpreterAccessor extends DesignatorInterpreter {

    /**
     * Just make the compiler happy.
     */
    private DesignatorInterpreterAccessor() {
      super( null ) ; 
    }
    
    public static Treepath< SyntacticTree > enrich(
      Treepath< SyntacticTree > treepath,
      final FragmentMapper< int[] > mapper
    ) {
        return DesignatorInterpreter.enrich( treepath, mapper ) ;
      }
    }
  

}

package novelang.treemangling.designator;

import java.util.Map;

import novelang.parser.antlr.TreeFixture;
import static novelang.parser.antlr.TreeFixture.tree;
import novelang.parser.NodeKind;
import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import novelang.system.LogFactory;
import novelang.system.Log;
import novelang.treemangling.designator.BabyMapper2;
import novelang.treemangling.designator.FragmentMapper;
import novelang.treemangling.EmbeddedListMangler;
import novelang.treemangling.DesignatorMapper;
import novelang.marker.FragmentIdentifier;
import org.fest.reflect.core.Reflection;
import org.fest.reflect.reference.TypeRef;
import org.junit.Test;
import com.google.common.collect.ImmutableMap;

/**
 * Tests for
 * {@link novelang.treemangling.designator.EnricherWithDesignator#enrich(Treepath, FragmentMapper)}.
 *
 * @author Laurent Caillette
 */
public class EnricherWithDesignatorTest {

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
        tree( NodeKind.ABSOLUTE_IDENTIFIER, tree( "L0" ) )
    ) ;

    final SyntacticTree partTree = tree( NodeKind.PART, levelTree ) ;

    final Treepath< SyntacticTree > levelTreepath = Treepath.create( partTree, 0 ) ;

    verifyEnrich(
        tree(
            NodeKind.PART,
            tree(
                NodeKind._LEVEL,
                tree( NodeKind.EXPLICIT_IDENTIFIER, "L0" )
            )                
        ),
        partTree,
        new FragmentMapperBuilder().
            addPure( new FragmentIdentifier( "L0" ), levelTreepath ).
            build()
    ) ;
  }




// =======
// Fixture
// =======

  private static final Log LOG = LogFactory.getLog( EmbeddedListMangler.class ) ;

  private static class FragmentMapperBuilder {
    private final ImmutableMap.Builder< FragmentIdentifier, Treepath< SyntacticTree > >
        pureIdentifierMapBuilder =
            new ImmutableMap.Builder< FragmentIdentifier, Treepath< SyntacticTree > >() ;
    private final ImmutableMap.Builder< FragmentIdentifier, Treepath< SyntacticTree > >
        derivedIdentifierMapBuilder =
              new ImmutableMap.Builder< FragmentIdentifier, Treepath< SyntacticTree > >() ;

    public FragmentMapperBuilder addPure(
        final FragmentIdentifier key,
        final Treepath< SyntacticTree > value
    ) {
      pureIdentifierMapBuilder.put( key, value ) ;
      return this ;
    }

    public FragmentMapperBuilder addDerived(
        final FragmentIdentifier key,
        final Treepath< SyntacticTree > value
    ) {
      derivedIdentifierMapBuilder.put( key, value ) ;
      return this ;
    }

    public FragmentMapper build() {
      final Map< FragmentIdentifier, Treepath< SyntacticTree > > pure =
              pureIdentifierMapBuilder.build() ;
      final Map< FragmentIdentifier, Treepath< SyntacticTree > > derived =
              pureIdentifierMapBuilder.build() ;
      return new FragmentMapper() {
        public Map getPureIdentifierMap() {
          return pure ;
        }

        public Map getDerivedIdentifierMap() {
          return derived ;
        }
      } ;
    }
 
  }

  private static void verifyEnrich(
      final SyntacticTree expectedTree,
      final SyntacticTree originalTree,
      final FragmentMapper fragmentMapper
  ) {
    LOG.info( "Flat tree: %s", TreeFixture.asString( originalTree ) ) ;
    LOG.info( "Expected tree: %s", TreeFixture.asString( expectedTree ) ) ;
    final Treepath< SyntacticTree > expectedTreepath = Treepath.create( expectedTree ) ;
    final Treepath< SyntacticTree > originalTreepath = Treepath.create( originalTree ) ;

    final Treepath< SyntacticTree > rehierarchized = EnricherWithDesignator.enrich(
        originalTreepath,
        fragmentMapper
    ) ;

      TreeFixture.assertEqualsNoSeparators(
        expectedTreepath.getTreeAtEnd(),
        rehierarchized.getTreeAtEnd()
    ) ;
  }

}

package org.novelang.treemangling;

import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.novelang.common.SyntacticTree;
import org.novelang.common.tree.RobustPath;
import org.novelang.common.tree.Treepath;
import org.novelang.designator.FragmentIdentifier;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.parser.antlr.TreeFixture;
import org.novelang.treemangling.designator.DesignatorTools;
import org.novelang.treemangling.designator.FragmentMapper;
import org.junit.Assert;
import org.junit.Test;

import static org.novelang.parser.NodeKind.*;
import static org.novelang.parser.antlr.TreeFixture.tree;

/**
 * Tests for
 * {@link DesignatorInterpreter#enrich(Treepath, FragmentMapper)}
 * which modifies identifier stuff in a {@code Treepath}. 
 *
 * @author Laurent Caillette
 */
public class DesignatorInterpreterEnrichmentTest {

  @Test
  public void enrichNothing() {
    verifyEnrich(
        tree( NOVELLA ),
        tree( NOVELLA ),
        new FragmentMapperBuilder().build()
    ) ;
  }


  @Test
  public void enrichWithSimpleAbsoluteIdentifier() {
    final SyntacticTree levelTree = tree(
        _LEVEL,
        tree( ABSOLUTE_IDENTIFIER, tree( "L0" ) )
    ) ;

    final SyntacticTree partTree = tree( NOVELLA, levelTree ) ;

    final Treepath< SyntacticTree > levelTreepath = Treepath.create( partTree, 0 ) ;

    verifyEnrich(
        tree(
            NOVELLA,
            tree(
                _LEVEL,
                tree( _EXPLICIT_IDENTIFIER, "\\\\L0" )
            )                
        ),
        partTree,
        new FragmentMapperBuilder().
            addPure(
                new FragmentIdentifier( "L0" ),
                RobustPath.create( levelTreepath, DesignatorTools.IDENTIFIER_TREE_FILTER )
            ).build()
    ) ;
  }


  /**
   * The {@link DesignatorInterpreter#enrich(Treepath, FragmentMapper)} method adds and removes
   * trees so it introduces an index shift.
   * By calling this method two times we check proper handling of index shift. 
   */
  @Test
  public void enrichTwoTimesToCheckResistanceToIndexShift() {
    final SyntacticTree levelTree0 = tree(
        _LEVEL,
        tree( ABSOLUTE_IDENTIFIER, tree( "L0" ) )
    ) ;

    final SyntacticTree levelTree1 = tree(
        _LEVEL,
        tree( ABSOLUTE_IDENTIFIER, tree( "L1" ) )
    ) ;

    final SyntacticTree partTree = tree( NOVELLA, levelTree0, levelTree1 ) ;

    final RobustPath< SyntacticTree > path0 = RobustPath.create(
        Treepath.create( partTree, 0 ),
        DesignatorTools.IDENTIFIER_TREE_FILTER
    ) ;
    final RobustPath< SyntacticTree > path1 = RobustPath.create(
        Treepath.create( partTree, 1 ),
        DesignatorTools.IDENTIFIER_TREE_FILTER
    ) ;

    verifyEnrich(
        tree(
            NOVELLA,
            tree(
                _LEVEL,
                tree( _EXPLICIT_IDENTIFIER, "\\\\L0" )
            ),
            tree(
                _LEVEL,
                tree( _EXPLICIT_IDENTIFIER, "\\\\L1" )
            )
        ),
        partTree,
        new FragmentMapperBuilder().
            addPure(new FragmentIdentifier( "L0" ), path0).
            addPure(new FragmentIdentifier( "L1" ), path1).
            build()
    ) ;
  }

  @Test
  public void enrichWithSimpleImplicitIdentifier() {
    
    final SyntacticTree levelTree = tree(
        _LEVEL
    ) ;

    final SyntacticTree partTree = tree( NOVELLA, levelTree ) ;

    final Treepath< SyntacticTree > levelTreepath = Treepath.create( partTree, 0 ) ;

    final FragmentMapper< RobustPath< SyntacticTree > > mapper = new FragmentMapperBuilder().
        addDerived(
            new FragmentIdentifier( "L0" ),
            RobustPath.create( levelTreepath, DesignatorTools.IDENTIFIER_TREE_FILTER )
        ).build()
    ;
    
    verifyEnrich(
        tree(
            NOVELLA,
            tree(
                _LEVEL,
                tree( _IMPLICIT_IDENTIFIER, "\\\\L0" )
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

  private static final Logger LOGGER =
      LoggerFactory.getLogger( DesignatorInterpreterEnrichmentTest.class ) ;

  private static class FragmentMapperBuilder {
    private final ImmutableMap.Builder< FragmentIdentifier, RobustPath< SyntacticTree > >
        pureIdentifierMapBuilder =
            new ImmutableMap.Builder< FragmentIdentifier, RobustPath< SyntacticTree > >() ;
    private final ImmutableMap.Builder< FragmentIdentifier, RobustPath< SyntacticTree > >
        derivedIdentifierMapBuilder =
              new ImmutableMap.Builder< FragmentIdentifier, RobustPath< SyntacticTree > >() ;

    public FragmentMapperBuilder addPure(
        final FragmentIdentifier key,
        final RobustPath< SyntacticTree > value
    ) {
      pureIdentifierMapBuilder.put( key, value ) ;
      return this ;
    }

    public FragmentMapperBuilder addDerived(
        final FragmentIdentifier key,
        final RobustPath< SyntacticTree > value
    ) {
      derivedIdentifierMapBuilder.put( key, value ) ;
      return this ;
    }

    public FragmentMapper< RobustPath< SyntacticTree > > build() {
      final Map< FragmentIdentifier, RobustPath< SyntacticTree > > pure =
              pureIdentifierMapBuilder.build() ;
      final Map< FragmentIdentifier, RobustPath< SyntacticTree > > derived =
              derivedIdentifierMapBuilder.build() ;
      return new FragmentMapper< RobustPath< SyntacticTree > >() {
        public Map< FragmentIdentifier, RobustPath< SyntacticTree > > getPureIdentifierMap() {
          return pure ;
        }

        public Map< FragmentIdentifier, RobustPath< SyntacticTree > > getDerivedIdentifierMap() {
          return derived ;
        }
      } ;
    }
 
  }

  private static void verifyEnrich(
      final SyntacticTree expectedTree,
      final SyntacticTree originalTree,
      final FragmentMapper< RobustPath< SyntacticTree > > fragmentMapper
  ) {
    LOGGER.info( "Flat tree: ", TreeFixture.asString( originalTree ) ) ;
    LOGGER.info( "Expected tree: ", TreeFixture.asString( expectedTree ) ) ;
    final Treepath< SyntacticTree > expectedTreepath = Treepath.create( expectedTree ) ;
    final Treepath< SyntacticTree > originalTreepath = Treepath.create( originalTree ) ;

    final Treepath< SyntacticTree > rehierarchized = DesignatorInterpreterAccessor.enrich(
        DesignatorTools.TRAVERSAL.first( originalTreepath ),
//        originalTreepath,
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
      final Treepath< SyntacticTree > treepath,
      final FragmentMapper< RobustPath< SyntacticTree > > mapper
    ) {
        return DesignatorInterpreter.enrich( treepath, mapper ) ;
      }
    }
  

}

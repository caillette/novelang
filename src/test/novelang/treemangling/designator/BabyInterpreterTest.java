package novelang.treemangling.designator;

import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNull;
import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import novelang.common.tree.RobustPath;
import static novelang.parser.antlr.TreeFixture.tree;
import static novelang.parser.NodeKind._LEVEL;
import static novelang.parser.NodeKind.LEVEL_TITLE;
import static novelang.parser.NodeKind.WORD_;
import static novelang.parser.NodeKind.RELATIVE_IDENTIFIER;
import static novelang.parser.NodeKind.ABSOLUTE_IDENTIFIER;
import static novelang.parser.NodeKind.NOVELLA;
import novelang.designator.FragmentIdentifier;

/**
 * Tests for {@link BabyInterpreter}.
 *
 * @author Laurent Caillette
 */
public class BabyInterpreterTest {

  @Test
  public void nothing() {
    final BabyInterpreter interpreter = createInterpreter( tree( NOVELLA ) ) ;
    assertEquals( 0, interpreter.getPureIdentifierMap().keySet().size() );
    assertFalse( "", interpreter.getProblems().iterator().hasNext() ) ;
  }


  /**
   * <pre>
   *  novella
   *     |
   * yLevel \\y
   *     |
   * zLevel \z
   * </pre>
   */
  @Test
  public void twoAbsoluteIdentifiers() {
    final SyntacticTree zLevel = tree(
        _LEVEL,
        tree( RELATIVE_IDENTIFIER, "z" )
    ) ;
    final SyntacticTree yLevel = tree(
        _LEVEL,
        tree( ABSOLUTE_IDENTIFIER, "y" ),
        zLevel
    ) ;
    final FragmentIdentifier yIdentifier = new FragmentIdentifier( "y" ) ;
    final FragmentIdentifier zIdentifier = new FragmentIdentifier( yIdentifier, "z" ) ;

    final SyntacticTree novella = tree(
        NOVELLA,
        yLevel
    ) ;
    final BabyInterpreter interpreter = createInterpreter( novella ) ;

    assertFalse( interpreter.hasProblem() ) ;

    assertEquals( 0, interpreter.getDerivedIdentifierMap().keySet().size() );
    final Map< FragmentIdentifier, RobustPath< SyntacticTree > > pureIdentifiers =
        interpreter.getPureIdentifierMap() ;
    assertEquals( 2, pureIdentifiers.keySet().size() ) ;

    assertSame(
        yLevel,
        makeTree( interpreter.getPureIdentifierMap().get( yIdentifier ), novella )
    ) ;

    assertSame(
        zLevel,
        makeTree( interpreter.getPureIdentifierMap().get( zIdentifier ), novella )
    ) ;



  }

  /**
   * <pre>
   *   novella
   *     |  
   * yLevel \\y
   *     |
   * zLevel "z" 
   * </pre>
   */
  @Test
  public void derivedIdentifierUnderAbsoluteIdentifier() {
    final SyntacticTree zLevel = tree(
        _LEVEL,
        tree( LEVEL_TITLE, tree( WORD_, "z" ) )
    ) ;
    final SyntacticTree yLevel = tree(
        _LEVEL,
        tree( ABSOLUTE_IDENTIFIER, "y" ),
        zLevel
    ) ;
    final FragmentIdentifier yIdentifier = new FragmentIdentifier( "y" ) ;
    final FragmentIdentifier zLikeRelativeIdentifier = new FragmentIdentifier( yIdentifier, "z" ) ;
    final FragmentIdentifier zLikeAbsoluteIdentifier = new FragmentIdentifier( "z" ) ;

    final SyntacticTree novella = tree(
        NOVELLA,
        yLevel
    ) ;
    final BabyInterpreter interpreter = createInterpreter( novella ) ;

    final Map< FragmentIdentifier, RobustPath< SyntacticTree > > pureIdentifiers =
        interpreter.getPureIdentifierMap() ;
    assertEquals( 1, pureIdentifiers.keySet().size() ) ;

    final Map< FragmentIdentifier, RobustPath< SyntacticTree > > derivedIdentifiers =
        interpreter.getDerivedIdentifierMap() ;


    assertFalse( interpreter.hasProblem() ) ;

    assertEquals( 2, derivedIdentifiers.keySet().size() ) ;

    assertSame(
        yLevel,
        makeTree( pureIdentifiers.get( yIdentifier ), novella )
    ) ;

    assertSame(
        zLevel,
        makeTree( derivedIdentifiers.get( zLikeRelativeIdentifier ), novella )
    ) ;

    assertSame(
        zLevel,
        makeTree( derivedIdentifiers.get( zLikeAbsoluteIdentifier ), novella )
    ) ;


  }


  @Test
  public void dontDeriveIdentifierFromDuplicateTitles() {
    final SyntacticTree tree = tree(
        NOVELLA,
        tree( _LEVEL, tree( LEVEL_TITLE, tree( WORD_, "z" ) ) ),
        tree( _LEVEL, tree( LEVEL_TITLE, tree( WORD_, "z" ) ) )
    ) ;

    final BabyInterpreter interpreter = createInterpreter( tree ) ;

    assertFalse( interpreter.hasProblem() ) ;
    assertEquals( 0, interpreter.getDerivedIdentifierMap().keySet().size() );
    assertEquals( 0, interpreter.getPureIdentifierMap().keySet().size() ) ;
  }

  /**
   * <pre>
   *        NOVELLA
   *       /       \
   * zLevel "z"     xLevel \\x
   *               /      \
   *        zLevel "z"    yLevel \y
   *                         |
   *                      zLevel "z"
   * </pre>
   */
  @Test
  public void ignoreDuplicateDerivedIdentifiersUnderExplicitIdentifiers() {
    final SyntacticTree zLevel = tree(
        _LEVEL,
        tree( LEVEL_TITLE, tree( WORD_, "z" ) )
    ) ;
    
    final SyntacticTree yLevel = tree(
        _LEVEL,
        tree( RELATIVE_IDENTIFIER, "y" ),
        zLevel
    ) ;
    
    final SyntacticTree xLevel = tree(
        _LEVEL,
        tree( ABSOLUTE_IDENTIFIER, "x" ),
        yLevel
    ) ;
    
    final SyntacticTree novella = tree(
        _LEVEL,
        zLevel,
        xLevel
    ) ;

    final BabyInterpreter interpreter = createInterpreter( novella ) ;

    assertFalse( interpreter.hasProblem() ) ;
    assertEquals( 2, interpreter.getPureIdentifierMap().keySet().size() ) ;
    assertEquals( 1, interpreter.getDerivedIdentifierMap().keySet().size() ) ;
    assertFalse( interpreter.hasProblem() ) ;
  }

  /**
   * <pre>
   *                     wLevel
   *                   /       \
   *         xLevel "x"         yLevel \\y
   *        /        \             |
   * zLevel "z"    zLevel "z"   zLevel "z"
   * </pre>
   */
  @Test
  public void derivedIdentifierMayOrMayNotWork() {
    final SyntacticTree zLevel = tree(
        _LEVEL,
        tree( LEVEL_TITLE, tree( WORD_, "z" ) )
    ) ;
    final SyntacticTree xLevel = tree(
        _LEVEL,
        tree( LEVEL_TITLE, tree( WORD_, "x" ) ),
        zLevel,
        zLevel
    ) ;
    final SyntacticTree yLevel = tree(
        _LEVEL,
        tree( ABSOLUTE_IDENTIFIER, "y" ),
        zLevel
    ) ;

    final SyntacticTree wLevel = tree(
        _LEVEL,
        xLevel,
        yLevel
    ) ;

    final FragmentIdentifier xIdentifier = new FragmentIdentifier( "x" ) ;
    final FragmentIdentifier yIdentifier = new FragmentIdentifier( "y" ) ;
    final FragmentIdentifier zIdentifier = new FragmentIdentifier( "z" ) ;
    final FragmentIdentifier yzIdentifier = new FragmentIdentifier( yIdentifier, "z" ) ;

    final BabyInterpreter interpreter = createInterpreter( wLevel ) ;
    final Map< FragmentIdentifier, RobustPath< SyntacticTree > > derivedIdentifiers =
        interpreter.getDerivedIdentifierMap() ;
    final Map< FragmentIdentifier, RobustPath< SyntacticTree > > pureIdentifierTreepathMap =
        interpreter.getPureIdentifierMap() ;

    assertFalse( interpreter.hasProblem() ) ;

    assertEquals( 1, pureIdentifierTreepathMap.keySet().size() ) ;
    assertEquals( 2, derivedIdentifiers.keySet().size() ) ;


    assertSame(
        xLevel,
        makeTree( derivedIdentifiers.get( xIdentifier ), wLevel )
    ) ;

    assertSame(
        yLevel,
        makeTree( pureIdentifierTreepathMap.get( yIdentifier ), wLevel )
    ) ;

    assertSame(
        zLevel,
        makeTree( derivedIdentifiers.get( yzIdentifier ), wLevel )
    ) ;

    assertNull( pureIdentifierTreepathMap.get( zIdentifier ) ) ;


  }
    

// =======    
// Fixture
// =======    
    
    
  private BabyInterpreter createInterpreter( final Treepath< SyntacticTree > treepath ) {
    return new BabyInterpreter( treepath ) ;
  }
    
  private BabyInterpreter createInterpreter( final SyntacticTree tree ) {
    return createInterpreter( Treepath.create( tree ) ) ;
  }
    
  private SyntacticTree makeTree(
      final RobustPath< SyntacticTree > mapped,
      final SyntacticTree root
  ) {
    return mapped.apply( root ).getTreeAtEnd() ;
  }


}

package novelang.treemangling.designator;

import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNull;
import novelang.common.SyntacticTree;
import novelang.common.Problem;
import novelang.common.tree.Treepath;
import static novelang.parser.antlr.TreeFixture.tree;
import static novelang.parser.NodeKind._LEVEL;
import static novelang.parser.NodeKind.LEVEL_TITLE;
import static novelang.parser.NodeKind.WORD_;
import static novelang.parser.NodeKind.RELATIVE_IDENTIFIER;
import static novelang.parser.NodeKind.ABSOLUTE_IDENTIFIER;
import static novelang.parser.NodeKind.PART;
import novelang.marker.FragmentIdentifier;
import novelang.treemangling.designator.FragmentMapper;

/**
 * Tests for both {@link novelang.treemangling.DesignatorInterpreter} and 
 * {@link BabyInterpreter}.
 *
 * @param < M > Type of the {@link Mapper} which bears type parametrization as we don't want
 *     to make tested classes generic.
 *
 * @author Laurent Caillette
 */
public abstract class AbstractMapperTest< M > {

  @Test
  public void nothing() {
    final Mapper< M > mapper = createMapper( tree( PART ) ) ;
    assertEquals( 0, mapper.getPureIdentifierMap().keySet().size() );
    assertFalse( "", mapper.getProblems().iterator().hasNext() ) ;
    if( mapper instanceof SyntheticMapper ) {
      final SyntheticMapper< M > syntheticMapper = ( SyntheticMapper< M > ) mapper ;
      assertNull( syntheticMapper.get( new FragmentIdentifier( "whatever" ) ) ) ;
    }
  }


  /**
   * <pre>
   *    part
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

    final SyntacticTree part = tree(
        PART,
        yLevel
    ) ;
    final Mapper< M > mapper = createMapper( part ) ;

    assertFalse( mapper.hasProblem() ) ;

    assertEquals( 0, mapper.getDerivedIdentifierMap().keySet().size() );
    final Map< FragmentIdentifier, M > pureIdentifiers =
        mapper.getPureIdentifierMap() ;
    assertEquals( 2, pureIdentifiers.keySet().size() ) ;

    assertSame(
        yLevel,
        makeTree( mapper.getPureIdentifierMap().get( yIdentifier ), part )
    ) ;

      final M actualZLevel = mapper.getPureIdentifierMap().get( zIdentifier );
      assertSame(
        zLevel,
        makeTree( actualZLevel, part )
    ) ;

    if( mapper instanceof SyntheticMapper ) {
      final SyntheticMapper< M > syntheticMapper = ( SyntheticMapper< M > ) mapper ;
      assertSame( yLevel, makeTree( syntheticMapper.get( yIdentifier ), part ) ) ;
      assertSame( zLevel, makeTree( syntheticMapper.get( zIdentifier ), part ) ) ;
    }


  }

  /**
   * <pre>
   *   PART
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

    final SyntacticTree part = tree(
        PART,
        yLevel
    ) ;
    final Mapper< M > mapper = createMapper( part ) ;

    final Map< FragmentIdentifier, M > pureIdentifiers = mapper.getPureIdentifierMap() ;
    assertEquals( 1, pureIdentifiers.keySet().size() ) ;

    final Map< FragmentIdentifier, M> derivedIdentifiers = mapper.getDerivedIdentifierMap() ;


    assertFalse( mapper.hasProblem() ) ;

    assertEquals( 2, derivedIdentifiers.keySet().size() ) ;

    assertSame(
        yLevel,
        makeTree( pureIdentifiers.get( yIdentifier ), part )
    ) ;

    assertSame(
        zLevel,
        makeTree( derivedIdentifiers.get( zLikeRelativeIdentifier ), part )
    ) ;

    assertSame(
        zLevel,
        makeTree( derivedIdentifiers.get( zLikeAbsoluteIdentifier ), part )
    ) ;

    if( mapper instanceof SyntheticMapper ) {
      final SyntheticMapper< M > syntheticMapper = ( SyntheticMapper< M > ) mapper ;
      assertSame( zLevel, makeTree( syntheticMapper.get( zLikeRelativeIdentifier ), part ) ) ;
      assertSame( zLevel, makeTree( syntheticMapper.get( zLikeAbsoluteIdentifier ), part ) ) ;
    }

  }


  @Test
  public void dontDeriveIdentifierFromDuplicateTitles() {
    final SyntacticTree tree = tree(
        PART,
        tree( _LEVEL, tree( LEVEL_TITLE, tree( WORD_, "z" ) ) ),
        tree( _LEVEL, tree( LEVEL_TITLE, tree( WORD_, "z" ) ) )
    ) ;

    final Mapper< M > mapper = createMapper( tree ) ;

    assertFalse( mapper.hasProblem() ) ;
    assertEquals( 0, mapper.getDerivedIdentifierMap().keySet().size() );
    assertEquals( 0, mapper.getPureIdentifierMap().keySet().size() ) ;
  }

  /**
   * <pre>
   *          PART
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
    
    final SyntacticTree part = tree(
        _LEVEL,
        zLevel,
        xLevel
    ) ;

    final Mapper< M > mapper = createMapper( part ) ;

    assertFalse( mapper.hasProblem() ) ;
    assertEquals( 2, mapper.getPureIdentifierMap().keySet().size() ) ;
    assertEquals( 1, mapper.getDerivedIdentifierMap().keySet().size() ) ;
    assertFalse( mapper.hasProblem() ) ;
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

    final Mapper< M > mapper = createMapper( wLevel ) ;
    final Map< FragmentIdentifier, M > derivedIdentifiers = mapper.getDerivedIdentifierMap() ;
    final Map< FragmentIdentifier, M > pureIdentifierTreepathMap =
        mapper.getPureIdentifierMap() ;

    assertFalse( mapper.hasProblem() ) ;

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

    if( mapper instanceof SyntheticMapper ) {
      final SyntheticMapper< M > syntheticMapper = ( SyntheticMapper< M > ) mapper ;
      assertSame( xLevel, makeTree( syntheticMapper.get( xIdentifier ), wLevel ) ) ;
      assertSame( yLevel, makeTree( syntheticMapper.get( yIdentifier ), wLevel ) ) ;
      assertSame( zLevel, makeTree( syntheticMapper.get( yzIdentifier ), wLevel ) ) ;
      assertNull( syntheticMapper.get( zIdentifier ) ) ;
    }

  }
    

// =======    
// Fixture
// =======    
    
    
  interface Mapper< T > extends FragmentMapper< T > {
    boolean hasProblem() ;
    Iterable< Problem > getProblems() ;
  }
    
  interface SyntheticMapper< T > extends Mapper< T > {
    T get( FragmentIdentifier fragmentIdentifier ) ;
  }
    
  protected abstract Mapper< M > createMapper( final Treepath< SyntacticTree > treepath ) ;
    
  protected final Mapper< M > createMapper( final SyntacticTree tree ) {
    return createMapper( Treepath.create( tree ) ) ;
  }
    
  protected abstract SyntacticTree makeTree( final M mapped, final SyntacticTree root ) ;  


}

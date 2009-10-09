package novelang.treemangling;

import java.util.Map;

import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import novelang.marker.FragmentIdentifier;
import static novelang.parser.NodeKind.ABSOLUTE_IDENTIFIER;
import static novelang.parser.NodeKind.COMPOSITE_IDENTIFIER;
import static novelang.parser.NodeKind.LEVEL_TITLE;
import static novelang.parser.NodeKind.PART;
import static novelang.parser.NodeKind.RELATIVE_IDENTIFIER;
import static novelang.parser.NodeKind._LEVEL;
import static novelang.parser.antlr.TreeFixture.tree;
import novelang.system.Log;
import novelang.system.LogFactory;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import org.junit.Test;

/**
 * Tests for {@link DesignatorMapper}.
 *
 * @author Laurent Caillette
 */
public class DesignatorMapperTest {

  @Test
  public void nothing() {
      final DesignatorMapper designatorMapper = createMarkerMapper( tree(
        PART
      ) ) ;
      assertEquals( 0, designatorMapper.getPureIdentifierMap().keySet().size() );
      Assert.assertFalse( "", designatorMapper.getProblems().iterator().hasNext() ) ;
  }


  @Test
  public void twoAbsoluteIdentifiers() {
    final SyntacticTree zLevel = tree(
        _LEVEL,
        tree( RELATIVE_IDENTIFIER, "z" )
    ) ;
    final SyntacticTree yLevel = tree(
        _LEVEL,
        tree( COMPOSITE_IDENTIFIER, "y" ),
        zLevel
    ) ;
    final FragmentIdentifier yIdentifier = new FragmentIdentifier( "y" ) ;
    final FragmentIdentifier zIdentifier = new FragmentIdentifier( yIdentifier, "z" ) ;

    final DesignatorMapper designatorMapper = createMarkerMapper( tree(
        PART,
        yLevel
    ) ) ;
    assertEquals( 0, designatorMapper.getDerivedIdentifierMap().keySet().size() );
    final Map< FragmentIdentifier, Treepath< SyntacticTree > >
        pureIdentifiers = designatorMapper.getPureIdentifierMap() ;
    assertEquals( 2, pureIdentifiers.keySet().size() ) ;

    assertSame(
        yLevel,
        designatorMapper.getPureIdentifierMap().get( yIdentifier ).getTreeAtEnd()
    ) ;

    assertSame(
        zLevel,
        designatorMapper.getPureIdentifierMap().get( zIdentifier ).getTreeAtEnd()
    ) ;

  }


  @Test
  public void derivedIdentifierUnderAbsoluteIdentifier() {
    final SyntacticTree zLevel = tree(
        _LEVEL,
        tree( LEVEL_TITLE, "z" )
    ) ;
    final SyntacticTree yLevel = tree(
        _LEVEL,
        tree( ABSOLUTE_IDENTIFIER, "y" ),
        zLevel
    ) ;
    final FragmentIdentifier yIdentifier = new FragmentIdentifier( "y" ) ;
    final FragmentIdentifier zRelativeIdentifier = new FragmentIdentifier( yIdentifier, "z" ) ;
    final FragmentIdentifier zAbsoluteIdentifier = new FragmentIdentifier( "z" ) ;

    final DesignatorMapper designatorMapper = createMarkerMapper( tree(
        PART,
        yLevel
    ) ) ;

    final Map< FragmentIdentifier, Treepath< SyntacticTree > >
        pureIdentifiers = designatorMapper.getPureIdentifierMap() ;
    assertEquals( 1, pureIdentifiers.keySet().size() ) ;

    final Map< FragmentIdentifier, Treepath< SyntacticTree > >
        derivedIdentifiers = designatorMapper.getDerivedIdentifierMap() ;
    assertEquals( 2, derivedIdentifiers.keySet().size() ) ;

    assertSame(
        yLevel,
        pureIdentifiers.get( yIdentifier ).getTreeAtEnd()
    ) ;

    assertSame(
        zLevel,
        derivedIdentifiers.get( zRelativeIdentifier ).getTreeAtEnd()
    ) ;

    assertSame(
        zLevel,
        derivedIdentifiers.get( zAbsoluteIdentifier ).getTreeAtEnd()
    ) ;

  }


    @Test
    public void dontDeriveIdentifierFromDuplicateTitles() {
      final SyntacticTree tree = tree(
          PART,
          tree( _LEVEL, tree( LEVEL_TITLE, "y" ) ),
          tree( _LEVEL, tree( LEVEL_TITLE, "z" ) )
      ) ;

      final DesignatorMapper designatorMapper = createMarkerMapper( tree ) ;

      assertEquals( 0, designatorMapper.getDerivedIdentifierMap().keySet().size() );
      assertEquals( 0, designatorMapper.getPureIdentifierMap().keySet().size() ) ;

    }



// =======
// Fixture
// =======

  private static final Log LOG = LogFactory.getLog( EmbeddedListMangler.class ) ;

    private DesignatorMapper createMarkerMapper( final SyntacticTree tree )
    {
      return new DesignatorMapper( Treepath.create( tree ) ) ;
    }


}

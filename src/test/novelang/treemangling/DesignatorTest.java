package novelang.treemangling;

import java.util.Map;

import novelang.common.SyntacticTree;
import novelang.common.Problem;
import novelang.common.tree.Treepath;
import novelang.marker.FragmentIdentifier;

/**
 * Tests for {@link novelang.treemangling.DesignatorMapper}.
 *
 * @author Laurent Caillette
 */
public class DesignatorTest extends AbstractMapperTest< Treepath< SyntacticTree > > {

  protected Mapper< Treepath< SyntacticTree > > createMapper(
      final Treepath< SyntacticTree > treepath
  ) {
     return new MyMapper( new DesignatorMapper( treepath ) ) ;
  }

  protected SyntacticTree makeTree(
      final Treepath< SyntacticTree > mapped,
      final SyntacticTree root
  ) {
    return mapped.getTreeAtEnd() ;
  }

  private class MyMapper implements SyntheticMapper< Treepath< SyntacticTree > > {

    private final DesignatorMapper designatorMapper;

    private MyMapper( final DesignatorMapper designatorMapper ) {
      this.designatorMapper = designatorMapper;
    }

    public Map< FragmentIdentifier, Treepath< SyntacticTree > > getPureIdentifierMap() {
      return designatorMapper.getPureIdentifierMap() ;
    }

    public Map< FragmentIdentifier, Treepath< SyntacticTree > > getDerivedIdentifierMap() {
      return designatorMapper.getDerivedIdentifierMap() ;
    }

    public boolean hasProblem() {
      return designatorMapper.hasProblem() ;
    }

    public Iterable< Problem > getProblems() {
      return designatorMapper.getProblems() ;
    }

    public Treepath<SyntacticTree> get( final FragmentIdentifier fragmentIdentifier ) {
      return designatorMapper.get( fragmentIdentifier ) ;
    }
  }


}
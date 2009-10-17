package novelang.treemangling;

import java.util.Map;

import novelang.treemangling.designator.BabyMapper2;
import novelang.common.SyntacticTree;
import novelang.common.Problem;
import novelang.common.tree.Treepath;
import novelang.marker.FragmentIdentifier;

/**
 * Tests for {@link BabyMapper2}.
 *
 * @author Laurent Caillette
 */
public class BabyMapper2Test extends AbstractMapperTest< int[] > {

  protected Mapper< int[] > createMapper( final Treepath< SyntacticTree > treepath ) {
     return new MyMapper( new BabyMapper2( treepath ) ) ;
  }

  protected SyntacticTree makeTree( final int[] mapped, final SyntacticTree root ) {
    return Treepath.create( root, mapped ).getTreeAtEnd() ;
  }

  private class MyMapper implements Mapper< int[] > {

    private final BabyMapper2 babyMapper2 ;

    private MyMapper( final BabyMapper2 babyMapper2 ) {
      this.babyMapper2 = babyMapper2;
    }

    public Map< FragmentIdentifier, int[] > getPureIdentifierMap() {
      return babyMapper2.getPureIdentifierMap() ;
    }

    public Map< FragmentIdentifier, int[] > getDerivedIdentifierMap() {
      return babyMapper2.getDerivedIdentifierMap() ;
    }

    public boolean hasProblem() {
      return babyMapper2.hasProblem() ;
    }

    public Iterable< Problem > getProblems() {
      return babyMapper2.getProblems() ;
    }
  }


}

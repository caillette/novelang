package novelang.common.tree;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * Index-based reference in a {@link Tree} that only takes care of trees satisfying a given
 * predicate.
 * 
 * @author Laurent Caillette
 */
public class RobustPath< T extends Tree > {
  
  private final int[] indexes ;
  final Predicate< T > treeFilter ;

  
  private RobustPath( final int[] indexes, final Predicate< T > treeFilter ) {
    this.indexes = indexes ;
    this.treeFilter = treeFilter ;
  }
  
  public final Treepath< T > apply( final Treepath< T > treepath ) {
    throw new UnsupportedOperationException( "apply" ) ;
  }
  
  
  public static < T extends Tree > RobustPath< T > create( final Treepath< T > treepath ) {
    return create( treepath, Predicates.< T >alwaysTrue() ) ;
  }

  public static < T extends Tree > RobustPath< T > create( 
      final Treepath< T > treepath, 
      final Predicate< T > filter 
  ) {
    return new RobustPath< T >( treepath.getIndicesInParent(), filter ) ;
  }
}

/*
 * Copyright (C) 2009 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
    for( int i = 0 ; i < treepath.getLength() ; i ++ ) {
      final Treepath intermediate = treepath.getTreepathAtDistanceFromStart( i ) ;
      final Tree tree = intermediate.getTreeAtEnd() ;
    }

    return new RobustPath< T >( treepath.getIndicesInParent(), filter ) ;
  }
}

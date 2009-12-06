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
import com.google.common.base.Preconditions;

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
    this.treeFilter = Preconditions.checkNotNull( treeFilter ) ;

  }


  public final Treepath< T > apply( final T root ) {
    if( indexes == null) {
      return Treepath.create( root ) ;
    }

    Treepath< T > result = Treepath.create( root ) ;
    treepathLoop: for( int treepathIndex = 0 ; treepathIndex < indexes.length ; treepathIndex ++ ) {
      final T tree = result.getTreeAtEnd() ;
      int translatedIndex = -1 ;
      for( int childIndex = 0 ; childIndex < tree.getChildCount() ; childIndex ++ ) {
        if( treeFilter.apply( ( T ) tree.getChildAt( childIndex ) ) ) {
          translatedIndex ++ ;
        }
        if( translatedIndex == indexes[ treepathIndex ] ) {
          result = Treepath.< T >create( result, childIndex ) ;
          continue treepathLoop ;
        }
      }
      throw new IllegalArgumentException(
          "Filtering with " + treeFilter + "doesn't let pass any child on " + result ) ;
    }
    return result ;
  }
  
  
  public static < T extends Tree > RobustPath< T > create( final Treepath< T > treepath ) {
    return create( treepath, Predicates.< T >alwaysTrue() ) ;
  }

  public static < T extends Tree > RobustPath< T > create( 
      final Treepath< T > treepath, 
      final Predicate< T > filter 
  ) {
    if( treepath.getLength() == 1 ) {
      return new RobustPath< T >( null, filter ) ;
    } else {
      final int[] indexes = new int[ treepath.getLength() - 1 ] ;
      for( int treepathIndex = 1 ; treepathIndex <= treepath.getLength() - 1 ; treepathIndex ++ ) {
        final Treepath intermediate = treepath.getTreepathAtDistanceFromStart( treepathIndex ) ;
        final int naturalIndexInPrevious = intermediate.getIndexInPrevious() ;
        int filteredIndexInPrevious = -1 ;
        final T parentTree = ( T ) intermediate.getPrevious().getTreeAtEnd() ;
        for( int childIndex = 0 ; childIndex <= naturalIndexInPrevious ; childIndex ++ ) {
          if( filter.apply( (T) parentTree.getChildAt( childIndex ) ) ) {
            filteredIndexInPrevious ++ ;
          }
        }
        if( filteredIndexInPrevious < 0 ) {
          throw new IllegalArgumentException(
              "Filtering with " + filter + "doesn't let pass any child on " + intermediate ) ;
        }
        indexes[ treepathIndex - 1 ] = filteredIndexInPrevious ;
      }
      return new RobustPath< T >( indexes, filter ) ;
    }
  }

}

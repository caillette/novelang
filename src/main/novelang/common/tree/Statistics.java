/*
 * Copyright (C) 2008 Laurent Caillette
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package novelang.common.tree;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.Maps;
import novelang.system.Log;
import novelang.system.LogFactory;

/**
 * Calculates and prints statistics about a {@link Tree}'s population.
 *
 * This shows that, on human-written documents, 33 % of {@link Tree} objects have 0 child and,
 * among all {@link Tree}s with one child or more, 50 % have a single child.
 *
 * @author Laurent Caillette
 */
public class Statistics {

  private static final Log LOG = LogFactory.getLog( Statistics.class ) ;

  /**
   * Returns a {@code Map} between the number of children and the number of nodes with this
   * number of children for the given {@link Tree}.
   *
   * @param root a non-null object.
   * @return a non-null {@code Map} containing no null with zero-or-positive, contiguous keys.
   *     The underlying implementation is a {@code TreeMap} with sorted keys.
   */
  public static Map< Integer, Integer > calculate( final Tree< ? > root ) {
    final TreeMap< Integer, Integer > treeMap = Maps.newTreeMap() ;
    upgrade( treeMap, 10 ) ;

    final Traversal< Tree > traversal = Traversal.Preorder.create() ;
    Treepath< Tree > current = Treepath.< Tree >create( root ) ;
    while( current != null ) {
      final int childCount = current.getTreeAtEnd().getChildCount() ;
      if( ! treeMap.containsKey( childCount ) ) {
        upgrade( treeMap, childCount ) ;
      }
      final Integer total = treeMap.get( childCount ) ;
      if( total == null ) {
        treeMap.put( childCount, 1 ) ;
      } else {
        treeMap.put( childCount, total + 1 ) ;
      }
      current = traversal.next( current ) ;
    }
    return Collections.unmodifiableMap( treeMap ) ;
  }

  private static void upgrade(
      final TreeMap< Integer, Integer > treeMap,
      final int maximum
  ) {
    int maximumKey = -1 ;
    for( final Integer key : treeMap.keySet() ) {
      if( key != null ) {
        maximumKey = Math.max( maximumKey, key ) ;
      }
    }
    for( int newKey = maximumKey + 1 ; newKey <= maximum ; newKey ++ ) {
      treeMap.put( newKey, 0 ) ;
    }

  }

  private static final int MAX_BAR_LENGTH = 40 ;

  public static void logStatistics( final Tree< ? > tree ) {
    if( LOG.isDebugEnabled() ) {
      final Map< Integer, Integer > map = calculate( tree ) ;
      final StringBuilder stringBuilder = new StringBuilder( "\n" ) ;
      int maximum = 0 ;
      for( final Map.Entry< Integer, Integer > entry : map.entrySet() ) {
        maximum = Math.max( maximum, entry.getValue() ) ;
      }
      int childCount = 0 ;
      for( final Map.Entry< Integer, Integer > entry : map.entrySet() ) {
        final Integer value = entry.getValue() ;
        if( value > 0 ) {
          final Integer population = entry.getKey() ;
          stringBuilder.append( String.format( "  %6d : %6d  ", population, value ) ) ;
          bar( stringBuilder, maximum, value ) ;
          stringBuilder.append( "\n" ) ;
          childCount += population * value ;
        }
      }
      stringBuilder.append( "-----------------\n" ) ;
      stringBuilder.append( String.format( "  Child count: %d \n", childCount ) ) ;
      LOG.debug( stringBuilder.toString() ) ;
    }
  }

  private static void bar( final StringBuilder stringBuilder, final int maximum, final int value ) {
    final int divider = ( maximum / MAX_BAR_LENGTH ) + 1 ; // Avoid division by 0.
    final int correctedBarLength = value / divider + ( value > 0 ? 1 : 0 ) ;

    for( int i = 0 ; i < correctedBarLength ; i ++ ) {
      stringBuilder.append( '+' ) ;
    }
  }
}

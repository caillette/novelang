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
package org.novelang.outfit;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Laurent Caillette
 */
public class CollectionTools {

  private CollectionTools() { }

  public static < T > ImmutableList< T > append(
      final ImmutableList< T > list,
      final T element
  ) {
    return ImmutableList.< T >builder()
        .addAll( list )
        .add( element )
        .build()
    ;
  }

  public static < K, V > ImmutableMap< K, V > append(
      final ImmutableMap< K, V > map,
      final K key,
      final V value
  ) {
    return ImmutableMap.< K, V >builder()
        .putAll( map )
        .put( key, value )
        .build()
    ;
  }

  public static < T > ImmutableSet< T > append(
      final ImmutableSet< T > set,
      final T element
  ) {
    return ImmutableSet.< T >builder()
        .addAll( set )
        .add( element )
        .build()
    ;
  }

  public static < T > ImmutableList< T > removeLast( final ImmutableList< T > list ) {
    checkArgument( ! list.isEmpty() ) ;

    final ImmutableList.Builder< T > remover = ImmutableList.builder() ;
    for( int i = 0 ; i < list.size() - 1 ; i ++ ) {
      remover.add( list.get( i ) ) ;
    }

    return remover.build() ;
  }
}

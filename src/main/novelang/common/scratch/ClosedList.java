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

package novelang.common.scratch;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * An immutable variant of {@link java.util.List}.
 *
 * @author Laurent Caillette
 */
public interface ClosedList< T > extends Iterable {

  Iterator< ? extends T > iterator() ;

  int size() ;

  T get( int index ) ;

  boolean isEmpty() ;


  class Tools {

    public static< T > ClosedList create( T[] array ) {
      return create( Arrays.asList( Preconditions.checkNotNull( array ) ) ) ;
    }
    
    public static< T > ClosedList< T > create( List< T > list ) {

      final List< T > immutableList = ImmutableList.copyOf( Preconditions.checkNotNull( list ) ) ;

      return new ClosedList< T >() {

        public Iterator< ? extends T > iterator() {
          return immutableList.iterator() ;
        }

        public int size() {
          return immutableList.size() ;
        }

        public T get( int index ) {
          return immutableList.get( index ) ;
        }

        public boolean isEmpty() {
          return immutableList.isEmpty() ;
        }
      } ;
    }

  }
}

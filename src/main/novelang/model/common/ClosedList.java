/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.model.common;

import java.util.Iterator;
import java.util.List;
import java.util.Arrays;

import com.google.common.collect.Lists;
import com.google.common.base.Objects;

/**
 * An immutable variant of {@link java.util.List}.
 *
 * @author Laurent Caillette
 */
public interface ClosedList< T > extends Iterable {

  Iterator iterator() ;

  int size() ;

  T get( int index ) ;

  boolean isEmpty() ;


  class Tools {

    public static< T > ClosedList create( T[] array ) {
      return create( Arrays.asList( Objects.nonNull( array ) ) ) ;
    }
    
    public static< T > ClosedList< T > create( List< T > list ) {

      final List< T > immutableList = Lists.immutableList( Objects.nonNull( list ) ) ;

      return new ClosedList< T >() {

        public Iterator iterator() {
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

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
package org.novelang.configuration.fop;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Laurent Caillette
 */
public class Stack< T > {

  private final ImmutableSet< ImmutableList< T > > legalPaths ;
  private final Function< T, String > pathElementToString ;

  public Stack(
      final ImmutableSet< ImmutableList< T > > legalPaths,
      final Function< T, String > pathElementToString
  ) {
    this.legalPaths = checkNotNull( legalPaths ) ;
    this.pathElementToString = checkNotNull( pathElementToString ) ;
  }

  private ImmutableList< T > elements = ImmutableList.of() ;

  public void push( final T element ) throws IllegalPathException {
    final ImmutableList< T > newPath = ImmutableList.< T >builder()
        .addAll( elements )
        .add( element )
        .build()
    ;
    if( legalPaths.contains( newPath ) ) {
      elements = newPath ;
    } else {
      throw new IllegalPathException( newPath, pathElementToString ) ;
    }
  }

  public T pop() {
    if( elements.isEmpty() ) {
      throw new IllegalStateException( "Empty stack" ) ;
    } else {
      final int lastIndex = elements.size() - 1;
      final T top = elements.get( lastIndex ) ;

      // Joy of removing last element from an immutable list.
      final ImmutableList.Builder< T > remover = ImmutableList.builder() ;
      for( int i = 0 ; i < elements.size() - 1 ; i ++ ) {
        remover.add( elements.get( i ) ) ;
      }
      elements = remover.build() ;
      return top ;
    }
  }

  public T top() {
    if( elements.isEmpty() ) {
      throw new IllegalStateException( "Empty stack" ) ;
    } else {
      final int lastIndex = elements.size() - 1;
      return elements.get( lastIndex ) ;
    }
  }

  public boolean isEmpty() {
    return elements.isEmpty() ;
  }

  public ImmutableList< T > allElements() {
    return ImmutableList.copyOf( elements ) ;
  }

  /**
   * @author Laurent Caillette
   */
  public static class IllegalPathException extends Exception  {

    public < T > IllegalPathException(
        final ImmutableList< T > path,
        final Function< T , String> pathElementToString
    ) {
      super(
          "Not a legal path: " +
              Joiner.on( "/" ).join( Iterables.transform( path, pathElementToString ) )
      ) ;
    }
  }
}

/*
 * Copyright (C) 2010 Laurent Caillette
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

import java.util.Iterator;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;

/**
 * Designed to hold {@link Tree}'s children.
 * Requires {@link Tree} API change: 
 * <pre>
public interface Tree< T extends Tree > {
  int getChildCount() ;
  T getChildAt( int index ) ;
  T adopt( Iterable< T > newChildren ) ;
  T updateChildren( Function< Iterable< T >, Iterable< T > > childrenTransformer ) ; 
}
</pre>
 * The aim is to make {@link novelang.treemangling.SeparatorsMangler separators removal} faster
 * without sacrifying API's purity. 
 * 
 * @author Laurent Caillette
 */
public final class ChainedList< T > implements Iterable< T > {
  
  private final ChainedList< T > previous ;
  private final T element ;

// ================  
// Creation methods
// ================  
  
  public ChainedList( final T element ) {
    this( null, element ) ;
  }
  
  public ChainedList( final ChainedList< T > previous, final T element ) {
    this.previous = previous ;
    this.element = Preconditions.checkNotNull( element ) ;
  }
  

// ==========  
// Navigation
// ==========  
  
  public int size() {
    return 1 + ( previous == null ? 0 : previous.size() ) ;
  }
  
  public T get() {
    return element ;
  }
  
  public T get( final int index ) {
    final int size = size() ;
    final int indexFromThis = size - index - 1 ;
    if( indexFromThis < 0 ) {
      throw new IllegalArgumentException( "Index " + index + " with size of " + size ) ;
    }
    return fromEnd( indexFromThis ).get() ;
  }
  
  private ChainedList< T > fromEnd( final int index ) {
    if( index == 0 ) {
      return this ;
    } else {
      return previous.fromEnd( index - 1 ) ;
    }
  }

  public ChainedList< T > getPrevious() {
    return previous ;
  }


// ========  
// Addition
// ========  
  
  public ChainedList< T > append( final T newLastElement ) {
    return new ChainedList< T >( this, newLastElement ) ;
  }

  public ChainedList< T > append( final Iterable< T > elements ) {
    ChainedList< T > last = this ;
    for( final T element : elements ) {
      last = last.append( element ) ;
    }
    return last ;
  }

  private ChainedList< T > append( final ChainedList< T > previous, final List< T > elements ) {
    // How to deal with null 'previous' and empty list?
    throw new UnsupportedOperationException( "append" ) ;
  }


// =======  
// Removal
// =======  
  
  public ChainedList< T > remove( final ChainedList< T > nodeToRemove ) {
    Preconditions.checkNotNull( nodeToRemove ) ;
    final List< T > untilFound = Lists.newLinkedList() ;
    ChainedList< T > current = this ;
    while( this != nodeToRemove ) {
      untilFound.add( get() ) ;
      if( previous == null ) {
        throw new IllegalArgumentException( "Not owned: " + nodeToRemove ) ;
      } else {
        current = previous ;
      }
    }
    current = current.previous ;
    return append( current, untilFound ) ; // TODO check if this always works.
  }

  public ChainedList< T > removeMatchingElements( 
      final Predicate< ChainedList< T > > removalPredicate 
  ) {
    final List< T > remainingElements = Lists.newLinkedList() ;
    ChainedList< T > current = this ;
    while( current != null ) {
      if( ! removalPredicate.apply( current ) ) {
        remainingElements.add( current.get() ) ;
      }
      current = current.getPrevious() ;
    }
    if( remainingElements.isEmpty() ) {
      return this ;
    } else {
      return append( current, remainingElements ) ;
    }
  }
  

// ========  
// Iterable
// ========  
  

  public Iterator< T > iterator() {
    
    return new AbstractIterator< T >() {
      private int invertedIndex = size() ;
      
      @Override
      protected T computeNext() {
        if( -- invertedIndex >= 0 ) {
          return fromEnd( invertedIndex ).get() ;
        } else {
          endOfData() ;
          return null ;
        }
      }
    } ;
  }
}

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
package novelang.system.shell;

import java.util.List;
import java.util.concurrent.Semaphore;

import com.google.common.base.Predicate;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newLinkedList;
import static java.util.Arrays.asList;

/**
 * Evaluates a stack of {@link Predicate}s against lines coming from process standard output.
 * For each matching line, the {@link #semaphore} release one permits.
 * In order to make releases happen in order, the semaphore must be fair.
 * Each matched {@link Predicate} goes out of the stack. When the last {@link Predicate}
 * evaluates to {@code true} the {@link #apply(String)} method returns true.
 * <p>
 * This class is not thread-safe as all calls to {@link #apply(String)} come from
 * the thread watching the output stream.
 *
 * @author Laurent Caillette
 */
/*package*/ class TieredStartupSensor implements Predicate< String > {

  private final Semaphore semaphore ;
  private final int initialPredicateCount ;
  private final List< Predicate< String > > predicates ;

  /**
   * Constructor.
   *
   * @param semaphore a non-null, fair {@code Semaphore}.
   * @param predicates a non-null array containing at least one non-null element. Nulls are ignored.
   */
  public TieredStartupSensor(
      final Semaphore semaphore,
      final Predicate< String >... predicates
  ) {
    checkArgument( semaphore.isFair() ) ;
    checkArgument( predicates.length > 0 ) ;
    this.semaphore = semaphore ;
    this.predicates = newLinkedList( filter( asList( predicates ), notNull() ) ) ;
    this.initialPredicateCount = this.predicates.size() ;
    checkArgument( this.initialPredicateCount > 0, "Got some null in %s" + asList( predicates ) ) ;
  }


  @Override
  public boolean apply( final String line ) {
    if( ! predicates.isEmpty() ) {
      final Predicate< String > top = predicates.get( 0 ) ;
      if( top.apply( line ) ) {
        predicates.remove( 0 ) ;
        semaphore.release( 1 ) ; 
        return true ;
      }
    }
    return false ;
  }

  public int getInitialPredicateCount() {
    return initialPredicateCount ;
  }
}

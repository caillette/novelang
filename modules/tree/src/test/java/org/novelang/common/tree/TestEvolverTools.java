/*
 * Copyright (C) 2011 Laurent Caillette
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

package org.novelang.common.tree;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link EvolverTools}.
 *
 * @author Laurent Caillette
 */
public class TestEvolverTools {


// ========================
// Removal of first element
// ========================

  @Test
  public void removeFirstOf3() {
    final Tree.Evolver< String > evolver = EvolverTools.firstElementRemover() ;
    check( evolver, asList( "A", "B", "C" ), asList( "B", "C" ) ) ;
  }

  @Test
  public void removeFirstOf2() {
    final Tree.Evolver< String > evolver = EvolverTools.firstElementRemover() ;
    check( evolver, asList( "A", "B" ), asList( "B" ) ) ;
  }

  @Test
  public void removeFirstOf1() {
    final Tree.Evolver< String > evolver = EvolverTools.firstElementRemover() ;
    check( evolver, asList( "A" ), EMPTY_STRING_LIST ) ;
  }

  @Test
  public void removeFirstOfNothing() {
    final Tree.Evolver< String > evolver = EvolverTools.firstElementRemover() ;
    check( evolver, EMPTY_STRING_LIST, EMPTY_STRING_LIST ) ;
  }


// =======================
// Removal of last element
// =======================

  @Test
  public void removeLastOf3() {
    final Tree.Evolver< String > evolver = EvolverTools.lastElementRemover() ;
    check( evolver, asList( "A", "B", "C" ), asList( "A", "B" ) ) ;
  }

  @Test
  public void removeLastOf2() {
    final Tree.Evolver< String > evolver = EvolverTools.lastElementRemover() ;
    check( evolver, asList( "A", "B" ), asList( "A" ) ) ;
  }

  @Test
  public void removeLastOf1() {
    final Tree.Evolver< String > evolver = EvolverTools.lastElementRemover() ;
    check( evolver, asList( "A" ), EMPTY_STRING_LIST ) ;
  }

  @Test
  public void removeLastOfNothing() {
    final Tree.Evolver< String > evolver = EvolverTools.lastElementRemover() ;
    check( evolver, EMPTY_STRING_LIST, EMPTY_STRING_LIST ) ;
  }

// ================
// Removal at index
// ================

  @Test
  public void remove1Of3() {
    final Tree.Evolver< String > evolver = EvolverTools.elementAtIndexRemover( 0 ) ;
    check( evolver, asList( "A", "B", "C" ), asList( "B", "C" ) ) ;
  }

  @Test
  public void remove2Of3() {
    final Tree.Evolver< String > evolver = EvolverTools.elementAtIndexRemover( 1 ) ;
    check( evolver, asList( "A", "B", "C" ), asList( "A", "C" ) ) ;
  }

  @Test
  public void remove3Of3() {
    final Tree.Evolver< String > evolver = EvolverTools.elementAtIndexRemover( 2 ) ;
    check( evolver, asList( "A", "B", "C" ), asList( "A", "B" ) ) ;
  }

  @Test
  public void remove1Of2() {
    final Tree.Evolver< String > evolver = EvolverTools.elementAtIndexRemover( 0 ) ;
    check( evolver, asList( "A", "B" ), asList( "B" ) ) ;
  }

  @Test
  public void remove2Of2() {
    final Tree.Evolver< String > evolver = EvolverTools.elementAtIndexRemover( 1 ) ;
    check( evolver, asList( "A", "B" ), asList( "A" ) ) ;
  }

  @Test
  public void remove1Of1() {
    final Tree.Evolver< String > evolver = EvolverTools.elementAtIndexRemover( 0 ) ;
    check( evolver, asList( "A" ), EMPTY_STRING_LIST ) ;
  }

  @Test( expected = IllegalArgumentException.class )
  public void removeIndexOutOfBound() {
    final Tree.Evolver< String > evolver = EvolverTools.elementAtIndexRemover( 10 ) ;
    check( evolver, asList( "A" ), EMPTY_STRING_LIST ) ;
  }

// =======
// Fixture
// =======

  private static final List<String> EMPTY_STRING_LIST = Arrays.asList() ;


  private static void check(
      final Tree.Evolver< String > evolver,
      final List< String > initialList,
      final List<String> expectedResult
  ) {
    final List< String > actualResult = Lists.newArrayList() ;
    apply( evolver,initialList, actualResult ) ;
    assertEquals( expectedResult, actualResult ) ;
  }

  public static<
          T,
          I extends Iterable< ? extends T >,
          C extends Collection< T >
  > void apply(
      final Tree.Evolver< T > evolver,
      final I initialList,
      final C resultCollector
  ) {
    final int listSize = Iterables.size( initialList ) ;

    int position = -1 ;
    {
      final T preprended = evolver.apply( position, null, listSize ) ;
      if( preprended != null ) {
        resultCollector.add( preprended ) ;
      }
      position ++ ;
    }

    for( final T initialElement : initialList ) {
      final T afterEvolving = evolver.apply( position, initialElement, listSize ) ;
      if( afterEvolving!= null ) {
        resultCollector.add( afterEvolving ) ;
      }
      position ++ ;
    }

    if( position != listSize ) {
      throw new IllegalStateException(
          "List size mistmatch, first was " + listSize + ", then recalculated as " + position ) ;
    }

    while( true ) {
      final T appended = evolver.apply( position ++, null, listSize ) ;
      if( appended == null ) {
        break ;
      } else {
        resultCollector.add( appended ) ;
      }
    }


  }


}

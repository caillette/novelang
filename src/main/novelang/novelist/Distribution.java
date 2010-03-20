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
package novelang.novelist;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import novelang.system.Log;
import novelang.system.LogFactory;

/**
 * Given a {@code Map} of objects and their frequency expressed as percentages, this class
 * finds them back with same distribution given a value in a [0..100[ fractional interval.
 *
 * @author Laurent Caillette
 */
public abstract class Distribution< T > {

  private static final Log LOG = LogFactory.getLog( Distribution.class ) ;

  private final Map< T, Float > summedFrequencies ;

  protected Distribution(
      final String requesterNameForLogging,
      final Map< T, Float > summedFrequencies
  ) {
    this.summedFrequencies = sumDistributions( requesterNameForLogging, summedFrequencies ) ;
  }

  public T get( final Bounded.Percentage probability ) {

    T found = null ;
    for( final Map.Entry< T, Float > entry : summedFrequencies.entrySet() ) {
      if( probability.isStrictlySmallerThan( entry.getValue() ) ) {
        found = entry.getKey() ;
        break ;
      }
    }

    if( found == null ) {
      throw new IllegalStateException( "Should not happen: found nothing for " + probability ) ;
    }

    return found ;
  }


  /**
   * Returns an immutable {@code Map} with keys sorted by frequency (descending sort),
   * associated with the sum of their frequency and frequencies of previous keys.
   */
  protected static< T > Map< T, Float > sumDistributions(
      final String requesterNameForLogging,
      final Map< T, Float > individualDistributions
  ) {
    final List< Map.Entry< T, Float > > sortedKeys =
        Lists.newArrayList( individualDistributions.entrySet() ) ;

    final Comparator< Map.Entry< T, Float > > invertedComparatorOnFrequency =
        new Comparator< Map.Entry< T, Float > >() {
          public int compare(
              final Map.Entry< T, Float > entry1,
              final Map.Entry< T, Float > entry2
          ) {
            return entry2.getValue().compareTo( entry1.getValue() ) ;
          }
    } ;

    Collections.sort( sortedKeys,invertedComparatorOnFrequency ) ;
    final Map< T, Float > cumulatedFrequencies = Maps.newLinkedHashMap() ;
    float sum = 0.0f ;

    // First, calculate the complete sum.
    for( final Map.Entry< T, Float > entry : sortedKeys ) {
      final float frequency = entry.getValue() ;
      sum += frequency ;
    }

    // Now start iterating again. This time we boost the frequency of the first element
    // to reach the total of 100. Boosting first element (the one with highest frequency)
    // introduces minimal change.
    final float correction = 99.999999f - sum ;
    LOG.info( "For " + requesterNameForLogging + ", " +
        "sum of raw frequencies: " + sum + "; correction: " + correction ) ;

    sum = correction ;
    for( final Map.Entry< T, Float > entry : sortedKeys ) {
      final float frequency = entry.getValue() ;
      if( frequency != 0.0f ) {
        sum += frequency ;
        cumulatedFrequencies.put( entry.getKey(), sum ) ;
      }
    }

    if( LOG.isDebugEnabled() && false ) {
      for( final Map.Entry< T, Float > entry : cumulatedFrequencies.entrySet() ) {
        LOG.debug( "  " + entry.getKey() + " -> " + entry.getValue() ) ;
      }
    }
    return Collections.unmodifiableMap( cumulatedFrequencies ) ;
  }


}

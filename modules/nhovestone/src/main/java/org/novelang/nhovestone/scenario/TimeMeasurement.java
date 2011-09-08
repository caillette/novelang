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
package org.novelang.nhovestone.scenario;

import com.google.common.base.Preconditions;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
* @author Laurent Caillette
*/
public class TimeMeasurement {
  private final long timeMilliseconds ;

  public TimeMeasurement( final long timeMilliseconds ) {
    Preconditions.checkArgument( timeMilliseconds > 0L ) ;
    this.timeMilliseconds = timeMilliseconds ;
  }

  public long getTimeMilliseconds() {
    return timeMilliseconds ;
  }


  public String asFormattedString() {
    final Period period = new Period( timeMilliseconds, PeriodType.time() ) ;
    return FORMATTER.print( period ) ;
  }

  private static final PeriodFormatter FORMATTER = new PeriodFormatterBuilder()
      .printZeroAlways()
      .appendMinutes()
      .appendSuffix( ":" )
      .appendSeconds()
      .appendSuffix( "." )
      .appendMillis3Digit()
      .toFormatter()
      ;

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + asFormattedString() + "]" ;
  }

  @Override
  public boolean equals( final Object other ) {
    if( this == other ) {
      return true ;
    }
    if( other == null || getClass() != other.getClass() ) {
      return false ;
    }

    final TimeMeasurement that = ( TimeMeasurement ) other ;

    if( timeMilliseconds != that.timeMilliseconds ) {
      return false ;
    }

    return true ;
  }

  @Override
  public int hashCode() {
    return ( int ) ( timeMilliseconds ^ ( timeMilliseconds >>> 32 ) ) ;
  }
}

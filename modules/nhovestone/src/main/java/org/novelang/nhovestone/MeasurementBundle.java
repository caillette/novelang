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
package org.novelang.nhovestone;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * @author Laurent Caillette
 */
public class MeasurementBundle< MEASUREMENT > implements Iterable< MEASUREMENT > {

  private final List< MEASUREMENT > measurementList ;
  private final Termination termination;

  public MeasurementBundle(
      final List< MEASUREMENT > measurementList,
      final Termination termination
  ) {
    this.measurementList = ImmutableList.copyOf( measurementList ) ;
    this.termination = termination;
  }

  public Termination getTermination() {
    return termination ;
  }

  public int getMeasurementCount() {
    return measurementList.size() ;
  }

  /**
   * @param index between 0 and {@link #getMeasurementCount()} - 1.
   * @return a possibly null value, if the {@link org.novelang.nhovestone.driver.HttpDaemonDriver} crashed
   *     at startup and did not record anything.
   */
  public MEASUREMENT getMeasurement( final int index ) {
    if( measurementList.isEmpty() ) {
      return measurementList.get( index );
    } else {
      return null;
    }
  }

  @Override public Iterator< MEASUREMENT > iterator() {
    return measurementList.iterator() ;
  }
}

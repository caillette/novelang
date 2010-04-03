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
package novelang.benchmark.scenario;

import com.google.common.base.Preconditions;
import org.joda.time.Duration;
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


  private static final PeriodFormatter FORMATTER = new PeriodFormatterBuilder().toFormatter() ;

  @Override
  public String toString() {
    final Period period = new Period( timeMilliseconds, PeriodType.time() ) ;
    
    return super.toString();
  }
}

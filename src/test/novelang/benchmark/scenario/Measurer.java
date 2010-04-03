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

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
* @author Laurent Caillette
*/
public interface Measurer< MEASUREMENT > {

  void runDry( URL url ) throws IOException;

  /**
   * Returns the measurement or {@code null} if something went wrong.
   */
  MEASUREMENT run( URL url ) throws IOException;

  /**
   * Returns if a measurement reflects a strain of the called Novelang instance
   * (like response times going on an exponential trend).
   *
   * @param previousMeasurements a non-null object, contains no null.
   * @param lastMeasurement a non-null object.
   */
  boolean detectStrain( List< MEASUREMENT > previousMeasurements, MEASUREMENT lastMeasurement ) ;

}

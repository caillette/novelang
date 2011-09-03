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

import org.junit.Test;

import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;

/**
 * Tests for {@link TimeMeasurement}.
 *
 * @author Laurent Caillette
 */
public class TestTimeMeasurement {


  @Test
  public void format() {
    final TimeMeasurement timeMeasurement = new TimeMeasurement( 12345678L ) ;
    LOGGER.debug( "Formatted: " + timeMeasurement ) ;
  }


// =======
// Fixture
// =======

  private static final Logger LOGGER = LoggerFactory.getLogger( TestTimeMeasurement.class );

}

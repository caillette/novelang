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

package org.novelang.nhovestone.persistence;

import org.novelang.Version;
import org.novelang.VersionFormatException;
import org.novelang.nhovestone.Telemetrics;

/**
 * @author Laurent Caillette
 */
/*package*/ class TelemetricsFixture {

  private static final Version VERSION_0_1_2 ;
  private static final Version VERSION_3_4_5 ;

  public static final Telemetrics EMPTY ;
  public static final Telemetrics V012_1 ;

  static {
    try {
      VERSION_0_1_2 = Version.parse( "0.1.2" );
      VERSION_3_4_5 = Version.parse( "3.4.5" );
    } catch( VersionFormatException e ) {
      throw new RuntimeException( e );
    }
    final Telemetrics.Builder emptyBuilder = Telemetrics.builder() ;
    emptyBuilder.getShotListBuilder( VERSION_0_1_2 ) ;
    emptyBuilder.getShotListBuilder( VERSION_3_4_5 ) ;
    EMPTY = emptyBuilder.build() ;

    final Telemetrics.Builder v012_1Builder = Telemetrics.builder() ;
    v012_1Builder.getShotListBuilder( VERSION_0_1_2 ).add( new Telemetrics.Shot( 11, 22 ) ) ;
    V012_1 = v012_1Builder.build() ;
  }


  private TelemetricsFixture() { }
}

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
package org.novelang.nhovestone.scenario;

import com.google.common.base.Preconditions;
import org.novelang.nhovestone.Termination;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
* @author Laurent Caillette
*/
public interface Measurer< MEASUREMENT > {

  Termination runDry( URL url ) throws IOException;

  /**
   * Returns the measurement or the termination reason if something went wrong.
   *
   * @param previousMeasurements a non-null list containing no null.
   * @param url correct URL to call the daemon.
   * @return a non-null object.
   */
  Result< MEASUREMENT > run( List< MEASUREMENT > previousMeasurements, URL url )
      throws IOException
  ;

  
  final class Result< MEASUREMENT > {
    
    private final MEASUREMENT measurement ;
    private final Termination termination ;

    private Result( final MEASUREMENT measurement ) {
      this.measurement = Preconditions.checkNotNull( measurement ) ;
      this.termination = null ;
    }

    private Result( final Termination termination ) {
      this.measurement = null ;
      this.termination = Preconditions.checkNotNull( termination ) ;
    }

    public boolean hasMeasurement() {
      return measurement != null ;
    }

    public boolean hasTermination() {
      return termination != null ;
    }

    public MEASUREMENT getMeasurement() {
      if( measurement == null ) {
        throw new IllegalStateException( "No measurement" ) ;
      }
      return measurement ;
    }

    public Termination getTermination() {
      if( termination == null ) {
        throw new IllegalStateException( "No termination" ) ;
      }
      return termination ;
    }

    public static< MEASUREMENT > Result< MEASUREMENT > create( final MEASUREMENT measurement ) {
      return new Result< MEASUREMENT >( measurement ) ;
    }

    public static< MEASUREMENT > Result< MEASUREMENT > create( final Termination termination ) {
      return new Result< MEASUREMENT >( termination ) ;
    }

  }

}

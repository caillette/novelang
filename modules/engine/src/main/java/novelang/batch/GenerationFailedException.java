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
package novelang.batch;

import java.io.StringWriter;
import java.util.List;

import novelang.common.Location;
import novelang.common.Problem;

/**
 * @author Laurent Caillette
 */
public class GenerationFailedException extends Exception {

  public GenerationFailedException( final List< Problem > problems ) {
    super( asString( problems ) ) ;
  }

  private static String asString( final List< Problem > problems ) {
    if( problems == null ) {
      return "<list of problems is null>" ;
    }
    else {
      final StringWriter writer = new StringWriter() ;
      boolean first = true ;

      // Get a bit liberal about input but it's bad to screw error reporting. 
      for( final Problem problem : problems ) {
        if( problem != null ) {
          final Location location = problem.getLocation();
          if( first ) {
            first = false ;
          } else {
            writer.write( "\n" ) ;
          }
          if( location != null ) {
            writer.write( location.toHumanReadableForm() ) ;
          }
          writer.write( "  " + problem.getMessage() ) ;
        }
      }
      return writer.toString() ;
    }
  }
}

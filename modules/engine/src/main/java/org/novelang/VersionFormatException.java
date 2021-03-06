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

package org.novelang;

/**
 * Thrown when version {@link Version#parse(String) parsing} fails.
 *  
 * @author Laurent Caillette
 */
public class VersionFormatException extends Exception {

  public VersionFormatException( final String unparseable ) {
    super( "Could not parse: '" + unparseable + "'" ) ;
  }
/*  
  public AsRuntime asRuntime() {
    return new AsRuntime( this ) ;
  }
  
  public class AsRuntime extends RuntimeException {
    public AsRuntime( VersionFormatException e ) {
      super( e ) ;
      setStackTrace( e.getStackTrace() ) ;
    }

    @Override
    public String getMessage() {
      return getCause().getMessage() ;
    }

    @Override
    public VersionFormatException getCause() {
      return ( VersionFormatException ) super.getCause() ;
    }
     
  }
*/  
}

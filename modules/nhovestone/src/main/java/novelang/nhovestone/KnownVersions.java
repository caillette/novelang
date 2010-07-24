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
package novelang.nhovestone;

import novelang.Version;
import novelang.VersionFormatException;

/**
 * @author Laurent Caillette
 */
public class KnownVersions {

  public static final Version VERSION_0_41_0 = parse( "0.41.0" ) ;
  public static final Version VERSION_0_38_1 = parse( "0.38.1" ) ;

  /**
   * First version supporting --content-root option.
   */
  public static final Version VERSION_0_35_0 = parse( "0.35.0" ) ;

  private static Version parse( final String versionAsString ) {
    try {
      return Version.parse( versionAsString ) ;
    } catch( VersionFormatException e ) {
      throw new RuntimeException( e ) ;
    }
  }

}
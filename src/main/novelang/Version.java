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
package novelang;

/**
 * This class holds the version of the whole application.
 * A copy of the source is modified and compiled during the build process.
 * @author Laurent Caillette
 */
public class Version {

  private Version() {
    throw new Error() ;
  }

  /**
   * The {@code @SNAPSHOT@} string is replaced by the official version
   * number.
   */
  private static final String VERSION = "@SNAPSHOT@" ;

  public static boolean isSnapshot() {
    // Don't use one-piece litteral because of replacement performed by build script.
    return ( "@" + "SNAPSHOT" + "@" ).equals( VERSION ) ;
  }

  public static String name() {
    return isSnapshot() ? "snapshot" : VERSION ;
  }
}

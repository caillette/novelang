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
package novelang.common.filefixture;

import java.io.File;

/**
 * @author Laurent Caillette
 */
public class FileFixture {

  private FileFixture() { }

  public static void register( File directory, Class structure ) {

  }

  /**
   * Returns a {@link Directory} object representing all directories and resources inside
   * a registered class representing a hierarchical structure.
   */
  public static Directory getAsDirectory( Class structure ) {
    throw new UnsupportedOperationException( "getAsDirectory" ) ;
  }

  public static Directory directory( String name ) {
    return new Directory( name ) ;
  }

  public static Resource resource( String name ) {
    return new Resource( name ) ;
  }

  public static void copyAllRecursively( File file, Directory directory ) {

  }

  public static void copyScoped( File somewhere, Directory dir, Resource foo ) {

  }

}

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
package novelang.common.scratch.resource;

import java.io.File;

/**
 * @author Laurent Caillette
 */
public class ResourceRoot {

  private final File rootFile ;

  /**
   * Flag global to all dispensed instances. Any access to dispensed instance will fail when
   * this flag is set to {@code false}.
   */
  private boolean available = false ;

  public ResourceRoot( final File rootFile ) {
    this.rootFile = rootFile ;
  }

  /**
   * Returns a fresh instance if this is the first call, or if it detected a change on the
   * filesystem, or if such detection is not available. Otherwise it returns the same instance
   * as for the previous call.
   *
   * @return a non-null object with exposed properties that make it look like an immutable object.
   */
  public ResourceEnumerator getEnumerator() {
    throw new UnsupportedOperationException( "TODO" ) ;
  }

  private ResourceEnumerator createRootEnumerator(
      final String[] directoryNames,
      final String[] fileNames
  ) {
    throw new UnsupportedOperationException( "TODO" ) ;
  }

}

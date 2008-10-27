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
package novelang.common.scratch;

import java.io.File;

import com.google.common.base.Preconditions;

/**
 * @author Laurent Caillette
 */
public class FileCacheValidity implements CacheValidity< FileCacheValidity > {

  private final long timestamp ;
  private final String absolutePath;

  /**
   * Constructor.
   * @param file a non-null object representing a file.
   */
  public FileCacheValidity( File file ) {
    Preconditions.checkNotNull( file ) ;
    Preconditions.checkArgument( file.exists(), "Should exist: %s", file ) ;
    Preconditions.checkArgument( file.isFile(), "Should be a file: %s", file ) ;
    timestamp = file.lastModified() ;
    absolutePath = file.getAbsolutePath();
  }

  public boolean isValid( FileCacheValidity other ) {
    return
        this.absolutePath.equals( other.absolutePath )
     && this.timestamp == other.timestamp
    ;
  }


}

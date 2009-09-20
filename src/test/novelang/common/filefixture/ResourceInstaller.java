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

import com.google.common.base.Preconditions;
import novelang.system.LogFactory;
import novelang.system.Log;

/**
 * @author Laurent Caillette
 */
public class ResourceInstaller extends AbstractResourceInstaller {

  private static final Log LOG = LogFactory.getLog( ResourceInstaller.class ) ;
  private final File targetDirectory ;

  /**
   * Constructor. Creates a {@code Relocator} object with a parent directory used as reference
   * for all other operations.
   *
   * @param targetDirectory a non-null object representing an existing directory.
   */
  public ResourceInstaller( File targetDirectory ) {
    Preconditions.checkArgument( targetDirectory.exists() ) ;
    Preconditions.checkArgument( targetDirectory.isDirectory() ) ;
    this.targetDirectory = targetDirectory;
    LOG.debug(
        "Created " + getClass().getSimpleName() +
        " on directory '" + targetDirectory.getAbsolutePath() + "'"
    ) ;
  }

  public File getTargetDirectory() {
    return targetDirectory ;
  }
}

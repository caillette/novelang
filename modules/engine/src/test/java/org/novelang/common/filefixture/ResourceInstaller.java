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
package org.novelang.common.filefixture;

import java.io.File;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;

/**
 * A concrete class that holds an immutable reference to the target directory.
 * @author Laurent Caillette
 */
public class ResourceInstaller extends AbstractResourceInstaller {

  private static final Logger LOGGER = LoggerFactory.getLogger( ResourceInstaller.class );

  private final Supplier< File > targetDirectorySupplier;


  /**
   * Lazy constructor that doesn't check for {@code targetDirectorySupplier}'s
   * directory existence because when instantiating with a
   * {@link org.novelang.testing.junit.MethodSupport}, the directory doesn't exist until
   * the test starts evaluating.
   *
   * @param targetDirectorySupplier a non-null object.
   */
  public ResourceInstaller( final Supplier< File > targetDirectorySupplier ) {
    this.targetDirectorySupplier = Preconditions.checkNotNull( targetDirectorySupplier ) ;
    LOGGER.debug(
        "Created ", getClass().getSimpleName(),
        " with ", targetDirectorySupplier, "."
    ) ;
  }

  /**
   * Constructor.
   *
   * @param targetDirectory a non-null object representing an existing directory.
   */
  public ResourceInstaller( final File targetDirectory ) {
    this( new Supplier< File >() {
      @Override
      public File get() {
        return targetDirectory ;
      }

      @Override
      public String toString() {
        return "Supplier[" + targetDirectory + "]";
      }
    } ) ;
  }

  @Override
  public File getTargetDirectory() {
    return targetDirectorySupplier.get() ;
  }
}

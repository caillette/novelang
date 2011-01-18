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
package org.novelang.outfit;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;

/**
 * @author Laurent Caillette
 */
public class TemporaryFileTools {

  private static final Logger LOGGER = LoggerFactory.getLogger( TemporaryFileTools.class ) ;

  private TemporaryFileTools() { }

  /**
   * Creates the directory if it doesn't exist, or deletes all its content if it already
   * exists, and sets the {@value #JAVA_TEMPORARY_DIR_KEY} system property.
   *
   * @param directory a possibly null object.
   * @return the effective file.
   */
  public static File setupTemporaryDirectory( final File directory ) throws IOException {
    final File actualDirectory ;
    if( directory == null ) {
      actualDirectory = new File( SystemUtils.USER_DIR, DEFAULT_DIRECTORY_NAME ) ;
    } else {
      actualDirectory = directory.getCanonicalFile() ;
    }
    if( FileUtils.deleteQuietly( actualDirectory ) ) {
      LOGGER.info( "Deleted '", actualDirectory.getAbsolutePath(), "'." ) ;
    }
    if( actualDirectory.mkdirs() ) {
      LOGGER.info( "Created '", actualDirectory.getAbsolutePath(), "'." ) ;
    }
    System.setProperty( JAVA_TEMPORARY_DIR_KEY, actualDirectory.getAbsolutePath() ) ;
    LOGGER.info( "System property [", JAVA_TEMPORARY_DIR_KEY, "] ",
        "set to '", actualDirectory.getAbsolutePath(), "'." ) ;
    return actualDirectory ;
  }

  private static final String DEFAULT_DIRECTORY_NAME = "$temporary$" ;
  private static final String JAVA_TEMPORARY_DIR_KEY = "java.io.tmpdir" ;

  /**
   * Default implementation that relies on {@value #JAVA_TEMPORARY_DIR_KEY}.
   */
  public static final TemporaryFileService TEMPORARY_FILE_SERVICE = new TemporaryFileService() {
    @Override
    public File createFile( final String prefix, final String suffix ) throws IOException {
      return File.createTempFile( prefix, suffix ) ;
    }

    @Override
    public File createDirectory( final String radix ) {
      throw new UnsupportedOperationException( "TODO" ) ;
    }

    @Override
    public FileSupplier createFileSupplier( final String prefix, final String suffix ) {
      return new FileSupplier() {
        @Override
        public File get() throws IOException {
          return createFile( prefix, suffix ) ;
        }
      };
    }
  } ;


}

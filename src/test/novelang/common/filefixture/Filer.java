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
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;

/**
 * @author Laurent Caillette
 */
public class Filer {

  private static final Logger LOGGER = LoggerFactory.getLogger( Filer.class ) ;


  public static final int TIMEOUT_SECONDS = 5 ;

  private final File physicalTargetDirectory ;

  public Filer( File physicalTargetDirectory ) {
    Preconditions.checkArgument( physicalTargetDirectory.exists() ) ;
    Preconditions.checkArgument( physicalTargetDirectory.isDirectory() ) ;
    this.physicalTargetDirectory = physicalTargetDirectory ;
  }

  /**
   * Copies the given {@code directory} into the {@code physicalTargetDirectory}.
   * @param directory a non-null object.
   */
  public void copy( Directory directory ) {
    final File target = createPhysicalDirectory( physicalTargetDirectory, directory.getName() ) ;
    copyTo( directory, target ) ;
  }

  public void copyContent( Directory directory ) {
    copyTo( directory, physicalTargetDirectory ) ;
  }

  /**
   * Copies the content of given {@code directory} into the {@code physicalTargetDirectory}.
   * @param directory a non-null object.
   * @param physicalTargetDirectory a non-null object representing an existing directory.
   */
  private static void copyTo( Directory directory, File physicalTargetDirectory ) {

    for( Directory subdirectory : directory.getSubdirectories() ) {
      final File physicalDirectory =
          createPhysicalDirectory( physicalTargetDirectory, subdirectory.getName() ) ;
      copyTo( subdirectory, physicalDirectory ) ;
    }
    for( Resource resource : directory.getResources() ) {
      final File physicalFile = new File( physicalTargetDirectory, resource.getName() ) ;
      try {
        final FileOutputStream fileOutputStream = new FileOutputStream( physicalFile ) ;
        IOUtils.copy( resource.getInputStream(), fileOutputStream ) ;
        fileOutputStream.flush() ;
        fileOutputStream.close() ;
      } catch( IOException e ) {
        throw new RuntimeException( "Should not happen", e ) ;
      }
    }
  }

  private static File createPhysicalDirectory( File physicalParentDirectory, String name ) {
    final File physicalDirectory = new File( physicalParentDirectory, name ) ;
    if( ! physicalDirectory.mkdir() ) {
      if( ! FileUtils.waitFor( physicalDirectory, TIMEOUT_SECONDS ) ) {
        throw new RuntimeException(
            "Failed to create file " + physicalDirectory.getAbsolutePath() ) ;
      }
    }
    return physicalDirectory ;
  }

  public void copyScoped( Directory scope, Resource resource ) {
    throw new UnsupportedOperationException( "copyScoped" ) ;
  }
}
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

  /**
   * Copies the content of the given {@code directory} into the {@code physicalTargetDirectory}.
   * @param directory a non-null object.
   */
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

  /**
   * Copies given node to some target directory, retaining directory hierarchy above, up to the
   * {@param scope} directory (included).
   * <pre>
copyScoped( ResourceTree.D0_1, ResourceTree.D0_1_0_0 )

tree                                 somewhere
 +-- d0                               +-- d0.1
      +-- d0.0                             +-- d0.1.0
           +-- r0.0.0.txt                       +-- r0.1.0.0.txt
scope >    +-- d0.1              -->
                +-- r0.1.0.txt
target >        +-- d0.1.0
                     +-- r0.1.0.0.txt
     </pre>
   * @param scope
   * @param node
   * @return
   */
  public File copyScoped( Directory scope, SchemaNode node ) {


    throw new UnsupportedOperationException( "copyScoped" ) ;
  }


}
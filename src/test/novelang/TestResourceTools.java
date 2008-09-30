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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.MissingResourceException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Assert;
import novelang.loader.ResourceName;

/**
 * Utility class for dealing with test-dedicated resources.
 * Some tests require files on the filesystem, while they are resources that are
 * only accessible from a classloader. So we have boring stuff with URLs, byte arrays
 * and so on. 
 *
 * @author Laurent Caillette
 */
public final class TestResourceTools {

  private static final Logger LOG = LoggerFactory.getLogger( TestResourceTools.class ) ;

  private TestResourceTools() { }

  public static URL getResourceUrl( Class owningClass, String resourceName ) {
    final String fullName ;
    if( resourceName.startsWith( "/" ) ) {
      fullName = resourceName ;
    } else {
      fullName =
              "/" +
              ClassUtils. getPackageName( owningClass ). replace( '.', '/' ) +
              "/" +
              resourceName
          ;
    }
    return owningClass.getResource( fullName ) ;
  }

  public static byte[] readResource( Class owningClass, String resourceName ) {

    final URL url = getResourceUrl( owningClass, resourceName ) ;

    if( null == url ) {
      throw new MissingResourceException(
          owningClass.toString() + " key '" + resourceName + "'",
          owningClass.toString(),
          resourceName
      ) ;
    }

    LOG.info( "Loading resource '" + url.toExternalForm() + "' " +
        "from " + owningClass ) ;
    final InputStream inputStream;
    try {
      inputStream = url.openStream();
    } catch( IOException e ) {
      throw new RuntimeException( "Should not happpen", e );
    }
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream() ;
    try {
      IOUtils.copy( inputStream, outputStream ) ;
    } catch( IOException e ) {
      throw new RuntimeException( "Should not happpen", e );
    }
    return outputStream.toByteArray() ;

  }

  /**
   * Reads a resource known to fit in a <code>String</code>.
   * @param owningClass the class accessing to the resource.
   * @param resourceName the resource name as defined from owningClass.
   * @param charset  a non-null charset, no default should be used but the known
   *     encoding on the development platform the resource was created with.
   * @return a non-null, possibly empty <code>String</code>.
   * @throws Error in case of encoding problem which should not happen because of {@code charset}
   *     parameter which should represent a supported encoding.
   */
  public static String readStringResource(
      Class owningClass,
      String resourceName,
      Charset charset
  ) {
    final byte[] bytes = readResource( owningClass, resourceName ) ;
    try {
      return new String( bytes, charset.name() ) ;
    } catch( UnsupportedEncodingException e ) {
      throw new Error( e );
    }
  }

  /**
   * Copy a resource into given directory, creating subdirectories if resource name includes
   * a directory.
   */
  public static File copyResourceToDirectory(
      Class owningClass,
      ResourceName resourceName,
      File destinationDir
  ) {
    return copyResourceToDirectory( owningClass, "/" + resourceName.getName(), destinationDir ) ;
  }


  /**
   * Copy a resource into given directory, creating subdirectories if resource name includes
   * a directory.
   */
  public static File copyResourceToDirectory(
      Class owningClass,
      String resourceName,
      File destinationDir
  ) {
    final byte[] resourceBytes = readResource( owningClass, resourceName ) ;
    final ByteArrayInputStream inputStream =
        new ByteArrayInputStream( resourceBytes );

    final File expandedDestinationDir =
        new File( destinationDir, FilenameUtils.getPath( resourceName ) ) ;

    final File destinationFile = new File( destinationDir, resourceName ) ;
    final FileOutputStream fileOutputStream ;
    try {
      expandedDestinationDir.mkdirs() ;
      fileOutputStream = new FileOutputStream( destinationFile ) ;
    } catch( FileNotFoundException e ) {
      throw new RuntimeException( e );
    }
    try {
      IOUtils.copy( inputStream, fileOutputStream ) ;
    } catch( IOException e ) {
      throw new RuntimeException( e );
    }
    return destinationFile ;
  }

  /**
   * Copy a resource into given directory, creating no directory above target file.
   */
  public static File copyResourceToDirectoryFlat(
      Class owningClass,
      String resourceName,
      File destinationDir
  ) {
    final byte[] resourceBytes = readResource( owningClass, resourceName ) ;
    final ByteArrayInputStream inputStream =
        new ByteArrayInputStream( resourceBytes );

    final File destinationFile = new File( destinationDir, FilenameUtils.getName( resourceName ) ) ;
    final FileOutputStream fileOutputStream ;
    try {
      fileOutputStream = new FileOutputStream( destinationFile ) ;
    } catch( FileNotFoundException e ) {
      throw new RuntimeException( e );
    }
    try {
      IOUtils.copy( inputStream, fileOutputStream ) ;
    } catch( IOException e ) {
      throw new RuntimeException( e );
    }
    return destinationFile ;
  }

  public static File createDirectory( File parent, String name ) {
    final File directory = new File( parent, name ) ;
    if( ! directory.exists() ) {
      directory.mkdirs() ;
    }
    org.junit.Assert.assertTrue(
        "Could not create: '" + directory.getAbsolutePath() + "'",
        FileUtils.waitFor( directory, 1 )
    ) ;
    return directory ;
  }

  public static File getDirectoryForSure( File parent, String name ) {
    final File directory = new File( parent, name ) ;
    Assert.assertTrue(
        "Does not exist: '" + directory.getAbsolutePath() + "'",
        directory.exists()
    ) ;
    Assert.assertTrue(
        "Not a directory: '" + directory.getAbsolutePath() + "'",
        directory.isDirectory() 
    ); ;
    return directory ;
  }
}

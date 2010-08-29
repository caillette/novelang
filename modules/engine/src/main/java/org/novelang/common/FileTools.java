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

package org.novelang.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.SystemUtils;

/**
 * Utility class for doing things with files.
 *
 * @author Laurent Caillette
 */
public class FileTools {

  private static final Logger LOGGER = LoggerFactory.getLogger( FileTools.class );

  private FileTools() { }

  public static final Comparator< ? super File >
  ABSOLUTEPATH_COMPARATOR = new Comparator< File >() {
    public int compare( final File file1, final File file2 ) {
      return file1.getAbsolutePath().compareTo( file2.getAbsolutePath() ) ;
    }
  } ;


  
// ============
// File loading
// ============

  /**
   * Returns the first file in given directory, with one of given extensions.
   * Match is done on according to the order of given extensions.
   *
   * @param basedir
   * @param fileNameNoExtension
   * @param fileExtensions
   * @return a non-null object.
   * @throws java.io.FileNotFoundException with extensive message listing all names of files
   *     that were looked for.
   */
  public static File load( 
      final File basedir,
      final String fileNameNoExtension,
      final String... fileExtensions
  ) throws FileNotFoundException {
    final StringBuffer buffer = new StringBuffer( "Not found:" ) ;
    for( final String extension : fileExtensions ) {
      final File file = new File( basedir, fileNameNoExtension + "." + extension ) ;
      if( file.exists() ) {
        return file ;
      } else {
        buffer.append( "\n    '" ) ;
        buffer.append( file.getAbsolutePath() ) ;
        buffer.append( "'" ) ;
      }
    }
    throw new FileNotFoundException( buffer.toString() ) ;
  }


// =============
// File scanning
// =============

  private static final IOFileFilter VISIBLE_DIRECTORY_FILTER = new IOFileFilter() {
    public boolean accept( final File file ) {
      return file.isDirectory() && ! file.isHidden() ;
    }
    public boolean accept( final File dir, final String name ) {
      return ! dir.isHidden() /*&& ! name.startsWith( "." )*/ ;
    }
  } ;

  private static class MyDirectoryWalker extends DirectoryWalker {

    public MyDirectoryWalker() {
      super(
          VISIBLE_DIRECTORY_FILTER,
          -1
      );
    }

    protected boolean handleDirectory( final File file, final int i, final Collection collection )
        throws IOException
    {
      collection.add( file ) ;
      return true ;
    }

    public void walk( final File root, final List< File > results ) throws IOException {
      super.walk( root, results ) ;
    }
  }


// ====================
// Relative directories
// ====================

  /**
   * Return files with given extensions in given directory.
   *
   * @param directory a non-null object.
   * @param extensions a non-null array containing no nulls.
   * @return a non-null object iterating on non-null objects.
   */
  public static List< File > scanFiles(
      final File directory,
      final String[] extensions,
      final boolean recurse
  ) {
    final Collection fileCollection = FileUtils.listFiles(
        directory,
        extensions,
        recurse
    ) ;

    // Workaround: Commons Collection doesn't know about Generics.
    final List< File > files = Lists.newArrayList() ;
    for( final Object o : fileCollection ) {
      files.add( ( File ) o ) ;
    }
    return files ;
  }


  /**
   * Returns a list of visible directories under a root directory.
   * The root directory is included in the list.
   *
   * @param root a non-null object representing a directory.
   * @return a non-null object containing no nulls.
   */
  public static List< File > scanDirectories( final File root ) {
    final List< File > directories = Lists.newArrayList() ;
    try {
      new MyDirectoryWalker().walk( root, directories ) ;
    } catch( IOException e ) {
      throw new RuntimeException( e ) ;
    }
    return directories ;

  }

  private static final Pattern PATTERN = Pattern.compile( "\\\\" ) ;

  /**
   * Returns a URL-friendly path where file separator is a solidus, not a reverse solidus.
   * Useful on Windows.
   */
  public static String urlifyPath( final String path ) {
    return PATTERN.matcher( path ).replaceAll( "/" );
  }


  /**
   * For a {@code File} object, returns its path relative to a given directory.
   * <p>
   * Given this code:
   * <pre>
final File parent = ...
final File child = new File( parent, "some relative path" ) ;
final File relative = new File( parent, relativizePath( parent, child ) ) ;
   * </pre>
   * We should have {@code child} and {@code relative} referencing the same file.
   *
   * @param parent a non-null object representing a directory.
   * @param child a non-null {@code File} object that must be a child of {@code base}.
   * @return a non-null, non-empty {@code String} representing the name of {@code child}
   *     relative to {@code parent}.
   *     separator.
   * @throws IllegalArgumentException
   */
  public static String relativizePath( 
      final File parent, 
      final File child 
  ) throws IllegalArgumentException {

    final String baseAbsolutePath = parent.getAbsolutePath() ;
    if( ! parent.isDirectory() ) {
      throw new IllegalArgumentException( "Not a directory: " + baseAbsolutePath ) ;
    }
    final String baseAbsolutePathFixed = normalizePath( baseAbsolutePath ) ;
    final String childAbsolutePath = child.isDirectory() ?
        normalizePath( child.getAbsolutePath() ) : child.getAbsolutePath() ;

    if( childAbsolutePath.startsWith( baseAbsolutePathFixed ) ) {
      final String relativePath = childAbsolutePath.substring( baseAbsolutePathFixed.length() ) ;
      if( relativePath.startsWith( SystemUtils.FILE_SEPARATOR ) ) {
        return relativePath.substring( 1 ) ;
      } else {
        return relativePath ;
      }
    } else {
      throw new IllegalArgumentException(
          "No parent-child relationship: '" +  baseAbsolutePathFixed + "' " +
          "not parent of '" + childAbsolutePath + "'"
      ) ;
    }
  }

  private static String normalizePath( final String path ) {
    return path.endsWith( SystemUtils.FILE_SEPARATOR ) ?
        path :
        path + SystemUtils.FILE_SEPARATOR
    ;
  }

  public static boolean isParentOf( final File maybeParent, final File maybeChild ) {
    final String maybeParentPathName = normalizePath( maybeParent.getAbsolutePath() ) ;
    final String maybeChildPathName = normalizePath( maybeChild.getAbsolutePath() ) ;
    return
        ( maybeParentPathName.length() < maybeChildPathName.length() ) &&
        maybeChildPathName.startsWith( maybeParentPathName )
    ;
  }

  public static boolean isParentOfOrSameAs( final File maybeParent, final File maybeChild ) {
    final String maybeParentPathName = normalizePath( maybeParent.getAbsolutePath() ) ;
    final String maybeChildPathName = normalizePath( maybeChild.getAbsolutePath() ) ;
    return
        ( maybeParentPathName.length() <= maybeChildPathName.length() ) &&
        maybeChildPathName.startsWith( maybeParentPathName )
    ;
  }


// ===================
// Temporary directory
// ===================

  private static final List< File > DIRECTORIES_TO_CLEAN_ON_EXIT =
      Collections.synchronizedList( new ArrayList< File >() ) ;

  private static final Thread DIRECTORIES_CLEANER = new Thread(
      new Runnable() {
        public void run() {
          LOGGER.debug( "Cleaning up directories scheduled for deletion..." );

          // Defensive copy even if no directory should be added at the time this runs.
          final List< File > files = Lists.newArrayList( DIRECTORIES_TO_CLEAN_ON_EXIT ) ;
          for( final File file : files ) {
            try {
              LOGGER.info( "Deleting temporary directory '", file.getAbsolutePath(), "'" ) ;
              FileUtils.deleteDirectory( file ) ;
            } catch( IOException e ) {
              LOGGER.error( e, "Failed to clean directory" ) ;
            }
          }
        }
      },
      "Cleaner of temporary directories"

  ) ;

  static {
    Runtime.getRuntime().addShutdownHook( DIRECTORIES_CLEANER ) ;
  }

  private static final String TEMPORARY_DIRECTORY_SUFFIX = ".temp" ;
  

  public static File createTemporaryDirectory(
      final String prefix,
      final File parent,
      final boolean deleteOnExit
  )
      throws IOException
  {
    File temporaryDirectory = null ;

    temporaryDirectory = File.createTempFile( prefix, TEMPORARY_DIRECTORY_SUFFIX, parent ) ;
    if ( ! temporaryDirectory.delete() ) {
      throw new IOException(
          "Created temporary file to get a name from, its deletion failed for an unknown reason") ;
    }
    if ( ! temporaryDirectory.mkdir() ) {
      throw new IOException( "Creation of temporary directory failed for an unknown reason" ) ;
    }
    if( deleteOnExit && null != temporaryDirectory ) {
      DIRECTORIES_TO_CLEAN_ON_EXIT.add( temporaryDirectory ) ;
      LOGGER.info( "Scheduled for deletion on exit: '",
          temporaryDirectory.getAbsolutePath(), "'" ) ;
    }

    return temporaryDirectory ;
  }


// ==================
// Directory creation
// ==================

  public static File createDirectoryForSure( final File directory ) {
    if( ! directory.exists() ) {
      if( directory.mkdirs() ) {
        LOGGER.debug( "Created directory '", directory.getAbsolutePath(), "'" ) ;
      }
    }
    return directory ;
  }

  public static File createFreshDirectory( final File parentDirectory, final String name )
      throws IOException
  {
    return createFreshDirectory( new File( parentDirectory, name ) ) ; 
  }

  public static File createFreshDirectory( final String fileName ) throws IOException {
    return createFreshDirectory( new File( fileName ) ) ;
  }
  
  public static File createFreshDirectory( final File directory ) throws IOException {
    FileUtils.deleteDirectory( directory ) ;
    createDirectoryForSure( directory ) ;
    return directory ;
  }


// =====
// Names
// =====

  private static final Pattern SANITIZATION_PATTERN = Pattern.compile( "[^0-9a-zA-Z]" ) ;
  
  public static String sanitizeFileName( final String name ) {
    return SANITIZATION_PATTERN.matcher( name ).replaceAll( "" );
  }


}

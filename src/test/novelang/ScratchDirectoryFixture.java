/*
 * Copyright (C) 2006 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang ;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.StandardToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates directories on-demand for test purposes.
 * Each test is supposed to instantiate this class in the <code>setUp()</code> method because
 * test name (method name) is not available at the time Test constructor is called.
 *
 * @author Laurent Caillette
 */
public class ScratchDirectoryFixture {

  private static final Logger LOGGER = LoggerFactory.getLogger( ScratchDirectoryFixture.class ) ;

  private final String testIdentifier ;

  private final Set< String > registeredTestIdentifiers = new HashSet< String >() ;
  private static final int FILE_EXISTENCE_TIMEOUT_SECONDS = 5 ;

  public ScratchDirectoryFixture( String testIdentifier ) throws IOException {
    this.testIdentifier = testIdentifier ;
    if( registeredTestIdentifiers.contains( testIdentifier ) ) {
      throw new IllegalArgumentException( "Already created for: " + testIdentifier ) ;
    }
    registeredTestIdentifiers.add( testIdentifier ) ;
    LOGGER.debug( "Created " + this ) ;

  }

  public String toString() {
    final StandardToStringStyle style = new StandardToStringStyle() ;
    style.setUseShortClassName( true ) ;

    return new ToStringBuilder( this, style )
        .append( testIdentifier )
        .toString()
    ;
  }


  private static final String ALL_TEST_DIRECTORIES = "test-files" ;

  private static File allFixturesDirectory ;

  private File getAllFixturesDirectory() throws IOException {
    File file = allFixturesDirectory ;
    if( null == file ) {
      file = new File( ALL_TEST_DIRECTORIES ) ;
      if( file.exists() ) {
        FileUtils.deleteDirectory( file ) ;
      } else {
        file.mkdir() ;
      }
      LOGGER.info( "Created '" + file.getAbsolutePath() + "' as clean directory for all fixtures." ) ;
    }
    allFixturesDirectory = file;
    return allFixturesDirectory ;
  }

  private File testScratchDirectory;

  private File getTestScratchDirectory() throws IOException {
    if( null == testScratchDirectory ) {
      testScratchDirectory = new File( getAllFixturesDirectory(), testIdentifier ) ;
      if( testScratchDirectory.exists() ) {
        FileUtils.deleteDirectory( testScratchDirectory ) ;
      }
      testScratchDirectory.mkdir() ;
    }
    return testScratchDirectory;
  }

  private File createIfNotExists( File file, String name ) throws IOException {
    if( null == file ) {
      file = new File( getTestScratchDirectory(), name ) ;
      FileUtils.forceMkdir( file ) ;
      FileUtils.waitFor( file, FILE_EXISTENCE_TIMEOUT_SECONDS ) ;
      LOGGER.info( "Created '" + file.getAbsolutePath() + "' directory." ) ;
    }
    return file ;
  }

// ================
// Specialized dirs
// ================

  private static final String BOOK = "book-1" ;

  private File book1Root;

  public File getBook1Directory() throws IOException {
    book1Root = createIfNotExists( book1Root, BOOK ) ;
    return book1Root;
  }


}

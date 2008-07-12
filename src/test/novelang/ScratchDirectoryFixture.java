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

package novelang ;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.StandardToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;
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

  public static final String SCRATCH_DIRECTORY_SYSTEM_PROPERTY_NAME =
      "novelang.test.scratch.dir" ;
  public static final String DELETE_SCRATCH_DIRECTORY_SYSTEM_PROPERTY_NAME =
      "novelang.test.scratch.delete" ;

  private static final String SCRATCH_DIR_NAME = "test-scratch" ;

  /**
   * Static field holding the directory once defined.
   */
  private static File allFixturesDirectory ;

  private File getAllFixturesDirectory() throws IOException {
    File file = allFixturesDirectory ;
    if( null == file ) {

      final String testfilesDirSystemProperty =
          System.getProperty( SCRATCH_DIRECTORY_SYSTEM_PROPERTY_NAME ) ;
      if( null == testfilesDirSystemProperty ) {
        file = new File( SCRATCH_DIR_NAME ) ;
      } else {
        file = new File( testfilesDirSystemProperty ) ;
      }

      if(
          file.exists() &&
          ! "no".equalsIgnoreCase(
              System.getProperty( DELETE_SCRATCH_DIRECTORY_SYSTEM_PROPERTY_NAME ) )
          ) {
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

  public File getTestScratchDirectory() throws IOException {
    if( null == testScratchDirectory ) {
      testScratchDirectory = new File( getAllFixturesDirectory(), testIdentifier ) ;
      if( testScratchDirectory.exists() ) {
        FileUtils.deleteDirectory( testScratchDirectory ) ;
      }
      testScratchDirectory.mkdir() ;
    }
    return testScratchDirectory;
  }



}

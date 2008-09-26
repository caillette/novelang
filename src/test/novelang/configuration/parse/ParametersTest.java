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
package novelang.configuration.parse;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.io.FileUtils;
import novelang.ScratchDirectoryFixture;
import com.google.common.collect.Iterables;

/**
 * Tests for {@link GenericParameters}, {@link BatchParameters}, {@link DaemonParameters}.
 * Option names are hardcoded here in sort that we get warned if there is a change in
 * implementation.
 *
 * @author Laurent Caillette
 */
public class ParametersTest {

  private static final Logger LOGGER = LoggerFactory.getLogger( ParametersTest.class ) ;

  @Test
  public void voidDaemonParameters() throws ArgumentsNotParsedException {
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, new String[ 0 ] ) ;
//    assertNull( parameters.getStyleDirectory() );
//    assertNull( parameters.getHyphenationDirectory() ) ;
//    assertFalse( parameters.getFontDirectories().iterator().hasNext() ) ;
//    assertEquals( 8080, parameters.getHttpDaemonPort() ) ;
  }

  @Test( expected = ArgumentsNotParsedException.class )
  public void voidBatchParameters() throws ArgumentsNotParsedException {
    new BatchParameters( scratchDirectory, new String[ 0 ] ) ;
  }

  @Test
  public void style() throws ArgumentsNotParsedException {
    final String[] arguments = { "--style", DIRECTORY_NAME_AAA } ;
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, arguments ) ;

//    final Iterable< File > fontDirectories = parameters.getFontDirectories() ;
//    assertFalse(
//        "There should be no font directories but got: " + Iterables.toString( fontDirectories ),
//        fontDirectories.iterator().hasNext()
//    ) ;

//    assertEquals( DIRECTORY_NAME_AAA, parameters.getStyleDirectory().getName() ) ;
  }

  @Test
  public void fonts2() throws ArgumentsNotParsedException {
    final String[] arguments =
        { "--fonts", DIRECTORY_NAME_AAA, /*SystemUtils.PATH_SEPARATOR*/ DIRECTORY_NAME_BBB } ;
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, arguments ) ;

//    final File styleDirectory = parameters.getStyleDirectory() ;
//    assertNull(
//        "There should be no style directory but got: " + styleDirectory,
//        styleDirectory
//    ) ;

//    final Iterator< File > fontsDirectories = parameters.getFontDirectories().iterator() ;
//    assertEquals( directoryAaa, fontsDirectories.next() );
//    assertEquals( directoryBbb, fontsDirectories.next() );
//    assertFalse( fontsDirectories.hasNext() ) ;
  }

// =======
// Fixture
// =======

  private static final String DIRECTORY_NAME_DDD = "ddd";
  private static final String DIRECTORY_NAME_AAA = "aaa";
  private static final String DIRECTORY_NAME_BBB = "bbb";
  private static final String DIRECTORY_NAME_CCC = "ccc";

  private final File scratchDirectory ;
  private final File directoryAaa ;
  private final File directoryBbb ;
  private final File directoryCcc ;
  private final File directoryCccDdd ;

  public ParametersTest() throws IOException {
    scratchDirectory =
        new ScratchDirectoryFixture( ParametersTest.class ).getTestScratchDirectory() ;

    directoryAaa = createDirectory( scratchDirectory, DIRECTORY_NAME_AAA ) ;
    directoryBbb = createDirectory( scratchDirectory, DIRECTORY_NAME_BBB ) ;
    directoryCcc = createDirectory( scratchDirectory, DIRECTORY_NAME_CCC ) ;
    directoryCccDdd = createDirectory( directoryCcc, DIRECTORY_NAME_DDD ) ;

    assertTrue( directoryAaa.exists() ) ;
    assertTrue( directoryBbb.exists() ) ;
    assertTrue( directoryCcc.exists() ) ;
    assertTrue( directoryCccDdd.exists() ) ;
  }

  private static File createDirectory( File parent, String name ) {
    final File directory = new File( parent, name ) ;
    if( ! directory.exists() ) {
      directory.mkdir() ;
    }
    assertTrue(
        "Could not create: '" + directory.getAbsolutePath() + "'",
        FileUtils.waitFor( directory, 1 )
    ) ;
    return directory ;
  }
}

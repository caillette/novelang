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
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FileUtils;
import novelang.ScratchDirectoryFixture;
import com.google.common.collect.Lists;

/**
 * Tests for {@link GenericParameters}, {@link BatchParameters}, {@link DaemonParameters}.
 * Option names are hardcoded here in sort that we get warned if there is a change in
 * implementation.
 *
 * @author Laurent Caillette
 */
public class ParametersTest {

  @Test
  public void voidDaemonParameters() throws ArgumentsNotParsedException {
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, new String[ 0 ] ) ;
    assertNull( parameters.getHttpDaemonPort() ) ;
    assertNull( parameters.getStyleDirectory() );
    assertNull( parameters.getHyphenationDirectory() ) ;
    assertOnIterable( parameters.getFontDirectories() ) ;
  }

  @Test( expected = ArgumentsNotParsedException.class )
  public void voidBatchParameters() throws ArgumentsNotParsedException {
    new BatchParameters( scratchDirectory, new String[ 0 ] ) ;
  }

  @Test
  public void style() throws ArgumentsNotParsedException {
    final String[] arguments = { DASHED_STYLE_DIR, DIRECTORY_NAME_AAA } ;
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, arguments ) ;

    assertEquals( directoryAaa , parameters.getStyleDirectory() ) ;
    assertOnIterable( parameters.getFontDirectories() ) ;
    assertNull( parameters.getLogDirectory() ) ;
    assertNull( parameters.getHyphenationDirectory() ) ;
  }

  @Test
  public void hyphenation() throws ArgumentsNotParsedException {
    final String[] arguments = { DASHED_HYPHENATION_DIR, DIRECTORY_NAME_AAA } ;
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, arguments ) ;

    assertEquals( directoryAaa , parameters.getHyphenationDirectory() ) ;
    assertOnIterable( parameters.getFontDirectories() ) ;
    assertNull( parameters.getLogDirectory() ) ;
    assertNull( parameters.getStyleDirectory() ) ;
  }

  @Test
  public void log() throws ArgumentsNotParsedException {
    final String[] arguments = { DASHED_LOG_DIR, DIRECTORY_NAME_AAA } ;
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, arguments ) ;

    assertEquals( directoryAaa , parameters.getLogDirectory() ) ;
    assertOnIterable( parameters.getFontDirectories() ) ;
    assertNull( parameters.getHyphenationDirectory() ) ;
    assertNull( parameters.getStyleDirectory() ) ;
  }

  @Test
  public void fonts2() throws ArgumentsNotParsedException {
    final String[] arguments =
        { DASHED_FONT_DIRS, DIRECTORY_NAME_AAA, DIRECTORY_NAME_BBB } ;
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, arguments ) ;

    assertNull( parameters.getStyleDirectory() ) ;
    assertOnIterable( parameters.getFontDirectories(), directoryAaa, directoryBbb ) ;
    assertNull( parameters.getLogDirectory() ) ;
    assertNull( parameters.getHyphenationDirectory() ) ;
  }

  @Test
  public void fonts2AndStyle() throws ArgumentsNotParsedException {
    final String[] arguments = { DASHED_FONT_DIRS, DIRECTORY_NAME_AAA, DIRECTORY_NAME_BBB,
        DASHED_STYLE_DIR, DIRECTORY_NAME_CCC } ;
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, arguments ) ;

    assertOnIterable( parameters.getFontDirectories(), directoryAaa, directoryBbb ) ;
    assertEquals( directoryCcc , parameters.getStyleDirectory() ) ;
    assertNull( parameters.getLogDirectory() ) ;
    assertNull( parameters.getHyphenationDirectory() ) ;
  }

  @Test
  public void styleAndFonts2() throws ArgumentsNotParsedException {
    final String[] arguments = { DASHED_STYLE_DIR, DIRECTORY_NAME_AAA,
            DASHED_FONT_DIRS, DIRECTORY_NAME_BBB, DIRECTORY_NAME_CCC } ;
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, arguments ) ;

    assertEquals( directoryAaa , parameters.getStyleDirectory() ) ;
    assertOnIterable( parameters.getFontDirectories(), directoryBbb, directoryCcc ) ;
    assertNull( parameters.getLogDirectory() ) ;
    assertNull( parameters.getHyphenationDirectory() ) ;
  }

  @Test ( expected = ArgumentsNotParsedException.class )
  public void badStyleDirectory() throws ArgumentsNotParsedException {
    final String[] arguments = { DASHED_STYLE_DIR, "xxx" } ;
    new DaemonParameters( scratchDirectory, arguments ) ;
  }

// =======
// Fixture
// =======

  private static final String DASHED_HYPHENATION_DIR = "--hyphenation-dir";
  private static final String DASHED_STYLE_DIR = "--style-dir";
  private static final String DASHED_FONT_DIRS = "--font-dirs";
  private static final String DASHED_LOG_DIR = "--log-dir";


  private static< T > void assertOnIterable( Iterable< T > actual, T... expected ) {
    final Iterator< T > iterator = actual.iterator() ;
    for( T expectedElement : expected ) {
      final T actualElement = iterator.next() ;
      assertEquals( expectedElement, actualElement ) ;
    }
    assertFalse( "Too many elements: " + Lists.newArrayList( actual ), iterator.hasNext() ) ;
  }


  private static final String DIRECTORY_NAME_DDD = "ddd";
  private static final String DIRECTORY_NAME_AAA = "aaa";
  private static final String DIRECTORY_NAME_BBB = "bbb";
  private static final String DIRECTORY_NAME_CCC = "ccc";

  private File scratchDirectory ;
  private File directoryAaa ;
  private File directoryBbb ;
  private File directoryCcc ;
  private File directoryCccDdd ;

  @Before
  public void setUp() throws IOException {
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

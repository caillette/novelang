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
import org.junit.Before;
import org.junit.Test;
import novelang.system.LogFactory;
import novelang.system.Log;
import org.apache.commons.lang.ClassUtils;
import com.google.common.collect.Lists;
import novelang.ScratchDirectoryFixture;
import novelang.TestResourceTools;
import novelang.produce.DocumentRequest;
import novelang.produce.RequestTools;

/**
 * Tests for {@link GenericParameters}, {@link BatchParameters}, {@link DaemonParameters}.
 * Option names are hardcoded here in sort that we get warned if there is a change in
 * implementation.
 *
 * @author Laurent Caillette
 */
public class ParametersTest {

  @Test
  public void voidDaemonParameters() throws ArgumentException {
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, new String[ 0 ] ) ;
    assertNull( parameters.getHttpDaemonPort() ) ;
    assertNull( parameters.getStyleDirectory() );
    assertNull( parameters.getHyphenationDirectory() ) ;
    assertOnIterable( parameters.getFontDirectories() ) ;
  }

  @Test( expected = ArgumentException.class )
  public void voidBatchParameters() throws ArgumentException {
    new BatchParameters( scratchDirectory, new String[ 0 ] ) ;
  }

  @Test
  public void style() throws ArgumentException {
    final String[] arguments = { DASHED_STYLE_DIR, DIRECTORY_NAME_AAA } ;
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, arguments ) ;

    assertEquals( directoryAaa , parameters.getStyleDirectory() ) ;
    assertOnIterable( parameters.getFontDirectories() ) ;
    assertNull( parameters.getLogDirectory() ) ;
    assertNull( parameters.getHyphenationDirectory() ) ;
  }

  @Test
  public void hyphenation() throws ArgumentException {
    final String[] arguments = { DASHED_HYPHENATION_DIR, DIRECTORY_NAME_AAA } ;
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, arguments ) ;

    assertEquals( directoryAaa , parameters.getHyphenationDirectory() ) ;
    assertOnIterable( parameters.getFontDirectories() ) ;
    assertNull( parameters.getLogDirectory() ) ;
    assertNull( parameters.getStyleDirectory() ) ;
  }

  @Test
  public void log() throws ArgumentException {
    final String[] arguments = { DASHED_LOG_DIR, DIRECTORY_NAME_AAA } ;
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, arguments ) ;

    assertEquals( directoryAaa , parameters.getLogDirectory() ) ;
    assertOnIterable( parameters.getFontDirectories() ) ;
    assertNull( parameters.getHyphenationDirectory() ) ;
    assertNull( parameters.getStyleDirectory() ) ;
  }

  @Test
  public void fonts2() throws ArgumentException {
    final String[] arguments =
        { DASHED_FONT_DIRS, DIRECTORY_NAME_AAA, DIRECTORY_NAME_BBB } ;
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, arguments ) ;

    assertNull( parameters.getStyleDirectory() ) ;
    assertOnIterable( parameters.getFontDirectories(), directoryAaa, directoryBbb ) ;
    assertNull( parameters.getLogDirectory() ) ;
    assertNull( parameters.getHyphenationDirectory() ) ;
  }

  @Test
  public void fonts2AndStyle() throws ArgumentException {
    final String[] arguments = { DASHED_FONT_DIRS, DIRECTORY_NAME_AAA, DIRECTORY_NAME_BBB,
        DASHED_STYLE_DIR, DIRECTORY_NAME_CCC } ;
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, arguments ) ;

    assertOnIterable( parameters.getFontDirectories(), directoryAaa, directoryBbb ) ;
    assertEquals( directoryCcc , parameters.getStyleDirectory() ) ;
    assertNull( parameters.getLogDirectory() ) ;
    assertNull( parameters.getHyphenationDirectory() ) ;
  }

  @Test
  public void styleAndFonts2() throws ArgumentException {
    final String[] arguments = { DASHED_STYLE_DIR, DIRECTORY_NAME_AAA,
            DASHED_FONT_DIRS, DIRECTORY_NAME_BBB, DIRECTORY_NAME_CCC } ;
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, arguments ) ;

    assertEquals( directoryAaa , parameters.getStyleDirectory() ) ;
    assertOnIterable( parameters.getFontDirectories(), directoryBbb, directoryCcc ) ;
    assertNull( parameters.getLogDirectory() ) ;
    assertNull( parameters.getHyphenationDirectory() ) ;
  }

  @Test ( expected = ArgumentException.class )
  public void badStyleDirectory() throws ArgumentException {
    final String[] arguments = { DASHED_STYLE_DIR, "xxx" } ;
    new DaemonParameters( scratchDirectory, arguments ) ;
  }

  @Test
  public void bugWithDocumentsConfusedWithFontDirectories() throws ArgumentException {
    final String[] arguments = {
        DASHED_FONT_DIRS,
        DIRECTORY_NAME_AAA,
        "--",
        OUTPUT_FILE_NAME
    } ;
    final BatchParameters batchParameters = new BatchParameters( scratchDirectory, arguments ) ;
    assertOnIterable( batchParameters.getFontDirectories(), directoryAaa ) ;
    assertOnIterable(
        batchParameters.getDocumentRequests(),
        DOCUMENT_REQUEST
    ) ;
  }

  @Test
  public void batchParametersWantDocumentRequests() {
    final String[] arguments = new String[ 0 ] ;
    try {
      new BatchParameters( scratchDirectory, arguments ) ;
      fail( "Exception should have been thrown" ) ;
    } catch( ArgumentException e ) {
      LOG.info( e.getHelpPrinter().asString( ClassUtils.getShortClassName( getClass() ), 80 ) );
    }
  }

// =======
// Fixture
// =======

  private static final Log LOG = LogFactory.getLog( ParametersTest.class ) ;

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

  private static final String OUTPUT_FILE_NAME = "/this-is-not-a-directory-but-output-file.html" ;

  private static final DocumentRequest DOCUMENT_REQUEST =
      RequestTools.createDocumentRequest( OUTPUT_FILE_NAME ) ;

  @Before
  public void setUp() throws IOException {
    scratchDirectory =
        new ScratchDirectoryFixture( ParametersTest.class ).getTestScratchDirectory() ;

    directoryAaa = TestResourceTools.createDirectory( scratchDirectory, DIRECTORY_NAME_AAA ) ;
    directoryBbb = TestResourceTools.createDirectory( scratchDirectory, DIRECTORY_NAME_BBB ) ;
    directoryCcc = TestResourceTools.createDirectory( scratchDirectory, DIRECTORY_NAME_CCC ) ;
    directoryCccDdd = TestResourceTools.createDirectory( directoryCcc, DIRECTORY_NAME_DDD ) ;

    assertTrue( directoryAaa.exists() ) ;
    assertTrue( directoryBbb.exists() ) ;
    assertTrue( directoryCcc.exists() ) ;
    assertTrue( directoryCccDdd.exists() ) ;
  }

}

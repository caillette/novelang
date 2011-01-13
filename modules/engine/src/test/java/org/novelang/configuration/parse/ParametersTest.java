/*
 * Copyright (C) 2010 Laurent Caillette
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
package org.novelang.configuration.parse;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import com.google.common.collect.Lists;
import org.apache.commons.lang.ClassUtils;
import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.novelang.ResourceTools;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.produce.DocumentRequest;
import org.novelang.produce.GenericRequest;
import org.novelang.produce.MalformedRequestException;
import org.novelang.testing.DirectoryFixture;

import static org.junit.Assert.*;

/**
 * Tests for {@link GenericParameters}, {@link DocumentGeneratorParameters}, {@link DaemonParameters}.
 * Option names are hardcoded here in sort that we get warned if there is a change in
 * implementation.
 *
 * @author Laurent Caillette
 */
public class ParametersTest {

  @Test
  public void voidDaemonParameters() throws ArgumentException {
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory ) ;
    assertNull( parameters.getContentRoot() ) ;
    assertNull( parameters.getLogDirectory() ) ;
    assertNull( parameters.getTemporaryDirectory() ) ;
    assertNull( parameters.getHttpDaemonPort() ) ;
    assertFalse( parameters.getStyleDirectories().iterator().hasNext() );
    assertNull( parameters.getHyphenationDirectory() ) ;
    assertOnIterable( parameters.getFontDirectories() ) ;
  }

  @Test( expected = ArgumentException.class )
  public void voidBatchParameters() throws ArgumentException {
    new DocumentGeneratorParameters( scratchDirectory, NO_PARAMETERS ) ;
  }

  @Test( expected = ArgumentException.class )
  public void missingLogDirectory() throws ArgumentException {
    new DocumentGeneratorParameters(
        scratchDirectory, new String[] { DASHED_LOG_DIR, "doesnotexist" } ) ;
  }

  @Test
  public void style1() throws ArgumentException {
    final String[] arguments = { DASHED_STYLE_DIRS, DIRECTORY_NAME_AAA } ;
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, arguments ) ;

    assertOnIterable( parameters.getStyleDirectories(), directoryAaa ) ;
    assertOnIterable( parameters.getFontDirectories() ) ;
    assertNull( parameters.getLogDirectory() ) ;
    assertNull( parameters.getHyphenationDirectory() ) ;
  }

  @Test
  public void contentRoot() throws ArgumentException {
    final String[] arguments = { DASHED_CONTENTROOT, DIRECTORY_NAME_AAA } ;
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, arguments ) ;

    assertEquals( directoryAaa , parameters.getContentRoot() ) ;
    assertOnIterable( parameters.getFontDirectories() ) ;
    assertNull( parameters.getLogDirectory() ) ;
    assertOnIterable( parameters.getStyleDirectories() ) ;
  }

  @Test
  public void hyphenation() throws ArgumentException {
    final String[] arguments = { DASHED_HYPHENATION_DIR, DIRECTORY_NAME_AAA } ;
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, arguments ) ;

    assertEquals( directoryAaa , parameters.getHyphenationDirectory() ) ;
    assertOnIterable( parameters.getFontDirectories() ) ;
    assertNull( parameters.getLogDirectory() ) ;
    assertOnIterable( parameters.getStyleDirectories() ) ;
  }

  @Test
  public void log() throws ArgumentException {
    final String[] arguments = { DASHED_LOG_DIR, DIRECTORY_NAME_AAA } ;
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, arguments ) ;

    assertEquals( directoryAaa , parameters.getLogDirectory() ) ;
    assertOnIterable( parameters.getFontDirectories() ) ;
    assertNull( parameters.getHyphenationDirectory() ) ;
    assertOnIterable( parameters.getStyleDirectories() ) ;
  }

  @Test
  public void style2() throws ArgumentException {
    final String[] arguments =
        { DASHED_STYLE_DIRS, DIRECTORY_NAME_AAA, DIRECTORY_NAME_BBB } ;
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, arguments ) ;

    assertOnIterable( parameters.getFontDirectories() ) ;
    assertOnIterable( parameters.getStyleDirectories(), directoryAaa, directoryBbb ) ;
    assertNull( parameters.getLogDirectory() ) ;
    assertNull( parameters.getHyphenationDirectory() ) ;
  }

  @Test
  public void fonts2() throws ArgumentException {
    final String[] arguments =
        { DASHED_FONT_DIRS, DIRECTORY_NAME_AAA, DIRECTORY_NAME_BBB } ;
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, arguments ) ;

    assertOnIterable( parameters.getStyleDirectories() ) ;
    assertOnIterable( parameters.getFontDirectories(), directoryAaa, directoryBbb ) ;
    assertNull( parameters.getLogDirectory() ) ;
    assertNull( parameters.getHyphenationDirectory() ) ;
  }

  @Test
  public void fonts2AndStyle1() throws ArgumentException {
    final String[] arguments = { DASHED_FONT_DIRS, DIRECTORY_NAME_AAA, DIRECTORY_NAME_BBB,
        DASHED_STYLE_DIRS, DIRECTORY_NAME_CCC } ;
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, arguments ) ;

    assertOnIterable( parameters.getFontDirectories(), directoryAaa, directoryBbb ) ;
    assertOnIterable( parameters.getStyleDirectories(), directoryCcc ) ;
    assertNull( parameters.getLogDirectory() ) ;
    assertNull( parameters.getHyphenationDirectory() ) ;
  }

  @Test
  public void style1AndFonts2() throws ArgumentException {
    final String[] arguments = { DASHED_STYLE_DIRS, DIRECTORY_NAME_AAA,
            DASHED_FONT_DIRS, DIRECTORY_NAME_BBB, DIRECTORY_NAME_CCC } ;
    final DaemonParameters parameters = new DaemonParameters( scratchDirectory, arguments ) ;

    assertOnIterable( parameters.getStyleDirectories(), directoryAaa ) ;
    assertOnIterable( parameters.getFontDirectories(), directoryBbb, directoryCcc ) ;
    assertNull( parameters.getLogDirectory() ) ;
    assertNull( parameters.getHyphenationDirectory() ) ;
  }

  @Test ( expected = ArgumentException.class )
  public void badStyleDirectory() throws ArgumentException {
    final String[] arguments = { DASHED_STYLE_DIRS, "xxx" } ;
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
    final DocumentGeneratorParameters batchParameters = new DocumentGeneratorParameters( scratchDirectory, arguments ) ;
    assertOnIterable( batchParameters.getFontDirectories(), directoryAaa ) ;
    assertOnIterable(
        batchParameters.getDocumentRequests(),
        DOCUMENT_REQUEST
    ) ;
  }

  @Test
  public void batchParametersWantDocumentRequests() {
    final String[] arguments = NO_PARAMETERS;
    try {
      new DocumentGeneratorParameters( scratchDirectory, arguments ) ;
      fail( "Exception should have been thrown" ) ;
    } catch( ArgumentException e ) {
      LOGGER.info( e.getHelpPrinter().asString( ClassUtils.getShortClassName( getClass() ), 80 ) );
    }
  }

  @Test
  public void temporaryDirectory() throws ArgumentException {
    final String[] arguments = { DASHED_TEMPORARY_DIR, "my-temporary-directory" } ;
    final DaemonParameters daemonParameters = new DaemonParameters( scratchDirectory, arguments ) ;
    Assertions.assertThat( daemonParameters.getTemporaryDirectory() ).isNotNull() ;
  }

// =======
// Fixture
// =======

  private static final Logger LOGGER = LoggerFactory.getLogger( ParametersTest.class );

  private static final String DASHED_CONTENTROOT = "--content-root" ;
  private static final String DASHED_HYPHENATION_DIR = "--hyphenation-dir" ;
  private static final String DASHED_STYLE_DIRS = "--style-dirs" ;
  private static final String DASHED_FONT_DIRS = "--font-dirs" ;
  private static final String DASHED_LOG_DIR = "--log-dir" ;
  private static final String DASHED_TEMPORARY_DIR = "--temporary-dir" ;


  private static< T > void assertOnIterable( final Iterable< T > actual, final T... expected ) {
    final Iterator< T > iterator = actual.iterator() ;
    for( final T expectedElement : expected ) {
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

  private static final String OUTPUT_FILE_NAME = "/this-is-not-a-directory-but-output-file.html" ;

  private static final DocumentRequest DOCUMENT_REQUEST ;
  static {
    try {
      DOCUMENT_REQUEST = ( DocumentRequest ) GenericRequest.parse( OUTPUT_FILE_NAME ) ;
    } catch( MalformedRequestException e ) {
      throw new RuntimeException( e ) ;
    }
  }

  @Before
  public void setUp() throws IOException {
    scratchDirectory =
        new DirectoryFixture( ParametersTest.class ).getDirectory() ;

    directoryAaa = ResourceTools.createDirectory( scratchDirectory, DIRECTORY_NAME_AAA ) ;
    directoryBbb = ResourceTools.createDirectory( scratchDirectory, DIRECTORY_NAME_BBB ) ;
    directoryCcc = ResourceTools.createDirectory( scratchDirectory, DIRECTORY_NAME_CCC ) ;
    File directoryCccDdd=ResourceTools.createDirectory( directoryCcc, DIRECTORY_NAME_DDD );

    assertTrue( directoryAaa.exists() ) ;
    assertTrue( directoryBbb.exists() ) ;
    assertTrue( directoryCcc.exists() ) ;
    assertTrue( directoryCccDdd.exists() ) ;
  }

  private static final String[] NO_PARAMETERS = new String[ 0 ] ;


}

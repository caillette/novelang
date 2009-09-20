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
package novelang.rendering;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import novelang.system.LogFactory;
import novelang.DirectoryFixture;
import novelang.TestResourceTools;
import novelang.TestResources;
import novelang.system.DefaultCharset;
import novelang.system.Log;
import novelang.configuration.ProducerConfiguration;
import novelang.loader.ResourceName;
import novelang.produce.DocumentProducer;
import novelang.produce.DocumentRequest;
import novelang.produce.RequestTools;

/**
 * Test for displaying page numbers, including a Java function call.
 *
 * @author Laurent Caillette
 */
public class NumberingTest {

  private static final Log LOG = LogFactory.getLog( NumberingTest.class ) ;

  @Test
  public void testNodeset() throws Exception {
    final ProducerConfiguration serverConfiguration = TestResources.createProducerConfiguration(
        styleDirectory,
        styleDirectory.getName(),
        true,
        DefaultCharset.RENDERING
    ) ;
    final DocumentProducer documentProducer = new DocumentProducer( serverConfiguration ) ;
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream() ;
    final DocumentRequest documentRequest =
        RequestTools.forgeDocumentRequest(
            SOME_CHAPTERS_FILENAME,
            RenditionMimeType.PDF,
            STYLESHEET_RESOURCE
        )
    ;
    documentProducer.produce( documentRequest, outputStream ) ;
    final String result = new String( outputStream.toByteArray() ) ;

//    LOGGER.info( "Produced: %s", result ) ;
  }

// =======
// Fixture
// =======

  private static final ResourceName STYLESHEET_RESOURCE = TestResources.NODESET_XSL ;
  public static final String NODESET_DIR = TestResources.NODESET_DIR ;
  public static final String SOME_CHAPTERS_FILENAME = TestResources.NODESET_SOMECHAPTERS_DOCUMENTNAME;
  public static final String SOME_CHAPTERS = TestResources.NODESET_SOMECHAPTERS;

  private File styleDirectory ;


  @Before
  public void setUp() throws IOException {
    final File scratchDirectory = new DirectoryFixture( getClass().getName() )
        .getDirectory() ;
    styleDirectory = new File( scratchDirectory, NODESET_DIR ) ;
    TestResourceTools.copyResourceToDirectory(
        getClass(),
        SOME_CHAPTERS,
        scratchDirectory
    ) ;
  }

}

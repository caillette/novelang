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

import novelang.ResourceTools;
import novelang.ResourcesForTests;
import novelang.common.filefixture.JUnitAwareResourceInstaller;
import novelang.configuration.ProducerConfiguration;
import novelang.logger.Logger;
import novelang.logger.LoggerFactory;
import novelang.produce.DocumentProducer;
import novelang.produce.DocumentRequest;
import novelang.produce.RequestTools;
import novelang.system.DefaultCharset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.NameAwareTestClassRunner;

/**
 * Test for displaying page and chapter numbers, including a Java function call.
 *
 * @author Laurent Caillette
 */
@RunWith( NameAwareTestClassRunner.class)
public class NumberingTest {


  @Test
  public void testNodeset() throws Exception {
    final JUnitAwareResourceInstaller resourceInstaller = new JUnitAwareResourceInstaller() ;
    resourceInstaller.copy( ResourcesForTests.XslFormatting.dir ) ;


    final ProducerConfiguration serverConfiguration = ResourceTools.createProducerConfiguration(
        resourceInstaller.getTargetDirectory(),
        resourceInstaller.createFileObject( ResourcesForTests.XslFormatting.dir ),
        true,
        DefaultCharset.RENDERING
    ) ;
    final DocumentProducer documentProducer = new DocumentProducer( serverConfiguration ) ;
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream() ;

    final String documentName =
        ResourcesForTests.XslFormatting.PART_SOMECHAPTERS.getPathNoEndSeparator() + "/" +
        ResourcesForTests.XslFormatting.PART_SOMECHAPTERS.getBaseName()
    ;
    LOGGER.debug( "Document name = '", documentName, "'." ) ;
    final DocumentRequest documentRequest = RequestTools.forgeDocumentRequest(
        documentName,
        RenditionMimeType.PDF,
        ResourcesForTests.XslFormatting.XSL_NUMBERING.getResourceName()
    ) ;

    documentProducer.produce( documentRequest, outputStream ) ;
    final String result = new String( outputStream.toByteArray() ) ;

//    LOGGER.info( "Produced: %s", result ) ;
  }

// =======
// Fixture
// =======

  private static final Logger LOGGER = LoggerFactory.getLogger( NumberingTest.class );


  static {
    ResourcesForTests.initialize() ;
  }

}

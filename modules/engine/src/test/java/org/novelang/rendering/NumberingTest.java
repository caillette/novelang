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
package org.novelang.rendering;

import java.io.ByteArrayOutputStream;

import org.junit.Rule;
import org.junit.Test;
import org.novelang.ResourceTools;
import org.novelang.ResourcesForTests;
import org.novelang.common.filefixture.ResourceInstaller;
import org.novelang.configuration.ProducerConfiguration;
import org.novelang.configuration.RenditionKinematic;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.DefaultCharset;
import org.novelang.outfit.loader.CompositeResourceLoader;
import org.novelang.produce.DocumentProducer;
import org.novelang.produce.DocumentRequest;
import org.novelang.produce.GenericRequest;
import org.novelang.produce.StreamDirector;
import org.novelang.testing.junit.MethodSupport;

/**
 * Test for displaying page and chapter numbers, including a Java function call.
 *
 * @author Laurent Caillette
 */
public class NumberingTest {


  @Test
  public void testNodeset() throws Exception {

    resourceInstaller.copy( ResourcesForTests.XslFormatting.dir ) ;


    final ProducerConfiguration serverConfiguration = ResourceTools.createProducerConfiguration(
        resourceInstaller.getTargetDirectory(),
//        resourceInstaller.createFileObject( ResourcesForTests.XslFormatting.dir ),
//        true,
        CompositeResourceLoader.create(
            org.novelang.configuration.ConfigurationTools.BUNDLED_STYLE_DIR,
            resourceInstaller.createFileObject( ResourcesForTests.XslFormatting.dir )
        ),
        DefaultCharset.RENDERING,
        RenditionKinematic.BATCH
    ) ;
    final DocumentProducer documentProducer = new DocumentProducer( serverConfiguration ) ;
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream() ;

    final String documentName =
        ResourcesForTests.XslFormatting.PART_SOMECHAPTERS.getPathNoEndSeparator() + "/" +
        ResourcesForTests.XslFormatting.PART_SOMECHAPTERS.getBaseName()
    ;
    LOGGER.debug( "Document name = '", documentName, "'." ) ;
    final DocumentRequest documentRequest = ( DocumentRequest ) GenericRequest.parse(
        "/" + documentName + "." + RenditionMimeType.PDF.getFileExtension() +
        "?stylesheet=" + ResourcesForTests.XslFormatting.XSL_NUMBERING.getResourceName().getName()
    ) ;

    documentProducer.produce(
        documentRequest,
        documentProducer.createRenderable( documentRequest ),
        StreamDirector.forExistingStream( outputStream ) 
    ) ;
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

  @Rule
  public final MethodSupport methodSupport = new MethodSupport() ;

  private final ResourceInstaller resourceInstaller = new ResourceInstaller( methodSupport ) ;

}

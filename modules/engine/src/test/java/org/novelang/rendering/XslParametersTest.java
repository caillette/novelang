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
import java.util.regex.Pattern;

import org.fest.assertions.Assertions;
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

import static org.fest.assertions.Assertions.assertThat;

/**
 * Test for parameters passed to the XSL stylesheet.
 *
 * @author Laurent Caillette
 */
public class XslParametersTest {


  @Test
  public void allParametersPassed() throws Exception {

    resourceInstaller.copyWithPath( ResourcesForTests.XslFormatting.PART_SOMECHAPTERS ) ;
    resourceInstaller.copyWithPath( ResourcesForTests.XslFormatting.XSL_STYLESHEET_PARAMETERS ) ;

    final ProducerConfiguration serverConfiguration = ResourceTools.createProducerConfiguration(
        resourceInstaller.getTargetDirectory(),
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
        "/" + documentName + "." + RenditionMimeType.HTML.getFileExtension() +
        "?stylesheet=" + ResourcesForTests.XslFormatting.XSL_STYLESHEET_PARAMETERS.getResourceName().getName()
    ) ;

    documentProducer.produce(
        documentRequest,
        documentProducer.createRenderable( documentRequest ),
        StreamDirector.forExistingStream( outputStream ) 
    ) ;
    final String result = new String( outputStream.toByteArray() ) ;

    LOGGER.info( "Produced: %s", result ) ;

    assertThat( TIMESTAMP_REGEX.matcher( result ).find() ).isTrue() ;
    assertThat( result ).contains( "charset=UTF-8;" ) ;
    assertThat( result ).contains(
        "content-root=" + resourceInstaller.getTargetDirectory().getAbsolutePath() ) ;

  }

// =======
// Fixture
// =======

  private static final Logger LOGGER = LoggerFactory.getLogger( XslParametersTest.class );


  static {
    ResourcesForTests.initialize() ;
  }

  @Rule
  public final MethodSupport methodSupport = new MethodSupport() ;

  private final ResourceInstaller resourceInstaller = new ResourceInstaller( methodSupport ) ;

  private static final Pattern TIMESTAMP_REGEX = Pattern.compile( "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}[+\\-]\\d{2}:\\d{2}" ) ;

}

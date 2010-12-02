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
package org.novelang.rendering.multipage;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.Executors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import javax.xml.transform.URIResolver;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.fest.assertions.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.novelang.ResourcesForTests;
import org.novelang.common.filefixture.Resource;
import org.novelang.common.filefixture.ResourceInstaller;
import org.novelang.designator.Tag;
import org.novelang.opus.Opus;
import org.novelang.outfit.DefaultCharset;
import org.novelang.outfit.loader.ClasspathResourceLoader;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.xml.EntityEscapeSelector;
import org.novelang.outfit.xml.LocalEntityResolver;
import org.novelang.outfit.xml.LocalUriResolver;
import org.novelang.testing.DirectoryFixture;
import org.novelang.testing.junit.NameAwareTestClassRunner;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import static org.fest.assertions.Assertions.assertThat;
import static org.novelang.ResourcesForTests.initialize;

/**
 * Tests for {@link org.novelang.rendering.multipage.XslMultipageStylesheetCapture}.
 *
 * @author Laurent Caillette
 */
@RunWith( value = NameAwareTestClassRunner.class )
public class XslPageIdentifierExtractorTest {

  @Test
  public void extractPageIdentifiers() throws Exception {

    final ResourceInstaller installer =
        new ResourceInstaller( new DirectoryFixture().getDirectory() ) ;
    final File stylesheetFile =
        installer.copy( ResourcesForTests.Multipage.NOVELLA_MULTIPAGE_XSL ) ;
    final Resource novellaDocument = ResourcesForTests.Multipage.NOVELLA_DOCUMENT ;
    installer.copy( novellaDocument ) ;

    final ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader() ;
    final EntityResolver entityResolver =
        new LocalEntityResolver( resourceLoader, NO_ENTITY_ESCAPE ) ;
    final URIResolver uriResolver = new LocalUriResolver( resourceLoader, entityResolver ) ;

    final XslMultipageStylesheetCapture stylesheetCapture =
        new XslMultipageStylesheetCapture( entityResolver ) ;

    final XMLReader reader = XMLReaderFactory.createXMLReader() ;
    reader.setContentHandler( stylesheetCapture ) ;
    reader.parse( new InputSource(
        new StringReader( FileUtils.readFileToString( stylesheetFile ) ) ) ) ;


    final Document stylesheetDocument = stylesheetCapture.getStylesheetDocument() ;
    LOGGER.info( "Got stylesheet:\n", stylesheetDocument.asXML() ) ;

    final PageIdentifierExtractor pageIdentifierExtractor = new XslPageIdentifierExtractor(
        entityResolver, uriResolver, stylesheetCapture ) ;

    final Opus opus = new Opus(
        installer.getTargetDirectory(),
        installer.getTargetDirectory(),
        Executors.newSingleThreadExecutor(),
        "insert file:" + novellaDocument.getName(),
        DefaultCharset.SOURCE,
        DefaultCharset.RENDERING,
        ImmutableSet.< Tag >of()
    ) ;

    final ImmutableMap< PageIdentifier,String > pages =
        pageIdentifierExtractor.extractPageIdentifiers( opus.getDocumentTree() ) ;

    assertThat( pages ).isEqualTo( ImmutableMap.of(
        new PageIdentifier( "Level-0" ), "/opus/level[1]",
        new PageIdentifier( "Level-1" ), "/opus/level[2]"
    ) ) ;

  }


// =======
// Fixture
// =======


  private static final Logger LOGGER = LoggerFactory.getLogger(
      XslPageIdentifierExtractorTest.class ) ;

  static {
    initialize() ;
  }

  private static final EntityEscapeSelector NO_ENTITY_ESCAPE =
      new EntityEscapeSelector() {
        @Override
        public boolean shouldEscape( final String publicId, final String systemId ) {
          return false ;
        }
      }
  ;


}

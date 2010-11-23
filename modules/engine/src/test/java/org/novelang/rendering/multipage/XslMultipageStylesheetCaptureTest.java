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
package org.novelang.rendering.multipage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.novelang.ResourcesForTests;
import org.novelang.common.filefixture.ResourceInstaller;
import org.novelang.loader.ClasspathResourceLoader;
import org.novelang.loader.ResourceLoader;
import org.novelang.rendering.LocalEntityResolver;
import org.novelang.rendering.XslWriter;
import org.novelang.testing.DirectoryFixture;
import org.novelang.testing.junit.NameAwareTestClassRunner;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import static org.novelang.ResourcesForTests.initialize;

/**
 * Tests for {@link org.novelang.rendering.multipage.XslMultipageStylesheetCapture}.
 *
 * @author Laurent Caillette
 */
@RunWith( value = NameAwareTestClassRunner.class )
@Ignore( "Unfinished implementation" )
public class XslMultipageStylesheetCaptureTest {

  @Test
  public void extractStylesheetDocument() throws IOException, SAXException {
    final ResourceInstaller installer =
        new ResourceInstaller( new DirectoryFixture().getDirectory() ) ;
    final File stylesheet = installer.copy( ResourcesForTests.Multipage.NOVELLA_MULTIPAGE_XSL ) ;
    final File xml = installer.copy( ResourcesForTests.Multipage.XML_DOCUMENT ) ;

    final XslMultipageStylesheetCapture stylesheetCapture = new XslMultipageStylesheetCapture(
        new LocalEntityResolver( new ClasspathResourceLoader(), NO_ENTITY_ESCAPE ) ) ;

    final XMLReader reader = XMLReaderFactory.createXMLReader() ;
    reader.setContentHandler( stylesheetCapture ) ;
    reader.parse( new InputSource( FileUtils.readFileToString( stylesheet ) ) ) ;

    stylesheetCapture.getStylesheetDocument() ;

  }


// =======
// Fixture
// =======


  static {
    initialize() ;
  }

  private static final XslWriter.EntityEscapeSelector NO_ENTITY_ESCAPE =
      new XslWriter.EntityEscapeSelector() {
        @Override
        public boolean shouldEscape( final String publicId, final String systemId ) {
          return false ;
        }
      }
  ;


}

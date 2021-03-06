/*
 * Copyright (C) 2011 Laurent Caillette
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
import java.io.StringReader;
import java.util.concurrent.Executors;
import javax.xml.transform.URIResolver;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FileUtils;
import org.fest.assertions.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import static org.fest.assertions.Assertions.assertThat;
import static org.novelang.ResourcesForTests.initialize;

import org.novelang.ResourcesForTests;
import org.novelang.common.filefixture.Resource;
import org.novelang.common.filefixture.ResourceInstaller;
import org.novelang.common.metadata.PageIdentifier;
import org.novelang.designator.Tag;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.opus.Opus;
import org.novelang.outfit.DefaultCharset;
import org.novelang.outfit.loader.ClasspathResourceLoader;
import org.novelang.outfit.xml.EntityEscapeSelector;
import org.novelang.outfit.xml.LocalEntityResolver;
import org.novelang.outfit.xml.LocalUriResolver;
import org.novelang.outfit.xml.SaxRecorder;
import org.novelang.outfit.xml.TransformerCompositeException;
import org.novelang.testing.junit.MethodSupport;

/**
 * Tests for {@link org.novelang.rendering.multipage.XslMultipageStylesheetCapture}.
 *
 * @author Laurent Caillette
 */
public class XslPageIdentifierExtractorTest {

  @Test
  public void extractPageIdentifiers() throws Exception {
    verify(
        ResourcesForTests.Multipage.MULTIPAGE_NOVELLA,
        ImmutableMap.of(
            new PageIdentifier( "Level-0" ), "/opus/level[1]",
            new PageIdentifier( "Level-1" ), "/opus/level[2]"
        )
    ) ;
  }

  /**
   * The {@link ResourcesForTests.Multipage#MULTIPAGE_XSL} use level title as it is,
   * so it may produce an invalid {@link PageIdentifier}, like when using
   * {@link ResourcesForTests.Multipage#MULTIPAGE_HAZARDOUS_NOVELLA}.
   * This test guarantees the exception makes its way out.
   * <p>
   * Maybe this test should take a more general form because it's about a wider mechanism
   * that takes place in {@link org.novelang.outfit.xml.TransformerErrorListener} and what's
   * installing it. 
   */
  @Test( expected = TransformerCompositeException.class )
  public void rethrowExceptionFromXslTransformer() throws Exception {
    try {
      verify(
          ResourcesForTests.Multipage.MULTIPAGE_HAZARDOUS_NOVELLA,
          ImmutableMap.of(
              new PageIdentifier( "Level-0" ), "/opus/level[1]",
              new PageIdentifier( "Level-1" ), "/opus/level[2]"
          )
      ) ;
    } catch( TransformerCompositeException e ) {
      LOGGER.info( e, "Caught expected exception" ) ;
      Assertions.assertThat( e.getMessage() ).contains(
          "line=19; column=16 - java.lang.IllegalArgumentException: Name '' doesn't match " ) ;
      throw e ;
    }
  }

// =======
// Fixture
// =======


  private static final Logger LOGGER = LoggerFactory.getLogger(
      XslPageIdentifierExtractorTest.class ) ;

  static {
    initialize() ;
  }

  @Rule
  public final MethodSupport methodSupport = new MethodSupport() ;

  private final ResourceInstaller resourceInstaller = new ResourceInstaller( methodSupport ) ;


  private void verify(
      final Resource novellaDocument,
      final ImmutableMap< PageIdentifier, String > expectedPages
  ) throws Exception {
    final File stylesheetFile =
        resourceInstaller.copy( ResourcesForTests.Multipage.MULTIPAGE_XSL ) ;
    resourceInstaller.copy( novellaDocument ) ;

    final ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader() ;
    final EntityResolver entityResolver =
        new LocalEntityResolver( resourceLoader, NO_ENTITY_ESCAPE ) ;
    final URIResolver uriResolver = new LocalUriResolver( resourceLoader, entityResolver ) ;

    final SaxRecorder.Player[] stylesheetPlayerReference = new SaxRecorder.Player[ 1 ] ;

    final XslMultipageStylesheetCapture stylesheetCapture =
        new XslMultipageStylesheetCapture( entityResolver ) {
          @Override
          protected void onStylesheetDocumentBuilt( final SaxRecorder.Player freshStylesheetPlayer ) {
            stylesheetPlayerReference[ 0 ] = freshStylesheetPlayer ;
          }
        }
    ;

    final XMLReader reader = XMLReaderFactory.createXMLReader() ;
    reader.setContentHandler( stylesheetCapture ) ;
    reader.parse( new InputSource(
        new StringReader( FileUtils.readFileToString( stylesheetFile ) ) ) ) ;
    final SaxRecorder.Player stylesheetPlayer = stylesheetPlayerReference[ 0 ] ;

    LOGGER.info( "Got stylesheet:\n",
        stylesheetPlayer == null ? "null" : SaxRecorder.asXml( stylesheetPlayer ) ) ;

    final PagesExtractor pageIdentifierExtractor = new XslPageIdentifierExtractor(
        entityResolver, uriResolver, stylesheetPlayer ) ;

    final Opus opus = new Opus(
        resourceInstaller.getTargetDirectory(),
        resourceInstaller.getTargetDirectory(),
        Executors.newSingleThreadExecutor(),
        "insert file:" + novellaDocument.getName(),
        DefaultCharset.SOURCE,
        DefaultCharset.RENDERING,
        ImmutableSet.<Tag>of()
    ) ;

    final ImmutableMap<PageIdentifier,String > pages =
        pageIdentifierExtractor.extractPages( opus.getDocumentTree() ) ;

    assertThat( pages ).isEqualTo( expectedPages ) ;
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

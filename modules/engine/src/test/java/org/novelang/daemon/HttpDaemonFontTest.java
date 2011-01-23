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
package org.novelang.daemon;

import java.net.URL;

import org.junit.Test;
import org.novelang.ResourcesForTests;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Tests separated from the rest as they sometimes break when ran in parallel.
 * (This happened for {@link #fontListingMakesNoSmoke()} returning a valid page.)
 * This is because FOP's font cache is not thread-safe, at least with FOP-0.96.
 * Solutions:
 * <ul>
 *   <li>Find a way to configure FOP-1.0.
 *   <li>Wait for next-to-FOP-1.0 version.
 *       <a href="http://xmlgraphics.apache.org/fop/changes.html#Changes+to+the+Font+Subsystem">FOP Changelist</a>
 *       says: "Reinstated support for being able to specify a font cache filepath in the fop
 *       user configuration."
 *   <li>(Current solution.) Refactor tests to move all font-related tests in one test class and use a
 *       monothreaded {@link org.novelang.testing.junit.MethodSupport}.
 * </ul>
 *
 * @author Laurent Caillette
 */
@SuppressWarnings( { "HardcodedFileSeparator" } )
public class HttpDaemonFontTest extends AbstractHttpDaemonTest {


  @Test
  public void emptyFontListingMakesNoSmoke() throws Exception {
    setup() ;
    final byte[] generated = readAsBytes(
        new URL( "http://localhost:" + daemonPort + FontDiscoveryHandler.DOCUMENT_NAME ) ) ;
    save( "generated.pdf", generated ) ;
    final String pdfText = extractPdfText( generated ) ;
    assertThat( pdfText ).contains( "No font found." ) ;
  }

  @Test
  public void fontListingMakesNoSmoke() throws Exception {
    daemonSetupWithFonts( ResourcesForTests.FontStructure.Parent.Child.dir ) ;
    final byte[] generated = readAsBytes(
        new URL( "http://localhost:" + daemonPort + FontDiscoveryHandler.DOCUMENT_NAME ) ) ;
    save( "generated.pdf", generated ) ;
    final String pdfText = extractPdfText( generated ) ;
    assertThat( pdfText )
        .contains( ResourcesForTests.FontStructure.Parent.Child.MONO_OBLIQUE.getBaseName() )
        .contains( "There are broken fonts!" )
        .contains( ResourcesForTests.FontStructure.Parent.Child.BAD.getBaseName() )
    ;

    LOGGER.debug( "Text extracted from PDF: ", pdfText ) ;
  }


// =======
// Fixture
// =======

  private static final Logger LOGGER = LoggerFactory.getLogger( HttpDaemonFontTest.class ) ;

  private static final Object METHOD_SUPPORT_LOCK = new Object() ;

  @Override
  protected Object getMethodSupportLock() {
    return METHOD_SUPPORT_LOCK ;
  }
}

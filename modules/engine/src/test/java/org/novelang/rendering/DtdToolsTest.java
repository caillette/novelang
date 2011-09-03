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
package org.novelang.rendering;

import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ClassUtils;
import org.junit.Assert;
import org.junit.Test;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.xml.DtdTools;
import org.xml.sax.InputSource;

/**
 * Tests for {@link DtdTools}.
 *
 * @author Laurent Caillette
 */
public class DtdToolsTest {

  @Test
  public void smokeTest() throws IOException {
    final String escapedDtd = escapeEntities( HYPHEN_ENTITY ) ;
    LOGGER.info( "Escaped DTD:\n", escapedDtd ) ;
  }

  @Test
  public void checkEscape() throws IOException {
    Assert.assertEquals( HYPHEN_ENTITY_ESCAPED, escapeEntities( HYPHEN_ENTITY ) ) ;
  }

// =======
// Fixture
// =======

  private static final Logger LOGGER = LoggerFactory.getLogger( DtdToolsTest.class );

  private static final String HYPHEN_ENTITY =
      " <!ENTITY  hyphen  \"&#x2010;\"  > <!--=hyphen-->" ;
  private static final String HYPHEN_ENTITY_ESCAPED =
      " <!ENTITY hyphen \"&amp;hyphen;\" >  <!--=hyphen-->" ;

  private String escapeEntities( final String dtd ) throws IOException {
    final InputSource sourceBeforeEscaping = new InputSource( id ) ;
    sourceBeforeEscaping.setEncoding( "UTF-8" ) ;
    sourceBeforeEscaping.setPublicId( id ) ;
    sourceBeforeEscaping.setCharacterStream( new StringReader( dtd ) ) ;
    final InputSource sourceAfterEscaping = DtdTools.escapeEntities( sourceBeforeEscaping ) ;
    return IOUtils.toString( sourceAfterEscaping.getCharacterStream() ) ;
  }

  private final String id = "Novelang-" + ClassUtils.getShortClassName( getClass() ) ;

}

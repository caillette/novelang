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
package novelang.parser;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import com.google.common.base.Join;

/**
 * Tests for {@link NodeKindTools}.
 *
 * @author Laurent Caillette
 */
public class NodeKindToolsTest {

  private static final Logger LOGGER = LoggerFactory.getLogger( NodeKindToolsTest.class ) ;

  @Test
  public void renderingNames() {
    final Set< String > names = NodeKindTools.getRenderingNames() ;
    LOGGER.info( "Rendering names:\n  {}\n", Join.join( ",\n  ", names ) ) ;
    Assert.assertTrue( names.contains(
        NodeKindTools.tokenNameAsXmlElementName( NodeKind.BLOCK_INSIDE_DOUBLE_QUOTES.name() ) ) ) ;
    Assert.assertFalse( names.contains(
        NodeKindTools.tokenNameAsXmlElementName( NodeKind.DELIMITING_TEXT_.name() ) ) ) ;
  }
}

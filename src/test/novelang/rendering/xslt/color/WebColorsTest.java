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
package novelang.rendering.xslt.color;

import org.junit.Test;
import org.junit.Assert;

/**
 * Tests for {@link WebColors}.
 *
 * @author Laurent Caillette
 */
public class WebColorsTest {

  @Test
  public void cycleOverTwoColors() {
    final WebColors.InternalColorMapper colorMapper = WebColors.createColorMapper() ;
    final WebColors.WebColor color1 = colorMapper.getColor( "1" ) ;
    final WebColors.WebColor color2 = colorMapper.getColor( "2" ) ;
    final WebColors.WebColor color1_1 = colorMapper.getColor( "1" ) ;
    Assert.assertSame( color1, color1_1 ) ;
    Assert.assertNotSame( color1, color2 ) ;
  }
}

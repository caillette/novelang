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

import java.awt.Color;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.ClassUtils;
import com.google.common.base.Preconditions;

/**
 * A color with a
 * <a href="http://www.w3.org/TR/SVG/types.html#ColorKeywords" >CSS-friendly name</a>
 *
 * @author Laurent Caillette
*/
public class WebColor {
  private final String name ;
  private final Color color ;

  public WebColor( String name, Color color ) {
    Preconditions.checkArgument( ! StringUtils.isBlank( name ) ) ;
    this.name = name ;
    this.color = Preconditions.checkNotNull( color ) ;
  }

  public String getName() {
    return name;
  }

  public Color getColor() {
    return color;
  }

  public Color getInverseColor() {
    return WebColors.getInverseColor( color ) ;
  }

  public String getRgbDeclaration() {
    return WebColors.getRgbDeclaration( color ) ;
  }

  public String getInverseRgbDeclaration() {
    return WebColors.getRgbDeclaration( getInverseColor() ) ;
  }

  @Override
  public String toString() {
    return ClassUtils.getShortClassName( getClass() ) + "['" + name + "', " + color + "]" ;
  }
}

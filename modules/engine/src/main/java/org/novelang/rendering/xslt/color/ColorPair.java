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
package org.novelang.rendering.xslt.color;

import com.google.common.base.Preconditions;

/**
 * Represents a background/foreground association between color names defined in
 * {@link SvgColorsDefinition}.
 *
 * @author Laurent Caillette
 */
public class ColorPair {

  private final String background ;
  private final String foreground ;

  public ColorPair( final String background, final String foreground ) {
    Preconditions.checkArgument(
        SvgColorsDefinition.exists( background ), 
        "No such color: %s", background
    ) ;
    this.background = ( background ) ;
    Preconditions.checkArgument(
        SvgColorsDefinition.exists( foreground ),
        "No such color: %s", foreground
    ) ;
    this.foreground = foreground ;
  }

  public String getBackground() {
    return background ;
  }

  public String getForeground() {
    return foreground ;
  }

  @Override
  public String toString() {
    return "ColorPair{" +
        "background='" + background + '\'' +
        ", foreground='" + foreground + '\'' +
        '}'
    ;
  }
}

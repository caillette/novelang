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
package org.novelang.rendering.xslt;

import java.util.Iterator;

import org.novelang.rendering.xslt.color.ColorPair;
import org.novelang.rendering.xslt.color.WebColors;

/**
 * A Xalan-friendly wrapper for cycling over {@link org.novelang.rendering.xslt.color.ColorPair}s.
 *
 * @see org.novelang.rendering.xslt.color.WebColors
 *
 * @author Laurent Caillette
 */
@SuppressWarnings( { "UnusedDeclaration" } )
public class WebColor {

  private final Iterator< ColorPair > cycler ;
  private ColorPair currentPair ;

  public WebColor() {
    this.cycler = WebColors.INSTANCE.createColorCycler().iterator() ;
    currentPair = cycler.next() ;
  }

  public String background() {
    return currentPair.getBackground() ;
  }

  public String foreground() {
    return currentPair.getForeground() ;
  }

  public void next() {
    currentPair = cycler.next() ;
  }

}

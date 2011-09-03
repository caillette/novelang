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
package org.novelang.configuration;

import org.apache.fop.fonts.FontTriplet;

/**
 * Convenient handle for both a font file name and a {@link FontTriplet}.
 * 
 * @author Laurent Caillette
*/
public class FontQuadruplet {
  private final String embedFileName ;
  private final FontTriplet fontTriplet ;

  public FontQuadruplet( final String embedFileName, final FontTriplet fontTriplet ) {
    this.embedFileName = embedFileName;
    this.fontTriplet = fontTriplet;
  }

  public String getEmbedFileName() {
    return embedFileName;
  }

  public FontTriplet getFontTriplet() {
    return fontTriplet;
  }
}

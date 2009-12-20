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
package novelang.configuration;

import java.util.Map;

import org.apache.fop.fonts.EmbedFontInfo;

/**
 * Describes what default {@code FopFactory} knows about its fonts.
 *
 * @see FopTools#createGlobalFontStatus(org.apache.fop.apps.FopFactory, Iterable)
 *
 * @author Laurent Caillette
*/
public final class FopFontStatus {

  private final Iterable< EmbedFontInfo > fontInfos ;
  private final Map< String, EmbedFontInfo > failedFonts ;

  public FopFontStatus(
      final Iterable< EmbedFontInfo > fontInfos,
      final Map< String, EmbedFontInfo > failedFonts
  ) {
    this.fontInfos = fontInfos;
    this.failedFonts = failedFonts;
  }

  public Iterable< EmbedFontInfo > getFontInfos() {
    return fontInfos ;
  }

  public Map< String, EmbedFontInfo > getFailedFonts() {
    return failedFonts ;
  }
}

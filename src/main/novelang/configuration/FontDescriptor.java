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

import java.io.File;

import com.google.common.base.Objects;

/**
 * Describes a font from fonts directory.
 *
 * @author Laurent Caillette
*/
public final class FontDescriptor {

  private final String fontName ;
  private final File fontFile ;
  private final File metricsFile ;
  private final Format fontFormat ;

  public FontDescriptor(
      String fontName,
      File fontFile,
      File metricsFile,
      Format fontFormat
  ) {
    this.fontName = Objects.nonNull( fontName ) ;
    this.fontFile = Objects.nonNull( fontFile ) ;
    this.metricsFile = Objects.nonNull( metricsFile ) ;
    this.fontFormat = Objects.nonNull( fontFormat ) ;
  }

  public String getFontName() {
    return fontName ;
  }

  public File getFontFile() {
    return fontFile ;
  }

  public File getMetricsFile() {
    return metricsFile ;
  }

  public Format getFontFormat() {
    return fontFormat ;
  }

  public static enum Format {
    PLAIN,
    BOLD,
    ITALIC,
    BOLD_ITALIC
  }
}

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

import novelang.system.LogFactory;
import novelang.system.Log;

/**
 * Xalan extension for attributing colors from names.
 *
 * @see novelang.rendering.xslt.color.WebColors.WebColor
 *
 * @author Laurent Caillette
 */
public class ColorMapper {

  private static final Log LOG = LogFactory.getLog( ColorMapper.class ) ;

  private final WebColors.InternalColorMapper colorMapper = WebColors.createColorMapper() ;

  public ColorMapper() {
    LOG.debug( "Initialized." ) ;
  }

  public String getColorName( String colorIdentifier ) {
    final String colorName = colorMapper.getColor( colorIdentifier ).getName() ;
    LOG.debug( "getColorName( %s ) -> %s", colorIdentifier, colorName ) ;
    return colorName;
  }

  public String getInverseRgbDeclaration( String colorIdentifier ) {
    final WebColors.WebColor originalColor = colorMapper.getColor( colorIdentifier ) ;
    final String inverseColorDeclaration = originalColor.getInverseRgbDeclaration() ;
    LOG.debug(
        "getRgbDeclarationForInverseColor( %s ) -> %s",
        colorIdentifier,
        inverseColorDeclaration
    ) ;
    return inverseColorDeclaration ;
  }


}

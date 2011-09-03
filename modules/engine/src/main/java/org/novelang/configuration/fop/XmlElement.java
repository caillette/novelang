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
package org.novelang.configuration.fop;

/**
 * Supported XML elements in the FOP-specific stylesheet metadata section.
 *
 * @author Laurent Caillette
 */
/*package*/ enum XmlElement {
  FOP( "fop" ),
  TARGET_RESOLUTION( "target-resolution" ),
  RENDERER( "renderer" ),
  FONTS_DIRECTORY( "fonts-directory" ),
  OUTPUT_PROFILE( "output-profile" ),
  FILTER_LIST( "filterList" ),
  VALUE( "value" ) ;

  private final String localName ;

  XmlElement( final String localName ) {
    this.localName = localName ;
  }

  public String getLocalName() {
    return localName ;
  }




  /**
   * @return a possibly null object.
   */
  public static XmlElement fromLocalName( final String localName ) {
    for( final XmlElement element : values() ) {
      if( element.getLocalName().equals( localName ) ) {
        return element ;
      }
    }
    return null ;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" + localName + "}" ;
  }
}

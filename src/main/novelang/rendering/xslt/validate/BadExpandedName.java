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
package novelang.rendering.xslt.validate;

import novelang.common.Location;

/**
 * Used by {@link ExpandedNameVerifier} to report suspicious XPath expression.
 *
 * @author Laurent Caillette
*/
public class BadExpandedName {
  final Location location ;
  final String xmlPrefix ;
  final String xpath ;
  final String element ;

  public BadExpandedName( Location location, String xpath, String xmlPrefix, String element ) {
    this.location = location ;
    this.xpath = xpath ;
    this.xmlPrefix = xmlPrefix ;
    this.element = element ;
  }

  public Location getLocation() {
    return location ;
  }

  public String getXpath() {
    return xpath ;
  }

  public String getXmlPrefix() {
    return xmlPrefix;
  }

  public String getElement() {
    return element;
  }

  @Override
  public String toString() {
    return xmlPrefix + ":" + element + " at " + location ; 
  }

  public static String toString( Iterable< BadExpandedName > badExpandedNames, String indent ) {
    final StringBuffer buffer = new StringBuffer() ;
    for( BadExpandedName bad : badExpandedNames ) {
      buffer.append( indent ) ;
      buffer.append( bad.toString() ) ;
    }
    return buffer.toString() ;
  }
}

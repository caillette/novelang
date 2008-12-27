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

/**
 * Represents the combination of an XML element name and an attribute name, that cause the
 * attribute value to be a XPath expression.
 *
 * @author Laurent Caillette
*/
/*package*/ class ElementAttributeCombination {

  private final String elementName;
  private final String attributeName ;

  public ElementAttributeCombination( String elementName, String attributeName ) {
    this.elementName = elementName;
    this.attributeName = attributeName;
  }

  public String getElementName() {
    return elementName;
  }

  public String getAttributeName() {
    return attributeName;
  }

  @Override
  public boolean equals( Object o ) {
    if( this == o ) {
      return true;
    }
    if( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final ElementAttributeCombination that = ( ElementAttributeCombination ) o ;

    if( attributeName != null
      ? ! attributeName.equals( that.attributeName )
      : that.attributeName != null
    ) {
      return false;
    }
    if( elementName != null
      ? ! elementName.equals( that.elementName )
      : that.elementName != null
    ) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = elementName != null ? elementName.hashCode() : 0 ;
    result = 31 * result + ( attributeName != null ? attributeName.hashCode() : 0 ) ;
    return result ;
  }
}

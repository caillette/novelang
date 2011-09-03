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
package org.novelang.rendering.xslt.validate;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * Represents the combination of an XML element name and an attribute name, that causes the
 * attribute value to be a XPath expression inside an XSL stylesheet.
 *
 * @author Laurent Caillette
*/
/*package*/ class XpathAwareAttribute {

  public static final Set< XpathAwareAttribute > XPATH_AWARE_ATTRIBUTES = ImmutableSet.of(
      new XpathAwareAttribute( "apply-templates", "select" ),
      new XpathAwareAttribute( "copy-of", "select" ),
      new XpathAwareAttribute( "for-each", "select" ),
      new XpathAwareAttribute( "if", "test" ),
      new XpathAwareAttribute( "template", "match" ),
      new XpathAwareAttribute( "value-of", "select" ),
      new XpathAwareAttribute( "when", "test" )
  ) ;

  private final String elementName;
  private final String attributeName ;

  public XpathAwareAttribute( final String elementName, final String attributeName ) {
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
  public boolean equals( final Object o ) {
    if( this == o ) {
      return true;
    }
    if( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final XpathAwareAttribute that = ( XpathAwareAttribute ) o ;

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

  static boolean isXpathCombination( final XpathAwareAttribute xpathAwareAttribute ) {
    return XPATH_AWARE_ATTRIBUTES.contains( xpathAwareAttribute ) ;
  }
}

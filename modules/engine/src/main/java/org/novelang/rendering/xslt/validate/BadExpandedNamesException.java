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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.xml.sax.SAXException;

/**
 * Thrown when incorrect names were found in XPath expression(s).
 * 
 * @author Laurent Caillette
 */
public class BadExpandedNamesException extends SAXException {

  final Iterable< BadExpandedName > badExpandedNames ;

  public BadExpandedNamesException( final Iterable< BadExpandedName > badExpandedNames ) {
    super(
        "Incorrect XPath expression(s): unknown element name(s)" +
        BadExpandedName.toString( badExpandedNames, "\n  " )
    ) ;
    Preconditions.checkState( badExpandedNames.iterator().hasNext() ) ;
    this.badExpandedNames = ImmutableList.copyOf( badExpandedNames ) ;
  }

  public Iterable< BadExpandedName > getBadExpandedNames() {
    return badExpandedNames ;
  }


}

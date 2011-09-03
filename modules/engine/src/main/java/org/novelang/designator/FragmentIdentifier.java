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

package org.novelang.designator;

import org.apache.commons.lang.StringUtils;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Identifier for a Novella's fragment.
 * 
 * @author Laurent Caillette
 */
public class FragmentIdentifier {
  
  private final String stringRepresentation ;


  public FragmentIdentifier( final String stringRepresentation ) {
    this.stringRepresentation = checkNotNull( stringRepresentation ) ;
    checkArgument( ! StringUtils.isBlank( stringRepresentation ) ) ;
  }

  @Override
  public int hashCode() {
    return stringRepresentation.hashCode() ;
  }

  @Override
  public boolean equals( final Object o ) {
    if( o instanceof FragmentIdentifier ) {
      return stringRepresentation.equals( ( ( FragmentIdentifier ) o ).stringRepresentation ) ;  
    } else {
      return false ;
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[\\\\" + stringRepresentation + "]" ;
  }

  public String getAbsoluteRepresentation() {
    return stringRepresentation ;
  }
}

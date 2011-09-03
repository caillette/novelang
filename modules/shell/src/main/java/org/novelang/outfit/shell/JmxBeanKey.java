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

package org.novelang.outfit.shell;

import javax.management.ObjectName;

import static com.google.common.base.Preconditions.checkNotNull;

/**
* @author Laurent Caillette
*/
/*package*/ class JmxBeanKey {

  private final ObjectName objectName ;
  private final JmxKit jmxKit ;

  public JmxBeanKey( final ObjectName objectName, final JmxKit jmxKit ) {
    this.objectName = checkNotNull( objectName ) ;
    this.jmxKit = checkNotNull( jmxKit ) ;
  }

  @Override
  public boolean equals( final Object other ) {
    if( this == other ) {
        return true ;
    }
    if( other == null || getClass() != other.getClass() ) {
      return false ;
    }

    final JmxBeanKey that = ( JmxBeanKey ) other ;

    if( !jmxKit.equals( that.jmxKit ) ) {
      return false ;
    }
    if( !objectName.equals( that.objectName ) ) {
      return false ;
    }
    return true ;
  }

  @Override
  public int hashCode() {
    int result = objectName.hashCode() ;
    result = 31 * result + jmxKit.hashCode() ;
    return result ;
  }
}

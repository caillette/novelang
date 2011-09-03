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

import org.novelang.outfit.shell.JmxConnectionBundle;

import static com.google.common.base.Preconditions.checkNotNull;

/**
* @author Laurent Caillette
*/
/*package*/ class JmxBeanValue
{
  private final ObjectName objectName ;
  private final Object jmxBean;
  private final JmxConnectionBundle connectionBundle ;

  public JmxBeanValue(
      final JmxConnectionBundle connectionBundle,
      final ObjectName objectName,
      final Object jmxBean
  ) {
    this.connectionBundle = checkNotNull( connectionBundle ) ;
    this.objectName = checkNotNull( objectName ) ;
    this.jmxBean = checkNotNull( jmxBean ) ;
  }

  public ObjectName getObjectName() {
    return objectName ;
  }

  public Object getJmxBean() {
    return jmxBean ;
  }

  public JmxConnectionBundle getConnectionBundle() {
    return connectionBundle;
  }
}

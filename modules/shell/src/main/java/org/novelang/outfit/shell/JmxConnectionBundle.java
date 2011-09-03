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

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;

import static com.google.common.base.Preconditions.checkNotNull;

/**
* Keeps a {@link javax.management.remote.JMXConnector} and its {@link javax.management.MBeanServerConnection}
* together because documentation says that {@link javax.management.remote.JMXConnector#getMBeanServerConnection()}
* may or may not return always the same instance.
*/
/*package*/  public class JmxConnectionBundle
{
public final JMXConnector connector ;
public final MBeanServerConnection connection ;

public JmxConnectionBundle(
    final JMXConnector connector,
    final MBeanServerConnection connection
) {
  this.connector = checkNotNull( connector ) ;
  this.connection = checkNotNull( connection ) ;
}
}

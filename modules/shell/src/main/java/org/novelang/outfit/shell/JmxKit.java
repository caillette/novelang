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

import java.io.IOException;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;

import com.google.common.collect.ImmutableSet;

/**
 * Creates a properly-configured {@link JMXConnector}.
 *
 * @author Laurent Caillette
 */
public interface JmxKit
{
    /**
     * Creates the JMX connector.
     *
     * @return a non-null object.
     */
    JMXConnector createJmxConnector( String host, int port ) throws InterruptedException, IOException ;


}

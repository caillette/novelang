/*
 * Copyright (C) 2010 Laurent Caillette
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

import com.google.common.collect.ImmutableSet;

/**
 * Adds the feature of returning JVM system properties for initializing JMX at startup.
 *
 * @author Laurent Caillette
 */
public interface BootstrappingJmxKit extends JmxKit
{

    /**
     * Returns JMX-related JVM properties to add at startup.
     *
     * @param heartbeatMaximumPeriod may be null.
     * @return a non-null object, may be an empty list.
     */
    public abstract ImmutableSet< String > getJvmProperties( final int jmxPort, final Integer heartbeatMaximumPeriod ) ;

}

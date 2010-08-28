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
package novelang.outfit.shell.insider;

import javax.management.MXBean;
import javax.management.ObjectName;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static novelang.outfit.shell.insider.JmxTools.getObjectNameQuiet;

/**
 * A remotely-callable service. It is not called "Agent" because the agent is what's installing it.
 *
 * @author Laurent Caillette
 */
@MXBean
public interface Insider {

  long HEARTBEAT_FATAL_DELAY_MILLISECONDS = MILLISECONDS.convert( 10L, SECONDS ) ;

  /**
   * JMX stuff.
   */
  ObjectName NAME = getObjectNameQuiet( "novelang.outfit.shell.insider:type=Insider" ) ;

  /**
   * Return value for {@code System.exit()}.
   */
  int STATUS_HEARTBEAT_PERIOD_EXPIRED = -1 ;

  String MAXIMUM_HEARTBEATDELAY_PARAMETERNAME = "maximumHeartbeatDelay" ;

  void shutdown() ;

  /**
   * Call this method at intervals shorter than {@link #HEARTBEAT_FATAL_DELAY_MILLISECONDS}
   * to keep the process alive.
   */
  void keepAlive() ;

  /**
   * Does nothing, but calling this method doesn't activate keepalive so tests may use it
   * to check that remote service is alive.
   */
  boolean isAlive() ;

}

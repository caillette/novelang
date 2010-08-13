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
package novelang.system.shell.insider;

import java.util.concurrent.atomic.AtomicLong;

import static java.lang.System.currentTimeMillis;


/**
 * Runs inside the JVM launched by {@link novelang.system.shell.JavaShell}.
 * 
 * @author Laurent Caillette
 */
@SuppressWarnings( { "JavadocReference" } )
public class LocalInsider implements Insider {

  private final AtomicLong keepaliveCounter = new AtomicLong( currentTimeMillis() ) ;

  @SuppressWarnings( { "CallToThreadStartDuringObjectConstruction" } )
  public LocalInsider() {
    final long delay = HEARTBEAT_MAXIMUM_PERIOD_MILLISECONDS ;
    final Thread heartbeatReceiver = new Thread(
        new Runnable() {
          @Override
          public void run() {
            try {
              Thread.sleep( delay ) ;
              final long lag = currentTimeMillis() - keepaliveCounter.get() ;
              if( lag > delay ) {
                System.out.println( "No heartbeat for more than " + delay + " milliseconds, " +
                    "exiting with value of " + STATUS_HEARTBEAT_PERIOD_EXPIRED + "..." ) ;
                System.exit( STATUS_HEARTBEAT_PERIOD_EXPIRED ) ;
              }
            } catch( InterruptedException ignore ) { }
          }
        }
        ,getClass().getSimpleName() + "-HeartbeatReceiver"
    ) ;
    heartbeatReceiver.setDaemon( true ) ;
    heartbeatReceiver.start() ;
  }

  /**
   * Performs shutdown in a separate thread to let the {@link #shutdown()} method return.
   */
  @Override
  public void shutdown() {
    new Thread(
        new Runnable() {
          public void run() {
            System.exit( 0 ) ;
          }
        },
        LocalInsider.class.getSimpleName()
    ).start() ;
  }

  @Override
  public void keepAlive() {
    keepaliveCounter.set( currentTimeMillis() ) ;
  }

}

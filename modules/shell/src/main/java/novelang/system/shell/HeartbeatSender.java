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
package novelang.system.shell;

import novelang.system.shell.insider.Insider;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Calls {@link novelang.system.shell.insider.Insider#keepAlive()} at repeated intervals
 * until {@link #stop()}.
 *
 * @author Laurent Caillette
 */
/*package*/ class HeartbeatSender {

  private final Thread thread ;

  public HeartbeatSender( final Insider insider, final String processNickname ) {
    this( insider, processNickname, Insider.HEARTBEAT_MAXIMUM_PERIOD_MILLISECONDS / 10L ) ;
  }


  @SuppressWarnings( { "CallToThreadStartDuringObjectConstruction" } )
  public HeartbeatSender(
      final Insider insider,
      final String processNickname,
      final long heartbeatPeriodMilliseconds
  ) {
    checkNotNull( insider ) ;
    checkArgument( heartbeatPeriodMilliseconds > 0L ) ;

    thread = new Thread(
        new Runnable() {
          @Override
          public void run() {
            while( true ) {
              try {
                Thread.sleep( heartbeatPeriodMilliseconds ) ;
              } catch( InterruptedException e ) {
                break ;
              }
              insider.keepAlive() ;
            }
          }
        },
        "Heartbeat-" + processNickname
    ) ;
    thread.setDaemon( true ) ;
    thread.start() ;
  }

  public void stop() {
    if( thread.isInterrupted() ) {
      throw new IllegalStateException( "Should not happen: thread already interrupted" ) ;
    }
    thread.interrupt() ;
  }


}

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

import java.lang.reflect.UndeclaredThrowableException;
import java.rmi.ConnectException;

import novelang.system.Log;
import novelang.system.LogFactory;
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

  private static final Log LOG = LogFactory.getLog( HeartbeatSender.class ) ;

  private final Thread thread ;

  public HeartbeatSender(
      final Insider insider,
      final Notifiee notifiee,
      final String processNickname
  ) {
    this( insider, notifiee, processNickname, Insider.HEARTBEAT_FATAL_DELAY_MILLISECONDS / 10L ) ;
  }


  @SuppressWarnings( { "CallToThreadStartDuringObjectConstruction" } )
  public HeartbeatSender(
      final Insider insider,
      final Notifiee notifiee,
      final String processNickname,
      final long heartbeatPeriodMilliseconds
  ) {
    checkNotNull( insider ) ;
    checkArgument( heartbeatPeriodMilliseconds > 0L ) ;

    LOG.debug( "Initializing for " + processNickname
        + " with a heartbeat period of " + heartbeatPeriodMilliseconds + " milliseconds..." ) ;

    final Runnable runnable = new Runnable() {
      @Override
      public void run() {
/*
        LOG.info( "Running " + Thread.currentThread().getName()
            + "for " + processNickname
            + " with a heartbeat of " + heartbeatPeriodMilliseconds + " milliseconds..."
        ) ;
*/
        while( true ) {
          try {
            Thread.sleep( heartbeatPeriodMilliseconds ) ;
          } catch( InterruptedException e ) {
            break;
          }
          if( Thread.currentThread().isInterrupted() ) {
            // Exit from the loop if thread interruption occured while we're not sleeping.
            break ;
          }
          try {
            insider.keepAlive();
          } catch( UndeclaredThrowableException e ) {
/*
            if( e.getCause() instanceof ConnectException ) {
              LOG.debug( "Could not send heartbeat to " + processNickname + "." );
            } else {
              LOG.error( "Could not send heartbeat to " + processNickname + ".", e );
            }
*/
            break ;
          }
        }
        notifiee.onUnreachableProcess() ;
      }
    } ;
    thread = new Thread( runnable, "Heartbeat-" + processNickname ) ;
    thread.setDaemon( true ) ;
    thread.start() ;
  }

  public void stop() {
    if( thread.isInterrupted() ) {
      throw new IllegalStateException( "Thread already interrupted" ) ;
    }
    thread.interrupt() ;
  }


  public interface Notifiee {
    void onUnreachableProcess() ;
  }


}

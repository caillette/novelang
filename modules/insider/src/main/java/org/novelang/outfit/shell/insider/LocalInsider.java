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
package org.novelang.outfit.shell.insider;

import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.System.currentTimeMillis;


/**
 * Runs inside the JVM launched by {@link org.novelang.outfit.shell.JavaShell}.
 * 
 * @author Laurent Caillette
 */
@SuppressWarnings( { "JavadocReference", "UseOfSystemOutOrSystemErr" } )
public class LocalInsider implements Insider {

  private final AtomicLong keepaliveCounter ;
  private final String virtualMachineName ;
  private final Thread heartbeatReceiver ;

  public LocalInsider() {
    this( HEARTBEAT_FATAL_DELAY_MILLISECONDS ) ;
  }


  @SuppressWarnings( { "CallToThreadStartDuringObjectConstruction" } )
  public LocalInsider( final long delay ) {
    virtualMachineName = ManagementFactory.getRuntimeMXBean().getName() ;

    printStandard( "Initializing " + getClass().getSimpleName() + " "
        + "with: "
        + "virtualMachineName=" + virtualMachineName + ", "
        + "fatalHeartbeatDelay=" + delay + " milliseconds..."
    ) ;

    keepaliveCounter = new AtomicLong( currentTimeMillis() ) ;

    heartbeatReceiver = new Thread(
        new Runnable() {
          @Override
          public void run() {
            printStandard(
                "Started keepalive watcher from thread " + Thread.currentThread()
                + " inside " + virtualMachineName + "."
            ) ;

            // Reset the counter for synchronization with thread start time.
            keepAlive() ;

            while( true ) {
              try {
                Thread.sleep( delay ) ;
                final long lag = currentTimeMillis() - keepaliveCounter.get() ;
                if( lag > delay ) {
                  printError(
                      "No heartbeat for more than " + delay + " milliseconds, "
                      + "halting " + virtualMachineName + " with status of "
                      + STATUS_HEARTBEAT_PERIOD_EXPIRED + "."
                  ) ;
                  Runtime.getRuntime().halt( STATUS_HEARTBEAT_PERIOD_EXPIRED ) ;
                  break ; // Avoid a compilation warning because of infinite loop.
                }
              } catch( InterruptedException ignore ) { }
            }
          }
        }
        ,getClass().getSimpleName() + "-HeartbeatReceiver"
    );
    heartbeatReceiver.setDaemon( true ) ;

  }

  /**
   * Call this method only after JMX registration happened. Otherwise there can be timing problems,
   * especially when running tests in parallel.
   */
  @Override
  public void startWatchingKeepalive() {
    heartbeatReceiver.start() ;
  }


  /**
   * Shortcut method for getting value returned by
   * {@link java.lang.management.RuntimeMXBean#getName()}.
   */
  protected final String getVirtualMachineName() {
    return virtualMachineName ;
  }

  /**
   * Performs shutdown in a separate thread to let the {@link #shutdown()} method return.
   */
  @Override
  public void shutdown() {
    heartbeatReceiver.interrupt() ; // Might avoid messy error messages in extreme cases.
    new Thread(
        new Runnable() {
          @Override
          public void run() {
            System.exit( 0 ) ;
          }
        },
        LocalInsider.class.getSimpleName()
    ).start() ;
  }

  @Override
  public void keepAlive() {
//    printOut( "Received keepalive call at " + currentTimeMillis() + "." ) ;
    keepaliveCounter.set( currentTimeMillis() ) ;
  }

  @Override
  public boolean isAlive() {
    return true ;
  }

  @Override
  public void printStandard( final String message ) {
    System.out.println( createTimestamp() + message ) ;
    System.out.flush() ;
  }

  @Override
  public void printError( final String message ) {
    System.out.println( createTimestamp() + message ) ;
    System.err.flush() ;
  }

  private static String createTimestamp() {
//    return new SimpleDateFormat( "HH:mm:ss,SSS " ).format( new Date() );
    return "" ;
  }
}

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

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
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
  private final String virtualMachineName ;

  public LocalInsider() {
    this( HEARTBEAT_FATAL_DELAY_MILLISECONDS ) ;
  }


  @SuppressWarnings( { "CallToThreadStartDuringObjectConstruction" } )
  public LocalInsider( final long delay ) {
    virtualMachineName = ManagementFactory.getRuntimeMXBean().getName() ;

    printOut( "Initializing " + getClass().getSimpleName() + " "
        + "from thread " + Thread.currentThread() + " "
        + "with: "
        + "virtualMachineName=" + virtualMachineName + ", "
        + "fatalHeartbeatDelay=" + delay + " milliseconds..."
    ) ;

    final Thread heartbeatReceiver = new Thread(
        new Runnable() {
          @Override
          public void run() {
            while( true ) {
              try {
//                printOut(
//                    "Started keepalive watcher from thread " + Thread.currentThread() + "." ) ;
                Thread.sleep( delay ) ;
                final long lag = currentTimeMillis() - keepaliveCounter.get() ;
                if( lag > delay ) {
                  printErr( "No heartbeat for more than " + delay + " milliseconds, " +
                      "halting with status of " + STATUS_HEARTBEAT_PERIOD_EXPIRED + "." ) ;
                  Runtime.getRuntime().halt( STATUS_HEARTBEAT_PERIOD_EXPIRED ) ;
                  break ; // Avoid a compilation warning because of infinite loop.
                }
              } catch( InterruptedException ignore ) { }
            }
          }
        }
        ,getClass().getSimpleName() + "-HeartbeatReceiver"
    ) ;
    heartbeatReceiver.setDaemon( true ) ;
    heartbeatReceiver.start() ;
    
    printOut( "Started " + getClass().getName() + "." ) ;
  }

  /**
   * Performs shutdown in a separate thread to let the {@link #shutdown()} method return.
   */
  @Override
  public void shutdown() {
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
  public String getVirtualMachineName() {
    return virtualMachineName ;
  }

  private static void printOut( final String message ) {
    System.out.println( createTimestamp() + message ) ;
    System.out.flush() ;
  }

  private static void printErr( final String message ) {
    System.out.println( createTimestamp() + message ) ;
    System.err.flush() ;
  }

  private static String createTimestamp() {
//    return new SimpleDateFormat( "HH:mm:ss,SSS " ).format( new Date() );
    return "" ;
  }
}

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
package novelang.nhovestone;

import com.google.common.base.Predicate;
import novelang.system.Log;
import novelang.system.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Starts and stops a {@link Process}, watching its standard and error outputs.
 *
 * Unfortunately the {@link Process} doesn't tell about OS-dependant PID.
 * There is no chance to kill spawned processes if the VM running {@link ProcessDriver}
 * crashes.
 *
 * See good discussion
 * <a href="http://blog.igorminar.com/2007/03/how-java-application-can-discover-its.html">here</a>.
 *
 * @author Laurent Caillette
 */
public class ProcessDriver {

  private static final Log LOG = LogFactory.getLog( ProcessDriver.class ) ;

  private final File workingDirectory ;
  private final List< String > processArguments ;
  private final Predicate< String > startupSensor ;

  private final ThreadGroup threadGroup ;
  private Thread standardStreamWatcherThread = null ;
  private Thread errorStreamWatcherThread = null ;
  private Process process = null ;


  public ProcessDriver(
      final File workingDirectory,
      final String displayName,
      final List< String > processArguments,
      final Predicate< String > startupSensor
  ) {
    this.workingDirectory = workingDirectory ;
    this.processArguments = processArguments ;
    this.startupSensor = startupSensor ;
    threadGroup = new ThreadGroup( getClass().getSimpleName() + "-" + displayName ) ;
  }


  public void start( final long timeout, final TimeUnit timeUnit )
      throws
      IOException, 
      InterruptedException,
      ProcessCreationFailedException
  {
    final Semaphore startupSemaphore = new Semaphore( 0 ) ;
    final String processAsString ;

    LOG.info( "Starting process in directory '" + workingDirectory.getAbsolutePath() + "'" ) ;
    LOG.info( "Arguments: " + processArguments ) ;

    synchronized( stateLock ) {

      ensureInState( State.READY ) ;
      process = new ProcessBuilder()
          .command( processArguments )
          .directory( workingDirectory )
          .start()
      ;

      standardStreamWatcherThread = new Thread(
          threadGroup,
          createStandardOutputWatcher( process.getInputStream(), startupSemaphore ),
          "standardWatcherThread"
      ) ;

      errorStreamWatcherThread = new Thread(
          threadGroup,
          createErrorOutputWatcher( process.getErrorStream() ),
          "errorWatcherThread"
      ) ;

      standardStreamWatcherThread.setDaemon( true ) ;
      standardStreamWatcherThread.start() ;
      errorStreamWatcherThread.setDaemon( true ) ;
      errorStreamWatcherThread.start() ;

      startupSemaphore.tryAcquire( 1, timeout, timeUnit ) ;

      if( state == State.BROKEN ) {
        throw new ProcessCreationFailedException() ;
      } else {
        state = State.RUNNING ;
      }
      processAsString = process.toString() ;
    }

    LOG.info( "Successfully started " + processAsString ) ;
  }


  private InputStreamWatcher createStandardOutputWatcher(
      final InputStream standardOutput,
      final Semaphore startupSemaphore
  ) {
    return new InputStreamWatcher( standardOutput ) {
      @Override
      protected void interpretLine( final String line ) {
        if( line != null ) {
          LOG.debug( "Standard output from supervised process: >>> " + line ) ;
        }
        if( startupSemaphore.availablePermits() == 0 && startupSensor.apply( line ) ) {
          startupSemaphore.release() ;
        }
      }

      @Override
      protected void handleThrowable( final Throwable throwable ) {
        handleThrowableFromProcess( throwable ) ;
      }
    } ;
  }


  private InputStreamWatcher createErrorOutputWatcher( final InputStream standardError ) {
    return new InputStreamWatcher( standardError ) {
      @Override
      protected void interpretLine( final String line ) {
        if( line != null ) {
          LOG.warn( "Error from supervised process: >>> " + line ) ;
        }
      }

      @Override
      protected void handleThrowable( final Throwable throwable ) {
        handleThrowableFromProcess( throwable ) ;
      }
    } ;
  }


  private void handleThrowableFromProcess( final Throwable throwable ) {
    final boolean shouldLog ;
    synchronized( stateLock ) {
      if( state != State.SHUTTINGDOWN && state != State.TERMINATED ) {
        state = State.BROKEN ;
        shouldLog = false ;
      } else {
        shouldLog = true ;
      }
    }
    if( shouldLog ) {
      LOG.error( "Throwable caught while reading supervised process stream", throwable ) ;
    }
  }


  public static class ProcessCreationFailedException extends Exception { }


  public void shutdown( final boolean force ) throws InterruptedException {
    synchronized( stateLock ) {
      try {
        if( state != State.BROKEN ) {
          ensureInState( State.RUNNING ) ;
          state = State.SHUTTINGDOWN ;
          if( force ) {
            interruptWatcherThreads() ;
            process.destroy() ;
          } else {
            process.waitFor() ;
            interruptWatcherThreads() ;
          }
        }
      } finally {
        process = null ;
        standardStreamWatcherThread = null ;
        errorStreamWatcherThread = null ;
        state = State.TERMINATED ;
      }
    }
    LOG.info( "Process ended." ) ;

  }

  private void interruptWatcherThreads() {
    standardStreamWatcherThread.interrupt() ;
    errorStreamWatcherThread.interrupt() ;
  }


  private static abstract class InputStreamWatcher implements Runnable {

    private final BufferedReader reader ;

    @SuppressWarnings( { "IOResourceOpenedButNotSafelyClosed" } )
    protected InputStreamWatcher( final InputStream stream ) {
      this.reader = new BufferedReader( new InputStreamReader( stream ) ) ;
    }

    public final void run() {
      try {
        while( ! Thread.currentThread().isInterrupted() ) {
          try {
            // Tried to read in a buffer manually, doesn't get more chars, just shows
            // the logging system flushes to the console too lazily.
            final String line = reader.readLine() ;
            interpretLine( line ) ;
          } catch( Throwable t ) {
            if( ! Thread.currentThread().isInterrupted() ) { // Double-check, may have changed.
              handleThrowable( t ) ;
            }
            break ;
          }
        }
      } finally {
        try {
          cleanup() ;
        } catch( IOException e ) {
          handleThrowable( e ) ;
        }
      }
    }

    protected abstract void interpretLine( final String line ) ;

    protected abstract void handleThrowable( final Throwable throwable ) ;

    public void cleanup() throws IOException {
      reader.close() ; // Not especially useful I guess.
    }
  }


  private final Object stateLock = new Object() ;

  private State state = State.READY ;

  /**
   * Synchronization left to caller.
   */
  private void ensureInState( final State expected ) {
    if( state != expected ) {
      throw new IllegalStateException(
          "Expected to be in state " + expected + " but was in " + state ) ;
    }
  }


  private enum State { READY, RUNNING, BROKEN, SHUTTINGDOWN, TERMINATED }

}

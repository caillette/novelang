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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import novelang.system.Log;
import novelang.system.LogFactory;
import org.apache.commons.lang.StringUtils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Starts and stops a {@link Process}, watching its standard and error outputs.
 *
 * Unfortunately the {@link Process} doesn't tell about OS-dependant PID.
 * There is no chance to kill spawned processes if the VM running {@link Shell}
 * crashes.
 *
 * See good discussion
 * <a href="http://blog.igorminar.com/2007/03/how-java-application-can-discover-its.html">here</a>.
 *
 * @author Laurent Caillette
 */
public abstract class Shell {

  private static final Log LOG = LogFactory.getLog( Shell.class ) ;

  private final File workingDirectory ;
  private final List< String > processArguments ;
  private final Predicate< String > startupSensor ;

  private final String nickname;
  private final ThreadGroup threadGroup ;
  private Thread standardStreamWatcherThread = null ;
  private Thread errorStreamWatcherThread = null ;
  private Process process = null ;

  private static final ImmutableList< String > NO_PARAMETERS = ImmutableList.of() ;


  protected Shell(
      final File workingDirectory,
      final String nickname,
      final List< String > processArguments,
      final Predicate< String > startupSensor
  ) {
    checkArgument( workingDirectory.isDirectory() ) ;
    this.workingDirectory = workingDirectory ;
    this.processArguments = processArguments == null ? NO_PARAMETERS : processArguments ;
    this.startupSensor = checkNotNull( startupSensor ) ;
    checkArgument( ! StringUtils.isBlank( nickname ) ) ;
    this.nickname = nickname ;
    threadGroup = new ThreadGroup( getClass().getSimpleName() + "-" + nickname ) ;
  }

  public final String getNickname() {
    return nickname;
  }

  public void start( final long timeout, final TimeUnit timeUnit )
      throws
      IOException, 
      InterruptedException,
      ProcessCreationFailedException
  {
    final Semaphore startupSemaphore = new Semaphore( 0 ) ;

    LOG.info( nickname + " starting process in directory '"
        + workingDirectory.getAbsolutePath() + "'" ) ;
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
    }

    LOG.info( "Successfully started " + nickname + "." ) ;
  }


  private InputStreamWatcher createStandardOutputWatcher(
      final InputStream standardOutput,
      final Semaphore startupSemaphore
  ) {
    return new InputStreamWatcher( standardOutput ) {
      @Override
      protected void interpretLine( final String line ) {
        if( line != null ) {
          LOG.debug( "Standard output from supervised process in " + nickname + ": >>> " + line ) ;
          if( startupSemaphore.availablePermits() == 0 && startupSensor.apply( line ) ) {
            startupSemaphore.release() ;
          }
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
          LOG.warn( "Error from " + nickname + ": >>> " + line ) ;
        }
      }

      @Override
      protected void handleThrowable( final Throwable throwable ) {
        handleThrowableFromProcess( throwable ) ;
      }
    } ;
  }


  private void handleThrowableFromProcess( final Throwable throwable ) {
    synchronized( stateLock ) {
      if( state != State.SHUTTINGDOWN && state != State.TERMINATED ) {
        state = State.BROKEN ;
      }
    }
      LOG.error(
          "Throwable caught while reading supervised process stream in " + nickname,
          throwable
      ) ;
  }


  public static class ProcessCreationFailedException extends Exception { }


  protected final Integer shutdownProcess( final boolean force ) throws InterruptedException {
    Integer exitCode = null ;
    synchronized( stateLock ) {
      try {
        if( state == State.RUNNING ) {
          state = State.SHUTTINGDOWN ;
          if( force ) {
            interruptWatcherThreads() ;
            process.destroy() ;
          } else {
            exitCode = process.waitFor();
            interruptWatcherThreads() ;
          }
        } else {
            LOG.warn( "Trying to shutdown while in " + state + " state for " + nickname + "." ) ;
        }
      } finally {
        process = null ;
        standardStreamWatcherThread = null ;
        errorStreamWatcherThread = null ;
        state = State.TERMINATED ;
      }
    }
    LOG.info( "Process ended for %s, returning %s", nickname, exitCode ) ;
    return exitCode ;
  }

  private void interruptWatcherThreads() {
    standardStreamWatcherThread.interrupt() ;
    errorStreamWatcherThread.interrupt() ;
  }


  private final Object stateLock = new Object() ;

  private State state = State.READY ;

  /**
   * Synchronization left to caller.
   */
  private void ensureInState( final State expected, final State... otherExpected ) {
    if( state != expected ) {
        for( final State other : otherExpected )
        {
            if( state == other )
            {
                return ;
            }
        }

      throw new IllegalStateException(
          "Expected to be in state " + expected + " but was in " + state ) ;
    }
  }


  private enum State { READY, RUNNING, BROKEN, SHUTTINGDOWN, TERMINATED }

}

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
package org.novelang.logger;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import static com.google.common.base.Preconditions.checkState;

/**
 * Keeps log lines in memory until a call to {@link #flush(LoggerFactory)} or the JVM exits.
 *
 * @author Laurent Caillette
 */
/*package*/ class DeferringLoggerFactory extends LoggerFactory {

  /**
   * Synchronize everything on this one. Might be suboptimal but who cares?
   */
  private static final Object LOCK = new Object() ;

  private static final List< NamedLogRecord > LOG_RECORDS = Lists.newArrayList() ;

  private static final Map< String, RecordingLogger > LOGGERS = Maps.newHashMap() ;

  @SuppressWarnings( { "StaticNonFinalField" } )
  private static boolean open = true ;

  @Override
  protected Logger doGetLogger( final String name ) {
    synchronized( LOCK ) {
      checkState( open ) ;
      final RecordingLogger existing = LOGGERS.get( name ) ;
      if( existing == null ) {
        final RecordingLogger newRecordingLogger = new RecordingLogger( name ) ;
        LOGGERS.put( name, newRecordingLogger ) ;
        return newRecordingLogger ;
      } else {
        return existing ;
      }
    }
  }

  /**
   * Call this only once.
   *
   * @param loggerFactory a non-null object. The {@link Logger} instances it provides must
   *         be instances of {@link org.novelang.logger.AbstractLogger}.
   */
  @SuppressWarnings( { "ThrowableResultOfMethodCallIgnored" } )
  public static void flush( final LoggerFactory loggerFactory ) {
    synchronized( LOCK ) {
      checkState( open ) ;
      for( final NamedLogRecord logRecord : LOG_RECORDS ) {
        final AbstractLogger sink = ( AbstractLogger )
            loggerFactory.doGetLogger( logRecord.getLoggerName() ) ;
        sink.log( logRecord.getLevel(), logRecord.getMessage(), logRecord.getThrowable() ) ;
      }
      LOGGERS.clear() ;
      LOG_RECORDS.clear() ;
      open = false ;
    }
  }

  static {
    Runtime.getRuntime().addShutdownHook( new Thread( new Runnable() {
      @Override
      public void run() {
        shutdownFlush() ;
      }
    }, "" ) ) ;
  }

  @SuppressWarnings( { "UseOfSystemOutOrSystemErr", "ThrowableResultOfMethodCallIgnored" } )
  private static void shutdownFlush() {
    synchronized( LOCK ) {
      if( open ) {
        final PrintStream printStream = System.err ;
        printStream.println( "Flushing in-memory log..." ) ;
        for( final NamedLogRecord logRecord : LOG_RECORDS ) {
          final StringBuilder logLineBuilder = new StringBuilder() ;
          logLineBuilder.append( String.format( "%-5s", logRecord.getLevel() ) ) ;
          logLineBuilder.append( " [" ) ;
          logLineBuilder.append( logRecord.getThreadName() ) ;
          logLineBuilder.append( "] " ) ;
          logLineBuilder.append( logRecord.getLoggerName() ) ;
          logLineBuilder.append( " - " ) ;
          logLineBuilder.append( logRecord.getMessage() ) ;
          if( logRecord.hasThrowable() ) {
            logLineBuilder.append( " " ) ;
            logLineBuilder.append( logRecord.getThrowable().toString() ) ;
          }
          printStream.println( logLineBuilder.toString() ) ;
        }
      }
    }
  }


  private static class RecordingLogger extends AbstractLogger {

    private final String loggerName ;

    private RecordingLogger( final String loggerName ) {
      this.loggerName = loggerName ;
    }

    @Override
    protected void log( final Level level, final String message, final Throwable throwable ) {
      synchronized( LOCK ) {
        final AbstractLogger maybeDelegate = ( AbstractLogger ) LoggerFactory.getLogger( loggerName );
        if( maybeDelegate == this ) {
          // Still using this factory.
          checkState( open ) ;
          LOG_RECORDS.add( new NamedLogRecord(
              loggerName, Thread.currentThread().getName(), level, message, throwable ) ) ;
        } else {
          // Switched to another factory.
          maybeDelegate.log( level, message, throwable ) ;
        }
      }
    }

    @Override
    public String getName() {
      return loggerName ;
    }

    @Override
    public boolean isTraceEnabled() {
      return true ;
    }

    @Override
    public boolean isDebugEnabled() {
      return true ;
    }

    @Override
    public boolean isInfoEnabled() {
      return true ;
    }
  }

}

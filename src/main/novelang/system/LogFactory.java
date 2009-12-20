/*
 * Copyright (C) 2009 Laurent Caillette
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
package novelang.system;

import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import com.google.common.collect.MapMaker;

/**
 * Thin wrapper around {@link LogFactory}.
 * {@code Logger} instances are kept in a weak hashmap and access is synchronized.
 * This should cause no performance problem, as most of {@code Log} instances are static members
 * so lookup happens only at initialization time.
 *
 * @author Laurent Caillette
 */
public class LogFactory {

  private static final Map< String, Log > LOGS = new MapMaker().weakKeys().makeMap() ;

  public static Log getLog( final String name ) {
    Log log ;
    synchronized( LOGS ) {
      log = LOGS.get( name ) ;
      if( null == log ) {
        final Logger logger = LoggerFactory.getLogger( name ) ;
        log = new LoggerWrapper( logger ) ;
        LOGS.put( name, log ) ;
      }
    }
    return log ;
  }

  public static Log getLog( final Class aClass ) {
    return getLog( aClass.getName() ) ;
  }

  private static class LoggerWrapper implements Log {

    private final Logger logger ;

    private LoggerWrapper( final Logger logger ) {
      this.logger = logger ;
    }

    public String getName() {
      return logger.getName() ;
    }

    public boolean isTraceEnabled() {
      return logger.isTraceEnabled() ;
    }

    public void trace( final String s ) {
      logger.trace( s ) ;
    }

    public void trace( final String s, final Object... o ) {
      logger.trace( s, o ) ;
    }

    public void trace( final String s, final Throwable throwable ) {
      logger.trace( s, throwable ) ;
    }

    public boolean isDebugEnabled() {
      return logger.isDebugEnabled() ;
    }

    public void debug( final String s ) {
      logger.debug( s ) ;
    }

    public void debug( final String s, final Object... o ) {
      logger.debug( String.format( s, o ) ) ;
    }

    public void debug( final String s, final Throwable throwable ) {
      logger.debug( s, throwable ) ;
    }

    public boolean isInfoEnabled() {
      return logger.isInfoEnabled() ;
    }

    public void info( final String s ) {
      logger.info( s ) ;
    }

    public void info( final String s, final Object... o ) {
      logger.info( String.format( s, o ) ) ;
    }

    public void info( final String s, final Throwable throwable ) {
      logger.info( s, throwable ) ;
    }

    public boolean isWarnEnabled() {
      return logger.isWarnEnabled() ;
    }

    public void warn( final String s ) {
      logger.warn( s ) ;
    }

    public void warn( final String s, final Object... o ) {
      logger.warn( String.format( s, o ) ) ;
    }

    public void warn( final String s, final Throwable throwable ) {
      logger.warn( s, throwable ) ;
    }

    public boolean isErrorEnabled() {
      return logger.isErrorEnabled() ;
    }

    public void error( final String s ) {
      logger.error( s ) ;
    }

    public void error( final String s, final Object... o ) {
      logger.error( String.format( s, o ) ) ;
    }

    public void error( final String s, final Throwable throwable ) {
      logger.error( s, throwable ) ;
    }



  }

}

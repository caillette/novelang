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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thin wrapper around {@link LogFactory}.
 * The wrapping of {@link org.apache.maven.plugin.logging.Log} is for code running as a Maven
 * plugin. There is some singleton ugliness here but I couldn't figure a better approach.
 *
 * @deprecated use {@link novelang.logger.LoggerFactory}.
 *
 * @author Laurent Caillette
 */
public class LogFactory {

  private static final Object LOCK = new Object() ;

  @SuppressWarnings( { "StaticNonFinalField" } )
  private static org.apache.maven.plugin.logging.Log mavenPluginLog;

  public static void setMavenPluginLog( final org.apache.maven.plugin.logging.Log log ) {
    synchronized( LOCK ) {
      mavenPluginLog = log ;
    }
  }

  public static novelang.system.Log getLog( final String name ) {
    synchronized( LOCK ) {
      if( mavenPluginLog == null ) {
          final Logger logger = LoggerFactory.getLogger( name ) ;
          return new Slf4jLoggerWrapper( logger ) ;
      } else {
        return new MojoLogWrapper( mavenPluginLog ) ;
      }
    }
  }

  public static novelang.system.Log getLog( final Class aClass ) {
    return getLog( aClass.getName() ) ;
  }

  private static class Slf4jLoggerWrapper implements novelang.system.Log {

    private final Logger logger ;

    private Slf4jLoggerWrapper( final Logger logger ) {
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

  private static final class MojoLogWrapper implements novelang.system.Log {

    private final org.apache.maven.plugin.logging.Log log ;

    public MojoLogWrapper( final org.apache.maven.plugin.logging.Log log ) {
      this.log = log ;
    }

    public String getName() {
      return log.toString() ;
    }

    public boolean isTraceEnabled() {
      return log.isDebugEnabled() ;
    }

    public void trace( final String s ) {
      log.debug( s ) ;
    }

    public void trace( final String s, final Object... o ) {
      log.debug( String.format( s, o ) ) ;
    }

    public void trace( final String s, final Throwable throwable ) {
      log.debug( s, throwable ) ;
    }

    public boolean isDebugEnabled() {
      return log.isDebugEnabled() ;
    }

    public void debug( final String s ) {
      log.debug( s ) ;
    }

    public void debug( final String s, final Object... o ) {
      log.debug( String.format( s, o ) ) ;
    }

    public void debug( final String s, final Throwable throwable ) {
      log.debug( s, throwable ) ;
    }

    public boolean isInfoEnabled() {
      return log.isInfoEnabled() ;
    }

    public void info( final String s ) {
      log.info( s ) ;
    }

    public void info( final String s, final Object... o ) {
      log.info( String.format( s, o ) ) ;
    }

    public void info( final String s, final Throwable throwable ) {
      log.info( s, throwable ) ;
    }

    public boolean isWarnEnabled() {
      return log.isWarnEnabled() ;
    }

    public void warn( final String s ) {
      log.warn( s ) ;
    }

    public void warn( final String s, final Object... o ) {
      log.warn( String.format( s, o ) ) ;
    }

    public void warn( final String s, final Throwable throwable ) {
      log.warn( s, throwable ) ;
    }



    public boolean isErrorEnabled() {
      return log.isErrorEnabled() ;
    }

    public void error( final String s ) {
      log.error( s ) ;

    }

    public void error( final String s, final Object... o ) {
      log.error( String.format( s, o ) ) ;
    }

    public void error( final String s, final Throwable throwable ) {
      log.error( s, throwable ) ;
    }
  }

}

/*
 * Copyright (C) 2010 Laurent Caillette
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

import static com.google.common.base.Preconditions.checkState;

/**
 * The API for obtaining instances of {@link Logger} object.
 * <p>
 * This class delegates to a {@value #CONCRETE_LOGGER_FACTORY_CLASS_NAME} class, giving
 * pluggable implementation ability.
 * <p>
 *
 * When not in a test environment (as reported by {@link #isTestEnvironment()}, provided
 * loggers log in memory until the call to {@link #configurationComplete()} occurs.
 *
 * @author Laurent Caillette
 */
public abstract class LoggerFactory {

  private static final boolean TESTING ;
  static {
    @SuppressWarnings( { "ThrowableInstanceNeverThrown" } )
    final StackTraceElement[] stackTraceElements = new Exception().getStackTrace() ;
    boolean junitPresent = false ;
    for( final StackTraceElement stackTraceElement : stackTraceElements ) {
      final String className = stackTraceElement.getClassName();
      if( className.startsWith( "org.junit" ) || className.startsWith( "com.intellij.junit4" ) ) {
        junitPresent = true ;
        break ;
      }
    }
    TESTING = junitPresent ;
  }
  @SuppressWarnings( { "StaticNonFinalField" } )
  private static LoggerFactory loggerFactory ;
  static {
    if( isTestEnvironment() ) {
      loggerFactory = createEffectiveLoggerFactory() ;
    } else {
      loggerFactory = new DeferringLoggerFactory() ;
    }
  }


  public static Logger getLogger( final Class someClass ) {
    return getLogger( someClass.getName() ) ;
  }

  public static Logger getLogger( final String name ) {
    synchronized( LOCK ) {
      if( isTestEnvironment() ) {
        return new HookableLogger( loggerFactory.doGetLogger( name ) ) ;
      } else {
        return loggerFactory.doGetLogger( name ) ;
      }
    }
  }

  /**
   * Switches from {@link org.novelang.logger.DeferringLoggerFactory} to some
   * more interesting implementation.
   *
   * @throws IllegalStateException if {@link #isTestEnvironment()} or if already called.
   */
  public static void configurationComplete() {
    synchronized( LOCK ) {
      checkState( loggerFactory instanceof DeferringLoggerFactory ) ;
      final LoggerFactory effectiveLoggerFactory = createEffectiveLoggerFactory() ;
      DeferringLoggerFactory.flush( effectiveLoggerFactory ) ;
      loggerFactory = effectiveLoggerFactory ;
    }
  }

  protected abstract Logger doGetLogger( String name ) ;


// =========
// Internals
// =========


  private static final Object LOCK = new Object() ;

  private static final String CONCRETE_LOGGER_FACTORY_CLASS_NAME =
      "org.novelang.logger.ConcreteLoggerFactory";


  private static LoggerFactory createEffectiveLoggerFactory() {
    try {
      return ( LoggerFactory ) Class.forName( CONCRETE_LOGGER_FACTORY_CLASS_NAME ).newInstance() ;
    } catch( ClassNotFoundException e ) {
      throw new RuntimeException( e ) ;
    } catch( InstantiationException e ) {
      throw new RuntimeException( e ) ;
    } catch( IllegalAccessException e ) {
      throw new RuntimeException( e ) ;
    }
  }



  /**
   * Returns true when running as JUnit test.
   * When true, every {@link Logger} instance returned by {@link #getLogger(String)} is an instance
   * of {@link org.novelang.logger.HookableLogger}.
   */
  public static boolean isTestEnvironment() {
    return TESTING ;
  }


}

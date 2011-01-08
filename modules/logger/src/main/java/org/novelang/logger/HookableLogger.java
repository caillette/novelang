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

import java.util.Map;

import com.google.common.collect.Maps;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A {@link Logger} wrapper installed on every instance returned by {@link LoggerFactory}
 * when {@link LoggerFactory#isTestEnvironment()} is true.
 *
 * @author Laurent Caillette
 */
public class HookableLogger extends AbstractLogger{

  private final AbstractLogger delegate ;

  /**
   * Don't forget to synchronize access.
   */
  private static final Map< String, HookableLogger > HOOKING_LOGGERS = Maps.newHashMap() ;

  /**
   * Constructor.
   *
   * @param delegate must be an {@link org.novelang.logger.AbstractLogger} for now.
   */
  public HookableLogger( final Logger delegate ) {
    this.delegate = ( AbstractLogger ) checkNotNull( delegate ) ;
    synchronized( HOOKING_LOGGERS ) {
      HOOKING_LOGGERS.put( delegate.getName(), this ) ;
    }
  }


  public static HookableLogger get( final String name ) {
    synchronized( HOOKING_LOGGERS ) {
      return HOOKING_LOGGERS.get( name ) ;
    }
  }

  private final Object lock = new Object() ;

  /**
   * Access synchronized on {@link #lock}.
   */
  private AbstractLogger hook = null ;

  /**
   * Install the other {@link Logger} to do special things with.
   *
   * @param hook must be an {@link AbstractLogger} for now, or null.
   */
  public void installHook( final Logger hook ) {
    synchronized( lock ) {
      this.hook = ( AbstractLogger ) hook ;
    }
  }

  public static void uninstallAllHooks() {
    synchronized( HOOKING_LOGGERS ) {
      for( final HookableLogger hookableLogger : HOOKING_LOGGERS.values() ) {
        hookableLogger.installHook( null ) ;
      }
    }
  }

// =================
// Overriden methods
// =================

  @Override
  protected void log( final Level level, final String message, final Throwable throwable ) {
    delegate.log( level, message, throwable ) ;
    synchronized( lock ) {
      if( hook != null ) {
        hook.log( level, message, throwable ) ;
      }
    }
  }

  @Override
  public String getName() {
    return delegate.getName() ;
  }

  @Override
  public boolean isTraceEnabled() {
    return delegate.isTraceEnabled() ;
  }

  @Override
  public boolean isDebugEnabled() {
    return delegate.isDebugEnabled() ;
  }

  @Override
  public boolean isInfoEnabled() {
    return delegate.isInfoEnabled() ;
  }
}

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

import com.google.common.collect.ImmutableList;

/**
 * Records every logging action.
 * Intended usage: as a {@link org.novelang.logger.HookableLogger#installHook(Logger) logger hook}.
 * Don't forget to call {@link HookableLogger#uninstallAllHooks()} when no longer needed.
 *
 * @author Laurent Caillette
 */
public class StandaloneRecordingLogger extends AbstractLogger {

  private final Object lock = new Object() ;

  /**
   * Every access should synchronize on {@link #lock}.
   * Even if {@link org.novelang.logger.HookableLogger} is thread-safe, it doesn't guarantee
   * thread-safety when accessing to its hooks.
   */
  private final ImmutableList.Builder< LogRecord > records = ImmutableList.builder() ;

  public ImmutableList< LogRecord > getRecords() {
    synchronized( lock ) {
      return records.build() ;
    }
  }



// =================
// Overriden methods
// =================

  @Override
  protected void log( final Level level, final String message, final Throwable throwable ) {
    synchronized( lock ) {
      records.add( new LogRecord( level, message, throwable ) ) ;
    }
  }

  @Override
  public String getName() {
    throw new UnsupportedOperationException( "Should never be called" ) ;
  }

  @Override
  public boolean isTraceEnabled() {
    throw new UnsupportedOperationException( "Should never be called" ) ;
  }

  @Override
  public boolean isDebugEnabled() {
    throw new UnsupportedOperationException( "Should never be called" ) ;
  }

  @Override
  public boolean isInfoEnabled() {
    throw new UnsupportedOperationException( "Should never be called" ) ;
  }

}

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

/**
 * This is how we log.
 *
 * @author Laurent Caillette
 */
public interface Logger {

  String getName() ;

  boolean isTraceEnabled() ;

  void trace( String message ) ;

  void trace( String message, Object... messageObjects ) ;

  void trace( Throwable throwable, Object... messageObjects ) ;

  boolean isDebugEnabled() ;

  void debug( String message ) ;

  void debug( String message, Object... messageObjects ) ;

  void debug( Throwable throwable, Object... messageObjects ) ;

  boolean isInfoEnabled() ;

  void info( String message ) ;

  void info( String message, Object... messageObjects ) ;

  void info( Throwable throwable, Object... messageObjects ) ;


  void warn( String message ) ;

  void warn( String message, Object... messageObjects ) ;

  void warn( Throwable throwable, Object... messageObjects ) ;


  void error( String message ) ;

  void error( String message, Object... messageObjects ) ;

  void error( Throwable throwable, Object... messageObjects ) ;


}

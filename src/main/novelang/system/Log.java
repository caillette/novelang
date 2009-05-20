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
package novelang.system;

/**
 * A wrapper around {@link org.slf4j.Logger} with varargs support and less clutter.
 *
 * @author Laurent Caillette
 */
public interface Log {

  String getName() ;

  boolean isTraceEnabled() ;

  void trace( String s ) ;

  void trace( String s, Object... o ) ;

  void trace( String s, Throwable throwable ) ;

  boolean isDebugEnabled() ;

  void debug( String s ) ;

  void debug( String s, Object... o ) ;

  void debug( String s, Throwable throwable ) ;

  boolean isInfoEnabled() ;

  void info( String s ) ;

  void info( String s, Object... o ) ;

  void info( String s, Throwable throwable ) ;

  boolean isWarnEnabled() ;

  void warn( String s ) ;

  void warn( String s, Object... o ) ;

  void warn( String s, Throwable throwable ) ;

  boolean isErrorEnabled() ;

  void error( String s ) ;

  void error( String s, Object... o ) ;

  void error( String s, Throwable throwable ) ;


}

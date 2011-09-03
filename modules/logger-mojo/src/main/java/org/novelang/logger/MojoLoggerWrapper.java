/*
 * Copyright (C) 2011 Laurent Caillette
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
 * @author Laurent Caillette
 */
public class MojoLoggerWrapper extends AbstractLogger {

  private final String name ;
  private final org.apache.maven.plugin.logging.Log mojoLog;

  public MojoLoggerWrapper(
      final String name,
      final org.apache.maven.plugin.logging.Log mojoLog
  ) {
    if( name == null || "".equals( name ) ) {
      throw new IllegalArgumentException( "Can't be null" ) ;
    }
    this.name = name ;
    this.mojoLog = mojoLog ;
  }


  @Override
  protected void log( final Level level, final String message, final Throwable throwable ) {

    final String prefixedMessage = "<" + getName() + "> " + message ;

    switch( level ) {
      case ERROR :
        if( throwable == null ) {
          mojoLog.error( prefixedMessage ) ;
        } else {
          mojoLog.error( prefixedMessage, throwable ) ;
        }
        break ;
      case WARN :
        if( throwable == null ) {
          mojoLog.warn( prefixedMessage ) ;
        } else {
          mojoLog.warn( prefixedMessage, throwable ) ;
        }
        break ;
      case INFO :
        if( throwable == null ) {
          mojoLog.info( prefixedMessage ) ;
        } else {
          mojoLog.info( prefixedMessage, throwable ) ;
        }
        break ;
      case DEBUG:
        if( throwable == null ) {
          mojoLog.debug( prefixedMessage ) ;
        } else {
          mojoLog.debug( prefixedMessage, throwable ) ;
        }
        break;
      case TRACE:
        if( throwable == null ) {
          mojoLog.debug( prefixedMessage ) ;
        } else {
          mojoLog.debug( prefixedMessage, throwable ) ;
        }
        break;
    }
  }

  @Override
  public String getName() {
    return name ;
  }

  @Override
  public boolean isTraceEnabled() {
    return mojoLog.isDebugEnabled() ;
  }

  @Override
  public boolean isDebugEnabled() {
    return mojoLog.isDebugEnabled() ;
  }

  @Override
  public boolean isInfoEnabled() {
    return mojoLog.isInfoEnabled() ;
  }
}

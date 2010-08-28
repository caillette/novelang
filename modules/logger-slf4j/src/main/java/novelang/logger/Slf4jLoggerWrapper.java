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
package novelang.logger;

import org.slf4j.Logger;

/**
 * @author Laurent Caillette
 */
public class Slf4jLoggerWrapper extends AbstractLogger {

  private final org.slf4j.Logger slf4jLogger ;

  public Slf4jLoggerWrapper( final Logger slf4jLogger ) {
    this.slf4jLogger = slf4jLogger ;
  }


  @Override
  protected void log( final Level level, final String message, final Throwable throwable ) {
    switch( level ) {
      case ERROR :
        if( throwable == null ) {
          slf4jLogger.error( message ) ;
        } else {
          slf4jLogger.error( message, throwable ) ;
        }
        break ;
      case WARN :
        if( throwable == null ) {
          slf4jLogger.warn( message ) ;
        } else {
          slf4jLogger.warn( message, throwable ) ;
        }
        break ;
      case INFO :
        if( throwable == null ) {
          slf4jLogger.info( message ) ;
        } else {
          slf4jLogger.info( message, throwable ) ;
        }
        break ;
      case DEBUG:
        if( throwable == null ) {
          slf4jLogger.debug( message ) ;
        } else {
          slf4jLogger.debug( message, throwable ) ;
        }
        break;
      case TRACE:
        if( throwable == null ) {
          slf4jLogger.trace( message ) ;
        } else {
          slf4jLogger.trace( message, throwable ) ;
        }
        break;
    }
  }

  @Override
  public String getName() {
    return slf4jLogger.getName() ;
  }

  @Override
  public boolean isTraceEnabled() {
    return slf4jLogger.isTraceEnabled() ;
  }

  @Override
  public boolean isDebugEnabled() {
    return slf4jLogger.isDebugEnabled() ;
  }

  @Override
  public boolean isInfoEnabled() {
    return slf4jLogger.isInfoEnabled() ;
  }
}

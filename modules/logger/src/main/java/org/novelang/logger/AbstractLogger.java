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
 * Useful things for implementing concrete {@link Logger}s.
 *
 * @author Laurent Caillette
 */
/*package*/ abstract class AbstractLogger implements Logger {

  enum Level {
    ERROR, WARN, INFO, DEBUG, TRACE
  }

  protected abstract void log( Level level, String message, Throwable throwable ) ;


// ==============================
// Single-string message building
// ==============================
  

  private static String buildMessage( final String message, final Object... messageObjects ) {
    final StringBuilder stringBuilder = new StringBuilder( ) ;
    if( message == null ) {
      stringBuilder.append( "<null>" ) ;
    } else {
      stringBuilder.append( message ) ;
    }
    add( stringBuilder, messageObjects ) ;
    return stringBuilder.toString() ;
  }

  private static String buildMessage( final Object... messageObjects ) {
    final StringBuilder stringBuilder = new StringBuilder( ) ;
    add( stringBuilder, messageObjects ) ;
    return stringBuilder.toString() ;
  }

  private static void add( final StringBuilder stringBuilder, final Object... messageObjects ) {
    for( final Object messageObject : messageObjects ) {
      if( messageObject == null ) {
        stringBuilder.append( "<null>" ) ;
      } else {
        stringBuilder.append( messageObject.toString() ) ;
      }
    }
  }



// ==============  
// Logger methods
// ==============  
  
  @Override
  public void trace( final String message ) {
    log( Level.TRACE, message, null ) ;
  }

  @Override
  public void trace( final String message, final Object... messageObjects ) {
    log( Level.TRACE, buildMessage( message, messageObjects ), null ) ;
  }

  @Override
  public void trace( final Throwable throwable, final Object... messageObjects ) {
    log( Level.TRACE, buildMessage( messageObjects ), throwable ) ;
  }

  @Override
  public void debug( final String message ) {
    log( Level.DEBUG, message, null ) ;
  }

  @Override
  public void debug( final String message, final Object... messageObjects ) {
    log( Level.DEBUG, buildMessage( message, messageObjects ), null ) ;
  }

  @Override
  public void debug( final Throwable throwable, final Object... messageObjects ) {
    log( Level.DEBUG, buildMessage( messageObjects ), throwable ) ;
  }

  @Override
  public void info( final String message ) {
    log( Level.INFO, message, null ) ;
  }

  @Override
  public void info( final String message, final Object... messageObjects ) {
    log( Level.INFO, buildMessage( message, messageObjects ), null ) ;
  }

  @Override
  public void info( final Throwable throwable, final Object... messageObjects ) {
    log( Level.INFO, buildMessage( messageObjects ), throwable ) ;
  }

  @Override
  public void warn( final String message ) {
    log( Level.WARN, message, null ) ;
  }

  @Override
  public void warn( final String message, final Object... messageObjects ) {
    log( Level.WARN, buildMessage( message, messageObjects ), null ) ;
  }

  @Override
  public void warn( final Throwable throwable, final Object... messageObjects ) {
    log( Level.WARN, buildMessage( messageObjects ), throwable ) ;
  }

  @Override
  public void error( final String message ) {
    log( Level.ERROR, message, null ) ;
  }

  @Override
  public void error( final String message, final Object... messageObjects ) {
    log( Level.ERROR, buildMessage( message, messageObjects ), null ) ;
  }

  @Override
  public void error( final Throwable throwable, final Object... messageObjects ) {
    log( Level.ERROR, buildMessage( messageObjects ), throwable ) ;
  }


}

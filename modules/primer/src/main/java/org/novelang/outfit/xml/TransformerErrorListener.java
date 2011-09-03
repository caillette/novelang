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
package org.novelang.outfit.xml;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import com.google.common.collect.ImmutableList;
import static com.google.common.base.Preconditions.checkNotNull;

import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;

/**
 * Logs all errors as they come and keeps them for rethrowing.
 *
 * @author Laurent Caillette
 */
public class TransformerErrorListener implements ErrorListener {

  private final Logger logger ;
  private final ImmutableList.Builder< Exception > exceptions = ImmutableList.builder() ;

  public TransformerErrorListener() {
    this( LoggerFactory.getLogger( TransformerErrorListener.class ) ) ;
  }

  public TransformerErrorListener( final Logger logger ) {
    this.logger = checkNotNull( logger ) ;
  }

  @Override
  public void warning( final TransformerException e ) throws TransformerException {
    logger.warn( e.getMessageAndLocation() ) ;
    addException( e ) ;
  }

  @Override
  public void error( final TransformerException e ) throws TransformerException {
    logger.error( e.getMessageAndLocation() ) ;
    addException( e ) ;
  }

  @Override
  public void fatalError( final TransformerException e ) throws TransformerException {
    logger.error( "Fatal !!! ", e.getMessageAndLocation() ) ;
    addException( e ) ;
  }

  /**
   * Create safe {@code Exception} objects that don't suffer from {@code SourceLocator}
   * state change that occurs after notification.
   */
  @SuppressWarnings( { "ThrowableInstanceNeverThrown" } )
  private void addException( final TransformerException original ) {
    final TransformerException safeException ;
    if( original.getLocator() == null ) {
      safeException = new TransformerException(
        original.getMessage(),
        original.getCause()
    ) ;
    } else {
      safeException = new TransformerException(
          original.getMessage(),
          ImmutableSourceLocator.create( original.getLocator() ),
          original.getCause()
          ) ;

    }
    safeException.setStackTrace( original.getStackTrace() ) ;
    exceptions.add( safeException ) ;
  }



  public void flush() throws TransformerCompositeException {
    final ImmutableList< Exception > list = exceptions.build() ;
    if( ! list.isEmpty() ) {
      throw new TransformerCompositeException(
          "Problem" + ( list.size() > 1 ? "s" : "" ) + " hit when processing stylesheet", list ) ;
    }
  }
}

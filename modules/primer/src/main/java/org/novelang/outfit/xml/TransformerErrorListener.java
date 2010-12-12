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

import com.google.common.base.Preconditions;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Laurent Caillette
 */
public class TransformerErrorListener implements ErrorListener {

  private final Logger logger ;

  public TransformerErrorListener() {
    this( LoggerFactory.getLogger( TransformerErrorListener.class ) ) ;
  }

  public TransformerErrorListener( final Logger logger ) {
    this.logger = checkNotNull( logger ) ;
  }

  @Override
  public void warning( final TransformerException e ) throws TransformerException {
    logger.warn( e.getMessageAndLocation() ) ;
  }

  @Override
  public void error( final TransformerException e ) throws TransformerException {
    logger.error( e.getMessageAndLocation() ) ;
  }

  @Override
  public void fatalError( final TransformerException e ) throws TransformerException {
    logger.error( e.getMessageAndLocation(), "Fatal" ) ;
  }
}

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

import com.google.common.collect.ImmutableList;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.novelang.outfit.CompositeException;
import org.xml.sax.SAXException;

/**
 * Thrown by {@link TransformerErrorListener}.
 *
 * @author Laurent Caillette
 */
public class TransformerMultiException extends CompositeException {

  public TransformerMultiException(
      final String message,
      final ImmutableList< Exception > exceptions
  ) {
    super( createMessageWithSources( message, exceptions ), exceptions ) ;
  }

  private static String createMessageWithSources(
      final String originalMessage,
      final ImmutableList< Exception > exceptions
  ) {
    final StringBuilder messageBuilder = new StringBuilder( originalMessage ) ;
    for( final Exception exception : exceptions ) {
      if( exception instanceof TransformerException ) {
        final SourceLocator sourceLocator = ( ( TransformerException ) exception ).getLocator() ;
        if( sourceLocator != null ) {
          messageBuilder.append( "\n" ) ;
          messageBuilder.append( ImmutableSourceLocator.asSingleLineString( sourceLocator ) ) ;
          messageBuilder.append( " - " ) ;
          messageBuilder.append( exception.getMessage() ) ;
        }
      }
    }
    return messageBuilder.toString() ;
  }
}

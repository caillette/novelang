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
package org.novelang.configuration.parse;

import java.io.File;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.produce.DocumentRequest;
import org.novelang.produce.RequestTools;
import org.apache.commons.cli.Options;

/**
 * Parses command-line arguments for {@link org.novelang.batch.DocumentGenerator}.
 *
 * TODO support a --flatten-ouput option as rendered documents go in the same path as sources.
 * TODO write test ensuring that absolute and relative directories are correctly handled.
 *
 * @author Laurent Caillette
 */
public class DocumentGeneratorParameters extends BatchParameters {

  private static final Logger LOGGER =
      LoggerFactory.getLogger( DocumentGeneratorParameters.class ) ;

  private final Iterable< DocumentRequest > documentRequests ;

  public DocumentGeneratorParameters( final File baseDirectory, final String[] parameters )
      throws ArgumentException
  {
    super( baseDirectory, parameters );
    final String[] sourceArguments = line.getArgs() ;
    LOGGER.debug( "found: sources = ", Lists.newArrayList( sourceArguments ) ) ;

    if( sourceArguments.length == 0 ) {
      throw new ArgumentException( "No source documents", helpPrinter ) ;
    } else {
      final List< DocumentRequest > requestList = Lists.newArrayList() ;
      for( String sourceArgument : sourceArguments ) {
        try {
          if( ! sourceArgument.startsWith( "/" ) ) {
            sourceArgument = "/" + sourceArgument ;
          }
          final DocumentRequest documentRequest =
              RequestTools.createDocumentRequest( sourceArgument ) ;
          if( null == documentRequest ) {
            throw new IllegalArgumentException(
                "Malformed document request: '" + sourceArgument + "'" ) ;
          }
          requestList.add( documentRequest ) ;
        } catch( IllegalArgumentException e ) {
          throw new ArgumentException( e, helpPrinter ) ;
        }
      }
      documentRequests = ImmutableList.copyOf( requestList ) ;
      LOGGER.debug( "Document requests = ", documentRequests ) ;
    }

  }

  protected void enrich( final Options options ) {
    options.addOption( OPTION_OUTPUT_DIRECTORY ) ;
  }
  
  /**
   * Returns document requests.
   * @return a non-null object iterating over no nulls, containing at least one element.
   */
  public Iterable< DocumentRequest > getDocumentRequests() {
    return documentRequests ;
  }



}

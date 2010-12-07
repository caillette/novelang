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
package org.novelang.configuration.parse;

import java.io.File;

import com.google.common.collect.Lists;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.produce.DocumentRequest;
import org.novelang.produce.RequestTools;
import org.novelang.rendering.RenditionMimeType;
import org.apache.commons.cli.Options;
import org.novelang.request.DocumentRequest2;
import org.novelang.request.GenericRequest;
import org.novelang.request.MalformedRequestException;

/**
 * Same as {@link DocumentGeneratorParameters} but allows only one single document request
 * @author Laurent Caillette
 */
public class LevelExploderParameters extends BatchParameters {

  private static final Logger LOGGER = LoggerFactory.getLogger( LevelExploderParameters.class ) ;
  private final DocumentRequest2 documentRequest ;

  public LevelExploderParameters(
      final File baseDirectory,
      final String[] parameters
  ) throws ArgumentException {
    super( baseDirectory, parameters ) ;

    final String[] sourceArguments = line.getArgs() ;
    LOGGER.debug( "found: sources = ", Lists.newArrayList( sourceArguments ) ) ;

    if( sourceArguments.length == 0 ) {
      throw new ArgumentException( "No source document", helpPrinter ) ;
    }
    if( sourceArguments.length > 1 ) {
      throw new ArgumentException( "Multiple source documents not supported", helpPrinter ) ;
    }
    String sourceArgument = sourceArguments[ 0 ] ;
    try {
      if( ! sourceArgument.startsWith( "/" ) ) {
        sourceArgument = "/" + sourceArgument ;
      }
      documentRequest = ( DocumentRequest2 ) GenericRequest.parse( sourceArgument ) ;
      if( null == documentRequest ) {
        throw new IllegalArgumentException(
            "Malformed document request: '" + sourceArgument + "'" ) ;
      }
      if( documentRequest.getRenditionMimeType() != RenditionMimeType.NOVELLA ) {
        throw new IllegalArgumentException(
            "Wrong mime type: '" + documentRequest.getRenditionMimeType() + "', " +
            "expecting " + RenditionMimeType.NOVELLA.getFileExtension()
        ) ;
      }
    } catch( IllegalArgumentException e ) {
      throw new ArgumentException( e, helpPrinter ) ;
    } catch( MalformedRequestException e ) {
      throw new ArgumentException( e, helpPrinter ) ;
    }
    LOGGER.debug( "Document request = ", documentRequest ) ;

  }

  @Override
  protected void enrich( final Options options ) {
    options.addOption( OPTION_OUTPUT_DIRECTORY ) ;
  }


  public DocumentRequest2 getDocumentRequest() {
    return documentRequest ;
  }
}

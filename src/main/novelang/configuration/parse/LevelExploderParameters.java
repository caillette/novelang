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
package novelang.configuration.parse;

import java.io.File;

import org.apache.commons.cli.Options;

import novelang.system.LogFactory;
import novelang.system.Log;
import novelang.produce.DocumentRequest;
import novelang.produce.RequestTools;
import novelang.rendering.RenditionMimeType;
import com.google.common.collect.Lists;

/**
 * Same as {@link DocumentGeneratorParameters} but allows only one single document request
 * @author Laurent Caillette
 */
public class LevelExploderParameters extends BatchParameters {

  private static final Log LOG = LogFactory.getLog( LevelExploderParameters.class ) ;
  private final DocumentRequest documentRequest ;

  public LevelExploderParameters(
      final File baseDirectory,
      final String[] parameters
  ) throws ArgumentException {
    super( baseDirectory, parameters ) ;

    final String[] sourceArguments = line.getArgs() ;
    LOG.debug( "found: sources = %s", Lists.newArrayList( sourceArguments ) ) ;

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
      documentRequest = RequestTools.createDocumentRequest( sourceArgument ) ;
      if( null == documentRequest ) {
        throw new IllegalArgumentException(
            "Malformed document request: '" + sourceArgument + "'" ) ;
      }
      if( documentRequest.getRenditionMimeType() != RenditionMimeType.NLP ) {
        throw new IllegalArgumentException(
            "Wrong mime type: '" + documentRequest.getRenditionMimeType() + "', " +
            "expecting " + RenditionMimeType.NLP.getFileExtension()
        ) ;
      }
    } catch( IllegalArgumentException e ) {
      throw new ArgumentException( e, helpPrinter ) ;
    }
    LOG.debug( "Document request = %s", documentRequest ) ;

  }

  protected void enrich( final Options options ) {
    options.addOption( OPTION_OUTPUT_DIRECTORY ) ;
  }


  public DocumentRequest getDocumentRequest() {
    return documentRequest ;
  }
}

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
import java.util.List;

import org.apache.commons.cli.Options;
import novelang.system.LogFactory;
import novelang.system.Log;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import novelang.produce.DocumentRequest;
import novelang.produce.RequestTools;

/**
 * Parses command-line arguments for {@link novelang.batch.DocumentGenerator}.
 *
 * TODO support a --flatten-ouput option as rendered documents go in the same path as sources.
 * TODO write test ensuring that absolute and relative directories are correctly handled.
 *
 * @author Laurent Caillette
 */
public class DocumentGeneratorParameters extends BatchParameters {

  private static final Log LOG = LogFactory.getLog( DocumentGeneratorParameters.class ) ;

  private final Iterable< DocumentRequest > documentRequests ;

  public DocumentGeneratorParameters( File baseDirectory, String[] parameters )
      throws ArgumentException
  {
    super( baseDirectory, parameters );
    final String[] sourceArguments = line.getArgs() ;
    LOG.debug( "found: sources = %s", Lists.newArrayList( sourceArguments ) ) ;

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
      LOG.debug( "Document requests = %s", documentRequests ) ;
    }

  }

  protected void enrich( Options options ) {
    options.addOption( CommonOptions.OPTION_OUTPUT_DIRECTORY ) ;
  }
  
  /**
   * Returns document requests.
   * @return a non-null object iterating over no nulls, containing at least one element.
   */
  public Iterable< DocumentRequest > getDocumentRequests() {
    return documentRequests ;
  }



}

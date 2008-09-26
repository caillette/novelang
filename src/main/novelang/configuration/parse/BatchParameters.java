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

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.produce.DocumentRequest;
import com.google.common.collect.Lists;

/**
 * @author Laurent Caillette
 */
public class BatchParameters extends GenericParameters {

  private static final Logger LOGGER = LoggerFactory.getLogger( BatchParameters.class ) ;

  public BatchParameters( File baseDirectory, String[] parameters ) throws ArgumentsNotParsedException {
    super( baseDirectory, parameters );
    final String[] sourceArguments = line.getArgs() ;
    LOGGER.debug( "found: sources = {}", Lists.<Object>newArrayList( sourceArguments ) ) ;

    if( sourceArguments.length == 0 ) {
      throw new ArgumentsNotParsedException( "No source documents" ) ;
    } else {
      // TODO
    }
  }

  protected void enrich( Options options ) {
    options.addOption( OPTION_OUTPUT_DIRECTORY ) ;
  }
  
  /**
   * Returns document requests.
   * @return a non-null object iterating over no nulls.
   */
  public Iterable< DocumentRequest > getDocumentRequests() {
    throw new UnsupportedOperationException( "getDocumentRequests" ) ;
  }

  /**
   * Returns the directory where documents are produced to.
   * @return null if not defined, an existing directory otherwise.
   */
  public File getOutputDirectory() {
    throw new UnsupportedOperationException( "getOutputDirectory" ) ;
  }



  private static final Option OPTION_OUTPUT_DIRECTORY = OptionBuilder
      .withLongOpt( "output-dir" )
      .withDescription( "Output directory for rendered documents" )
      .hasArg()
      .create()
  ;



}

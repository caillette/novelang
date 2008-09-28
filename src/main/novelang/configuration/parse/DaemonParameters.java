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

/**
 * Parses command-line arguments for {@link novelang.daemon.HttpDaemon}.
 * 
 * @author Laurent Caillette
 */
public class DaemonParameters extends GenericParameters {

  private static final Logger LOGGER = LoggerFactory.getLogger( DaemonParameters.class ) ;

  private final Integer port ;

  public DaemonParameters( File baseDirectory, String[] parameters )
      throws ArgumentsNotParsedException
  {
    super( baseDirectory, parameters ) ;

    if( line.hasOption( OPTION_HTTPDAEMON_PORT.getLongOpt() ) ) {
      final String portParameter = line.getOptionValue( OPTION_HTTPDAEMON_PORT.getLongOpt() ) ;
      LOGGER.debug( "found: {} = '{}'", OPTION_HTTPDAEMON_PORT.getLongOpt(), portParameter ) ;
      try {
        port = Integer.parseInt( portParameter ) ;
      } catch( NumberFormatException e ) {
        throw new ArgumentsNotParsedException( e );
      }
    } else {
      port = null ;
    }

  }


  protected void enrich( Options options ) {
    options.addOption( OPTION_HTTPDAEMON_PORT ) ;
  }

  /**
   * Returns the port of HTTP daemon.
   * @return an integer with a value greater than 0, or null if undefined.
   */
  public Integer getHttpDaemonPort() {
    return port ;
  }

  public String getHttpDaemonPortOptionDescription() {
    return createOptionDescription( OPTION_HTTPDAEMON_PORT ) ;
  }

  public static final String OPTIONNAME_HTTPDAEMON_PORT = "port" ;

  private static final Option OPTION_HTTPDAEMON_PORT = OptionBuilder
      .withLongOpt( OPTIONNAME_HTTPDAEMON_PORT )
      .withDescription( "TCP port for daemon" )
      .hasArg()
      .create()
  ;




}

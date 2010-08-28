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

import novelang.logger.Logger;
import novelang.logger.LoggerFactory;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 * Parses command-line arguments for {@link novelang.daemon.HttpDaemon}.
 *
 * TODO support --serve-shutdown 
 * 
 * @author Laurent Caillette
 */
public class DaemonParameters extends GenericParameters {

  private static final Logger LOGGER = LoggerFactory.getLogger( DaemonParameters.class );

  private final Integer port ;
  private final Boolean serveRemotes;

  public DaemonParameters( final File baseDirectory, final String... parameters )
      throws ArgumentException
  {
    super( baseDirectory, parameters ) ;

    if( line.hasOption( OPTION_HTTPDAEMON_PORT.getLongOpt() ) ) {
      final String portParameter = line.getOptionValue( OPTION_HTTPDAEMON_PORT.getLongOpt() ) ;
      LOGGER.debug( "found: ",
          OPTION_HTTPDAEMON_PORT.getLongOpt(),
          " = '",
          portParameter,
          "'"
      ) ;
      try {
        port = Integer.parseInt( portParameter ) ;
      } catch( NumberFormatException e ) {
        throw new ArgumentException( e, helpPrinter );
      }
    } else {
      port = null ;
    }

    if( line.hasOption( OPTION_HTTPDAEMON_SERVEREMOTES.getLongOpt() ) ) {
      this.serveRemotes = true ;
      LOGGER.debug( "found: ", OPTION_HTTPDAEMON_SERVEREMOTES.getLongOpt() ) ;
    } else {
      serveRemotes = null ;
    }

  }


  protected void enrich( final Options options ) {
    options.addOption( OPTION_HTTPDAEMON_PORT ) ;
    options.addOption( OPTION_HTTPDAEMON_SERVEREMOTES ) ;
  }

  /**
   * Returns the port of HTTP daemon.
   * @return an integer with a value greater than 0, or null if undefined.
   */
  public Integer getHttpDaemonPort() {
    return port ;
  }

  /**
   * Returns if should serve other HTTP requests than those originating from localhost.
   */
  public Boolean getServeRemotes() {
    return serveRemotes;
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

  public static final String OPTIONNAME_HTTPDAEMON_SERVEREMOTES = "serve-remotes" ;

  public String getHttpDaemonServeRemotesOptionDescription() {
    return createOptionDescription( OPTION_HTTPDAEMON_SERVEREMOTES ) ;
  }

  private static final Option OPTION_HTTPDAEMON_SERVEREMOTES = OptionBuilder
      .withLongOpt( OPTIONNAME_HTTPDAEMON_SERVEREMOTES )
      .withDescription( "Serve other requests than from localhost (127.0.0.*)" )
//      .hasArg()
      .create()
  ;


}

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

package org.novelang.daemon;

import java.io.File;

import org.apache.commons.lang.SystemUtils;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.HandlerCollection;
import org.novelang.Version;
import org.novelang.configuration.ConfigurationTools;
import org.novelang.configuration.DaemonConfiguration;
import org.novelang.configuration.parse.ArgumentException;
import org.novelang.configuration.parse.DaemonParameters;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;

/**
 * Main class for Novelang document generator daemon.
 *
 * @author Laurent Caillette
 */
@SuppressWarnings( { "UseOfSystemOutOrSystemErr" } )
public class HttpDaemon {

  private static final Logger LOGGER = LoggerFactory.getLogger( HttpDaemon.class ) ;

  private final Server server ;
  public static final String COMMAND_NAME = "httpdaemon";

  public static void main( final String commandName, final String[] args ) throws Exception {

    final DaemonParameters parameters ;

    try {
      parameters = new DaemonParameters( new File( SystemUtils.USER_DIR ), args ) ;
    } catch( ArgumentException e ) {
      if( e.isHelpRequested() ) {
        printHelpOnConsole( commandName, e ) ;
        System.exit( -1 ) ;
        throw new Error( "Never executes but makes compiler happy" ) ;
      } else {
        LOGGER.error( e, "Parameters exception, printing help and exiting." ) ;
        printHelpOnConsole( commandName, e ) ;
        System.exit( -2 ) ;
        throw new Error( "Never executes but makes compiler happy" ) ;
      }
    }

    final DaemonConfiguration daemonConfiguration =
        ConfigurationTools.createDaemonConfiguration( parameters );

    final String starting =
        "Starting " + HttpDaemon.class.getName() +
        " version " + Version.CURRENT_PRODUCT_VERSION.getName() +
        " on port " + daemonConfiguration.getPort()
    ;
    System.out.println( starting ) ;

    LOGGER.info( starting ) ;
    new HttpDaemon( daemonConfiguration ).start() ;
  }

  public HttpDaemon( final DaemonConfiguration daemonConfiguration ) {
    final HandlerCollection handlers = new HandlerCollection() ;
    if( ! daemonConfiguration.getServeRemotes() ) {
      handlers.addHandler( new LocalhostOnlyHandler() ) ;
    }
//    handlers.addHandler( new ShutdownHandler() ) ;
    handlers.addHandler( new FontDiscoveryHandler( daemonConfiguration.getProducerConfiguration() ) ) ;
    handlers.addHandler( new DirectoryScanHandler(
        daemonConfiguration.getProducerConfiguration().getContentConfiguration() ) ) ;
    handlers.addHandler( new DocumentHandler( daemonConfiguration.getProducerConfiguration() ) ) ;
    handlers.addHandler( new ResourceHandler( daemonConfiguration.getProducerConfiguration() ) ) ;
    server = new Server( daemonConfiguration.getPort() ) ;
    server.setHandler( handlers ) ;
  }

  public void start() throws Exception {
    server.start() ;
    LOGGER.info( "Server started on port ", server.getConnectors()[ 0 ].getLocalPort() ) ;
  }

  public void stop() throws Exception {
    final int port = server.getConnectors()[ 0 ].getLocalPort();
    server.stop() ;
    LOGGER.info( "Server stopped on port ", port ) ;
  }

  private static void printHelpOnConsole( final String commandName, final ArgumentException e ) {
    if( null != e.getMessage() ) {
      System.out.println( e.getMessage() ) ;
    }
    e.getHelpPrinter().print(
        System.out,
        commandName + " [OPTIONS]",
        80
    ) ;
  }

}

/*
 * Copyright (C) 2011 Laurent Caillette
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
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
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
  public static final String COMMAND_NAME = "httpdaemon" ;

  public static DaemonParameters createParameters( final String... arguments )
      throws ArgumentException
  {
    return new DaemonParameters( new File( SystemUtils.USER_DIR ), arguments ) ;
  }

  public static void main( final DaemonParameters daemonParameters ) throws Exception {
    final DaemonConfiguration daemonConfiguration =
        ConfigurationTools.createDaemonConfiguration( daemonParameters );

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
    handlers.addHandler( new FontDiscoveryHandler( daemonConfiguration.getProducerConfiguration() ) ) ;
    handlers.addHandler( new DirectoryScanHandler(
        daemonConfiguration.getProducerConfiguration().getContentConfiguration() ) ) ;
    handlers.addHandler( new DocumentHandler( daemonConfiguration.getProducerConfiguration() ) ) ;
    handlers.addHandler( new ResourceHandler( daemonConfiguration.getProducerConfiguration() ) ) ;
    handlers.addHandler( new UnhandledRequestHandler() ) ; // Must be last.
    server = new Server( daemonConfiguration.getPort() ) ;
    server.setHandler( handlers ) ;
    server.setThreadPool( new JettyThreadPool() ) ;
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


}

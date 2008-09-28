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

package novelang.daemon;

import java.io.File;

import org.apache.commons.lang.SystemUtils;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.HandlerCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.Version;
import novelang.configuration.ConfigurationTools2;
import novelang.configuration.DaemonConfiguration;
import novelang.configuration.parse.DaemonParameters;
import novelang.system.EnvironmentTools;
import novelang.system.StartupTools;

/**
 * Main class for Novelang document generator daemon.
 *
 * @author Laurent Caillette
 */
public class HttpDaemon {

  private static Logger LOGGER ;

  private final Server server ;

  public static void main( String[] args ) throws Exception {

    StartupTools.fixLogDirectory( args ) ;
    StartupTools.installXalan() ;
    LOGGER = LoggerFactory.getLogger( HttpDaemon.class ) ;
    EnvironmentTools.logSystemProperties() ;

    final DaemonParameters parameters =
        new DaemonParameters( new File( SystemUtils.USER_DIR ), args ) ;

    final DaemonConfiguration daemonConfiguration =
        ConfigurationTools2.createDaemonConfiguration( parameters );

    final String starting =
        "Starting " + HttpDaemon.class.getName() +
        " version " + Version.name() +
        " on port " + daemonConfiguration.getPort()
    ;
    System.out.println( starting ) ;

    LOGGER.info( starting ) ;
    new HttpDaemon( daemonConfiguration ).start() ;
  }

  public HttpDaemon( DaemonConfiguration daemonConfiguration ) {
    final HandlerCollection handlers = new HandlerCollection() ;
    handlers.addHandler( new ShutdownHandler() ) ;
    handlers.addHandler( new FontListHandler( daemonConfiguration.getProducerConfiguration() ) ) ;
    handlers.addHandler( new DirectoryScanHandler(
        daemonConfiguration.getProducerConfiguration().getContentConfiguration() ) ) ;
    handlers.addHandler( new DocumentHandler( daemonConfiguration.getProducerConfiguration() ) ) ;
    handlers.addHandler( new ResourceHandler( daemonConfiguration.getProducerConfiguration() ) ) ;
    server = new Server( daemonConfiguration.getPort() ) ;
    server.setHandler( handlers ) ;
  }

  private static synchronized Logger getLogger() {
    if( null == LOGGER ) {
      LOGGER = LoggerFactory.getLogger( HttpDaemon.class ) ;
    }
    return LOGGER ;
  }

  public void start() throws Exception {
    server.start() ;
    getLogger().info( "Server started on port {}", server.getConnectors()[ 0 ].getLocalPort() ) ;
  }

  public void stop() throws Exception {
    final int port = server.getConnectors()[ 0 ].getLocalPort();
    server.stop() ;
    getLogger().info( "Server stopped on port {}", port ) ;
  }


}

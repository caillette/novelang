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
import novelang.system.LogFactory;
import novelang.system.Log;
import novelang.Version;
import novelang.configuration.ConfigurationTools;
import novelang.configuration.DaemonConfiguration;
import novelang.configuration.parse.ArgumentException;
import novelang.configuration.parse.DaemonParameters;

/**
 * Main class for Novelang document generator daemon.
 *
 * @author Laurent Caillette
 */
public class HttpDaemon {

  private static Log LOG = LogFactory.getLog( HttpDaemon.class ) ;

  private final Server server ;

  public static void main( String commandName, String[] args ) throws Exception {

    final DaemonParameters parameters ;

    try {
      parameters = new DaemonParameters( new File( SystemUtils.USER_DIR ), args ) ;
    } catch( ArgumentException e ) {
      if( e.isHelpRequested() ) {
        printHelpOnConsole( commandName, e ) ;
        System.exit( -1 ) ;
        throw new Error( "Never executes but makes compiler happy" ) ;
      } else {
        LOG.error( "Parameters exception, printing help and exiting.", e ) ;
        printHelpOnConsole( commandName, e ) ;
        System.exit( -2 ) ;
        throw new Error( "Never executes but makes compiler happy" ) ;
      }
    }

    final DaemonConfiguration daemonConfiguration =
        ConfigurationTools.createDaemonConfiguration( parameters );

    final String starting =
        "Starting " + HttpDaemon.class.getName() +
        " version " + Version.name() +
        " on port " + daemonConfiguration.getPort()
    ;
    System.out.println( starting ) ;

    LOG.info( starting ) ;
    new HttpDaemon( daemonConfiguration ).start() ;
  }

  public HttpDaemon( DaemonConfiguration daemonConfiguration ) {
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
    LOG.info( "Server started on port %s", server.getConnectors()[ 0 ].getLocalPort() ) ;
  }

  public void stop() throws Exception {
    final int port = server.getConnectors()[ 0 ].getLocalPort();
    server.stop() ;
    LOG.info( "Server stopped on port %s", port ) ;
  }

  private static void printHelpOnConsole( String commandName, ArgumentException e ) {
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

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

import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.HandlerCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.configuration.ConfigurationTools;
import novelang.configuration.ServerConfiguration;

/**
 * 
 * @author Laurent Caillette
 */
public class HttpDaemon {

  private static final Logger LOGGER = LoggerFactory.getLogger( HttpDaemon.class ) ;

  private static final int HTTP_SERVER_PORT = 8080 ;

  private final Server server ;

  public HttpDaemon( int httpServerPort, ServerConfiguration serverConfiguration ) {
    final HandlerCollection handlers = new HandlerCollection() ;
    handlers.addHandler( new DocumentHandler( serverConfiguration ) ) ;
    handlers.addHandler( new ResourceHandler( serverConfiguration ) ) ;
    server = new Server( httpServerPort ) ;
    server.setHandler( handlers ) ;
  }

  public void start() throws Exception {
    server.start() ;
    LOGGER.info( "Server started on port {}", server.getConnectors()[ 0 ].getLocalPort() ) ;
  }

  public void stop() throws Exception {
    final int port = server.getConnectors()[ 0 ].getLocalPort();
    server.stop() ;
    LOGGER.info( "Server stopped on port {}", port ) ;
  }

  public static void main( String[] args ) throws Exception {
    new HttpDaemon( HTTP_SERVER_PORT, ConfigurationTools.buildServerConfiguration() ).start() ;
  }

}

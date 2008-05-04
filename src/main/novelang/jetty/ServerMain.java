/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.jetty;

import java.io.File;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.HandlerCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.SystemUtils;
import novelang.configuration.HttpServerConfiguration;
import novelang.configuration.ConfigurationTools;

/**
 * 
 * @author Laurent Caillette
 */
public class ServerMain {

  private static final Logger LOGGER = LoggerFactory.getLogger( ServerMain.class ) ;

  private static final int HTTP_SERVER_PORT = 8080;


  public static void main( String[] args ) throws Exception {
    final HandlerCollection handlers = new HandlerCollection() ;
    final HttpServerConfiguration httpServerConfiguration =
        ConfigurationTools.buildHttpServerConfiguration() ;
    handlers.addHandler( new DocumentHandler( httpServerConfiguration ) ) ;
    handlers.addHandler( new ResourceHandler( httpServerConfiguration ) ) ;
    final Server server = new Server( HTTP_SERVER_PORT ) ;
    server.setHandler( handlers ) ;
    server.start() ;
    LOGGER.info( "Server started on port " + HTTP_SERVER_PORT ) ;
  }

}

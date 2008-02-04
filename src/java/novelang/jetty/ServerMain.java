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

/**
 * 
 * @author Laurent Caillette
 */
public class ServerMain {

  private static final Logger LOGGER = LoggerFactory.getLogger( ServerMain.class ) ;

  private static final String HELLO = "-hello" ;

  private static HandlerCollection readArguments( String[] args ) {
    final HandlerCollection handlers = new HandlerCollection() ;

    if( 0 == args.length ) {
      LOGGER.error( "No command-line parameter" ) ;
      System.exit( -1 ) ;
    }

    if( HELLO.equals( args[ 0 ] ) ) {
      handlers.addHandler( new HelloHandler() ) ;
      LOGGER.warn( "Set to 'hello' mode, no book will be served." ) ;
    } else {
      for( int i = 0; i < args.length ; i += 2 ) {
        final String bookIdentifier = args[ i ] ;
        if( i + 1 < args.length ) {
          final String structureFileName = args[ i + 1 ] ;
          final File structureFile = new File( structureFileName ) ;
          if( structureFile.exists() ) {
            handlers.addHandler( new BookHandler( bookIdentifier, structureFile ) ); ;
          } else {
            LOGGER.error( "File does not exist: '" + structureFile + "'" ) ;
          }
        } else {
          LOGGER.error( "No structure file declared for book '" + bookIdentifier + "'" ) ;
        }
      }
    }
    return handlers;
  }

  public static void main( String[] args ) throws Exception {
    final HandlerCollection handlers = readArguments( args ) ;
    final Server server = new Server( 8080 ) ;
    server.setHandler( handlers ) ;
    server.start() ;
  }

}

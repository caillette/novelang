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
package org.novelang.outfit.shell;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Does stupid things for {@link org.novelang.outfit.shell.JavaShell} test.
 *
 * @author Laurent Caillette
 */
@SuppressWarnings( { "JavadocReference", "SocketOpenedButNotSafelyClosed" } )
public class StupidListener {
  
  private StupidListener() {
  }

  public static void main( final String... arguments ) throws IOException, InterruptedException {
    final File logFile = new File( arguments[ 0 ] ) ;

    Runtime.getRuntime().addShutdownHook( new Thread( new Runnable() {
      @Override
      public void run() {
        try {
          write( logFile, "Terminated." ) ;
        } catch( IOException e ) {
          throw new RuntimeException( e ) ;
        }
      }
    } ) ) ;

    System.out.println( "Started." ) ; // Used by startup sensor.

    write( logFile, "Starting up and listening..." ) ;
    
    final int port = Integer.parseInt( arguments[ 1 ] ) ;
    final ServerSocket serverSocket = new ServerSocket( port ) ;
    try {
      serverSocket.accept() ;
    } finally {
      serverSocket.close() ;
    }
  }

  private static void write( final File logFile, final String message ) throws IOException {
    final FileOutputStream fileOutputStream = new FileOutputStream( logFile, true ) ;
    try {
      fileOutputStream.write( message.getBytes( ) ) ;
      fileOutputStream.write( "\n".getBytes( ) ) ;
    } finally {
      fileOutputStream.close() ;
    }
  }
}

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
package org.novelang.outfit;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Scans local TCP ports for getting one available.
 *
 * @author Laurent Caillette
 */
public interface TcpPortBooker {
  TcpPortBooker THIS = new TcpPortBooker() {
    private final AtomicInteger counter = new AtomicInteger( LOWEST_PORT ) ;

    @Override
    public int find() {
      while( true ) {
        counter.compareAndSet( HIGHEST_PORT, LOWEST_PORT ) ;
        final ServerSocket serverSocket ;
        try {
          final int port = counter.incrementAndGet() ;
          serverSocket = new ServerSocket( port ) ;
          serverSocket.close() ;
          return port ;
        } catch( IOException ignore ) { }
      }
    }

  } ;

  int find() ;


  /**
   * Don't use port 1024 which is default for RMI registry.
   * Ports below 10000 are reserved; using them can cause problems in some cases.
   * (Like Firefox which doesn't want to connect to them, maybe annoying for projects
   * reusing that stuff.
   */
  int LOWEST_PORT = 10000 ;

  int HIGHEST_PORT = 65535 ;

}

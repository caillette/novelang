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
package novelang.system.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
* @author Laurent Caillette
*/
/*package*/ abstract class InputStreamWatcher implements Runnable {

  private final BufferedReader reader ;

  @SuppressWarnings( { "IOResourceOpenedButNotSafelyClosed" } )
  InputStreamWatcher( final InputStream stream ) {
    this.reader = new BufferedReader( new InputStreamReader( stream ) ) ;
  }

  public final void run() {
    try {
      while( ! Thread.currentThread().isInterrupted() ) {
        try {
          // Tried to read in a buffer manually, doesn't get more chars, just shows
          // the logging system flushes to the console too lazily.
          final String line = reader.readLine() ;
          interpretLine( line ) ;
        } catch( Throwable t ) {
          if( ! Thread.currentThread().isInterrupted() ) { // Double-check, may have changed.
            handleThrowable( t ) ;
          }
          break ;
        }
      }
    } finally {
      try {
        cleanup() ;
      } catch( IOException e ) {
        handleThrowable( e ) ;
      }
    }
  }

  protected abstract void interpretLine( final String line ) ;

  protected abstract void handleThrowable( final Throwable throwable ) ;

  public void cleanup() throws IOException {
    reader.close() ; // Not especially useful I guess.
  }
}

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
package org.novelang.outfit.shell;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import org.novelang.testing.DirectoryFixture;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.novelang.testing.junit.NameAwareTestClassRunner;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Get sure that {@link org.novelang.outfit.shell.StupidListener} does what we think it does
 * before accusing other code.
 *
 * @author Laurent Caillette
 */
@RunWith( NameAwareTestClassRunner.class )
public class StupidListenerTest {

  @Test
  public void startAndListen() throws IOException, InterruptedException {
    final int port = org.novelang.outfit.TcpPortBooker.THIS.find() ;
    final File logFile = new File(
        new DirectoryFixture().getDirectory(),
        StupidListener.class.getSimpleName() + ".txt"
    ).getCanonicalFile() ;

    new Thread( new Runnable() {
      @Override
      public void run() {
        try {
          StupidListener.main( logFile.getAbsolutePath(), Integer.toString( port ) ) ;
        } catch( Exception e ) {
          throw new RuntimeException( e ) ;
        }
      }
    } ).start() ;
    Thread.sleep( 1000L ) ;
    final Socket clientSocket = new Socket( "localhost", port ) ;
    clientSocket.close() ;

    final List< String > logLines = FileUtils.readLines( logFile ) ;
    
    assertThat( logLines )
        .contains( "Starting up and listening..."
        // Don't assert on "Terminated." because it occurs in the shutdown hook and
        // we are running the test in-process here.
    ) ;

  }
}

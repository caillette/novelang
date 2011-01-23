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

import org.novelang.testing.junit.MethodSupport;

import static com.google.common.collect.ImmutableList.of;

/**
 * Lots of stuff used by {@link JavaShellTest}. 
 *
 * @author Laurent Caillette
 */
class ShellFixture {

  private final File logFile ;
  private final JavaShellParameters parameters ;
  private final int dummyListenerPort ;
  private final File jarFile ;

  public File getLogFile() {
    return logFile;
  }

  public JavaShellParameters getParameters() {
    return parameters;
  }

  public File getJarFile() {
    return jarFile ;
  }

  public ShellFixture( final MethodSupport methodSupport ) throws IOException {
    final int jmxPort = org.novelang.outfit.TcpPortBooker.THIS.find() ;
    dummyListenerPort = org.novelang.outfit.TcpPortBooker.THIS.find() ;
    final File scratchDirectory = methodSupport.getDirectory() ;
    logFile = new File( scratchDirectory, "dummy.txt" );
    jarFile = JavaShellTest.installFixturePrograms( scratchDirectory ) ;

    parameters = org.novelang.outfit.Husk.create( JavaShellParameters.class )
        .withNickname( "Stupid-" + methodSupport.getTestName().substring( 0, 4 ) )
        .withWorkingDirectory( scratchDirectory )
        .withJavaClasses( new JavaClasses.ClasspathAndMain(
            "org.novelang.outfit.shell.StupidListener",
            jarFile
        ) )
        .withStartupSensor( JavaShellTest.STUPID_LISTENER_STARTED )
        .withProgramArguments( of(
            logFile.getAbsolutePath(),
            Integer.toString( dummyListenerPort )
        ) )
        .withJmxPortConfiguredAtJvmStartup( jmxPort )
        .withJmxKit( new DefaultJmxKit() )
    ;
  }

  public void askForSelfTermination() {
    try {
      final Socket clientSocket = new Socket( "localhost", dummyListenerPort ) ;
      clientSocket.close() ;
    } catch( IOException e ) {
      JavaShellTest.LOGGER.debug( "Couldn't open socket on port ", dummyListenerPort, ": ", e.getMessage() ) ;
    }
  }

}

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
package org.novelang.nhovestone.driver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import org.novelang.configuration.parse.DaemonParameters;
import org.novelang.daemon.HttpDaemon;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.Husk;
import org.novelang.outfit.shell.ProcessCreationException;
import org.novelang.outfit.shell.ProcessInitializationException;

/**
 * Starts and stops an {@link org.novelang.daemon.HttpDaemon} in its deployment directory,
 * in a separate JVM.
 *
 * @author Laurent Caillette
 */
public class HttpDaemonDriver extends EngineDriver {

  private static final Logger LOGGER = LoggerFactory.getLogger( HttpDaemonDriver.class );
  
  private final int tcpPort ;


  public HttpDaemonDriver( final Configuration configuration ) {
    super(
        enrichWithProgramArguments( configuration ),
        HttpDaemon.COMMAND_NAME,
        PROCESS_STARTED_SENSOR
    ) ;

    // Could avoid this check either by some complicated inheritance that Husk doesn't
    // support for now, or by adding a constructor parameter in the superclass.
    Preconditions.checkArgument(
        configuration.getProgramOtherOptions() == null,
        "Use method in " + Configuration.class.getName() + " to set program options"
    ) ;

    this.tcpPort = getTcpPortForSure( configuration ) ;
  }


  private static int getTcpPortForSure( final Configuration configuration ) {
    final Integer tcpPort = configuration.getHttpPort() ;
    if( tcpPort == null ) {
      return org.novelang.configuration.ConfigurationTools.DEFAULT_HTTP_DAEMON_PORT ;
    } else {
      return tcpPort ;
    }

  }


  private static EngineDriver.Configuration enrichWithProgramArguments(
      final Configuration configuration
  ) {
    return configuration.withProgramOtherOptions(
        "--" + DaemonParameters.OPTIONNAME_HTTPDAEMON_PORT,
        "" + getTcpPortForSure( configuration )
    ) ;
  }

  public int getTcpPort() {
    return tcpPort ;
  }

  @Override
  public void start( final long timeout, final TimeUnit timeUnit )
      throws
      IOException,
      InterruptedException,
      ProcessCreationException,
      ProcessInitializationException 
  {
    ensureTcpPortAvailable() ;
    super.start( timeout, timeUnit );
  }

  
  @SuppressWarnings( { "SocketOpenedButNotSafelyClosed" } )
  public void ensureTcpPortAvailable() throws IOException {
    final ServerSocket serverSocket;
    try {
      serverSocket = new ServerSocket( tcpPort );
    } catch( IOException e ) {
      // Need to do this because some finally clause in calling class may cause an exception
      // masking this one.
      LOGGER.error( "Port already in use: ", tcpPort ) ;
      throw e ;
    }
    serverSocket.close() ;
  }


  private static final Predicate< String > PROCESS_STARTED_SENSOR = new Predicate< String >() {
    public boolean apply( final String lineInConsole ) {
      // Can't use "Server started" because this message is never flushed to the standard output
      // in time.
      //   return lineInConsole.contains( "Server started " ) ;
      return lineInConsole.contains( "Starting novelang.daemon.HttpDaemon" ) ;
    }
  } ;


  @Husk.Converter( converterClass = ConfigurationHelper.class )
  public interface Configuration extends EngineDriver.Configuration< Configuration > {

    Integer getHttpPort() ;
    Configuration withHttpPort( Integer port ) ;
  }

}

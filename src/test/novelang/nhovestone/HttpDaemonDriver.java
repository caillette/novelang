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
package novelang.nhovestone;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import novelang.Version;
import novelang.configuration.parse.DaemonParameters;
import novelang.configuration.parse.GenericParameters;
import novelang.system.Husk;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static novelang.configuration.parse.GenericParameters.OPTIONPREFIX;

/**
 * Starts and stops an {@link novelang.daemon.HttpDaemon} in its deployment directory,
 * in a separate JVM.
 *
 * @author Laurent Caillette
 */
public class HttpDaemonDriver {

  private final ProcessDriver processDriver ;
  private final int tcpPort ;


  public HttpDaemonDriver( final Configuration configuration ) {

    tcpPort = configuration.getTcpPort() ;

    final String applicationName = "Novelang-" + configuration.getVersion().getName() ;

    final ImmutableList.Builder< String > optionsBuilder = new ImmutableList.Builder< String >() ;

//    optionsBuilder.add( SystemUtils.JAVA_HOME + "/bin/java" ) ;
    optionsBuilder.add( "java" ) ; 

    optionsBuilder.add( "-Xmx" + checkNotNull( configuration.getJvmHeapSizeMegabytes() + "M" ) ) ;

//    optionsBuilder.add( "-Djava.awt.headless=true" ) ;

    optionsBuilder.add( "-server" ) ;

    if( "64".equals( System.getProperty( "sun.arch.data.model" ) ) ) {
      optionsBuilder.add( "-d64" ) ;
    }

    final Iterable< String > otherJvmOptions = configuration.getJvmOtherOptions() ;
    if( otherJvmOptions != null ) {
      for( final String processOption : otherJvmOptions ) {
        if( processOption.startsWith( "-Xmx" ) ) {
          throw new IllegalArgumentException(
              "Use method in " + Configuration.class.getName() + " to set -Xmx" ) ;
        } else {
          optionsBuilder.add( checkNotNull( processOption ) ) ;
        }
      }
    }

    optionsBuilder.add( "-jar" ) ;

    optionsBuilder.add( configuration.getInstallationDirectory().getAbsolutePath()
        + File.separator + applicationName + File.separator + applicationName + ".jar" ) ;

    optionsBuilder.add( "httpdaemon" ) ;

    optionsBuilder.add( OPTIONPREFIX + DaemonParameters.OPTIONNAME_HTTPDAEMON_PORT ) ;
    optionsBuilder.add( "" + tcpPort ) ;

    optionsBuilder.add( OPTIONPREFIX + GenericParameters.LOG_DIRECTORY_OPTION_NAME ) ;
    optionsBuilder.add( checkNotNull( configuration.getLogDirectory() ).getAbsolutePath() ) ;

    optionsBuilder.add( OPTIONPREFIX + GenericParameters.OPTIONNAME_CONTENT_ROOT ) ;
    optionsBuilder.add( checkNotNull( configuration.getContentRootDirectory() ).getAbsolutePath() ) ;

    final List< String > processOptions = optionsBuilder.build() ;

    processDriver = new ProcessDriver(
        checkNotNull( configuration.getWorkingDirectory() ),
        applicationName,
        processOptions,
        PROCESS_STARTED_SENSOR
    ) ;

  }

  
  public int getTcpPort() {
    return tcpPort ;
  }


  @SuppressWarnings( { "SocketOpenedButNotSafelyClosed" } )
  public void ensureTcpPortAvailable() throws IOException {
    final ServerSocket serverSocket = new ServerSocket( tcpPort ) ;
    serverSocket.close() ;
  }

  public void start( final long timeout, final TimeUnit timeUnit )
      throws
      IOException,
      ProcessDriver.ProcessCreationFailedException,
      InterruptedException
  {
    ensureTcpPortAvailable() ;
    processDriver.start( timeout, timeUnit ) ;
  }


  public void shutdown( final boolean force ) throws InterruptedException {
    processDriver.shutdown( force ) ;
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
  public interface Configuration {

    File getInstallationDirectory() ;
    Configuration withInstallationDirectory( File directory ) ;

    Version getVersion() ;
    Configuration withVersion( Version version ) ;

    Integer getTcpPort() ;
    Configuration withTcpPort( Integer port ) ;

    File getWorkingDirectory() ;
    Configuration withWorkingDirectory( File directory ) ;

    File getLogDirectory() ;
    Configuration withLogDirectory( File directory ) ;

    File getContentRootDirectory() ;
    Configuration withContentRootDirectory( File directory ) ;

    Integer getJvmHeapSizeMegabytes() ;
    Configuration withJvmHeapSizeMegabytes( Integer sizeMegabytes) ;

    Iterable< String > getJvmOtherOptions() ;
    Configuration withJvmOtherOptions( String... options ) ;

  }

  @SuppressWarnings( { "UnusedDeclaration" } )
  public static class ConfigurationHelper {

    public static Iterable< String > convert( final String... strings ) {
      return Arrays.asList( strings ) ;
    }
    
  }
}

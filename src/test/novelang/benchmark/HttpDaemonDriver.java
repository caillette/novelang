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
package novelang.benchmark;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import novelang.Version;
import novelang.configuration.parse.DaemonParameters;
import novelang.configuration.parse.GenericParameters;

import java.io.File;
import java.io.IOException;
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


  public HttpDaemonDriver( final Configuration configuration ) {

    final String applicationName = "Novelang-" + configuration.getVersion().getName() ;

    final ImmutableList.Builder< String > optionsBuilder = new ImmutableList.Builder< String >() ;

    optionsBuilder.add( "java" ) ; // TODO: use the path to current VM.

    optionsBuilder.add( "-Xmx" + checkNotNull( configuration.getJvmHeapSizeMegabytes() + "M" ) ) ;

    optionsBuilder.add( "-Djava.awt.headless=true" ) ;

    for( final String processOption : configuration.getJvmOtherOptions() ) {
      if( processOption.startsWith( "-Xmx" ) ) {
        throw new IllegalArgumentException(
            "Use method in " + Configuration.class.getName() + " to set -Xmx" ) ;
      } else {
        optionsBuilder.add( checkNotNull( processOption ) ) ;
      }
    }

    optionsBuilder.add( "-jar" ) ;

    final File applicationDirectory =
        new File( configuration.getVersionsDirectory(), applicationName ) ;
    optionsBuilder.add( applicationDirectory.getAbsolutePath() + applicationName + ".jar" ) ;

    optionsBuilder.add( "httpdaemon" ) ;

    optionsBuilder.add( OPTIONPREFIX + DaemonParameters.OPTIONNAME_HTTPDAEMON_PORT ) ;
    optionsBuilder.add( "" + configuration.getTcpPort() ) ;

    optionsBuilder.add( OPTIONPREFIX + GenericParameters.LOG_DIRECTORY_OPTION_NAME ) ;
    optionsBuilder.add( checkNotNull( configuration.getLogDirectory() ).getAbsolutePath() ) ;

    optionsBuilder.add( OPTIONPREFIX + GenericParameters.OPTIONNAME_CONTENT_ROOT ) ;
    optionsBuilder.add( checkNotNull( configuration.getContentRootDirectory() ).getAbsolutePath() ) ;

    final List< String > processOptions = optionsBuilder.build();

    processDriver = new ProcessDriver(
        checkNotNull( configuration.getWorkingDirectory() ),
        applicationName,
        processOptions,
        PROCESS_STARTED_SENSOR
    ) ;

  }


  public void start( final long timeout, final TimeUnit timeUnit )
      throws
      IOException,
      ProcessDriver.ProcessCreationFailedException,
      InterruptedException
  {
    processDriver.start( timeout, timeUnit ) ;
  }


  public void shutdown( final boolean force ) throws InterruptedException {
    processDriver.shutdown( force ) ;
  }


  private static final Predicate< String > PROCESS_STARTED_SENSOR = new Predicate< String >() {
    public boolean apply( final String lineInConsole ) {
      return lineInConsole.startsWith( "Started " ) ;
    }
  } ;


  public interface Configuration {

    File getVersionsDirectory() ;
    Configuration withVersionsDirectory() ;

    Version getVersion() ;
    Configuration withVersion( Version version ) ;

    Integer getTcpPort() ;
    Configuration withTcpPort( Integer port ) ;

    File getWorkingDirectory() ;
    Configuration withWorkingDirectory( File directory ) ;

    File getLogDirectory() ;
    Configuration withLogDirectory( File directory ) ;

    File getContentRootDirectory() ;
    Configuration withContentRootDirectory() ;

    Integer getJvmHeapSizeMegabytes() ;
    Configuration withJvmHeapSizeMegabytes( int sizeMegabytes) ;

    Iterable< String > getJvmOtherOptions() ;
    Configuration withJvmOtherOptions( String... options ) ;

  }
}

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
package novelang.benchmark.scenario;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import novelang.Version;
import novelang.benchmark.HttpDaemonDriver;
import novelang.benchmark.ProcessDriver;
import novelang.common.FileTools;
import novelang.system.Husk;
import novelang.system.Log;
import novelang.system.LogFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Runs several {@link novelang.benchmark.HttpDaemonDriver}s in parallel against an evolving
 * source document.
 *
 * @author Laurent Caillette
 */
public class Scenario< MEASUREMENT > {

  private static final Log LOG = LogFactory.getLog( Scenario.class ) ;

  private final String name ;
  private final int warmupIterations ;
  private final Integer maximumIterations ;

  /**
   * Mutable objects: contains {@link novelang.benchmark.HttpDaemonDriver}s not yet evaluated
   * as {@link Measurer#detectStrain(java.util.List, Object)} strained}.
   */
  private final Map< Version, HttpDaemonDriver > drivers = Maps.newHashMap() ;

  /**
   * Mutable object: contains list of non-null {@code MEASUREMENT} objects.
   */
  private final Map< Version, List< MEASUREMENT > > measurements = Maps.newHashMap() ;
  private final String documentRequest ;
  private final Upsizer upsizer ;
  private final Measurer< MEASUREMENT > measurer ;

  public Scenario(
      final File scenariiDirectory,
      final Upsizer.Factory upsizerFactory,
      final File novelangInstallationsDirectory,
      final Iterable< Version > versions,
      final int firstTcpPort,
      final Measurer< MEASUREMENT > measurer
  ) throws IOException {
    this(
        UNKNOWN,
        DEFAULT_WARMUP_ITERATIONS,
        DEFAULT_MAXIMUM_ITERATIONS,
        scenariiDirectory,
        upsizerFactory,
        novelangInstallationsDirectory,
        versions,
        firstTcpPort,
        measurer
    ) ;
  }


  /**
   * Hack: we can't call {@link #getClass()} in a constructor calling this( ... ).
   */
  @SuppressWarnings( { "RedundantStringConstructorCall" } )
  private static final String UNKNOWN = new String( "UNKNOWN" ) ;


  public Scenario(
      final String scenarioName,
      final int warmupIterations,
      final Integer maximumIterations,
      final File scenariiDirectory,
      final Upsizer.Factory upsizerFactory,
      final File novelangInstallationsDirectory,
      final Iterable<Version> versions,
      final int firstTcpPort,
      final Measurer<MEASUREMENT> measurer
  ) throws IOException {

    //noinspection StringEquality
    name = ( scenarioName == UNKNOWN ) ? getClass().getSimpleName() : scenarioName ;
    this.warmupIterations = warmupIterations ;
    this.maximumIterations = maximumIterations ;

    final File scenarioDirectory = FileTools.createFreshDirectory( scenariiDirectory, name ) ;
    final File contentDirectory = FileTools.createFreshDirectory( scenarioDirectory, "content" ) ;

    upsizer = upsizerFactory.create( contentDirectory ) ;
    documentRequest = upsizerFactory.getDocumentRequest() ;

    this.measurer = Preconditions.checkNotNull( measurer ) ;

    int tcpPort = firstTcpPort ;
    
    for( final Version version : versions ) {
      final File versionWorkingDirectory =
          FileTools.createFreshDirectory( scenarioDirectory, version.getName() ) ;

      final HttpDaemonDriver httpDaemonDriver = new HttpDaemonDriver(
          Husk.create( HttpDaemonDriver.Configuration.class )
          .withWorkingDirectory( versionWorkingDirectory )
          .withContentRootDirectory( contentDirectory )
          .withJvmHeapSizeMegabytes( 32 )
          .withInstallationDirectory( novelangInstallationsDirectory )
          .withLogDirectory( versionWorkingDirectory )
          .withVersion( version )
          .withTcpPort( tcpPort )
      ) ;

      drivers.put( version, httpDaemonDriver ) ;
      tcpPort ++ ;

      measurements.put( version, Lists.< MEASUREMENT >newArrayList() ) ;
    }
  }

  private static final int DEFAULT_WARMUP_ITERATIONS = 1 ;
  private static final Integer DEFAULT_MAXIMUM_ITERATIONS = 100 ;

  public void run()
      throws
      InterruptedException,
      IOException,
      ProcessDriver.ProcessCreationFailedException
  {
    startDaemons() ;
    warmup( warmupIterations ) ;
    for( int iterationCount = 1 ;
        ( maximumIterations == null || iterationCount <= maximumIterations )
            && ! drivers.isEmpty() ;
        iterationCount ++
    ) {
      final int daemonCount = drivers.size() ;
      logPassCount( "Querying " + daemonCount + " daemon(s), pass ", iterationCount );
      runOnceWithMeasurementsOnEveryDaemon() ;
      logPassCount( "Done querying " + daemonCount + " daemon(s), pass ", iterationCount );
    }
    shutdownDaemons() ;
  }

  
  public Map< Version, List< MEASUREMENT > > getMeasurements() {
    final ImmutableMap.Builder< Version, List< MEASUREMENT > > builder =
        new ImmutableMap.Builder< Version, List< MEASUREMENT > >() ;
    for( final Map.Entry< Version, List< MEASUREMENT > > entry : measurements.entrySet() ) {
      final List< MEASUREMENT > measurementList = ImmutableList.copyOf( entry.getValue() ) ;
      builder.put( entry.getKey(), measurementList ) ;
    }
    return builder.build() ;
  }


  private void startDaemons()
      throws IOException,
      ProcessDriver.ProcessCreationFailedException,
      InterruptedException
  {
    for( final HttpDaemonDriver driver : drivers.values() ) {
      driver.start( 20, TimeUnit.SECONDS ) ;
    }
  }

  /**
   * Normally, all daemons got strained and there are none to stop.
   * But if we get something like a limit on run count this might become useful.
   */
  private void shutdownDaemons() throws InterruptedException {
    for( final HttpDaemonDriver driver : drivers.values() ) {
      driver.shutdown( true ) ;
    }
  }

  private void warmup( final int passCount ) throws IOException {

    LOG.info( "Warming up " + name + ", " + passCount + " iterations..." ) ;
    upsizer.upsize() ;
    for( int pass = 1 ; pass <= passCount ; pass ++ ) {
      for( final HttpDaemonDriver driver : drivers.values() ) {
        final URL url = createRequestUrl( driver.getTcpPort() ) ;
        measurer.runDry( url ) ;
      }
      logPassCount( "  Performed warmup pass ", pass ) ;
    }
    LOG.info( "Warmup of " + name + " complete." ) ;


  }

  private static void logPassCount( final String message, final int pass ) {
    if( pass == 1 || pass == 10 || pass % 100 == 0 ) {
      LOG.debug( message + pass + "." ) ;
    }
  }


  protected final void runOnceWithMeasurementsOnEveryDaemon()
      throws InterruptedException, IOException 
  {
    upsizer.upsize() ;
    for( final Map.Entry< Version, HttpDaemonDriver > entry : drivers.entrySet() ) {
      final HttpDaemonDriver driver = entry.getValue() ;
      final Version version = entry.getKey() ;
      final URL url = createRequestUrl( driver.getTcpPort() ) ;
      final MEASUREMENT measurement = measurer.run( url ) ;
      final List< MEASUREMENT > measurementHistory = measurements.get( version ) ;
      if( measurement == null
       || measurer.detectStrain( measurementHistory, measurement )
      ) {
        driver.shutdown( true ) ;
        drivers.remove( version ) ;
      } else {
        measurementHistory.add( measurement ) ;
      }
    }
  }

  private URL createRequestUrl( final int tcpPort ) {
    try {
      return new URL( "http", "localhost", tcpPort, documentRequest ) ;
    } catch( MalformedURLException e ) {
      throw new RuntimeException( e ) ;
    }
  }


}

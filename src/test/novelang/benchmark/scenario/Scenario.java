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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Starts and queries several {@link novelang.benchmark.HttpDaemonDriver}s against an evolving
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
   * Keeps the monitoring state for one given {@link Version}.
   * Not static as it saves additional generic declaration.
   */
  private class Monitoring {
    public final HttpDaemonDriver driver ;
    public Termination termination = null ;
    public final List< MEASUREMENT > measurements = Lists.newArrayList() ;

    public Monitoring( final HttpDaemonDriver driver ) {
      this.driver = driver ;
    }
  }

  /**
   * Mutable object: contains list of non-null {@link Scenario.Monitoring} objects.
   */
  private final Map< Version, Monitoring > monitorings = Maps.newHashMap() ;
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
      final Iterable< Version > versions,
      final int firstTcpPort,
      final Measurer< MEASUREMENT > measurer
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

      final Monitoring monitoring = new Monitoring( httpDaemonDriver ) ;
      monitorings.put( version, monitoring ) ;
      tcpPort ++ ;
    }
  }

  private static final int DEFAULT_WARMUP_ITERATIONS = 10 ;
  private static final Integer DEFAULT_MAXIMUM_ITERATIONS = 100 ;
  private static final long LAUNCH_TIMEOUT_SECONDS = 20L ;

  /**
   * Call this once only.
   */
  public void run()
      throws InterruptedException, IOException, ProcessDriver.ProcessCreationFailedException
  {
    try {
      startDaemons() ;
      warmup( warmupIterations ) ;
      int activeCount = monitorings.size() ;
      int iterationCount = 1 ;
      for( ; isBelowMaximumIterations( iterationCount ) && activeCount > 0 ; iterationCount ++ ) {
        logPassCount( "Querying " + activeCount + " daemon(s), pass %d.", iterationCount ) ;
        runOnceWithMeasurementsOnEveryDaemon() ;
        final int updatedActiveCount = countActive( monitorings.values() ) ;
        final int difference = activeCount - updatedActiveCount ;
        LOG.info( "Done querying. " +
            ( difference > 0 ? difference + " daemon(s) terminated." : ""  ) ) ;
        activeCount = updatedActiveCount ;
      }
      if( hasReachedMaximumIterations( iterationCount ) ) {
        for( final Monitoring monitoring : monitorings.values() ) {
          if( monitoring.termination != null ) {
            monitoring.termination = ITERATION_COUNT_EXCEEDED ;
          }
        }
      }
    } finally {
      shutdownDaemons() ;
    }
  }

  private boolean isBelowMaximumIterations( final int iterationCount ) {
    return ( maximumIterations == null || iterationCount <= maximumIterations );
  }

  private boolean hasReachedMaximumIterations( final int iterationCount ) {
    return ( maximumIterations != null && iterationCount >= maximumIterations );
  }

  private int countActive( final Collection< Monitoring > monitorings ) {
    int count = 0 ;
    for( final Monitoring monitoring : monitorings ) {
      if( monitoring.termination == null ) {
        count ++ ;
      }
    }
    return count ;
  }


  public Map< Version, MeasurementBundle< MEASUREMENT > > getMeasurements() {
    final ImmutableMap.Builder< Version, MeasurementBundle< MEASUREMENT > > builder =
        new ImmutableMap.Builder< Version, MeasurementBundle< MEASUREMENT > >() ;
    for( final Map.Entry< Version, Monitoring > entry : monitorings.entrySet() ) {
      final MeasurementBundle< MEASUREMENT > measurementBundle =
          new MeasurementBundle< MEASUREMENT >(
              entry.getValue().measurements,
              entry.getValue().termination
          )
      ;
      builder.put( entry.getKey(), measurementBundle ) ;
    }
    return builder.build() ;
  }


  private void startDaemons()
      throws IOException,
      ProcessDriver.ProcessCreationFailedException,
      InterruptedException
  {
    for( final Monitoring monitoring : monitorings.values() ) {
      final HttpDaemonDriver driver = monitoring.driver ;
      driver.start( LAUNCH_TIMEOUT_SECONDS, TimeUnit.SECONDS ) ;
    }
  }

  /**
   * Normally, all daemons got strained and there are none to stop.
   * But if we get something like a limit on run count this might become useful.
   */
  private void shutdownDaemons() {
    for( final Monitoring monitoring : monitorings.values() ) {
      final HttpDaemonDriver driver = monitoring.driver ;
      if( monitoring.termination == null ) {
        try {
          driver.shutdown( true ) ;
        } catch( InterruptedException e ) {
          LOG.error( "Could not stop " + driver, e ) ;
        }
        monitoring.termination = LAST_CLEANUP ;
      }
    }
  }

  private final static Termination LAST_CLEANUP = new Termination( "Last cleanup" ) ;
  private final static Termination ITERATION_COUNT_EXCEEDED =
      new Termination( "Iteration count exceeded" ) ;

  private void warmup( final int passCount ) throws IOException {

    LOG.info( "Warming up " + name + ", " + passCount + " iterations..." ) ;
    upsizer.upsize() ;
    for( int pass = 1 ; pass <= passCount ; pass ++ ) {
      for( final Monitoring monitoring : monitorings.values() ) {
        final HttpDaemonDriver driver = monitoring.driver ;
        final URL url = createRequestUrl( driver.getTcpPort() ) ;
        measurer.runDry( url ) ;
      }
      logPassCount( "Performed warmup pass %d.", pass ) ;
    }
    LOG.info( "Warmup of " + name + " complete." ) ;


  }

  private static void logPassCount( final String message, final int pass ) {
    if( pass == 1 || pass == 2 || pass == 10 || pass % 100 == 0 ) {
      LOG.debug( String.format( message, pass ) ) ;
    }
  }


  private void runOnceWithMeasurementsOnEveryDaemon()
      throws InterruptedException, IOException 
  {
    upsizer.upsize() ;
    for( final Monitoring monitoring : monitorings.values() ) {
      final URL url = createRequestUrl( monitoring.driver.getTcpPort() ) ;
      if( monitoring.termination == null ) {
        final List< MEASUREMENT > measurementHistory = monitoring.measurements ;
        final Measurer.Result< MEASUREMENT > result = measurer.run( measurementHistory, url ) ;
        if( result.hasTermination() ) {
          monitoring.driver.shutdown( true ) ;
          monitoring.termination = result.getTermination() ;
        } else {
          measurementHistory.add( result.getMeasurement() ) ;
        }
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

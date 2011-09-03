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
package org.novelang.nhovestone;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.novelang.KnownVersions;
import org.novelang.Version;
import org.novelang.common.FileTools;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.nhovestone.driver.HttpDaemonDriver;
import org.novelang.nhovestone.scenario.Measurer;
import org.novelang.nhovestone.scenario.Upsizer;
import org.novelang.outfit.shell.ProcessCreationException;
import org.novelang.outfit.shell.ProcessInitializationException;
import org.apache.commons.lang.StringUtils;

/**
 * Starts and queries several {@link org.novelang.nhovestone.driver.HttpDaemonDriver}s against an evolving
 * source document.
 *
 * @author Laurent Caillette
 */
public class Scenario< UPSIZING, MEASUREMENT > {

  private static final Logger LOGGER = LoggerFactory.getLogger( Scenario.class );

  private final String name ;
  private final int warmupIterations ;
  private final Integer maximumIterations ;

  /**
   * Keeps the monitoring state for one given {@link org.novelang.Version}.
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
  private final Upsizer< UPSIZING > upsizer ;
  private final Measurer< MEASUREMENT > measurer ;

  public Scenario(
      final Configuration< ? extends Configuration, UPSIZING, MEASUREMENT > configuration
  )
      throws IOException
  {

    name = ( configuration.getScenarioName() == null ) ?
        getClass().getSimpleName() : configuration.getScenarioName() ;


    this.warmupIterations = configuration.getWarmupIterationCount() > 0 ?
        configuration.getWarmupIterationCount() : DEFAULT_WARMUP_ITERATIONS ;

    this.maximumIterations = configuration.getMaximumIterations() > 0 ?
        configuration.getMaximumIterations() : DEFAULT_MAXIMUM_ITERATIONS ;

    final File scenarioDirectory = FileTools.createFreshDirectory( 
        configuration.getScenariiDirectory(), FileTools.sanitizeFileName( name ) ) ;

    final File contentDirectory = FileTools.createFreshDirectory( scenarioDirectory, "content" ) ;

    upsizer = configuration.getUpsizerFactory().create( contentDirectory ) ;

    documentRequest = configuration.getUpsizerFactory().getDocumentRequest() ;
    Preconditions.checkArgument( ! StringUtils.isBlank( documentRequest ) ) ;

    this.measurer = Preconditions.checkNotNull( configuration.getMeasurer() ) ;
    
    Preconditions.checkArgument( configuration.getJvmHeapSizeMegabytes() > 0 ) ;

    int tcpPort = configuration.getFirstTcpPort() ;
    Preconditions.checkArgument( tcpPort > 0 ) ;
    
    for( final Version version : configuration.getVersions() ) {
      final File versionWorkingDirectory =
          FileTools.createFreshDirectory( scenarioDirectory, version.getName() ) ;

      final HttpDaemonDriver httpDaemonDriver = new HttpDaemonDriver(
          org.novelang.outfit.Husk.create( HttpDaemonDriver.Configuration.class )
          .withContentRootDirectory( contentDirectory )
          .withJvmHeapSizeMegabytes( configuration.getJvmHeapSizeMegabytes() )
          .withJavaClasses( KnownVersions.asJavaClasses(
              configuration.getInstallationsDirectory(), version ) )
          .withLogDirectory( versionWorkingDirectory )
          .withVersion( version )
          .withHttpPort( tcpPort )
          .withWorkingDirectory( versionWorkingDirectory )
      ) ;

      final Monitoring monitoring = new Monitoring( httpDaemonDriver ) ;
      monitorings.put( version, monitoring ) ;
      tcpPort ++ ;
    }
  }

  
  private static final int DEFAULT_WARMUP_ITERATIONS = 100 ;
  private static final Integer DEFAULT_MAXIMUM_ITERATIONS = 1000 ;
  private static final long LAUNCH_TIMEOUT_SECONDS = 20L ;

  /**
   * Call this once only.
   */
  public void run()
      throws
      InterruptedException,
      IOException,
      ProcessCreationException,
      ProcessInitializationException
  {
    try {
      startDaemons() ;
      warmup( warmupIterations ) ;
      int activeCount = monitorings.size() ;
      int iterationCount = 1 ;
      for( ; isBelowMaximumIterations( iterationCount ) && activeCount > 0 ; iterationCount ++ ) {
        logPassCount( "Querying " + activeCount + " daemon(s), pass %d...", iterationCount ) ;
        runOnceWithMeasurementsOnEveryDaemon() ;
        final int updatedActiveCount = countActive( monitorings.values() ) ;
        final int difference = activeCount - updatedActiveCount ;
        final String message = "{" + name + "} pass  " + iterationCount + 
            " on " + activeCount + " daemons. "  ;
        if( difference > 0 ) {
          LOGGER.info( message, ( difference + " daemon(s) terminated." ) ) ;
        } else {
          LOGGER.debug( message ) ;
        }
        activeCount = updatedActiveCount ;
      }
      if( hasReachedMaximumIterations( iterationCount ) ) {
        for( final Monitoring monitoring : monitorings.values() ) {
          if( monitoring.termination != null ) {
            monitoring.termination = Terminations.ITERATION_COUNT_EXCEEDED ;
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

  public List< UPSIZING > getUpsizings() {
    return upsizer.getUpsizings() ;
  }


  private void startDaemons()
      throws IOException,
      InterruptedException,
      ProcessCreationException,
      ProcessInitializationException
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
          LOGGER.error( e, "Could not stop " + driver ) ;
        } catch( IOException e ) {
          LOGGER.error( e, "Could not stop " + driver ) ;
        }
        monitoring.termination = Terminations.LAST_CLEANUP ;
      }
    }
  }

  public interface Terminations {
    Termination LAST_CLEANUP = new Termination( "Last cleanup" ) ;
    Termination ITERATION_COUNT_EXCEEDED = new Termination( "Iteration count exceeded" ) ;
  }

  private void warmup( final int passCount ) throws IOException {

    LOGGER.info( "Warming up " + name + ", " + passCount + " iterations..." ) ;
    upsizer.upsize() ;
    for( int pass = 1 ; pass <= passCount ; pass ++ ) {
      for( final Monitoring monitoring : monitorings.values() ) {
        final HttpDaemonDriver driver = monitoring.driver ;
        final URL url = createRequestUrl( driver.getTcpPort() ) ;
        measurer.runDry( url ) ;
      }
      logPassCount( "Performed warmup pass %d.", pass ) ;
    }
    LOGGER.info( "Warmup of ", name, " complete." ) ;


  }

  private static void logPassCount( final String message, final int pass ) {
    if( pass == 1 || pass == 2 || pass == 10 || pass % 100 == 0 ) {
      LOGGER.info( String.format( message, pass ) ) ;
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


  public interface Configuration< CONFIGURATION extends Configuration, UPSIZING, MEASUREMENT > {

    String getScenarioName() ;
    CONFIGURATION withScenarioName( String name ) ;

    int getWarmupIterationCount() ;
    CONFIGURATION withWarmupIterationCount( int count ) ;

    Integer getMaximumIterations() ;
    CONFIGURATION withMaximumIterations( Integer maximumOrNullForNoLimit ) ;

    File getScenariiDirectory() ;
    CONFIGURATION withScenariiDirectory( File scenariiDirectory ) ;

    Upsizer.Factory< UPSIZING > getUpsizerFactory() ;
    CONFIGURATION withUpsizerFactory( Upsizer.Factory< UPSIZING > factory ) ;

    File getInstallationsDirectory() ;
    CONFIGURATION withInstallationsDirectory( File directory ) ;

    Iterable< Version > getVersions() ;
    CONFIGURATION withVersions( Iterable< Version > versions ) ;

    int getFirstTcpPort() ;
    CONFIGURATION withFirstTcpPort( int firstTcpPort ) ;
    
    int getJvmHeapSizeMegabytes() ;
    CONFIGURATION withJvmHeapSizeMegabytes( int megabytes ) ;

    Measurer< MEASUREMENT > getMeasurer() ;
    CONFIGURATION withMeasurer( Measurer< MEASUREMENT > measurer ) ;

  }

}

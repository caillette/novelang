/*
 * Copyright (C) 2010 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package novelang.benchmark;

import novelang.Version;
import novelang.VersionFormatException;
import novelang.benchmark.scenario.TimeMeasurer;
import novelang.common.FileTools;
import novelang.novelist.Novelist;
import novelang.system.EnvironmentTools;
import novelang.system.Husk;
import novelang.system.Log;
import novelang.system.LogFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * @author Laurent Caillette
 */
public class Nhovestone {

  private static final Log LOG = LogFactory.getLog( Nhovestone.class );

  private final URI documentRequestUri;
  private final File benchmarkWorkingDirectory;
  private final File documentSourceDirectory ;

  
  private static final int WARMUP_DEFAULT_PASS_COUNT = 3000;
  private static final int WARMUP_DEFAULT_GHOSTWRITER_COUNT = 10;
  private static final int WARMUP_DEFAULT_ITERATION_COUNT_PER_PASS = 10 ;

  public Nhovestone(
      final URL documentRequestUri,
      final File benchmarkWorkingDirectory,
      final File documentSourceDirectory
  )
      throws URISyntaxException
  {
    this.documentRequestUri = documentRequestUri.toURI() ;
    this.benchmarkWorkingDirectory = benchmarkWorkingDirectory;
    this.documentSourceDirectory = documentSourceDirectory ;
  }

  public void warmup( final int passCount ) throws IOException {
    final StopWatch stopWatch = new StopWatch() ;
    stopWatch.start() ;
    final Novelist novelist = new Novelist(
        documentSourceDirectory, 
        getClass().getSimpleName(),
        new Novelist.LevelGeneratorSupplierWithDefaults(),
        WARMUP_DEFAULT_GHOSTWRITER_COUNT
    ) ;
    novelist.write( WARMUP_DEFAULT_ITERATION_COUNT_PER_PASS ) ;
    stopWatch.stop() ;
    LOG.info( "Wrote files for warmup in " + stopWatch ) ;
    LOG.info( "Warming up, " + passCount + " iterations..." ) ;
    for( int pass = 1 ; pass <= passCount ; pass ++ ) {
      requestWholeDocument() ;
      if( pass == 1 || pass == 10 || pass % 100 == 0 ) {
        LOG.debug( "  Performed pass " + pass + ".");
      }
    }
    LOG.info( "Warmup complete." ) ;
  }

  /**
   * Runs with no exit condition, increasing {@link novelang.novelist.Novelist.Ghostwriter} count
   * and appending one level to everyone.
   */
  public void runForever() throws IOException {
    final Novelist.LevelGeneratorSupplierWithDefaults levelGenerator = 
        new Novelist.LevelGeneratorSupplierWithDefaults() ;
    final Novelist novelist = new Novelist(
        documentSourceDirectory, 
        getClass().getSimpleName(),
        levelGenerator,
        0
    ) ;
    boolean loop = true ;
    while( loop ) {
      novelist.addGhostwriter() ;
      novelist.write( 1 ) ;
      loop = requestWholeDocument() > -1L ;
    }
    
  }

  private static final int BUFFER_SIZE = 1024 * 1024 ;
  private byte[] buffer = new byte[ BUFFER_SIZE ] ;


  @Deprecated
  public long requestWholeDocument() throws IOException {
    final AbstractHttpClient httpClient = new DefaultHttpClient() ;

    final HttpParams parameters = new BasicHttpParams() ;
    parameters.setIntParameter( CoreConnectionPNames.SO_TIMEOUT, TimeMeasurer.TIMEOUT_MILLISECONDS ) ;
    final HttpGet httpGet = new HttpGet( documentRequestUri ) ;
    httpGet.setParams( parameters ) ;
    final HttpResponse httpResponse = httpClient.execute( httpGet ) ;
    if( httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK ) {
      final InputStream inputStream = httpResponse.getEntity().getContent() ;
      try {
        final long startTime = System.currentTimeMillis() ;
        for( int read = 0 ; read > -1 ; read = inputStream.read( buffer ) ) { }
        final long endTime = System.currentTimeMillis() ;
        return endTime - startTime ;
      } finally {
        inputStream.close() ;
      }
    } else {
      return -1L ;
    }
  }

  @Deprecated
  public void startAndStopHttpDaemon()
      throws
      VersionFormatException,
      IOException,
      ProcessDriver.ProcessCreationFailedException,
      InterruptedException
  {
    final File contentDirectory = new File( benchmarkWorkingDirectory, "content" ) ;
    FileTools.createDirectoryForSure( contentDirectory ) ;

    final Version version = Version.parse( "0.41.0" );

    final File installationDirectory =
        new File( new File( "distrib" ), "Novelang-" + version.getName() ) ;

    final File applicationWorkingDirectory =
        new File( benchmarkWorkingDirectory, version.getName() ) ;
    FileTools.createDirectoryForSure( applicationWorkingDirectory ) ;

    final File logsDirectory = new File( applicationWorkingDirectory, "logs" ) ;
    FileTools.createDirectoryForSure( logsDirectory ) ;


    final HttpDaemonDriver httpDaemonDriver = new HttpDaemonDriver(
        Husk.create( HttpDaemonDriver.Configuration.class )
        .withWorkingDirectory( applicationWorkingDirectory )
        .withContentRootDirectory( contentDirectory )
        .withJvmHeapSizeMegabytes( 32 )
        .withInstallationDirectory( new File( "distrib" ) )
        .withLogDirectory( logsDirectory )
        .withVersion( version )
        .withTcpPort( 8091 )
    ) ;
    httpDaemonDriver.start( 1L, TimeUnit.MINUTES ) ;

    TimeUnit.SECONDS.sleep( 1L ) ;

    httpDaemonDriver.shutdown( true ) ; // TODO send some preliminary signal.
  }


  private static File createBenchmarkWorkingDirectory() throws IOException {
    final File workingDirectory = new File( "Nhovestone" ) ;
    FileUtils.deleteDirectory( workingDirectory ) ;
    if( ! workingDirectory.mkdirs() ) {
      LOG.warn( "Didn't create working directory as expected: '" + workingDirectory + "'" ) ;
    } ;
    return workingDirectory ;
  }

  public static void main( final String[] args )
      throws
      IOException,
      URISyntaxException,
      ProcessDriver.ProcessCreationFailedException,
      VersionFormatException,
      InterruptedException
  {

    EnvironmentTools.logSystemProperties() ;

    new Nhovestone(
        new URL( "http://localhost:8080/scratch/novelist/book.html" ),
        createBenchmarkWorkingDirectory(),
        new File( "scratch/novelist" )
    ).startAndStopHttpDaemon() ;
//    ).runForever() ;
//    ).warmup( WARMUP_DEFAULT_PASS_COUNT ) ;

    System.exit( 0 ) ;
  }

}

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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * @author Laurent Caillette
 */
public class TimeMeasurer implements Measurer< TimeMeasurement > {


  public static final int TIMEOUT_MILLISECONDS = 5 * 60 * 1000 ;

  private static final int BUFFER_SIZE = 1024 * 1024 ;
  private byte[] buffer = new byte[ BUFFER_SIZE ] ;

  public TimeMeasurement run( final URL url ) throws IOException {
    // HttpKit avoids including creation of HttpClient stuff into measurement. Yeah!
    final HttpKit httpKit = createHttpKit( url ) ;
    final long startTime = System.currentTimeMillis() ;
    run( httpKit ) ;
    final long endTime = System.currentTimeMillis() ;
    return new TimeMeasurement( endTime - startTime ) ;
  }


  public void runDry( final URL url ) throws IOException {
    run( createHttpKit( url ) ) ;
  }

  private static final long GUARD_FACTOR = 4L ;

  public boolean detectStrain(
      final List< TimeMeasurement > previousMeasurements,
      final TimeMeasurement lastMeasurement
  ) {
    final int measurementCount = previousMeasurements.size() ;
    if( measurementCount == 0 ) {
      return false ;
    } else {
      final long first = previousMeasurements.get( 0 ).getTimeMilliseconds() ;
      final long middle = previousMeasurements.get( measurementCount / 2 ).getTimeMilliseconds() ;
      final long delta = middle - first ;
      final long linearExtrapolationToLast = first + ( 2L * delta ) ;
      final long maximum = linearExtrapolationToLast + GUARD_FACTOR * delta ;
      return lastMeasurement.getTimeMilliseconds() < maximum ;
    }
  }


// =================
// Shared HTTP stuff
// =================

  private HttpKit createHttpKit( final URL url ) {
    final AbstractHttpClient httpClient = new DefaultHttpClient() ;
    final HttpParams parameters = new BasicHttpParams() ;
    parameters.setIntParameter(
        CoreConnectionPNames.SO_TIMEOUT, TimeMeasurer.TIMEOUT_MILLISECONDS ) ;
    final HttpGet httpGet = new HttpGet( asUri( url ) ) ;
    httpGet.setParams( parameters ) ;
    return new HttpKit( httpClient, httpGet, buffer ) ;
  }
  
  private static void run( final HttpKit httpKit ) throws IOException {
    final HttpResponse httpResponse = httpKit.httpClient.execute( httpKit.httpGet ) ;
    if( httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK ) {
      final InputStream inputStream = httpResponse.getEntity().getContent() ;
      try {
        for( int read = 0 ; read > -1 ; read = inputStream.read( httpKit.buffer ) ) { }
      } finally {
        inputStream.close() ;
      }
    }    
  }
  
  private static class HttpKit {
    public final AbstractHttpClient httpClient ; 
    public final HttpGet httpGet ;
    public final byte[] buffer ;

    private HttpKit(
        final AbstractHttpClient httpClient,
        final HttpGet httpGet,
        final byte[] buffer
    ) {
      this.httpClient = httpClient;
      this.httpGet = httpGet;
      this.buffer = buffer;
    }
  }

  private static URI asUri( final URL url ) {
    try {
      return url.toURI() ;
    } catch( URISyntaxException e ) {
      throw new RuntimeException( e ) ;
    }
  }

}

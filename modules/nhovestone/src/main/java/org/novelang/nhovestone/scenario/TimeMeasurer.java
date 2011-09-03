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
package org.novelang.nhovestone.scenario;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.commons.math.stat.regression.SimpleRegression;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

import org.novelang.nhovestone.Termination;

/**
 * @author Laurent Caillette
 */
public class TimeMeasurer implements Measurer< TimeMeasurement > {


  public static final int TIMEOUT_MILLISECONDS = 5 * 60 * 1000 ;
  private static final int MINIMUM_MEASUREMENT_COUNT_FOR_REGRESSION = 50 ;
  private static final double GUARD_FACTOR = 10.0 ;

  private static final int BUFFER_SIZE = 1024 * 1024 ;
  private final byte[] buffer = new byte[ BUFFER_SIZE ] ;

  @Override
  public Result< TimeMeasurement > run(
      final List< TimeMeasurement > previousMeasurements,
      final URL url
  ) throws IOException {
    // HttpKit avoids including creation time of HttpClient stuff into measurement. Yeah!
    final HttpKit httpKit = createHttpKit( url ) ;
    final long startTime = System.currentTimeMillis() ;
    final Termination termination = run( httpKit ) ;
    final long endTime = System.currentTimeMillis() ;
    if( termination == null ) {
      final TimeMeasurement lastMeasurement = new TimeMeasurement( endTime - startTime ) ;
      if( detectStrain( previousMeasurements, lastMeasurement ) ) {
        return Result.create( Terminations.STRAIN ) ;
      } else {
        return Result.create( lastMeasurement ) ;
      }
    } else {
      return Result.create( termination ) ;
    }
  }


  @Override
  public Termination runDry( final URL url ) {
    return run( createHttpKit( url ) ) ;
  }



  /**
   * Strain means last request exceeded of more than {@value #GUARD_FACTOR} times the time
   * extrapolated from first half of measurements, using linear regression.
   *
   * @param previousMeasurements a non-null object, contains no null.
   * @param lastMeasurement a non-null object.
   * @return true if strain detected, false otherwise.
   */
  private static boolean detectStrain(
      final List< TimeMeasurement > previousMeasurements,
      final TimeMeasurement lastMeasurement
  ) {
    final int measurementCount = previousMeasurements.size() ;
    if( measurementCount < MINIMUM_MEASUREMENT_COUNT_FOR_REGRESSION ) {
      return false ;
    } else {
      final SimpleRegression simpleRegression = new SimpleRegression() ;
      for( int i = 0 ; i < measurementCount / 2 ; i ++ ) {
        final TimeMeasurement measurement = previousMeasurements.get( i ) ;
        simpleRegression.addData( ( double ) i, ( double ) measurement.getTimeMilliseconds() ) ;
      }
      final double extrapolated = simpleRegression.predict( ( double ) measurementCount ) ;
      if( Double.isNaN( extrapolated ) ) {
        return false ;
      } else {
        final double increasing = extrapolated - simpleRegression.getIntercept() ;
        final double highLimit = simpleRegression.getIntercept() + increasing * GUARD_FACTOR ;
        return lastMeasurement.getTimeMilliseconds() > ( long ) highLimit ;
      }
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


  private static Termination run( final HttpKit httpKit ) {
    final HttpResponse httpResponse;
    try {
      httpResponse = httpKit.httpClient.execute( httpKit.httpGet );
      final int statusCode = httpResponse.getStatusLine().getStatusCode();
      if( statusCode == HttpStatus.SC_OK ) {
        final InputStream inputStream = httpResponse.getEntity().getContent() ;
        try {
          for( int read = 0 ; read > -1 ; read = inputStream.read( httpKit.buffer ) ) { }
          return null ;
        } finally {
          inputStream.close() ;
        }
      } else {
        return Terminations.HTTP_CODE;
      }
    } catch( IOException e ) {
      return Terminations.CALLER_EXCEPTION ;
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


// ==================  
// Termination causes
// ==================

  public interface Terminations {
    Termination HTTP_CODE = new Termination( "HTTP response code not OK" ) ;
    Termination CALLER_EXCEPTION = new Termination( "Exception occured" ) ;
    Termination STRAIN = new Termination( "Daemon strained") ;
  }

}

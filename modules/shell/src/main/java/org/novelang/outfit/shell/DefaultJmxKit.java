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

package org.novelang.outfit.shell;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.ServiceUnavailableException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.shell.insider.Insider;

/**
 * Default implementation with no authentication nor SSL.
 *
 * @author Laurent Caillette
 */
public class DefaultJmxKit implements BootstrappingJmxKit {

  private static final Logger LOGGER = LoggerFactory.getLogger( DefaultJmxKit.class ) ;

  private static final Object lock = new Object() ;

  private final ImmutableMap< String, ? > environment ;

  /**
   * Constructor.
   *
   * @param environment a possibly null object.
   */
  public DefaultJmxKit( final ImmutableMap< String, ? > environment ) {
    this.environment = environment ;
  }

  public DefaultJmxKit() {
    this( null ) ;
  }


  @Override
  public JMXConnector createJmxConnector( final String host, final int port ) throws InterruptedException, IOException {
    synchronized( lock ) {
      final JMXServiceURL url ;
        try {
          url = new JMXServiceURL( "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi" );
        } catch( MalformedURLException e ) {
          throw new Error( e ) ;
        }
        LOGGER.info( "Connecting to ", url, " ..." ) ;
        int attemptCount = 0 ;
        while( true ) {
          try {
            final JMXConnector jmxConnector = JMXConnectorFactory.connect( url, environment ) ;
            LOGGER.debug( "Successfully connected to ", url ) ;
            return jmxConnector ;
          } catch( IOException e ) {
            final Throwable cause = e.getCause() ;
            if(    cause instanceof ServiceUnavailableException
                || cause instanceof java.rmi.ConnectException
            ) {
              if( attemptCount ++ < 10 ) {
                LOGGER.debug( "Couldn't connect to ", url, ", waiting a bit before another attempt..." ) ;
                TimeUnit.MILLISECONDS.sleep( 200L ) ;
              } else {
                throw e ;
              }
            }
          }
        }
      }
    }

  @Override
  public ImmutableSet<String> getJvmProperties(
          final int jmxPortForJvmProperty,
          final Integer heartbeatMaximumPeriod
  ) {
    final ImmutableSet.Builder< String > argumentList = ImmutableSet.builder() ;
    argumentList.add( "-Dcom.sun.management.jmxremote.port=" + jmxPortForJvmProperty ) ;

    // No security here.
    argumentList.add( "-Dcom.sun.management.jmxremote.authenticate=false" ) ;
    argumentList.add( "-Dcom.sun.management.jmxremote.ssl=false" ) ;

    argumentList.add(
        "-javaagent:" + AgentFileInstaller.getInstance().getJarFile().getAbsolutePath()
        + ( heartbeatMaximumPeriod == null
            ? ""
            : "=" + Insider.MAXIMUM_HEARTBEATDELAY_PARAMETERNAME + heartbeatMaximumPeriod
        )
    ) ;

    return argumentList.build() ;
  }
}

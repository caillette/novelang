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
import java.util.Map;
import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MBeanRegistrationException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * For a given host and port, takes care of every JMX connections and beans and maps them to {@link JmxKit} instances.
 * <p>
 * Inside the Novelang project, {@link org.novelang.outfit.shell.JavaShell} doesn't need more that one {@link JmxKit}
 * instance. But one (commercial) project the author is working on needs this kind of mapping. 
 * <p>
 * This implementation is not thread-safe.
 *
 * @author Laurent Caillette
 */
public class JmxBeanPool {

  private final String host ;
  private final int port ;

  public JmxBeanPool( final String host, final int port ) {
    checkArgument( ! StringUtils.isBlank( host ) ) ;
    checkArgument( port > 0 ) ;
    this.host = host ;
    this.port = port ;
  }

  private final Map< JmxKit, JmxConnectionBundle> connectionBundles = Maps.newHashMap() ;

  /**
   * This is a {@code Map} between a ({@code ObjectName}, {@link org.novelang.outfit.shell.JmxKit}) pair,
   * and a ({@link ObjectName}, JMX bean proxy, and {@link JmxConnectionBundle}) triplet.
   */
  private final Map<JmxBeanKey, JmxBeanValue> managedBeans = Maps.newHashMap() ;


  private JmxConnectionBundle getOrCreateConnectionBundle( final JmxKit someJmxKit )
          throws IOException, InterruptedException
  {
    final JmxConnectionBundle connectionBundle ;
    final JmxConnectionBundle maybeConnectionBundle = connectionBundles.get( someJmxKit ) ;
    if( maybeConnectionBundle == null ) {
      final JMXConnector jmxConnector = someJmxKit.createJmxConnector( host, port ) ;
      connectionBundle = new JmxConnectionBundle( jmxConnector, jmxConnector.getMBeanServerConnection() ) ;
      connectionBundles.put( someJmxKit, connectionBundle ) ;
    } else {
      connectionBundle = maybeConnectionBundle ;
    }
    return connectionBundle ;
  }

  public < BEAN > BEAN getManagedBean(
      final Class< BEAN > beanClass,
      final ObjectName beanName,
      final JmxKit jmxKit
  ) throws IOException, InterruptedException
  {
    checkNotNull( beanClass ) ;
    checkNotNull( beanName ) ;
    checkNotNull( jmxKit ) ;

    final JmxBeanKey key = new JmxBeanKey( beanName, jmxKit ) ;

    final JmxBeanValue value = managedBeans.get( key ) ;
    final BEAN bean ;
    if( value == null ) {
      final JmxConnectionBundle connectionBundle = getOrCreateConnectionBundle( jmxKit ) ;
      bean = JMX.newMBeanProxy( connectionBundle.connection, beanName, beanClass, true ) ;
      final JmxBeanValue newValue = new JmxBeanValue(
          connectionBundle,
          beanName,
          bean
      ) ;
      managedBeans.put( key, newValue ) ;
    } else {
      //noinspection unchecked
      bean = ( BEAN ) value.getJmxBean() ;
    }
    return bean ;
   }



  /**
   * Unregisters JMX Beans and closes the {@link javax.management.remote.JMXConnector}s.
   * This method should close the default JMX connector o {@link org.novelang.outfit.shell.JavaShell} because, when there is one, there is always
   * a registered {@link org.novelang.outfit.shell.insider.Insider} at startup so it should appear inside the
   * {@code Map}.
   *
   */
  public void disconnectAll() {

    // First, unregister all beans.
    for( final JmxBeanValue value : managedBeans.values() ) {
      try {
        value.getConnectionBundle().connection.unregisterMBean( value.getObjectName() ) ;
      } catch( InstanceNotFoundException e ) {
        logCouldntUnregister( value.getObjectName(), e ) ;
      } catch( MBeanRegistrationException e ) {
        logCouldntUnregister( value.getObjectName(), e ) ;
      } catch( IOException e ) {
        logCouldntUnregister( value.getObjectName(), e ) ;
      }
    }

    // Now we can close safely. The JMXConnector#close() method has no effect when called more than once.
    for( final JmxBeanValue value : managedBeans.values() ) {
      try {
        value.getConnectionBundle().connector.close() ;
      } catch( IOException e ) {
        logCouldntUnregister( value.getConnectionBundle().connector, e ) ;
      }
    }
  }

  protected void logCouldntUnregister( final Object culprit, final Exception e ) {
/*
    LOG.debug( "Couldn't disconnect or unregister " + culprit + ", cause: " + e.getClass() +
        " (may be normal if other VM terminated)." ) ;
*/
  }

}

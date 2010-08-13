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
package novelang.system.shell;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import novelang.system.Log;
import novelang.system.LogFactory;
import novelang.system.shell.insider.Insider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Starts and shuts down a JVM with an {@link novelang.system.shell.insider.InsiderAgent}
 * that installs an {@link Insider} for various tasks.
 * This class takes great care of JVM shutdown.
 * <ul>
 *   <li>
 *     With a {@link ShutdownStyle#GENTLE} shutdown, it calls
 *     {@link novelang.system.shell.insider.Insider#shutdown()} that attempts to perform
 *     a nice {@code System.exit()} that calls shutdown hooks.
 *   </li> <li>
 *     The JVM starts with a special system property
 *     {@value ShutdownTools#SHUTDOWN_TATTOO_PROPERTYNAME} used by
 *     {@link ShutdownTools#shutdownAllTattooedVirtualMachines()} to recognize JVMs to shut down.
 *   </li> <li>
 *     The {@link Insider} in the JVM halts when
 *     {@link novelang.system.shell.insider.Insider#keepAlive()} wasn't called for a few seconds.
 *     The {@link JavaShell} runs a thread doing this, so if its JVM gets down, the JVM it started
 *     soon goes down, too.
 *   </li>
 * </ul>
 *
 * @author Laurent Caillette
 */
public class JavaShell extends Shell {

  private static final Log LOG = LogFactory.getLog( JavaShell.class ) ;
  private final Object lock = new Object() ;
  
  private final int jmxPort ;

  public JavaShell(
      final String nickname,
      final File workingDirectory,
      final List< String > jvmArguments,
      final JavaClasses javaClasses,
      final List< String > programArguments,
      final Predicate< String > startupSensor,
      final int jmxPort
  ) {
    super(
        workingDirectory,
        nickname,
        createProcessArguments( jvmArguments, jmxPort, javaClasses, programArguments ),
        startupSensor
    ) ;
    Preconditions.checkArgument( jmxPort > 0 ) ;
    this.jmxPort = jmxPort ;
  }

  private static List< String > createProcessArguments(
      final List< String > jvmArguments,
      final int jmxPort,
      final JavaClasses javaClasses,
      final List< String > programArguments
  ) {
    final List< String > argumentList = Lists.newArrayList() ;

    // This is a very optimistic approach for obtaining Java executable.
    // TODO: see how Ant's Java task solves this.
    argumentList.add( "java" ) ;

    argumentList.add( "-D" + ShutdownTools.SHUTDOWN_TATTOO_PROPERTYNAME ) ;

    argumentList.add( "-Dcom.sun.management.jmxremote.port=" + jmxPort ) ;

    // No security yet.
    argumentList.add( "-Dcom.sun.management.jmxremote.authenticate=false" ) ;
    argumentList.add( "-Dcom.sun.management.jmxremote.ssl=false" ) ;

    argumentList.add( "-javaagent:" + AgentFileInstaller.getJarFile().getAbsolutePath() ) ;

    argumentList.addAll( jvmArguments ) ;
    argumentList.addAll( javaClasses.asStringList() ) ;
    argumentList.addAll( programArguments ) ;

    return argumentList ;
  }

  private JMXConnector jmxConnector = null ;
  private MBeanServerConnection jmxConnection = null ;
  private Insider insider = null ;
  private HeartbeatSender heartbeatThread = null ;

  @Override
  public void start( final long timeout, final TimeUnit timeUnit )
      throws IOException, InterruptedException, ProcessCreationFailedException
  {
    synchronized( lock ) {
      super.start( timeout, timeUnit ) ;
      connect() ;
      heartbeatThread = new HeartbeatSender( insider, getNickname() ) ;
    }
  }

  /**
   * Requests the underlying process to shutdown. When requested to shut down in a
   * {@link ShutdownStyle#GENTLE} or {@link ShutdownStyle#WAIT} style, the method waits until the
   * process gently terminates.
   *
   * @param shutdownStyle a non-null object.
   * @throws InterruptedException should not happen.
   * @throws IOException too bad.
   */
  public void shutdown( final ShutdownStyle shutdownStyle )
      throws InterruptedException, IOException
  {
    synchronized( lock ) {

      heartbeatThread.stop() ;
      heartbeatThread = null ;

      try {
        switch( shutdownStyle ) {
          case GENTLE :
            try {
              LOG.info( "Requesting shutdown (through JMX) for ", getNickname(), "..." ) ;
              insider.shutdown() ;
            } catch( Exception e ) {
              shutdownProcess( true ) ;
            }
            // ... After asking for shutdown, we wait for natural process end. TODO: add timeout.
          case WAIT :
            shutdownProcess( false ) ;
            break ;
          case FORCED :
            shutdownProcess( true ) ;
            break ;
          default :
            throw new IllegalArgumentException( "Unsupported: " + shutdownStyle ) ;
        }
      } finally {
        disconnect() ;
      }
    }
    LOG.info( "Shutdown (" + shutdownStyle + ") complete for " + getNickname() + "." ) ;
  }



// ===
// JMX
// ===


  private void connect() throws IOException {
    final JMXServiceURL url;
    try {
      url = new JMXServiceURL( "service:jmx:rmi:///jndi/rmi://:" + jmxPort + "/jmxrmi" );
    } catch( MalformedURLException e ) {
      throw new Error( e );
    }
    jmxConnector = JMXConnectorFactory.connect( url, null ) ;
    jmxConnection = jmxConnector.getMBeanServerConnection() ;
    insider = getManagedBean( Insider.class, Insider.NAME ) ;
  }


  private final Map< ObjectName, Object > managedBeans = Maps.newHashMap() ;

  public < BEAN > BEAN getManagedBean( final Class< BEAN > beanClass, final ObjectName beanName ) {
    checkNotNull( beanClass ) ;
    checkNotNull( beanName ) ;

    synchronized( lock ) {
      final Object cachedBean = managedBeans.get( beanName ) ;
      final BEAN bean ;
      if( cachedBean == null ) {
        bean = JMX.newMBeanProxy( jmxConnection, beanName, beanClass, true ) ;
        managedBeans.put( beanName, bean ) ;
      } else {
        //noinspection unchecked
        bean = ( BEAN ) cachedBean ;
      }
      return bean ;
    }
  }


  private void disconnect() {
    for( final ObjectName beanName : managedBeans.keySet() ) {
      try {
        jmxConnection.unregisterMBean( beanName ) ;
      } catch( InstanceNotFoundException e ) {
        logCouldntUnregister( beanName, e ) ;
      } catch( MBeanRegistrationException e ) {
        logCouldntUnregister( beanName, e ) ;
      } catch( IOException e ) {
        logCouldntUnregister( beanName, e ) ;
      }
    }
    managedBeans.clear() ;
    try {
      jmxConnector.close() ;
    } catch( IOException e ) {
      logCouldntUnregister( jmxConnector, e ) ;
    }
    jmxConnection = null ;
    jmxConnector = null ;
  }

  private static void logCouldntUnregister( final Object culprit, final Exception e ) {
    LOG.info( "Couldn't disconnect or unregister " + culprit + ", cause: " + e.getClass() +
        " (may be normal if other VM terminated)." ) ;
  }


}

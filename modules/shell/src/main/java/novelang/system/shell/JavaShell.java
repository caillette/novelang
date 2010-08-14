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

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
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
import javax.naming.ServiceUnavailableException;
import novelang.system.Log;
import novelang.system.LogFactory;
import novelang.system.shell.insider.Insider;

import static com.google.common.base.Preconditions.checkArgument;
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
  private final Integer heartbeatPeriodMilliseconds ;

  public JavaShell( final Parameters parameters ) {
    super(
        parameters.getWorkingDirectory(),
        parameters.getNickname(),
        createProcessArguments(
            parameters.getJvmArguments(),
            parameters.getJmxPort(),
            parameters.getJavaClasses(),
            parameters.getProgramArguments(),
            parameters.getHeartbeatFatalDelayMilliseconds()
        ),
        parameters.getStartupSensor()
    ) ;
    checkArgument( parameters.getJmxPort() > 0 ) ;
    this.jmxPort = parameters.getJmxPort() ;
    this.heartbeatPeriodMilliseconds = parameters.getHeartbeatPeriodMilliseconds() ;
  }


  private static List< String > createProcessArguments(
      final List< String > jvmArguments,
      final int jmxPort,
      final JavaClasses javaClasses,
      final List< String > programArguments,
      final Integer heartbeatMaximumPeriod
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

    argumentList.add(
        "-javaagent:" + AgentFileInstaller.getJarFile().getAbsolutePath()
        + ( heartbeatMaximumPeriod == null
            ? ""
            : "=" + Insider.MAXIMUM_HEARTBEATDELAY_PARAMETERNAME + heartbeatMaximumPeriod
        )
    ) ;

    if( jvmArguments != null ) {
      argumentList.addAll( jvmArguments ) ;
    }

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
      if( heartbeatPeriodMilliseconds == null ) {
        heartbeatThread = new HeartbeatSender( insider, getNickname() ) ;
      } else {
        heartbeatThread = new HeartbeatSender(
            insider, getNickname(), heartbeatPeriodMilliseconds ) ;
      }
    }
  }

  public boolean isUp() {
    synchronized( lock ) {
      if( insider == null ) {
        throw new IllegalStateException( "Not ready" ) ;
      }
      try {
        insider.checkAlive() ;
        return true ;
      } catch( Exception e ) {
        return false ;
      }
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


  private void connect() throws IOException, InterruptedException {
    final JMXServiceURL url;
    try {
      url = new JMXServiceURL( "service:jmx:rmi:///jndi/rmi://:" + jmxPort + "/jmxrmi" );
    } catch( MalformedURLException e ) {
      throw new Error( e );
    }
    connectWithRetries( url ) ;
    jmxConnection = jmxConnector.getMBeanServerConnection() ;
    insider = getManagedBean( Insider.class, Insider.NAME ) ;
  }

  private void connectWithRetries( final JMXServiceURL url )
      throws IOException, InterruptedException
  {
    int attemptCount = 0 ;
    while( true ) {
      try {
        jmxConnector = JMXConnectorFactory.connect( url, null ) ;
        return ;
      } catch( IOException e ) {
        if( e.getCause() instanceof ServiceUnavailableException ) {
          if( attemptCount ++ < 10 ) {
            LOG.debug( "Couldn't connect to " + url + ", waiting a bit before another attempt..." ) ;
            TimeUnit.MILLISECONDS.sleep( 100L ) ;
          } else {
            throw e ;
          }
        }
      }
    }
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
    LOG.debug( "Couldn't disconnect or unregister " + culprit + ", cause: " + e.getClass() +
        " (may be normal if other VM terminated)." ) ;
  }


  public static interface Parameters {

    String getNickname() ;
    Parameters withNickname( String nickname ) ;

    File getWorkingDirectory() ;
    Parameters withWorkingDirectory( File workingDirectory ) ;

    ImmutableList< String > getJvmArguments() ;
    Parameters withJvmArguments( ImmutableList< String > jvmArguments ) ;

    JavaClasses getJavaClasses() ;
    Parameters withJavaClasses( JavaClasses javaClasses ) ;

    ImmutableList< String > getProgramArguments() ;
    Parameters withProgramArguments( ImmutableList< String > programArguments ) ;

    Predicate< String > getStartupSensor() ;
    Parameters withStartupSensor( Predicate< String > startupSensor ) ;

    int getJmxPort() ;
    Parameters withJmxPort( int jmxPort ) ;

    Integer getHeartbeatFatalDelayMilliseconds() ;
    Parameters withHeartbeatFatalDelayMilliseconds( Integer maximum ) ;

    Integer getHeartbeatPeriodMilliseconds() ;
    Parameters withHeartbeatPeriodMilliseconds( Integer maximum ) ;

  }
}

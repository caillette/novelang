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
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
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
public class JavaShell extends ProcessShell {

  private static final Log LOG = LogFactory.getLog( JavaShell.class ) ;
  
  private final int jmxPort ;
  private final Integer heartbeatPeriodMilliseconds ;

  /**
   * A semaphore that is slightly redundant with the one inside {@link ProcessShell}
   * but this helps preserving encapsulation.
   */
  private final Semaphore ownStartupSensorSemaphore ;

  /**
   * The count of permits that {@link #ownStartupSensorSemaphore} will try to acquire.
   * It depends upon the number of sensors inside {@link TieredStartupSensor}.
   */
  private final int startupSensorSemaphorePermitCount ;

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
        createTieredStartupSensor(
            LOCAL_INSIDER_STARTED,
            parameters.getStartupSensor()
        )
    );
    checkArgument( parameters.getJmxPort() > 0 ) ;
    this.jmxPort = parameters.getJmxPort() ;
    this.heartbeatPeriodMilliseconds = parameters.getHeartbeatPeriodMilliseconds() ;
    this.ownStartupSensorSemaphore = THREADLOCAL_SEMAPHORE.get() ;
    this.startupSensorSemaphorePermitCount = THREADLOCAL_PERMITCOUNT.get() ;
    THREADLOCAL_SEMAPHORE.set( null ) ;
    THREADLOCAL_PERMITCOUNT.set( null ) ;

    // Code checker happy: avoid access in both synchronized and unsynchronized context.
    synchronized( stateLock ) {
      jmxConnector = null ;
      jmxConnection = null ;
      heartbeatSender = null ;
      insider = null ;
    }
  }


  private static final Predicate< String > LOCAL_INSIDER_STARTED = new Predicate< String >() {
    @Override
    public boolean apply( final String input ) {
      // Seems printed by the JVM itself.
      return input.contains( "Loaded novelang.system.shell.insider.InsiderAgent." ) ;
    }
  } ;




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

    // Log JMX activity. Didn't prove useful.
//    argumentList.add( "-Djava.util.logging.config.file=" +
//        JAVA_UTIL_LOGGING_CONFIGURATION_FILE.getAbsolutePath() ) ;

    argumentList.add(
        "-javaagent:" + AgentFileInstaller.getInstance().getJarFile().getAbsolutePath()
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

  private JMXConnector jmxConnector ;
  private MBeanServerConnection jmxConnection;
  private Insider insider;
  private HeartbeatSender heartbeatSender;

  /**
   * We need a dedicated lock for {@link #processIdentifier} otherwise there is a deadlock
   * when watcher threads try to access it while {@link #connect()} executes.
   */
  private final Object processIdentifierLock = new Object() ;

  private int processIdentifier = JavaShellTools.UNDEFINED_PROCESS_ID ;

  @Override
  public String getNickname() {
    final String defaultNickname = super.getNickname() ;
    final int currentIdentifier ;
    synchronized( processIdentifierLock ) {
      currentIdentifier = processIdentifier ;
    }
    return defaultNickname + (
        currentIdentifier == JavaShellTools.UNDEFINED_PROCESS_ID ? "" : "#" + currentIdentifier ) ;
  }

  @Override
  public void start( final long timeout, final TimeUnit timeUnit )
      throws IOException, InterruptedException, ProcessCreationFailedException
  {
    synchronized( stateLock ) {
      super.start( timeout, timeUnit ) ;
      connect() ;

      insider.keepAlive() ;

      if( heartbeatPeriodMilliseconds == null ) {
        heartbeatSender = new HeartbeatSender( insider, getNickname() ) ;
      } else {
        heartbeatSender = new HeartbeatSender(
            insider, getNickname(), heartbeatPeriodMilliseconds ) ;
      }
    }
    synchronized( processIdentifierLock ) {
      final RuntimeMXBean runtimeMXBean = getManagedBean(
          RuntimeMXBean.class, JavaShellTools.RUNTIME_MX_BEAN_OBJECTNAME ) ;
      processIdentifier = JavaShellTools.extractProcessId( runtimeMXBean.getName() ) ;
    }
    ownStartupSensorSemaphore.tryAcquire(
        startupSensorSemaphorePermitCount,
        timeUnit.toMillis( timeout ) / 2L, TimeUnit.MILLISECONDS
    ) ;
    LOG.info( "Started " + getNickname() + "." ) ;
  }

  public boolean isUp() {
    synchronized( stateLock ) {
      if( insider == null ) {
        throw new IllegalStateException( "Not ready" ) ;
      }
      try {
        return insider.isAlive() ;
      } catch( Exception ignored ) {
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
  public Integer shutdown( final ShutdownStyle shutdownStyle )
      throws InterruptedException, IOException
  {
    LOG.info( "Shutdown (" + shutdownStyle + ") requested for " + getNickname() + "..." ) ;

    // Code checker happy: avoid access to static member in synchronized context.
    final Log log = LOG ;

    Integer exitStatus = null ;
    synchronized( stateLock ) {
      try {
        switch( shutdownStyle ) {
          case GENTLE :
            try {
              insider.shutdown() ;
            } catch( Exception e ) {
              log.info( "Shutdown request failed: " + e.getMessage() + ", forcing..." ) ;
              exitStatus = shutdownProcess( true ) ;
              break ;
            }
            // ... After asking for shutdown, we wait for natural process end. TODO: add timeout?
          case WAIT :
            exitStatus = shutdownProcess( false ) ;
            break ;
          case FORCED :
            shutdownProcess( true ) ;
            break ;
          default :
            throw new IllegalArgumentException( "Unsupported: " + shutdownStyle ) ;
        }
        log.info( "Shutdown (" + shutdownStyle + ") successful for " + getNickname() + "." ) ;
      } finally {
        stopHeartbeat() ;
        disconnect() ;
        synchronized( processIdentifierLock ) {
          processIdentifier = JavaShellTools.UNDEFINED_PROCESS_ID ;
        }
      }
    }
    log.info( "Shutdown (" + shutdownStyle + ") complete for " + getNickname()
        + " with exit status code of " + exitStatus + "." ) ;
    return exitStatus ;
  }

  private void stopHeartbeat() {
    if( heartbeatSender != null ) {
      heartbeatSender.stop() ;
      heartbeatSender = null ;
    }
  }


// ===
// JMX
// ===


  /**
   * Synchronization on {@link #stateLock} left to caller.
   */
  private void connect() throws IOException, InterruptedException {
    final JMXServiceURL url ;
    try {
      url = new JMXServiceURL( "service:jmx:rmi:///jndi/rmi://localhost:" + jmxPort + "/jmxrmi" );
    } catch( MalformedURLException e ) {
      throw new Error( e ) ;
    }
    connectWithRetries( url ) ;
    jmxConnection = jmxConnector.getMBeanServerConnection() ;
    insider = getManagedBean( Insider.class, Insider.NAME ) ;
  }

  private void connectWithRetries( final JMXServiceURL url )
      throws IOException, InterruptedException
  {
    LOG.info( "Connecting to " + url + " ..." ) ;
    int attemptCount = 0 ;
    while( true ) {
      try {
        jmxConnector = JMXConnectorFactory.connect( url, null ) ;
        LOG.debug( "Successfully connected to " + url ) ;
        return ;
      } catch( IOException e ) {
        final Throwable cause = e.getCause() ;
        if(    cause instanceof ServiceUnavailableException
            || cause instanceof java.rmi.ConnectException
        ) {
          if( attemptCount ++ < 10 ) {
            LOG.debug( "Couldn't connect to " + url + ", waiting a bit before another attempt..." ) ;
            TimeUnit.MILLISECONDS.sleep( 200L ) ;
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

    synchronized( stateLock ) {
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
    try {
      if( jmxConnection != null ) {
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
      }
      if( jmxConnector != null ) {
        try {
          jmxConnector.close() ;
        } catch( IOException e ) {
          logCouldntUnregister( jmxConnector, e ) ;
        }
      }
    } finally {
      managedBeans.clear() ;
      jmxConnection = null ;
      jmxConnector = null ;
    }
  }

  private static void logCouldntUnregister( final Object culprit, final Exception e ) {
    LOG.debug( "Couldn't disconnect or unregister " + culprit + ", cause: " + e.getClass() +
        " (may be normal if other VM terminated)." ) ;
  }


// ==========  
// Parameters
// ==========

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

  

// =======================================================================
// Boring stuff to access member variable before superclass initialization
// =======================================================================

  private static final ThreadLocal< Semaphore > THREADLOCAL_SEMAPHORE =
      new ThreadLocal< Semaphore >() ;

  private static final ThreadLocal< Integer > THREADLOCAL_PERMITCOUNT =
      new ThreadLocal< Integer >() ;

  private static TieredStartupSensor createTieredStartupSensor(
      final Predicate< String >... startupSensors
  ) {
    final Semaphore semaphore = new Semaphore( 0, true ) ;
    final TieredStartupSensor tieredStartupSensor =
        new TieredStartupSensor( semaphore, startupSensors ) ;
    THREADLOCAL_SEMAPHORE.set( semaphore ) ;
    THREADLOCAL_PERMITCOUNT.set( tieredStartupSensor.getInitialPredicateCount() ) ;
    return tieredStartupSensor ;
  }


}

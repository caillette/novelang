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

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Removes clutter from {@link JavaShell}.
 * @author Laurent Caillette
 */
public class JavaShellTools {


  public static final Runnable NULL_RUNNABLE = new Runnable() {
    @Override
    public void run() { }
  } ;


  private JavaShellTools() { }

  /**
   * Tries to obtain system process identifier of running JVM.
   *
   * @return {@value #UNKNOWN_PROCESS_ID} if not found, process identifier otherwise.
   *
   * @author from <a href="http://samuelsjoberg.com/archive/2006/12/jvm-pid" >Samuel Sjöberg</a>'s
   *     blog.
   */
  public static int extractProcessId( final String jvmName ) {
    final StringBuilder pid = new StringBuilder() ;
    for( int i = 0, l = jvmName.length()  ; i < l  ; i++ ) {
      if( Character.isDigit( jvmName.charAt( i ) ) ) {
        pid.append( jvmName.charAt( i ) ) ;
      } else if( pid.length() > 0 ) {
        break ;
      }
    }
    try {
      return Integer.parseInt( pid.toString() ) ;
    } catch( NumberFormatException ignored ) {
      return UNKNOWN_PROCESS_ID;
    }
  }

  public static final int UNDEFINED_PROCESS_ID = -1 ;
  public static final int UNKNOWN_PROCESS_ID = -2 ;

  public static final ObjectName RUNTIME_MX_BEAN_OBJECTNAME ;
  static {
    try {
      RUNTIME_MX_BEAN_OBJECTNAME = new ObjectName( ManagementFactory.RUNTIME_MXBEAN_NAME ) ;
    } catch( MalformedObjectNameException e ) {
      throw new RuntimeException( e ) ;
    }
  }


// ====================================
// Java Util Logging configuration file
// ====================================


  static final ImmutableList< String > JAVA_UTIL_LOGGING_CONFIGURATION = ImmutableList.of(
    "handlers= java.util.logging.ConsoleHandler",
    ".level=INFO",
    "",
    "java.util.logging.FileHandler.pattern = %h/java%u.log",
    "java.util.logging.FileHandler.limit = 50000",
    "java.util.logging.FileHandler.count = 1",
    "java.util.logging.FileHandler.formatter = java.util.logging.SimpleFormatter",
    "",
    "java.util.logging.ConsoleHandler.level = FINEST",
    "java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter",
    "",
    "javax.management.level=FINEST",
    "javax.management.remote.level=FINER"
) ;

  static final File JAVA_UTIL_LOGGING_CONFIGURATION_FILE ;
  static {
    try {
      JAVA_UTIL_LOGGING_CONFIGURATION_FILE =
          File.createTempFile( "javautillogging", "properties" ).getCanonicalFile() ;
      FileUtils.writeLines( JavaShellTools.JAVA_UTIL_LOGGING_CONFIGURATION_FILE, JavaShellTools.JAVA_UTIL_LOGGING_CONFIGURATION ) ;
    } catch( IOException e ) {
      throw new RuntimeException( e ) ;
    }
  }


  static ImmutableList< String > createProcessArguments(
      final ImmutableList< String > jvmArguments,
      final BootstrappingJmxKit jmxKit,
      final Integer jmxPort,
      final JavaClasses javaClasses,
      final ImmutableList< String > programArguments,
      final Integer heartbeatMaximumPeriod
  ) {
    final List< String > argumentList = Lists.newArrayList() ;

    // This is a very optimistic approach for obtaining Java executable.
    // TODO: see how Ant's Java task solves this.
    argumentList.add( "java" ) ;

    argumentList.add( "-D" + ShutdownTools.SHUTDOWN_TATTOO_PROPERTYNAME ) ;

    if( jmxKit == null ) {
        Preconditions.checkArgument( jmxPort == null, "Null jmxKit implies null jmxPort" ) ;
    }
    else
    {
        checkNotNull( jmxPort ) ;
        argumentList.addAll( jmxKit.getJvmProperties( jmxPort, heartbeatMaximumPeriod ) ) ;
    }

    // Log JMX activity. Didn't prove useful.
//    argumentList.add( "-Djava.util.logging.config.file=" +
//        JAVA_UTIL_LOGGING_CONFIGURATION_FILE.getAbsolutePath() ) ;

    if( jvmArguments != null ) {
      argumentList.addAll( jvmArguments ) ;
    }

    argumentList.addAll( javaClasses.asStringList() ) ;
    argumentList.addAll( programArguments ) ;

    return ImmutableList.copyOf( argumentList ) ;
  }


// ===
// JMX
// ===


  /**
   * Unregisters JMX Beans and closes the {@link javax.management.remote.JMXConnector}s.
   * This method should close the default JMX connector o {@link JavaShell} because, when there is one, there is always
   * a registered {@link org.novelang.outfit.shell.insider.Insider} at startup so it should appear inside the
   * {@code Map}.
   *
   */
  /*package*/ static void disconnectAll( final Map<JmxBeanKey, JmxBeanValue > connectedBeans ) {

    // First, unregister all beans.
    for( final JmxBeanValue value : connectedBeans.values() ) {
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
    for( final JmxBeanValue value : connectedBeans.values() ) {
      try {
        value.getConnectionBundle().connector.close() ;
      } catch( IOException e ) {
        logCouldntUnregister( value.getConnectionBundle().connector, e ) ;
      }
    }
  }

  private static void logCouldntUnregister( final Object culprit, final Exception e ) {
/*
    LOG.debug( "Couldn't disconnect or unregister " + culprit + ", cause: " + e.getClass() +
        " (may be normal if other VM terminated)." ) ;
*/
  }

}

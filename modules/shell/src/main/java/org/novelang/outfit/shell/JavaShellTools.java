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
package org.novelang.outfit.shell;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.novelang.outfit.shell.insider.Insider;
import org.apache.commons.io.FileUtils;

/**
 * Removes clutter from {@link JavaShell}.
 * @author Laurent Caillette
 */
public class JavaShellTools {



  private JavaShellTools() {
  }

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


  static final List< String > JAVA_UTIL_LOGGING_CONFIGURATION = ImmutableList.of(
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


  static List< String > createProcessArguments(
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
}

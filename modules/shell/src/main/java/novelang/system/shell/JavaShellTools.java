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
import java.util.List;

import com.google.common.collect.ImmutableList;
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



}

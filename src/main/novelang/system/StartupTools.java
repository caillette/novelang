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
package novelang.system;

import org.apache.commons.lang.StringUtils;

/**
 * Helps logging system to initialize correctly. Call {@link #fixLogDirectory()} method before any
 * logging operation.
 *
 * @author Laurent Caillette
 */
public class StartupTools {

  /**
   * Set the {@value #LOG_DIR_SYSTEMPROPERTYNAME} system property and use it
   * in Logback configuration to log in another place than current directory.
   */
  public static final String LOG_DIR_SYSTEMPROPERTYNAME = "novelang.log.dir" ;
  private static final String DEFAULT_LOG_DIR = ".";

  public static void fixLogDirectory() {

    final String logDir = System.getProperty( StartupTools.LOG_DIR_SYSTEMPROPERTYNAME ) ;
    if( StringUtils.isBlank( logDir ) ) {
      System.setProperty( LOG_DIR_SYSTEMPROPERTYNAME, DEFAULT_LOG_DIR ) ;
//      System.out.println(
//          "System property [" +
//          StartupTools.LOG_DIR_SYSTEMPROPERTYNAME + "] not defined. " +
//          "Log file (if any) will be created in current directory."
//      ) ;
    } else {
      System.out.println( "System property [" +
          StartupTools.LOG_DIR_SYSTEMPROPERTYNAME + "] ='" +
          System.getProperty( StartupTools.LOG_DIR_SYSTEMPROPERTYNAME ) + "'"
      ) ;

    }

  }

  private StartupTools() {
    throw new Error() ;
  }


}

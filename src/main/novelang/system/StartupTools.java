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
 * Helps logging system to initialize correctly.
 * <p>
 * The {@link #fixLogDirectory()} method must be called before any logging operation in order
 * to force definition of {@link #LOG_DIR_SYSTEMPROPERTYNAME} system property if it was not
 * user-defined.
 *
 * @author Laurent Caillette
 */
public class StartupTools {

  /**
   * The {@value #LOG_DIR_SYSTEMPROPERTYNAME} system property is required by Logback configuration
   * file used for production deployment.
   */
  public static final String LOG_DIR_SYSTEMPROPERTYNAME = "novelang.log.dir" ;
  private static final String DEFAULT_LOG_DIR = ".";

  public static void fixLogDirectory() {

    final String logDir = System.getProperty( StartupTools.LOG_DIR_SYSTEMPROPERTYNAME ) ;
    if( StringUtils.isBlank( logDir ) ) {
      System.setProperty( LOG_DIR_SYSTEMPROPERTYNAME, DEFAULT_LOG_DIR ) ;
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

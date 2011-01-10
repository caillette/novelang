/*
 * Copyright (C) 2010 Laurent Caillette
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
package org.novelang.outfit;

import java.io.File;
import java.io.IOException;

import org.novelang.configuration.parse.GenericParameters;
import org.novelang.configuration.parse.GenericParametersConstants;
import org.novelang.logger.ConsoleLogger;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.logger.NullLogger;

/**
 * Helps logging system to initialize correctly.
 * <p>
 * The {@link #fixLogDirectory(java.io.File)} method must be called before any logging operation
 * by Logback in order to force definition of {@link #LOG_DIR_SYSTEMPROPERTYNAME} system property
 * (user doesn't have to set it manually).
 *
 * @author Laurent Caillette
 */
public class LogbackConfigurationTools {

  private static final Logger LOGGER = LoggerFactory.getLogger( LogbackConfigurationTools.class ) ;

  public static final String DEFAULT_LOG_DIR = ".";

  public static final String LOG_DIR_SYSTEMPROPERTYNAME = "org.novelang.log.dir" ;

  /**
   * This method sets the value of the {@value #LOG_DIR_SYSTEMPROPERTYNAME} system property
   * as it is required by Logback configuration for production deployment.
   * <p>
   * TODO something like <a href="http://logback.qos.ch/xref/chapter3/MyApp2.html">this</a>.
   *
   * @param logDirectoryFromParameters maybe null.
   */
  public static void fixLogDirectory( final File logDirectoryFromParameters ) {
    final File realLogDirectory = prepareLogDirectory(
        logDirectoryFromParameters,
        NullLogger.INSTANCE // Avoids logging twice, as ConfigurationTools calls this method, too.
    ) ;

    System.setProperty( LOG_DIR_SYSTEMPROPERTYNAME, realLogDirectory.getPath() ) ;
    final String message = "System property [" +
        LogbackConfigurationTools.LOG_DIR_SYSTEMPROPERTYNAME + "] set to '" +
        realLogDirectory.getAbsolutePath() + "'.";
    System.out.println( message ) ;
    LOGGER.info( message ) ;

  }

  private LogbackConfigurationTools() {
    throw new Error() ;
  }



  /**
   * This method gets also called by {@code ConfigurationTools}.
   * Each caller passes its own {@link Logger} instance for cleaner logging.
   */
  public static File prepareLogDirectory(
      final File logDirectoryFromParameters,
      final Logger logger
  ) {
    final File logDirectory;
    if( null == logDirectoryFromParameters ) {
      logDirectory = canonicize( new File( DEFAULT_LOG_DIR ) ) ;
      logger.info(
          "Got log directory from default value '",
          DEFAULT_LOG_DIR,
          "' (option not set: ",
          GenericParametersConstants.getLogDirectoryOptionDescription(),
          ")."
      ) ;
    } else {
      logDirectory = canonicize( logDirectoryFromParameters ) ;
      logger.info(
          "Got log directory from custom value '",
          logDirectory,
          "' (from option: ",
          GenericParametersConstants.getLogDirectoryOptionDescription(),
          ")."
      ) ;
    }
    if( logDirectory.mkdirs() ) {
      logger.info( "Created '" + logDirectory.getAbsolutePath() + "'." ) ;
    }
    return logDirectory ;
  }


  private static File canonicize( final File logDirectoryFromParameters ) {
    final File logDirectory ;
    try {
      logDirectory = logDirectoryFromParameters.getCanonicalFile() ;
    } catch( IOException e ) {
      throw new RuntimeException( e ) ;
    }
    return logDirectory ;
  }

  public static void printLogbackConfigurationFiles() {
    throw new UnsupportedOperationException( "TODO" ) ;
  }
}

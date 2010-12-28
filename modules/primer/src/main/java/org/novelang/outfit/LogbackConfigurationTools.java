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

/**
 * Helps logging system to initialize correctly.
 * <p>
 * The {@link #fixLogDirectory(String[])} method must be called before any logging operation
 * in order to force definition of {@link #LOG_DIR_SYSTEMPROPERTYNAME} system property
 * if it was not user-defined from startup arguments.
 *
 * @author Laurent Caillette
 */
public class LogbackConfigurationTools {

  public static final String DEFAULT_LOG_DIR = ".";

  public static final String LOG_DIR_SYSTEMPROPERTYNAME = "org.novelang.log.dir" ;

  /**
   * This method sets the value of the {@value #LOG_DIR_SYSTEMPROPERTYNAME} system property
   * as it is required by Logback configuration for production deployment. It delegates to
   * {@link #extractLogDirectory(String[])} for finding the value out from startup arguments.
   * It also logs some stuff.
   * <p>
   * TODO something like <a href="http://logback.qos.ch/xref/chapter3/MyApp2.html">this</a>.
   *
   * @param arguments A non-null array containing no nulls.
   */
  public static void fixLogDirectory( final String[] arguments ) {
    final File logDirectoryFromParameters ;
    final String logDirectoryName = extractLogDirectory( arguments ) ;
    if( logDirectoryName == null ) {
      logDirectoryFromParameters = null ;
    } else {
      logDirectoryFromParameters = new File( logDirectoryName ) ;
    }

    final File realLogDirectory =
        prepareLogDirectory( logDirectoryFromParameters, ConsoleLogger.INSTANCE ) ;

    System.setProperty( LOG_DIR_SYSTEMPROPERTYNAME, realLogDirectory.getPath() ) ;
    System.out.println( "System property [" +
        LogbackConfigurationTools.LOG_DIR_SYSTEMPROPERTYNAME + "] set to '" +
        realLogDirectory.getAbsolutePath() + "'."
    ) ;

  }

  private LogbackConfigurationTools() {
    throw new Error() ;
  }


  /**
   * This extract the option value for log directory the same way {@link GenericParameters}
   * does, with hand-written parsing. The difference is, this function doesn't perform
   * any log operation, thus not triggering logging configuration before logging is set
   * as it should.
   *
   * @param startupArguments
   * @return null if there was no such option.
   */
  public static String extractLogDirectory( final String[] startupArguments ) {
    final String logDirectoryOption =
        GenericParametersConstants.OPTIONPREFIX + GenericParametersConstants.LOG_DIRECTORY_OPTION_NAME ;
    for( int i = 0 ; i < startupArguments.length ; i++ ) {
      final String startupArgument = startupArguments[ i ] ;
      if( startupArgument.equals( logDirectoryOption ) && i < startupArgument.length() - 1 ) {
        return startupArguments[ i + 1 ] ;
      }
    }
    return null ;
  }

  /**
   * This method gets also called by {@code ConfigurationTools} for proper logging,
   * so logged messages must stay consistent.
   * The first call is for configuring the logging system, the second call is for
   * logging in the logging system.
   * <p>
   * Sidenote: a better logging system with in-memory, pre-configuration recording
   * would remove that mess.
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
}

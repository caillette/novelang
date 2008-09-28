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

import java.io.File;

import novelang.configuration.parse.GenericParameters;

/**
 * Helps logging system to initialize correctly.
 * <p>
 * The {@link #fixLogDirectory(String[])} method must be called before any logging operation
 * in order to force definition of {@link #LOG_DIR_SYSTEMPROPERTYNAME} system property
 * if it was not user-defined from startup arguments.
 *
 * @author Laurent Caillette
 */
public class StartupTools {

  private static final String DEFAULT_LOG_DIR = ".";

  public static final String LOG_DIR_SYSTEMPROPERTYNAME = "novelang.log.dir" ;

  /**
   * This method sets the value of the {@value #LOG_DIR_SYSTEMPROPERTYNAME} system property
   * as it is required by Logback configuration for production deployment. It delegates to
   * {@link #extractLogDirectory(String[])} for finding the value out from startup arguments.
   *
   * TODO something like <a href="http://logback.qos.ch/xref/chapter3/MyApp2.html">this</a>.
   *
   * @param arguments A non-null array containing no nulls.
   */
  public static void fixLogDirectory( String[] arguments ) {

    final File logDirectory = extractLogDirectory( arguments ) ;
    if( null == logDirectory ) {
      System.setProperty( LOG_DIR_SYSTEMPROPERTYNAME, DEFAULT_LOG_DIR ) ;
    } else {
      System.out.println( "System property [" +
          StartupTools.LOG_DIR_SYSTEMPROPERTYNAME + "] ='" +
          System.getProperty( logDirectory.getAbsolutePath() ) + "'"
      ) ;

    }

  }

  private StartupTools() {
    throw new Error() ;
  }


  public static void installXalan() {
    System.setProperty(
        "javax.xml.transform.TransformerFactory",
        org.apache.xalan.processor.TransformerFactoryImpl.class.getName()
    ) ;

    System.setProperty(
        "javax.xml.parsers.DocumentBuilderFactory",
        org.apache.xerces.jaxp.DocumentBuilderFactoryImpl.class.getName()
    ) ;

    System.setProperty(
        "javax.xml.parsers.SAXParserFactory",
        org.apache.xerces.jaxp.SAXParserFactoryImpl.class.getName()
    ) ;
    
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
  public static File extractLogDirectory( String[] startupArguments ) {
    final String logDirectoryOption =
        GenericParameters.OPTIONPREFIX + GenericParameters.LOG_DIRECTORY_OPTION_NAME ;
    for( int i = 0 ; i < startupArguments.length - 1 ; i++ ) {
      final String startupArgument = startupArguments[ i ] ;
      if( logDirectoryOption.equals( startupArgument ) ) {
        final String directoryName = startupArguments[ i + 1 ] ;
        final File directory = new File( directoryName ) ;
        if( directory.exists() ) {
          return directory ;
        }
      }
    }
    return null ;
  }

}

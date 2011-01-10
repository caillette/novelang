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
package org.novelang.bootstrap;

import java.io.File;
import java.util.Map;
import java.io.PrintStream;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.SystemUtils;
import org.novelang.batch.CannotStartException;
import org.novelang.batch.DocumentGenerator;
import org.novelang.batch.LevelExploder;
import org.novelang.configuration.parse.ArgumentException;
import org.novelang.configuration.parse.DaemonParameters;
import org.novelang.configuration.parse.DocumentGeneratorParameters;
import org.novelang.configuration.parse.GenericParameters;
import org.novelang.configuration.parse.LevelExploderParameters;
import org.novelang.daemon.HttpDaemon;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.LogbackConfigurationTools;
import org.novelang.outfit.EnvironmentTools;

/**
 * The single entry point for launching all Novelang commands.
 *
 * @author Laurent Caillette
 */
public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger( Main.class ) ;
  private static final File USER_DIRECTORY = new File( SystemUtils.USER_DIR );

  public static void main( final String[] originalArguments ) throws Exception {

    // This must happen first. The need for originalArguments parameter prevents from
    // putting this initialization in a static block.
    LogbackConfigurationTools.fixLogDirectory( originalArguments ) ;

    // Switch from deferred logging to real one.
    LoggerFactory.configurationComplete() ;
    
    EnvironmentTools.logSystemProperties() ;

    new Main().doMain( originalArguments ) ;
  }

  protected void doMain( final String[] originalArguments ) throws Exception {

    if( 0 == originalArguments.length ) {
      help() ;
      System.exit( 1 ) ;
    }
    final String commandName = originalArguments[ 0 ];
    final MainCaller caller = commands.get( commandName ) ;
    if( null == caller ) {
      help( commandName ) ;
      System.exit( -1 ) ;
    } else {

      final String[] trimmedArguments = new String[ originalArguments.length - 1 ] ;
      System.arraycopy( originalArguments, 1, trimmedArguments,  0, trimmedArguments.length ) ;

      final GenericParameters parameters ;
      try {
        parameters = caller.createParameters( trimmedArguments ) ;
      } catch( ArgumentException e ) {
        final String commandLineParametersDescriptor = caller.getSpecificCommandLineParametersDescriptor();
        if( e.isHelpRequested() ) {
          printHelpOnConsole( commandName, e, commandLineParametersDescriptor ) ;
//          if( mayTerminateJvm ) {
            System.exit( -1 ) ;
//          }
          throw new CannotStartException( e ) ;
        } else {
          LOGGER.error( e, "Parameters exception, printing help and exiting." ) ;
          printHelpOnConsole( commandName, e, commandLineParametersDescriptor ) ;
//          if( mayTerminateJvm ) {
            System.exit( -2 ) ;
//          }
          throw new CannotStartException( e ) ;
        }
      }


      caller.main( parameters ) ;
    }
  }

  @SuppressWarnings( { "UseOfSystemOutOrSystemErr" } )
  private static void printHelpOnConsole(
      final String commandName,
      final ArgumentException e,
      final String specificCommandLineParametersDescriptor

  ) {
    if( null != e.getMessage() ) {
      System.out.println( e.getMessage() ) ;
    }
    e.getHelpPrinter().print(
        System.out,
        commandName + " " + specificCommandLineParametersDescriptor,
        80
    ) ;
  }


  @SuppressWarnings( { "UseOfSystemOutOrSystemErr" } )
  private void help() {
    printHelp( System.out ) ;
  }

  @SuppressWarnings( { "UseOfSystemOutOrSystemErr" } )
  private void help( final String badCommand ) {
    printBadCommand( System.out, badCommand ) ;
    printHelp( System.out ) ;
  }

  private static void printBadCommand( final PrintStream out, final String badCommand ) {
    out.println( "Unknown command: '" + badCommand + "'" ) ;
  }

  private void printHelp( final PrintStream out ) {
    out.println( "Supported commands: " ) ;
    for( final String commandName : commands.keySet() ) {
      out.println( "  " + commandName + " <arguments>" ) ;
    }
    out.println( "Use <command> --help for help on a particular command." ) ;
  }


// ========
// Commands
// ========


  private interface MainCaller< PARAMETERS extends GenericParameters > {

    public void main( PARAMETERS parameters ) throws Exception ;

    /**
     * @param arguments arguments without the command name.
     */
    public PARAMETERS createParameters( final String[] arguments ) throws ArgumentException ;

    public abstract String getSpecificCommandLineParametersDescriptor() ;

  }

  /**
   * As an instance variable, this gets initialized right after the {@link #main(String[])}
   * method did its initialization job.
   * So other classes are not loaded too soon, avoiding them to wake an unconfigured logging
   * system up during their static initialization.
   */
  private final Map< String, ? extends MainCaller > commands = ImmutableMap.of(
      HttpDaemon.COMMAND_NAME,
      new MainCaller< DaemonParameters >() {

        @Override
        public void main( final DaemonParameters parameters ) throws Exception {
          HttpDaemon.main( parameters ) ;
        }

        @Override
        public DaemonParameters createParameters( final String[] arguments )
            throws ArgumentException
        {
          return HttpDaemon.createParameters( arguments ) ;
        }

        @Override
        public String getSpecificCommandLineParametersDescriptor() {
          return " [OPTIONS]" ;
        }
      }
      ,
      DocumentGenerator.COMMAND_NAME,
       new MainCaller< DocumentGeneratorParameters >() {
         @Override
         public void main( final DocumentGeneratorParameters parameters ) throws Exception {
           new DocumentGenerator().main( parameters ) ;
         }

         @Override
         public DocumentGeneratorParameters createParameters( final String[] arguments )
             throws ArgumentException
         {
           return DocumentGenerator.createParameters( arguments, USER_DIRECTORY ) ;
         }

         @Override
         public String getSpecificCommandLineParametersDescriptor() {
           return DocumentGenerator.getSpecificCommandLineParametersDescriptor() ;
         }
       }
      ,
      "explodelevels",
       new MainCaller< LevelExploderParameters >() {
         @Override
         public void main( final LevelExploderParameters parameters ) throws Exception {
           new LevelExploder().main( parameters ) ;
         }

         @Override
         public LevelExploderParameters createParameters( final String[] arguments )
             throws ArgumentException
         {
           return LevelExploder.createParameters( arguments, USER_DIRECTORY ) ;
         }

         @Override
         public String getSpecificCommandLineParametersDescriptor() {
           return LevelExploder.getSpecificCommandLineParametersDescriptor() ;
         }
       }
  ) ;


}

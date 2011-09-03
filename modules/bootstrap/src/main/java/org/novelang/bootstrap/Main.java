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
package org.novelang.bootstrap;

import java.io.File;
import java.io.PrintStream;

import com.google.common.collect.ImmutableSet;
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
import org.novelang.outfit.TemporaryFileTools;

/**
 * The single entry point for launching all Novelang commands.
 *
 * @author Laurent Caillette
 */
@SuppressWarnings( { "UseOfSystemOutOrSystemErr" } )
public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger( Main.class ) ;
  private static final File USER_DIRECTORY = new File( SystemUtils.USER_DIR ) ;

  public static void main( final String[] originalArguments ) throws Exception {
    new Main().doMainWithSystemSetup( originalArguments ) ;
  }


  private void doMainWithSystemSetup( final String[] originalArguments ) throws Exception {

    final MainCaller caller = findMainCaller( originalArguments ) ;
    final GenericParameters parameters = createParametersOrExit( originalArguments, caller ) ;

    TemporaryFileTools.setupTemporaryDirectory( parameters.getTemporaryDirectory() ) ;
    LogbackConfigurationTools.printLogbackConfigurationFiles() ;
    LogbackConfigurationTools.fixLogDirectory( parameters.getLogDirectory() ) ;

    // Switch from deferred logging to real one.
    LoggerFactory.configurationComplete() ;

    EnvironmentTools.logSystemProperties() ;

    caller.main( parameters ) ;

  }

  protected void doMainWithoutSystemSetup( final String[] originalArguments ) throws Exception {
    final MainCaller caller = findMainCaller( originalArguments ) ;
    final GenericParameters parameters = createParametersOrExit( originalArguments, caller ) ;
    caller.main( parameters ) ;
  }

  private static GenericParameters createParametersOrExit(
      final String[] originalArguments,
      final MainCaller caller
  ) throws CannotStartException
  {
    final String[] trimmedArguments = new String[ originalArguments.length - 1 ] ;
    System.arraycopy( originalArguments, 1, trimmedArguments,  0, trimmedArguments.length ) ;

    final GenericParameters parameters ;
    try {
      parameters = caller.createParameters( trimmedArguments ) ;
    } catch( ArgumentException e ) {
      final String commandLineParametersDescriptor =
          caller.getSpecificCommandLineParametersDescriptor() ;
      if( e.isHelpRequested() ) {
        printHelpOnConsole( caller.commandName, e, commandLineParametersDescriptor ) ;
          System.exit( -1 ) ;
        throw new CannotStartException( e ) ;
      } else {
        LOGGER.error( e, "Parameters exception, printing help and exiting." ) ;
        printHelpOnConsole( caller.commandName, e, commandLineParametersDescriptor ) ;
          System.exit( -2 ) ;
        throw new CannotStartException( e ) ;
      }
    }
    return parameters;
  }

  private MainCaller findMainCaller( final String[] originalArguments ) {
    if( 0 == originalArguments.length ) {
      justPrintHelp() ;
      System.exit( 1 ) ;
    }
    final String commandName = originalArguments[ 0 ];
    final MainCaller caller = getMainCaller( commandName ) ;
    if( null == caller ) {
      help( commandName ) ;
      System.exit( -1 ) ;
    }
    return caller;
  }


// =============
// Help printers
// =============

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


  private void justPrintHelp() {
    printHelp( System.out ) ;
  }

  private void help( final String badCommand ) {
    printBadCommand( System.out, badCommand ) ;
    printHelp( System.out ) ;
  }

  private static void printBadCommand( final PrintStream out, final String badCommand ) {
    out.println( "Unknown command: '" + badCommand + "'" ) ;
  }

  private void printHelp( final PrintStream out ) {
    out.println( "Supported commands: " ) ;
    for( final MainCaller mainCaller : COMMANDS ) {
      out.println( "  " + mainCaller.commandName + " <arguments>" ) ;
    }
    out.println( "Use <command> --help for help on a particular command." ) ;
  }





// ========
// Commands
// ========


  private static abstract class MainCaller< PARAMETERS extends GenericParameters > {

    private final String commandName ;

    protected MainCaller( final String commandName ) {
      this.commandName = commandName ;
    }

    public abstract void main( PARAMETERS parameters ) throws Exception ;

    /**
     * @param arguments arguments without the command name.
     */
    public abstract PARAMETERS createParameters( final String[] arguments )
        throws ArgumentException ;

    public abstract String getSpecificCommandLineParametersDescriptor() ;

  }

  private static< PARAMETERS extends GenericParameters > MainCaller< PARAMETERS >
  getMainCaller( final String commandName ) {
    for( final MainCaller mainCaller : COMMANDS ) {
      if( mainCaller.commandName.equals( commandName ) ) {
        return mainCaller ;
      }
    }
    return null ;
  }

  /**
   * As an instance variable, this gets initialized right after the {@link #main(String[])}
   * method did its initialization job.
   * So other classes are not loaded too soon, avoiding them to wake an unconfigured logging
   * system up during their static initialization.
   */
  private static final ImmutableSet< ? extends MainCaller > COMMANDS = ImmutableSet.of(

    new MainCaller< DaemonParameters >( HttpDaemon.COMMAND_NAME ) {

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
    new MainCaller< DocumentGeneratorParameters >( DocumentGenerator.COMMAND_NAME ) {
      @Override
      public void main( final DocumentGeneratorParameters parameters ) throws Exception {
        new DocumentGenerator().main( parameters ) ;
      }

      @Override
      public DocumentGeneratorParameters createParameters( final String[] arguments )
          throws ArgumentException {
        return DocumentGenerator.createParameters( arguments, USER_DIRECTORY ) ;
      }

      @Override
      public String getSpecificCommandLineParametersDescriptor() {
        return DocumentGenerator.getSpecificCommandLineParametersDescriptor() ;
      }
    }
    ,
    new MainCaller< LevelExploderParameters >( "explodelevels" ) {
      @Override
      public void main( final LevelExploderParameters parameters ) throws Exception {
        new LevelExploder().main( parameters ) ;
      }

      @Override
      public LevelExploderParameters createParameters( final String[] arguments )
          throws ArgumentException {
        return LevelExploder.createParameters( arguments, USER_DIRECTORY ) ;
      }

      @Override
      public String getSpecificCommandLineParametersDescriptor() {
        return LevelExploder.getSpecificCommandLineParametersDescriptor() ;
      }
    }
  ) ;


}

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
package novelang.launcher;

import java.util.Map;
import java.io.PrintStream;

import com.google.common.collect.ImmutableMap;
import novelang.batch.DocumentGenerator;
import novelang.batch.LevelExploder;
import novelang.daemon.HttpDaemon;
import novelang.system.StartupTools;
import novelang.system.EnvironmentTools;

/**
 * The single entry point for launching all Novelang commands.
 *
 * @author Laurent Caillette
 */
public class Main {

  public static void main( final String[] originalArguments ) throws Exception {
    // This must happen first. The need for originalArguments parameter prevents from
    // putting this initialization in a static block.
    StartupTools.fixLogDirectory( originalArguments ) ;
    StartupTools.installXalan() ;
    EnvironmentTools.logSystemProperties() ;

    new Main().doMain( originalArguments ) ;
  }

  private void doMain( final String[] originalArguments ) throws Exception {

    if( 0 == originalArguments.length ) {
      help() ;
      System.exit( 1 ) ;
    }
    final String commandName = originalArguments[ 0 ];
    final MainCaller caller = commands.get( commandName ) ;
    if( null == caller ) {
      help( commandName ) ;
    } else {
      final String[] trimmedArguments = new String[ originalArguments.length - 1 ] ;
      System.arraycopy( originalArguments, 1, trimmedArguments,  0, trimmedArguments.length ) ;
      caller.main( commandName, trimmedArguments ) ;
    }
  }

  private void help() {
    printHelp( System.out ) ;
  }

  private void help( final String badCommand ) {
    printBadCommand( System.out, badCommand ) ;
    printHelp( System.out ) ;
  }

  private void printBadCommand( final PrintStream out, final String badCommand ) {
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


  private interface MainCaller {
    void main( String commandName, String[] arguments ) throws Exception ;
  }

  /**
   * As an instance variable, this gets initialized right after the {@link #main(String[])}
   * method did its initialization job.
   * So other classes are not loaded too soon, avoiding them to wake an unconfigured logging
   * system up during their static initialization.
   */
  private final Map< String, ? extends MainCaller > commands = ImmutableMap.of(
      "httpdaemon",
      new MainCaller() {
        public void main( final String commandName, final String[] arguments ) throws Exception {
          HttpDaemon.main( commandName, arguments ) ;
        }
      }
      ,
      DocumentGenerator.COMMAND_NAME,
       new MainCaller() {
         public void main( final String commandName, final String[] arguments ) throws Exception {
           new DocumentGenerator().main( commandName, arguments ) ;
         }
       }
      ,
      "explodelevels",
       new MainCaller() {
         public void main( final String commandName, final String[] arguments ) throws Exception {
           new LevelExploder().main( commandName, arguments ) ;
         }
       }
  ) ;


}

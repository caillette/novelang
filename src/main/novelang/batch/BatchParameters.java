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

package novelang.batch;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Lists;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Command-line arguments, including parsing and help message.
 *
 * @author Laurent Caillette
 */
public class BatchParameters {

  private static final Logger LOGGER = LoggerFactory.getLogger( BatchParameters.class ) ;

  private BatchParameters() { }

  private boolean help ;
  private File targetDirectory ;
  private Iterable< String > documents = EMPTY_STRING_LIST ;

  public boolean isHelp() {
    return help;
  }

  private void setHelp( boolean help ) {
    this.help = help;
  }

  public File getTargetDirectory() {
    return targetDirectory;
  }

  private void setTargetDirectoryName( String targetDirectoryName ) {
    this.targetDirectory = new File( targetDirectoryName ) ;
  }

  public Iterable< String > getDocuments() {
    return null == documents ?  EMPTY_STRING_LIST : documents ;
  }

  public void setDocuments( Iterable< String > documents ) {
    this.documents = documents ;
  }

  private void check() throws ParametersException {
    if( ! isHelp() && ! getDocuments().iterator().hasNext() ) {
      throw new ParametersException( "No document file(s) specified" ) ;
    }
  }

  @Override
  public String toString() {
    return
        getClass().getName() +
        "[isHelp=" + isHelp() +
        ";targetDirectory=" + getTargetDirectory() +
        ";documents=" + getDocuments()
    ;
  }

  private static final List<String> EMPTY_STRING_LIST =
      Lists.immutableList( new ArrayList< String >() ) ;


// =======
// Parsing
// =======

  private static final OptionParser PARSER = new OptionParser() { {

    acceptsAll(
        list( OPTION_HELP, OPTION_HELP_LONG, OPTION_HELP_SYMBOL ),
        "Shows this help message"
    ) ;

    acceptsAll(
        list( OPTION_TARGET_DIRECTORY, OPTION_TARGET_DIRECTORY_LONG ),
        "Where generated files go to (default is '" + DEFAULT_TARGET_DIRECTORY + "')"
    ).withRequiredArg().describedAs( "dir" ) ;

  } } ;

  private static final String DEFAULT_TARGET_DIRECTORY = "./generated" ;

  private static final String OPTION_HELP = "h";
  private static final String OPTION_HELP_LONG = "help";
  private static final String OPTION_HELP_SYMBOL = "?";

  private static final String OPTION_TARGET_DIRECTORY = "t";
  private static final String OPTION_TARGET_DIRECTORY_LONG = "targetDirectory";


  /**
   * Returns a consistent {@code BatchParameters} instance.
   * 
   * @param args a non-null object.
   * @return a non-null object
   * @throws ParametersException in case of parse / consistency problem.
   */
  public static BatchParameters buildFrom( String[] args ) throws ParametersException {
    final BatchParameters parameters = parse( args ) ;
    parameters.check() ;    
    return parameters ;
  }

  protected static BatchParameters parse( String[] args ) throws ParametersException {

    if( 0 == args.length ) {
      throw new ParametersException( "No arguments passed." ) ;
    }

    final OptionSet options ;
    try {
      options = PARSER.parse( args );
    } catch( OptionException e ) {
      throw new ParametersException( e.getMessage() ) ;
    }

    return instantiateFrom( options ) ;

  }

  private static BatchParameters instantiateFrom( OptionSet options ) throws OptionException {
    final BatchParameters batchParameters = new BatchParameters() ;

    batchParameters.setHelp( options.has( OPTION_HELP ) ) ;

    if( options.has( OPTION_TARGET_DIRECTORY ) ) {
      batchParameters.setTargetDirectoryName( ( String ) options.valueOf( OPTION_TARGET_DIRECTORY ) ) ;
    } else {
      batchParameters.setTargetDirectoryName( DEFAULT_TARGET_DIRECTORY ) ;
    }

    batchParameters.setDocuments( options.nonOptionArguments() ) ;

    return batchParameters ;
  }

  private static final List< String > list( String... s ) {
    return Arrays.asList( s ) ;
  }

// ====
// Help
// ====

  private static final String GENERAL_HELP =
      "Usage:   " + ClassUtils.getShortClassName( Main.class ) +
      " [Options] <document [document2 [...]]> \n\n" +
      "<document>: logical name of a document to generate.\n" +
      "    Starts with '/part/' or '/book/' (like URLs). \n" +
      "    Valid names: /part/somedir/mydoc.html or /book/mybook.pdf\n" +
      "\n"
  ;

  /**
   * The help to be printed on the console.
   */
  public static final String HELP ;

  static {
    final StringWriter writer = new StringWriter() ;
    try {
      PARSER.printHelpOn( writer ) ;
    } catch( IOException e ) {
      throw new Error( e ) ;
    }
    HELP = GENERAL_HELP + writer.toString() ;
  }
}

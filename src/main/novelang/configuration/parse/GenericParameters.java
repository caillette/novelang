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
package novelang.configuration.parse;

import java.io.File;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.Argument;
import org.apache.commons.cli2.Parent;
import org.apache.commons.cli2.validation.FileValidator;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.util.HelpFormatter;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.base.Objects;

/**
 * Command-line parameters parsing.
 *
 * @author Laurent Caillette
 */
public abstract class GenericParameters {

  private static final Logger LOGGER = LoggerFactory.getLogger( GenericParameters.class ) ;

  private final File baseDirectory ;
  private final Iterable< File > fontDirectories = null ;
  private final File styleDirectory = null ;
  private final File hyphenationDirectory = null ;

  public GenericParameters(
      File baseDirectory,
      String[] parameters
  )
      throws ArgumentsNotParsedException
  {    
    this.baseDirectory = baseDirectory ;

    LOGGER.debug( "Base directory: ({}'", baseDirectory.getAbsolutePath() ) ;
    LOGGER.debug( "Parameters: '{}'", Lists.newArrayList( parameters ) ) ;
    logHelpMessage() ;

    final HelpFormatter helpFormatter = new HelpFormatter(
        "  ",
        HelpFormatter.DEFAULT_GUTTER_CENTER,
        HelpFormatter.DEFAULT_GUTTER_RIGHT,
        HelpFormatter.DEFAULT_FULL_WIDTH
    ) ;
    helpFormatter.setGroup( OPTIONS ) ;
    final Parser parser = new Parser() ;
    parser.setGroup( OPTIONS ) ;
    parser.setHelpFormatter( helpFormatter ) ;
    parser.setHelpTrigger( HELP_TRIGGER_NAME ) ;

    final CommandLine commandLine;
    try {
      commandLine = parser.parse( parameters );
      if( null == commandLine ) {
        //noinspection ThrowableInstanceNeverThrown
        LOGGER.debug( "Arguments not parsed:", new ArgumentsNotParsedException( helpFormatter ) ) ;
//        fontDirectories = Iterables.emptyIterable() ;
//        styleDirectory = null ;
//      hyphenationDirectory = null ;
        // TODO: raise some 'helpRequested' flag.
      } else {
        if( commandLine.hasOption( FONTS_DIRECTORIES_OPTION ) ) {
          LOGGER.debug( "Command line has option '{}'", FONTS_DIRECTORIES_OPTION ) ;
          LOGGER.debug( " = {}", commandLine.getValues( FONTS_DIRECTORIES_OPTION ) ) ;
//          fontDirectories = getFiles( commandLine, FONTS_DIRECTORIES_OPTION ) ;
        } else {
//          fontDirectories = null ;
        }
        if( commandLine.hasOption( STYLE_DIRECTORY_OPTION ) ) {
          LOGGER.debug( "Command line has option '{}'", STYLE_DIRECTORY_OPTION ) ;
          LOGGER.debug( " = {}", commandLine.getValues( STYLE_DIRECTORY_OPTION ) ) ;
//          styleDirectory = getFile( commandLine, STYLE_DIRECTORY_OPTION ) ;
        } else {
//          styleDirectory = null ;
        }
//      hyphenationDirectory = ( File ) commandLine.getValue( HYPHENATION_DIRECTORY_OPTION ) ;
      }
    } catch( OptionException e ) {
      helpFormatter.setException( e ) ;
      throw new ArgumentsNotParsedException( helpFormatter ) ;
    }
  }

  private File getFile( CommandLine commandLine, Option option ) throws OptionException {
    final String fileName = ( String ) commandLine.getValue( option ) ;
    if( null == fileName ) {
      return null ;
    } else {
      return createFile( fileName, option );
    }
  }

  /**
   * Needed because {@link CommandLine#getValues(Option)} doesn't seem to work
   * at least with commons-cli-20070823.
   */
  private Iterable< File > getFiles(
      CommandLine commandLine,
      Option option
  ) throws OptionException {
    final List values = commandLine.getValues( option ) ;
    final List< File > files = Lists.newArrayList() ;
    boolean first = true ;
    for( Object value : values ) {
      final String fileName = String.valueOf( value ) ;
      if( first ) {
        first = false ;
        files.add( createFile(
            fileName.substring( option.getPreferredName().length() + 3 ), option ) );
      } else {
        files.add( createFile( fileName, option ) ) ;
      }
    }
    return files ;

  }

  private File createFile( String fileName, Option option ) throws OptionException {
    fileName = Objects.nonNull( fileName ) ;
    option = Objects.nonNull( option ) ;
    final File file = new File( baseDirectory, fileName ) ;
    if( file.exists() ) {
      return file ;
    } else {
      throw new OptionException(
          option,
          "Directory does not exist: '" + fileName + "'"
      ) ;
    }
  }

  private static final String FONT_DIRECTORIES_OPTION_NAME = "fonts" ;

  private static final String STYLE_DIRECTORY_OPTION_NAME = "style" ;
  private static final String HYPHENATION_DIRECTORY_OPTION_NAME = "hyphenation" ;
  private static final String HELP_TRIGGER_NAME = "--help" ;


  private static final Argument DIRECTORY_ARGUMENT = new ArgumentBuilder()
      .withName( "dir")
      .withValidator( FileValidator.getExistingDirectoryInstance() )
      .create()
      ;

  private static final Group SINGLE_DIRECTORY_GROUP = new GroupBuilder()
      .withName( "an existing directory" )
      .withOption( DIRECTORY_ARGUMENT )
      .create()
      ;

  private static final Group MULTIPLE_DIRECTORIES_GROUP = new GroupBuilder()
      .withOption( DIRECTORY_ARGUMENT )
      .create()
  ;

  private static final Parent FONTS_DIRECTORIES_OPTION = new DefaultOptionBuilder()
      .withLongName( FONT_DIRECTORIES_OPTION_NAME )
      .withDescription( "Directories containing embeddable fonts" )
      .withChildren( MULTIPLE_DIRECTORIES_GROUP )
      .create()
      ;

  private static final Parent STYLE_DIRECTORY_OPTION = new DefaultOptionBuilder()
      .withLongName( STYLE_DIRECTORY_OPTION_NAME )
      .withDescription( "Directory containing style files" )
      .withChildren( SINGLE_DIRECTORY_GROUP )
      .create()
  ;

//  private static final Group HYPHENATION_DIRECTORY_OPTION = new GroupBuilder()
//      .withName( HYPHENATION_DIRECTORY_OPTION_NAME )
//      .withDescription( "Directory containing hyphenation files" )
//      .withMinimum( 0 )
//      .withMaximum( 1 )
//      .create()
//  ;

  private static final Group OPTIONS = new GroupBuilder()
      .withMinimum( 0 )
      .withOption( STYLE_DIRECTORY_OPTION )
      .withOption( FONTS_DIRECTORIES_OPTION )
//      .withOption( HYPHENATION_DIRECTORY_OPTION )
      .create()
  ;

  /**
   * Returns directories containing fonts.
   * @return a non-null object containing no nulls; all directories are supposed
   *     to exist.
   */
  public Iterable< File > getFontDirectories() {
    return fontDirectories ;
  }

  /**
   * Returns the directory containing style files.
   * @return null if not defined, an existing directory otherwise.
   */
  public File getStyleDirectory() {
    return styleDirectory ;
  }

  /**
   * Returns the directory containing hyphenation files.
   * @return null if not defined, an existing directory otherwise.
   */
  public File getHyphenationDirectory() {
    throw new UnsupportedOperationException( "getHyphenationDirectory" ) ;
  }

  private static < T > Iterable< T > nonNull( Iterable< T > iterable ) {
    if( null == iterable ) {
      return Iterables.emptyIterable() ;
    } else {
      return iterable ;
    }
  }

  private static HelpFormatter createHelpFormatter() {
    return createHelpFormatter(
        HelpFormatter.DEFAULT_GUTTER_LEFT,
        HelpFormatter.DEFAULT_FULL_WIDTH
    ) ;
  }

  private static void logHelpMessage() {
    final HelpFormatter helpFormatter = createHelpFormatter( "  ", 120 ) ;
    final StringWriter helpWriter = new StringWriter() ;
    helpFormatter.setPrintWriter( new PrintWriter( helpWriter ) ) ;
    helpFormatter.printHelp() ;
    LOGGER.debug( helpWriter.toString() ) ;
  }

  private static HelpFormatter createHelpFormatter( String gutterRight, int characterWidth ) {
    final HelpFormatter helpFormatter = new HelpFormatter(
        gutterRight,
        HelpFormatter.DEFAULT_GUTTER_CENTER,
        HelpFormatter.DEFAULT_GUTTER_RIGHT,
        characterWidth
    ) ;
    helpFormatter.setGroup( OPTIONS ) ;
    return helpFormatter ;
  }



}

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Command-line parameters parsing.
 *
 * @author Laurent Caillette
 */
public abstract class GenericParameters {

  private static final Logger LOGGER = LoggerFactory.getLogger( GenericParameters.class ) ;

  private final Iterable< File > fontDirectories ;
  private final File styleDirectory ;
  private final File hyphenationDirectory ;
  private final File logDirectory ;

  protected final Options options ;
  protected final CommandLine line ;

  public GenericParameters(
      File baseDirectory,
      String[] parameters
  )
      throws ArgumentsNotParsedException
  {    
    LOGGER.debug( "Base directory: ({}'", baseDirectory.getAbsolutePath() ) ;
    LOGGER.debug( "Parameters: '{}'", Lists.newArrayList( parameters ) ) ;

    options = new Options() ;
    options.addOption( OPTION_FONT_DIRECTORIES ) ;
    options.addOption( OPTION_STYLE_DIRECTORY ) ;
    options.addOption( OPTION_LOG_DIRECTORY ) ;
    options.addOption( OPTION_HYPHENATION_DIRECTORY ) ;
    enrich( options ) ;

    logHelp() ;

    final CommandLineParser parser = new PosixParser() ;
    try {
      line = parser.parse( options, parameters ) ;

      styleDirectory = extractDirectory( baseDirectory, OPTION_STYLE_DIRECTORY, line ) ;
      logDirectory = extractDirectory( baseDirectory, OPTION_LOG_DIRECTORY, line ) ;
      hyphenationDirectory = extractDirectory( baseDirectory, OPTION_HYPHENATION_DIRECTORY, line ) ;

      if( line.hasOption( OPTION_FONT_DIRECTORIES.getLongOpt() ) ) {
        final String[] fontDirectoriesNames =
            line.getOptionValues( OPTION_FONT_DIRECTORIES.getLongOpt() ) ;
        LOGGER.debug(
            "found: font directories = '{}'", Lists.newArrayList( fontDirectoriesNames ) ) ;
        fontDirectories = extractDirectories( baseDirectory, fontDirectoriesNames ) ;
      } else {
        fontDirectories = Iterables.emptyIterable() ;
      }

    } catch( ParseException e ) {
      throw new ArgumentsNotParsedException( e ) ;
    }

  }

  protected abstract void enrich( Options options ) ;

// =======
// Getters
// =======

  public Iterable< File > getFontDirectories() {
    return fontDirectories;
  }

  public File getStyleDirectory() {
    return styleDirectory;
  }

  public File getHyphenationDirectory() {
    return hyphenationDirectory;
  }

  public File getLogDirectory() {
    return logDirectory;
  }


// ==========
// Extractors
// ==========

  protected File extractDirectory( File baseDirectory, Option option, CommandLine line )
      throws ArgumentsNotParsedException
  {
    final File directory ;
    if( line.hasOption( option.getLongOpt() ) ) {
      final String styleDirectoryName =
          line.getOptionValue( option.getLongOpt() ) ;
      LOGGER.debug( "found: {} = '{}'", option.getDescription(), styleDirectoryName ) ;
      directory = extractDirectory( baseDirectory, styleDirectoryName ) ;
    } else {
      directory = null ;
    }
    return directory ;
  }

  protected static Iterable< File > extractDirectories( File parent, String[] directoryNames )
      throws ArgumentsNotParsedException
  {
    final List directories = Lists.newArrayList() ;
    for( String directoryName : directoryNames ) {
      directories.add( extractDirectory( parent, directoryName ) ) ;
    }
    return ImmutableList.copyOf( directories ) ;
  }

  protected static File extractDirectory( File parent, String directoryName )
      throws ArgumentsNotParsedException
  {
    final File directory = new File( parent, directoryName ) ;
    if( ! ( directory.exists() && directory.isDirectory() ) ) {
      throw new ArgumentsNotParsedException( "Not a directory: '" + directoryName + "'" ) ;
    }
    return directory ;
  }

// =================
// Commons-CLI stuff
// =================

  private void logHelp() {
    final HelpFormatter helpFormatter = new HelpFormatter() ;
    final StringWriter helpWriter = new StringWriter() ;
    helpFormatter.printHelp(
        new PrintWriter( helpWriter ),
        120,
        "<command>",
        "",
        options,
        2,
        0,
        ""
    ) ;
    LOGGER.debug( "Help is:\n" + helpWriter.toString() ) ;
  }

  private static final Option OPTION_FONT_DIRECTORIES = OptionBuilder
      .withLongOpt( "font-dirs" )
      .withDescription( "Directories containing embeddable fonts" )
      .withValueSeparator()
      .hasArgs()
      .create()
  ;

  private static final Option OPTION_STYLE_DIRECTORY = OptionBuilder
      .withLongOpt( "style-dir" )
      .withDescription( "Directory containing style files" )
      .withValueSeparator()
      .hasArg()
      .create()
  ;

  private static final Option OPTION_LOG_DIRECTORY = OptionBuilder
      .withLongOpt( "log-dir" )
      .withDescription( "Directory containing style files" )
      .withValueSeparator()
      .hasArg()
      .create()
  ;

  private static final Option OPTION_HYPHENATION_DIRECTORY = OptionBuilder
      .withLongOpt( "hyphenation-dir" )
      .withDescription( "Directory containing hyphenation files" )
      .withValueSeparator()
      .hasArg()
      .create()
  ;


}

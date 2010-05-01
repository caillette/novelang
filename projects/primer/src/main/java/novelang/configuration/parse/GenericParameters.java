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
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.nio.charset.Charset;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import novelang.system.LogFactory;
import novelang.system.Log;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Base class for command-line parameters parsing.
 *
 * @author Laurent Caillette
 */
public abstract class GenericParameters {

  private static final Log LOG = LogFactory.getLog( GenericParameters.class ) ;

  protected final Options options ;
  protected final CommandLine line ;
  protected final HelpPrinter helpPrinter;

  private final File baseDirectory ;
  private final File contentRoot ;
  private final Iterable< File > fontDirectories ;
  private final Iterable< File > styleDirectories ;
  private final File hyphenationDirectory ;
  private final Charset defaultSourceCharset ;
  private final Charset defaultRenderingCharset ;

  private final File logDirectory ;

  public GenericParameters(
      final File baseDirectory,
      final String[] parameters
  )
      throws ArgumentException
  {    
    LOG.debug( "Base directory: '%s'", baseDirectory.getAbsolutePath() ) ;
    LOG.debug( "Parameters: '%s'", Lists.newArrayList( parameters ) ) ;

    this.baseDirectory = Preconditions.checkNotNull( baseDirectory ) ;
    options = new Options() ;
    options.addOption( OPTION_HELP ) ;
    options.addOption( OPTION_CONTENT_ROOT ) ;
    options.addOption( OPTION_FONT_DIRECTORIES ) ;
    options.addOption( OPTION_EMPTY ) ;
    options.addOption( OPTION_STYLE_DIRECTORIES ) ;
    options.addOption( OPTION_LOG_DIRECTORY ) ;
    options.addOption( OPTION_HYPHENATION_DIRECTORY ) ;
    options.addOption( OPTION_DEFAULT_SOURCE_CHARSET ) ;
    options.addOption( OPTION_DEFAULT_RENDERING_CHARSET ) ;
    enrich( options ) ;

    helpPrinter = new HelpPrinter( options ) ;

    if( containsHelpTrigger( parameters ) ) {
      LOG.debug( "Help trigger detected" ) ;
      throw new ArgumentException( helpPrinter ) ;
    }

    final CommandLineParser parser = new PosixParser() ;

    try {
      line = parser.parse( options, parameters ) ;

      logDirectory = extractDirectory( baseDirectory, OPTION_LOG_DIRECTORY, line, false ) ;

      if( line.hasOption( OPTION_CONTENT_ROOT.getLongOpt() ) ) {
        contentRoot = extractDirectory( baseDirectory, OPTION_CONTENT_ROOT, line ) ;
      } else {
        contentRoot = null ;
      }

      if( line.hasOption( OPTION_DEFAULT_SOURCE_CHARSET.getLongOpt() ) ) {
        defaultSourceCharset = Charset.forName(
            line.getOptionValue( OPTION_DEFAULT_SOURCE_CHARSET.getLongOpt() ) ) ;
      } else {
        defaultSourceCharset = null ;
      }

      if( line.hasOption( OPTION_DEFAULT_RENDERING_CHARSET.getLongOpt() ) ) {
        defaultRenderingCharset = Charset.forName(
            line.getOptionValue( OPTION_DEFAULT_RENDERING_CHARSET.getLongOpt() ) ) ;
      } else {
        defaultRenderingCharset = null ;
      }

      if( line.hasOption( OPTION_STYLE_DIRECTORIES.getLongOpt() ) ) {
        final String[] styleDirectoriesNames =
            line.getOptionValues( OPTION_STYLE_DIRECTORIES.getLongOpt() ) ;
        LOG.debug( "Argument for Style directories = '%s'",
            Lists.newArrayList( styleDirectoriesNames ) ) ;
        styleDirectories = extractDirectories( baseDirectory, styleDirectoriesNames ) ;
      } else {
        styleDirectories = ImmutableList.of() ;
      }

      hyphenationDirectory = extractDirectory( baseDirectory, OPTION_HYPHENATION_DIRECTORY, line ) ;

      if( line.hasOption( OPTION_FONT_DIRECTORIES.getLongOpt() ) ) {
        final String[] fontDirectoriesNames =
            line.getOptionValues( OPTION_FONT_DIRECTORIES.getLongOpt() ) ;
        LOG.debug( "Argument for Font directories = '%s'",
            Lists.newArrayList( fontDirectoriesNames ) ) ;
        fontDirectories = extractDirectories( baseDirectory, fontDirectoriesNames ) ;
      } else {
        fontDirectories = ImmutableList.of() ;
      }

    } catch( ParseException e ) {
      throw new ArgumentException( e, helpPrinter ) ;
    }

  }

  protected abstract void enrich( Options options ) ;

  private void throwArgumentException( final String message ) throws ArgumentException {
    throw new ArgumentException( message, helpPrinter ) ;
  }


// =======
// Getters
// =======

  /**
   * Return the directory used to evaluate all relative directories from.
   * @return a non-null object.
   */
  public File getBaseDirectory() {
    return baseDirectory ;
  }

  /**
   * Returns the content root directory to get content from.
   * @return a possibly-null object.
   */
  public File getContentRoot() {
    return contentRoot ;
  }

  /**
   * Returns a human-readable description of {@link #OPTION_CONTENT_ROOT}.
   */
  public String getContentRootOptionDescription() {
    return createOptionDescription( OPTION_CONTENT_ROOT ) ;
  }

  /**
   * Returns a human-readable description of {@link #OPTION_FONT_DIRECTORIES}.
   */
  public String getFontDirectoriesOptionDescription() {
    return createOptionDescription( OPTION_FONT_DIRECTORIES ) ;
  }

  /**
   * Returns the directories containing embeddable font files.
   * @return a non-null object iterating over no nulls.
   */
  public Iterable< File > getFontDirectories() {
    return fontDirectories;
  }

  /**
   * Returns a human-readable description of {@link #OPTION_STYLE_DIRECTORIES}.
   */
  public String getStyleDirectoriesDescription() {
    return createOptionDescription( OPTION_STYLE_DIRECTORIES ) ;
  }

  /**
   * Returns the directory containing style files.
   * @return a non-null object iterating over no nulls.
   */
  public Iterable< File > getStyleDirectories() {
    return styleDirectories;
  }

  /**
   * Returns a human-readable of {@link #OPTION_HYPHENATION_DIRECTORY}.
   */
  public String getHyphenationDirectoryOptionDescription() {
    return createOptionDescription( OPTION_HYPHENATION_DIRECTORY ) ;
  }
  /**
   * Returns the directory containing hyphenation files.
   * @return a null object if undefined, a reference to an existing directory otherwise.
   */
  public File getHyphenationDirectory() {
    return hyphenationDirectory;
  }

  /**
   * Returns the directory to spit log files into.
   * @return a null object if undefined, a reference to an existing directory otherwise.
   */
  public File getLogDirectory() {
    return logDirectory;
  }

  /**
   * Returns a human-readable of {@link #OPTION_DEFAULT_SOURCE_CHARSET}.
   */
  public String getDefaultSourceCharsetOptionDescription() {
    return createOptionDescription( OPTION_DEFAULT_SOURCE_CHARSET ) ;
  }
  /**
   * Returns the default charset for source documents.
   * @return a null object if undefined, a valid {@code Charset} otherwise.
   */
  public Charset getDefaultSourceCharset() {
    return defaultSourceCharset ;
  }

  /**
   * Returns a human-readable of {@link #OPTION_DEFAULT_RENDERING_CHARSET}.
   */
  public String getDefaultRenderingCharsetOptionDescription() {
    return createOptionDescription( OPTION_DEFAULT_RENDERING_CHARSET ) ;
  }
  /**
   * Returns the default charset for rendering documents.
   * @return a null object if undefined, a valid {@code Charset} otherwise.
   */
  public Charset getDefaultRenderingCharset() {
    return defaultRenderingCharset;
  }

// ==========
// Extractors
// ==========

  protected File extractDirectory(
      final File baseDirectory,
      final Option option,
      final CommandLine line
  ) throws ArgumentException {
    return extractDirectory( baseDirectory, option, line, true ) ;
  }

  protected File extractDirectory(
      final File baseDirectory,
      final Option option,
      final CommandLine line,
      final boolean failOnNonExistingDirectory
  )
      throws ArgumentException
  {
    final File directory ;
    if( line.hasOption( option.getLongOpt() ) ) {
      final String directoryName =
          line.getOptionValue( option.getLongOpt() ) ;
      LOG.debug( "Argument for %s = '%s'", option.getDescription(), directoryName ) ;
      directory = extractDirectory( baseDirectory, directoryName, failOnNonExistingDirectory ) ;
    } else {
      directory = null ;
    }
    return directory ;
  }

  protected final Iterable< File > extractDirectories( 
      final File parent, 
      final String[] directoryNames 
  )
      throws ArgumentException
  {
    final List directories = Lists.newArrayList() ;
    for( final String directoryName : directoryNames ) {
      directories.add( extractDirectory( parent, directoryName, true ) ) ;
    }
    return ImmutableList.copyOf( directories ) ;
  }

  protected final File extractDirectory(
      final File parent,
      final String directoryName,
      final boolean failOnNonExistingDirectory
  )
      throws ArgumentException
  {
    final File directory ;
    final File maybeAbsoluteDirectory = new File( directoryName ) ;
    if( maybeAbsoluteDirectory.isAbsolute() ) {
      directory = maybeAbsoluteDirectory ;
    } else {
      directory = new File( parent, directoryName ) ;
    }
    if( failOnNonExistingDirectory && ! ( directory.exists() && directory.isDirectory() ) ) {
      throwArgumentException( "Not a directory: '" + directoryName + "'" ) ;
    }
    return directory ;
  }


// =================
// Commons-CLI stuff
// =================

  public static final String OPTIONNAME_FONT_DIRECTORIES = "font-dirs" ;

  private static final Option OPTION_FONT_DIRECTORIES = OptionBuilder
      .withLongOpt( OPTIONNAME_FONT_DIRECTORIES )
      .withDescription( "Directories containing embeddable fonts" )
      .withValueSeparator()
      .hasArgs()
      .create()
  ;

  private static final Option OPTION_EMPTY = OptionBuilder
      .withLongOpt( "" )
      .withDescription( "Empty option to end directory list" )
      .create()
  ;

  public static final String OPTIONNAME_CONTENT_ROOT = "content-root" ;
  
  private static final Option OPTION_CONTENT_ROOT = OptionBuilder
      .withLongOpt( OPTIONNAME_CONTENT_ROOT )
      .withDescription( "Root directory for content files" )
      .withValueSeparator()
      .hasArg()
      .create()
  ;

  public static final String OPTIONNAME_STYLE_DIRECTORIES = "style-dirs" ;

  private static final Option OPTION_STYLE_DIRECTORIES = OptionBuilder
      .withLongOpt( OPTIONNAME_STYLE_DIRECTORIES )
      .withDescription( "Directories containing style files" )
      .withValueSeparator()
      .hasArgs()
      .create()
  ;

  public static final String OPTIONNAME_DEFAULT_SOURCE_CHARSET = "source-charset" ;

  private static final Option OPTION_DEFAULT_SOURCE_CHARSET = OptionBuilder
      .withLongOpt( OPTIONNAME_DEFAULT_SOURCE_CHARSET )
      .withDescription( "Default charset for source documents" )
      .withValueSeparator()
      .hasArg()
      .create()
  ;

  public static final String OPTIONNAME_DEFAULT_RENDERING_CHARSET = "rendering-charset" ;

  private static final Option OPTION_DEFAULT_RENDERING_CHARSET = OptionBuilder
      .withLongOpt( OPTIONNAME_DEFAULT_RENDERING_CHARSET )
      .withDescription( "Default charset for rendered documents" )
      .withValueSeparator()
      .hasArg()
      .create()
  ;


  public static final String OPTIONPREFIX = "--" ;
  public static final String LOG_DIRECTORY_OPTION_NAME = "log-dir" ;

  private static final Option OPTION_LOG_DIRECTORY = OptionBuilder
      .withLongOpt( LOG_DIRECTORY_OPTION_NAME )
      .withDescription( "Directory containing log file(s)" )
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

  public static final String HELP_OPTION_NAME = "help";
  private static final Option OPTION_HELP = OptionBuilder
      .withLongOpt( HELP_OPTION_NAME )
      .withDescription( "Print help" )
      .create()
  ;


  protected static String createOptionDescription( final Option option ) {
    return OPTIONPREFIX + option.getLongOpt() + ", " + option.getDescription() ;
  }


// ====
// Help
// ====

  public static class HelpPrinter {
    private final Options options ;

    public HelpPrinter( final Options options ) {
      this.options = options;
    }

    public void print( 
        final PrintStream printStream, 
        final String commandName, 
        final int columns 
    ) {
      print( new PrintWriter( printStream ), commandName, columns ) ;
    }
    
    public void print( 
        final PrintWriter printWriter, 
        final String commandName, 
        final int columns 
    ) {
      final HelpFormatter helpFormatter = new HelpFormatter() ;
      helpFormatter.printHelp(
          printWriter,
          columns,
          commandName,
          "",
          options,
          2,
          2,
          ""
      ) ;
      printWriter.flush() ;
    }

    public String asString( final String commandName, final int columns ) {
      final StringWriter stringWriter = new StringWriter() ;
      print( new PrintWriter( stringWriter ), commandName, columns ) ;
      return stringWriter.toString() ;
    }
  }

  private static final String HELP_TRIGGER = OPTIONPREFIX + OPTION_HELP.getLongOpt() ;

  private boolean containsHelpTrigger( final String[] parameters ) {
    for( int i = 0 ; i < parameters.length ; i++ ) {
      final String parameter = parameters[ i ] ;
      if( HELP_TRIGGER.equals( parameter ) ) {
        return true ;
      }
    }
    return false ;
  }



}

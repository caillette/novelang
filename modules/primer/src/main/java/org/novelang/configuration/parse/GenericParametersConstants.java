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
package org.novelang.configuration.parse;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * Various constants aside of {@link GenericParameters}.
 * It's important to declare them in a class where there is no
 * {@link org.novelang.logger.Logger} instance because
 * {@link org.novelang.outfit.LogbackConfigurationTools} needs to access some of them
 * before the logging system gets configured.
 *
 * @author Laurent Caillette
 */
public class GenericParametersConstants {

  public static final String OPTIONNAME_FONT_DIRECTORIES = "font-dirs" ;

  public static final Option OPTION_FONT_DIRECTORIES = OptionBuilder
      .withLongOpt( OPTIONNAME_FONT_DIRECTORIES )
      .withDescription( "Directories containing embeddable fonts" )
      .withValueSeparator()
      .hasArgs()
      .create()
  ;

  public static final Option OPTION_EMPTY = OptionBuilder
      .withLongOpt( "" )
      .withDescription( "Empty option to end directory list" )
      .create()
  ;

  public static final String OPTIONNAME_CONTENT_ROOT = "content-root" ;

  public static final Option OPTION_CONTENT_ROOT = OptionBuilder
      .withLongOpt( OPTIONNAME_CONTENT_ROOT )
      .withDescription( "Root directory for content files" )
      .withValueSeparator()
      .hasArg()
      .create()
  ;

  public static final String OPTIONNAME_TEMPORARY_DIRECTORY = "temporary-dir" ;

  public static final Option OPTION_TEMPORARY_DIRECTORY = OptionBuilder
      .withLongOpt( OPTIONNAME_TEMPORARY_DIRECTORY )
      .withDescription( "Directory for temporary files" )
      .withValueSeparator()
      .hasArg()
      .create()
  ;

  public static final String OPTIONNAME_STYLE_DIRECTORIES = "style-dirs" ;

  public static final Option OPTION_STYLE_DIRECTORIES = OptionBuilder
      .withLongOpt( OPTIONNAME_STYLE_DIRECTORIES )
      .withDescription( "Directories containing style files" )
      .withValueSeparator()
      .hasArgs()
      .create()
  ;

  public static final String OPTIONNAME_DEFAULT_SOURCE_CHARSET = "source-charset" ;

  public static final Option OPTION_DEFAULT_SOURCE_CHARSET = OptionBuilder
      .withLongOpt( OPTIONNAME_DEFAULT_SOURCE_CHARSET )
      .withDescription( "Default charset for source documents" )
      .withValueSeparator()
      .hasArg()
      .create()
  ;

  public static final String OPTIONNAME_DEFAULT_RENDERING_CHARSET = "rendering-charset" ;

  public static final Option OPTION_DEFAULT_RENDERING_CHARSET = OptionBuilder
      .withLongOpt( OPTIONNAME_DEFAULT_RENDERING_CHARSET )
      .withDescription( "Default charset for rendered documents" )
      .withValueSeparator()
      .hasArg()
      .create()
  ;

  public static final String OPTIONPREFIX = "--" ;

  public static final String LOG_DIRECTORY_OPTION_NAME = "log-dir" ;

  public static final Option OPTION_LOG_DIRECTORY = OptionBuilder
      .withLongOpt( LOG_DIRECTORY_OPTION_NAME )
      .withDescription( "Directory containing log file(s)" )
      .withValueSeparator()
      .hasArg()
      .create()
  ;

  public static final Option OPTION_HYPHENATION_DIRECTORY = OptionBuilder
      .withLongOpt( "hyphenation-dir" )
      .withDescription( "Directory containing hyphenation files" )
      .withValueSeparator()
      .hasArg()
      .create()
  ;

  public static final String HELP_OPTION_NAME = "help";

  public static final Option OPTION_HELP = OptionBuilder
      .withLongOpt( HELP_OPTION_NAME )
      .withDescription( "Print help" )
      .create()
  ;

  public static final String HELP_TRIGGER = OPTIONPREFIX + OPTION_HELP.getLongOpt() ;

  private GenericParametersConstants() {
  }

  /**
   * Returns a human-readable description of {@link #OPTION_CONTENT_ROOT}.
   */
  public static String getContentRootOptionDescription() {
    return createOptionDescription( OPTION_CONTENT_ROOT ) ;
  }

  /**
   * Returns a human-readable description of {@link #OPTION_FONT_DIRECTORIES}.
   */
  public static String getFontDirectoriesOptionDescription() {
    return createOptionDescription( OPTION_FONT_DIRECTORIES ) ;
  }

  /**
   * Returns a human-readable description of {@link #OPTION_STYLE_DIRECTORIES}.
   */
  public static String getStyleDirectoriesDescription() {
    return createOptionDescription( OPTION_STYLE_DIRECTORIES ) ;
  }

  /**
   * Returns a human-readable of {@link #OPTION_HYPHENATION_DIRECTORY}.
   */
  public static String getHyphenationDirectoryOptionDescription() {
    return createOptionDescription( OPTION_HYPHENATION_DIRECTORY ) ;
  }

  /**
   * Returns a human-readable of {@link #OPTION_LOG_DIRECTORY}.
   */
  public static String getLogDirectoryOptionDescription() {
    return createOptionDescription( OPTION_LOG_DIRECTORY ) ;
  }

  /**
   * Returns a human-readable of {@link #OPTION_DEFAULT_SOURCE_CHARSET}.
   */
  public static String getDefaultSourceCharsetOptionDescription() {
    return createOptionDescription( OPTION_DEFAULT_SOURCE_CHARSET ) ;
  }

  /**
   * Returns a human-readable of {@link #OPTION_DEFAULT_RENDERING_CHARSET}.
   */
  public static String getDefaultRenderingCharsetOptionDescription() {
    return createOptionDescription( OPTION_DEFAULT_RENDERING_CHARSET ) ;
  }

  protected static String createOptionDescription( final Option option ) {
    return OPTIONPREFIX + option.getLongOpt() + ", " + option.getDescription() ;
  }
}

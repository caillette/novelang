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
package org.novelang.configuration.parse;

import java.io.File;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * @author Laurent Caillette
 */
public abstract class BatchParameters extends GenericParameters {

  private final File outputDirectory ;

  public static final String OPTIONNAME_OUTPUTDIRECTORY = "output-dir" ;

  @SuppressWarnings({"AccessStaticViaInstance"})
  static final Option OPTION_OUTPUT_DIRECTORY = OptionBuilder
      .withLongOpt( OPTIONNAME_OUTPUTDIRECTORY )
      .withDescription( "Output directory for rendered documents" )
      .hasArg()
      .create()
  ;


  public BatchParameters(
      final File baseDirectory,
      final String[] parameters
  ) throws ArgumentException {
    super( baseDirectory, parameters ) ;
    outputDirectory = extractDirectory(
        baseDirectory,
        OPTION_OUTPUT_DIRECTORY,
        line,
        false
    ) ;
  }

  /**
   * Returns the directory where documents are produced to.
   * @return null if not defined, an existing directory otherwise.
   */
  public File getOutputDirectory() {
    return outputDirectory ;
  }


  public static String getOutputDirectoryOptionDescription() {
    return GenericParametersConstants.createOptionDescription( OPTION_OUTPUT_DIRECTORY ) ;
  }

}

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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.cli2.util.HelpFormatter;

/**
 * Thrown when a parsing error occurs <em>or</em> when help was requested.
 * 
 * @author Laurent Caillette
 */
public class ArgumentsNotParsedException extends Exception {

//  private final HelpFormatter helpFormatter ;

  public ArgumentsNotParsedException( HelpFormatter helpFormatter ) {
    super( "\n" + extractMessage( helpFormatter ) ) ;
//    this.helpFormatter = helpFormatter ;
  }

//  public HelpFormatter getHelpFormatter() {
//    return helpFormatter ;
//  }

  private static final String extractMessage( HelpFormatter helpFormatter ) {
    final StringWriter helpWriter = new StringWriter() ;
    helpFormatter.setPrintWriter( new PrintWriter( helpWriter ) ) ;
    helpFormatter.print() ;
    return helpWriter.toString() ;
  }
}

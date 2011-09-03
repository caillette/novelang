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
package org.novelang.configuration.parse;

import com.google.common.base.Preconditions;

/**
 * Thrown when a parsing error occurs <em>or</em> when help was requested.
 * 
 * @author Laurent Caillette
 */
public class ArgumentException extends Exception {

  private final GenericParameters.HelpPrinter helpPrinter;
  private final boolean helpRequested ;

  public ArgumentException( final GenericParameters.HelpPrinter helpPrinter ) {
    helpRequested = true ;
    this.helpPrinter = Preconditions.checkNotNull( helpPrinter ) ;
  }

  public ArgumentException( 
      final String message, 
      final GenericParameters.HelpPrinter helpPrinter 
  ) {
    super( message ) ;
    this.helpRequested = false ;
    this.helpPrinter = Preconditions.checkNotNull( helpPrinter ) ;
  }

  public ArgumentException( final Exception e, final GenericParameters.HelpPrinter helpPrinter ) {
    this( e.getMessage(), helpPrinter ) ;
  }

  public boolean isHelpRequested() {
    return helpRequested ;
  }

  public GenericParameters.HelpPrinter getHelpPrinter() {
    return helpPrinter;
  }
}

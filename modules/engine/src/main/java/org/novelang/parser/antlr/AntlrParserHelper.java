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

package org.novelang.parser.antlr;

import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;

import org.novelang.common.Location;
import org.novelang.common.LocationFactory;

/**
 * @author Laurent Caillette
 */
public final class AntlrParserHelper {

  private AntlrParserHelper() { }

  public static Location createLocation( final LocationFactory factory, final TokenStream input ) {
    return factory.createLocation(
        ( ( Token ) input.LT( 1 ) ).getLine(),
        ( ( Token ) input.LT( 1 ) ).getCharPositionInLine()
    ) ;
  }


}

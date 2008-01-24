/*
 * Copyright (C) 2006 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.parser.antlr;

import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import novelang.model.common.Location;
import novelang.model.common.LocatorFactory;

/**
 * @author Laurent Caillette
 */
public final class AntlrParserHelper {

  private AntlrParserHelper() { }

  public static Location createLocation( LocatorFactory factory, TokenStream input ) {
    return factory.createStructuralLocator(
        ( ( Token ) input.LT( 1 ) ).getLine(),
        ( ( Token ) input.LT( 1 ) ).getCharPositionInLine()
    ) ;
  }


}

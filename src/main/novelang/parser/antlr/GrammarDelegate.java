/*
 * Copyright (C) 2008 Laurent Caillette
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

import java.util.List;

import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.Token;
import org.antlr.runtime.CommonToken;
import novelang.model.common.LocationFactory;
import novelang.model.common.Problem;
import novelang.model.common.Location;
import novelang.parser.SymbolUnescape;
import novelang.parser.UnsupportedEscapedSymbolException;
import com.google.common.collect.Lists;

/**
 * Holds stuff which is not convenient to code inside ANTLR grammar because of code generation.
 *
 * @author Laurent Caillette
 */
public abstract class GrammarDelegate {

  private final LocationFactory locationFactory ;
  private final List< Problem > problems = Lists.newArrayList() ;

  public GrammarDelegate( LocationFactory locationFactory ) {
    this.locationFactory = locationFactory ;
  }

  public void report( String antlrMessage ) {
    problems.add( Problem.createProblem( locationFactory, antlrMessage ) ) ;
  }

  public Iterable< Problem > getProblems() {
    return Lists.immutableList( problems ) ;
  }

  public LocationFactory getLocationFactory() {
    return locationFactory;
  }

  public Tree createTree( int tokenIdentifier, String tokenPayload ) {
    return new CustomTree(
        getLocationFactory(),
        new CommonToken( tokenIdentifier, tokenPayload )
    ) ;
  }

  public String escapeSymbol( String unescaped, int line, int column ) {
    try {
      return SymbolUnescape.unescape( unescaped ) ;
    } catch( UnsupportedEscapedSymbolException e ) {
      final Location location = locationFactory.createLocation( line, column ) ;
      problems.add( Problem.createProblem(
          "Cannot unescape: '" + unescaped + "'", location ) ) ;
      return "<unescaped:" + unescaped + ">" ;
    }
  }
}

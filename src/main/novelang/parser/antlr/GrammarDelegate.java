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

package novelang.parser.antlr;

import java.util.List;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.tree.Tree;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList;
import novelang.common.Location;
import novelang.common.LocationFactory;
import novelang.common.Problem;
import novelang.parser.Symbols;
import novelang.parser.UnsupportedEscapedSymbolException;

/**
 * Holds stuff which is not convenient to code inside ANTLR grammar because of code generation.
 *
 * @author Laurent Caillette
 */
public class GrammarDelegate {

  private final LocationFactory locationFactory ;
  private final List< Problem > problems = Lists.newArrayList() ;

  /**
   * With this constructor the {@code LocationFactory} gives only partial information.
   * Its use is reserved to ANTLR parser, which needs a default {@code GrammarDelegate}
   * for running in the debugger.
   */
  public GrammarDelegate() {
    this.locationFactory = new LocationFactory() {
      public Location createLocation( int line, int column ) {
        return new Location( "<debug>", line, column ) ;
      }
    } ;
  }

  public GrammarDelegate( LocationFactory locationFactory ) {
    this.locationFactory = locationFactory ;
  }

  public void report( String antlrMessage ) {
    problems.add( Problem.createProblem( locationFactory, antlrMessage ) ) ;
  }

  public Iterable< Problem > getProblems() {
    return ImmutableList.copyOf( problems ) ;
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
      return Symbols.unescapeSymbol( unescaped ) ;
    } catch( UnsupportedEscapedSymbolException e ) {
      final Location location = locationFactory.createLocation( line, column ) ;
      problems.add( Problem.createProblem(
          "Cannot unescape: '" + unescaped + "'", location ) ) ;
      return "<unescaped:" + unescaped + ">" ;
    }
  }
}

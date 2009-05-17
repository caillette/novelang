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

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.tree.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.common.Location;
import novelang.common.LocationFactory;
import novelang.common.Problem;
import novelang.parser.antlr.delimited.BlockDelimiter;
import novelang.parser.NoUnescapedCharacterException;
import novelang.parser.SourceUnescape;
import novelang.parser.antlr.delimited.DefaultBlockDelimitersBoundary;
import novelang.parser.antlr.delimited.BlockDelimiterSupervisor;
import novelang.parser.antlr.delimited.DefaultBlockDelimiterSupervisor;

/**
 * Holds stuff which is not convenient to code inside ANTLR grammar because of code generation.
 *
 * @author Laurent Caillette
 */
public class GrammarDelegate extends ProblemDelegate implements BlockDelimiterSupervisor {

  private static final Logger LOGGER = LoggerFactory.getLogger( GrammarDelegate.class ) ;

  /**
   * With this constructor the {@code LocationFactory} gives only partial information.
   * Its use is reserved to ANTLR parser, which needs a default {@code GrammarDelegate}
   * for running in the debugger.
   */
  public GrammarDelegate() { }

  public GrammarDelegate( LocationFactory locationFactory ) {
    super( locationFactory ) ;
  }

  public Tree createTree( int tokenIdentifier, String tokenPayload ) {
    return new CustomTree(
        getLocationFactory(),
        new CommonToken( tokenIdentifier, tokenPayload )
    ) ;
  }

  public String unescapeCharacter( String escaped, int line, int column ) {
    try {
      return "" + SourceUnescape.unescapeCharacter( escaped ) ;
    } catch( NoUnescapedCharacterException e ) {
      final Location location = locationFactory.createLocation( line, column ) ;
      problems.add( Problem.createProblem(
          "Cannot unescape: '" + escaped + "'", location ) ) ;
      return "<unescaped:" + escaped + ">" ;
    }
  }


// ==========
// Delimiters
// ==========


  private final BlockDelimiterSupervisor blockDelimiterSupervisor =
      new DefaultBlockDelimiterSupervisor( locationFactory ) ;

  public void startDelimitedText( BlockDelimiter blockDelimiter, Token startToken ) {
    blockDelimiterSupervisor.startDelimitedText( blockDelimiter, startToken ) ;
  }

  public void reachEndDelimiter( BlockDelimiter blockDelimiter ) {
    blockDelimiterSupervisor.reachEndDelimiter( blockDelimiter ) ;
  }

  public void endDelimitedText( BlockDelimiter blockDelimiter ) {
    blockDelimiterSupervisor.endDelimitedText( blockDelimiter ) ;
  }

  public void reportMissingDelimiter(
      BlockDelimiter blockDelimiter,
      MismatchedTokenException mismatchedTokenException
  )
      throws MismatchedTokenException
  {
    blockDelimiterSupervisor.reportMissingDelimiter( blockDelimiter, mismatchedTokenException ) ;
  }

  public void enterBlockDelimiterBoundary( Token location ) {
    blockDelimiterSupervisor.enterBlockDelimiterBoundary( location ) ;
  }

  public Iterable< Problem > leaveBlockDelimiterBoundary() {
    final Iterable< Problem > boundaryProblems =
        blockDelimiterSupervisor.leaveBlockDelimiterBoundary() ;
    report( boundaryProblems ) ;
    return boundaryProblems;
  }

  /**
   * TODO remove this method.
   */
  public BlockDelimiterSupervisor getBlockDelimiterSupervisor() {
    return blockDelimiterSupervisor;
  }

}

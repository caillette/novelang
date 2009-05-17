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
import org.antlr.runtime.Token;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.tree.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.common.Location;
import novelang.common.LocationFactory;
import novelang.common.Problem;
import novelang.common.BlockDelimiter;
import novelang.parser.NoUnescapedCharacterException;
import novelang.parser.SourceUnescape;
import com.google.common.collect.Lists;
import com.google.common.base.Preconditions;

/**
 * Holds stuff which is not convenient to code inside ANTLR grammar because of code generation.
 *
 * @author Laurent Caillette
 */
public class GrammarDelegate extends ProblemDelegate {

  private static final Logger LOGGER = LoggerFactory.getLogger( ProblemDelegate.class ) ;

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

  private static class DelimitedText {
    private final String startDelimiter ;
    private final int line ;
    private final int column ;

    private DelimitedText( String startDelimiter, int line, int column ) {
      this.startDelimiter = startDelimiter;
      this.line = line;
      this.column = column;
    }

    public String getStartDelimiter() {
      return startDelimiter ;
    }

    public int getLine() {
      return line ;
    }

    public int getColumn() {
      return column ;
    }
  }

  private final List< DelimitedText > delimiterStack = Lists.newLinkedList() ;
  private DelimitedText innermostMismatch = null ;
  private int innermostMismatchDepth = -1 ;
  private boolean handlingEndDelimiter = false ;

  public void startDelimitedText( Token startToken1, Token startToken2 ) {
    LOGGER.debug( "startDelimiter[ startToken='{}' ; line={} ]",
        startToken1.getText() + startToken2.getText(),
        startToken1.getLine()
    ) ;
    delimiterStack.add( new DelimitedText(
        startToken1.getText() + startToken2.getText(),
        startToken1.getLine(),
        startToken1.getCharPositionInLine()
    ) ) ;
  }

  public void startDelimitedText( Token startToken ) {
    LOGGER.debug( "startDelimiter[ startToken='{}' ; line={} ]",
        startToken.getText(),
        startToken.getLine()
    ) ;
    delimiterStack.add( new DelimitedText(
        startToken.getText(),
        startToken.getLine(),
        startToken.getCharPositionInLine()
    ) ) ;
  }

  public void startDelimitedText( BlockDelimiter blockDelimiter, Token startToken ) {
    LOGGER.debug( "startDelimiter[ blockDelimiter={} ; line={} ]",
        blockDelimiter,
        startToken.getLine()
    ) ;
    delimiterStack.add( new DelimitedText(
        startToken.getText(),
        startToken.getLine(),
        startToken.getCharPositionInLine()
    ) ) ;
  }

  public void reachEndDelimiter( BlockDelimiter blockDelimiter ) {
    LOGGER.debug( "reachEndDelimiter[ {} ]", blockDelimiter ) ;
    handlingEndDelimiter = true ;
  }

  public void endDelimitedText( BlockDelimiter blockDelimiter ) {
    LOGGER.debug( "endDelimitedText[ {} ]", blockDelimiter ) ;
    Preconditions.checkArgument( ! delimiterStack.isEmpty() ) ;
    handlingEndDelimiter = false ;
    delimiterStack.remove( delimiterStack.size() - 1 ) ;

    if( delimiterStack.isEmpty() && innermostMismatch != null ) {
      report(
          "No ending delimiter matching with " + innermostMismatch.getStartDelimiter(),
          innermostMismatch.getLine(),
          innermostMismatch.getColumn()
      ) ;

    }
  }

  public void reportMissingDelimiter(
      BlockDelimiter blockDelimiter,
      MismatchedTokenException mismatchedTokenException
  )
      throws MismatchedTokenException
  {
    LOGGER.debug( "reportMissingDelimiter[ blockDelimiter={} ; line={} ]",
        blockDelimiter, mismatchedTokenException.line ) ;
    if( handlingEndDelimiter ){
      final int depth = delimiterStack.size() - 1;
      if( depth > innermostMismatchDepth ){
        innermostMismatch = delimiterStack.get( depth ) ;
        innermostMismatchDepth = depth ;
      }
      handlingEndDelimiter = false ;
    }
  }


}

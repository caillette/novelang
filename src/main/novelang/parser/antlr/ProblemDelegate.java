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
 */package novelang.parser.antlr;

import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.MismatchedTokenException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.base.Preconditions;
import novelang.common.Location;
import novelang.common.LocationFactory;
import novelang.common.Problem;
import novelang.common.BlockDelimiter;

/**
 * Just hooks into ANTLR's error reporting.
 */
public class ProblemDelegate {

  private static final Logger LOGGER = LoggerFactory.getLogger( ProblemDelegate.class ) ;

  protected final LocationFactory locationFactory ;
  protected final List< Problem > problems = Lists.newArrayList() ;


  public ProblemDelegate() {
    this.locationFactory = new LocationFactory() {
      public Location createLocation( int line, int column ) {
        return new Location( "<debug>", line, column ) ;
      }
    } ;    
  }

  public ProblemDelegate( LocationFactory locationFactory ) {
    this.locationFactory = locationFactory;
  }

  public void report( String antlrMessage ) {
    problems.add( Problem.createProblem( locationFactory, antlrMessage ) ) ;
  }

  public void report( RecognitionException exception ) {
    problems.add( Problem.createProblem( locationFactory, exception ) ) ;
  }

  public void report( String message, int line, int column ) {
    problems.add( Problem.createProblem( message, locationFactory, line, column ) ) ;
  }

  public Iterable< Problem > getProblems() {
    return ImmutableList.copyOf( problems ) ;
  }

  public LocationFactory getLocationFactory() {
    return locationFactory;
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

  public void handleEndDelimiter() {
    LOGGER.debug( "handleEndDelimiter" ) ;
    handlingEndDelimiter = true ;
  }

  public void endDelimitedText( BlockDelimiter blockDelimiter ) {
    LOGGER.debug( "endDelimitedText" ) ;
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

  public void reportMissingDelimiter( MismatchedTokenException mismatchedTokenException )
      throws MismatchedTokenException 
  {
    LOGGER.debug( "reportMissingDelimiter[ line={} ]", mismatchedTokenException.line ) ;
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

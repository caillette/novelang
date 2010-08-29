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
package org.novelang.parser.antlr.delimited;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.novelang.common.LocationFactory;
import org.novelang.common.Problem;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.Token;

/**
 * Delegates to multiple {@link BlockDelimitersBoundary}.
 *
 * @author Laurent Caillette
 */
public class DefaultBlockDelimiterSupervisor implements BlockDelimiterSupervisor{

  private static final Logger LOGGER =
      LoggerFactory.getLogger( DefaultBlockDelimiterSupervisor.class );

  private final LocationFactory locationFactory ;
  private final Stack stack = new Stack() ;
  private final List< Problem > problems = Lists.newArrayList() ;

  public DefaultBlockDelimiterSupervisor( final LocationFactory locationFactory ) {
    this.locationFactory = locationFactory;
  }

  public void enterBlockDelimiterBoundary( final Token location ) {
    LOGGER.debug( "Entering block delimiter boundary at line " + location.getLine() ) ;
    stack.grow() ;
  }

  public Iterable< Problem > leaveBlockDelimiterBoundary() {
    final Iterable< Problem > boundaryProblems = stack.getTop().getProblems() ;
    ( ( DefaultBlockDelimitersBoundary ) stack.getTop() ).dumpStatus() ;
    stack.shrink() ;
    Iterables.addAll( problems, boundaryProblems ) ;
    return boundaryProblems ;
  }

  public Iterable< Problem > getProblems() {
    return ImmutableList.copyOf( problems ) ;
  }

  public void startDelimitedText( final BlockDelimiter blockDelimiter, final Token startToken ) {
    if( stack.isEmpty() ) {
      LOGGER.warn( "Empty stack! Ignoring start of block delimiter" ) ;
    } else {
      stack.getTop().startDelimitedText( blockDelimiter, startToken ) ;
    }
  }

  public void reachEndDelimiter( final BlockDelimiter blockDelimiter ) {
    if( stack.isEmpty() ) {
      LOGGER.warn( "Empty stack! Ignoring start of block delimiter" ) ;
    } else {
      stack.getTop().reachEndDelimiter( blockDelimiter ) ;
    }
  }

  public void endDelimitedText( final BlockDelimiter blockDelimiter ) {
    if( stack.isEmpty() ) {
      LOGGER.warn( "Empty stack! Ignoring start of block delimiter" ) ;
    } else {
      stack.getTop().endDelimitedText( blockDelimiter ) ;
    }
  }

  public void reportMissingDelimiter(
      final BlockDelimiter blockDelimiter,
      final MismatchedTokenException mismatchedTokenException
  ) throws MismatchedTokenException {
    if( stack.isEmpty() ) {
      LOGGER.warn( "Empty stack! Ignoring start of block delimiter" ) ;
    } else {
      stack.getTop().reportMissingDelimiter( blockDelimiter, mismatchedTokenException ) ;
    }
  }

  private class Stack {
    private final List< BlockDelimitersBoundary > blockDelimitersBoundaries = Lists.newArrayList() ;

    public void grow() {
      blockDelimitersBoundaries.add( new DefaultBlockDelimitersBoundary( locationFactory ) ) ;
    }

    public boolean isEmpty() {
      return blockDelimitersBoundaries.isEmpty() ;
    }

    public BlockDelimitersBoundary getTop() {
      if( blockDelimitersBoundaries.isEmpty() ) {
        throw new IllegalStateException( "Empty stack" ) ;
      }
      return blockDelimitersBoundaries.get( blockDelimitersBoundaries.size() - 1 ) ;
    }

    public void shrink() {
      if( blockDelimitersBoundaries.isEmpty() ) {
        throw new IllegalStateException( "Empty stack" ) ;
      }
      blockDelimitersBoundaries.remove( blockDelimitersBoundaries.size() - 1 ) ;
    }
  }

}

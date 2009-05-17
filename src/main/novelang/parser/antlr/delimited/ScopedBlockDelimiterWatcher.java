/*
 * Copyright (C) 2009 Laurent Caillette
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
package novelang.parser.antlr.delimited;

import java.util.Map;
import java.util.List;

import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.parser.antlr.delimited.BlockDelimiter;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;

/**
 * Receives notifications from {@link novelang.parser.antlr.GrammarDelegate} from what's going on with delimiters.
 *
 * @author Laurent Caillette
 */
public class ScopedBlockDelimiterWatcher {

  private static final Logger LOGGER = LoggerFactory.getLogger( ScopedBlockDelimiterWatcher.class ) ;

  private final Map< BlockDelimiter, DelimitedBlockStatus > primes = Maps.newHashMap() ;

  public ScopedBlockDelimiterWatcher() {
    for( BlockDelimiter blockDelimiter : BlockDelimiter.values() ) {
      primes.put( blockDelimiter, new DelimitedBlockStatus( blockDelimiter ) ) ;
    }
  }

  public void startDelimitedText( BlockDelimiter blockDelimiter, Token startToken ) {
    primes.get( blockDelimiter )
        .updatePosition( startToken.getLine(), startToken.getCharPositionInLine() )
        .increaseStartCount()
    ;

  }

  public void reachEndDelimiter( BlockDelimiter blockDelimiter ) {
    primes.get( blockDelimiter ).increaseReachEndCount() ;
  }

  public void endDelimitedText( BlockDelimiter blockDelimiter ) {
    primes.get( blockDelimiter ).increaseEndPassedCount() ;
  }

  public void reportMissingDelimiter(
      BlockDelimiter blockDelimiter,
      MismatchedTokenException mismatchedTokenException
  )
      throws MismatchedTokenException
  {
    primes.get( blockDelimiter ).increaseMissingDelimiterCount() ;
  }

  public Iterable< DelimitedBlockStatus > getFaultyDelimitedBlocks() {
    final List< DelimitedBlockStatus > faultyDelimitedBlockStatuses = Lists.newArrayList() ;
    for( BlockDelimiter blockDelimiter : BlockDelimiter.getTwinDelimiters() ) {
      final DelimitedBlockStatus status = primes.get( blockDelimiter ) ;
      if( ! status.isConsistent() ) {
        faultyDelimitedBlockStatuses.add( status ) ;
      }
    }
    if( faultyDelimitedBlockStatuses.isEmpty() ) {
      for( BlockDelimiter blockDelimiter : BlockDelimiter.getOnlyDelimiters() ) {
        final DelimitedBlockStatus status = primes.get( blockDelimiter ) ;
        if( ! status.isConsistent() ) {
          faultyDelimitedBlockStatuses.add( status ) ;
        }
      }
    }
    return faultyDelimitedBlockStatuses ;
  }

  public void dumpStatus() {
    final StringBuffer buffer = new StringBuffer() ;
    for( BlockDelimiter blockDelimiter : BlockDelimiter.values() ) {
      final DelimitedBlockStatus status = primes.get( blockDelimiter ) ;
      buffer.append( "\n" ) ;
      buffer.append( String.format( "%1$16s", blockDelimiter.name() ) ) ;
      buffer.append( " " ) ;
      buffer.append( status.getInternalStatusAsString() ) ;
    }
    LOGGER.debug( buffer.toString() ) ;

  }


}

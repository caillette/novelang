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
package org.novelang.testing;

import java.util.concurrent.TimeUnit;

import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;
import static org.junit.Assert.fail;

/**
 * @author Laurent Caillette
 */
public class RepeatedAssert {

  private static final Logger LOGGER = LoggerFactory.getLogger( RepeatedAssert.class ) ;

  private RepeatedAssert() {
  }

  public static void assertEventually(
      final StandalonePredicate predicate,
      final long period,
      final TimeUnit timeUnit,
      final int retries
  ) {
    checkArgument( retries > 0 ) ;
    checkArgument( period > 0L ) ;

    LOGGER.debug( "Asserting for a maximum duration of ",
        period * ( long ) retries, " ", timeUnit, "..." ) ;
    
    int retryCount = 0 ;
    while( true ) {

      if( predicate.apply() ) {
        return ;
      }
      if( retryCount ++ < retries ) {
        try {
          LOGGER.debug( "Unmatched predicate " + predicate + ", waiting a bit and retrying..." );
          timeUnit.sleep( period ) ;
        } catch( InterruptedException e ) {
          throw new RuntimeException( "Should not happen", e ) ;
        }
        continue ;
      }
      fail( "Unmatched predicate after " + retries + " retries." ) ;
    }

  }

}

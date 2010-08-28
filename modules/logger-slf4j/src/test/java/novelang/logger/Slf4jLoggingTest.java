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
package novelang.logger;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Laurent Caillette
 */
@Ignore( "It pollutes Maven logs." )
public class Slf4jLoggingTest {

  @Test
  public void noSmoke() {
    
    LOGGER.trace( "Hi here!" ) ;
    LOGGER.trace( "Hi", " here!" ) ;
    LOGGER.trace( throwableForLogging, "Hi", " here!" ) ;
    LOGGER.trace( throwableForLogging ) ;

    LOGGER.debug( "Hi here!" ) ;
    LOGGER.debug( "Hi", " here!" ) ;
    LOGGER.debug( throwableForLogging, "Hi", " here!" ) ;
    LOGGER.debug( throwableForLogging ) ;

    LOGGER.info( "Hi here!" ) ;
    LOGGER.info( "Hi", " here!" ) ;
    LOGGER.info( throwableForLogging, "Hi", " here!" ) ;
    LOGGER.info( throwableForLogging ) ;
    
    LOGGER.warn( "Hi here!" ) ;
    LOGGER.warn( "Hi", " here!" ) ;
    LOGGER.warn( throwableForLogging, "Hi", " here!" ) ;
    LOGGER.warn( throwableForLogging ) ;

    LOGGER.error( "Hi here!" ) ;
    LOGGER.error( "Hi", " here!" ) ;
    LOGGER.error( throwableForLogging, "Hi", " here!" ) ;
    LOGGER.error( throwableForLogging ) ;
    
  }

// =======
// Fixture
// =======

  private static final Logger LOGGER = LoggerFactory.getLogger( Slf4jLoggingTest.class );

  private final Throwable throwableForLogging = new Exception( "Just for logging" ) ;
  
}

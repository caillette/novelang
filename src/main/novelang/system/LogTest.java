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
package novelang.system;

import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Kind of demo for {@link LogFactory} and {@link Log}.
 *
 * @author Laurent Caillette
 */
public class LogTest {

  @Test
  public void logSomeStuffWithLog() {
    final Log log = LogFactory.getLog( "Wrapper-around-slf4j" ) ;
    log.error( "error" ) ;
    log.error( "error %s %s", 1, 2 ) ;
    log.error( "error", new Error() ) ;

  }

  @Test
  public void logSomeStuffWithLogger() {
    final Logger logger = LoggerFactory.getLogger( "True-slf4j" ) ;
    logger.error( "error", new Error() ) ;
  }
}

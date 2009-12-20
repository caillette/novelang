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

package novelang.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Laurent Caillette
 */
public class ProblemTest {

  private static final String FILE_LOCATION = "file_location" ;
  private static final LocationFactory LOCATION_FACTORY = new LocationFactory() {
    public Location createLocation( final int line, final int column ) {
      return new Location( FILE_LOCATION, line, column ) ;
    }
  } ;

  @Test
  public void parseLocationInformationOk() {
    final Problem problem = Problem.createProblem( LOCATION_FACTORY, "line 12:34 blah blah blah" ) ;
    Assert.assertEquals( 12, problem.getLocation().getLine() ) ;
    Assert.assertEquals( 34, problem.getLocation().getColumn() ) ;
    Assert.assertEquals( FILE_LOCATION, problem.getLocation().getFileName() ) ;
    Assert.assertEquals( "blah blah blah", problem.getMessage() ) ;
  }

  @Test
  public void parseLocationInformationBad() {
    final Problem problem = Problem.createProblem( LOCATION_FACTORY, "blah blah blah" ) ;
    Assert.assertEquals( -1, problem.getLocation().getLine() ) ;
    Assert.assertEquals( -1, problem.getLocation().getColumn() ) ;
    Assert.assertEquals( FILE_LOCATION, problem.getLocation().getFileName() ) ;
    Assert.assertEquals( "blah blah blah", problem.getMessage() ) ;
  }

}

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

package org.novelang.common;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author Laurent Caillette
 */
public class ProblemTest {

  private static final String FILE_LOCATION = "file_location" ;
  private static final LocationFactory LOCATION_FACTORY = new LocationFactory() {
    @Override
    public Location createLocation( final int line, final int column ) {
      return new Location( FILE_LOCATION, line, column ) ;
    }
    @Override
    public Location createLocation() {
      return new Location( FILE_LOCATION ) ;
    }
  } ;

  @Test
  public void parseLocationInformationOk() {
    final Problem problem = Problem.createProblem( LOCATION_FACTORY, "line 12:34 blah blah blah" ) ;
    assertEquals( 12, problem.getLocation().getLine() ) ;
    assertEquals( 34, problem.getLocation().getColumn() ) ;
    assertEquals( FILE_LOCATION, problem.getLocation().getFileName() ) ;
    assertEquals( "blah blah blah", problem.getMessage() ) ;
  }

  @Test
  public void parseLocationInformationBad() {
    final Problem problem = Problem.createProblem( LOCATION_FACTORY, "blah blah blah" ) ;
    assertEquals( -1, problem.getLocation().getLine() ) ;
    assertEquals( -1, problem.getLocation().getColumn() ) ;
    assertEquals( FILE_LOCATION, problem.getLocation().getFileName() ) ;
    assertEquals( "blah blah blah", problem.getMessage() ) ;
  }


  @Test
  public void problemEquals() {
    assertEquals( Problem.createProblem( "foo" ), Problem.createProblem( "foo" ) );
    assertEquals(
        Problem.createProblem( "foo", LOCATION_FACTORY, 1, 1 ),
        Problem.createProblem( "foo", LOCATION_FACTORY, 1, 1 )
    ) ;
    assertFalse( Problem.createProblem( "foo" ).equals( Problem.createProblem( "bar" ) ) ) ;
  }


// =======
// Fixture
// =======




}

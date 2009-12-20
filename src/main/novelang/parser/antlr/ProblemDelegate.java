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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;
import novelang.common.Location;
import novelang.common.LocationFactory;
import novelang.common.Problem;

/**
 * Just hooks into ANTLR's error reporting.
 */
public class ProblemDelegate {

  protected final LocationFactory locationFactory ;
  protected final List< Problem > problems = Lists.newArrayList() ;


  public ProblemDelegate() {
    this.locationFactory = new LocationFactory() {
      public Location createLocation( final int line, final int column ) {
        return new Location( "<debug>", line, column ) ;
      }
    } ;    
  }

  public ProblemDelegate( final LocationFactory locationFactory ) {
    this.locationFactory = locationFactory;
  }

  public void report( final String antlrMessage ) {
    problems.add( Problem.createProblem( locationFactory, antlrMessage ) ) ;
  }

  public void report( final RecognitionException exception ) {
    problems.add( Problem.createProblem( locationFactory, exception ) ) ;
  }

  public void report( final String message, final int line, final int column ) {
    problems.add( Problem.createProblem( message, locationFactory, line, column ) ) ;
  }

  protected void report( final Iterable< Problem > problems ) {
    Iterables.addAll( this.problems, problems ) ;
  }

  public Iterable< Problem > getProblems() {
    return ImmutableList.copyOf( problems ) ;
  }

  public LocationFactory getLocationFactory() {
    return locationFactory;
  }


}

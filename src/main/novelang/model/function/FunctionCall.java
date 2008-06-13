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
package novelang.model.function;

import novelang.model.common.Problem;
import novelang.model.common.Location;
import novelang.model.common.Treepath;
import novelang.model.book.Environment;
import com.google.common.collect.Iterables;

/**
 * @author Laurent Caillette
 */
public abstract class FunctionCall {

  private final Location location ;

  public FunctionCall( Location location ) {
    this.location = location ;
  }

  public Location getLocation() {
    return location ;
  }

  /**
   *
   * @param environment
   * @param book
   * @return a non-null {@link Result} instance.
   */
  public abstract Result evaluate( Environment environment, Treepath book ) ;

  public static class Result {

    private final Treepath book ;
    private final Iterable< Problem > problems ;

    private static final Iterable< Problem > NO_PROBLEM = Iterables.emptyIterable() ;

    public Result(
        Treepath book,
        Iterable< Problem > problems
    ) {
      this.book = book ;
      this.problems = null == problems ? NO_PROBLEM : problems ;
    }

    /**
     * May be null.
     */
    public Treepath getBook() {
      return book ;
    }

    /**
     * Never null.
     */
    public Iterable< Problem > getProblems() {
      return problems ;
    }
  }

}

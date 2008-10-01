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
package novelang.book.function;

import novelang.common.Problem;
import novelang.common.Location;
import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import novelang.book.Environment;
import com.google.common.collect.Iterables;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Represents an instance of a {@link FunctionDefinition} inside a Book file,
 * that does things with {@link Environment}, Book's {@code Treepath} and may generate
 * problems.
 *
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
  public abstract Result evaluate( Environment environment, Treepath< SyntacticTree > book ) ;

  public static final FunctionCall DO_NOTHING = new FunctionCall( null ) {
    public Result evaluate( Environment environment, Treepath< SyntacticTree > book ) {
      return new Result( environment, book, null ) ;
    }
  } ;

  public static class Result {

    private final Environment environment ;
    private final Treepath< SyntacticTree > book ;
    private final Iterable< Problem > problems ;

    private static final Iterable< Problem > NO_PROBLEM = Iterables.emptyIterable() ;

    public Result(
        Environment environment,
        Treepath< SyntacticTree > book,
        Iterable< Problem > problems
    ) {
      this.book = book ;
      this.problems = null == problems ? NO_PROBLEM : problems ;
      this.environment = Preconditions.checkNotNull( environment ) ;
    }

    /**
     * May be null.
     */
    public Treepath<SyntacticTree> getBook() {
      return book ;
    }

    /**
     * Never null.
     */
    public Iterable< Problem > getProblems() {
      return problems ;
    }

    /**
     * Never null.
     */
    public Environment getEnvironment() {
      return environment;
    }
  }

}

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
package novelang.model.function.builtin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Iterables;
import novelang.model.book.Environment;
import novelang.model.common.Location;
import static novelang.model.common.NodeKind.URL;
import static novelang.model.common.NodeKind.VALUED_ARGUMENT_PRIMARY;
import novelang.model.common.Problem;
import novelang.model.common.Tree;
import novelang.model.common.Treepath;
import novelang.model.function.FunctionCall;
import novelang.model.function.FunctionDefinition;
import novelang.model.function.IllegalFunctionCallException;

/**
 * @author Laurent Caillette
 */
public class FunctionInsert implements FunctionDefinition {

  private static final Logger LOGGER = LoggerFactory.getLogger( FunctionInsert.class ) ;

  public String getName() {
    return "insert" ;
  }

  public FunctionCall instantiate(
      Location location,
      Tree functionCall
  ) throws IllegalFunctionCallException {

    verify( "No primary argument", 2, functionCall.getChildCount() ) ;
    final Tree primaryArgument = functionCall.getChildAt( 1 ) ;
    verify( "No value for primary argument",
        VALUED_ARGUMENT_PRIMARY.name(), primaryArgument.getText() ) ;
    verify( "No value for primary argument", 1, primaryArgument.getChildCount() ) ;
    final Tree url = primaryArgument.getChildAt( 0 ) ;
    verify( URL.name(), url.getText() ) ;
    final String urlAsString = url.getChildAt( 0 ).getText() ;

    LOGGER.debug( "Parsed function '{}' url='{}'", getName(), urlAsString ) ;

    return new FunctionCall( location ) {

      public Result evaluate( Environment environment, Treepath book ) {
        final Iterable< Problem > empty = Iterables.emptyIterable() ;
        return new Result( book, empty ) ;
      }
    };
  }


  private static final< T > void verify( String message, T expected, T actual )
      throws IllegalFunctionCallException
  {
    try {
      verify( expected, actual ) ;
    } catch( IllegalFunctionCallException e ) {
      throw new IllegalFunctionCallException( message + "\n" + e.getMessage() ) ;
    }
  }

  private static final< T > void verify( T expected, T actual )
      throws IllegalFunctionCallException
  {
    if( null == expected ) {
      if( null == actual ) {
        return ;
      } else {
        throw new IllegalFunctionCallException( buildErrorMessage( expected, actual ) ) ;
      }
    } else if( expected.equals( actual ) ) {
      return ;
    } else {
      throw new IllegalFunctionCallException( buildErrorMessage( expected, actual ) ) ;
    }
  }

  private static final String buildErrorMessage( Object expected, Object actual ) {
    return
        "\nExpected:\n  " + expected +
        "\nActual:\n  " + actual
    ;
  }

}

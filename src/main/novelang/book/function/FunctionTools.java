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

/**
 * Utility class.
 *
 * @author Laurent Caillette
 */
public class FunctionTools {
  public static final< T > void verify( String message, T expected, T actual )
      throws IllegalFunctionCallException
  {
    try {
      verify( expected, actual ) ;
    } catch( IllegalFunctionCallException e ) {
      throw new IllegalFunctionCallException( message + "\n" + e.getMessage() ) ;
    }
  }

  public static final< T > void verify( T expected, T actual )
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

  public static final String buildErrorMessage( Object expected, Object actual ) {
    return
        "\nExpected:\n  " + expected +
        "\nActual:\n  " + actual
    ;
  }
}
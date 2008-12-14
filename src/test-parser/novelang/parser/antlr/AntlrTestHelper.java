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

package novelang.parser.antlr;

import junit.framework.AssertionFailedError;
import novelang.common.Problem;


/**
 * Lots of {@code ParserMethod} instances and some utility methods.
 *
 * @author Laurent Caillette
 */
public class AntlrTestHelper {

  static final String BREAK = "\n" ;


  /*package*/ static void checkSanity( AbstractDelegatingParser parser ) {
    if( parser.hasProblem() ) {
      throw new AssertionFailedError(
          "Parser has problems. " + createProblemList( parser.getProblems() ) ) ;
    }
  }/*package*/ static DelegatingPartParser createPartParser( String text ) {
    return ( DelegatingPartParser )
        new DefaultPartParserFactory().createParser( TreeFixture.LOCATION_FACTORY, text ) ;
  }/*package*/ static DelegatingBookParser createBookParser( String text ) {
    return ( DelegatingBookParser )
        new DefaultBookParserFactory().createParser( TreeFixture.LOCATION_FACTORY, text ) ;
  }

  public static String createProblemList( Iterable<Problem> problems ) {
    final StringBuffer buffer = new StringBuffer( "Problems:" ) ;
    for( final Problem problem : problems ) {
      buffer.append( "\n    " ) ;
      buffer.append( problem.getMessage() ) ;
      buffer.append( "  at  " ) ;
      buffer.append( problem.getLocation() ) ;
    }
    return buffer.toString() ;
  }
}

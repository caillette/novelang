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

import org.antlr.runtime.RecognitionException;
import org.junit.Assert;
import junit.framework.AssertionFailedError;
import novelang.model.common.Problem;
import novelang.model.common.Tree;

/**
 * @author Laurent Caillette
 */
public class AntlrTestHelper {
  static final String BREAK = "\n" ;

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

  static void title( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = title( text ) ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  static Tree title( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().title().getTree() ;
    checkSanity( parser );
    return tree;
  }

  static void headerIdentifier( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = headerIdentifier( text ) ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  static Tree headerIdentifier( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().headerIdentifier().getTree() ;
    checkSanity( parser );
    return tree;
  }

  static void word( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = word( text ) ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  static Tree word( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().word().getTree() ;
    checkSanity( parser );
    return tree;
  }

  static void wordFails( String s ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( s ) ;
    parser.getAntlrParser().word() ;
    final String readableProblemList = createProblemList( parser.getProblems() ) ;
    final boolean parserHasProblem = parser.hasProblem();
    Assert.assertTrue( readableProblemList, parserHasProblem ) ;
  }

  static void paragraph( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = paragraph( text ) ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  static Tree paragraph( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().paragraph().getTree() ;
    checkSanity( parser );
    return tree;
  }

  static void paragraphFails( String s ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( s ) ;
    parser.getAntlrParser().paragraph() ;
    final String readableProblemList = createProblemList( parser.getProblems() ) ;
    final boolean parserHasProblem = parser.hasProblem() ;
    Assert.assertTrue( readableProblemList, parserHasProblem ) ;
  }

  static void section( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = section( text ) ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  static Tree section( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().section().getTree() ;
    checkSanity( parser );
    return tree;
  }

  static void litteral( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = litteral( text ) ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  static Tree litteral( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().litteral().getTree() ;
    checkSanity( parser );
    return tree;
  }

  static void chapter( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = chapter( text ) ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  static Tree chapter( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().chapter().getTree() ;
    checkSanity( parser );
    return tree;
  }

  static void part( String text, Tree expectedTree )
      throws RecognitionException
  {
    final Tree actualTree = part( text ); ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  static Tree part( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().part().getTree() ;
    checkSanity( parser );
    return tree;
  }

  static void url( String text, Tree expectedTree )
      throws RecognitionException
  {
    final Tree actualTree = url( text ) ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  static Tree url( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().url().getTree() ;
    checkSanity( parser );
    return tree;
  }

  static void functionCall( String text, Tree expectedTree )
      throws RecognitionException
  {
    final Tree actualTree = functionCall( text ) ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  static Tree functionCall( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().functionCall().getTree() ;
    checkSanity( parser ) ;
    return tree;
  }

  static void valuedArgument( String text, Tree expectedTree )
      throws RecognitionException
  {
    final Tree actualTree = valuedArgument( text ); ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  static Tree valuedArgument( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().valuedArgument().getTree() ;
    checkSanity( parser ) ;
    return tree;
  }



  static void checkSanity( DelegatingPartParser parser ) {
    if( parser.hasProblem() ) {
      throw new AssertionFailedError(
          "Parser has problems. " + createProblemList( parser.getProblems() ) ) ;
    }
  }

  static DelegatingPartParser createPartParser( String text ) {
    return ( DelegatingPartParser )
        new DefaultPartParserFactory().createParser( TreeFixture.LOCATION_FACTORY, text );
  }
}

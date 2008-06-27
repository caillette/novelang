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
import novelang.common.Problem;
import novelang.common.SyntacticTree;

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

  static void title( String text, SyntacticTree expectedTree ) throws RecognitionException {
    final SyntacticTree actualTree = title( text ) ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  static SyntacticTree title( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final SyntacticTree tree = ( SyntacticTree ) parser.getAntlrParser().title().getTree() ;
    checkSanity( parser );
    return tree;
  }

  static void headerIdentifier( String text, SyntacticTree expectedTree ) throws RecognitionException {
    final SyntacticTree actualTree = headerIdentifier( text ) ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  static SyntacticTree headerIdentifier( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final SyntacticTree tree = ( SyntacticTree ) parser.getAntlrParser().headerIdentifier().getTree() ;
    checkSanity( parser );
    return tree;
  }

  static void word( String text, SyntacticTree expectedTree ) throws RecognitionException {
    final SyntacticTree actualTree = word( text ) ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  static SyntacticTree word( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final SyntacticTree tree = ( SyntacticTree ) parser.getAntlrParser().word().getTree() ;
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

  static void paragraph( String text, SyntacticTree expectedTree ) throws RecognitionException {
    final SyntacticTree actualTree = paragraph( text ) ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  static SyntacticTree paragraph( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final SyntacticTree tree = ( SyntacticTree ) parser.getAntlrParser().paragraph().getTree() ;
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

  static void section( String text, SyntacticTree expectedTree ) throws RecognitionException {
    final SyntacticTree actualTree = section( text ) ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  static SyntacticTree section( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final SyntacticTree tree = ( SyntacticTree ) parser.getAntlrParser().section().getTree() ;
    checkSanity( parser );
    return tree;
  }

  static void litteral( String text, SyntacticTree expectedTree ) throws RecognitionException {
    final SyntacticTree actualTree = litteral( text ) ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  static SyntacticTree litteral( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final SyntacticTree tree = ( SyntacticTree ) parser.getAntlrParser().litteral().getTree() ;
    checkSanity( parser );
    return tree;
  }

  static void chapter( String text, SyntacticTree expectedTree ) throws RecognitionException {
    final SyntacticTree actualTree = chapter( text ) ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  static SyntacticTree chapter( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final SyntacticTree tree = ( SyntacticTree ) parser.getAntlrParser().chapter().getTree() ;
    checkSanity( parser );
    return tree;
  }

  static void part( String text, SyntacticTree expectedTree )
      throws RecognitionException
  {
    final SyntacticTree actualTree = part( text ); ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  static SyntacticTree part( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final SyntacticTree tree = ( SyntacticTree ) parser.getAntlrParser().part().getTree() ;
    checkSanity( parser );
    return tree;
  }

  static void url( String text, SyntacticTree expectedTree )
      throws RecognitionException
  {
    final SyntacticTree actualTree = url( text ) ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  static SyntacticTree url( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final SyntacticTree tree = ( SyntacticTree ) parser.getAntlrParser().url().getTree() ;
    checkSanity( parser );
    return tree;
  }

  static void functionCall( String text, SyntacticTree expectedTree )
      throws RecognitionException
  {
    final SyntacticTree actualTree = functionCall( text ) ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  static SyntacticTree functionCall( String text ) throws RecognitionException {
    final DelegatingBookParser parser = createBookParser( text ) ;
    final SyntacticTree tree = ( SyntacticTree ) parser.getAntlrParser().functionCall().getTree() ;
    checkSanity( parser ) ;
    return tree;
  }

  static void ancillaryArgument( String text, SyntacticTree expectedTree )
      throws RecognitionException
  {
    final SyntacticTree actualTree = ancillaryArgument( text ); ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  static SyntacticTree ancillaryArgument( String text ) throws RecognitionException {
    final DelegatingBookParser parser = createBookParser( text ) ;
    final SyntacticTree tree = ( SyntacticTree )
        parser.getAntlrParser().ancillaryArgument().getTree() ;
    checkSanity( parser ) ;
    return tree;
  }

  public static void book( String text, SyntacticTree expectedTree )
      throws RecognitionException
  {
    final SyntacticTree actualTree = book( text ) ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
  }

  public static SyntacticTree book( String text ) throws RecognitionException {
    final DelegatingBookParser parser = createBookParser( text ) ;
    final SyntacticTree tree = ( SyntacticTree ) parser.getAntlrParser().book().getTree() ;
    checkSanity( parser ) ;
    return tree;
  }



  static void checkSanity( AbstractDelegatingParser parser ) {
    if( parser.hasProblem() ) {
      throw new AssertionFailedError(
          "Parser has problems. " + createProblemList( parser.getProblems() ) ) ;
    }
  }

  static DelegatingPartParser createPartParser( String text ) {
    return ( DelegatingPartParser )
        new DefaultPartParserFactory().createParser( TreeFixture.LOCATION_FACTORY, text );
  }

  static DelegatingBookParser createBookParser( String text ) {
    return ( DelegatingBookParser )
        new DefaultBookParserFactory().createParser( TreeFixture.LOCATION_FACTORY, text );
  }
}

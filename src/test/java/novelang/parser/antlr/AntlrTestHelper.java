/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.parser.antlr;

import org.antlr.runtime.RecognitionException;
import org.junit.Assert;
import novelang.model.common.Problem;
import novelang.model.common.Tree;
import junit.framework.AssertionFailedError;

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
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
  }

  static Tree title( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().title().getTree() ;
    checkSanity( parser );
    return tree;
  }

  static void identifier( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = identifier( text ) ;
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
  }

  static Tree identifier( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().identifier().getTree() ;
    checkSanity( parser );
    return tree;
  }

  static void word( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = word( text ) ;
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
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
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
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
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
  }

  static Tree section( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().section().getTree() ;
    checkSanity( parser );
    return tree;
  }

  static void litteral( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = litteral( text ) ;
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
  }

  static Tree litteral( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().litteral().getTree() ;
    checkSanity( parser );
    return tree;
  }

  static void chapter( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = chapter( text ) ;
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
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
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
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
    final Tree actualTree = url( text ); ;
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
  }

  static Tree url( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().url().getTree() ;
    checkSanity( parser );
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
        new DefaultPartParserFactory().createParser( TreeHelper.LOCATION_FACTORY, text );
  }
}

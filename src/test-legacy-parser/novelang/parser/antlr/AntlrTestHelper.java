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
import novelang.common.Problem;
import novelang.common.SyntacticTree;

/**
 * Here are tons of delightfully stupid code but at least we don't have to debug
 * reflexion stuff.
 *
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
  
  
  private static final ParserMethod TITLE = new ParserMethod( "title" ) ;
  private static final ParserMethod HEADER_IDENTIFIER = new ParserMethod( "headerIdentifier" ) ;
  private static final ParserMethod WORD = new ParserMethod( "word" ) ;
  private static final ParserMethod PARAGRAPH = new ParserMethod( "paragraph" ) ;
  private static final ParserMethod SECTION = new ParserMethod( "section" ) ;
  private static final ParserMethod LITERAL = new ParserMethod( "literal" ) ;
  private static final ParserMethod SOFT_INLINE_LITERAL = new ParserMethod( "softInlineLiteral" ) ;
  private static final ParserMethod HARD_INLINE_LITERAL = new ParserMethod( "hardInlineLiteral" ) ;
  private static final ParserMethod CHAPTER = new ParserMethod( "chapter" ) ;
  private static final ParserMethod PART = new ParserMethod( "part" ) ;
  private static final ParserMethod URL = new ParserMethod( "url" ) ;
  private static final ParserMethod FUNCTION_CALL = new ParserMethod( "functionCall" ) ;
  private static final ParserMethod ANCILLARY_ARGUMENT = new ParserMethod( "ancillaryArgument" ) ;
  private static final ParserMethod VALUED_ARGUMENT_ASSIGNMENT = new ParserMethod( "assignmentArgument" ) ;
  private static final ParserMethod BOOK = new ParserMethod( "book" ) ;
  

  static void title( String text, SyntacticTree expectedTree ) throws RecognitionException {
    TITLE.checkTree( text, expectedTree ) ;
  }

  static SyntacticTree title( String text ) throws RecognitionException {
    return TITLE.createTree( text ) ;
  }

  static void headerIdentifier( String text, SyntacticTree expectedTree ) throws RecognitionException {
    HEADER_IDENTIFIER.checkTree( text, expectedTree ) ;
  }

  static SyntacticTree headerIdentifier( String text ) throws RecognitionException {
    return HEADER_IDENTIFIER.createTree( text ) ;
  }

  static void word( String text, SyntacticTree expectedTree ) throws RecognitionException {
    WORD.checkTree( text, expectedTree ) ;
  }

  static SyntacticTree word( String text ) throws RecognitionException {
    return WORD.createTree( text ) ;
  }

  static void wordFails( String s ) throws RecognitionException {
    final DelegatingPartParser parser = GenericAntlrTestHelper.createPartParser( s ) ;
    parser.getAntlrParser().word() ;
    final String readableProblemList = createProblemList( parser.getProblems() ) ;
    final boolean parserHasProblem = parser.hasProblem();
    Assert.assertTrue( readableProblemList, parserHasProblem ) ;
  }

  static void paragraph( String text, SyntacticTree expectedTree ) throws RecognitionException {
    PARAGRAPH.checkTree( text, expectedTree ) ;
  }

  static SyntacticTree paragraph( String text ) throws RecognitionException {
    return PARAGRAPH.createTree( text ) ;
  }

  static void paragraphFails( String s ) throws RecognitionException {
    final DelegatingPartParser parser = GenericAntlrTestHelper.createPartParser( s ) ;
    parser.getAntlrParser().paragraph() ;
    final String readableProblemList = createProblemList( parser.getProblems() ) ;
    final boolean parserHasProblem = parser.hasProblem() ;
    Assert.assertTrue( readableProblemList, parserHasProblem ) ;
  }

  static void section( String text, SyntacticTree expectedTree ) throws RecognitionException {
    SECTION.checkTree( text, expectedTree ) ;
  }

  static SyntacticTree section( String text ) throws RecognitionException {
    return SECTION.createTree( text ) ;
  }

  static void literal( String text, SyntacticTree expectedTree ) throws RecognitionException {
    LITERAL.checkTree( text, expectedTree ) ;
  }

  static SyntacticTree literal( String text ) throws RecognitionException {
    return LITERAL.createTree( text ) ;
  }

  static void softInlineLiteral( String text, SyntacticTree expectedTree )
      throws RecognitionException
  {
    SOFT_INLINE_LITERAL.checkTree( text, expectedTree ) ;
  }

  static SyntacticTree softInlineLiteral( String text ) throws RecognitionException {
    return SOFT_INLINE_LITERAL.createTree( text ) ;
  }

  static void hardInlineLiteral( String text, SyntacticTree expectedTree )
      throws RecognitionException
  {
    HARD_INLINE_LITERAL.checkTree( text, expectedTree ) ;
  }

  static SyntacticTree hardInlineLiteral( String text ) throws RecognitionException {
    return HARD_INLINE_LITERAL.createTree( text ) ;
  }

  static void chapter( String text, SyntacticTree expectedTree ) throws RecognitionException {
    CHAPTER.checkTree( text, expectedTree ) ;
  }

  static SyntacticTree chapter( String text ) throws RecognitionException {
    return CHAPTER.createTree( text ) ;
  }

  static void part( String text, SyntacticTree expectedTree )
      throws RecognitionException
  {
    PART.checkTree( text, expectedTree ) ;
  }

  static SyntacticTree part( String text ) throws RecognitionException {
    return PART.createTree( text ) ;
  }

  static void url( String text, SyntacticTree expectedTree )
      throws RecognitionException
  {
    URL.checkTree( text, expectedTree ) ;
  }

  static SyntacticTree url( String text ) throws RecognitionException {
    return URL.createTree( text ) ;
  }

  static void functionCall( String text, SyntacticTree expectedTree )
      throws RecognitionException
  {
    FUNCTION_CALL.checkTree( text, expectedTree ) ;
  }

  static SyntacticTree functionCall( String text ) throws RecognitionException {
    return FUNCTION_CALL.createTree( text ) ;
  }

  static void ancillaryArgument( String text, SyntacticTree expectedTree )
      throws RecognitionException
  {
    ANCILLARY_ARGUMENT.checkTree( text, expectedTree ) ;
  }

  static SyntacticTree ancillaryArgument( String text ) throws RecognitionException {
    return ANCILLARY_ARGUMENT.createTree( text ) ;
  }

  static void valuedArgumentAssignment( String text, SyntacticTree expectedTree )
      throws RecognitionException
  {
    VALUED_ARGUMENT_ASSIGNMENT.checkTree( text, expectedTree ) ;
  }

  static SyntacticTree valuedArgumentAssignment( String text ) throws RecognitionException {
    return VALUED_ARGUMENT_ASSIGNMENT.createTree( text ) ;
  }

  public static void book( String text, SyntacticTree expectedTree )
      throws RecognitionException
  {
    BOOK.checkTree( text, expectedTree ) ;
  }

  public static SyntacticTree book( String text ) throws RecognitionException {
    return BOOK.createTree( text ) ;
  }


}

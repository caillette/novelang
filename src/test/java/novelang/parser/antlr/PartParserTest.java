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

import static novelang.parser.antlr.AntlrTestHelper.BREAK;
import org.antlr.runtime.RecognitionException;
import org.junit.Assert;
import org.junit.Test;
import junit.framework.AssertionFailedError;
import static novelang.model.common.NodeKind.*;
import novelang.model.common.Tree;
import static novelang.parser.antlr.TreeHelper.tree;

/**
 * GUnit sucks as it has completely obscure failures and stupid reports,
 * but I took some ideas from it anyways.
 *
 * @author Laurent Caillette
 */
public class PartParserTest {

  @Test
  public void title() throws RecognitionException {
    title( "'some title", tree(
        TITLE,
        tree( WORD, "some" ),
        tree( WORD, "title" )
    ) ) ;

    title( "'some title !", tree(
        TITLE,
        tree(WORD, "some"),
        tree(WORD, "title"),
        tree( PUNCTUATION_SIGN, SIGN_EXCLAMATIONMARK )
    ) ) ;

    title( "'some (title) !", tree(
        TITLE,
        tree( WORD, "some" ),
        tree( PARENTHESIS, tree( WORD, "title" ) ),
        tree( PUNCTUATION_SIGN, SIGN_EXCLAMATIONMARK )
    ) ) ;


  }

  @Test
  public void identifier() throws RecognitionException {
    identifier( "myIdentifier", tree(
        IDENTIFIER,
        tree( WORD, "myIdentifier" )
    ) ) ;
  }

  @Test
  public void word() throws RecognitionException {

    word( "w",       tree( WORD, "w" ) ) ;
    word( "Www",     tree( WORD, "Www" ) ) ;
    word( "123",     tree( WORD, "123" ) ) ;
    word( "123-456", tree( WORD, "123-456" ) ) ;

    wordFails( "'w" ) ;
    wordFails( "'w-" ) ;
//    wordFails( "w--w" ) ;

  }

  @Test
  public void paragraph() throws RecognitionException {

    paragraph( "--- w0", tree(
        PARAGRAPH_SPEECH,
        tree( WORD, "w0" )
    ) ) ;

    paragraph( "--| w0", tree(
        PARAGRAPH_SPEECH_ESCAPED,
        tree( WORD, "w0" )
    ) ) ;

    paragraph( "--+ w0", tree(
        PARAGRAPH_SPEECH_CONTINUED,
        tree( WORD, "w0" )
    ) ) ;

    paragraph( "--- w0 w1 :: w2", tree(
        PARAGRAPH_SPEECH,
        tree( LOCUTOR, tree( WORD, "w0" ), tree( WORD, "w1" ) ),
        tree( WORD, "w2" )
    ) ) ;



    // Following tests are for paragraphBody rule. But we need to rely on a rule
    // returning a sole tree as GUnit doesn't support exepecting more than one.


    paragraph( "w0,", tree(
        PARAGRAPH_PLAIN,
        tree( WORD, "w0" ),
        tree( PUNCTUATION_SIGN, SIGN_COMMA )
    ) ) ;

    paragraph( "w0;", tree(
        PARAGRAPH_PLAIN,
        tree( WORD, "w0" ),
        tree( PUNCTUATION_SIGN, SIGN_SEMICOLON )
    ) ) ;

    paragraph( "w0.", tree(
        PARAGRAPH_PLAIN,
        tree( WORD, "w0" ),
        tree( PUNCTUATION_SIGN, SIGN_FULLSTOP )
    ) ) ;

    paragraph( "w0?", tree(
        PARAGRAPH_PLAIN,
        tree( WORD, "w0" ),
        tree( PUNCTUATION_SIGN, SIGN_QUESTIONMARK )
    ) ) ;

    paragraph( "w0!", tree(
        PARAGRAPH_PLAIN,
        tree( WORD, "w0" ),
        tree( PUNCTUATION_SIGN, SIGN_EXCLAMATIONMARK )
    ) ) ;

    paragraph( "w0:", tree(
        PARAGRAPH_PLAIN,
        tree( WORD, "w0" ),
        tree( PUNCTUATION_SIGN, SIGN_COLON )
    ) ) ;

    paragraph( "w0...", tree(
        PARAGRAPH_PLAIN,
        tree( WORD, "w0" ),
        tree( PUNCTUATION_SIGN, SIGN_ELLIPSIS )
    ) ) ;

    paragraph( "w0 w1'/w2/.", tree(
        PARAGRAPH_PLAIN,
        tree( WORD, "w0" ),
        tree( WORD, "w1'" ),
        tree( EMPHASIS, tree( WORD, "w2" ) ),
        tree( PUNCTUATION_SIGN, SIGN_FULLSTOP )
    ) ) ;


/*
"w0.." FAIL
"w0,," FAIL
"w0??" FAIL
"w0;;" FAIL
*/

  }

  @Test
  public void section1() throws RecognitionException {

    // Vanilla: one section with one paragraph.
    section(
        "=== s00" + BREAK +
        BREAK +
        "p10 w11.",
        tree(
            SECTION,
            tree( IDENTIFIER, tree( WORD, "s00") ),
            tree( PARAGRAPH_PLAIN, tree( WORD, "p10" ), tree( WORD, "w11" ),
            tree( PUNCTUATION_SIGN, SIGN_FULLSTOP )
     ) ) ) ;


    // Vanilla: anonymous section.
    section(
        "===" + BREAK +
        BREAK +
        "p0",
        tree(
            SECTION,
            tree( PARAGRAPH_PLAIN, tree( WORD, "p0" )
        )
     ) ) ;


    // Section with several multiline paragraphs.
    section(
        "===" + BREAK +
        BREAK +
        "p0 w01" + BREAK +
        "w02" + BREAK +
        BREAK +
        "p1 w11" + BREAK +
        "w12",
        tree(
            SECTION,
            tree( PARAGRAPH_PLAIN, tree( WORD, "p0" ), tree( WORD, "w01" ), tree( WORD, "w02" ) ),
            tree( PARAGRAPH_PLAIN, tree( WORD, "p1" ), tree( WORD, "w11" ), tree( WORD, "w12" )
     ) ) ) ;

  }

  @Test
  public void section2() throws RecognitionException {

    // Sections with trailing whitespaces everywhere.
    section(
        "===  " + BREAK +
        "  " + BREAK +
        " p0 w01  " + BREAK +
        "w02 " + BREAK +
        "  " + BREAK +
        "p1 w11  " + BREAK +
        " w12 ",
        tree(
            SECTION,
            tree( PARAGRAPH_PLAIN, tree( WORD, "p0" ), tree( WORD, "w01" ), tree( WORD, "w02" ) ),
            tree( PARAGRAPH_PLAIN, tree( WORD, "p1" ), tree( WORD, "w11" ), tree( WORD, "w12" )
     ) ) ) ;


  }

  @Test
  public void section3() throws RecognitionException {
    section(
      "===" + BREAK +
      BREAK +
      "<<< w0 w1" + BREAK +
      ">>>",
      tree( SECTION,
          tree(
              BLOCKQUOTE,
              tree( PARAGRAPH_PLAIN, tree( WORD, "w0" ), tree( WORD, "w1" ) )
          )
      )
    ) ;

    section(
      "===" + BREAK +
      BREAK +
      "<<< w0 w1" + BREAK +
      BREAK +
      "w2" + BREAK +
      ">>>",
      tree( SECTION,
          tree(
              BLOCKQUOTE,
              tree( PARAGRAPH_PLAIN, tree( WORD, "w0" ), tree( WORD, "w1" ) ),
              tree( PARAGRAPH_PLAIN, tree( WORD, "w2" ) )
          )
      )
    ) ;


  }


  @Test
  public void paragraphBody1() throws RecognitionException {
    
    paragraphBody( "/w0/", tree(
        EMPHASIS, tree( WORD, "w0" )
    ) ) ;

    paragraphBody( "(w0)", tree(
        PARENTHESIS, tree( WORD, "w0" )
    ) ) ;

    paragraphBody( "\"w0\"", tree(
        QUOTE, tree( WORD, "w0" )
    ) ) ;

    paragraphBody( "-- w0 --", tree(
        INTERPOLATEDCLAUSE, tree( WORD, "w0" )
    ) ) ;

    paragraphBody( "-- w0 -_", tree(
        INTERPOLATEDCLAUSE_SILENTEND, tree( WORD, "w0" )
    ) ) ;

  }

  @Test
  public void paragraphBody2() throws RecognitionException {

    paragraphBody( "\"w00\" w01 w02 \" w03 w04 ! \"." ) ;
    paragraphBody( "w10 \"w11\" \"w12\", \"w13\"" ) ;
    paragraphBody( "\"w20 w21... w22\" !" ) ;

    paragraphBody( "\"p00 (w01) w02.\" w04 (w05 \"w06 (w07)\".)." ) ;
    paragraphBody( "p10 -- w11 w12 --." ) ;
    paragraphBody( "p20 -- w21 w22 -_." ) ;

    paragraphBody( "\"w0 (w1)\"") ;
    paragraphBody( "\"w0 (w1 /w2/)\"") ;
    paragraphBody( "\"w0 (w1 /w2 (w3)/)\"") ;
    paragraphBody( "\"(w0 -- w1 (w2) --)\"") ;
    paragraphBody( "/w0 (w1)/.") ;
    paragraphBody( "/w0 (w1, \"w2\")/ ?") ;
    paragraphBody( "(w0 !)") ;
    paragraphBody( "(w0 \"w1\"...)") ;
    paragraphBody( "(w0 \"w1 /w2/\") : w3 !") ;

    paragraphBody(
        "/w1" + BREAK +
        "(w2 " + BREAK +
        "-- w3  " + BREAK +
        "\"w4 " + BREAK +
        "w5\"--)/."
    ) ;
  }

  @Test
  public void paragraphBody3() throws RecognitionException {

    // Quote depth limited to 1.
    paragraphBodyFails( "(w0 \"w1 (w2 \"w3\")\")" ) ;

    // Emphasis depth limited to 1.
    paragraphBodyFails( "(w0 /w1 (w2 /w3/)/)" ) ;

    // Parenthesis depth limited to 2.
    paragraphBodyFails( "(w0 /w1 (w2 \"w3 (w4)\")/)" ) ;

    // Interpolated clauses depth limited to 1.
    paragraphBodyFails( "(w0 -- w1 (w2 -- w3 -- ) --)" ) ;

  }

  @Test public void part() throws RecognitionException {


    // Chapter delimiter.
    part(
        "*** c0" + BREAK +
        BREAK +
        "=== s0" + BREAK +
        BREAK +
        "p0",
        tree(
          PART,
          tree(
              CHAPTER,
              tree( IDENTIFIER, tree(WORD, "c0" ) ),
              tree(
                  SECTION,
                  tree( IDENTIFIER, tree( WORD, "s0" ) ),
                  tree( PARAGRAPH_PLAIN, tree( WORD, "p0" ) )
              )
          )
        )

    ) ;

    // Support leading breaks and several sections with no chapter.
    part(
        BREAK +
        BREAK +
        "===" + BREAK +
        BREAK +
        "p0" + BREAK +
        BREAK +
        "===" + BREAK +
        BREAK +
        "p1",
        tree(
            PART,
            tree(
                SECTION,
                tree( PARAGRAPH_PLAIN, tree( WORD, "p0" ) )
            ),
            tree(
                SECTION,
                tree( PARAGRAPH_PLAIN, tree( WORD, "p1" ) )
            )
        )
    ) ;

  }

// ========================================
// Wrappers for parser rules.
// First-class methods in Java are welcome!
// Yes this is verbose but totally readable
// stuff. Reflexion would be a mess.
// ========================================

  private static void title( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = title( text ) ;
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
  }

  private static Tree title( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().title().getTree() ;
    checkSanity( parser );
    return tree;
  }

  private static void identifier( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = identifier( text ) ;
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
  }

  private static Tree identifier( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().identifier().getTree() ;
    checkSanity( parser );
    return tree;
  }


  private static void word( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = word( text ) ;
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
  }

  private static Tree word( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().word().getTree() ;
    checkSanity( parser );
    return tree;
  }

  private static void wordFails( String s ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( s ) ;
    parser.getAntlrParser().word() ;
    final String readableProblemList = AntlrTestHelper.createProblemList( parser.getProblems() ) ;
    final boolean parserHasProblem = parser.hasProblem();
    Assert.assertTrue( readableProblemList, parserHasProblem ) ;
  }

  private static void paragraph( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = paragraph( text ) ;
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
  }

  private static Tree paragraph( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().paragraph().getTree() ;
    checkSanity( parser );
    return tree;
  }

  private static void section( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = section( text ) ;
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
  }

  private static Tree section( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().section().getTree() ;
    checkSanity( parser );
    return tree;
  }

  private static void paragraphBody( String text, Tree expectedTree )
      throws RecognitionException
  {
    final Tree actualTree = paragraphBody( text ); ;
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
  }

  private static Tree paragraphBody( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().paragraphBody().getTree() ;
    checkSanity( parser );
    return tree;
  }

  private static void paragraphBodyFails( String s ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( s ) ;
    parser.getAntlrParser().paragraphBody() ;
    final String readableProblemList = AntlrTestHelper.createProblemList( parser.getProblems() ) ;
    final boolean parserHasProblem = parser.hasProblem() ;
    Assert.assertTrue( readableProblemList, parserHasProblem ) ;
  }

  private static void part( String text, Tree expectedTree )
      throws RecognitionException
  {
    final Tree actualTree = part( text ); ;
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
  }

  private static Tree part( String text ) throws RecognitionException {
    final DelegatingPartParser parser = createPartParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().part().getTree() ;
    checkSanity( parser );
    return tree;
  }


// ================
// Boring utilities
// ================

  private static void checkSanity( DelegatingPartParser parser ) {
    if( parser.hasProblem() ) {
      throw new AssertionFailedError(
          "Parser has problems. " + AntlrTestHelper.createProblemList( parser.getProblems() ) ) ;
    }
  }


  private static DelegatingPartParser createPartParser( String text ) {
    return ( DelegatingPartParser )
        new DefaultPartParserFactory().createParser( TreeHelper.LOCATION_FACTORY, text );
  }
}

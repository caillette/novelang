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

import java.util.ArrayList;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import junit.framework.AssertionFailedError;
import novelang.model.common.Tree;
import novelang.model.common.Problem;
import novelang.model.common.Location;
import static novelang.model.common.NodeKind.*;
import novelang.model.structural.StructuralBook;
import novelang.model.structural.StructuralChapter;
import novelang.model.implementation.Part;
import com.google.common.collect.Lists;

/**
 * GUnit sucks as it has completely obscure failures and stupid reports,
 * but I took some ideas from it anyways.
 *
 * @author Laurent Caillette
 */
public class BookParserTest {

  private static final Logger LOGGER = LoggerFactory.getLogger( BookParserTest.class ) ;
  private static final String BREAK = "\n" ;

  @Test
  public void book() throws RecognitionException {
    book(
        "# path/to/part" + BREAK +
        BREAK +
        "***'some (title)" + BREAK +
        BREAK +
        "=== 'chapter identifier" + BREAK +
        BREAK +
        "+ included"
    ) ;

    book(
        "# path/to/part " + BREAK +
        " " + BREAK +
        "***'some (title) " + BREAK +
        "  " + BREAK +
        "=== 'chapter identifier " + BREAK +
        " " + BREAK +
        " + included" + BREAK +
        " " + BREAK +
        " "
    ) ;


    book(
        "# 123/4/56" + BREAK +
        "# path/to/file1.np" + BREAK +
        "# path/with/wilcards/*.np" + BREAK +
        BREAK +
        "*** 'Optional chapter title" + BREAK +
        BREAK +
        "=== 'Optional section title" + BREAK +
        ":style override-chapter-style-with-section-style" + BREAK +
        BREAK +
        "+ id00" + BREAK +
        "+ id10" + BREAK +
        BREAK +
        "+ id20 <= 1  3  5..7     0-  1-" + BREAK +
        BREAK +
        BREAK +
        "===" + BREAK +
        BREAK +
        "+ id30" + BREAK +
        "| id40" + BREAK +
        BREAK +
        BREAK +
        BREAK +
        "***" + BREAK +
        ":style chapter-style" + BREAK +
        BREAK +
        "===" + BREAK +
        BREAK +
        "+ id50" + BREAK +
        BREAK +
        BREAK
    ) ;

  }

  @Test
  public void bookChapter() throws RecognitionException {

    bookChapter(
        "***" + BREAK +
        BREAK +
        "===" + BREAK +
        BREAK +
        "+ included"
    ) ;

    bookChapter(
        "***  " + BREAK +
        BREAK +
        "===  " + BREAK +
        BREAK +
        "+ included"
    ) ;
  }

  @Test
  public void bookPart() throws RecognitionException {
    bookPart( "# path/to/part" ) ;
    bookPart( "#path/t-o/part.nlp" ) ;
    bookPart( "#path/to/*.nlp" ) ;

    bookPart(
        "# w*/*.e",
        TreeHelper.multiTokenTree( "# w*/*.e" )
    ) ;

    bookPartFails( "#path/to/forbidden/../place" ) ;
  }

  @Test
  public void bookSection() throws RecognitionException {
    bookSection(
       "=== 'w0 w1" + BREAK +
       "w2" + BREAK +
       "+ w3"
    ) ;
  }

  @Test
  public void style() throws RecognitionException {
    style( ":style this-is-a-style-identifier" ) ;  
  }

  @Test
  public void inclusion() throws RecognitionException {

    inclusion(
        "+ w0" 
    ) ;

    inclusion(
        "+ w0 w1" + BREAK +
        BREAK +
        "<= 1"
    ) ;

    inclusion(
        "+ w0 (w1)" + BREAK +
        "<= 1"
    ) ;

    inclusion(
        "| w0 (w1)" + BREAK +
        "<= 1"
    ) ;

  }

  @Test
  public void paragraphReferences() throws RecognitionException{
    paragraphReferences( "<= 1" ) ;
    paragraphReferences( "<= 11-" ) ;
    paragraphReferences( "<= 1 2" ) ;
    paragraphReferences( "<= 1..2" ) ;
    paragraphReferences( "<= 0- 2..3 45 67-" ) ;
  }


// ========================================
// Wrappers for parser rules.
// First-class methods in Java are welcome!
// Yes this is verbose but totally readable
// stuff. Reflexion would be a mess.
// ========================================

  private static Tree book( String text ) throws RecognitionException {
    final DelegatingBookParser parser = createBookParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().book().getTree() ;
    checkSanity( parser ) ;
    return tree;
  }

  private static Tree bookChapter( String text ) throws RecognitionException {
    final DelegatingBookParser parser = createBookParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().bookChapter().getTree() ;
    checkSanity( parser ) ;
    return tree;
  }

  private static void bookPart( String text, Tree expectedTree ) throws RecognitionException {
    final Tree actualTree = bookPart( text ) ;
    TreeHelper.assertEquals( expectedTree, actualTree ) ;
  }

  private static Tree bookPart( String text ) throws RecognitionException {
    final DelegatingBookParser parser = createBookParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().bookPart().getTree() ;
    checkSanity( parser ) ;
    return tree;
  }

  private static void bookPartFails( String s ) throws RecognitionException {
    final DelegatingBookParser parser = createBookParser( s ) ;
    parser.getAntlrParser().bookPart() ;
    final String readableProblemList = AntlrTestHelper.createProblemList( parser.getProblems() ) ;
    final boolean parserHasProblem = parser.hasProblem();
    Assert.assertTrue( readableProblemList, parserHasProblem ) ;
  }

  private static Tree bookSection( String text ) throws RecognitionException {
    final DelegatingBookParser parser = createBookParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().bookSection().getTree() ;
    checkSanity( parser ) ;
    return tree;
  }

  private static Tree style( String text ) throws RecognitionException {
    final DelegatingBookParser parser = createBookParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().style().getTree() ;
    checkSanity( parser ) ;
    return tree;
  }

  private static Tree paragraphReferences( String text ) throws RecognitionException {
    final DelegatingBookParser parser = createBookParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().paragraphReferences().getTree() ;
    checkSanity( parser ) ;
    return tree;
  }

  private static Tree inclusion( String text ) throws RecognitionException {
    final DelegatingBookParser parser = createBookParser( text ) ;
    final Tree tree = ( Tree ) parser.getAntlrParser().inclusion().getTree() ;
    checkSanity( parser ) ;
    return tree;
  }


// ================
// Boring utilities
// ================

  private static void checkSanity( DelegatingBookParser parser ) {
    if( parser.hasProblem() ) {
      throw new AssertionFailedError(
          "Parser has problems. " + AntlrTestHelper.createProblemList( parser.getProblems() ) ) ;
    }
  }


  private static DelegatingBookParser createBookParser( String text ) {
    final DelegatingBookParser delegatingBookParser = ( DelegatingBookParser )
        new DefaultBookParserFactory().createParser(
            new StructuralBook() {

              public void collect( Problem ex ) {
                LOGGER.debug( "collect" ) ;
              }

              public Iterable< Problem > getProblems() {
                LOGGER.debug( "getProblems" ) ;
                return Lists.immutableList( new ArrayList< Problem >() ) ;
              }

              public Part createPart( String partFileName, Location location ) {
                LOGGER.debug( "createPart" ) ;
                return null ;
              }

              public StructuralChapter createChapter( Location location ) {
                LOGGER.debug( "createChapter" ) ;
                return null ;
              }

              public void setIdentifier( Tree identifier ) {
                LOGGER.debug( "setIdentifier" ) ;
              }

              public Location createLocation( int line, int column ) {
                return TreeHelper.LOCATION_FACTORY.createLocation( line, column ) ;
              }
            },
            text
        ) ;
    delegatingBookParser.setScopesEnabled( false ) ;
    return delegatingBookParser ;
  }

}

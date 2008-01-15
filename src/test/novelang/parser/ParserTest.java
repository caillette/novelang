/*
 * Copyright (C) 2006 Laurent Caillette
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
package novelang.parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.MissingResourceException;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Test;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import novelang.ResourceTools;

/**
 * Tests for parser using external files.
 *
 * @author Laurent Caillette
 */
public class ParserTest {

  /**
   * Get sure that errors are detected.
   */
  @Test
  public void detectIllFormedDocument() throws IOException, RecognitionException {
    initializeParser( "ill-formed document" ) ;
    parser.document() ;
    List< RecognitionException > parserExceptions = parser.getExceptions() ;
    assertFalse( "Parser failed to throw exceptions", parserExceptions.isEmpty() ) ;
  }


  @Test
  public void sections1() throws IOException, RecognitionException {
    runParserOnResource( "/sections-1.sample" ) ;
  }

  @Test
  public void sections2() throws IOException, RecognitionException {
    runParserOnResource( "/sections-2.sample" ) ;
  }

  @Test
  public void speechSequence1() throws IOException, RecognitionException {
    runParserOnResource( "/speechsequence-1.sample" ) ;
  }

  @Test
  public void speechSequence2() throws IOException, RecognitionException {
    runParserOnResource( "/speechsequence-2.sample" ) ;
  }

  @Test
  public void quotes1() throws IOException, RecognitionException {
    runParserOnResource( "/quotes-1.sample" ) ;
  }

  @Test
  public void paragraphBody1() throws IOException, RecognitionException {
    runParserOnResource( "/paragraphbody-1.sample" ) ;
  }

  @Test
  public void blockQuote1() throws IOException, RecognitionException {
    runParserOnResource( "/blockquote-1.sample" ) ;
  }

  @Test
  public void readResourceOk() throws IOException {
    final String resource = readResource( "/sections-1.sample" ) ;
    assertFalse( "".equals( resource ) );
  }

  @Test( expected = MissingResourceException.class )  
  public void readResourceFailed() throws IOException {
    readResource( "/doesnotexist" ) ;
  }



// =======
// Fixture
// =======


  private Content7Lexer lexer ;
  private Content7Parser parser ;


  private void initializeParser( String testString ) throws IOException {
    final CharStream stream = new ANTLRStringStream( testString ) ;
    lexer = new Content7Lexer( stream ) ;
    final CommonTokenStream tokens = new CommonTokenStream( lexer ) ;
    parser = new Content7Parser( tokens ) ;
  }

  private static String readResource( String resourceName ) throws IOException {
    return ResourceTools.readStringResource(
        ParserTest.class,
        resourceName,
        Charset.forName( "ISO-8859-1" )
    ) ;
  }

  private void checkNoParserException() {
    List< RecognitionException > parserExceptions = parser.getExceptions() ;
    assertTrue( "Parser threw exceptions -- see output", parserExceptions.isEmpty() ) ;
  }

  private void runParserOnResource( String resourceName )
      throws IOException, RecognitionException
  {
    runParserOnString( readResource( resourceName ) ) ;
  }

  private void runParserOnString( String text )
      throws IOException, RecognitionException
  {
    initializeParser( text ) ;
    parser.document() ;
    checkNoParserException() ;
  }

}

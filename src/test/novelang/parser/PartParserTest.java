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
import novelang.model.implementation.Book;
import novelang.parser.implementation.DefaultPartParserFactory;
import junit.framework.TestSuite;

/**
 * Tests for parser using external files.
 *
 * Yes, JUnit's Parametrized runner is supposed to avoid typing all these methods
 * but it sucks as test names are like {@code test[0], test[1]...}
 * and it would require to move tests like {@code testIllFormedDocument()}
 * to another class.
 *
 * @author Laurent Caillette
 */
public class PartParserTest extends AbstractParserTest< PartParser > {


  /**
   * Get sure that errors are detected.
   */
  @Test
  public void detectIllFormedDocument() throws IOException, RecognitionException {
    initializeParser( "ill-formed document" ) ;
    parser.parse() ;
    assertTrue( "Parser failed to throw exceptions", parser.hasProblem() ) ;
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
  public void sections3() throws IOException, RecognitionException {
    runParserOnResource( "/sections-3.sample" ) ;
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
  public void paragraphBody2() throws IOException, RecognitionException {
    runParserOnResource( "/paragraphbody-2.sample" ) ;
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


  protected PartParser createParser( Book book, String s ) {
    return new DefaultPartParserFactory().createParser( book, s ) ;
  }
}

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
package novelang.parser;

import java.io.IOException;
import java.util.MissingResourceException;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Ignore;
import org.antlr.runtime.RecognitionException;
import novelang.model.implementation.Book;
import novelang.parser.implementation.DefaultPartParserFactory;

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

  /**
   * Get sure that errors are detected.
   */
  @Test
  public void unicodeBad() throws IOException, RecognitionException {
    initializeParser(
        "=== \n" +
        "\n" +
        "\u2981" // Z NOTATION SPOT 
    ) ;
    parser.parse() ;
    assertTrue( "Parser failed to throw exceptions", parser.hasProblem() ) ;
  }

  @Test
  public void unicodeOk0() throws IOException, RecognitionException {
    runParserOnString(
        "=== \n" +
        "\n" +
        "\u00e0" +
        "\u00c0" +

        "\u00e6" +
        "\u00c6" +

        "\u00e8" +
        "\u00c8" +

        "\u00e9" +
        "\u00c9" +

        "\u0153" +
        "\u0152" +

        "\u00f9" +
        "\u00d9" +

        " and that's all!"

    ) ;
  }

  @Test
  public void unicodeOk1() throws IOException, RecognitionException {
    runParserOnResource( "/unicode-1.sample" ) ;
  }

  @Test
//  @Ignore
  public void unicodeOk2() throws IOException, RecognitionException {
    runParserOnResource( "/unicode-2.sample" ) ;
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

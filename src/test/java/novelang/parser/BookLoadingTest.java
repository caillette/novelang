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

package novelang.parser;

import java.io.IOException;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.Assert;
import novelang.parser.antlr.DefaultBookParserFactory;
import novelang.model.implementation.Book;
import novelang.model.implementation.Chapter;

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
public class BookLoadingTest extends AbstractParserTest< BookParser > {


  /**
   * Get sure that errors are detected.
   */
  @Test
  public void detectIllFormedDocument() throws IOException, RecognitionException {
    initializeParser( "ill-formed document" ) ;
    parser.parse() ;
    Assert.assertTrue( "Parser failed to throw exceptions", parser.hasProblem() ) ;
  }

  @Test
  public void structure1() throws IOException, RecognitionException {
    runParserOnResource( "/structure-1.nlb" ) ;
  }

  @Test
  public void structure1AndTheBook() throws IOException, RecognitionException {
    runParserOnResource( "/structure-1.nlb" ) ;
    final Iterable< Chapter > chapters = book.getChapters() ;
    Assert.assertTrue( chapters.iterator().hasNext() ) ;
  }

  @Test
  public void structure2() throws IOException, RecognitionException {
    runParserOnResource( "/structure-2.nlb" ) ;
  }

  @Test
  public void structure3() throws IOException, RecognitionException {
    runParserOnResource( "/structure-3.nlb" ) ;
  }



// =======
// Fixture
// =======


  protected BookParser createParser( Book book, String s ) {
    return new DefaultBookParserFactory().createParser( book, s ) ;
  }
}

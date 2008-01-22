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

import org.antlr.runtime.RecognitionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;
import novelang.parser.implementation.DefaultStructureParserFactory;
import com.google.common.collect.Lists;

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
public class StructureParserTest extends AbstractParserTest< StructureParser > {


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
    runParserOnResource( "/structure-1.sample" ) ;
  }

  @Test @Ignore
  public void structure2() throws IOException, RecognitionException {
    runParserOnResource( "/structure-2.sample" ) ;
  }

  @Test @Ignore
  public void structure3() throws IOException, RecognitionException {
    runParserOnResource( "/structure-3.sample" ) ;
  }



// =======
// Fixture
// =======


  protected StructureParser createParser( String s ) {
    return new DefaultStructureParserFactory().createParser( s ) ;
  }
}

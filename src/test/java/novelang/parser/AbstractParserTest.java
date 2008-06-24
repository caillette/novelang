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
import java.nio.charset.Charset;
import java.util.MissingResourceException;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.lang.ClassUtils;
import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import novelang.TestResourceTools;
import novelang.TestResources;
import novelang.model.common.Problem;
import novelang.model.common.SyntacticTree;

/**
 * @author Laurent Caillette
 */
public abstract class AbstractParserTest< P extends GenericParser > {

  protected P parser ;
  String bookName = ClassUtils.getShortClassName( getClass() );
//  protected final Book book = new Book( bookName, new File( bookName ) ) ;


//  protected abstract P createParser( Book book, String s ) ;

  protected final void initializeParser( String testString ) throws IOException {
//    parser = createParser( book, testString ) ;
  }

  protected final static String readResource( String resourceName ) throws IOException {
    return TestResourceTools.readStringResource(
        AbstractParserTest.class,
        resourceName,
        Charset.forName( "ISO-8859-1" )
    ) ;
  }

  protected final void checkNoParserException() {
    final StringBuffer buffer = new StringBuffer( "Problems: " ) ;
    for( final Problem problem : parser.getProblems() ) {
      buffer.append( "\n    " ) ;
      buffer.append( problem.getLocation() ) ;
      buffer.append( problem.getMessage() ) ;
    }
    assertFalse( buffer.toString(), parser.hasProblem() ) ;
  }

  protected final void runParserOnResource( String resourceName )
      throws IOException, RecognitionException {
    runParserOnString( readResource( resourceName ) ) ;
  }

  protected final void runParserOnString( String text )
      throws IOException, RecognitionException
  {
    initializeParser(  text ) ;
    final SyntacticTree tree = parser.parse();
    checkNoParserException() ;
    Assert.assertNotNull( tree ) ;
  }

  @Test
  public void readResourceOk() throws IOException {
    final String resource = readResource( TestResources.JUST_SECTIONS ) ;
    Assert.assertFalse( "".equals( resource ) );
  }

  @Test( expected = MissingResourceException.class )
  public void readResourceFailed() throws IOException {
    readResource( "/doesnotexist" ) ;
  }

  

}

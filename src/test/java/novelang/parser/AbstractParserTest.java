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
import java.io.File;
import java.nio.charset.Charset;
import java.util.MissingResourceException;

import org.antlr.runtime.RecognitionException;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.junit.Assert;
import org.apache.commons.lang.ClassUtils;
import novelang.ResourceTools;
import novelang.model.implementation.Book;
import novelang.model.common.Tree;

/**
 * @author Laurent Caillette
 */
public abstract class AbstractParserTest< P extends GenericParser > {

  protected P parser ;
  String bookName = ClassUtils.getShortClassName( getClass() );
  protected final Book book = new Book( bookName, new File( bookName ) ) ;


  protected abstract P createParser( Book book, String s ) ;

  protected final void initializeParser( String testString ) throws IOException {
    parser = createParser( book, testString ) ;
  }

  protected final static String readResource( String resourceName ) throws IOException {
    return ResourceTools.readStringResource(
        AbstractParserTest.class,
        resourceName,
        Charset.forName( "ISO-8859-1" )
    ) ;
  }

  protected final void checkNoParserException() {
    assertFalse( "Parser threw exceptions -- see output", parser.hasProblem() ) ;
  }

  protected final void runParserOnResource( String resourceName )
      throws IOException, RecognitionException {
    runParserOnString( readResource( resourceName ) ) ;
  }

  protected final void runParserOnString( String text )
      throws IOException, RecognitionException
  {
    initializeParser(  text ) ;
    final Tree tree = parser.parse();
    Assert.assertNotNull( tree ) ;
    checkNoParserException() ;
  }

  @Test
  public void readResourceOk() throws IOException {
    final String resource = readResource( "/sections-1.nlp" ) ;
    Assert.assertFalse( "".equals( resource ) );
  }

  @Test( expected = MissingResourceException.class )
  public void readResourceFailed() throws IOException {
    readResource( "/doesnotexist" ) ;
  }

  

}

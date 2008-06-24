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
package novelang.model.function.builtin;

import java.io.File;

import org.apache.commons.lang.ClassUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import novelang.ScratchDirectoryFixture;
import novelang.TestResourceTools;
import novelang.TestResources;
import novelang.model.book.Environment;
import novelang.model.common.Location;
import static novelang.model.common.NodeKind.BOOK;
import novelang.model.common.SimpleTree;
import novelang.model.common.SyntacticTree;
import novelang.model.common.tree.Treepath;
import novelang.model.function.FunctionCall;
import novelang.model.function.FunctionDefinition;
import novelang.model.function.IllegalFunctionCallException;
import novelang.parser.antlr.BookParserTest;

/**
 * @author Laurent Caillette
 */
public class InsertFunctionTest {

  @Test
  public void goodFileUrl() throws IllegalFunctionCallException {
    final FunctionDefinition definition = new InsertFunction() ;
    final FunctionCall call = definition.instantiate(
        new Location( "", -1, -1 ), 
        BookParserTest.createFunctionCallWithUrlTree( oneWordFile.getAbsolutePath() )
    ) ;

    final SyntacticTree initialTree = new SimpleTree( BOOK.name() ) ;
    final FunctionCall.Result result =
        call.evaluate( new Environment( contentDirectory ), Treepath.create( initialTree ) ) ;

    Assert.assertFalse( result.getProblems().iterator().hasNext() ) ;
    Assert.assertNotNull( result.getBook() ) ;
    Assert.assertNotNull( result.getBook() ) ;

  }

  @Test
  public void recurse() throws IllegalFunctionCallException {
    final FunctionDefinition definition = new InsertFunction() ;
    final FunctionCall call = definition.instantiate(
        new Location( "", -1, -1 ),
        BookParserTest.createFunctionCallWithUrlTree(
            scannedDirectory.getAbsolutePath(), "recurse" )
    ) ;

  }



// =======
// Fixture
// =======

  private static final String ONE_WORD_FILENAME = TestResources.ONE_WORD ;
  private File contentDirectory ;
  private File oneWordFile ;
  private File scannedDirectory ;

  @Before
  public void setUp() throws Exception {

    final String testName = ClassUtils.getShortClassName( getClass() ) ;
    final ScratchDirectoryFixture scratchDirectoryFixture =
        new ScratchDirectoryFixture( testName ) ;
    contentDirectory = scratchDirectoryFixture.getTestScratchDirectory() ;

    oneWordFile = TestResourceTools.copyResourceToFile(
        getClass(),
        ONE_WORD_FILENAME,
        contentDirectory
    ) ;

    scannedDirectory = new File( contentDirectory, TestResources.SCANNED_DIR ) ;
    
  }

}

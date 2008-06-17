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

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.apache.commons.lang.ClassUtils;
import novelang.model.function.FunctionCall;
import novelang.model.function.FunctionDefinition;
import novelang.model.function.IllegalFunctionCallException;
import novelang.model.common.Tree;
import novelang.model.common.Treepath;
import novelang.model.common.NodeKind;
import novelang.model.common.Location;
import novelang.model.implementation.DefaultMutableTree;
import novelang.model.book.Environment;
import novelang.parser.antlr.BookParserTest;
import novelang.parser.Encoding;
import novelang.ScratchDirectoryFixture;
import novelang.TestResourceTools;
import novelang.TestResources;
import novelang.daemon.HttpDaemon;

/**
 * @author Laurent Caillette
 */
public class FunctionInsertTest {

  @Test
  public void testGoodUrl() throws IllegalFunctionCallException {
    final FunctionDefinition definition = new FunctionInsert() ;
    final FunctionCall call = definition.instantiate(
        new Location( "", -1, -1 ), 
        BookParserTest.createFunctionCallWithUrlTree( oneWordFile.getAbsolutePath() ) ) ;

    final Tree initialTree = new DefaultMutableTree( NodeKind.BOOK ) ;
    final FunctionCall.Result result =
        call.evaluate( new Environment( contentDirectory ), Treepath.create( initialTree ) ) ;

    Assert.assertFalse( result.getProblems().iterator().hasNext() ) ;
    Assert.assertNotNull( result.getBook() ) ;

  }


// =======
// Fixture
// =======

  private static final String ONE_WORD_FILENAME = TestResources.ONE_WORD ;
  private File oneWordFile ;
  private File contentDirectory ;

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
  }
}

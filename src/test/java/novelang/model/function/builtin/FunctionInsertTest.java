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

import org.junit.Test;
import org.junit.Assert;
import novelang.model.function.FunctionCall;
import novelang.model.function.FunctionDefinition;
import novelang.model.function.IllegalFunctionCallException;
import novelang.model.common.Tree;
import novelang.model.common.Treepath;
import novelang.model.common.NodeKind;
import novelang.model.implementation.DefaultMutableTree;
import novelang.model.book.Environment;
import novelang.parser.antlr.BookParserTest;

/**
 * @author Laurent Caillette
 */
public class FunctionInsertTest {

  @Test
  public void testGoodUrl() throws IllegalFunctionCallException {
    final FunctionDefinition definition = new FunctionInsert() ;
    final FunctionCall call = definition.instantiate(
        null, BookParserTest.FUNCTIONCALLWITHURL_TREE ) ;

    final Tree initialTree = new DefaultMutableTree( NodeKind.BOOK ) ;
    final FunctionCall.Result result =
        call.evaluate( new Environment(), Treepath.create( initialTree ) ) ;

    Assert.assertFalse( result.getProblems().iterator().hasNext() ) ;
    Assert.assertNotNull( result.getBook() ) ;

  }

}

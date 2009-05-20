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
package novelang.book.function.builtin;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import novelang.system.LogFactory;
import novelang.system.Log;
import com.google.common.collect.ImmutableMap;
import novelang.book.Environment;
import novelang.book.function.FunctionCall;
import novelang.book.function.FunctionDefinition;
import novelang.book.function.IllegalFunctionCallException;
import novelang.common.Location;
import novelang.common.SimpleTree;
import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import novelang.loader.ResourceName;
import static novelang.parser.NodeKind.BOOK;
import novelang.parser.antlr.BookParserTest;
import novelang.rendering.RenditionMimeType;

/**
 * Tests for {@link MapStylesheetFunction}.
 *
 * @author Laurent Caillette
 */
public class MapStylesheetFunctionTest {

  private static final Log LOG = LogFactory.getLog( MapStylesheetFunctionTest.class ) ;

  @Test
  public void correctMapping() throws IllegalFunctionCallException {
    final FunctionDefinition definition = new MapStylesheetFunction() ;
    final SyntacticTree callTree = BookParserTest.createFunctionCallWithValuedAssignmentTree(
        "stylesheet",
        ImmutableMap.of( "html", "dir/stylesheet.xsl", "pdf", "other/pdf.xsl" )
    ) ;
    LOG.debug( "Function call tree: \n%s", callTree.toStringTree() ) ;

    final FunctionCall call = definition.instantiate(
        new Location( "", -1, -1 ),
        callTree
    ) ;


    final SyntacticTree initialTree = new SimpleTree( BOOK.name() ) ;
    final FunctionCall.Result result =
        call.evaluate( new Environment( new File( "" ) ), Treepath.create( initialTree ) ) ;

    Assert.assertEquals(
        new ResourceName( "dir/stylesheet.xsl" ),
        result.getEnvironment().getCustomStylesheets().get( RenditionMimeType.HTML )
    ) ;

    Assert.assertEquals(
        new ResourceName( "other/pdf.xsl" ),
        result.getEnvironment().getCustomStylesheets().get( RenditionMimeType.PDF )  
    ) ;

  }




}
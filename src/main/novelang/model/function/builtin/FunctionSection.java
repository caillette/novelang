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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Iterables;
import novelang.model.book.Environment;
import novelang.model.common.Location;
import static novelang.model.common.NodeKind.URL;
import static novelang.model.common.NodeKind.VALUED_ARGUMENT_PRIMARY;
import static novelang.model.common.NodeKind.TITLE;
import static novelang.model.common.NodeKind.SECTION;
import novelang.model.common.Problem;
import novelang.model.common.Tree;
import novelang.model.common.Treepath;
import novelang.model.common.NodeKind;
import novelang.model.common.TreeTools;
import novelang.model.function.FunctionCall;
import novelang.model.function.FunctionDefinition;
import novelang.model.function.IllegalFunctionCallException;
import novelang.model.function.FunctionTools;
import static novelang.model.function.FunctionTools.verify;
import novelang.model.implementation.DefaultMutableTree;

/**
 * @author Laurent Caillette
 */
public class FunctionSection implements FunctionDefinition {

  private static final Logger LOGGER = LoggerFactory.getLogger( FunctionSection.class ) ;

  public String getName() {
    return "section" ;
  }

  public FunctionCall instantiate(
      Location location,
      Tree functionCall
  ) throws IllegalFunctionCallException {

    verify( "No primary argument", 2, functionCall.getChildCount() ) ;
    final Tree primaryArgument = functionCall.getChildAt( 1 ) ;
    final String primaryArgumentText = primaryArgument.getText();
    verify( "Incorrect declaration for primary argument: '" + primaryArgumentText + "'",
        VALUED_ARGUMENT_PRIMARY.name(), primaryArgumentText ) ;
    verify( "Primary argument is empty", true, primaryArgument.getChildCount() > 0 ) ;

    LOGGER.debug( "Parsed function '{}' title='{}'", getName(), primaryArgument.toStringTree() ) ;

    final DefaultMutableTree titleTree = new DefaultMutableTree( TITLE.name() ) ;
    for( Tree child : primaryArgument.getChildren() ) {
      titleTree.addChild( child ) ;
    }

    final DefaultMutableTree sectionTree = new DefaultMutableTree( SECTION.name() ) ;
    sectionTree.addChild( titleTree ) ;

    return new FunctionCall( location ) {
      public Result evaluate( Environment environment, Treepath book ) {
        final Treepath newBook = TreeTools.addChildAtRight( book, sectionTree ) ;
        return new Result( newBook, null ) ;
      }
    } ;
  }


}